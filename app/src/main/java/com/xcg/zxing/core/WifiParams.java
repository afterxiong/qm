package com.xcg.zxing.core;
public class WifiParams {
	private String ssid;
	private String password;
	private int type;
	private String ip;

	public WifiParams() {
	}

	public WifiParams(String ssid, String password, int type, String ip) {
		this.ssid = ssid;
		this.password = password;
		this.type = type;
		this.ip = ip;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}



	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
