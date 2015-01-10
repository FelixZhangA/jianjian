package com.wanxiang.recommandationapp.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSON;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;

public class Utils {
	public static String formatDate(Context context, long date) {
		if (date != 0) {
			Date tmpDate = new Date(date);
			SimpleDateFormat mDateFormat;
			ContentResolver cv = context.getContentResolver();
			String strTimeFormat = android.provider.Settings.System.getString(
					cv, android.provider.Settings.System.TIME_12_24);
			if (!TextUtils.isEmpty(strTimeFormat) && strTimeFormat.equals("24"))
				mDateFormat = new SimpleDateFormat(
						context.getString(R.string.scan_date_time_format_24hour_clock));
			else
				mDateFormat = new SimpleDateFormat(
						context.getString(R.string.scan_date_time_format_12hour_clock));

			if (null != tmpDate) {
				return mDateFormat.format(tmpDate);
			}
		}
		return null;
	}

	public static Map<String, Object> getHashMapFromJson(String jsonStr) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(jsonStr)) {
			jsonMap = (Map<String, Object>) JSON.parse(jsonStr);
		}
		return jsonMap;
	}

	public static void hideIMME(Context context, IBinder binder) {
		InputMethodManager iMMgr = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		iMMgr.hideSoftInputFromWindow(binder, 0);

	}

	public static void showIMME(Context context) {
		InputMethodManager iMMgr = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		iMMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);

	}

	public static Recommendation getRecFromJson(JSONObject obj) {
		Recommendation ret = new Recommendation();
		try {
			ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_ID));
			if (obj.has(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID)) {
				ret.setContentId(obj
						.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			}

			ret.setEntityName(obj
					.getString(AppConstants.RESPONSE_HEADER_ENTITY_NAME));
			ret.setEntityId(obj.getLong(AppConstants.RESPONSE_HEADER_ENTITY_ID));
			ret.setCategoryName(obj
					.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME));
			ret.setCategoryId(obj
					.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID));
			ret.setUserId(obj.getLong(AppConstants.RESPONSE_HEADER_USER_ID));
			ret.setUser(obj.getJSONObject(AppConstants.HEADER_USER));

			ret.setUpdateTime(obj
					.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));

			ret.setPhraiseNum(obj
					.getInt(AppConstants.RESPONSE_HEADER_PRAISE_COUNT));

			ret.setCommentNum(obj
					.getInt(AppConstants.RESPONSE_HEADER_COMMENT_COUNT));

			ret.setDescription(obj
					.getString(AppConstants.RESPONSE_HEADER_CONTENT));

			ret.setUpdateTime(obj
					.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			// TODO: 评论列表
			// ret.setComments(obj.getJSONArray(name))

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;

	}

	public static AskRecommendation getAskRecFromJson(JSONObject obj) {
		AskRecommendation ret = new AskRecommendation();
		try {
			ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_ID));
			ret.setContentId(obj
					.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			ret.setUpdateTime(obj
					.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			ret.setCategoryName(obj
					.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME));
			if (obj.has(AppConstants.RESPONSE_HEADER_CATEGORY_ID)) {
				ret.setCategoryId(obj
						.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID));
			}
			if (obj.has(AppConstants.RESPONSE_HEADER_USER_ID)) {
				ret.setUserId(obj.getLong(AppConstants.RESPONSE_HEADER_USER_ID));
			}
			ret.setUser(obj.getJSONObject(AppConstants.HEADER_USER));

			if (obj.has(AppConstants.RESPONSE_HEADER_CONTENT)) {
				ret.setDescription(obj
						.getString(AppConstants.RESPONSE_HEADER_CONTENT));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;

	}

	public static boolean isCategoryFavorited(Context context, Category category) {
		ArrayList<Category> favoriteLst = AppPrefs.getInstance(context)
				.getFavoriteCategory();
		if (favoriteLst != null && category != null) {
			for (Category cat : favoriteLst) {
				if (category.getCagetoryId() == cat.getCagetoryId())
					return true;
			}

		}
		return false;
	}

	public static void showNotification(Context context, String title,
			String description, PendingIntent pendingIntent) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify2 = new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
														// icon)
				.setTicker("您有新消息~")// 设置在status // bar上显示的提示文字
				.setContentTitle(title)// 设置在下拉status
										// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
				.setContentText(description)// TextView中显示的详细内容
				.setContentIntent(pendingIntent) // 关联PendingIntent
				.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
				.getNotification(); // 需要注意build()是在API level
									// 16及之后增加的，在API11中可以使用getNotificatin()来代替
		notify2.flags |= Notification.FLAG_AUTO_CANCEL;
		
        nm.notify(1, notify2);  
	}
}
