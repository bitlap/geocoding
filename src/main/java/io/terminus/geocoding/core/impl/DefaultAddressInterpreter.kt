package io.terminus.geocoding.core.impl

import io.terminus.geocoding.core.AddressInterpreter
import io.terminus.geocoding.core.Context
import io.terminus.geocoding.core.TermIndexVisitor
import io.terminus.geocoding.index.TermIndexBuilder
import io.terminus.geocoding.index.TermType
import io.terminus.geocoding.model.AddressEntity
import io.terminus.geocoding.model.RegionEntity
import io.terminus.geocoding.utils.head
import io.terminus.geocoding.utils.remove
import io.terminus.geocoding.utils.removeRepeatNum
import io.terminus.geocoding.utils.tail
import io.terminus.geocoding.utils.take
import java.util.regex.Pattern

/**
 * Desc: 地址解析操作
 *      从地址文本中解析出省、市、区、街道、乡镇、道路等地址组成部分
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/17
 */
open class DefaultAddressInterpreter : AddressInterpreter {

    private var indexBuilder: TermIndexBuilder? = null
    private val ignoringRegionNames = mutableListOf(
            // JD, Tmall
            "其它区", "其他地区", "其它地区", "全境", "城区", "城区以内", "城区以外", "郊区", "县城内", "内环以内", "开发区", "经济开发区", "经济技术开发区",
            // ehaier (来自TMall或HP)
            "省直辖", "省直辖市县",
            // 其他
            "地区", "市区"
    )

    init {
        // 初始化索引builder
        indexBuilder = TermIndexBuilder(Context.getPersister().getRootRegion(), ignoringRegionNames)
    }


    companion object {
        // 特殊字符1
        private val specialChars1 = " \r\n\t,，。·.．;；:：、！@$%*^`~=+&'\"|_-\\/".toCharArray()
        // 包裹的特殊字符2
        private val specialChars2 = "{}【】〈〉<>[]「」“”（）()".toCharArray()

        /**
         * 匹配没有路号的情况
         * xx路xx号楼
         * xx路xx-xx
         */
        private val P_BUILDING_NUM0 = Pattern.compile(
                //"((路|街|巷)[0-9]+号([0-9A-Z一二三四五六七八九十][\\#\\-一－/\\\\]|楼)?)?([0-9A-Z一二三四五六七八九十]+(栋|橦|幢|座|号楼|号|\\#楼?)){0,1}([一二三四五六七八九十东西南北甲乙丙0-9]+([\\#\\-一－/\\\\]|单元|门|梯|层|座))?([0-9]+(室|房)?)?"
                "((路|街|巷)[0-9]+号([0-9A-Z一二三四五六七八九十][\\#\\-一－/\\\\]|楼)?)?([0-9A-Z一二三四五六七八九十]+(栋|橦|幢|座|号楼|号|\\#楼?)){0,1}([一二三四五六七八九十东西南北甲乙丙0-9]+([\\#\\-一－/\\\\]|单元|门|梯|层|座))?([0-9]+([\\#\\-一－/\\\\]|室|房)?)?([0-9]+号?)?"
        )
        /**
         * 标准匹配building的模式：xx栋xx单元xxx。<br />
         *   注1：山东青岛市南区宁夏路118号4号楼6单元202。如果正则模式开始位置不使用(路[0-9]+号)?，则第一个符合条件的匹配结果是【118号4】,
         *   按照逻辑会将匹配结果及之后的所有字符当做building，导致最终结果为：118号4号楼6单元202
         *
         *   所以需要先匹配 (路[0-9]+号)?
         */
        private val P_BUILDING_NUM1 = Pattern.compile(
                "((路|街|巷)[0-9]+号)?([0-9A-Z一二三四五六七八九十]+(栋|橦|幢|座|号楼|号|\\#楼?)){0,1}([一二三四五六七八九十东西南北甲乙丙0-9]+(单元|门|梯|层|座))?([0-9]+(室|房)?)?"
        )
        /**
         * 校验building的模式。building1M能够匹配到纯数字等不符合条件的文本，使用building1V排除掉
         */
        private val P_BUILDING_NUM_V = Pattern.compile(
                "(栋|幢|橦|号楼|号|\\#|\\#楼|单元|室|房|门)+"
        )
        /**
         * 匹配building的模式：12-2-302，12栋3单元302
         */
        private val P_BUILDING_NUM2 = Pattern.compile(
                "[A-Za-z0-9]+([\\#\\-一－/\\\\]+[A-Za-z0-9]+)+"
        )
        /**
         * 匹配building的模式：10组21号，农村地址
         */
        private val P_BUILDING_NUM3 = Pattern.compile(
                "[0-9]+(组|通道)[A-Z0-9\\-一]+号?"
        )

        // 简单括号匹配
        private val BRACKET_PATTERN = Pattern.compile(
                "(?<bracket>([\\(（\\{\\<〈\\[【「][^\\)）\\}\\>〉\\]】」]*[\\)）\\}\\>〉\\]】」]))"
        )
        // 道路信息
        private val P_ROAD = Pattern.compile(
                "^(?<road>([\u4e00-\u9fa5]{2,6}(路|街坊|街|道|大街|大道)))(?<ex>[甲乙丙丁])?(?<roadnum>[0-9０１２３４５６７８９一二三四五六七八九十]+(号院|号楼|号大院|号|號|巷|弄|院|区|条|\\#院|\\#))?"
        )
        // 村信息
        private val P_TOWN1 = Pattern.compile("^((?<z>[\u4e00-\u9fa5]{2,2}(镇|乡))(?<c>[\u4e00-\u9fa5]{1,3}村)?)")
        private val P_TOWN2 = Pattern.compile("^((?<z>[\u4e00-\u9fa5]{1,3}镇)?(?<x>[\u4e00-\u9fa5]{1,3}乡)?(?<c>[\u4e00-\u9fa5]{1,3}村(?!(村|委|公路|(东|西|南|北)?(大街|大道|路|街))))?)")
        private val P_TOWN3 = Pattern.compile("^(?<c>[\u4e00-\u9fa5]{1,3}村(?!(村|委|公路|(东|西|南|北)?(大街|大道|路|街))))?")
        private var invalidTown: MutableSet<String> = mutableSetOf()
        private var invalidTownFollowings: MutableSet<String> = mutableSetOf()
        init {
                invalidTownFollowings.add("政府")
                invalidTownFollowings.add("大街")
                invalidTownFollowings.add("大道")
                invalidTownFollowings.add("社区")
                invalidTownFollowings.add("小区")
                invalidTownFollowings.add("小学")
                invalidTownFollowings.add("中学")
                invalidTownFollowings.add("医院")
                invalidTownFollowings.add("银行")
                invalidTownFollowings.add("中心")
                invalidTownFollowings.add("卫生")
                invalidTownFollowings.add("一小")
                invalidTownFollowings.add("一中")
                invalidTownFollowings.add("政局")
                invalidTownFollowings.add("企局")

                invalidTown.add("新村")
                invalidTown.add("外村")
                invalidTown.add("大村")
                invalidTown.add("后村")
                invalidTown.add("东村")
                invalidTown.add("南村")
                invalidTown.add("北村")
                invalidTown.add("西村")
                invalidTown.add("上村")
                invalidTown.add("下村")
                invalidTown.add("一村")
                invalidTown.add("二村")
                invalidTown.add("三村")
                invalidTown.add("四村")
                invalidTown.add("五村")
                invalidTown.add("六村")
                invalidTown.add("七村")
                invalidTown.add("八村")
                invalidTown.add("九村")
                invalidTown.add("十村")
                invalidTown.add("中村")
                invalidTown.add("街村")
                invalidTown.add("头村")
                invalidTown.add("店村")
                invalidTown.add("桥村")
                invalidTown.add("楼村")
                invalidTown.add("老村")
                invalidTown.add("户村")
                invalidTown.add("山村")
                invalidTown.add("才村")
                invalidTown.add("子村")
                invalidTown.add("旧村")
                invalidTown.add("文村")
                invalidTown.add("全村")
                invalidTown.add("和村")
                invalidTown.add("湖村")
                invalidTown.add("甲村")
                invalidTown.add("乙村")
                invalidTown.add("丙村")
                invalidTown.add("邻村")
                invalidTown.add("乡村")
                invalidTown.add("村二村")
                invalidTown.add("中关村")
                invalidTown.add("城乡")
                invalidTown.add("县乡")
                invalidTown.add("头乡")
                invalidTown.add("牌乡")
                invalidTown.add("茶乡")
                invalidTown.add("水乡")
                invalidTown.add("港乡")
                invalidTown.add("巷乡")
                invalidTown.add("七乡")
                invalidTown.add("站乡")
                invalidTown.add("西乡")
                invalidTown.add("宝乡")
                invalidTown.add("还乡")
                invalidTown.add("古镇")
                invalidTown.add("小镇")
                invalidTown.add("街镇")
                invalidTown.add("城镇")
                invalidTown.add("环镇")
                invalidTown.add("湾镇")
                invalidTown.add("岗镇")
                invalidTown.add("镇镇")
                invalidTown.add("场镇")
                invalidTown.add("新镇")
                invalidTown.add("乡镇")
                invalidTown.add("屯镇")
                invalidTown.add("大镇")
                invalidTown.add("南镇")
                invalidTown.add("店镇")
                invalidTown.add("铺镇")
                invalidTown.add("关镇")
                invalidTown.add("口镇")
                invalidTown.add("和镇")
                invalidTown.add("建镇")
                invalidTown.add("集镇")
                invalidTown.add("庙镇")
                invalidTown.add("河镇")
                invalidTown.add("村镇")
        }
    }

    /**
     * 将`脏`地址进行标准化处理, 解析成 [AddressEntity]
     */
    override fun interpret(address: String?): AddressEntity? {
        return interpret(address, Context.getVisitor())
    }

    private fun interpret(address: String?, visitor: TermIndexVisitor): AddressEntity? {
        if (address.isNullOrBlank()) return null

        val entity = AddressEntity(address)

        // 清洗下开头垃圾数据, 针对海尔用户数据
        prepare(entity)
        // extractBuildingNum, 提取建筑物号
        extractBuildingNum(entity)
        // 去除特殊字符
        removeSpecialChars(entity)
        // 提取包括的数据
        var brackets = extractBrackets(entity)
        // 去除包括的特殊字符
        brackets = brackets.remove(specialChars2)
        removeBrackets(entity)
        // 提取行政规划标准地址
        extractRegion(entity, visitor)
        // 规整省市区街道等匹配的结果
        removeRedundancy(entity, visitor)
        // 提取道路信息
        extractRoad(entity)
        // 提取农村信息
        // extractTownVillage(entity)

        entity.text = entity.text!!.replace("[0-9A-Za-z\\#]+(单元|楼|室|层|米|户|\\#)", "")
        entity.text = entity.text!!.replace("[一二三四五六七八九十]+(单元|楼|室|层|米|户)", "")
        if (brackets.isNotEmpty()) {
            entity.text = entity.text + brackets
            // 如果没有道路信息, 可能存在于 Brackets 中
            if (entity.road.isNullOrBlank()) extractRoad(entity)
        }

        return entity
    }

    // 清洗下开头垃圾数据
    private fun prepare(entity: AddressEntity) {
        // 去除开头的数字, 字母, 空格等
        if (entity.text.isNullOrBlank()) return

        val p = Pattern.compile("[ \\da-zA-Z\r\n\t,，。·.．;；:：、！@$%*^`~=+&'\"|_\\-\\/]")
        entity.text = entity.text?.trimStart {
            p.matcher("$it").find()
        }
    }

    // 提取建筑物号
    private fun extractBuildingNum(entity: AddressEntity): Boolean {
        if (entity.text.isNullOrBlank()) return false

        var found = false       // 是否找到的标志
        var building: String?   // 最后匹配的文本

        // 使用 P_BUILDING_NUM0 先进行匹配
        var matcher = P_BUILDING_NUM0.matcher(entity.text)
        while (matcher.find()) {
            if (matcher.start() == matcher.end()) continue
            building = entity.text!!.take(matcher.start(), matcher.end() - 1)
            // 查看匹配数量, 对building进行最小匹配
            var notEmptyGroups = 0
            for (i in 0 until matcher.groupCount()) {
                if (matcher.group(i) != null) notEmptyGroups++
            }
            // 如果匹配group的数量大于3, 并且匹配到了building
            // 去除前面的 `xx路xx号` 前缀
            if (P_BUILDING_NUM_V.matcher(building).find() && notEmptyGroups > 3) {
                var pos = matcher.start()
                if (building.startsWith("路") || building.startsWith("街") || building.startsWith("巷")) {
                    if (building.contains("号楼")) pos += building.indexOf("路") + 1
                    else pos += building.indexOf("号") + 1
                    building = entity.text!!.take(pos, matcher.end() - 1)
                }
                entity.buildingNum = building
                entity.text = entity.text.head(pos) + entity.text!!.take(matcher.end())
                found = true
                break
            }
        }

        if (!found) {
            matcher = P_BUILDING_NUM1.matcher(entity.text)
            while (matcher.find()) {
                if (matcher.start() == matcher.end()) continue
                building = entity.text!!.take(matcher.start(), matcher.end() - 1)
                // 查看匹配数量, 对building进行最小匹配
                var notEmptyGroups = 0
                for (i in 0 until matcher.groupCount()) {
                    if (matcher.group(i) != null) notEmptyGroups++
                }
                // 如果匹配group的数量大于3, 并且匹配到了building
                // 去除前面的 `xx路xx号` 前缀
                if (P_BUILDING_NUM_V.matcher(building).find() && notEmptyGroups > 3) {
                    var pos = matcher.start()
                    if (building.startsWith("路") || building.startsWith("街") || building.startsWith("巷")) {
                        pos += building.indexOf("号") + 1
                        building = entity.text!!.take(pos, matcher.end() - 1)
                    }
                    entity.buildingNum = building
                    entity.text = entity.text.head(pos) + entity.text!!.take(matcher.end())
                    found = true
                    break
                }
            }
        }

        if (!found) {
            //xx-xx-xx（xx栋xx单元xxx）
            matcher = P_BUILDING_NUM2.matcher(entity.text)
            if (matcher.find()) {
                entity.buildingNum = entity.text!!.take(matcher.start(), matcher.end() - 1)
                entity.text = entity.text.head(matcher.start()) + entity.text!!.take(matcher.end())
                found = true
            }
        }
        if (!found) {
            //xx组xx号, xx通道xx号
            matcher = P_BUILDING_NUM3.matcher(entity.text)
            if (matcher.find()) {
                entity.buildingNum = entity.text!!.take(matcher.start(), matcher.end() - 1)
                entity.text = entity.text.head(matcher.start()) + entity.text!!.take(matcher.end())
                found = true
            }
        }
        return found
    }

    // 去除特殊字符
    private fun removeSpecialChars(entity: AddressEntity) {
        if (entity.text.isNullOrBlank()) return

        var text = entity.text!!
        // 1. 删除特殊字符1, 简单场景比 replaceAll 优化了10~20倍
        text = text.remove(specialChars1)

        // 2. 删除连续出现6个以上的数字, TODO: 可能真会出现, 这个暂做这个处理
        text = text.removeRepeatNum(6)
        entity.text = text

        // 去除building
        var building = entity.buildingNum
        if (building.isNullOrBlank()) return
        building = building!!.remove(specialChars1, "-一－_#")
        building = building.removeRepeatNum(6)
        entity.buildingNum = building
    }

    // 去除包裹的特殊字符
    private fun removeBrackets(entity: AddressEntity) {
        if (entity.text.isNullOrBlank()) return
        entity.text = entity.text!!.remove(specialChars2)
    }

    // 提取包括的数据
    private fun extractBrackets(entity: AddressEntity): String {
        if (entity.text.isNullOrBlank()) return ""

        // 匹配出带有 `Brackets` 的文字
        // 最后将文字拼接到 text 中
        val matcher = BRACKET_PATTERN.matcher(entity.text)
        var found = false
        val brackets = StringBuilder()
        while (matcher.find()) {
            val bracket = matcher.group("bracket")
            if (bracket.length <= 2) continue   // 如果没有文字
            brackets.append(bracket.take(1, bracket.length - 2))
            found = true
        }
        if (found) {
            val result = brackets.toString()
            entity.text = matcher.replaceAll("")
            return result
        }
        return ""
    }


    // 提取标准4级地址
    private fun extractRegion(entity: AddressEntity, visitor: TermIndexVisitor): Boolean {
        if (entity.text.isNullOrBlank()) return false

        // 开始匹配
        visitor.reset()
        indexBuilder!!.deepMostQuery(entity.text, visitor)
        entity.province = visitor.devision().province
        entity.city = visitor.devision().city
        entity.district = visitor.devision().district
        entity.street = visitor.devision().street
        entity.town = visitor.devision().town
        entity.village = visitor.devision().village
        entity.text = entity.text!!.take(visitor.endPosition() + 1)
        return visitor.hasResult()
    }


    private fun removeRedundancy(entity: AddressEntity, visitor: TermIndexVisitor): Boolean {
        if (entity.text.isNullOrBlank() || !entity.hasProvince() || !entity.hasCity()) return false

        var removed = false
        //采用后序数组方式匹配省市区
        var endIndex = entity.text!!.length - 2
        var i = 0
        while (i < endIndex) {
            visitor.reset()
            indexBuilder!!.deepMostQuery(entity.text, i, visitor)
            if (visitor.matchCount() < 2 && visitor.fullMatchCount() < 1) {
                //没有匹配上，或者匹配上的行政区域个数少于2个认当做无效匹配
                i++
                continue
            }
            //匹配上的省份、地级市不正确
            if (entity.province!! != visitor.devision().province || entity.city!! != visitor.devision().city) {
                i++
                continue
            }
            //正确匹配，进行回馈
            val devision = visitor.devision()
            if (!entity.hasDistrict() && devision.hasDistrict() && devision.district!!.parentId == entity.city!!.id)
                entity.district = devision.district
            if (entity.hasDistrict() && !entity.hasStreet()
                    && devision.hasStreet() && devision.street!!.parentId == entity.district!!.id)
                entity.street = devision.street
            if (entity.hasDistrict() && !entity.hasTown()
                    && devision.hasTown() && devision.town!!.parentId == entity.district!!.id)
                entity.town = devision.town
            else if (entity.hasDistrict() && entity.hasTown() && entity.town!! == entity.street
                    && devision.hasTown()
                    && devision.town!! != devision.street
                    && devision.town!!.parentId == entity.district!!.id)
                entity.town = devision.town
            if (entity.hasDistrict() && !entity.hasVillage() && devision.hasVillage()
                    && devision.village!!.parentId == entity.district!!.id)
                entity.village = devision.village

            //正确匹配上，删除
            entity.text = entity.text!!.take(visitor.endPosition() + 1)
            endIndex = entity.text!!.length
            i = 0
            removed = true
        }
        return removed
    }

    // 提取道路信息
    private fun extractRoad(entity: AddressEntity): Boolean {
        if (entity.text.isNullOrBlank()) return false
        // 如果已经提取过了
        if (entity.road != null && entity.road!!.isNotEmpty()) return true
        val matcher = P_ROAD.matcher(entity.text)
        if (matcher.find()) {
            val road = matcher.group("road")
            val ex = matcher.group("ex")
            var roadNum: String? = matcher.group("roadnum")
            roadNum = (ex ?: "") + if (roadNum == null) "" else roadNum
            val leftText = entity.text!!.take(road.length + roadNum.length)
            if (leftText.startsWith("小区")) return false
            entity.road = road
            // 仅包含【甲乙丙丁】单个汉字，不能作为门牌号
            if (roadNum.length == 1) {
                entity.text = roadNum + leftText
            } else {
                entity.roadNum = roadNum
                entity.text = leftText
            }
            return true
        }
        return false
    }

    // 提取农村信息
    private fun extractTownVillage(addr: AddressEntity) {
        if (extractTownVillage(addr, P_TOWN1, "z", null, "c") >= 0) return
        if (addr.hasTown())
            extractTownVillage(addr, P_TOWN3, null, null, "c")
        else
            extractTownVillage(addr, P_TOWN2, "z", "x", "c")
    }

    //返回值：
    // 1: 执行了匹配操作，匹配成功
    //-1: 执行了匹配操作，未匹配上
    // 0: 未执行匹配操作
    private fun extractTownVillage(addr: AddressEntity, pattern: Pattern, gz: String?, gx: String?, gc: String?): Int {
        if (addr.text.isNullOrBlank() || !addr.hasDistrict()) return 0

        var result = -1
        val matcher = pattern.matcher(addr.text)

        if (matcher.find()) {
            val text = addr.text!!
            var c: String? = if (gc == null) null else matcher.group("c")
            var ic = if (gc == null) -1 else matcher.end("c")

            if (gz != null) {
                val z = matcher.group(gz)
                val iz = matcher.end(gz)
                if (!z.isNullOrBlank()) { //镇
                    if (z.length == 2 && text.startsWith("村", z.length)) {
                        c = z + "村"
                        ic = iz + 1
                    } else if (isAcceptableTownFollowingChars(z, text, z.length)) {
                        if (acceptTown(z, addr.district) >= 0) {
                            addr.text = text.take(iz)
                            result = 1
                        }
                    }
                }
            }

            if (gx != null) {
                val x = matcher.group(gx)
                val ix = matcher.end(gx)
                if (!x.isNullOrBlank()) { //镇
                    if (x.length == 2 && text.startsWith("村", x.length)) {
                        c = x + "村"
                        ic = ix + 1
                    } else if (isAcceptableTownFollowingChars(x, text, x.length)) {
                        if (acceptTown(x, addr.district) >= 0) {
                            addr.text = text.take(ix)
                            result = 1
                        }
                    }
                }
            }

            if (!c.isNullOrBlank()) { //村
                if (c!!.endsWith("农村")) return result
                var leftString = text.take(ic)
                if (c.endsWith("村村")) {
                    c = c.head(c.length - 1)
                    leftString = "村" + leftString
                }
                if (leftString.startsWith("委") || leftString.startsWith("民委员")) {
                    leftString = "村" + leftString
                }
                if (c!!.length >= 4 && (c[0] == '东' || c[0] == '西' || c[0] == '南' || c[0] == '北'))
                    c = c.tail(c.length - 1)
                if (c!!.length == 2 && !isAcceptableTownFollowingChars(c, leftString, 0)) return ic
                if (acceptTown(c, addr.district) >= 0) {
                    addr.text = leftString
                    result = 1
                }
            }
        }
        return result
    }

    private fun isAcceptableTownFollowingChars(matched: String, text: String?, start: Int): Boolean {
        if (text == null || start >= text.length) return true
        if (matched.length == 4) {
            when (text[start]) {
                '区', '县', '乡', '镇', '村', '街', '路' -> return false
            }
        }
        var s1 = text.take(start, start + 1)
        if (invalidTownFollowings.contains(s1)) return false
        s1 = text.take(start, start + 2)
        if (invalidTownFollowings.contains(s1)) return false
        return true
    }

    //返回值：
    // -1: 无效的匹配
    //  0: 有效的匹配，无需执行添加操作
    //  1: 有效的匹配，已经执行添加操作
    private fun acceptTown(town: String?, district: RegionEntity?): Int {
        if (town.isNullOrBlank() || district == null) return -1
        if (invalidTown.contains(town)) return -1

        // 已加入bas_region表，不再添加
        val items = indexBuilder!!.fullMatch(town)
        if (items != null) {
            for (item in items) {
                if (item.type != TermType.Town && item.type != TermType.Street && item.type != TermType.Village)
                    continue
                val region = item.value as RegionEntity
                if (region.parentId == district.id) return 0
            }
        }

        // 排除一些特殊情况：草滩街镇、西乡街镇等
        if (town!!.length == 4 && town[2] == '街') return -1

        return 1
    }
}

