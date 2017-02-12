package io.terminus.geocoding.similarity

import java.io.Serializable

/**
 * Desc: 匹配的词条信息
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/7
 */
open class MatchedTerm : Serializable {

    // 匹配的词条
    var term: Term? = null

    // 匹配率
    var coord: Double = 0.0

    // 稠密度
    var density: Double = 0.0

    // 权重
    var boost: Double = 0.0

    // 特征值 TF-IDF
    var tfidf: Double = 0.0

    constructor(term: Term) {
        this.term = term
    }
}