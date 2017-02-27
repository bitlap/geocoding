DROP TABLE IF EXISTS `addr_address`;
CREATE TABLE `addr_address` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT COMMENT 'Address Record ID',
  `province` BIGINT(11) NOT NULL DEFAULT '0' COMMENT 'Province ID',
  `city` BIGINT(11) NOT NULL DEFAULT '0' COMMENT 'City ID',
  `district` BIGINT(11) NOT NULL DEFAULT '0' COMMENT 'District ID',
  `street` BIGINT(11) NOT NULL DEFAULT '0' COMMENT 'Street ID',
  `text` varchar(100) NOT NULL DEFAULT '' COMMENT 'Address Text',
  `town` varchar(20) NOT NULL DEFAULT '' COMMENT '镇',
  `village` varchar(5) NOT NULL DEFAULT '' COMMENT '村',
  `road` varchar(8) NOT NULL DEFAULT '' COMMENT '道路',
  `road_num` varchar(10) NOT NULL DEFAULT '' COMMENT '道路号码',
  `building_num` varchar(20) NOT NULL DEFAULT '' COMMENT '几号楼+几单元+房间号',
  `hash` int(11) NOT NULL DEFAULT '0' COMMENT 'Address Text Hash Code',
  `raw_text` varchar(150) NOT NULL DEFAULT '' COMMENT 'Original Address Text',
  `prop1` varchar(20) NOT NULL DEFAULT '' COMMENT '扩展字段：订单号',
  `prop2` varchar(20) NOT NULL DEFAULT '' COMMENT '扩展字段：片区ID',
  `create_time` date NOT NULL DEFAULT '1900-01-01',
  PRIMARY KEY (`id`),
  KEY `ix_hash` (`hash`),
  KEY `ix_pid_cid_did` (`province`,`city`,`district`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8;