package com.wanxiang.recommandationapp.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSON;
import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;


public class Utils
{
	public static String formatDate( Context context, long date )
	{
		if (date != 0)
		{
			date = date*1000;
			Date tmpDate = new Date(date);
			SimpleDateFormat mDateFormat;
			ContentResolver cv = context.getContentResolver();
			String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
			if (!TextUtils.isEmpty(strTimeFormat) && strTimeFormat.equals("24"))
				mDateFormat = new SimpleDateFormat(context.getString(R.string.scan_date_time_format_24hour_clock));
			else
				mDateFormat = new SimpleDateFormat(context.getString(R.string.scan_date_time_format_12hour_clock));

			if (null != tmpDate)
			{
				return mDateFormat.format(tmpDate);
			}
		}
		return null;
	}

	public static Map<String, Object> getHashMapFromJson( String jsonStr )
	{
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(jsonStr))
		{
			jsonMap = (Map<String, Object>)JSON.parse(jsonStr);
		}
		return jsonMap;
	}

	public static void hideIMME(Context context, IBinder binder)
	{
		InputMethodManager iMMgr = (InputMethodManager) context.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		iMMgr.hideSoftInputFromWindow(binder, 0);
		
	}
	
	public static void showIMME(Context context)
	{
		InputMethodManager iMMgr = (InputMethodManager) context.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		iMMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
		
	}
	public static Recommendation getRecFromJson( JSONObject obj )
	{
		Recommendation ret = new Recommendation();
		try
		{
			if (obj.has(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID))
			{
				ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			}
			
			ret.setEntityName(obj.getString(AppConstants.RESPONSE_HEADER_ENTITY_NAME));
			ret.setEntityId(obj.getLong(AppConstants.RESPONSE_HEADER_ENTITY_ID));
			ret.setCategoryName(obj.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME));
			ret.setCategoryId(obj.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID));
			ret.setUserId(obj.getLong(AppConstants.RESPONSE_HEADER_USER_ID));
			ret.setUserName(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME));

			ret.setUpdateTime(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));

			ret.setPhraiseNum(obj.getInt(AppConstants.RESPONSE_HEADER_PRAISE_COUNT));

			ret.setCommentNum(obj.getInt(AppConstants.RESPONSE_HEADER_COMMENT_COUNT));

			ret.setDescription(obj.getString(AppConstants.RESPONSE_HEADER_CONTENT));

			ret.setUpdateTime(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			ret.setUserName(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME));
			// TODO: 评论列表
			// ret.setComments(obj.getJSONArray(name))

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;

	}
	
	public static AskRecommendation getAskRecFromJson(JSONObject obj)
	{
		AskRecommendation ret = new AskRecommendation();
		try
		{
			ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			ret.setUpdateTime(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			ret.setCategoryName(obj.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME));
			if (obj.has(AppConstants.RESPONSE_HEADER_CATEGORY_ID))
			{
				ret.setCategoryId(obj.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID));
			}
			if (obj.has(AppConstants.RESPONSE_HEADER_USER_ID))
			{
				ret.setUserId(obj.getLong(AppConstants.RESPONSE_HEADER_USER_ID));
			}
			ret.setUserName(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME));
			
			if (obj.has(AppConstants.RESPONSE_HEADER_CONTENT))
			{
				ret.setDescription(obj.getString(AppConstants.RESPONSE_HEADER_CONTENT));
			}

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return ret;

	}
	
	public static boolean isCategoryFavorited(Context context, Category category)
	{
		ArrayList<Category> favoriteLst = AppPrefs.getInstance(context).getFavoriteCategory();
		if (favoriteLst != null && category != null)
		{
			for (Category cat : favoriteLst)
			{
				if (category.getCagetoryId() == cat.getCagetoryId())
					return true;
			}
			
		}
		return false;
	}
}
