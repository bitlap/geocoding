package io.terminus.geocoding.index


/**
 * Desc: 词条的类型
 *      地址虽算不上标准结构化文本，但格式具备一定的规则性，例如省/市/区、道路/门牌号、小区/楼号/户号等
 *      词条类型用来标记该词条属于地址的哪一组成部分，主要用于相似度计算时，为不同组成部分区别性的进行加权
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
enum class TermType(val type: Char) {

    Undefined('0'),
    // 国家
    Country('C'),
    // 省
    Province('1'),
    // 地级市
    City('2'),
    // 区县
    District('3'),
    // 街道
    Street('4'),
    // 乡镇
    Town('T'),
    // 村
    Village('V'),
    // 道路
    Road('R'),
    // 门牌号
    RoadNum('N'),
    // 其他地址文本
    Text('X'),
    // 忽略项
    Ignore('I');

    // 获取枚举类型
    fun toEnum(type: Char): TermType {
        val enums = TermType.values()
        for (e in enums) {
            if (e.type == type) return e
        }
        return TermType.Undefined
    }
}