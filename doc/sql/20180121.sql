--设置所有菜为"非促销"模式
update digitalmenu.dish set isPromotion = 0, originPrice = 0 where id > 0;
