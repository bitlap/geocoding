package io.patamon.geocoding.core

/**
 * Desc: 分词器接口，对文本执行分词操作。
 *      实现可以是 SmartCN, IKAnalyzer, Word等等
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/2/6
 */
interface Segmenter {

    /**
     * 分词方法
     */
    fun segment(text: String): List<String>

}