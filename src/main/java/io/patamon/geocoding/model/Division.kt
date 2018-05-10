package io.patamon.geocoding.model

import io.patamon.geocoding.model.RegionType.PlatformL4
import io.patamon.geocoding.model.RegionType.Street
import io.patamon.geocoding.model.RegionType.Town

/**
 * Desc: 行政区规范实体
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/13
 */
open class Division {

    // 省
    var province: io.patamon.geocoding.model.RegionEntity? = null
    // 市
    var city: io.patamon.geocoding.model.RegionEntity? = null
    // 区
    var district: io.patamon.geocoding.model.RegionEntity? = null
    // 街道
    var street: io.patamon.geocoding.model.RegionEntity? = null
    // 乡镇
    var town: io.patamon.geocoding.model.RegionEntity? = null
        set(town) {
            town ?: return
            when(town.type) {
                Town -> field = town
                Street, PlatformL4 -> this.street = town
                else -> return
            }
        }
        get() {
            if (field != null) return field
            if (this.street == null) return null
            return if (this.street!!.isTown()) this.street else null
        }
    // 村
    var village: io.patamon.geocoding.model.RegionEntity? = null


    fun hasProvince(): Boolean = this.province != null
    fun hasCity(): Boolean = this.city != null
    fun hasDistrict(): Boolean = this.district != null
    fun hasStreet(): Boolean = this.street != null
    fun hasTown(): Boolean = this.town != null
    fun hasVillage(): Boolean = this.village != null

    /**
     * 获取最小一级有效行政区域对象。
     */
    fun leastRegion(): io.patamon.geocoding.model.RegionEntity {
        if (hasVillage()) return this.village!!
        if (hasTown()) return this.town!!
        if (hasStreet()) return this.street!!
        if (hasDistrict()) return this.district!!
        if (hasCity()) return this.city!!
        return this.province!!
    }
}