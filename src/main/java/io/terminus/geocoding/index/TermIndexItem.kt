package io.terminus.geocoding.index

/**
 * Desc: 索引对象
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/16
 */
open class TermIndexItem {
    // 索引对象类型
    var type: TermType? = null
        private set
    /** 对象, 当前是 [TermIndexItem] */
    var value: Any? = null
        private set

    constructor(type: TermType, value: Any?) {
        this.type = type
        this.value = value
    }
}