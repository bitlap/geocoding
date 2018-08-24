package io.patamon.geocoding

import io.patamon.geocoding.model.Address
import org.junit.Test
import java.sql.DriverManager
import kotlin.test.assertEquals

/**
 * Desc: 测试地址标准化
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/18
 */
class TestNormalizing {

    @Test
    fun testNormalizing() {
        assertEquals(Geocoding.normalizing("江苏泰州兴化市昌荣镇【康琴网吧】 (昌荣镇附近)"),
                Address(
                        320000000000, "江苏省",
                        321200000000, "泰州市",
                        321281000000, "兴化市",
                        321281119000, "昌荣镇",
                        321281119000, "昌荣镇",
                        null, null,
                        null, null,
                        null,
                        "康琴网吧昌荣镇附近"
                )
        )
        assertEquals(Geocoding.normalizing("中国山东临沂兰山区小埠东社区居委会【绿杨榭公寓31-1-101 】 (绿杨榭公寓附近)"),
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
        assertEquals(Geocoding.normalizing("抚顺顺城区将军桥【将军水泥厂住宅4-1-102】 (将军桥附近)"),
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
        assertEquals(Geocoding.normalizing("中国辽宁沈阳辽中县北一路【虹桥商厦西行100米】(邮政储蓄银行北一路支行附近)"),
                Address(
                        210000000000, "辽宁省",
                        210100000000, "沈阳市",
                        210122000000, "辽中县",
                        null, null,
                        null, null,
                        null, null,
                        null,
                        null,
                        null,
                        "北路虹桥商厦西行100米邮政储蓄银行北路支行附近"
                )
        )
        assertEquals(Geocoding.normalizing("辽宁 沈阳 辽中县中国辽宁 沈阳 辽中县虹桥商厦苏宁易购"),
                Address(
                        210000000000, "辽宁省",
                        210100000000, "沈阳市",
                        210122000000, "辽中县",
                        null, null,
                        null, null,
                        null, null,
                        null,
                        null,
                        null,
                        "虹桥商厦苏宁易购"
                )
        )
        assertEquals(Geocoding.normalizing("辽宁沈阳于洪区沈阳市辽中县县城虹桥商厦西侧三单元外跨楼梯3-2-23-"),
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
                        "县城虹桥商厦西侧三单元外跨楼梯"
                )
        )
        assertEquals(Geocoding.normalizing("山东济宁任城区金宇路【杨柳国际新城K8栋3单元1302】(杨柳国际新城·丽宫附近)"),
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
        assertEquals(Geocoding.normalizing("上海宝山区杨行镇宝山区江杨北路98号农贸批发市场蔬菜二区7通道A16号"),
                Address(
                        310000000000, "上海",
                        310100000000, "上海市",
                        310113000000, "宝山区",
                        310113103000, "杨行镇",
                        310113103000, "杨行镇",
                        null, null,
                        "江杨北路",
                        "98号",
                        "7通道A16号",
                        "农贸批发市场蔬菜二区"
                )
        )
        assertEquals(Geocoding.normalizing("上海上海宝山区宝山区【新沪路58弄11-802  水韵华庭 】 (水韵华庭附近)"),
                Address(
                        310000000000, "上海",
                        310100000000, "上海市",
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
        assertEquals(Geocoding.normalizing("赤城街道赤城大厦10E"),
                Address(
                        130000000000, "河北省",
                        130700000000, "张家口市",
                        130732000000, "赤城县",
                        331023001000, "赤城街道",
                        null, null,
                        null, null,
                        null,
                        null,
                        null,
                        "大厦10E"
                )
        )
        assertEquals(Geocoding.normalizing("上海黄浦区内环以内黄浦区小东门聚奎街43号"),
                Address(
                        310000000000, "上海",
                        310100000000, "上海市",
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
        assertEquals(Geocoding.normalizing("河南信阳平桥区王岗镇【镇上】 (王岗乡（大杨墩）附近)"),
                Address(
                        410000000000, "河南省",
                        411500000000, "信阳市",
                        411503000000, "平桥区",
                        411503209000, "王岗乡",
                        411503209000, "王岗乡",
                        null, null,
                        null,
                        null,
                        null,
                        "附近镇上王岗乡大杨墩"
                )
        )
        // fix 若干电话号码
        assertEquals(Geocoding.normalizing("四川自贡贡井区四川省自贡市贡井区莲花镇四川自贡贡井区莲花镇黄桷村7组22号13298213121/15609000090/18681337139"),
                Address(
                        510000000000, "四川省",
                        510300000000, "自贡市",
                        510303000000, "贡井区",
                        510303107000, "莲花镇",
                        510303107000, "莲花镇",
                        null, null,
                        null,
                        null,
                        "7组22号",
                        "黄桷村"
                )
        )
        // fix 大云小区, 大云是镇名称的情
        assertEquals(Geocoding.normalizing("浙江嘉兴嘉善县浙江省嘉兴市嘉善县大云镇大云镇大云小区公寓楼1号302室"),
                Address(
                        330000000000, "浙江省",
                        330400000000, "嘉兴市",
                        330421000000, "嘉善县",
                        330421102000, "大云镇",
                        330421102000, "大云镇",
                        null, null,
                        null,
                        null,
                        "1号302室",
                        "大云小区公寓楼"
                )
        )
        // fix xx路xx号楼
        assertEquals(Geocoding.normalizing("辽宁沈阳铁西区中国辽宁沈阳沈阳市铁西区南十一西路12号楼472 (第九医院（沈阳）附近)"),
                Address(
                        210000000000, "辽宁省",
                        210100000000, "沈阳市",
                        210106000000, "铁西区",
                        null, null,
                        null, null,
                        null, null,
                        "南十西路",
                        "",
                        "12号楼472",
                        "附近第九医院沈阳"
                )
        )
        assertEquals(Geocoding.normalizing("重庆重庆渝北区重庆渝北区两路镇双龙西路236号5-4（交警12支队红绿灯路口渝达商务宾馆楼上5-4）"),
                Address(
                        500000000000, "重庆",
                        500100000000, "重庆市",
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
        assertEquals(Geocoding.normalizing("山东青岛市北区山东省青岛市市北区水清沟街道九江路20号大都会3号楼2单元1303"),
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
        assertEquals(Geocoding.normalizing("中国山东青岛城阳区湘潭路【华胥美邦 到了联系20-1-1402】 (中铁华胥美邦附近)"),
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
        assertEquals(Geocoding.normalizing("辽宁沈阳沈河区辽宁沈阳市沈河区一环内会武街56号4-3-2"),
                Address(
                        210000000000, "辽宁省",
                        210100000000, "沈阳市",
                        210103000000, "沈河区",
                        null, null,
                        null, null,
                        null, null,
                        "环内会武街",
                        "56号",
                        "4-3-2",
                        ""
                )
        )
        // fix 辣鸡数据
        assertEquals(Geocoding.normalizing("1008中国"), null)
        // fix 3层/楼
        assertEquals(Geocoding.normalizing("清徐县中国山西太原清徐县清徐县人民医院附近苹果社区2号楼1单元3层"),
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
        // fix 3组
        assertEquals(Geocoding.normalizing("辽宁辽阳宏伟区辽宁省辽阳市宏伟区新村街道龙鼎山小区B区08栋3组401号"),
                Address(
                        210000000000, "辽宁省",
                        211000000000, "辽阳市",
                        211004000000, "宏伟区",
                        211004003000, "新村街道",
                        null, null,
                        null, null,
                        null,
                        null,
                        "08栋3组401号",
                        "龙鼎山小区B区"
                )
        )
        // fix 3门
        assertEquals(Geocoding.normalizing("北京北京市西城区 白纸坊街道右安门内西街甲10号院11楼3门501"),
                Address(
                        110000000000, "北京",
                        110100000000, "北京市",
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
        // 这种辣鸡数据, 忽略
        assertEquals(Geocoding.normalizing("武夷山市中国福建南平武夷山市海晨土菜馆"), null)
        // fix 延川是县区的情况, 不能将延川路识别成延川县
        assertEquals(Geocoding.normalizing("延川路116号绿城城园东区7号楼2单元802户"), null)
        // fix 绍兴路匹配上绍兴市的情况
        assertEquals(Geocoding.normalizing("绍兴路59号速递易"), null)
        // fix 同上, 不能识别成金水区
        assertEquals(Geocoding.normalizing("金水路751号1号楼3单元501"), null)
        assertEquals(Geocoding.normalizing("中国上海上海宝山区 顾村镇菊太路777弄24号602室"),
                Address(
                        310000000000, "上海",
                        310100000000, "上海市",
                        310113000000, "宝山区",
                        310113109000, "顾村镇",
                        310113109000, "顾村镇",
                        null, null,
                        "菊太路",
                        "777弄",
                        "24号602室",
                        ""
                )
        )
        // fix字符 —
        assertEquals(Geocoding.normalizing("辽宁大连甘井子区辽宁, 大连, 甘井子区, 泡崖街玉境路26号3—2—1"),
                Address(
                        210000000000, "辽宁省",
                        210200000000, "大连市",
                        210211000000, "甘井子区",
                        null, null,
                        null, null,
                        null, null,
                        "泡崖街玉境路",
                        "26号",
                        "3—2—1",
                        ""
                )
        )
        // fix 开发区的影响
        assertEquals(Geocoding.normalizing("山东德州德城区宋官屯街道开发区段庄村"),
                Address(
                        370000000000, "山东省",
                        371400000000, "德州市",
                        371402000000, "德城区",
                        371402008000, "宋官屯街道",
                        null, null,
                        null, null,
                        null,
                        null,
                        null,
                        "段庄村"
                )
        )
        // fix 只有 1号楼 的情
        assertEquals(Geocoding.normalizing("北京市西城区新康街2号院1号楼北侧楼房"),
                Address(
                        110000000000, "北京",
                        110100000000, "北京市",
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
    }


    /**
     * 将测试数据解析, 载入到数据库, 便于观察
     *
     * 表结构在 sql/creat.sql
     *
     * 注意: 自行修改数据库连接地址
     */
    // @Test
    fun testImport() {
        Class.forName("com.mysql.jdbc.Driver")
        val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/geocoding", "root", "anywhere")
        val statement = connection.prepareStatement(
                "INSERT INTO `addr_address` (`province`, `city`, `district`, `street`, `text`, `town`, `village`, `road`, `road_num`, `building_num`, `raw_text`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"
        )
        TestNormalizing::class.java.classLoader.getResourceAsStream("address.txt").reader().readLines().forEach {
            val address = io.patamon.geocoding.Geocoding.normalizing(it)
            statement.setLong(1, address?.provinceId ?: 0)
            statement.setLong(2, address?.cityId ?: 0)
            statement.setLong(3, address?.districtId ?: 0)
            statement.setLong(4, address?.streetId ?: 0)
            statement.setString(5, address?.text ?: "")
            statement.setString(6, address?.town ?: "")
            statement.setString(7, address?.village ?: "")
            statement.setString(8, address?.road ?: "")
            statement.setString(9, address?.roadNum ?: "")
            statement.setString(10, address?.buildingNum ?: "")
            statement.setString(11, it)

            statement.execute()
        }

        statement.close()
        connection.close()
    }

}