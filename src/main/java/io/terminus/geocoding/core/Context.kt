package io.terminus.geocoding.core

import io.terminus.geocoding.core.impl.DefaultAddressInterpreter
import io.terminus.geocoding.core.impl.DefaultAddressPersister
import io.terminus.geocoding.core.impl.DefaultRegoinCache
import io.terminus.geocoding.core.impl.RegionInterpreterVisitor

/**
 * Desc: 上下文
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
object Context {

    private var interpreter: AddressInterpreter? = null
    private var persister: AddressPersister? = null

    init {
        // region entity默认, 此处暂时直接实例化
        persister = DefaultAddressPersister(DefaultRegoinCache())
        // 实例化
        interpreter = DefaultAddressInterpreter()
    }

    // 获取 AddressInterpreter
    fun getInterpreter(): AddressInterpreter {
        interpreter ?: throw IllegalArgumentException("[Context] -> 地址解析服务类初始化失败.")
        return interpreter!!
    }

    // 获取 AddressPersister
    fun getPersister(): AddressPersister {
        persister ?: throw IllegalArgumentException("[Context] -> 地址持久化服务类初始化失败.")
        return persister!!
    }

    // 获取 visitor
    fun getVisitor(): TermIndexVisitor {
        return RegionInterpreterVisitor(getPersister())
    }
}