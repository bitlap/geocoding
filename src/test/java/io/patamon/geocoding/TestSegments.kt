package io.patamon.geocoding

import org.junit.Test

/**
 * Desc: 测试 segments
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/6
 */
class TestSegments {

    private val simple = io.patamon.geocoding.core.segment.SimpleSegmenter()
    private val smart = io.patamon.geocoding.core.segment.SmartCNSegmenter()
    private val word = io.patamon.geocoding.core.segment.WordSegmenter()
    private val ik = io.patamon.geocoding.core.segment.IKAnalyzerSegmenter()

    @Test
    fun test_segments() {
        var text = "7号楼1单元102室"
        // text = "九鼎2期B7号楼东数新都商贸购物中心附近"

        println(">>> simple 分词: ")
        println(simple.segment(text))

        println(">>> smart 分词: ")
        println(smart.segment(text))

//        println(">>> word 分词: ")
//        println(word.segment(text))

        println(">>> ik 分词: ")
        println(ik.segment(text))
    }
}