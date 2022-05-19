package org.bitlap.geocoding.model

import org.bitlap.geocoding.model.RegionType.Street
import org.bitlap.geocoding.model.RegionType.Town
import java.io.Serializable
import java.util.*

/**
 * Desc: 区域实体类, 标准地址库4级地址(region.dat from Taobao, JD)
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
open class RegionEntity : Serializable {

    var id: Long = 0
    var parentId: Long = 0
    var name: String = ""
    var alias = ""
    var type: RegionType = RegionType.Undefined
    var zip = ""
    var children: ArrayList<RegionEntity>? = null
    var orderedNames: List<String>? = null
        get() {
            synchronized(this) {
                if (field != null) return field
                field = buildOrderedNames()
                return field
            }
        }

    // 创建排序后的别名, 并按照长度排序
    private fun buildOrderedNames(): List<String> {
        val fields = mutableListOf(this.name)
        if (this.alias.isBlank()) return fields
        this.alias.split(";").forEach {
            if (it.isNotBlank()) {
                fields.add(it)
            }
        }
        // 按长度倒序
        fields.sortWith { t1, t2 ->
            t2.length - t1.length
        }
        return fields
    }

    /**
     * 判断是否是乡镇
     */
    fun isTown(): Boolean {
        when (this.type) {
            Town -> return true
            Street -> {
                if (this.name.isBlank()) return false
                return this.name.length <= 4 && (this.name.last() == '镇' || this.name[this.name.lastIndex] == '乡')
            }
            else -> return false
        }
    }


    override fun equals(other: Any?): Boolean {
        if (other == null || other.javaClass != RegionEntity::class.java) return false
        val region = other as RegionEntity?
        return this.id == region!!.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    fun equalsWithoutId(other: Any?): Boolean {
        if (other == null || other.javaClass != RegionEntity::class.java) return false
        other as RegionEntity

        if (parentId != other.parentId) return false
        if (name != other.name) return false
        if (alias != other.alias) return false
        if (type != other.type) return false
        if (zip != other.zip) return false

        return true
    }

    override fun toString(): String {
        return "RegionEntity(id=$id, parentId=$parentId, name='$name', alias='$alias', type=$type, zip='$zip')"
    }
}