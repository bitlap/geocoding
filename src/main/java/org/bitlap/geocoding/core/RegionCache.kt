package org.bitlap.geocoding.core;

import org.bitlap.geocoding.model.RegionEntity;

/**
 * Desc: 获取 region entity 的抽象接口
 *      默认从 region.dat 中获取, 还可以从比如数据库中获取
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
interface RegionCache {

    /**
     * 加载全部区域列表，按照行政区域划分构建树状结构关系
     */
    fun get(): RegionEntity


    /**
     * 加载区域map结构, key是区域id, 值是区域实体
     */
    fun getCache(): Map<Long, RegionEntity>

    /**
     * 新增一个region信息
     */
    fun addRegionEntity(entity: RegionEntity)
}
