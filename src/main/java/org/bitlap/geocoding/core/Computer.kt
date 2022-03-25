package org.bitlap.geocoding.core

import org.bitlap.geocoding.model.Address
import org.bitlap.geocoding.similarity.Document
import org.bitlap.geocoding.similarity.MatchedResult

/**
 * Desc: 相似度算法相关逻辑
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/2/5
 */
interface Computer {

    /**
     * 将标准地址转化成文档对象
     * 1. 对text进行分词
     * 2. 对每个部分计算 IDF
     */
    fun analyze(address: Address): Document

    /**
     * 计算两个标准地址的相似度
     * 1. 将两个地址形成 Document
     * 2. 为每个Document的Term设置权重
     * 3. 计算两个分词组的余弦相似度
     */
    fun compute(addr1: Address?, addr2: Address?): MatchedResult
}