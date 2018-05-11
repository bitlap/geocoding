# 介绍
本项目旨在将不规范(或者连续)的文本地址进行尽可能的**标准化**, 以及对两个地址进行**相似度的计算**。

地理编码技术, 主要分为如下步骤
 * 地址标准库
 * 地址标准化
 * 相似度计算

## pom
 
```xml
<dependencies>
    <dependency>
        <groupId>io.patamon.jesque</groupId>
        <artifactId>spring-boot-starter-jesque</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>patamon.release.repository</id>
        <name>github release repository</name>
        <url>https://raw.github.com/icemimosa/maven/release/</url>
    </repository>
</repositories>
```
 
# 1. 数据测试

方法调用: `Geocoding` 类
 * normalizing: 标准化
 * analyze: 解析成分词文档
 * similarity: 相似度计算
 * similarityWithResult: 相似度计算, 返回包含更多丰富的数据

## 1.1 标准化

```java
>> 输入: 山东青岛市北区山东省青岛市市北区水清沟街道九江路20号大都会3号楼2单元1303
>> 输出:
Address(
	provinceId=370000000000, province=山东省, 
	cityId=370200000000, city=青岛市, 
	districtId=370203000000, district=市北区, 
	streetId=370203030000, street=水清沟街道, 
	townId=null, town=null, 
	villageId=null, village=null, 
	road=九江路, 
	roadNum=20号, 
	buildingNum=3号楼2单元1303, 
	text=大都会
)
```

```java
>> 输入: 上海上海宝山区宝山区【新沪路58弄11-802  水韵华庭 】 (水韵华庭附近)
>> 输出: 
Address(
	provinceId=310000000000, province=上海, 
	cityId=310100000000, city=上海市, 
	districtId=310113000000, district=宝山区, 
	streetId=null, street=null, 
	townId=null, town=null, 
	villageId=null, village=null, 
	road=新沪路, 
	roadNum=58弄, 
	buildingNum=11-802, 
	text=水韵华庭水韵华庭附近
)
```

* 返回的对象解释
    * province相关: 省
    * city相关: 市
    * district相关: 区、县
    * street相关: 街道
    * town相关: 乡镇
    * village相关: 村
    * road: 道路
    * roadNum: 路号
    * buildingNum: 建筑物号
    * text: 标准化后为匹配的地址。一般包含小区, 商场名称等信息

# 1.2 相似度

```java
>> 输入:
  浙江金华义乌市南陈小区8幢2号
  浙江金华义乌市稠城街道浙江省义乌市宾王路99号后面南陈小区8栋2号
>> 输出: 
  0.8451542547285166
```

```java
>> 输入:
  山东省沂水县四十里堡镇东艾家庄村206号
  浙江金华义乌市南陈小区8幢2号
>> 输出:
  0.0
```

# 2. 说明

## 2.1 标准地址库
项目目前采用的是 [淘宝物流4级地址][1] 的标准地址库

也可以采用[国家的标准地址库][2] (对用的github库, [中国5级行政区域mysql库][3])


## 2.2 标准化
1. 首先基于正则提取出道路、建筑物号等信息
2. 省市区等匹配
    1. 将标准的地址库建立**倒排索引**
    2. 将文本从起始位置开始, 采用**最大长度优先**的方式匹配所有词条
    3. 对所有匹配结果进行标准行政区域从属关系校验

## 2.3 相似度计算
1. 对输入的两个地址进行标准化
2. 对省市区等信息分配不同的权重
3. 对道路号, 建筑号进行语义处理, 分配权重
4. 对剩余文本(text)使用**IK Analyzer**进行分词
5. 对两个结果集使用**余弦相似度算法**计算相似度


项目参考[address-semantic-search][4]，简化了流程，修复了各种不规则错误，使得使用更加方便。

[1]:https://lsp.wuliu.taobao.com/locationservice/addr/output_address_town.do
[2]:http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/index.html
[3]:https://github.com/kakuilan/china_area_mysql
[4]:https://github.com/liuzhibin-cn/address-semantic-search
