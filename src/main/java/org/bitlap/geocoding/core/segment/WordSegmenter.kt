package org.bitlap.geocoding.core.segment

import org.bitlap.geocoding.core.Segmenter

/**
 * Desc: word 分词器 @see https://github.com/ysc/word
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/2/6
 */
open class WordSegmenter : Segmenter {

    /**
     * 分词方法
     */
    override fun segment(text: String): List<String> {
        val segs = arrayListOf<String>()
        // 去除停用词
//        WordSegmenter.segWithStopWords(text).forEach {
//            segs.add(it.text)
//        }
        return segs
    }

}