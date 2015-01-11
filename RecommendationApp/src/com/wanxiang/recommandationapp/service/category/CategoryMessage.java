package com.wanxiang.recommandationapp.service.category;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.cache.CacheManager;
import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.util.AppConstants;


public class CategoryMessage extends JasonNetTaskMessage<CategoryData>
{

	public CategoryMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_CATEGORY);

	}

	@Override
	protected CategoryData parseNetTaskResponse( JSONObject jsonObject ) throws JSONException
	{
		CategoryData ret = new CategoryData();
		JSONObject data = jsonObject.getJSONObject(AppConstants.RESPONSE_HEADER_DATA);
		JSONArray likeCategoryArray = data.getJSONArray(AppConstants.RESPONSE_HEADER_LIKE_LIST);
		JSONArray otherCategoryArray = data.getJSONArray(AppConstants.RESPONSE_HEADER_OTHER_LIST);

		ArrayList<Category> lstLike = new ArrayList<Category>();
		for (int i = 0; i < likeCategoryArray.length(); i++)
		{
			JSONObject tmp = (JSONObject)likeCategoryArray.get(i);
			Category cat = new Category();
			cat.setCategoryId(tmp.getLong(AppConstants.RESPONSE_HEADER_ID));
			cat.setCategoryName(tmp.getString(AppConstants.RESPONSE_HEADER_NAME));
			if (cat != null && !TextUtils.isEmpty(cat.getCategoryName()))
			{
				lstLike.add(cat);
			}
		}
		ret.setListLike(lstLike);
		
		ArrayList<Category> lstOther = new ArrayList<Category>();
		for (int i = 0; i < otherCategoryArray.length(); i++)
		{
			JSONObject tmp = (JSONObject)otherCategoryArray.get(i);
			Category cat = new Category();
			cat.setCategoryId(tmp.getLong(AppConstants.RESPONSE_HEADER_ID));
			cat.setCategoryName(tmp.getString(AppConstants.RESPONSE_HEADER_NAME));
			if (cat != null && !TextUtils.isEmpty(cat.getCategoryName()))
			{
				lstOther.add(cat);
			}
		}
		ret.setListOther(lstOther);
		
		// 增加缓存
		CacheManager.saveCache(context, AppConstants.CACHE_CATEGORY_DATA, ret);
		return ret;

	}

}
