package io.terminus.geocoding;

import io.terminus.geocoding.core.Context
import io.terminus.geocoding.model.Address

/**
 * Desc: 提供服务的主类
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
object Geocoding {

    /**
     * 地址的标准化, 将不规范的地址清洗成标准的地址格式
     */
    fun normalizing(address: String): Address? {
        return Address.build(Context.getInterpreter().interpret(address))
    }
}
