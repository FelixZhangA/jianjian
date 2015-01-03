package com.wanxiang.recommandationapp.model;

import java.io.Serializable;

public class Entity implements Serializable {
	private String name;
	private long id;

	public Entity(String name) {
		this.setEntityName(name);
	}

	public String getEntityName() {
		return name;
	}

	public void setEntityName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
