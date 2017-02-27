package io.terminus.geocoding.core.segment

import io.terminus.geocoding.core.Segmenter
import io.terminus.geocoding.utils.take

/**
 * Desc: 简单的分词, 直接按单个字符切分，连续出现的数字、英文字母会作为一个词条
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/6
 */
open class SimpleSegmenter : Segmenter {

    /**
     * 分词方法
     */
    override fun segment(text: String): List<String> {
        return segment(text, false)
    }

    /**
     * [remove] 是否去除 非ascii字符, 其实只保留英文和数字
     */
    protected fun segment(text: String, remove: Boolean): List<String> {
        val segs = arrayListOf<String>()
        if (text.isNullOrBlank()) {
            return segs
        }
        var digitNum = 0
        var ansiCharNum = 0
        for (i in 0 .. text.length - 1) {
            val c = text[i]
            // 是否是数字
            if (c in '0'..'9') {
                // 截取出字母
                if (ansiCharNum > 0) {
                    segs.add(text.take(i - ansiCharNum, i - 1))
                    ansiCharNum = 0
                }
                digitNum++
                continue
            }
            // 是否是字母
            if (c in 'A'..'Z' || c in 'a'..'z') {
                // 截取出数字
                if (digitNum > 0) {
                    segs.add(text.take(i - digitNum, i - 1))
                    digitNum = 0
                }
                ansiCharNum++
                continue
            }
            // 非数字字母时, 截取
            if (digitNum > 0 || ansiCharNum > 0) { //digitNum, ansiCharNum中只可能一个大于0
                segs.add(text.take(i - digitNum - ansiCharNum, i - 1))
                ansiCharNum = 0
                digitNum = 0
            }
            if (!remove) segs.add(c.toString())
        }
        // 截取剩余
        if (digitNum > 0 || ansiCharNum > 0) { //digitNum, ansiCharNum中只可能一个大于0
            segs.add(text.take(text.length - digitNum - ansiCharNum))
            // ansiCharNum = 0
            // digitNum = 0
        }
        return segs
    }
}