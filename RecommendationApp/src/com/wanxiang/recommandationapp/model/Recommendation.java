package com.wanxiang.recommandationapp.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.util.Utils;

/*
 * 
 * ( [update_time] => 1418220999 [content_type] => 1 [content_id] => 178
 * [user_id] => 1 [category_id] => 10001 [entity_id] => 99 [description] => 狙击手
 * [praise_count] => 0 [comment_count] => 0 [user_name] => 万象小助手 [category_name]
 * => 综艺 [entity_name] => 兵临城下 )
 */
public class Recommendation extends AbstractRecommendation implements
		Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private long updateTime;

	private long categoryId;
	private long entityId;
	private String description;

	private ArrayList<Comment> comments;
	private int phraise_num = 0;
	private int comment_num = 0;
	private ArrayList<User> praisesUser;
	private ArrayList<User> commentUser;

	private String entityName;
	private String categoryName;

	private int friendType; // 好友类型
	private User commonFriend; // 共同好友
	private long needId = 0;

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Recommendation() {
		// For cursor transfer use;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(JSONArray jsonArray) {
		
		if (jsonArray != null && jsonArray.length() > 0) {
			comments = new ArrayList<Comment>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object;
				try {
					object = jsonArray.getJSONObject(i);
					comments.add(Utils.getCommentFromJson(object));

				} catch (JSONException e) {
				}
			}
		}	}

	public int getPhraiseNum() {
		return phraise_num;
	}

	public void setPhraiseNum(int num) {
		this.phraise_num = num;
	}

	public int getCommentNum() {
		return comment_num;
	}

	public void setCommentNum(int num) {
		this.comment_num = num;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public long getNeedId() {
		return needId;
	}

	public void setNeedId(long needId) {
		this.needId = needId;
	}

	public ArrayList<User> getPraisesUser() {
		return praisesUser;
	}

	public void setPraisesUser(JSONArray jsonArray) {
		if (jsonArray != null && jsonArray.length() > 0) {
			praisesUser = new ArrayList<User>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object;
				try {
					object = jsonArray.getJSONObject(i);
					praisesUser.add(Utils.getUserFromJson(object));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<User> getCommentUser() {
		return commentUser;
	}

	public void setCommentUser(JSONArray jsonArray) {
		if (jsonArray != null && jsonArray.length() > 0) {
			commentUser = new ArrayList<User>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object;
				try {
					object = jsonArray.getJSONObject(i);
					commentUser.add(Utils.getUserFromJson(object));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public int getFriendType() {
		return friendType;
	}

	public void setFriendType(int friendType) {
		this.friendType = friendType;
	}

	public User getCommonFriend() {
		return commonFriend;
	}

	public void setCommonFriend(User commonFriend) {
		this.commonFriend = commonFriend;
	}

}
