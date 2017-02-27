package io.terminus.geocoding.core.segment

/**
 * Desc: 简单的分词, 直接按单个字符切分，连续出现的数字、英文字母会作为一个词条.
 *      去除非 ASCII 字符 (其实只保留英文和数字)
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/28
 */
class AsciiSegmenter : SimpleSegmenter() {

    /**
     * 分词方法
     */
    override fun segment(text: String): List<String> {
        return super.segment(text, true)
    }

}