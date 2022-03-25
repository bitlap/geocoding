package org.bitlap.geocoding.index

import org.bitlap.geocoding.utils.head
import java.util.*

/**
 * Desc: 索引条目
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
open class TermIndexEntry {
    // 条目的key
    var key: String? = null
    // 每个条目下的所有索引对象
    var items: ArrayList<TermIndexItem>? = null
    // 子条目
    var children: HashMap<Char, TermIndexEntry>? = null


    fun addItem(item: TermIndexItem): TermIndexEntry {
        if (this.items == null) {
            this.items = arrayListOf()
        }
        this.items!!.add(item)
        return this
    }
    fun hasItem(): Boolean = this.items != null && this.items!!.isNotEmpty()

    /**
     * 初始化倒排索引
     */
    fun buildIndex(text: String?, pos: Int, item: TermIndexItem) {
        if (text.isNullOrBlank() || pos < 0 || pos >=text!!.length) {
            return
        }
        val c = text[pos]
        if (this.children == null) {
            this.children = hashMapOf()
        }
        var entry = this.children!![c]
        if (entry == null) {
            entry = TermIndexEntry()
            entry.key = text.head(pos + 1)
            this.children!!.put(c, entry)
        }
        if (pos == text.length - 1) {
            entry.addItem(item)
            return
        }
        entry.buildIndex(text, pos + 1, item)
    }
}