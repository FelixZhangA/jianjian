package com.wanxiang.recommandationapp.service.db.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "category_info")
public class CategoryDataBean implements Serializable {

	@DatabaseField(columnName = "id", generatedId = true)
	private long id;
	@DatabaseField(columnName = "category_id", canBeNull = false)
	private long categoryId;
	@DatabaseField(columnName = "category_name", canBeNull = false)
	private String categoryName;
	@DatabaseField(columnName = "favorite")
	private boolean favorite;
	@DatabaseField(columnName = "children")
	private String childrenList;

	@DatabaseField(columnName = "recent")
	private boolean recent;

	public boolean isRecent() {
		return recent;
	}

	public void setRecent(boolean recent) {
		this.recent = recent;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public String getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(String childrenList) {
		this.childrenList = childrenList;
	}

}
