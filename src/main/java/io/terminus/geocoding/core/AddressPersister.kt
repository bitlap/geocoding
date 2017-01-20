package io.terminus.geocoding.core

import io.terminus.geocoding.model.RegionEntity

/**
 * Desc: 地址持久层的操作, 这边暂时只是对标准地址库的处理.
 *      暂时不将标准化后的地址存储在数据出中。
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
interface AddressPersister {

    /**
     * 获取行政规划地址树状结构关系
     */
    fun getRootRegion(): RegionEntity

    /**
     * 根据id获取
     */
    fun getRegion(id: Long): RegionEntity?
}