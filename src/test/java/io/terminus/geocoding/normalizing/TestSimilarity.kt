package io.terminus.geocoding.normalizing

import io.terminus.geocoding.Geocoding
import org.junit.Test

/**
 * Desc: 测试相似度
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/7
 */
open class TestSimilarity {

    @Test
    fun test_similarity() {
        // 一般匹配
        var text1 = "山东省沂水县四十里堡镇东艾家庄村205号"
        var text2 = "山东省沂水县四十里堡镇东艾家庄村206号"

        // 带有building匹配
        text1 = "湖南衡阳常宁市湖南省衡阳市常宁市泉峰街道泉峰街道消防大队南园小区A栋1单元601"
        text2 = "湖南衡阳常宁市湖南省衡阳市常宁市泉峰街道泉峰街道消防大队南园小区A栋2单元601"


        // 标准化
        val addr1 = Geocoding.normalizing(text1)
        val addr2 = Geocoding.normalizing(text2)
        println("addr1 >>>> $addr1")
        println(">>>>>>>>>>>>>>>>>")
        println("addr2 >>>> $addr2")

        println("相似度结果分析 >>>>>>>>> " + Geocoding.similarityWithResult(addr1, addr2))
    }

}