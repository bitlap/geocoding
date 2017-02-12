package io.terminus.geocoding.similarity

import java.io.Serializable

/**
 * Desc: 文档对象
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/5
 */
open class Document : Serializable {

    // 文档所有词条, 按照文档顺序, 未去重
    var terms: List<Term>? = null
    // Term.text -> Term
    var termsMap: HashMap<String, Term>? = null

    // 乡镇相关的词条信息
    var town: Term? = null
    var village: Term? = null

    // 道路信息
    var road: Term? = null
    var roadNum: Term? = null
    var roadNumValue = 0

    /**
     * 获取 Term
     */
    fun getTerm(text: String?): Term? {
        if (this.terms == null || this.terms!!.isEmpty()) return null
        if (this.termsMap == null) {
            // build cache
            synchronized(this) {
                if (this.termsMap == null) {
                    this.termsMap = hashMapOf()
                    this.terms?.forEach {
                        if (!it.text.isNullOrBlank()) {
                            this.termsMap!!.put(it.text!!, it)
                        }
                    }
                }
            }
        }
        return this.termsMap!![text]
    }

    override fun toString(): String {
        return "Document(terms=$terms, town=$town, village=$village, road=$road, roadNum=$roadNum, roadNumValue=$roadNumValue)"
    }

}