package com.wanxiang.recommandationapp.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.JSONUtils;

/*
 * [id] => 56 [tuijian_id] => 168 [user_id] => 3 [replyed_user_id] => 0
 * [comment] => 儿子每天回来都看 [update_time] => 1417976164 [user_name] => 邹剑
 */

public class Comment implements Serializable {
	private long userId;
	private User user;
	private long replyedUserId;
	private String content;
	private long time;
	private long rId;
	private long id;

	public Comment() {

	}

	public Comment(long recommendationId, String user, String content,
			long currentTimeMillis) {
		this.rId = recommendationId;
		this.content = content;
		this.time = currentTimeMillis;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getReplyedUserId() {
		return replyedUserId;
	}

	public void setReplyedUserId(long replyedUserId) {
		this.replyedUserId = replyedUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getrId() {
		return rId;
	}

	public void setrId(long rId) {
		this.rId = rId;
	}

	public String getCommentContent() {
		return content;
	}

	public void setCommentContent(String content) {
		this.content = content;
	}

	public long getCommentDate() {
		return time;
	}

	public void setCommentDate(long time) {
		this.time = time;
	}

	public long getRecommendationId() {
		return rId;
	}

	public void setRecommendationId(long rId) {
		this.rId = rId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(JSONObject object) {
		if (object != null) {
			this.user = new User();
			user.setId(JSONUtils.getLong(object,
					AppConstants.RESPONSE_HEADER_ID));
			user.setName(JSONUtils.getString(object,
					AppConstants.HEADER_USER_NAME));
			user.setSignature(JSONUtils.getString(object,
					AppConstants.HEADER_SIGNATURE));
			user.setHeadImage(JSONUtils.getString(object,
					AppConstants.HEADER_HEAD_IMAGE));
			user.setRemark(JSONUtils.getString(object,
					AppConstants.HEADER_REMARK));
		}
	}
}
