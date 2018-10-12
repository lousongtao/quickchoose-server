CREATE DATABASE `digitalmenu`;

USE `digitalmenu`;

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hashed_password` varchar(40) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`),
  KEY `username_idx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission_id` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_k6j8r050y1kxdu3rjg4ji6a1y` (`permission_id`),
  KEY `FK_hsesj1sxjqummghhxb5ayo4os` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8;


INSERT INTO `digitalmenu`.`user` (`id`,`hashed_password`, `username`) VALUES ('1','D033E22AE348AEB5660FC2140AEC35850C4DA997', 'admin'); 

INSERT INTO `permission` VALUES (1,'for create/update/delete account; for change account\'s permission','CREATE_USER',2),(2,'add/update/delete menu(including category1/category2/dish/dishconfiggroup/dishconfig), set dish SOLDOUT/PROMOTION','EDIT_MENU',2),(3,'query order data','QUERY_ORDER',3),(4,'query account','QUERY_USER',4),(5,'change the configurations of system','CHANGE_CONFIG',3),(6,'query desk data','QUERY_DESK',4),(7,'create/update/delete desk data','EDIT_DESK',5),(8,'create/update/delete printer data','EDIT_PRINTER',8),(9,'create/update/delete the order, including add dish, cancel order, checkout order, etc','UPDATE_ORDER',9),(10,'add/update/delete the discount template','EDIT_DISCOUNTTEMPLATE',7),(11,'query shift work data','QUERY_SHIFTWORK',11),(12,'add/update/delete the payway for checkout order','EDIT_PAYWAY',6),(13,'statistics the income report','STATISTICS',13),(14,'raw material','RAWMATERIAL',14),(15,'query member data, including query the member\'s score log and balance log','QUERY_MEMBER',15),(16,'create/update member, not including update the member\'s score & balance','UPDATE_MEMBER',16),(17,'update member\'s score','UPDATE_MEMBERSCORE',17),(18,'update member\'s balance, including recharge','UPDATE_MEMBERBALANCE',18),(19,'update member\'s password','UPDATE_MEMBERPASSWORD',19),(20,'delete member','DELETE_MEMBER',20);


INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('1', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('2', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('3', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('4', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('5', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('6', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('7', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('8', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('9', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('10', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('11', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('12', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('13', '1');
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('14', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('15', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('16', '1'); 
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('17', '1');
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('18', '1');
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('19', '1');
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('20', '1'); 