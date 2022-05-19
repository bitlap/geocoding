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

    constructor(): this(false)
    constructor(strict: Boolean): this("core/region.dat", strict)
    constructor(dataClassPath: String): this(dataClassPath, false)

    /**
     * @param dataClassPath 自定义地址文档的classpath路径
     * @param strict 解析模式, 默认为false。当发现没有省和市，且匹配的父项数量等于1时，能成功匹配。
     *  * true: 严格模式，当发现没有省和市，且匹配的父项数量大于0时，返回null
     *  * false: 非严格模式，当发现没有省和市，且匹配的父项数量大于0时，匹配随机一项省和市
     */
    constructor(dataClassPath: String, strict: Boolean): this(Context(dataClassPath, strict))

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
        val add = normalizing(address) ?: return null
        return ctx.computer.analyze(add)
    }
    fun analyze(address: Address?): Document? {
        address ?: return null
        return ctx.computer.analyze(address)
    }

    /**
     * 地址的相似度计算
     */
    fun similarity(address1: String, address2: String): Double {
        val compute = ctx.computer.compute(
            normalizing(address1),
            normalizing(address2)
        )
        return compute.similarity
    }
    fun similarity(address1: Address?, address2: Address?): Double {
        val compute = ctx.computer.compute(address1, address2)
        return compute.similarity
    }

    /**
     * 地址相似度计算, 包含匹配的所有结果
     */
    fun similarityWithResult(address1: String, address2: String): MatchedResult {
        return ctx.computer.compute(
            normalizing(address1),
            normalizing(address2)
        )
    }
    fun similarityWithResult(address1: Address?, address2: Address?): MatchedResult {
        return ctx.computer.compute(address1, address2)
    }

    /**
     * 深度优先匹配符合[text]的地址信息
     */
    fun match(text: String): List<RegionEntity> {
        val terms = ctx.interpreter.getTermIndexBuilder().fullMatch(text) ?: emptyList()
        return terms.mapNotNull { it.value }
    }

    /**
     * 设置自定义地址
     *
     * @param id          地址的ID
     * @param parentId    地址的父ID, 必须存在
     * @param name        地址的名称
     * @param type        地址类型, [RegionType]
     * @param alias       地址的别名
     * @param replace     是否替换旧地址, 当除了[id]之外的字段, 如果相等就替换
     */
    fun addRegionEntry(id: Long, parentId: Long, name: String, type: RegionType = RegionType.Undefined, alias: String = "", replace: Boolean = true) {
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
        indexBuilder.indexRegions(listOf(region), replace)
    }
}