package com.wanxiang.recommandationapp.service.category;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.util.AppConstants;


public class CategoryMessage extends JasonNetTaskMessage<ArrayList<Category>>
{

	public CategoryMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_CATEGORY);

	}

	@Override
	protected ArrayList<Category> parseNetTaskResponse( JSONObject jsonObject ) throws JSONException
	{
		ArrayList<Category> ret = new ArrayList<Category>();
		JSONArray array = jsonObject.getJSONArray(AppConstants.RESPONSE_HEADER_DATA);
		for (int i = 0; i < array.length(); i++)
		{
			JSONObject tmp = (JSONObject)array.get(i);
			Category r = translate(tmp);
			if (r != null && !TextUtils.isEmpty(r.getCategoryName()))
			{
				ret.add(r);
			}
		}
		return ret;

	}

	private Category translate( JSONObject jsonObject )
	{
		Category ret = new Category();
		try
		{
			ret.setCategoryId(jsonObject.getLong(AppConstants.RESPONSE_HEADER_ID));
			ret.setCategoryName(jsonObject.getString(AppConstants.RESPONSE_HEADER_NAME));
			ret.setParentId(jsonObject.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_PARENT));

			ArrayList<Category> childList = new ArrayList<Category>();
			JSONArray array = jsonObject.getJSONArray(AppConstants.RESPONSE_HEADER_CATEGORY_CHILDREN);
			if (array != null && array.length() > 0)
			{
				for (int i = 0; i < array.length(); i++)
				{
					JSONObject tmp = (JSONObject)array.get(i);
					Category child = translate(tmp);
					childList.add(child);
				}
			}
			
			ret.setChildrenList(childList);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return ret;
	}

}
