package org.bitlap.geocoding;

import org.bitlap.geocoding.core.Context
import org.bitlap.geocoding.model.Address
import org.bitlap.geocoding.model.RegionType
import org.bitlap.geocoding.similarity.Document
import org.bitlap.geocoding.similarity.MatchedResult

/**
 * Desc: 提供服务的主类
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
object Geocoding {

    @JvmStatic
    val DEFAULT = GeocodingX()

    /**
     * 地址的标准化, 将不规范的地址清洗成标准的地址格式
     */
    @JvmStatic
    fun normalizing(address: String): Address? {
        return DEFAULT.normalizing(address)
    }

    /**
     * 将地址进行切分
     */
    @JvmStatic
    fun analyze(address: String): Document? {
        return DEFAULT.analyze(address)
    }
    @JvmStatic
    fun analyze(address: Address?): Document? {
        return DEFAULT.analyze(address)
    }

    /**
     * 地址的相似度计算
     */
    @JvmStatic
    fun similarity(address1: String, address2: String): Double {
        return DEFAULT.similarity(address1, address2)
    }
    @JvmStatic
    fun similarity(address1: Address?, address2: Address?): Double {
        return DEFAULT.similarity(address1, address2)
    }

    /**
     * 地址相似度计算, 包含匹配的所有结果
     */
    @JvmStatic
    fun similarityWithResult(address1: String, address2: String): MatchedResult {
        return DEFAULT.similarityWithResult(address1, address2)
    }
    @JvmStatic
    fun similarityWithResult(address1: Address?, address2: Address?): MatchedResult {
        return DEFAULT.similarityWithResult(address1, address2)
    }

    @JvmStatic
    fun getContext(): Context = DEFAULT.ctx

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
    fun addRegionEntry(id: Long, parentId: Long, name: String, type: RegionType = RegionType.Undefined, alias: String = ""): Geocoding {
        DEFAULT.addRegionEntry(id, parentId, name, type, alias)
        return this
    }
}
