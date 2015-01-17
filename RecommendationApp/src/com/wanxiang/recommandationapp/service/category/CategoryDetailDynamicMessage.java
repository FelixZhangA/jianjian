package com.wanxiang.recommandationapp.service.category;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;


public class CategoryDetailDynamicMessage extends JasonNetTaskMessage<ArrayList<AbstractRecommendation>>
{

	public CategoryDetailDynamicMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_CATEGORY_DYNAMIC);
	}

	@Override
	protected ArrayList<AbstractRecommendation> parseNetTaskResponse( JSONObject jsonObject ) throws JSONException
	{
		ArrayList<AbstractRecommendation> ret = new ArrayList<AbstractRecommendation>();
		JSONArray array = jsonObject.getJSONArray(AppConstants.RESPONSE_HEADER_DATA);
		for (int i = 0; i < array.length(); i++)
		{
			JSONObject tmp = (JSONObject)array.get(i);
			if (AppConstants.RECOMMEDATION_TYPE == tmp.getInt(AppConstants.RESPONSE_HEADER_TYPE))
			{
				Recommendation r = Utils.getRecFromJson(context, tmp);
				if (r != null && !TextUtils.isEmpty(r.getEntityName()) && !TextUtils.isEmpty(r.getCategoryName()))
				{
					ret.add(r);
				}
			}
			else
			{
				AskRecommendation aRec = Utils.getAskRecFromJson(context, tmp);
				if (aRec != null)
				{
					ret.add(aRec);
				}
			}

		}
		return ret;
	}

}
