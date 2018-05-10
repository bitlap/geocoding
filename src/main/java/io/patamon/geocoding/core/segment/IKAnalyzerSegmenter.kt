package io.patamon.geocoding.core.segment

import io.patamon.geocoding.core.Segmenter
import org.wltea.analyzer.core.IKSegmenter
import org.wltea.analyzer.core.Lexeme
import java.io.StringReader

/**
 * Desc: ik 分词器
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/2/12
 */
class IKAnalyzerSegmenter : Segmenter {

    /**
     * 分词方法
     */
    override fun segment(text: String): List<String> {
        val segs = arrayListOf<String>()
        val reader = StringReader(text)
        // 设置ik的智能分词
        val ik = IKSegmenter(reader, true)
        var lexeme: Lexeme? = ik.next()
        while (lexeme != null) {
            segs.add(lexeme.lexemeText)
            lexeme = ik.next()
        }
        reader.close()
        return segs
    }
}