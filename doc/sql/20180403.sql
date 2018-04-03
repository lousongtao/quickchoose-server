ALTER TABLE `digitalmenu`.`discounttemplate` DROP COLUMN `value`;
ALTER TABLE `digitalmenu`.`discounttemplate` CHANGE COLUMN `rate` `value` DOUBLE NOT NULL ;
update digitalmenu.discounttemplate set type = 1 where id > 0;