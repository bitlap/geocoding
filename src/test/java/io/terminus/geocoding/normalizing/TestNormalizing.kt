package io.terminus.geocoding.normalizing

import io.terminus.geocoding.Geocoding
import org.junit.Test
import java.sql.DriverManager

/**
 * Desc: 测试地址标准化
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/18
 */
class TestNormalizing {

    @Test
    fun testNormalizing() {
        var add = "江苏泰州兴化市昌荣镇【康琴网吧】 (昌荣镇附近)"
        add = "中国山东临沂兰山区小埠东社区居委会【绿杨榭公寓31-1-101 】 (绿杨榭公寓附近)"
        add = "抚顺顺城区将军桥【将军水泥厂住宅4-1-102】 (将军桥附近)"
        add = "中国辽宁沈阳辽中县北一路【虹桥商厦西行100米】(邮政储蓄银行北一路支行附近)"
        add = "辽宁 沈阳 辽中县中国辽宁 沈阳 辽中县虹桥商厦苏宁易购"
        add = "辽宁沈阳于洪区沈阳市辽中县县城虹桥商厦西侧三单元外跨楼梯3-2-23-"
        add = "山东济宁任城区金宇路【杨柳国际新城K8栋3单元1302】(杨柳国际新城·丽宫附近)"
        add = "上海宝山区杨行镇宝山区江杨北路98号农贸批发市场蔬菜二区7通道A16号"
        add = "上海上海宝山区宝山区【新沪路58弄11-802  水韵华庭 】 (水韵华庭附近)"
        add = "赤城街道赤城大厦10E" // 精确度缺失
        add = "上海黄浦区内环以内黄浦区小东门聚奎街43号"
        add = "河南信阳平桥区王岗镇【镇上】 (王岗乡（大杨墩）附近)"
        add = "四川自贡贡井区四川省自贡市贡井区莲花镇四川自贡贡井区莲花镇黄桷村7组22号13298213121/15609000090/18681337139" // fix
        add = "浙江嘉兴嘉善县浙江省嘉兴市嘉善县大云镇大云镇大云小区公寓楼1号302室" // fix 大云小区, 大云是镇名称的情况
        add = "辽宁沈阳铁西区中国辽宁沈阳沈阳市铁西区南十一西路12号楼472 (第九医院（沈阳）附近)" // fix xx路xx号楼
        add = "重庆重庆渝北区重庆渝北区两路镇双龙西路236号5-4（交警12支队红绿灯路口渝达商务宾馆楼上5-4）"
        add = "山东青岛市北区山东省青岛市市北区水清沟街道九江路20号大都会3号楼2单元1303" //
        add = "中国山东青岛城阳区湘潭路【华胥美邦 到了联系20-1-1402】 (中铁华胥美邦附近)"
        add = "辽宁沈阳沈河区辽宁沈阳市沈河区一环内会武街56号4-3-2"
        add = "1008中国" // fix
        add = "清徐县中国山西太原清徐县清徐县人民医院附近苹果社区2号楼1单元3层" // fix 3层/楼
        add = "辽宁辽阳宏伟区辽宁省辽阳市宏伟区新村街道龙鼎山小区B区08栋3组401号" // fix 3组
        add = "北京北京市西城区 白纸坊街道右安门内西街甲10号院11楼3门501" // fix 3门
        // add = "武夷山市中国福建南平武夷山市海晨土菜馆" // fix 3门
        add = "延川路116号绿城城园东区7号楼2单元802户" // fix 延川是县区的情况
        add = "金水路751号1号楼3单元501" // fix 延川是县区的情况
        add = "中国上海上海宝山区 顾村镇菊太路777弄24号602室"
        add = "辽宁大连甘井子区辽宁, 大连, 甘井子区, 泡崖街玉境路26号3—2—1" // fix字符 —
        add = "绍兴路59号速递易" // fix 绍兴路匹配上绍兴市的情况
        val address = Geocoding.normalizing(add)
        println(address)
    }

    @Test
    fun testImport() {
        Class.forName("com.mysql.jdbc.Driver")
        val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/geocoding", "root", "anywhere")
        val statement = connection.prepareStatement(
                "INSERT INTO `addr_address` (`province`, `city`, `district`, `street`, `text`, `town`, `village`, `road`, `road_num`, `building_num`, `raw_text`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"
        )
        TestNormalizing::class.java.classLoader.getResourceAsStream("1.txt").reader().readLines().forEach {
            val address = Geocoding.normalizing(it)
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