package org.bitlap.geocoding.core.segment

import org.bitlap.geocoding.core.Segmenter
//import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

/**
 * Desc: lucene 的 smartCN 分词器
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/2/6
 */
open class SmartCNSegmenter : Segmenter {

//    private val ANALYZER = SmartChineseAnalyzer()

    /**
     * 分词方法
     */
    override fun segment(text: String): List<String> {
        val segs = arrayListOf<String>()
        // 切分
//        val ts = ANALYZER.tokenStream("text", text)
//        ts.reset()
//        while (ts.incrementToken()) {
//            val attr = ts.getAttribute(CharTermAttribute::class.java)
//            segs.add(attr.toString())
//        }
//        ts.end()
//        ts.close()
        return segs
    }

}