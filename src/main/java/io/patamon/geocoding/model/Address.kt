package io.patamon.geocoding.model

import java.io.Serializable

/**
 * Desc: address 实体类
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/18
 */
open class Address : Serializable {

    // 省
    var provinceId: Long? = null
    var province: String? = null
    // 市
    var cityId: Long? = null
    var city: String? = null
    // 区
    var districtId: Long? = null
    var district: String? = null
    // 街道
    var streetId: Long? = null
    var street: String? = null
    // 乡镇
    var townId: Long? = null
    var town: String? = null
    // 村
    var villageId: Long? = null
    var village: String? = null
    // 道路
    var road: String? = null
    // 道路号
    var roadNum: String? = null
    // 建筑物信息
    var buildingNum: String? = null
    // 切分剩余未解析出来的地址
    var text: String? = null

    companion object {
        // 构建一个Address对象
        fun build(entity: AddressEntity?): Address? {
            if (entity == null || !entity.hasProvince()) return null
            val address = Address()
            address.provinceId = entity.province?.id
            address.province = entity.province?.name
            address.cityId = entity.city?.id
            address.city = entity.city?.name
            address.districtId = entity.district?.id
            address.district = entity.district?.name
            address.streetId = entity.street?.id
            address.street = entity.street?.name
            address.townId = entity.town?.id
            address.town = entity.town?.name
            address.villageId = entity.village?.id
            address.village = entity.village?.name
            address.road = entity.road
            address.roadNum = entity.roadNum
            address.buildingNum = entity.buildingNum
            address.text = entity.text
            return address
        }
    }

    constructor()
    constructor(provinceId: Long?, province: String?, cityId: Long?, city: String?, districtId: Long?, district: String?, streetId: Long?, street: String?, townId: Long?, town: String?, villageId: Long?, village: String?, road: String?, roadNum: String?, buildingNum: String?, text: String?) {
        this.provinceId = provinceId
        this.province = province
        this.cityId = cityId
        this.city = city
        this.districtId = districtId
        this.district = district
        this.streetId = streetId
        this.street = street
        this.townId = townId
        this.town = town
        this.villageId = villageId
        this.village = village
        this.road = road
        this.roadNum = roadNum
        this.buildingNum = buildingNum
        this.text = text
    }

    override fun toString(): String {
        return "Address(\n\tprovinceId=$provinceId, province=$province, " +
                "\n\tcityId=$cityId, city=$city, " +
                "\n\tdistrictId=$districtId, district=$district, " +
                "\n\tstreetId=$streetId, street=$street, " +
                "\n\ttownId=$townId, town=$town, " +
                "\n\tvillageId=$villageId, village=$village, " +
                "\n\troad=$road, " +
                "\n\troadNum=$roadNum, " +
                "\n\tbuildingNum=$buildingNum, " +
                "\n\ttext=$text\n)"

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Address) return false

        if (provinceId != other.provinceId) return false
        if (province != other.province) return false
        if (cityId != other.cityId) return false
        if (city != other.city) return false
        if (districtId != other.districtId) return false
        if (district != other.district) return false
        if (streetId != other.streetId) return false
        if (street != other.street) return false
        if (townId != other.townId) return false
        if (town != other.town) return false
        if (villageId != other.villageId) return false
        if (village != other.village) return false
        if (road != other.road) return false
        if (roadNum != other.roadNum) return false
        if (buildingNum != other.buildingNum) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = provinceId?.hashCode() ?: 0
        result = 31 * result + (province?.hashCode() ?: 0)
        result = 31 * result + (cityId?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + (districtId?.hashCode() ?: 0)
        result = 31 * result + (district?.hashCode() ?: 0)
        result = 31 * result + (streetId?.hashCode() ?: 0)
        result = 31 * result + (street?.hashCode() ?: 0)
        result = 31 * result + (townId?.hashCode() ?: 0)
        result = 31 * result + (town?.hashCode() ?: 0)
        result = 31 * result + (villageId?.hashCode() ?: 0)
        result = 31 * result + (village?.hashCode() ?: 0)
        result = 31 * result + (road?.hashCode() ?: 0)
        result = 31 * result + (roadNum?.hashCode() ?: 0)
        result = 31 * result + (buildingNum?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        return result
    }

}