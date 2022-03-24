package org.bitlap.geocoding

import org.bitlap.geocoding.core.segment.IKAnalyzerSegmenter
import org.bitlap.geocoding.core.segment.SimpleSegmenter
import org.bitlap.geocoding.core.segment.SmartCNSegmenter
import org.bitlap.geocoding.core.segment.WordSegmenter
import org.junit.Test

/**
 * Desc: 测试 segments
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/2/6
 */
class TestSegments {

    private val simple = SimpleSegmenter()
    private val smart = SmartCNSegmenter()
    private val word = WordSegmenter()
    private val ik = IKAnalyzerSegmenter()

    @Test
    fun test_segments() {
        var text = "7号楼1单元102室"
        // text = "九鼎2期B7号楼东数新都商贸购物中心附近"

        println(">>> simple 分词: ")
        println(simple.segment(text))

//        println(">>> smart 分词: ")
//        println(smart.segment(text))

//        println(">>> word 分词: ")
//        println(word.segment(text))

        println(">>> ik 分词: ")
        println(ik.segment(text))
    }
}