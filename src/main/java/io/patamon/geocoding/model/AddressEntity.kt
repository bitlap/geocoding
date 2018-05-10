package io.patamon.geocoding.model

import java.io.Serializable

/**
 * Desc: 标准地址实体类
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/17
 */
open class AddressEntity constructor() : Division(), Serializable {

    /**
     * 解析地址后剩余的地址
     */
    var text: String? = null
        set(value) {
            if (value == null) field = "" else field = value.trim()
        }
    /**
     * 解析出的道路信息
     */
    var road: String? = null
        set(value) {
            if (value == null) field = "" else field = value.trim()
        }
    /**
     * 解析出的道路号
     */
    var roadNum: String? = null
        set(value) {
            if (value == null) field = "" else field = value.trim()
        }
    /**
     * 解析出的建筑信息
     */
    var buildingNum: String? = null
        set(value) {
            if (value == null) field = "" else field = value.trim()
        }
    /**
     * 源地址的hash值, 保留做唯一性处理
     */
    var hash: Int? = null
    /**
     * 源地址保留
     */
    var address: String? = null

    constructor(address: String?) : this() {
        this.address = address
        this.text = address
    }
}