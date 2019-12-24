package io.patamon.geocoding;

import io.patamon.geocoding.core.Context
import io.patamon.geocoding.model.Address
import io.patamon.geocoding.model.Address.Companion.build
import io.patamon.geocoding.model.RegionEntity
import io.patamon.geocoding.model.RegionType
import io.patamon.geocoding.similarity.Document
import io.patamon.geocoding.similarity.MatchedResult

/**
 * Desc: 提供服务的主类
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
object Geocoding {

    /**
     * 地址的标准化, 将不规范的地址清洗成标准的地址格式
     */
    @JvmStatic
    fun normalizing(address: String): Address? {
        return build(Context.getInterpreter().interpret(address))
    }

    /**
     * 将地址进行切分
     */
    @JvmStatic
    fun analyze(address: String): Document? {
        val addr = normalizing(address) ?: return null
        return Context.getComputer().analyze(addr)
    }
    @JvmStatic
    fun analyze(address: Address?): Document? {
        address ?: return null
        return Context.getComputer().analyze(address)
    }

    /**
     * 地址的相似度计算
     */
    @JvmStatic
    fun similarity(addr1: String, addr2: String): Double {
        val compute = Context.getComputer().compute(normalizing(addr1), normalizing(addr2))
        return compute.similarity
    }
    @JvmStatic
    fun similarity(addr1: Address?, addr2: Address?): Double {
        val compute = Context.getComputer().compute(addr1, addr2)
        return compute.similarity
    }

    /**
     * 地址相似度计算, 包含匹配的所有结果
     */
    @JvmStatic
    fun similarityWithResult(addr1: String, addr2: String): MatchedResult {
        return Context.getComputer().compute(normalizing(addr1), normalizing(addr2))
    }
    @JvmStatic
    fun similarityWithResult(addr1: Address?, addr2: Address?): MatchedResult {
        return Context.getComputer().compute(addr1, addr2)
    }

    @JvmStatic
    fun getContext(): Context = Context

    /**
     * 设置自定义地址
     *
     * @param id          地址的ID
     * @param parentId    地址的父ID, 必须存在
     * @param name        地址的名称
     * @param type        地址类型, [RegionType]
     * @param alias       地址的别名
     */
    @JvmStatic
    fun addRegionEntry(id: Long, parentId: Long, name: String, type: RegionType = RegionType.Undefined, alias: String = "") {
        val persister = this.getContext().getPersister()
        persister.getRegion(parentId) ?: throw IllegalArgumentException("Parent Address is not exists, parentId is $parentId")
        if (name.isBlank()) {
            throw IllegalArgumentException("name should not be blank.")
        }
        // 构建 region 对象
        val region = RegionEntity()
        region.id = id
        region.parentId = parentId
        region.name = name
        region.alias = alias
        region.type = type
        // 1. Add to cache (id -> Region)
        persister.addRegionEntity(region)
        // 2. Build term index
        val indexBuilder = this.getContext().getInterpreter().getTermIndexBuilder()
        indexBuilder.indexRegions(listOf(region))
    }
}
