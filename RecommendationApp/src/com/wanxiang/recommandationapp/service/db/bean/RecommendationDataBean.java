package com.wanxiang.recommandationapp.service.db.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "recommendation_info")
public class RecommendationDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7983795391960577831L;

	@DatabaseField(columnName = "id", generatedId = true)
	private int id;
	@DatabaseField(columnName = "recommandation_id", canBeNull = false)
	private long recommendationId;
	@DatabaseField(columnName = "entity", canBeNull = false)
	private String entity;
	@DatabaseField(columnName = "category")
	private String category;
	@DatabaseField(columnName = "date")
	private long date;
	@DatabaseField(columnName = "comment_num")
	private int commentNum;
	@DatabaseField(columnName = "praise_num")
	private int praiseNum;
	@DatabaseField(columnName = "user")
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getRecommendationId() {
		return recommendationId;
	}

	public void setRecommendationId(long recommendationId) {
		this.recommendationId = recommendationId;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}
}
