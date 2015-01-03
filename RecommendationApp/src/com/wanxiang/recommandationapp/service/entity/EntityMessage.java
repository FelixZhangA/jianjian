package com.wanxiang.recommandationapp.service.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;

public class EntityMessage extends
		JasonNetTaskMessage<ArrayList<Recommendation>> {

	public EntityMessage(
			com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType) {
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_ENTITY);
	}

	@Override
	protected ArrayList<Recommendation> parseNetTaskResponse(
			JSONObject jsonObject) throws JSONException {
		ArrayList<Recommendation> ret = new ArrayList<Recommendation>();
		JSONArray array = jsonObject
				.getJSONArray(AppConstants.RESPONSE_HEADER_DATA);
		for (int i = 0; i < array.length(); i++) {
			JSONObject tmp = (JSONObject) array.get(i);
			if (AppConstants.RECOMMEDATION_TYPE == tmp
					.getInt(AppConstants.RESPONSE_HEADER_TYPE)) {
				Recommendation r = Utils.getRecFromJson(tmp);
				if (r != null && !TextUtils.isEmpty(r.getEntityName())
						&& !TextUtils.isEmpty(r.getCategoryName())) {
					ret.add(r);
				}
			}
		}
		return ret;
	}

}
