package com.shuishou.digitalmenu;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class ConstantValue {
	public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DFWEEK = new SimpleDateFormat("EEE");
	public static final DateFormat DFYMDHMS_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";
	public static final String DATE_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_DOUBLE = "%.2f";
	
	public static final String PERMISSION_QUERY_USER = "QUERY_USER";
	public static final String PERMISSION_CREATE_USER = "CREATE_USER";
	public static final String PERMISSION_EDIT_MENU = "EDIT_MENU";
	public static final String PERMISSION_QUERY_ORDER = "QUERY_ORDER";
	public static final String PERMISSION_UPDATE_ORDER = "UPDATE_ORDER";
	public static final String PERMISSION_CHANGE_CONFIG = "CHANGE_CONFIG";
	public static final String PERMISSION_QUERY_DESK = "QUERY_DESK";
	public static final String PERMISSION_EDIT_DESK = "EDIT_DESK";
	public static final String PERMISSION_EDIT_PRINTER = "EDIT_PRINTER";
	public static final String PERMISSION_EDIT_DISCOUNTTEMPLATE = "EDIT_DISCOUNTTEMPLATE";
	public static final String PERMISSION_QUERY_SHIFTWORK = "QUERY_SHIFTWORK";
	public static final String PERMISSION_EDIT_PAYWAY = "EDIT_PAYWAY";
	public static final String PERMISSION_STATISTICS = "STATISTICS";
	public static final String PERMISSION_RAWMATERIAL = "RAWMATERIAL";
	public static final String PERMISSION_QUERY_MEMBER = "QUERY_MEMBER";
	public static final String PERMISSION_DELETE_MEMBER = "DELETE_MEMBER";
	public static final String PERMISSION_UPDATE_MEMBER = "UPDATE_MEMBER";
	public static final String PERMISSION_UPDATE_MEMBERSCORE = "UPDATE_MEMBERSCORE";
	public static final String PERMISSION_UPDATE_MEMBERBALANCE = "UPDATE_MEMBERBALANCE";
	public static final String PERMISSION_UPDATE_MEMBERPASSWORD = "UPDATE_MEMBERPASSWORD";
	public static final String PERMISSION_UPDATE_PACKAGEBIND = "UPDATE_PACKAGEBIND";
	
	public final static String SPLITTAG_PERMISSION = ";";
	
	public static final String TYPE_CATEGORY1INFO = "C1";
	public static final String TYPE_CATEGORY2INFO = "C2";
	public static final String TYPE_DISHINFO = "DISH";
	
	public static final String CATEGORY_ERRORLOG = "errorlog";
	public static final String CATEGORY_UPGRADEAPK = "upgradeApk";
	public static final String CATEGORY_BACKUPDB = "backupDB";
	public static final String CATEGORY_DISHIMAGE_BIG = "dishimage_big";
	public static final String CATEGORY_DISHIMAGE_MIDDLE = "dishimage_middle";
	public static final String CATEGORY_DISHIMAGE_SMALL = "dishimage_small";
	public static final String CATEGORY_DISHIMAGE_ORIGIN = "dishimage_origin";
	public static final String CATEGORY_PRINTTEMPLATE = "printtemplate";
	
	public static final int DISHIMAGE_WIDTH_SMALL = 120;
	public static final int DISHIMAGE_HEIGHT_SMALL = 120;
	public static final int DISHIMAGE_WIDTH_BIG = 240;
	public static final int DISHIMAGE_HEIGHT_BIG = 240;
	public static final int DISHIMAGE_WIDTH_ORIGIN = 540;
	public static final int DISHIMAGE_HEIGHT_ORIGIN = 540;
	
	public static final String CSS_MENUTREENODE_ICON_SIZE = "menutreenode-icon-size";
	
	public static final byte INDENT_STATUS_OPEN = 1;
	public static final byte INDENT_STATUS_CLOSED = 2;
	public static final byte INDENT_STATUS_PAID = 3;
	public static final byte INDENT_STATUS_CANCELED = 4;
	public static final byte INDENT_STATUS_FORCEEND = 5;//强制清台
	public static final byte INDENT_STATUS_REFUND = 6;
	
	public static final byte INDENT_OPERATIONTYPE_ADD = 1;
//	public static final byte INDENT_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENT_OPERATIONTYPE_CANCEL = 3;
	public static final byte INDENT_OPERATIONTYPE_PAY = 4;
	
	//付款方式
	public static final String INDENT_PAYWAY_CASH = "cash";//现金
	public static final String INDENT_PAYWAY_BANKCARD = "bankcard";//刷卡
	public static final String INDENT_PAYWAY_MEMBER = "member";//会员
	
//	public static final byte INDENTDETAIL_OPERATIONTYPE_ADD = 1;//deprecated
	public static final byte INDENTDETAIL_OPERATIONTYPE_DELETE = 2;
//	public static final byte INDENTDETAIL_OPERATIONTYPE_ADDAMOUNT = 3;//no use any more
//	public static final byte INDENTDETAIL_OPERATIONTYPE_MINUSAMOUNT = 4;// no use any more
	public static final byte INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT = 5;
	public static final byte INDENTDETAIL_OPERATIONTYPE_REFUND = 6;
	
	public static final byte MENUCHANGE_TYPE_DISHSOLDOUT = 0;//设置soldout或取消soldout
	public static final byte MENUCHANGE_TYPE_CHANGEPROMOTION = 1;//设置promotion或取消promotion
	public static final byte MENUCHANGE_TYPE_DISHCONFIGSOLDOUT = 2;//设置dishconfig的soldout或取消soldout
	public static final byte MENUCHANGE_TYPE_DISHADD = 3; //增加菜品
	public static final byte MENUCHANGE_TYPE_DISHUPDATE = 4;//修改菜品属性
	public static final byte MENUCHANGE_TYPE_DISHPICTURE = 5;//修改菜品图片
	public static final byte MENUCHANGE_TYPE_DISHDELETE = 6;//delete菜品
	public static final byte MENUCHANGE_TYPE_CATEGORY1ADD = 7;
	public static final byte MENUCHANGE_TYPE_CATEGORY1UPDATE = 8;
	public static final byte MENUCHANGE_TYPE_CATEGORY1DELETE = 9;
	public static final byte MENUCHANGE_TYPE_CATEGORY2ADD = 10;
	public static final byte MENUCHANGE_TYPE_CATEGORY2UPDATE = 11;
	public static final byte MENUCHANGE_TYPE_CATEGORY2DELETE = 12;
	public static final byte MENUCHANGE_TYPE_DISHCONFIGGROUPADD = 13;
	public static final byte MENUCHANGE_TYPE_DISHCONFIGGROUPUPDATE = 14;
	public static final byte MENUCHANGE_TYPE_DISHCONFIGGROUPDELETE = 15;
	public static final byte MENUCHANGE_TYPE_DISHCONFIGADD = 16;
	public static final byte MENUCHANGE_TYPE_DISHCONFIGUPDATE = 17;
	public static final byte MENUCHANGE_TYPE_DISHCONFIGDELETE = 18;
	public static final byte MENUCHANGE_TYPE_DISHMOVEINCONFIGGROUP = 19;
	public static final byte MENUCHANGE_TYPE_DISHMOVEOUTCONFIGGROUP = 20;
	
	
	public static final byte PRINT_STYLE_TOGETHER = 0;
	public static final byte PRINT_STYLE_SEPARATELY = 1;
	public static final byte PRINTER_TYPE_COUNTER = 1;
	public static final byte PRINTER_TYPE_KITCHEN = 2;
//	public static final byte SHIFTWORK_ONWORK = 0;
//	public static final byte SHIFTWORK_OFFWORK = 1;
	
	public static final byte DISH_CHOOSEMODE_DEFAULT = 1;
//	public static final byte DISH_CHOOSEMODE_SUBITEM = 2;
	public static final byte DISH_CHOOSEMODE_POPINFOCHOOSE = 3;
	public static final byte DISH_CHOOSEMODE_POPINFOQUIT = 4;
	
	public static final byte DISH_PURCHASETYPE_UNIT = 1;
	public static final byte DISH_PURCHASETYPE_WEIGHT = 2;
	
	public static final String CONFIGS_CANCELORDERCODE = "CANCELORDERCODE";
	public static final String CONFIGS_CLEARTABLECODE = "CLEARTABLECODE";
	public static final String CONFIGS_CONFIRMCODE = "CONFIRMCODE";
	public static final String CONFIGS_OPENCASHDRAWERCODE = "OPENCASHDRAWERCODE";
	public static final String CONFIGS_LANGUAGEAMOUNT = "LANGUAGEAMOUNT";
	public static final String CONFIGS_FIRSTLANGUAGENAME= "FIRSTLANGUAGENAME";
	public static final String CONFIGS_FIRSTLANGUAGEABBR = "FIRSTLANGUAGEABBR";
	public static final String CONFIGS_SECONDLANGUAGENAME= "SECONDLANGUAGENAME";
	public static final String CONFIGS_SECONDLANGUAGEABBR = "SECONDLANGUAGEABBR";
	public static final String CONFIGS_PRINT2NDLANGUAGENAME= "PRINT2NDLANGUAGENAME";
	public static final String CONFIGS_MEMBERMGR_NEEDPASSWORD = "MEMBERMGR_NEEDPASSWORD";
	public static final String CONFIGS_BRANCHNAME= "BRANCHNAME";
	public static final String CONFIGS_MEMBERMGR_BYSCORE= "MEMBERMGR_BYSCORE";
	public static final String CONFIGS_MEMBERMGR_BYDEPOSIT = "MEMBERMGR_BYDEPOSIT";
	public static final String CONFIGS_MEMBERMGR_SCOREPERDOLLAR = "MEMBERMGR_SCOREPERDOLLAR";
	public static final String CONFIGS_PRINTTICKET = "PRINTTICKET";
	public static final String CONFIGS_PRINTTICKET_AFTERMAKEORDER = "PRINTTICKET_AFTERMAKEORDER";
	public static final String CONFIGS_PRINTTICKET_AFTERPAY = "PRINTTICKET_AFTERPAY";
	
	public static final int STATISTICS_DIMENSTION_PAYWAY = 1;
	public static final int STATISTICS_DIMENSTION_SELL = 2;
	public static final int STATISTICS_DIMENSTION_PERIODSELL = 3;
	
	public static final int STATISTICS_SELLGRANULARITY_BYDISH = 1;
	public static final int STATISTICS_SELLGRANULARITY_BYCATEGORY2 = 2;
	public static final int STATISTICS_SELLGRANULARITY_BYCATEGORY1 = 3;
	
	public static final int STATISTICS_PERIODSELL_PERDAY = 1;
	public static final int STATISTICS_PERIODSELL_PERHOUR = 2;
	public static final int STATISTICS_PERIODSELL_PERWEEK = 3;
	public static final int STATISTICS_PERIODSELL_PERMONTH = 4;
	
	public static final int MATERIAL_ALARMSTATUS_NOALARM = 1;
	public static final int MATERIAL_ALARMSTATUS_ALARMACCEPTED = 2;
	public static final int MATERIAL_ALARMSTATUS_ALARMDELAY = 3;
	
	public static final int MATERIALRECORD_TYPE_PURCHASE = 1;//采购入库
	public static final int MATERIALRECORD_TYPE_CHANGEAMOUNT = 2;//操作员手动调整库存
	public static final int MATERIALRECORD_TYPE_SELLDISH = 3;//卖菜自动减库存
	
	public static final int MEMBERSCORE_CONSUM = 1;//积分类型-消费
	public static final int MEMBERSCORE_REFUND = 2;//积分类型-退货
	public static final int MEMBERSCORE_ADJUST = 3;//积分类型-调整
	public static final int MEMBERDEPOSIT_CONSUM = 1;//消费余额类型-消费
	public static final int MEMBERDEPOSIT_REFUND = 2;//消费余额类型-退款
	public static final int MEMBERDEPOSIT_RECHARGE = 3;//消费余额类型-充值
	public static final int MEMBERDEPOSIT_ADJUST = 4;//消费余额类型-调整
	
	public static final int VALIDATELICENSE_STATUS_SECCESS = 0;
	public static final int VALIDATELICENSE_STATUS_FAILED = 1;
	
	public static final int VALIDATELICENSE_EXPIREDAYS = 15; //过期天数, 超过这个天数后禁止使用系统
	public static final int VALIDATELICENSE_FAILEDTIMES = 15; //验证失败次数, 超过这个次数后禁止使用系统
	
	
	
}
