package org.bitlap.geocoding

import org.bitlap.geocoding.core.Context
import org.bitlap.geocoding.model.Address
import org.bitlap.geocoding.model.RegionEntity
import org.bitlap.geocoding.model.RegionType
import org.bitlap.geocoding.similarity.Document
import org.bitlap.geocoding.similarity.MatchedResult


/**
 * Create custom geocoding
 */
open class GeocodingX(val ctx: Context) {

    constructor(): this("core/region.dat")
    constructor(dataClassPath: String): this(Context(dataClassPath))

    /**
     * 地址的标准化, 将不规范的地址清洗成标准的地址格式
     */
    fun normalizing(address: String): Address? {
        return Address.build(ctx.interpreter.interpret(address))
    }

    /**
     * 将地址进行切分
     */
    fun analyze(address: String): Document? {
        val addr = normalizing(address) ?: return null
        return ctx.computer.analyze(addr)
    }
    fun analyze(address: Address?): Document? {
        address ?: return null
        return ctx.computer.analyze(address)
    }

    /**
     * 地址的相似度计算
     */
    fun similarity(addr1: String, addr2: String): Double {
        val compute = ctx.computer.compute(
            normalizing(addr1),
            normalizing(addr2)
        )
        return compute.similarity
    }
    fun similarity(addr1: Address?, addr2: Address?): Double {
        val compute = ctx.computer.compute(addr1, addr2)
        return compute.similarity
    }

    /**
     * 地址相似度计算, 包含匹配的所有结果
     */
    fun similarityWithResult(addr1: String, addr2: String): MatchedResult {
        return ctx.computer.compute(
            normalizing(addr1),
            normalizing(addr2)
        )
    }
    fun similarityWithResult(addr1: Address?, addr2: Address?): MatchedResult {
        return ctx.computer.compute(addr1, addr2)
    }

    /**
     * 设置自定义地址
     *
     * @param id          地址的ID
     * @param parentId    地址的父ID, 必须存在
     * @param name        地址的名称
     * @param type        地址类型, [RegionType]
     * @param alias       地址的别名
     */
    fun addRegionEntry(id: Long, parentId: Long, name: String, type: RegionType = RegionType.Undefined, alias: String = "") {
        ctx.persister.getRegion(parentId) ?: throw IllegalArgumentException("Parent Address is not exists, parentId is $parentId")
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
        ctx.persister.addRegionEntity(region)
        // 2. Build term index
        val indexBuilder = ctx.interpreter.getTermIndexBuilder()
        indexBuilder.indexRegions(listOf(region))
    }
}