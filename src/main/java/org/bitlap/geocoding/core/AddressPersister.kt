package org.bitlap.geocoding.core

import org.bitlap.geocoding.model.RegionEntity

/**
 * Desc: 地址持久层的操作, 这边暂时只是对标准地址库的处理.
 *      暂时不将标准化后的地址存储在数据出中。
 * Mail: chk19940609@gmail.com
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

    /**
     * 新增一个region信息
     */
    fun addRegionEntity(entity: RegionEntity)
}