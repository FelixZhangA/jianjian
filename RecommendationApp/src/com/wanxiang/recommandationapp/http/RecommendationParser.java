package com.wanxiang.recommandationapp.http;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.util.AppConstants;


public class RecommendationParser
{

	private String mResponse;
	public RecommendationParser(String response)
	{
		mResponse = response;
	}
	
	public ArrayList<Recommendation> parseRecommendationFromResponse()
	{
		ArrayList<Recommendation> ret = null;
		if (!TextUtils.isEmpty(mResponse))
		{
			try
			{
				JSONObject json = new JSONObject(mResponse);
				String error = json.getString(AppConstants.RESPONSE_HEADER_ERROR);
				if (TextUtils.equals("0", error))
				{
					ret = new ArrayList<Recommendation>();
					JSONArray array = json.getJSONArray(AppConstants.RESPONSE_HEADER_DATA);
					for (int i = 0; i < array.length(); i++)
					{
						JSONObject tmp = (JSONObject)array.get(i);
						Recommendation r = translate(tmp);
						if (r != null && !TextUtils.isEmpty(r.getEntityName()) && !TextUtils.isEmpty(r.getCategoryName()))
						{
							ret.add(r);
						}
					}
					
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return ret;
		
	}
	
	private Recommendation translate(JSONObject obj)
	{
		Recommendation ret = new Recommendation();
//		try
//		{
//			ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
//			ret.setEntity(new Entity(obj.getString(AppConstants.RESPONSE_HEADER_ENTITY_NAME)));
//		
//			String category_name = obj.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME);
//			long category_id = obj.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID);
//			ret.setCategory(new Category(category_name, category_id));
//			
//			ret.setUser(new User(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME)));
//			
//			ret.setDate(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
//			
//			ret.setPhraiseNum(obj.getInt(AppConstants.RESPONSE_HEADER_PRAISE_COUNT));
//			
//			ret.setCommentNum(obj.getInt(AppConstants.RESPONSE_HEADER_COMMENT_COUNT));
//			
//			ret.setContent(obj.getString(AppConstants.RESPONSE_HEADER_CONTENT));
//
//		}
//		catch (JSONException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return ret;
		
	}
}
