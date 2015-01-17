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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSON;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.cache.CacheManager;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.model.User;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryData;

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

	public static Recommendation getRecFromJson(Context context, JSONObject obj) {
		Recommendation ret = new Recommendation();
		ret.setId(JSONUtils.getLong(obj, AppConstants.RESPONSE_HEADER_ID));
		ret.setContentId(JSONUtils.getLong(obj,
				AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));

		ret.setEntityName(JSONUtils.getString(obj,
				AppConstants.RESPONSE_HEADER_ENTITY_NAME));
		ret.setEntityId(JSONUtils.getLong(obj,
				AppConstants.RESPONSE_HEADER_ENTITY_ID));

		long catId = JSONUtils.getLong(obj,
				AppConstants.RESPONSE_HEADER_CATEGORY_ID);
		ret.setCategoryId(catId);

		ret.setCategoryName(getCategoryById(context, catId));
		ret.setUserId(JSONUtils.getLong(obj,
				AppConstants.RESPONSE_HEADER_USER_ID));
		ret.setUser(JSONUtils.getObject(obj, AppConstants.HEADER_USER));

		ret.setUpdateTime(JSONUtils.getLong(obj,
				AppConstants.RESPONSE_HEADER_UPDATE_TIME));

		ret.setPhraiseNum(JSONUtils.getInt(obj,
				AppConstants.RESPONSE_HEADER_PRAISE_COUNT));
		ret.setPraisesUser(JSONUtils.getArray(obj,
				AppConstants.RESPONSE_HEADER_PRAISE));

		ret.setCommentNum(JSONUtils.getInt(obj,
				AppConstants.RESPONSE_HEADER_COMMENT_COUNT));

		ret.setComments(JSONUtils.getArray(obj,
				AppConstants.RESPONSE_HEADER_COMMENT));

		ret.setDescription(JSONUtils.getString(obj,
				AppConstants.RESPONSE_HEADER_CONTENT));

		ret.setUpdateTime(JSONUtils.getLong(obj,
				AppConstants.RESPONSE_HEADER_UPDATE_TIME));
		// TODO: 评论列表
		// ret.setComments(obj.getJSONArray(name))
		return ret;

	}

	public static AskRecommendation getAskRecFromJson(Context context,
			JSONObject obj) {
		AskRecommendation ret = new AskRecommendation();
		try {
			ret.setId(JSONUtils.getLong(obj, AppConstants.RESPONSE_HEADER_ID));
			ret.setContentId(JSONUtils.getLong(obj,
					AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			ret.setUpdateTime(JSONUtils.getLong(obj,
					AppConstants.RESPONSE_HEADER_UPDATE_TIME));

			if (obj.has(AppConstants.RESPONSE_HEADER_CATEGORY_ID)) {
				long catId = obj
						.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID);
				ret.setCategoryId(catId);
				ret.setCategoryName(Utils.getCategoryById(context, catId));
			}
			if (obj.has(AppConstants.RESPONSE_HEADER_USER_ID)) {
				ret.setUserId(obj.getLong(AppConstants.RESPONSE_HEADER_USER_ID));
			}
			ret.setUser(JSONUtils.getObject(obj, AppConstants.HEADER_USER));

			ret.setDescription(JSONUtils.getString(obj,
					AppConstants.RESPONSE_HEADER_CONTENT));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;

	}

	public static User getUserFromJson(JSONObject object) {
		if (object == null)
			return null;
		User user = new User();
		user.setId(JSONUtils.getLong(object, AppConstants.RESPONSE_HEADER_ID));
		user.setName(JSONUtils.getString(object, AppConstants.HEADER_USER_NAME));
		user.setSignature(JSONUtils.getString(object, AppConstants.HEADER_SIGNATURE));
		user.setHeadImage(JSONUtils.getString(object, AppConstants.HEADER_HEAD_IMAGE));
		return user;
	}
	
	public static Comment getCommentFromJson(JSONObject object) {
		if (object == null)
			return null;
		Comment comment = new Comment();
		comment.setId(JSONUtils.getLong(object, AppConstants.RESPONSE_HEADER_ID));
		comment.setRecommendationId(JSONUtils.getLong(object, AppConstants.RESPONSE_HEADER_OWNER_ID));
		comment.setContent(JSONUtils.getString(object, AppConstants.HEADER_COMMENT_CONTENT));
		comment.setUser(JSONUtils.getObject(object, AppConstants.HEADER_USER));
		comment.setUserId(JSONUtils.getLong(object, AppConstants.RESPONSE_HEADER_USER_ID));
		comment.setCommentDate(JSONUtils.getLong(object, AppConstants.RESPONSE_HEADER_UPDATE_TIME));
		return comment;
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
				.setDefaults(Notification.DEFAULT_SOUND)
				.setContentIntent(pendingIntent) // 关联PendingIntent
				.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
				.getNotification(); // 需要注意build()是在API level
									// 16及之后增加的，在API11中可以使用getNotificatin()来代替
		notify2.flags |= Notification.FLAG_AUTO_CANCEL;

		nm.notify(1, notify2);
	}

	/**
	 * 将时间戳转为代表"距现在多久之前"的字符串
	 * 
	 * @param timeStr
	 *            时间戳
	 * @return
	 */
	public static String getStandardDate(long date) {

		StringBuffer sb = new StringBuffer();

		long time = System.currentTimeMillis() - date;
		long mill = (long) Math.ceil(time / 1000);// 秒前

		long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

		long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

		long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

		if (day - 1 > 0) {
			sb.append(day + "天");
		} else if (hour - 1 > 0) {
			if (hour >= 24) {
				sb.append("1天");
			} else {
				sb.append(hour + "小时");
			}
		} else if (minute - 1 > 0) {
			if (minute == 60) {
				sb.append("1小时");
			} else {
				sb.append(minute + "分钟");
			}
		} else if (mill - 1 > 0) {
			if (mill == 60) {
				sb.append("1分钟");
			} else {
				sb.append(mill + "秒");
			}
		} else {
			sb.append("刚刚");
		}
		if (!sb.toString().equals("刚刚")) {
			sb.append("前");
		}
		return sb.toString();
	}

	/**
	 * 将图片截取为圆角图片
	 * 
	 * @param bitmap
	 *            原图片
	 * @param ratio
	 *            截取比例，如果是8，则圆角半径是宽高的1/8，如果是2，则是圆形图片
	 * @return 圆角矩形图片
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
				bitmap.getHeight() / ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static String getCategoryById(Context context, long categoryId) {
		CategoryData cacheData = (CategoryData) CacheManager.loadCache(context,
				AppConstants.CACHE_CATEGORY_DATA);
		if (cacheData != null) {
			ArrayList<Category> likeList = cacheData.getListLike();
			if (likeList != null) {
				for (Category tmp : likeList) {
					if (categoryId == tmp.getCagetoryId()) {
						return tmp.getCategoryName();
					}
				}
			}
			ArrayList<Category> otherList = cacheData.getListOther();
			if (otherList != null) {
				for (Category tmp : otherList) {
					if (categoryId == tmp.getCagetoryId()) {
						return tmp.getCategoryName();
					}
				}
			}
		}
		return "";
	}
}
