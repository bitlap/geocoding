package io.patamon.geocoding.model

import java.io.Serializable

/**
 * Desc: address 实体类
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/18
 */
open class Address: Serializable {

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
        fun build(entity: io.patamon.geocoding.model.AddressEntity?): Address? {
            if (entity == null ||!entity.hasProvince()) return null
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
}