package com.shuishou.digitalmenu.management.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="shift_work")
public class ShiftWork {
	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name="user_name", nullable=false)
	private String userName;
	
	@Column
	private Date time;
	
	@Column
	private byte status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ShiftWork [id=" + id + ", userName=" + userName + ", time=" + time + ", status=" + status + "]";
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
		ShiftWork other = (ShiftWork) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
