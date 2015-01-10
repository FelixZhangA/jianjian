package com.wanxiang.recommandationapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

	private String name;
	private long id;

	private int friendsCount;
	private ArrayList<Category> childrenList;

	private boolean isRecent;

	public Category() {

	}

	public Category(String name) {
		this.name = name;
		this.id = System.currentTimeMillis();
	}

	public Category(String name, long id) {
		this.name = name;
		this.id = id;
	}

	public Category(String name, int color) {
		this.name = name;
		this.id = System.currentTimeMillis();
	}

	public String getCategoryName() {
		return name;
	}

	public void setCategoryName(String name) {
		this.name = name;
	}

	public long getCagetoryId() {
		return id;
	}

	public void setCategoryId(long id) {
		this.id = id;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public ArrayList<Category> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(ArrayList<Category> childrenList) {
		this.childrenList = childrenList;
	}

	public boolean isRecent() {
		return isRecent;
	}

	public void setRecent(boolean isRecent) {
		this.isRecent = isRecent;
	}
}
