package io.patamon.geocoding.core;

import io.patamon.geocoding.index.TermIndexEntry;
import io.patamon.geocoding.model.Division

/**
 * Desc: 基于词条倒排索引搜索的访问者
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
interface TermIndexVisitor {

    /**
     * 开始一轮词条匹配。
     */
    fun startRound()

    /**
     * 匹配到一个索引条目，由访问者确定是否是可接受的匹配项。
     * 索引条目 [entry] 下的items一定包含一个或多个索引对象
     *
     * @return 可以接受返回true, 否则返回false。对于可以接受的索引条目调用 [endVisit] 结束访问
     */
    fun visit(entry: io.patamon.geocoding.index.TermIndexEntry, text: String, pos: Int): Boolean

    /**
     * [visit] 接受某个索引项之后当前匹配的指针位置
     */
    fun position(): Int

    /**
     * 结束索引访问
     */
    fun endVisit(entry: io.patamon.geocoding.index.TermIndexEntry, text: String, pos: Int)

    /**
     * 结束一轮词条匹配。
     */
    fun endRound()

    /**
     * 是否匹配上了结果
     */
    fun hasResult(): Boolean

    /**
     * 获取访问后最终匹配结果
     */
    fun devision(): io.patamon.geocoding.model.Division

    fun matchCount(): Int
    fun fullMatchCount(): Int

    /**
     * 获取最终匹配结果的终止位置
     */
    fun endPosition(): Int

    /**
     * 状态复位
     */
    fun reset()
}
