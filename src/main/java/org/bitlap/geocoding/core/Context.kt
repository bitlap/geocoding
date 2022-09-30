package org.bitlap.geocoding.core

import org.bitlap.geocoding.core.impl.DefaultAddressInterpreter
import org.bitlap.geocoding.core.impl.DefaultAddressPersister
import org.bitlap.geocoding.core.impl.DefaultRegionCache
import org.bitlap.geocoding.core.impl.SimilarityComputer

/**
 * Desc: 上下文
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/1/12
 */
open class Context(
    val dataClassPath: String,
    val strict: Boolean,
    val persister: AddressPersister = DefaultAddressPersister(DefaultRegionCache(dataClassPath)),
    val interpreter: AddressInterpreter = DefaultAddressInterpreter(persister, strict),
    val computer: Computer = SimilarityComputer(),
) {



}
