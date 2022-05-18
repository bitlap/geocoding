package org.bitlap.geocoding

import org.bitlap.geocoding.model.Address
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Desc: 测试地址标准化
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/18
 */
class TestNormalizingCustom {

    @Test
    fun testNormalizing() {
        val geocoding = GeocodingX("region_2021.dat")
        assertEquals(
            geocoding.normalizing("江苏泰州兴化市昌荣镇【康琴网吧】 (昌荣镇附近)"),
            Address(
                320000000000, "江苏省",
                321200000000, "泰州市",
                321281000000, "兴化市",
                321281119000, "昌荣镇",
                null, null,
                null, null,
                null, null,
                null,
                "康琴网吧昌荣镇附近"
            )
        )
        assertEquals(
            geocoding.normalizing("中国山东临沂兰山区小埠东社区居委会【绿杨榭公寓31-1-101 】 (绿杨榭公寓附近)"),
            Address(
                370000000000, "山东省",
                371300000000, "临沂市",
                371302000000, "兰山区",
                null, null,
                null, null,
                null, null,
                null, null,
                "31-1-101",
                "小埠东社区居委会绿杨榭公寓绿杨榭公寓附近"
            )
        )
        assertEquals(
            geocoding.normalizing("抚顺顺城区将军桥【将军水泥厂住宅4-1-102】 (将军桥附近)"),
            Address(
                210000000000, "辽宁省",
                210400000000, "抚顺市",
                210411000000, "顺城区",
                null, null,
                null, null,
                null, null,
                null,
                null,
                "4-1-102",
                "将军桥将军水泥厂住宅将军桥附近"
            )
        )
        assertEquals(
            geocoding.normalizing("辽宁沈阳于洪区沈阳市辽中县县城虹桥商厦西侧三单元外跨楼梯3-2-23-"),
            Address(
                210000000000, "辽宁省",
                210100000000, "沈阳市",
                210114000000, "于洪区",
                null, null,
                null, null,
                null, null,
                null,
                null,
                "3-2-23",
                "辽中县县城虹桥商厦西侧三单元外跨楼梯"
            )
        )
        assertEquals(
            geocoding.normalizing("山东济宁任城区金宇路【杨柳国际新城K8栋3单元1302】(杨柳国际新城·丽宫附近)"),
            Address(
                370000000000, "山东省",
                370800000000, "济宁市",
                370811000000, "任城区",
                null, null,
                null, null,
                null, null,
                "金宇路",
                "",
                "K8栋3单元1302",
                "杨柳国际新城杨柳国际新城丽宫附近"
            )
        )
        assertEquals(
            geocoding.normalizing("上海宝山区杨行镇宝山区江杨北路98号农贸批发市场蔬菜二区7通道A16号"),
            Address(
                310000000000, "上海市",
                310100000000, "直辖区",
                310113000000, "宝山区",
                310113103000, "杨行镇",
                null, null,
                null, null,
                "江杨北路",
                "98号",
                "7通道A16号",
                "农贸批发市场蔬菜二区"
            )
        )
        assertEquals(
            geocoding.normalizing("上海上海宝山区宝山区【新沪路58弄11-802  水韵华庭 】 (水韵华庭附近)"),
            Address(
                310000000000, "上海市",
                310100000000, "直辖区",
                310113000000, "宝山区",
                null, null,
                null, null,
                null, null,
                "新沪路",
                "58弄",
                "11-802",
                "水韵华庭水韵华庭附近"
            )
        )
        // 精确度缺失
        assertEquals(
            geocoding.normalizing("赤城街道赤城大厦10E"),
            Address(
                330000000000, "浙江省",
                331000000000, "台州市",
                331023000000, "天台县",
                331023001000, "赤城街道",
                null, null,
                null, null,
                null,
                null,
                null,
                "大厦10E"
            )
        )
        assertEquals(
            geocoding.normalizing("上海黄浦区内环以内黄浦区小东门聚奎街43号"),
            Address(
                310000000000, "上海市",
                310100000000, "直辖区",
                310101000000, "黄浦区",
                null, null,
                null, null,
                null, null,
                "小东门聚奎街",
                "43号",
                null,
                ""
            )
        )
        // fix 若干电话号码
        assertEquals(
            geocoding.normalizing("四川自贡贡井区四川省自贡市贡井区莲花镇四川自贡贡井区莲花镇黄桷村7组22号13298213121/15609000090/18681337139"),
            Address(
                510000000000, "四川省",
                510300000000, "自贡市",
                510303000000, "贡井区",
                510303107000, "莲花镇",
                null, null,
                null, null,
                null,
                null,
                "7组22号",
                "黄桷村"
            )
        )
        // fix 大云小区, 大云是镇名称的情
        assertEquals(
            geocoding.normalizing("浙江嘉兴嘉善县浙江省嘉兴市嘉善县大云镇大云镇大云小区公寓楼1号302室"),
            Address(
                330000000000, "浙江省",
                330400000000, "嘉兴市",
                330421000000, "嘉善县",
                330421102000, "大云镇",
                null, null,
                null, null,
                null,
                null,
                "1号302室",
                "大云小区公寓楼"
            )
        )
        // fix xx路xx号楼
        assertEquals(
            geocoding.normalizing("辽宁沈阳铁西区中国辽宁沈阳沈阳市铁西区南十一西路12号楼472 (第九医院（沈阳）附近)"),
            Address(
                210000000000, "辽宁省",
                210100000000, "沈阳市",
                210106000000, "铁西区",
                null, null,
                null, null,
                null, null,
                "南十一西路",
                "",
                "12号楼472",
                "附近第九医院沈阳"
            )
        )
        assertEquals(
            geocoding.normalizing("重庆重庆渝北区重庆渝北区两路镇双龙西路236号5-4（交警12支队红绿灯路口渝达商务宾馆楼上5-4）"),
            Address(
                500000000000, "重庆市",
                500100000000, "直辖区",
                500112000000, "渝北区",
                500112016000, "两路街道",
                null, null,
                null, null,
                "双龙西路",
                "236号",
                "5-4",
                "交警12支队红绿灯路口渝达商务宾馆楼上54"
            )
        )
        assertEquals(
            geocoding.normalizing("山东青岛市北区山东省青岛市市北区水清沟街道九江路20号大都会3号楼2单元1303"),
            Address(
                370000000000, "山东省",
                370200000000, "青岛市",
                370203000000, "市北区",
                370203030000, "水清沟街道",
                null, null,
                null, null,
                "九江路",
                "20号",
                "3号楼2单元1303",
                "大都会"
            )
        )
        assertEquals(
            geocoding.normalizing("中国山东青岛城阳区湘潭路【华胥美邦 到了联系20-1-1402】 (中铁华胥美邦附近)"),
            Address(
                370000000000, "山东省",
                370200000000, "青岛市",
                370214000000, "城阳区",
                null, null,
                null, null,
                null, null,
                "湘潭路",
                "",
                "20-1-1402",
                "华胥美邦到了联系中铁华胥美邦附近"
            )
        )
        assertEquals(
            geocoding.normalizing("辽宁沈阳沈河区辽宁沈阳市沈河区一环内会武街56号4-3-2"),
            Address(
                210000000000, "辽宁省",
                210100000000, "沈阳市",
                210103000000, "沈河区",
                null, null,
                null, null,
                null, null,
                "一环内会武街",
                "56号",
                "4-3-2",
                ""
            )
        )
        // fix 辣鸡数据
        assertEquals(geocoding.normalizing("1008中国"), null)
        // fix 3层/楼
        assertEquals(
            geocoding.normalizing("清徐县中国山西太原清徐县清徐县人民医院附近苹果社区2号楼1单元3层"),
            Address(
                140000000000, "山西省",
                140100000000, "太原市",
                140121000000, "清徐县",
                null, null,
                null, null,
                null, null,
                null,
                null,
                "2号楼1单元3层",
                "人民医院附近苹果社区"
            )
        )
        // fix 3门
        assertEquals(
            geocoding.normalizing("北京北京市西城区 白纸坊街道右安门内西街甲10号院11楼3门501"),
            Address(
                110000000000, "北京市",
                110100000000, "直辖区",
                110102000000, "西城区",
                110102019000, "白纸坊街道",
                null, null,
                null, null,
                "右安门内西街",
                "甲10号院",
                "11楼3门501",
                ""
            )
        )
        // fix 延川是县区的情况, 不能将延川路识别成延川县
        assertEquals(geocoding.normalizing("延川路116号绿城城园东区7号楼2单元802户"), null)
        // fix 同上, 不能识别成金水区
        assertEquals(geocoding.normalizing("金水路751号1号楼3单元501"), null)
        assertEquals(
            geocoding.normalizing("中国上海上海宝山区 顾村镇菊太路777弄24号602室"),
            Address(
                310000000000, "上海市",
                310100000000, "直辖区",
                310113000000, "宝山区",
                310113109000, "顾村镇",
                null, null,
                null, null,
                "菊太路",
                "777弄",
                "24号602室",
                ""
            )
        )
        // fix字符 —
        assertEquals(
            geocoding.normalizing("辽宁大连甘井子区辽宁, 大连, 甘井子区, 泡崖街道玉境路26号3—2—1"),
            Address(
                210000000000, "辽宁省",
                210200000000, "大连市",
                210211000000, "甘井子区",
                210211007000, "泡崖街道",
                null, null,
                null, null,
                "玉境路",
                "26号",
                "3-2-1",
                ""
            )
        )
        // fix 只有 1号楼 的情
        assertEquals(
            geocoding.normalizing("北京市西城区新康街2号院1号楼北侧楼房"),
            Address(
                110000000000, "北京市",
                110100000000, "直辖区",
                110102000000, "西城区",
                null, null,
                null, null,
                null, null,
                "新康街",
                "2号院",
                "1号楼",
                "北侧楼房"
            )
        )
        // Fix issues #10
        assertEquals(
            geocoding.normalizing("福建福州鼓楼区六一路111号金三桥大厦"),
            Address(
                350000000000, "福建省",
                350100000000, "福州市",
                350102000000, "鼓楼区",
                null, null,
                null, null,
                null, null,
                "六一路",
                "111号",
                null,
                "金三桥大厦"
            )
        )
        // Fix issues #8
        assertEquals(
            geocoding.normalizing("广东省河源市源城区中山大道16号华怡小区"),
            Address(
                440000000000, "广东省",
                441600000000, "河源市",
                441602000000, "源城区",
                null, null,
                null, null,
                null, null,
                "中山大道",
                "16号",
                null,
                "华怡小区"
            )

        )
        assertEquals(
            geocoding.normalizing("广东省河源市中山大道16号华怡小区"),
            Address(
                440000000000, "广东省",
                441600000000, "河源市",
                null, null,
                null, null,
                null, null,
                null, null,
                "中山大道",
                "16号",
                null,
                "华怡小区"
            )
        )
        // Fix issues #9
        assertEquals(
            geocoding.normalizing("浙江省杭州市西湖区中国建设银河西湖支行"),
            Address(
                330000000000, "浙江省",
                330100000000, "杭州市",
                330106000000, "西湖区",
                null, null,
                null, null,
                null, null,
                null,
                null,
                null,
                "中国建设银河西湖支行"
            )
        )
        assertEquals(
            geocoding.normalizing("江西赣州市赣县区王母渡镇"),
            Address(
                360000000000, "江西省",
                360700000000, "赣州市",
                360704000000, "赣县区",
                360704101000, "王母渡镇",
                null, null,
                null, null,
                null,
                null,
                null,
                ""
            )
        )
    }

    @Test
    fun testNormalizingWithStrict() {
        // 严格模式
        val geocoding = GeocodingX(true)
        assertEquals(
            geocoding.normalizing("灵山镇海榆大道4号绿地城.润园11#楼2单元203"),
            null
        )

        // 非严格模式
        val geocoding2 = GeocodingX(false)
        assertEquals(
            geocoding2.normalizing("灵山镇海榆大道4号绿地城.润园11#楼2单元203"),
            Address(
                130000000000, "河北省",
                130600000000, "保定市",
                130634000000, "曲阳县",
                130634101000, "灵山镇",
                130634101000, "灵山镇",
                null, null,
                "海榆大道",
                "4号",
                "11#楼2单元203",
                "绿地城润园"
            )
        )
    }
}