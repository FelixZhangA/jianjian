package com.wanxiang.recommandationapp.service.homepage;


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


public class HomePageMessage extends JasonNetTaskMessage<ArrayList<AbstractRecommendation>>
{
	public HomePageMessage( HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_REC_LIST);
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
				Recommendation r = Utils.getRecFromJson(tmp);
				if (r != null && !TextUtils.isEmpty(r.getEntityName()) && !TextUtils.isEmpty(r.getCategoryName()))
				{
					ret.add(r);
				}
			}
			else
			{
				AskRecommendation aRec = Utils.getAskRecFromJson(tmp);
				if (aRec != null)
				{
					ret.add(aRec);
				}
			}

		}
		return ret;
	}

}
