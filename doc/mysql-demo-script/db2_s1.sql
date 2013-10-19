/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50613
Source Host           : localhost:3306
Source Database       : db2_s1

Target Server Type    : MYSQL
Target Server Version : 50613
File Encoding         : 65001

Date: 2013-10-19 14:50:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `company`
-- ----------------------------
DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `sharding_id` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_test` (`sharding_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of company
-- ----------------------------
INSERT INTO `company` VALUES ('10010', '1', 'db2_s1', '联通', '2');
INSERT INTO `company` VALUES ('10010', '2', 'db2_s1', '联通1', '3');
INSERT INTO `company` VALUES ('10010', '4', 'db2_s1', '电信', '5');
