package com.shuishou.digitalmenu.menu.views;

import java.util.List;

import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.views.GridResult;

public class GetMenuResult extends GridResult {

	
	
	public static class Category1Info{
		public boolean expanded = true; //for extjs
		public boolean loaded = false; // for extjs, For all non-leaf nodes that do not have children, the server response MUST set the loaded property to true. 
		
		public int objectid;
		public String chineseName;
		public String englishName;
		public int sequence;
//		public String iconCls = ConstantValue.CSS_MENUTREENODE_ICON_SIZE;
//		public String icon = "../" + ConstantValue.CATEGORY_DISHIMAGE_SMALL+"/catalog1.png";
		public String level = ConstantValue.TYPE_CATEGORY1INFO;
		public List<Category2Info> children;
		public String displayText;//this is for display in tree of maintain tool
		public Category1Info(int id, String chineseName,String englishName,int sequence,List<Category2Info> c2s){
			this.objectid = id;
			this.chineseName = chineseName;
			this.englishName = englishName;
			this.sequence = sequence;
			this.children = c2s;
			displayText = chineseName;
		}
	}

	public static class Category2Info{
		public int objectid;
		public String chineseName;
		public String englishName;
		public int printerId;
		public int sequence;
		public String level = ConstantValue.TYPE_CATEGORY2INFO;
		public List<DishInfo> children;
//		public String iconCls = ConstantValue.CSS_MENUTREENODE_ICON_SIZE;
//		public String icon = "../" + ConstantValue.CATEGORY_DISHIMAGE_SMALL+"/catalog2.png";
		public boolean expanded = true;
		public boolean loaded = false; // for extjs, For all non-leaf nodes that do not have children, the server response MUST set the loaded property to true.
		public int parentID;
		public String displayText;//this is for display in tree of maintain tool
		public Category2Info(int id, String chineseName,String englishName,int sequence, int parentID, List<DishInfo> dishes, int printerId){
			this.objectid = id;
			this.chineseName = chineseName;
			this.englishName = englishName;
			this.sequence = sequence;
			this.children = dishes;
			this.parentID = parentID;
			displayText = chineseName;
			this.printerId = printerId;
		}
	}
	
	/**
	 * @param id, 此处ID内容为Type+"-"+id. 为了适应前端Extjs在展开节点时, 会自动发送id的功能. 后面做子节点查询时, 重新拆分这个
	 * @author Administrator
	 *
	 */
	public static class DishInfo{
		public int objectid;
		public String chineseName;
		public String englishName;
		public int sequence;
		public boolean leaf = true;
//		public String iconCls = ConstantValue.CSS_MENUTREENODE_ICON_SIZE;
//		public String icon;
		public int parentID;
		public double price;
		public boolean isNew;
		public boolean isSpecial;
		public boolean isSoldOut;
		public int hotLevel;
		public String pictureName;
		public String displayText; //this is for display in tree of maintain tool
		public String level = ConstantValue.TYPE_DISHINFO;
//		public DishInfo(int id, String chineseName,String englishName,int sequence,double price, int parentID, boolean isNewProduct, boolean isSpecial, int hotLevel, String pictureName){
//			this.objectid = id;
//			this.chineseName = chineseName;
//			this.englishName = englishName;
//			this.sequence = sequence;
//			this.parentID = parentID;
//			this.price = price;
//			this.isNewProduct = isNewProduct;
//			this.isSpecial = isSpecial;
//			this.pictureName = pictureName;
//			this.hotLevel = hotLevel;
//		}
	}
	
	public List children;//这里必须定义成children, 否则前端读出来这部分不认做跟节点的children
	public GetMenuResult(String result, boolean success, List infos) {
		super(result, success);
		this.children = infos;
	}
	
//	public final class 

}
