package com.wanxiang.recommandationapp.http;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.util.AppConstants;

public class CommentsParser
{
	private String mResponse;
	public CommentsParser(String response)
	{
		mResponse = response;
	}
	
	public ArrayList<Comment> parseCommentsFromResponse()
	{
		ArrayList<Comment> ret = null;
		if (!TextUtils.isEmpty(mResponse))
		{
			try
			{
				JSONObject json = new JSONObject(mResponse);
				String error = json.getString(AppConstants.RESPONSE_HEADER_ERROR);
				if (TextUtils.equals("0", error))
				{
					ret = new ArrayList<Comment>();
					JSONObject data = json.getJSONObject(AppConstants.RESPONSE_HEADER_DATA);
					if (data != null)
					{
						JSONArray array = data.getJSONArray(AppConstants.RESPONSE_HEADER_COMMENT);

						for (int i = 0; i < array.length(); i++)
						{
							JSONObject tmp = (JSONObject)array.get(i);
							Comment r = translate(tmp);
							if (r != null)
							{
								ret.add(r);
							}
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
	
    /*[0] => Array
            (
                [id] => 5
                [tuijian_id] => 103
                [user_id] => 1
                [replyed_user_id] => 0
                [comment] => ������飬���Կ���
                [update_time] => 1411554021
                [user_name] => ����С����
            )*/
	private Comment translate(JSONObject obj)
	{
		Comment ret = new Comment();
		try
		{
			ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_ID));
			ret.setCommentContent(obj.getString(AppConstants.HEADER_COMMENT_CONTENT));
			ret.setCommentDate(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			ret.setUserName(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME));
			ret.setRecommendationId(obj.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			/*ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			ret.setEntity(new Entity(obj.getString(AppConstants.RESPONSE_HEADER_ENTITY_NAME)));
		
			String category_name = obj.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME);
			long category_id = obj.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID);
			ret.setCategory(new Category(category_name, category_id));
			
			ret.setUser(new User(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME)));
			
			ret.setDate(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			
			ret.setPhraiseNum(obj.getInt(AppConstants.RESPONSE_HEADER_PRAISE_COUNT));
			
			ret.setCommentNum(obj.getInt(AppConstants.RESPONSE_HEADER_COMMENT_COUNT));
			
			ret.setContent(obj.getString(AppConstants.RESPONSE_HEADER_CONTENT));*/

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
		
	}
}
