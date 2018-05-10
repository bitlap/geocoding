package io.patamon.geocoding.core.impl

import com.google.gson.Gson
import io.patamon.geocoding.core.RegionCache
import io.patamon.geocoding.model.RegionEntity
import io.patamon.geocoding.model.RegionType
import sun.misc.BASE64Decoder
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

/**
 * Desc: 默认 [RegionEntity] 获取的缓存类
 *     默认从 region.dat 中获取
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
open class DefaultRegoinCache : io.patamon.geocoding.core.RegionCache {

    private var regions: io.patamon.geocoding.model.RegionEntity? = null
    private val REGION_CACHE = hashMapOf<Long, io.patamon.geocoding.model.RegionEntity>()

    init {
        // 加载区域数据
        if (regions == null) {
            regions = Gson().fromJson(decode(String(this.javaClass.classLoader.getResourceAsStream("core/region.dat").readBytes())), io.patamon.geocoding.model.RegionEntity::class.java)
        }
        // 加载cache
        REGION_CACHE.put(regions!!.id, regions!!)
        loadChildsInCache(regions)
    }

    private fun loadChildsInCache(parent: io.patamon.geocoding.model.RegionEntity?) {
        //已经到最底层，结束
        if (parent == null || parent.type == io.patamon.geocoding.model.RegionType.Street ||
                parent.type == io.patamon.geocoding.model.RegionType.Village ||
                parent.type == io.patamon.geocoding.model.RegionType.PlatformL4 ||
                parent.type == io.patamon.geocoding.model.RegionType.Town) return

        // 递归children
        parent.children?.forEach {
            REGION_CACHE.put(it.id, it)
            this.loadChildsInCache(it)
        }
    }

    /**
     * 解压缩数据
     */
    private fun decode(dat: String): String {
        return String(GZIPInputStream(ByteArrayInputStream(BASE64Decoder().decodeBuffer(dat))).readBytes())
    }

    /**
     * 加载全部区域列表，按照行政区域划分构建树状结构关系
     */
    override fun get(): io.patamon.geocoding.model.RegionEntity {
        if (regions == null) throw IllegalArgumentException("行政规划区域数据加载失败!")
        return regions!!
    }

    /**
     * 加载区域map结构, key是区域id, 值是区域实体
     */
    override fun getCache(): Map<Long, io.patamon.geocoding.model.RegionEntity> {
        return REGION_CACHE
    }

}