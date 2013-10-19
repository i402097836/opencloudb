/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50613
Source Host           : localhost:3306
Source Database       : db1_s2

Target Server Type    : MYSQL
Target Server Version : 50613
File Encoding         : 65001

Date: 2013-10-19 14:50:20
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
INSERT INTO `company` VALUES ('10000', '1', 'db1_s2', '电信1', '1');
INSERT INTO `company` VALUES ('10000', '2', 'db1_s2', '电信', '4');
INSERT INTO `company` VALUES ('10000', '11', 'db1_s2', '??4', '5');

-- ----------------------------
-- Table structure for `tasklog`
-- ----------------------------
DROP TABLE IF EXISTS `tasklog`;
CREATE TABLE `tasklog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `classname` varchar(50) NOT NULL,
  `task` varchar(50) NOT NULL,
  `calltime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of tasklog
-- ----------------------------
INSERT INTO `tasklog` VALUES ('1', 'classname', 'task', '2013-08-15 15:48:43');
