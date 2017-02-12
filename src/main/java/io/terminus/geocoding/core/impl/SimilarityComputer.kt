package io.terminus.geocoding.core.impl

import io.terminus.geocoding.core.Computer
import io.terminus.geocoding.core.segment.IKAnalyzerSegmenter
import io.terminus.geocoding.core.segment.SimpleSegmenter
import io.terminus.geocoding.model.Address
import io.terminus.geocoding.similarity.Document
import io.terminus.geocoding.similarity.MatchedResult
import io.terminus.geocoding.similarity.MatchedTerm
import io.terminus.geocoding.similarity.Term
import io.terminus.geocoding.similarity.Term.TermType
import io.terminus.geocoding.similarity.Term.TermType.Building
import io.terminus.geocoding.similarity.Term.TermType.City
import io.terminus.geocoding.similarity.Term.TermType.District
import io.terminus.geocoding.similarity.Term.TermType.Province
import io.terminus.geocoding.similarity.Term.TermType.Road
import io.terminus.geocoding.similarity.Term.TermType.RoadNum
import io.terminus.geocoding.similarity.Term.TermType.Street
import io.terminus.geocoding.similarity.Term.TermType.Text
import io.terminus.geocoding.similarity.Term.TermType.Town
import io.terminus.geocoding.similarity.Term.TermType.Village
import io.terminus.geocoding.utils.isAsciiChars
import io.terminus.geocoding.utils.isNumericChars

/**
 * Desc: 相似度算法相关逻辑
 *
 * 1. >>>>> 关于 TF-IDF
 *  TC: 词数 Term Count, 某个词在文档中出现的次数
 *  TF: 词频 Term Frequency, 某个词在文档中出现的频率. TF = 该词在文档中出现的次数 / 该文档的总词数
 *  IDF: 逆文档词频 Inverse Document Frequency. IDF = log( 语料库文档总数 / ( 包含该词的文档数 + 1 ) ). 分母加1是为了防止分母出现0的情况
 *  TF-IDF: 词条的特征值, TF-IDF = TF * IDF
 *
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/5
 */
open class SimilarityComputer : Computer {

    private val segmenter = IKAnalyzerSegmenter() // text的分词, 默认 ik 分词器
    private val simpleSegmenter = SimpleSegmenter() // 暂时用于处理 building 的分词

    // 中文数字字符
    private val NUMBER_CN = arrayOf('一', '二', '三', '四', '五', '六', '七', '八', '九', '０', '１', '２' ,'３' ,'４' ,'５' ,'６' ,'７' ,'８' ,'９')

    // 权重值常量
    private val BOOST_M = 1.0   // 正常权重
    private val BOOST_L = 2.0   // 加权高值
    private val BOOST_XL = 4.0  // 加权高值
    private val BOOST_S = 0.5   // 降权
    private val BOOST_XS = 0.25 // 降权

    /**
     * 将标准地址转化成文档对象
     * 1. 对text进行分词
     * 2. 对每个部分设置权重
     */
    override fun analyze(address: Address): Document {
        val doc = Document()

        var tokens: List<String> = emptyList()
        // 1. 对 text (地址解析后剩余文本) 进行分词
        if (!address.text.isNullOrBlank()) {
            tokens = segmenter.segment(address.text!!)
        }

        val terms = arrayListOf<Term>()
        // 2. 生成 term
        // 2.1 town
        val town = if (!address.town.isNullOrBlank()) address.town else address.street
        if (!town.isNullOrBlank()) {
            doc.town = Term(TermType.Town, town)
            terms.add(doc.town!!)
        }
        // 2.2 village
        val village = address.village
        if (!village.isNullOrBlank()) {
            doc.village = Term(TermType.Village, village)
            terms.add(doc.village!!)
        }
        // 2.3 road
        val road = address.road
        if (!road.isNullOrBlank()) {
            doc.road = Term(Road, road)
            terms.add(doc.road!!)
        }
        // 2.4 road num
        val roadNum = address.roadNum
        if (!roadNum.isNullOrBlank()) {
            val roadNumTerm = Term(RoadNum, roadNum)
            doc.roadNum = roadNumTerm
            doc.roadNumValue = translateRoadNum(roadNum)
            roadNumTerm.ref = doc.road
            terms.add(doc.roadNum!!)
        }
        // 2.5 building num
        val buildingNum = address.buildingNum
        if (!buildingNum.isNullOrBlank()) {
            // 转换 building串
            translateBuilding(buildingNum).forEach {
                terms.add(Term(Building, it))
            }
        }

        // 3. 将分词放置到token中
        val termTexts = terms.map(Term::text)
        tokens.forEach {
            // 如果 terms 中不包含
            if (!termTexts.contains(it)) {
                terms.add(Term(TermType.Text, it))
            }
        }

        // 4. 设置每个 Term 的 IDF
        // 由于 TF-IDF 在计算地址相似度上意义不是特别明显
        putIdfs(terms)

        doc.terms = terms
        return doc
    }

    /**
     * 计算两个标准地址的相似度
     * 1. 将两个地址形成 Document
     * 2. 为每个Document的Term设置权重
     * 3. 计算两个分词组的余弦相似度, 值为0~1，值越大表示相似度越高，返回值为1则表示完全相同
     */
    override fun compute(addr1: Address?, addr2: Address?): MatchedResult? {
        if (addr1 == null || addr2 == null) {
            return null
        }
        // 如果两个地址不在同一个省市区, 则认为是不相同地址
        if (addr1.provinceId != addr2.provinceId || addr1.cityId != addr2.cityId || addr1.districtId != addr2.districtId) {
            return null
        }

        // 为每个address计算词条
        val doc1 = analyze(addr1)
        val doc2 = analyze(addr2)

        // 计算两个document的相似度
        return computeSimilarity(doc1, doc2)
    }


    /**
     * 提取 道路门牌号中的数字, 如 40号、一号院
     */
    private fun translateRoadNum(roadNum: String?): Int {
        if (roadNum.isNullOrBlank()) return 0

        val sb = StringBuilder()
        var isTen = false // 是否含有十
        loop@ for (i in 0..roadNum!!.length - 1) {
            val c = roadNum.get(i)

            // 识别汉字中的 "十", 由于 "十号" 和 "二十号" 的意义不同
            if (isTen) {
                val pre = sb.isNotEmpty()
                val post = NUMBER_CN.contains(c) || c in '0'..'9'
                if (pre) { // 如果前面含有, 则追加 0
                    if (post) { /*do nothing*/
                    } else {
                        sb.append('0')
                    }
                } else {
                    if (post) sb.append('1')
                    else sb.append("10")
                }
                isTen = false
            }
            // 追加数字
            when (c) {
                '一' -> { sb.append(1); continue@loop }
                '二' -> { sb.append(2); continue@loop }
                '三' -> { sb.append(3); continue@loop }
                '四' -> { sb.append(4); continue@loop }
                '五' -> { sb.append(5); continue@loop }
                '六' -> { sb.append(6); continue@loop }
                '七' -> { sb.append(7); continue@loop }
                '八' -> { sb.append(8); continue@loop }
                '九' -> { sb.append(9); continue@loop }
                '十' -> { isTen = true; continue@loop }
            }

            //ANSI数字字符
            if (c in '0'..'9') {
                sb.append(c)
                continue
            }
            //中文全角数字字符
            when (c) {
                '０' -> { sb.append(0); continue@loop}
                '１' -> { sb.append(1); continue@loop}
                '２' -> { sb.append(2); continue@loop}
                '３' -> { sb.append(3); continue@loop}
                '４' -> { sb.append(4); continue@loop}
                '５' -> { sb.append(5); continue@loop}
                '６' -> { sb.append(6); continue@loop}
                '７' -> { sb.append(7); continue@loop}
                '８' -> { sb.append(8); continue@loop}
                '９' -> { sb.append(9); continue@loop}
            }
        }
        if (isTen) {
            if (sb.isNotEmpty())
                sb.append('0')
            else
                sb.append("10")
        }
        if (sb.isNotEmpty()) return Integer.parseInt(sb.toString())
        return 0
    }

    /**
     * 与 road 不同的是, building可能存在多个数字
     * 将字符串中的数字, 字母等提取出来
     */
    private fun translateBuilding(building: String?): List<String>  {
        if (building.isNullOrBlank()) return emptyList()
        return simpleSegmenter.segment(building!!)
    }

    /**
     * 获取 termText -> IDF 的映射
     * 简单实现, TODO: 未进行语料库的统计
     */
    private fun putIdfs(terms: List<Term>) {
        terms.forEach {
            // 计算 IDF
            val key = it.text
            if (key.isNumericChars()) it.idf = 2.0
            else if (key.isAsciiChars()) it.idf = 2.0
            // else it.idf = Math.log(docs / (tdocs + 1))
            else it.idf = 4.0   // 由于未进行语料库的统计, 默认4
        }
    }

    /**
     * 计算两个文档的余弦相似度
     */
    private fun computeSimilarity(doc1: Document, doc2: Document): MatchedResult {

        // 1. 计算Terms中 text类型词条 的匹配率
        var qTextTermCount = 0 // 文档1的Text类型词条数目
        var dTextTermMatchCount = 0 // 与文档2的Text类型词条匹配数目
        // 匹配此处之间的词数间隔
        var matchStart = -1
        var matchEnd = -1
        for (term1 in doc1.terms ?: emptyList()) {
            if (term1.type != TermType.Text) continue
            qTextTermCount++
            for ((i, term2) in (doc2.terms ?: emptyList()).withIndex()) {
                if (term2.type != TermType.Text) continue
                if (term1.text == term2.text) {
                    dTextTermMatchCount++
                    if (matchStart == -1) {
                        matchEnd = i
                        matchStart = matchEnd
                        break
                    }
                    if (i > matchEnd)
                        matchEnd = i
                    else if (i < matchStart)
                        matchStart = i
                    break
                }
            }
        }

        // 1.1 计算匹配率
        var termCoord = 1.0
        if (qTextTermCount > 0) {
            // Math.sqrt( 匹配上的词条数 / doc1的Text词条数 ) * 0.5 + 0.5
            termCoord = Math.sqrt(dTextTermMatchCount * 1.0 / qTextTermCount) * 0.5 + 0.5
        }
        // 1.2 计算稠密度
        var termDensity = 1.0
        if (qTextTermCount >= 2 && dTextTermMatchCount >= 2) {
            // Math.sqrt( 匹配上的词条数 / doc2匹配词条之间的距离 ) * 0.5 + 0.5
            termDensity = Math.sqrt(dTextTermMatchCount * 1.0 / (matchEnd - matchStart + 1)) * 0.5 + 0.5
        }

        // 2. 计算 TF-IDF(非标准) 和 余弦相似度的中间值
        val result = MatchedResult()
        result.doc1 = doc1
        result.doc2 = doc2

        // 余弦相似度的中间值
        var sumQD = 0.0
        var sumQQ = 0.0
        var sumDD = 0.0
        for (qterm in doc1.terms ?: emptyList()) {
            val qboost = getBoostValue(false, doc1, qterm, doc2, null)
            val q_TF_IDF = qboost * qterm.idf!!
            // 文档2的term
            var dterm = doc2.getTerm(qterm.text)
            if (dterm == null && RoadNum == qterm.type) {
                // 从文档2中找门牌号词条
                if (doc2.roadNum != null && doc2.road != null && doc2.road == qterm.ref)
                    dterm = doc2.roadNum
            }

            val dboost = if (dterm == null) 0.0 else getBoostValue(true, doc1, qterm, doc2, dterm)
            val coord = if (dterm != null && Text == dterm.type) termCoord else 1.0
            val density = if (dterm != null && Text == dterm.type) termDensity else 1.0
            val d_TF_IDF = (if (dterm != null) dterm.idf else qterm.idf)!! * dboost * coord * density

            // 计算相似度
            if (dterm != null) {
                val matchedTerm = MatchedTerm(dterm)
                matchedTerm.boost = dboost
                matchedTerm.tfidf = d_TF_IDF
                if (Text == dterm.type) {
                    matchedTerm.density = density
                    matchedTerm.coord = coord
                } else {
                    matchedTerm.density = -1.0
                    matchedTerm.coord = -1.0
                }
                result.terms.add(matchedTerm)
            }

            sumQQ += q_TF_IDF * q_TF_IDF
            sumQD += q_TF_IDF * d_TF_IDF
            sumDD += d_TF_IDF * d_TF_IDF
        }

        if (sumDD == 0.0 || sumQQ == 0.0) return result

        // 计算余弦相似度
        result.similarity = sumQD / Math.sqrt(sumQQ * sumDD)

        return result
    }

    /**
     * 根据不同的词条设置不同的权重
     * [forDoc]
     *  > true 则计算 [ddoc] 的权重, 此时 [qdoc], [qterm], [ddoc], [dterm] 不为空
     *  > false 则计算 [qdoc] 的权重, 此时 [qdoc], [qterm], [ddoc] 不为空, [dterm] 为空
     */
    private fun getBoostValue(forDoc: Boolean, qdoc: Document, qterm: Term, ddoc: Document, dterm: Term?): Double {

        val termType = if (forDoc) dterm!!.type else qterm.type
        // 权重值
        var boost = BOOST_M
        when (termType) {
            // 省市区、道路出现频次高, IDF值较低, 但重要程度最高, 因此给予比较高的加权权重
            Province, City, District -> boost = BOOST_XL
            // 一般人对于城市街道范围概念不强，在地址中随意选择街道的可能性较高，因此降权处理
            Street -> boost = BOOST_XS
            // 乡镇和村庄
            Town, Village -> {
                boost = BOOST_XS
                // 乡镇
                if (Town == termType) {
                    // 查询两个文档之间都有乡镇, 为乡镇加权。注意：存在乡镇相同、不同两种情况。
                    // > 乡镇相同：查询文档和地址库文档都加权BOOST_L，提高相似度
                    // > 乡镇不同：只有查询文档的词条加权BOOST_L, 地址库文档的词条因无法匹配不会进入该函数。结果是拉开相似度的差异
                    if (qdoc.town != null && ddoc.town != null) boost = BOOST_L
                }
                // 村庄
                else {
                    // 两个文档都有乡镇且乡镇相同，且查询文档和地址库文档都有村庄时，为村庄加权
                    // 与上述乡镇类似，存在村庄相同和不同两种情况
                    if (qdoc.village != null && ddoc.village != null && qdoc.town != null) {
                        if (qdoc.town == ddoc.town) { // 镇相同
                            if (qdoc.village == ddoc.village) boost = BOOST_XL
                            else boost = BOOST_L
                        } else if (ddoc.town != null) { // 镇不同
                            if (!forDoc) boost = BOOST_L
                            else boost = BOOST_S
                        }
                    }
                }
            }
            // 道路信息
            Road, RoadNum, Building -> {
                // 有乡镇有村庄，不再考虑道路、门牌号的加权
                if (qdoc.town == null || qdoc.village == null) {
                    // 道路
                    if (Road == termType) {
                        if (qdoc.road != null && ddoc.road != null) boost = BOOST_L
                    }
                    // 门牌号。注意：查询文档和地址库文档的门牌号都会进入此处执行, 这一点跟Road、Town、Village不同。
                    // TODO: building 暂时和道路号的权重一致, 后期需优化单独处理
                    else {
                        if (qdoc.roadNumValue > 0 && ddoc.roadNumValue > 0 && qdoc.road != null && qdoc.road == ddoc.road) {
                            if (qdoc.roadNumValue ==  ddoc.roadNumValue)
                                boost = 3.0
                            else
                                boost = if (forDoc)
                                    1 / Math.sqrt(Math.sqrt((Math.abs(qdoc.roadNumValue - ddoc.roadNumValue) + 1).toDouble())) * BOOST_L
                                else
                                    3.0
                        }
                    }
                }
            }
            Text -> boost = BOOST_M
            else -> boost = BOOST_M
        }

        return boost
    }
}