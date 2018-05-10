package io.patamon.geocoding;

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
    fun normalizing(address: String): io.patamon.geocoding.model.Address? {
        return io.patamon.geocoding.model.Address.build(io.patamon.geocoding.core.Context.getInterpreter().interpret(address))
    }

    /**
     * 将地址进行切分
     */
    fun analyze(address: String): io.patamon.geocoding.similarity.Document? {
        val addr = normalizing(address) ?: return null
        return io.patamon.geocoding.core.Context.getComputer().analyze(addr)
    }
    fun analyze(address: io.patamon.geocoding.model.Address?): io.patamon.geocoding.similarity.Document? {
        address ?: return null
        return io.patamon.geocoding.core.Context.getComputer().analyze(address)
    }

    /**
     * 地址的相似度计算
     */
    fun similarity(addr1: String, addr2: String): Double {
        val compute = io.patamon.geocoding.core.Context.getComputer().compute(normalizing(addr1), normalizing(addr2))
        return compute.similarity
    }
    fun similarity(addr1: io.patamon.geocoding.model.Address?, addr2: io.patamon.geocoding.model.Address?): Double {
        val compute = io.patamon.geocoding.core.Context.getComputer().compute(addr1, addr2)
        return compute.similarity
    }

    /**
     * 地址相似度计算, 包含匹配的所有结果
     */
    fun similarityWithResult(addr1: String, addr2: String): io.patamon.geocoding.similarity.MatchedResult {
        return io.patamon.geocoding.core.Context.getComputer().compute(normalizing(addr1), normalizing(addr2))
    }
    fun similarityWithResult(addr1: io.patamon.geocoding.model.Address?, addr2: io.patamon.geocoding.model.Address?): io.patamon.geocoding.similarity.MatchedResult {
        return io.patamon.geocoding.core.Context.getComputer().compute(addr1, addr2)
    }

}
