package org.bitlap.geocoding.core.impl

import com.google.gson.Gson
import org.bitlap.geocoding.core.RegionCache
import org.bitlap.geocoding.model.RegionEntity
import org.bitlap.geocoding.model.RegionType
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * Desc: 默认 [RegionEntity] 获取的缓存类
 *     默认从 region.dat 中获取
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
open class DefaultRegoinCache : RegionCache {

    private var regions: RegionEntity? = null
    private val REGION_CACHE = hashMapOf<Long, RegionEntity>()

    init {
        // 加载区域数据
        if (regions == null) {
            regions = Gson().fromJson(decode(String(this.javaClass.classLoader.getResourceAsStream("core/region.dat").readBytes())), RegionEntity::class.java)
        }
        // 加载cache
        REGION_CACHE.put(regions!!.id, regions!!)
        loadChildrenInCache(regions)
    }

    private fun loadChildrenInCache(parent: RegionEntity?) {
        // 已经到最底层，结束
        if (parent == null || parent.type == RegionType.Street ||
                parent.type == RegionType.Village ||
                parent.type == RegionType.PlatformL4 ||
                parent.type == RegionType.Town) return

        // 递归children
        parent.children?.forEach {
            REGION_CACHE.put(it.id, it)
            this.loadChildrenInCache(it)
        }
    }

    /**
     * 解压缩数据
     */
    private fun decode(dat: String): String {
        return String(GZIPInputStream(ByteArrayInputStream(Base64.getMimeDecoder().decode(dat))).readBytes())
    }

    /**
     * 加载全部区域列表，按照行政区域划分构建树状结构关系
     */
    override fun get(): RegionEntity {
        if (regions == null) throw IllegalArgumentException("行政规划区域数据加载失败!")
        return regions!!
    }

    /**
     * 加载区域map结构, key是区域id, 值是区域实体
     */
    override fun getCache(): Map<Long, RegionEntity> {
        return REGION_CACHE
    }

    /**
     * 新增一个region信息
     */
    override fun addRegionEntity(entity: RegionEntity) {
        this.loadChildrenInCache(entity)
        this.REGION_CACHE[entity.id] = entity
        this.REGION_CACHE[entity.parentId]?.children?.add(entity)
    }
}