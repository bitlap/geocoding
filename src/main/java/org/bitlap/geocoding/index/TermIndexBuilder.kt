package org.bitlap.geocoding.index

import org.bitlap.geocoding.core.TermIndexVisitor
import org.bitlap.geocoding.model.RegionEntity
import org.bitlap.geocoding.model.RegionType.City
import org.bitlap.geocoding.model.RegionType.CityLevelDistrict
import org.bitlap.geocoding.model.RegionType.Country
import org.bitlap.geocoding.model.RegionType.District
import org.bitlap.geocoding.model.RegionType.PlatformL4
import org.bitlap.geocoding.model.RegionType.Province
import org.bitlap.geocoding.model.RegionType.ProvinceLevelCity1
import org.bitlap.geocoding.model.RegionType.ProvinceLevelCity2
import org.bitlap.geocoding.model.RegionType.Street
import org.bitlap.geocoding.model.RegionType.Town
import org.bitlap.geocoding.model.RegionType.Village
import org.bitlap.geocoding.utils.head

/**
 * Desc: 行政区划建立倒排索引
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/17
 */
open class TermIndexBuilder(
        rootRegion: RegionEntity,
        ignoringRegionNames: List<String>
) {

    private val indexRoot = TermIndexEntry()

    init {
        this.indexRegions(rootRegion.children ?: emptyList())
        this.indexIgnorings(ignoringRegionNames)
    }

    // 为行政区划(标准地址库建立倒排索引)
    fun indexRegions(regions: List<RegionEntity>) { synchronized(this) {
        if (regions.isEmpty()) return
        for (region in regions) {
            val indexItem = TermIndexItem(convertRegionType(region), region)
            for (alias in region.orderedNames ?: emptyList()) {
                indexRoot.buildIndex(alias, 0, indexItem)
            }

            //1. 为xx街道，建立xx镇、xx乡的别名索引项
            //2. 为xx镇，建立xx乡的别名索引项
            //3. 为xx乡，建立xx镇的别名索引项
            val rName = region.name
            var autoAlias = rName.length <= 5 && region.alias.isEmpty()
                    && (region.isTown() || rName.endsWith("街道"))
            if (autoAlias && rName.length == 5) {
                when (region.name[2]) {
                    '路', '街', '门', '镇', '村', '区' -> autoAlias = false
                }
            }
            if (autoAlias) {
                var shortName: String?
                if (region.isTown()) {
                    shortName = rName.head(rName.length - 1) ?: ""
                } else {
                    shortName = rName.head(rName.length - 2) ?: ""
                }
                // 建立索引
                if (shortName.length >= 2) {
                    indexRoot.buildIndex(shortName, 0, indexItem)
                }
                if (rName.endsWith("街道") || rName.endsWith("镇"))
                    indexRoot.buildIndex(shortName + "乡", 0, indexItem)
                if (rName.endsWith("街道") || rName.endsWith("乡"))
                    indexRoot.buildIndex(shortName + "镇", 0, indexItem)
            }

            // 递归
            if (region.children != null && region.children!!.isNotEmpty()) {
                this.indexRegions(region.children!!)
            }
        }
    } }

    /**
     * 为忽略列表建立倒排索引
     */
    fun indexIgnorings(ignoringRegionNames: List<String>) { synchronized(this) {
        if (ignoringRegionNames.isEmpty()) return
        for (ignore in ignoringRegionNames) {
            indexRoot.buildIndex(ignore, 0, TermIndexItem(TermType.Ignore, null))
        }
    } }

    // 获取 region 的类型
    private fun convertRegionType(region: RegionEntity): TermType =
        when (region.type) {
            Country -> TermType.Country
            Province, ProvinceLevelCity1 -> TermType.Province
            City, ProvinceLevelCity2 -> TermType.City
            District, CityLevelDistrict -> TermType.District
            PlatformL4 -> TermType.Street
            Town -> TermType.Town
            Village -> TermType.Village
            Street -> if (region.isTown()) TermType.Town else TermType.Street
            else -> TermType.Undefined
        }

    /**
     * 深度优先匹配词条
     */
    fun deepMostQuery(text: String?, visitor: TermIndexVisitor) {
        if (text == null || text.isEmpty()) return
        // 判断是否有中国开头
        var p = 0
        if (text.startsWith("中国") || text.startsWith("天朝")) {
            p += 2
        }
        this.deepMostQuery(text, p, visitor)
    }

    fun deepMostQuery(text: String?, pos: Int, visitor: TermIndexVisitor) {
        if (text == null || text.isEmpty()) return
        // 开始匹配
        visitor.startRound()
        this.deepFirstQueryRound(text, pos, indexRoot.children ?: emptyMap(), visitor)
        visitor.endRound()
    }

    private fun deepFirstQueryRound(text: String, pos: Int, entries: Map<Char, TermIndexEntry>, visitor: TermIndexVisitor) {
        // 获取索引对象
        if (pos > text.length - 1) return
        val entry = entries[text[pos]] ?: return

        if (entry.children != null && pos + 1 <= text.length - 1) {
            this.deepFirstQueryRound(text, pos + 1, entry.children ?: emptyMap(), visitor)
        }
        if (entry.hasItem()) {
            if (visitor.visit(entry, text, pos)) {
                // 给访问者一个调整当前指针的机会
                val p = visitor.position()
                if (p + 1 <= text.length - 1) {
                    deepMostQuery(text, p + 1, visitor)
                }
                visitor.endVisit(entry, text, p)
            }
        }
    }

    fun fullMatch(text: String?): List<TermIndexItem>? {
        if (text == null || text.isEmpty()) return null
        return fullMatch(text, 0, indexRoot.children)
    }

    private fun fullMatch(text: String, pos: Int, entries: Map<Char, TermIndexEntry>?): List<TermIndexItem>? {
        if (entries == null) return null
        val c = text[pos]
        val entry = entries[c] ?: return null
        if (pos == text.length - 1) return entry.items
        return fullMatch(text, pos + 1, entry.children)
    }
}