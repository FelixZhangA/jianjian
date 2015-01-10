package com.wanxiang.recommandationapp.service.push;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.util.AppConstants;

public class SendRegisterMessage extends JasonNetTaskMessage<Integer> {

	public SendRegisterMessage(
			com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType) {
		super(httpType);
		setRequestAction(AppConstants.ACTION_NOTIFY_REGISTER);
	}

	@Override
	protected Integer parseNetTaskResponse(JSONObject jsonObject)
			throws JSONException {
		return null;
	}

}
