package org.bitlap.geocoding

import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Desc: 测试相似度
 * Mail: chk19940609@gmail.com
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

        // 特殊
        text1 = "山东青岛李沧区延川路116号绿城城园东区7号楼2单元802户"
        text2 = "山东青岛李沧区延川路绿城城园东区7-2-802"

        // 标准化
        val addr1 = Geocoding.normalizing(text1)
        val addr2 = Geocoding.normalizing(text2)
        println("addr1 >>>> $addr1")
        println(">>>>>>>>>>>>>>>>>")
        println("addr2 >>>> $addr2")

        println("相似度结果分析 >>>>>>>>> " + Geocoding.similarityWithResult(addr1, addr2))
    }

    @Test
    fun test_fix_null_test() {
        // 一般匹配
        val text1 = "中国湖南郴州宜章县梅田镇【梅田镇】(梅田镇附近)"
        val text2 = "湖南省郴州市宜章县梅田镇上寮村2组"

        // 标准化
        val addr2 = Geocoding.normalizing(text1)
        val addr1 = Geocoding.normalizing(text2)
        println("addr1 >>>> $addr1")
        println(">>>>>>>>>>>>>>>>>")
        println("addr2 >>>> $addr2")

        println("相似度结果分析 >>>>>>>>> " + Geocoding.similarityWithResult(addr1, addr2))
    }

    @Test
    fun test_similarity_threads() {
        val pool = Executors.newFixedThreadPool(10)

        val addr1 = "中国湖南郴州宜章县梅田镇【梅田镇】(梅田镇附近)"
        val addr2 = "湖南省郴州市宜章县梅田镇上寮村2组"

        (1 .. 1000).map {
            pool.submit(Callable {
                Geocoding.similarity(addr1, addr2)
            })
        }.forEach {
            val r = it.get()
            assert(0.8164965809277261 == r)
        }
        pool.shutdown()
    }
}