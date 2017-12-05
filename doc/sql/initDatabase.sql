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

INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('1', 'CREATE_USER'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('2', 'EDIT_MENU'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('3', 'QUERY_ORDER'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('4', 'QUERY_USER'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('5', 'CHANGE_CONFIG'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('6', 'QUERY_DESK'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('7', 'EDIT_DESK'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('8', 'EDIT_PRINTER'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('9', 'UPDATE_ORDER'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('10', 'EDIT_DISCOUNTTEMPLATE'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('11', 'QUERY_SHIFTWORK'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('12', 'EDIT_PAYWAY'); 
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('13', 'STATISTICS');
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('14', 'RAWMATERIAL');

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