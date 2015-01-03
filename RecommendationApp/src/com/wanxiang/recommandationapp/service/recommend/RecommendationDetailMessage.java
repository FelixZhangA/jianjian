package com.wanxiang.recommandationapp.service.recommend;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.data.RecommendationDetail;
import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.util.AppConstants;

public class RecommendationDetailMessage extends JasonNetTaskMessage<RecommendationDetail>
{

	public RecommendationDetailMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_REC_DETAIL);
	}

	@Override
	protected RecommendationDetail parseNetTaskResponse( JSONObject obj ) throws JSONException
	{
		RecommendationDetail ret = new RecommendationDetail();
		try
		{
			JSONObject data = obj.getJSONObject(AppConstants.RESPONSE_HEADER_DATA);
			ret.setUserId(data.getLong(AppConstants.RESPONSE_HEADER_USER_ID));
			ret.setCategoryId(data.getLong(AppConstants.RESPONSE_HEADER_CATEGORY_ID));
			ret.setEntityId(data.getLong(AppConstants.RESPONSE_HEADER_ENTITY_ID));
			ret.setDescription(data.getString(AppConstants.RESPONSE_HEADER_CONTENT));
			ret.setPhraiseCount(data.getInt(AppConstants.RESPONSE_HEADER_PRAISE_COUNT));
			ret.setCommentCount(data.getInt(AppConstants.RESPONSE_HEADER_COMMENT_COUNT));
			ret.setUserName(data.getString(AppConstants.RESPONSE_HEADER_USER_NAME));
			ret.setCategoryName(data.getString(AppConstants.RESPONSE_HEADER_CATEGORY_NAME));

			
			JSONArray array = data.getJSONArray(AppConstants.RESPONSE_HEADER_COMMENT);
			ArrayList<Comment> commentLst = new ArrayList<Comment>();
			for (int i = 0; i < array.length(); i++)
			{
				JSONObject tmp = (JSONObject)array.get(i);
				Comment r = translate(tmp);
				if (r != null)
				{
					commentLst.add(r);
				}
			}
			ret.setCommentList(commentLst);

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	/* [id] => 56
                            [tuijian_id] => 168
                            [user_id] => 3
                            [replyed_user_id] => 0
                            [comment] => 儿子每天回来都看
                            [update_time] => 1417976164
                            [user_name] => 邹剑*/

	private Comment translate( JSONObject obj )
	{
		Comment ret = new Comment();
		try
		{
			ret.setId(obj.getLong(AppConstants.RESPONSE_HEADER_ID));
			ret.setCommentContent(obj.getString(AppConstants.HEADER_COMMENT_CONTENT));
			ret.setCommentDate(obj.getLong(AppConstants.RESPONSE_HEADER_UPDATE_TIME));
			ret.setUserName(obj.getString(AppConstants.RESPONSE_HEADER_USER_NAME));
			ret.setRecommendationId(obj.getLong(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID));
			ret.setReplyedUserId(obj.getLong(AppConstants.RESPONSE_HEADER_REPLYED_USER_ID));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	
		return ret;
	}

	
}
