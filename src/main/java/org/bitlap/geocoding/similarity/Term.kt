package org.bitlap.geocoding.similarity

import org.bitlap.geocoding.similarity.Term.TermType.City
import org.bitlap.geocoding.similarity.Term.TermType.District
import org.bitlap.geocoding.similarity.Term.TermType.Ignore
import org.bitlap.geocoding.similarity.Term.TermType.Province
import org.bitlap.geocoding.similarity.Term.TermType.Street
import org.bitlap.geocoding.similarity.Term.TermType.Town
import java.io.Serializable

/**
 * Desc: 词条
 * Mail: chk19940609@gmail.com
 * Created by
 * Date: 2017/2/5
 */
open class Term : Serializable {
    // 词条内容
    var text: String? = null

    // 词条类型
    var type: TermType? = null

    // Inverse Document Frequency，逆文档词频
    var idf: Double? = null
        get() {
            when (type) {
                Province, City, District -> return 0.0
                Street -> return 1.0
                // Town, Village, Road, RoadNum, Text,
                else -> return field
            }
        }

    // 相关联的词条引用
    var ref: Term? = null

    constructor(type: TermType, text: String?) {
        this.type = type
        if (text == null) {
            this.text = null
            return
        }
        when (type) {
            Province, City, District, Street, Town, Ignore -> this.text = text.intern()
            else -> this.text = text
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other.javaClass != Term::class.java)
            return false
        val t = other as Term
        if (this.text == null) return t.text == null
        return this.text == t.text
    }

    override fun hashCode(): Int {
        if (this.text == null) return 0
        return this.text!!.hashCode()
    }

    override fun toString(): String {
        return "Term($text)"
    }


    // 词条类型, 主要用于给每部分加权重
    enum class TermType(val value: Char) {
        Undefined('0'),
        // 省
        Province('1'),
        // 地级市
        City('2'),
        // 区县
        District('3'),
        // 街道
        Street('4'),
        // 乡镇
        Town('T'),
        // 村
        Village('V'),
        // 道路
        Road('R'),
        // 门牌号
        RoadNum('N'),
        // 建筑物号
        Building('B'),
        // 其他地址文本
        Text('X'),
        // 忽略项
        Ignore('I');
    }


}