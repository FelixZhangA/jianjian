package com.wanxiang.recommandationapp.controller;

/**
 * pageContext.xml 页面属性类
 * @author xianye
 *
 */
public class FusionPage implements java.io.Serializable{

	private static final long serialVersionUID = 4122048711699308920L;
	private String mName;
	private String mClazz;
	private String mParams;
	
	
	public FusionPage(String name,String clazz,String params){
		this.mName = name;
		this.mClazz = clazz;
		this.mParams = params;
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public String getClazz() {
		return mClazz;
	}
	public void setClazz(String clazz) {
		this.mClazz = clazz;
	}
	public String getParams() {
		return mParams;
	}
	public void setParams(String params) {
		this.mParams = params;
	}
	
}
