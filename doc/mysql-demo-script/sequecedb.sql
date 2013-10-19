/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50613
Source Host           : localhost:3306
Source Database       : sequecedb

Target Server Type    : MYSQL
Target Server Version : 50613
File Encoding         : 65001

Date: 2013-10-19 14:51:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `log`
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` int(11) DEFAULT '0',
  `name` varchar(64) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of log
-- ----------------------------
INSERT INTO `log` VALUES ('10', '1378879658', 'result', '27:191,1');
INSERT INTO `log` VALUES ('11', '1378879668', 'result', '6:192,1');
INSERT INTO `log` VALUES ('12', '1378879768', 'result', '27:193,1');
INSERT INTO `log` VALUES ('13', '1378880158', 'result', '6:194,1');
INSERT INTO `log` VALUES ('14', '1378880163', 'result', '27:195,1');
INSERT INTO `log` VALUES ('15', '1378880335', 'result', '6:196,1');
INSERT INTO `log` VALUES ('16', '1378880338', 'result', '27:197,1');
INSERT INTO `log` VALUES ('17', '1378880447', 'result', '6:198,1');
INSERT INTO `log` VALUES ('18', '1378880454', 'result', '27:199,1');
INSERT INTO `log` VALUES ('19', '1378894581', 'result', '47:200,1');
INSERT INTO `log` VALUES ('20', '1378894601', 'result', '47:201,1');
INSERT INTO `log` VALUES ('21', '1379138270', 'result', '12:202,1');
INSERT INTO `log` VALUES ('22', '1379138299', 'result', '12:203,1');

-- ----------------------------
-- Table structure for `unisequence`
-- ----------------------------
DROP TABLE IF EXISTS `unisequence`;
CREATE TABLE `unisequence` (
  `name` varchar(200) NOT NULL,
  `current_value` bigint(20) unsigned NOT NULL DEFAULT '0',
  `increment` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of unisequence
-- ----------------------------
INSERT INTO `unisequence` VALUES ('test', '22372', '1');

-- ----------------------------
-- Function structure for `currval`
-- ----------------------------
DROP FUNCTION IF EXISTS `currval`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `currval`(seq_name VARCHAR(100)) RETURNS varchar(100) CHARSET utf8
BEGIN   
         DECLARE value VARCHAR(100);
         SELECT CONCAT(current_value,',',increment) INTO value
         FROM unisequence
         WHERE name= seq_name; 
         RETURN value;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `nextval`
-- ----------------------------
DROP FUNCTION IF EXISTS `nextval`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `nextval`(seq_name varchar(100) , step int) RETURNS varchar(100) CHARSET utf8
BEGIN
  	DECLARE result VARCHAR(100);
DECLARE badone VARCHAR(100);
if step >=1 then

       SELECT CONCAT(current_value + step,',',step ) INTO result  FROM unisequence   WHERE name = seq_name for update;
#        insert into log(timestamp,name,value) values(UNIX_TIMESTAMP(),'result',CONCAT(CONNECTION_ID(),':',result));
#Eselect sleep(20) into badone; 
       UPDATE unisequence    SET current_value = current_value + step   WHERE name = seq_name;
else
         #select sleep(20) into badone;  

          SELECT CONCAT(current_value + increment,',',increment) INTO result  FROM unisequence   WHERE name = seq_name for update;
          
					 



          UPDATE unisequence    SET current_value = current_value + increment   WHERE name = seq_name;

end if;

return result ;
END
;;
DELIMITER ;
