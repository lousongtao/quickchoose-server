package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuishou.digitalmenu.ConstantValue;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Dish implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "first_language_name", nullable = false)
	private String firstLanguageName;
	
	@Column(name = "second_language_name")
	private String secondLanguageName;
	
	@Column(nullable = false)
	private int sequence;
	
//	@JsonBackReference
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "category2_id")
	private Category2 category2;
	
	@Column(nullable = false, precision = 8, scale = 2)
	private double price;
	
	@Column
	private String pictureName;
	
	@JsonProperty("isNew")
	@Column
	private boolean isNew = false;
	
	@JsonProperty("isSpecial")
	@Column
	private boolean isSpecial = false;
	
	
	@Column
	private int hotLevel = 0;
	
	//spring mvc can change "isSoldOut" to "soldOut" automatically, so must use JsonProperty
	@JsonProperty("isSoldOut")
	@Column
	private boolean isSoldOut = false;
	
	//used for quick search on Android 
	@Column
	private String abbreviation;
	
	@JsonProperty("isPromotion")
	@Column
	private boolean isPromotion = false;
	
	@Column
	private double originPrice;
	
	/**
	 * 点菜时动作     1.	默认值, 直接点菜     2.	强制选择特定子类         3.	提示信息后点菜       4.	提示信息后不点菜, 即只提示信息
	 */
	@Column(name="choose_mode", nullable = false)
	private int chooseMode = ConstantValue.DISH_CHOOSEMODE_DEFAULT;
	
	@OneToOne(cascade=CascadeType.ALL, mappedBy="dish", orphanRemoval = true)
	private DishChoosePopinfo choosePopInfo;
	
	//set whether merge to one record while customer choose this dish more than one time
	@Column(name="automerge_whilechoose")
	private boolean autoMergeWhileChoose = true;
	
	//是否支持配置口味
	@Column
	private boolean allowFlavor = true;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="dish_dishconfiggroup", joinColumns = {@JoinColumn(name="dish_id")}, 
		inverseJoinColumns = {@JoinColumn(name="dishconfiggroup_id")},
		uniqueConstraints = {@UniqueConstraint(columnNames = {"dish_id", "dishconfiggroup_id"})})
	private List<DishConfigGroup> configGroups = new ArrayList<>();
	
	
	/**
	 * 购买方式, 普通按照份数购买, 对于麻辣烫等, 需要按照重量购买
	 */
	@Column
	private int purchaseType = ConstantValue.DISH_PURCHASETYPE_UNIT;
	
	@Column
	private String description_1stlang;
	
	@Column
	private String description_2ndlang;
	
	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, mappedBy="dish")
	private List<DishMaterialConsume> materialConsumes;
	
	

	public List<DishMaterialConsume> getMaterialConsumes() {
		return materialConsumes;
	}

	public void setMaterialConsumes(List<DishMaterialConsume> materialConsumes) {
		this.materialConsumes = materialConsumes;
	}

	public void setConfigGroups(List<DishConfigGroup> configGroups) {
		this.configGroups = configGroups;
	}

	public List<DishConfigGroup> getConfigGroups() {
		return configGroups;
	}

	public void setConfigGroups(ArrayList<DishConfigGroup> configGroups) {
		this.configGroups = configGroups;
	}

	public void addConfigGroup(DishConfigGroup configGroup){
		if (configGroups == null)
			configGroups = new ArrayList<>();
		configGroups.add(configGroup);
	}
	
	public String getDescription_1stlang() {
		return description_1stlang;
	}

	public void setDescription_1stlang(String description_1stlang) {
		this.description_1stlang = description_1stlang;
	}

	public String getDescription_2ndlang() {
		return description_2ndlang;
	}

	public void setDescription_2ndlang(String description_2ndlang) {
		this.description_2ndlang = description_2ndlang;
	}

	public boolean isPromotion() {
		return isPromotion;
	}

	public void setPromotion(boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public double getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(double originPrice) {
		this.originPrice = originPrice;
	}

	public int getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(int purchaseType) {
		this.purchaseType = purchaseType;
	}

	public boolean isAutoMergeWhileChoose() {
		return autoMergeWhileChoose;
	}

	public void setAutoMergeWhileChoose(boolean autoMergeWhileChoose) {
		this.autoMergeWhileChoose = autoMergeWhileChoose;
	}

	public DishChoosePopinfo getChoosePopInfo() {
		return choosePopInfo;
	}

	public void setChoosePopInfo(DishChoosePopinfo choosePopInfo) {
		this.choosePopInfo = choosePopInfo;
	}

	public int getChooseMode() {
		return chooseMode;
	}

	public void setChooseMode(int chooseMode) {
		this.chooseMode = chooseMode;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public boolean isSoldOut() {
		return isSoldOut;
	}

	public void setSoldOut(boolean isSoldOut) {
		this.isSoldOut = isSoldOut;
	}

	public int getHotLevel() {
		return hotLevel;
	}

	public void setHotLevel(int hotLevel) {
		this.hotLevel = hotLevel;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Category2 getCategory2() {
		return category2;
	}

	public void setCategory2(Category2 category2) {
		this.category2 = category2;
	}
	
	

	public boolean isAllowFlavor() {
		return allowFlavor;
	}

	public void setAllowFlavor(boolean allowFlavor) {
		this.allowFlavor = allowFlavor;
	}


	public String getFirstLanguageName() {
		return firstLanguageName;
	}

	public void setFirstLanguageName(String firstLanguageName) {
		this.firstLanguageName = firstLanguageName;
	}

	public String getSecondLanguageName() {
		return secondLanguageName;
	}

	public void setSecondLanguageName(String secondLanguageName) {
		this.secondLanguageName = secondLanguageName;
	}

	@Override
	public String toString() {
		return "Dish [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dish other = (Dish) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
