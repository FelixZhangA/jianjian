package com.wanxiang.recommandationapp.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.util.AppConstants;

public class AbstractRecommendation implements Serializable {
	public int content_type;
	private long id;
	private long contentId;
	private long updateTime;
	private User user;
	private long userId;

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long id) {
		this.contentId = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
