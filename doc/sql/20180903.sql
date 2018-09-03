INSERT INTO `digitalmenu`.`permission` (`id`, `name`) VALUES ('20', 'DELETE_MEMBER');
INSERT INTO `digitalmenu`.`user_permission` (`permission_id`, `user_id`) VALUES ('20', '1'); 

ALTER TABLE `digitalmenu`.`permission` 
CHANGE COLUMN `name` `name` VARCHAR(45) NOT NULL ,
ADD COLUMN `description` VARCHAR(400) NULL AFTER `name`;

ALTER TABLE `digitalmenu`.`permission` 
ADD COLUMN `sequence` INT NOT NULL DEFAULT 1 AFTER `description`;

UPDATE `digitalmenu`.`permission` SET `description`='for create/update/delete account; for change account\'s permission', `sequence`='2' WHERE `id`='1';
UPDATE `digitalmenu`.`permission` SET `description`='add/update/delete menu(including category1/category2/dish/dishconfiggroup/dishconfig), set dish SOLDOUT/PROMOTION' WHERE `id`='2';
UPDATE `digitalmenu`.`permission` SET `description`='query order data' WHERE `id`='3';
UPDATE `digitalmenu`.`permission` SET `description`='query account' WHERE `id`='4';
UPDATE `digitalmenu`.`permission` SET `description`='change the configurations of system', `sequence`='3' WHERE `id`='5';
UPDATE `digitalmenu`.`permission` SET `description`='query desk data', `sequence`='4' WHERE `id`='6';
UPDATE `digitalmenu`.`permission` SET `description`='create/update/delete desk data', `sequence`='5' WHERE `id`='7';
UPDATE `digitalmenu`.`permission` SET `description`='create/update/delete printer data', `sequence`='8' WHERE `id`='8';
UPDATE `digitalmenu`.`permission` SET `description`='create/update/delete the order, including add dish, cancel order, checkout order, etc' WHERE `id`='9';
UPDATE `digitalmenu`.`permission` SET `description`='add/update/delete the discount template', `sequence`='7' WHERE `id`='10';
UPDATE `digitalmenu`.`permission` SET `description`='query shift work data' WHERE `id`='11';
UPDATE `digitalmenu`.`permission` SET `description`='add/update/delete the payway for checkout order', `sequence`='6' WHERE `id`='12';
UPDATE `digitalmenu`.`permission` SET `description`='statistics the income report' WHERE `id`='13';
UPDATE `digitalmenu`.`permission` SET `description`='raw material' WHERE `id`='14';
UPDATE `digitalmenu`.`permission` SET `description`='query member data, including query the member\'s score log and balance log' WHERE `id`='15';
UPDATE `digitalmenu`.`permission` SET `description`='create/update member, not including update the member\'s score & balance' WHERE `id`='16';
UPDATE `digitalmenu`.`permission` SET `description`='update member\'s score' WHERE `id`='17';
UPDATE `digitalmenu`.`permission` SET `description`='update member\'s balance, including recharge' WHERE `id`='18';
UPDATE `digitalmenu`.`permission` SET `description`='update member\'s password' WHERE `id`='19';
UPDATE `digitalmenu`.`permission` SET `description`='delete member' WHERE `id`='20';
UPDATE `digitalmenu`.`permission` SET `sequence`='10' WHERE `id`='2';
UPDATE `digitalmenu`.`permission` SET `sequence`='20' WHERE `id`='3';
UPDATE `digitalmenu`.`permission` SET `sequence`='21' WHERE `id`='9';
UPDATE `digitalmenu`.`permission` SET `sequence`='50' WHERE `id`='11';
UPDATE `digitalmenu`.`permission` SET `sequence`='51' WHERE `id`='13';
UPDATE `digitalmenu`.`permission` SET `sequence`='12' WHERE `id`='14';
UPDATE `digitalmenu`.`permission` SET `sequence`='40' WHERE `id`='15';
UPDATE `digitalmenu`.`permission` SET `sequence`='41' WHERE `id`='16';
UPDATE `digitalmenu`.`permission` SET `sequence`='42' WHERE `id`='17';
UPDATE `digitalmenu`.`permission` SET `sequence`='43' WHERE `id`='18';
UPDATE `digitalmenu`.`permission` SET `sequence`='44' WHERE `id`='19';
UPDATE `digitalmenu`.`permission` SET `sequence`='45' WHERE `id`='20';
