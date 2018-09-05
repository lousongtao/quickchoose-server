package com.shuishou.digitalmenu.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GenerateLicenseKey {
	/**
	 * 生成注册公司的key值;
	 * key的取值规则是, customerName+"JS-Link", eg: haoszechuanJS-Link, fasteddyJS-Link
	 * @param args
	 */
	public static void main(String[] args){
		String customerName = "judian";
		System.out.println(customerName);
		byte[] data = (customerName + "JS-Link").getBytes(); 
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		byte[] digest = md.digest(data);
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%1$02X", b));
		}

		System.out.println(sb.toString());
	}
}
