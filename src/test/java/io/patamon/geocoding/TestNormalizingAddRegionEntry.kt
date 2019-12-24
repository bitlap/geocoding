package io.patamon.geocoding

import io.patamon.geocoding.model.Address
import io.patamon.geocoding.model.RegionType
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Desc: 测试地址标准化
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/18
 */
class TestNormalizingAddRegionEntry {

    @Test
    fun testNormalizing() {
        Geocoding.addRegionEntry(888888, 321200000000, "泥煤市", RegionType.District)
        assertEquals(Geocoding.normalizing("江苏泰州泥煤市泥煤大道888号"),
                Address(
                        320000000000, "江苏省",
                        321200000000, "泰州市",
                        888888, "泥煤市",
                        null, null,
                        null, null,
                        null, null,
                        "泥煤大道", "888号",
                        null,
                        ""
                )
        )
        Geocoding.addRegionEntry(88888888, 100000000000, "尼玛省", RegionType.Province)
        Geocoding.addRegionEntry(8888888, 88888888, "尼玛市", RegionType.City)
        Geocoding.addRegionEntry(888888, 8888888, "泥煤市", RegionType.District)
        assertEquals(Geocoding.normalizing("中国尼玛省尼玛市泥煤市泥煤大道888号xxx"),
                Address(
                        88888888, "尼玛省",
                        8888888, "尼玛市",
                        888888, "泥煤市",
                        null, null,
                        null, null,
                        null, null,
                        "泥煤大道", "888号",
                        null,
                        "xxx"
                )
        )
    }
}