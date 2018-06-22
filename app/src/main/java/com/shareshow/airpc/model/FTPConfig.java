package com.shareshow.airpc.model;



public class FTPConfig {

	private String username;
	
	private String passwd;
	
	private String ip;
	
	private	int  port;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public FTPConfig() {
		super();
		this.username="xtxk";
		this.passwd="xtxk";
		this.ip="192.168.43.32";
		this.port=2121;
	}
	
	
}
