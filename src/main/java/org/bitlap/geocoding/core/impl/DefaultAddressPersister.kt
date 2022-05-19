package org.bitlap.geocoding.core.impl

import org.bitlap.geocoding.core.AddressPersister
import org.bitlap.geocoding.core.RegionCache
import org.bitlap.geocoding.model.RegionEntity

/**
 * Desc: 地址持久层的操作, 这边暂时只是对标准地址库的处理.
 *      暂时不将标准化后的地址存储在数据出中。
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/17
 */
open class DefaultAddressPersister (
    // 行政规划准地址库
    private val regionCache: RegionCache
) : AddressPersister {

    /**
     * 获取行政规划地址树状结构关系
     */
    override fun getRootRegion(): RegionEntity {
        return regionCache.get()
    }

    /**
     * 根据id获取
     */
    override fun getRegion(id: Long): RegionEntity? {
        return regionCache.getCache()[id]
    }

    /**
     * 新增一个region信息
     */
    override fun addRegionEntity(entity: RegionEntity) {
        regionCache.addRegionEntity(entity)
    }

}