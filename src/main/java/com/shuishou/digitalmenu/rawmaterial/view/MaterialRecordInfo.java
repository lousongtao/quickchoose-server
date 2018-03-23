package com.shuishou.digitalmenu.rawmaterial.view;

public class MaterialRecordInfo {

	public int materialId;
	public String materialName;
	public String categoryName;
	public String unit;
	public double price;
	public double totalPrice;
	public double purchaseAmount;
	public double consumeAmount;
	
	public MaterialRecordInfo(int materialId){
		this.materialId = materialId;
	}
	
	public MaterialRecordInfo(int materialId, String materialName, String categoryName, String unit, double price){
		this.materialId = materialId;
		this.materialName = materialName;
		this.categoryName = categoryName;
		this.unit = unit;
		this.price = price;
	}
	
	public MaterialRecordInfo(int materialId, double purchaseAmount, double consumeAmount){
		this.materialId = materialId;
		this.purchaseAmount = purchaseAmount;
		this.consumeAmount = consumeAmount;
	}
}
