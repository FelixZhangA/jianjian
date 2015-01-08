package com.wanxiang.recommandationapp.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.model.User;
import com.wanxiang.recommandationapp.util.AppConstants;

/*
 [user_id] => 4
 [category_id] => 10008
 [entity_id] => 97
 [description] => 儿子特别喜欢看
 [praise_count] => 0
 [comment_count] => 10
 [user_name] => 吕威
 [category_name] => 图书
 [entity_name] => 数学大战
 [praises] => Array
 (
 )

 [comments] => Array
 (
 [0] => Array
 (
 [id] => 56
 [tuijian_id] => 168
 [user_id] => 3
 [replyed_user_id] => 0
 [comment] => 儿子每天回来都看
 [update_time] => 1417976164
 [user_name] => 邹剑
 )*/
public class RecommendationDetail implements Serializable {

	private long userId;
	private long categoryId;
	private long entityId;
	private String description;
	private int phraiseCount;
	private int commentCount;
	private User user;
	private String categoryName;
	private String entityName;
	private ArrayList<String> praiseList;
	private ArrayList<Comment> commentList;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entity_id) {
		this.entityId = entity_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPhraiseCount() {
		return phraiseCount;
	}

	public void setPhraiseCount(int phraiseCount) {
		this.phraiseCount = phraiseCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public ArrayList<String> getPraiseList() {
		return praiseList;
	}

	public void setPraiseList(ArrayList<String> praiseList) {
		this.praiseList = praiseList;
	}

	public ArrayList<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(ArrayList<Comment> commentList) {
		this.commentList = commentList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(JSONObject object) {
		if (object != null) {
			this.user = new User();
			try {
				user.setId(object.getLong(AppConstants.RESPONSE_HEADER_ID));
				user.setName(object.getString(AppConstants.HEADER_USER_NAME));
				user.setSignature(object.getString(AppConstants.HEADER_SIGNATURE));
				user.setHeadImage(object.getString(AppConstants.HEADER_HEAD_IMAGE));
				user.setRemark(object.getString(AppConstants.HEADER_REMARK));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

}
