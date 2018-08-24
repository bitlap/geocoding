package io.patamon.geocoding.utils

/**
 * Desc: String 一些帮助类
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/17
 */


/**
 * 获取String头部length字符的子串。
 * 此处优化边界处理
 */
fun String?.head(length: Int): String? {
    if (this.isNullOrBlank() || this!!.length <= length) return this
    if (length <= 0) return ""
    return this.substring(0, length)
}

/**
 * 获取String尾部length字符的子串。
 * 此处优化边界处理
 */
fun String?.tail(length: Int): String? {
    if (this.isNullOrBlank() || this!!.length <= length) return this
    if (length <= 0) return ""
    return this.substring(this.length - length)
}

/**
 * 提取子串, 优化边界判断
 * [begin]: 开始位置, 包括
 */
fun String.take(begin: Int): String {
    if (this.isBlank() || begin <= 0) return this
    if (begin > this.length - 1) return ""
    return this.substring(begin)
}
/**
 * 提取子串, 优化边界判断
 * [begin]: 开始位置, 包括
 * [end]: 结束位置, 包括
 */
fun String.take(begin: Int, end: Int): String {
    if (this.isBlank()) return this
    val s = if (begin <= 0) 0 else begin
    val e = if (end >= this.length - 1) this.length - 1 else end
    if (s > e) return ""
    if (s == 0 && e == this.length - 1) return this
    return this.substring(s, e + 1)
}

/**
 * 删除数组中对应的字符
 */
@JvmOverloads
fun String.remove(array: CharArray, exclude: String = ""): String {
    if (this.isBlank() || array.isEmpty()) return this
    // 去除字符
    val sb = StringBuilder(this.length)
    var remove = false
    this.forEach {
        if (array.contains(it) && !exclude.contains(it)) {
            remove = true
            return@forEach
        }
        sb.append(it)
    }
    return if (remove) sb.toString() else this
}

/**
 * 去除重复出现 [length] 个以上的数字
 * [length] : 重复出现的次数
 */
fun String.removeRepeatNum(length: Int): String {
    if (this.isBlank() || this.length < length) return this
    val sb = StringBuilder(this.length)
    var count = 0
    this.forEachIndexed { i, c ->
        if (c in '0'..'9') {
            count++
            return@forEachIndexed
        }
        // 如果小于重复出现的长度
        if (count > 0 && count < length) {
            sb.append(this.take(i - count, i - 1))
        }
        // 重置标志
        count = 0
        sb.append(c)
    }
    if (count > 0 && count < length) {
        sb.append(this.tail(count))
    }
    return sb.toString()
}

/**
 * 判断是否是纯数字
 */
fun String?.isNumericChars(): Boolean {
    if (this.isNullOrBlank()) return false
    return this!!.none {
        it !in '0'..'9'
    }
}

/**
 * 全部为 ASCII 字母
 */
fun String?.isAsciiChars(): Boolean {
    if (this.isNullOrBlank()) return false
    return this!!.none {
        it !in 'a'..'z' && it !in 'A'..'Z'
    }
}
