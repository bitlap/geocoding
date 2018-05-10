package io.patamon.geocoding.core

import io.patamon.geocoding.core.impl.DefaultAddressInterpreter
import io.patamon.geocoding.core.impl.DefaultAddressPersister
import io.patamon.geocoding.core.impl.DefaultRegoinCache
import io.patamon.geocoding.core.impl.RegionInterpreterVisitor
import io.patamon.geocoding.core.impl.SimilarityComputer

/**
 * Desc: 上下文
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/1/12
 */
object Context {

    private var interpreter: io.patamon.geocoding.core.AddressInterpreter? = null
    private var persister: io.patamon.geocoding.core.AddressPersister? = null
    private var computer: io.patamon.geocoding.core.Computer? = null

    init {
        // region entity默认, 此处暂时直接实例化
        persister = io.patamon.geocoding.core.impl.DefaultAddressPersister(io.patamon.geocoding.core.impl.DefaultRegoinCache())
        // 实例化
        interpreter = io.patamon.geocoding.core.impl.DefaultAddressInterpreter()
        // 计算类
        computer = io.patamon.geocoding.core.impl.SimilarityComputer()
    }

    // 获取 AddressInterpreter
    fun getInterpreter(): io.patamon.geocoding.core.AddressInterpreter {
        interpreter ?: throw IllegalArgumentException("[Context] -> 地址解析服务类初始化失败.")
        return interpreter!!
    }

    // 获取 AddressPersister
    fun getPersister(): io.patamon.geocoding.core.AddressPersister {
        persister ?: throw IllegalArgumentException("[Context] -> 地址持久化服务类初始化失败.")
        return persister!!
    }

    // 获取 visitor
    fun getVisitor(): io.patamon.geocoding.core.TermIndexVisitor {
        return io.patamon.geocoding.core.impl.RegionInterpreterVisitor(getPersister())
    }

    // 获取 计算类
    fun getComputer(): io.patamon.geocoding.core.Computer {
        computer ?: throw IllegalArgumentException("[Context] -> 地址计算服务类初始化失败.")
        return computer!!
    }
}