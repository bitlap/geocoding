
[![Java 8 CI](https://github.com/IceMimosa/geocoding/actions/workflows/java8.yml/badge.svg)](https://github.com/IceMimosa/geocoding/actions/workflows/java8.yml)

# 介绍
项目目前采用的是 [淘宝物流4级地址](!https://lsp.wuliu.taobao.com/locationservice/addr/output_address_town.do)的标准地址库，即`classpath:src/main/resources/core/region.dat`中的数据，
本package下代码可将 [中国5级行政区域](!https://github.com/kakuilan/china_area_mysql) 处理为兼容geocoding的标准地址库。

### 使用步骤

1. 成功导入china_area_mysql到数据库
2. 执行本package下sql/china.sql插`中国`数据
3. 修改本package下util/JdbcUtil.java中的jdbc相关参数
4. 执行本package下Maine类中main方法
5. 将生成的dat文件改名为region.dat并放入`classpath:src/main/resources/core/`

### 注意事项
本测试配置基于Server version: 8.0.21 MySQL Community Server - GPL环境，其它可能略有差异，可通过下面两个SQL确认配置是否OK

```
show variables like '%CHARACTER%';
show variables like '%max_allowed_packet%';
```

1. 设置max_allowed_packet，[mysqld]下max_allowed_packet = 2000M，[mysqldump]下max_allowed_packet = 2000M
2. 设置字符集，[client]下default-character-set=utf8mb4，[mysqld]下character-set-server=utf8mb4和init_connect='SET NAMES utf8mb4'，[mysql]下default-character-set=utf8mb4
