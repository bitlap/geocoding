package org.bitlap.geocoding.core.impl

import org.bitlap.geocoding.core.AddressPersister
import org.bitlap.geocoding.core.TermIndexVisitor
import org.bitlap.geocoding.index.TermIndexEntry
import org.bitlap.geocoding.index.TermIndexItem
import org.bitlap.geocoding.index.TermType
import org.bitlap.geocoding.model.Division
import org.bitlap.geocoding.model.RegionEntity
import org.bitlap.geocoding.model.RegionType
import org.bitlap.geocoding.model.RegionType.City
import org.bitlap.geocoding.model.RegionType.CityLevelDistrict
import org.bitlap.geocoding.model.RegionType.District
import org.bitlap.geocoding.model.RegionType.PlatformL4
import org.bitlap.geocoding.model.RegionType.Province
import org.bitlap.geocoding.model.RegionType.ProvinceLevelCity1
import org.bitlap.geocoding.model.RegionType.ProvinceLevelCity2
import org.bitlap.geocoding.model.RegionType.Street
import org.bitlap.geocoding.model.RegionType.Town
import org.bitlap.geocoding.model.RegionType.Village
import java.util.*

/**
 * Desc: 基于倒排索引搜索匹配省市区行政区划的访问者
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
open class RegionInterpreterVisitor (
        // 地址持久层对象
        val persister: AddressPersister
) : TermIndexVisitor {

    private var currentLevel = 0
    private var deepMostLevel = 0
    private var currentPos = -1
    private var deepMostPos = -1

    private var fullMatchCount = 0
    private var deepMostFullMatchCount = 0

    private val deepMostDivision = Division()
    private val curDivision = Division()
    private val stack = ArrayDeque<TermIndexItem>()

    companion object {
        private val ambiguousChars = mutableListOf('市', '县', '区', '镇', '乡')
    }

    /**
     * 开始一轮词条匹配。
     */
    override fun startRound() {
        currentLevel++
    }

    /**
     * 匹配到一个索引条目，由访问者确定是否是可接受的匹配项。
     * 索引条目 [entry] 下的items一定包含一个或多个索引对象
     *
     * @return 可以接受返回true, 否则返回false。对于可以接受的索引条目调用 [endVisit] 结束访问
     */
    override fun visit(entry: TermIndexEntry, text: String, pos: Int): Boolean {
        // 找到最匹配的 被索引对象. 没有匹配对象，匹配不成功，返回
        val acceptableItem = findAcceptableItem(entry, text, pos) ?: return false

        // acceptableItem可能为TermType.Ignore类型，此时其value并不是RegionEntity对象，因此下面region的值可能为null
        val region = acceptableItem.value as? RegionEntity

        // 更新当前状态
        stack.push(acceptableItem) // 匹配项压栈
        // 使用全名匹配的词条数
        if (isFullMatch(entry, region))
            fullMatchCount++
        currentPos = positioning(region, entry, text, pos) // 当前结束的位置
        updateCurrentDivisionState(region) // 刷新当前已经匹配上的省市区

        return true
    }

    private fun findAcceptableItem(entry: TermIndexEntry, text: String, pos: Int): TermIndexItem? {
        var mostPriority = -1
        var acceptableItem: TermIndexItem? = null

        entry.items ?: return null
        // 每个 被索引对象循环，找出最匹配的
        loop@ for (item in entry.items!!) {
            // 仅处理省市区类型的 被索引对象，忽略其它类型的
            if (!isAcceptableItemType(item.type!!)) continue

            //省市区中的特殊名称
            if (item.type == TermType.Ignore) {
                if (acceptableItem == null) {
                    mostPriority = 4
                    acceptableItem = item
                }
                continue
            }

            val region = item.value as RegionEntity
            // 从未匹配上任何一个省市区，则从全部被索引对象中找出一个级别最高的
            if (!curDivision.hasProvince()) {

                // 在为匹配上任务省市区情况下, 由于 `xx路` 的xx是某县区/市区/省的别名, 如江苏路, 绍兴路等等, 导致错误的匹配。
                // 如 延安路118号, 错误匹配上了延安县
                if (!isFullMatch(entry, region) && pos + 1 <= text.length - 1) {
                    if (region.type == Province
                            || region.type == City
                            || region.type in listOf(CityLevelDistrict, District)
                            || region.type == RegionType.Street
                            || region.type == RegionType.Town) { // 县区或街道

                        // 如果是某某路, 街等
                        when (text[pos + 1]) {
                            '路', '街', '巷', '道' -> continue@loop
                        }
                    }
                }

                if (mostPriority == -1) {
                    mostPriority = region.type.value
                    acceptableItem = item
                }
                if (region.type.value < mostPriority) {
                    mostPriority = region.type.value
                    acceptableItem = item
                }
                continue
            }

            // 对于省市区全部匹配, 并且当前term属于非完全匹配的时候
            // 需要忽略掉当前term, 以免污染已经匹配的省市区
            if (!isFullMatch(entry, region) && hasThreeDivision()) {
                when (region.type) {
                    Province -> {
                        if (region.id != curDivision.province!!.id) {
                            continue@loop
                        }
                    }
                    City, CityLevelDistrict -> {
                        if (region.id != curDivision.city!!.id) {
                            continue@loop
                        }
                    }
                    District -> {
                        if (region.id != curDivision.district!!.id) {
                            continue@loop
                        }
                    }
                    else -> { }
                }
            }

            // 已经匹配上部分省市区，按下面规则判断最匹配项
            // 高优先级的排除情况
            if (!isFullMatch(entry, region) && pos + 1 <= text.length - 1) { // 使用别名匹配，并且后面还有一个字符
                // 1. 湖南益阳沅江市万子湖乡万子湖村
                //   错误匹配方式：提取省市区时，将【万子湖村】中的字符【万子湖】匹配成【万子湖乡】，剩下一个【村】。
                // 2. 广东广州白云区均和街新市镇
                //   白云区下面有均和街道，街道、乡镇使用别名匹配时，后续字符不能是某些行政区域和道路关键字符
                if (region.type == Province
                        || region.type == City
                        || region.type in listOf(CityLevelDistrict, District)
                        || region.type == RegionType.Street
                        || region.type == RegionType.Town) { //街道、乡镇
                    when (text[pos + 1]) {
                        '区', '县', '乡', '镇', '村', '街', '路' -> continue@loop
                        '大' -> if (pos + 2 <= text.length - 1) {
                            val c = text[pos + 2]
                            if (c == '街' || c == '道') continue@loop
                        }
                    }
                }
            }

            // 1. 匹配度最高的情况，正好是下一级行政区域
            if (region.parentId == curDivision.leastRegion().id) {
                acceptableItem = item
                break
            }

            // 2. 中间缺一级的情况。
            if (mostPriority == -1 || mostPriority > 2) {
                val parent = persister.getRegion(region.parentId)
                // 2.1 缺地级市
                if (!curDivision.hasCity() && curDivision.hasProvince() && region.type == RegionType.District
                        && curDivision.province!!.id == parent!!.parentId) {
                    mostPriority = 2
                    acceptableItem = item
                    continue
                }
                // 2.2 缺区县
                if (!curDivision.hasDistrict() && curDivision.hasCity()
                        && (region.type == RegionType.Street || region.type == RegionType.Town
                        || region.type == RegionType.PlatformL4 || region.type == RegionType.Village)
                        && curDivision.city!!.id == parent!!.parentId) {
                    mostPriority = 2
                    acceptableItem = item
                    continue
                }
            }

            // 3. 地址中省市区重复出现的情况
            if (mostPriority == -1 || mostPriority > 3) {
                if (curDivision.hasProvince() && curDivision.province!!.id == region.id ||
                        curDivision.hasCity() && curDivision.city!!.id == region.id ||
                        curDivision.hasDistrict() && curDivision.district!!.id == region.id ||
                        curDivision.hasStreet() && curDivision.street!!.id == region.id ||
                        curDivision.hasTown() && curDivision.town!!.id == region.id ||
                        curDivision.hasVillage() && curDivision.village!!.id == region.id) {
                    mostPriority = 3
                    acceptableItem = item
                    continue
                }
            }

            // 4. 容错
            if (mostPriority == -1 || mostPriority > 4) {
                // 4.1 新疆阿克苏地区阿拉尔市
                // 到目前为止，新疆下面仍然有地级市【阿克苏地区】
                //【阿拉尔市】是县级市，以前属于地级市【阿克苏地区】，目前已变成新疆的省直辖县级行政区划
                // 即，老的行政区划关系为：新疆->阿克苏地区->阿拉尔市
                // 新的行政区划关系为：
                // 新疆->阿克苏地区
                // 新疆->阿拉尔市
                // 错误匹配方式：新疆 阿克苏地区 阿拉尔市，会导致在【阿克苏地区】下面无法匹配到【阿拉尔市】
                // 正确匹配结果：新疆 阿拉尔市
                if (region.type == RegionType.CityLevelDistrict
                        && curDivision.hasProvince() && curDivision.province!!.id == region.parentId) {
                    mostPriority = 4
                    acceptableItem = item
                    continue
                }
                // 4.2 地级市-区县从属关系错误，但区县对应的省份正确，则将使用区县的地级市覆盖已匹配的地级市
                // 主要是地级市的管辖范围有调整，或者由于外部系统地级市与区县对应关系有调整导致
                if (region.type == RegionType.District // 必须是普通区县
                        && curDivision.hasCity() && curDivision.hasProvince()
                        && isFullMatch(entry, region) // 使用的全名匹配
                        && curDivision.city!!.id != region.parentId) {
                    val city = persister.getRegion(region.parentId)!! // 区县的地级市
                    if (city.parentId == curDivision.province!!.id && !hasThreeDivision()) {
                        mostPriority = 4
                        acceptableItem = item
                        continue
                    }
                }
            }

            // 5. 街道、乡镇，且不符合上述情况
            if (region.type == RegionType.Street || region.type == RegionType.Town
                    || region.type == RegionType.Village || region.type == RegionType.PlatformL4) {
                if (!curDivision.hasDistrict()) {
                    var parent = persister.getRegion(region.parentId) // parent为区县
                    parent = persister.getRegion(parent!!.parentId) // parent为地级市
                    if (curDivision.hasCity() && curDivision.city!!.id == parent!!.id) {
                        mostPriority = 5
                        acceptableItem = item
                        continue
                    }
                } else if (region.parentId == curDivision.district!!.id) {
                    //已经匹配上区县
                    mostPriority = 5
                    acceptableItem = item
                    continue
                }
            }
        }
        return acceptableItem
    }

    private fun isFullMatch(entry: TermIndexEntry, region: RegionEntity?): Boolean {
        if (region == null) return false
        if (entry.key!!.length == region.name.length) return true
        if (region.type == Street && region.name.endsWith("街道") && region.name.length == entry.key!!.length + 1)
            return true //xx街道，使用别名xx镇、xx乡匹配上的，认为是全名匹配
        return false
    }

    /**
     * 索引对象是否是可接受的省市区等类型。
     */
    private fun isAcceptableItemType(type: TermType): Boolean {
        when (type) {
            TermType.Province, TermType.City, TermType.District,
            TermType.Street, TermType.Town, TermType.Village, TermType.Ignore -> return true
            else -> return false
        }
    }

    /**
     * 当前是否已经完全匹配了省市区了
     */
    private fun hasThreeDivision(): Boolean {
        return (curDivision.hasProvince() && curDivision.hasCity() && curDivision.hasDistrict())
            && (curDivision.city!!.parentId == curDivision.province!!.id)
            && (curDivision.district!!.parentId == curDivision.city!!.id)
    }

    private fun positioning(acceptedRegion: RegionEntity?, entry: TermIndexEntry, text: String, pos: Int): Int {
        if (acceptedRegion == null) return pos
        // 需要调整指针的情况
        // 1. 山东泰安肥城市桃园镇桃园镇山东省泰安市肥城县桃园镇东伏村
        // 错误匹配方式：提取省市区时，将【肥城县】中的字符【肥城】匹配成【肥城市】，剩下一个【县】
        if ((acceptedRegion.type == City || acceptedRegion.type == District
                || acceptedRegion.type == Street)
                && !isFullMatch(entry, acceptedRegion) && pos + 1 <= text.length - 1) {
            val c = text[pos + 1]
            if (ambiguousChars.contains(c)) { //后续跟着特殊字符
                for (child in acceptedRegion.children ?: arrayListOf()) {
                    if (child.name[0] == c) return pos
                }
                return pos + 1
            }
            // fix: 如果已经匹配最低等级
            if (curDivision.hasTown() || curDivision.hasStreet()) {
                // 如果不是特殊字符的, 由于存在 `xx小区, xx苑,  xx是以镇名字命名的情况`
                if (!ambiguousChars.contains(c)) {
                    deepMostPos = currentPos // 则不移动当前指针
                }
            }
        }
        return pos
    }

    /**
     * 更新当前已匹配区域对象的状态。
     * @param region
     */
    private fun updateCurrentDivisionState(region: RegionEntity?) {
        if (region == null) return
        // region为重复项，无需更新状态
        if (region == curDivision.province || region == curDivision.city
                || region == curDivision.district || region == curDivision.street
                || region == curDivision.town || region == curDivision.village)
            return

        when (region.type) {
            Province, ProvinceLevelCity1 -> {
                curDivision.province = region
                curDivision.city = null
            }
            City, ProvinceLevelCity2 -> {
                curDivision.city = region
                if (!curDivision.hasProvince())
                    curDivision.province = persister.getRegion(region.parentId)
            }
            CityLevelDistrict -> {
                curDivision.city = region
                curDivision.district = region
                if (!curDivision.hasProvince())
                    curDivision.province = persister.getRegion(region.parentId)
            }
            District -> {
                curDivision.district = region
                //成功匹配了区县，则强制更新地级市
                curDivision.city = persister.getRegion(curDivision.district!!.parentId)
                if (!curDivision.hasProvince())
                    curDivision.province = persister.getRegion(curDivision.city!!.parentId)
            }
            Street, PlatformL4 -> {
                if (!curDivision.hasStreet()) curDivision.street = region
                if (!curDivision.hasDistrict()) curDivision.district = persister.getRegion(region.parentId)
            }
            Town -> {
                if (!curDivision.hasTown()) curDivision.town = region
                if (!curDivision.hasDistrict()) curDivision.district = persister.getRegion(region.parentId)
            }
            Village -> {
                if (!curDivision.hasVillage()) curDivision.village = region
                if (!curDivision.hasDistrict()) curDivision.district = persister.getRegion(region.parentId)
            }
            else -> { }
        }
    }

    /**
     * [visit] 接受某个索引项之后当前匹配的指针位置
     */
    override fun position(): Int {
        return this.currentPos
    }

    /**
     * 结束索引访问
     */
    override fun endVisit(entry: TermIndexEntry, text: String, pos: Int) {
        this.checkDeepMost()

        val indexTerm = stack.pop() // 当前访问的索引对象出栈
        currentPos = pos - entry.key!!.length // 恢复当前位置指针
        val region = indexTerm.value as? RegionEntity
        if (isFullMatch(entry, region)) fullMatchCount++ //更新全名匹配的数量
        if (indexTerm.type == TermType.Ignore) return //如果是忽略项，无需更新当前已匹配的省市区状态

        // 扫描一遍stack，找出街道street、乡镇town、村庄village，以及省市区中级别最低的一个least
        var least: RegionEntity? = null
        var street: RegionEntity? = null
        var town: RegionEntity? = null
        var village: RegionEntity? = null
        stack.forEach {
            if (it.type == TermType.Ignore) return@forEach
            val r = it.value as RegionEntity
            when (r.type) {
                Street, PlatformL4 -> {
                    street = r
                    return@forEach
                }
                Town -> {
                    town = r
                    return@forEach
                }
                Village -> {
                    village = r
                    return@forEach
                }
                else -> { }
            }
            if (least == null) {
                least = r
                return@forEach
            }
        }
        if (street == null) curDivision.street = null // 剩余匹配项中没有街道了
        if (town == null) curDivision.town = null // 剩余匹配项中没有乡镇了
        if (village == null) curDivision.village = null // 剩余匹配项中没有村庄了
        // 只有街道、乡镇、村庄都没有时，才开始清空省市区
        if (curDivision.hasStreet() || curDivision.hasTown() || curDivision.hasVillage()) return
        if (least != null) {
            when (least!!.type) {
                Province, ProvinceLevelCity1 -> {
                    curDivision.city = null
                    curDivision.district = null
                    return
                }
                City, ProvinceLevelCity2 -> {
                    curDivision.district = null
                    return
                }
                else -> return
            }
        }
        // least为null，说明stack中什么都不剩了
        curDivision.province = null
        curDivision.city = null
        curDivision.district = null
    }

    /**
     * 结束一轮词条匹配。
     */
    override fun endRound() {
        this.checkDeepMost()
        currentLevel--
    }

    private fun checkDeepMost() {
        if (stack.size > deepMostLevel) {
            deepMostLevel = stack.size
            deepMostPos = currentPos
            deepMostFullMatchCount = fullMatchCount
            deepMostDivision.province = curDivision.province
            deepMostDivision.city = curDivision.city
            deepMostDivision.district = curDivision.district
            deepMostDivision.street = curDivision.street
            deepMostDivision.town = curDivision.town
            deepMostDivision.village = curDivision.village
        }
    }

    /**
     * 是否匹配上了结果
     */
    override fun hasResult(): Boolean {
        return deepMostPos > 0 && deepMostDivision.hasDistrict()
    }

    /**
     * 获取访问后的对象
     */
    override fun devision(): Division {
        return deepMostDivision
    }

    override fun matchCount(): Int {
        return deepMostLevel
    }

    override fun fullMatchCount(): Int {
        return deepMostFullMatchCount
    }

    /**
     * 获取最终匹配结果的终止位置
     */
    override fun endPosition(): Int {
        return deepMostPos
    }

    /**
     * 状态复位
     */
    override fun reset() {
        currentLevel = 0
        deepMostLevel = 0
        currentPos = -1
        deepMostPos = -1
        fullMatchCount = 0
        deepMostFullMatchCount = 0
        deepMostDivision.province = null
        deepMostDivision.city = null
        deepMostDivision.district = null
        deepMostDivision.street = null
        deepMostDivision.town = null
        deepMostDivision.village = null
        curDivision.province = null
        curDivision.city = null
        curDivision.district = null
        curDivision.street = null
        curDivision.town = null
        curDivision.village = null
    }

}