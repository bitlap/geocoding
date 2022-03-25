package org.bitlap.geocoding.core

import org.bitlap.geocoding.index.TermIndexBuilder
import org.bitlap.geocoding.model.AddressEntity

/**
 * Desc: 地址解析操作
 *      从地址文本中解析出省、市、区、街道、乡镇、道路等地址组成部分
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
interface AddressInterpreter {

    /**
     * 将`脏`地址进行标准化处理, 解析成 [AddressEntity]
     */
    fun interpret(address: String?): AddressEntity?


    /**
     * 获取 [TermIndexBuilder]
     */
    fun getTermIndexBuilder(): TermIndexBuilder
}