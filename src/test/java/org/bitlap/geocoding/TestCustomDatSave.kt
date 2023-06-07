package org.bitlap.geocoding

import org.bitlap.geocoding.model.Address
import org.bitlap.geocoding.model.RegionType
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Desc: 测试保存自定义文件
 * Mail: blvyoucan@163.com
 * Created by hechongbin
 * Date: 2023/6/7
 */
class TestCustomDatSave {

    @Test
    fun saveAndNomalizing() {
        val geocoding = GeocodingX("region_2021.dat")
        val addrss = "浙江省杭州市临平区经济开发区新颜路22号501D"

        // 未添加自定义地区"临平区"
        assertEquals(
            geocoding.normalizing(addrss),
            Address(
                330000000000, "浙江省",
                330100000000, "杭州市",
                330110000000, "余杭区",
                330110001000, "临平街道",
                null, null,
                null, null,
                null, null,
                "501",
                "区经济开发区新颜路22号D"
            )
        )

        // 添加自定义地区"临平区"
        geocoding.addRegionEntry(330113000000, 330100000000, "临平区", RegionType.District, "", true)

        val addNew = Address(
            330000000000, "浙江省",
            330100000000, "杭州市",
            330113000000, "临平区",
            null, null,
            null, null,
            null, null,
            "新颜路", "22号",
            "501",
            "D"
        )

        assertEquals(geocoding.normalizing(addrss), addNew)

        // 添加后"临平区"后保存自定义字典文件
        val filename = "mydata.dat"
        val filePath = "${this.javaClass.classLoader.getResource("").path}/$filename"
        geocoding.save(filePath)

        // 读取添加了"临平区"的自定义字典文件
        val myGeocoding = GeocodingX(filename)
        assertEquals(myGeocoding.normalizing(addrss), addNew)
    }

}