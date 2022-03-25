package org.bitlap.geocoding.similarity

import java.io.Serializable

/**
 * Desc: 相似度匹配的结果
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/2/7
 */
open class MatchedResult : Serializable {

    // 两个地址分析出的文档
    var doc1: Document? = null
    var doc2: Document? = null

    // 匹配的词条信息
    var terms: ArrayList<MatchedTerm> = arrayListOf()

    // 相似度值
    var similarity = 0.0

    override fun toString(): String {
        return "MatchedResult(\n\tdoc1=$doc1, \n\tdoc2=$doc2, \n\tterms=$terms, \n\tsimilarity=$similarity\n)"
    }
}