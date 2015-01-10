package com.wanxiang.recommandationapp.model;

import java.io.Serializable;

/*
 * user": {
 "id": 8,
 "name": "周游",
 "headImage": "http://121.42.48.249/upload/150105/VD9NoO60hcpw.jpg",
 "signature": "分享品质生活",
 "remark": null
 },*/
public class User implements Serializable {
	private long id;
	private String name;
	private String headImage;
	private String signature;
	private String remark;

	public User(String userName) {
		this.name = userName;
	}

	public User() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
