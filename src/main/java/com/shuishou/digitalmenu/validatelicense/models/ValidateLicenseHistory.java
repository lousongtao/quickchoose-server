package com.shuishou.digitalmenu.validatelicense.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuishou.digitalmenu.ConstantValue;

@Entity
@Table(name="validate_license_history")
public class ValidateLicenseHistory {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
	@Column(nullable = false)
	private Date validateDate;

	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
	@Column
	private Date expireDate;

	/**
	 * 可能的失败原因有
	 * 网络连接失败
	 * key跟customerName对应不上
	 * 账户过期
	 */
	@Column
	private String failureReason;
	
	/**
	 * 0 = success
	 * 1 = failed
	 */
	@Column(nullable = false)
	private int status;
	
	/**
	 * 连续失败次数, 
	 * 当上一次连接是成功时, 本次失败标记为1;
	 * 当上一次连接为失败时, 本次失败标记增1;
	 */
	@Column(nullable = false)
	private int failureTimes = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getValidateDate() {
		return validateDate;
	}

	public void setValidateDate(Date validateDate) {
		this.validateDate = validateDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getFailureTimes() {
		return failureTimes;
	}

	public void setFailureTimes(int failureTimes) {
		this.failureTimes = failureTimes;
	}

	@Override
	public String toString() {
		return "ValidateLicenseHistory [id=" + id + ", validateDate=" + validateDate + ", expireDate=" + expireDate
				+ ", failureReason=" + failureReason + ", status=" + status + ", failureTimes=" + failureTimes + "]";
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
		ValidateLicenseHistory other = (ValidateLicenseHistory) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
