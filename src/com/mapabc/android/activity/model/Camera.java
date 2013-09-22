package com.mapabc.android.activity.model;

public class Camera {
	private String nodeid;
	private String gpsx;
	private String gpsy;
	private String flag;
	
	public Camera(){}
	public Camera(String nodeid,String gpsx,String gpsy,String flag){
		this.nodeid = nodeid;
		this.gpsx = gpsx;
		this.gpsy = gpsy;
		this.flag = flag;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getGpsx() {
		return gpsx;
	}
	public void setGpsx(String gpsx) {
		this.gpsx = gpsx;
	}
	public String getGpsy() {
		return gpsy;
	}
	public void setGpsy(String gpsy) {
		this.gpsy = gpsy;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
