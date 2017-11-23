--初始化admin用户
INSERT INTO `digitalmenu`.`user` (`id`,`hashed_password`, `username`) VALUES ('1','D033E22AE348AEB5660FC2140AEC35850C4DA997', 'admin');

--初始化permission
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
INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('11', 'EDIT_PAYWAY');




--初始化admin权限
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

INSERT INTO `digitalmenu`.`configs` (`name`, `value`) VALUES ('LANGUAGEAMOUNT', '2');
INSERT INTO `digitalmenu`.`configs` (`name`, `value`) VALUES ('FIRSTLANGUAGENAME', '中文');
INSERT INTO `digitalmenu`.`configs` (`name`, `value`) VALUES ('SECONDLANGUAGENAME', 'English');
