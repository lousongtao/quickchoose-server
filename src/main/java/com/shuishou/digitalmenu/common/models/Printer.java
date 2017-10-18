package com.shuishou.digitalmenu.common.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.shuishou.digitalmenu.common.ConstantValue;

@Entity
@Table
public class Printer {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "printer_name", nullable = false)
	private String printerName;
	
	@Column(name = "copy", nullable = false)
	private int copy;
	
	/**
	 * distinct printers as counter, kitchen
	 * 
	 */
	@Column(name = "type")
	private int type = ConstantValue.PRINTER_TYPE_COUNTER;
	
	@Column(name = "print_style", nullable = false)
	private byte printStyle; //0: 连续打印, 1: 单菜打印
	
	public Printer(){}
	
	public Printer(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public int getCopy() {
		return copy;
	}

	public void setCopy(int copy) {
		this.copy = copy;
	}


	public byte getPrintStyle() {
		return printStyle;
	}

	public void setPrintStyle(byte printStyle) {
		this.printStyle = printStyle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Printer [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Printer other = (Printer) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
