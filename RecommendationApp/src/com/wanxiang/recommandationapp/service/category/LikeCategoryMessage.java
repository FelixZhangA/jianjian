package com.wanxiang.recommandationapp.service.category;


import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.cache.CacheManager;
import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.util.AppConstants;


public class LikeCategoryMessage extends JasonNetTaskMessage<Integer>
{

	public LikeCategoryMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_LIKE_CATEGORY);

	}

	@Override
	protected Integer parseNetTaskResponse( JSONObject jsonObject ) throws JSONException
	{
		if (jsonObject != null)
		{
			boolean success = jsonObject.getBoolean(AppConstants.RESPONSE_HEADER_SUCCESS);
			if (success)
			{
				CacheManager.clearCache(context, AppConstants.CACHE_CATEGORY_DATA);
				return 0;
			}
		}
		return -1;
	}

}
