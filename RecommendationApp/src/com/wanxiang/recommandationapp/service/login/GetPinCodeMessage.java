package com.wanxiang.recommandationapp.service.login;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.service.login.GetPinCodeMessage.GetPinCodeResult;
import com.wanxiang.recommandationapp.util.AppConstants;

public class GetPinCodeMessage extends JasonNetTaskMessage<GetPinCodeResult> {
	public GetPinCodeMessage(HTTP_TYPE httpType) {
		super(httpType);
		setRequestAction(AppConstants.ACTION_SEND_PINCODE);
	}

	@Override
	protected GetPinCodeResult parseNetTaskResponse(JSONObject jsonObject)
			throws JSONException {
		GetPinCodeResult result = new GetPinCodeResult();
		boolean success = jsonObject.getBoolean("success");
		int errCode = jsonObject.getInt("errCode");
		String errMsg = jsonObject.getString("errMsg");
		String token = "";
		if (errCode == 0) {
			JSONObject data = jsonObject.getJSONObject("data");
			if (data != null) {
				token = data.getString("code");
			}
		}
		result.success = success;
		result.errCode = errCode;
		result.errMsg = errMsg;
		result.code = token;
		return result;
	}

	public class GetPinCodeResult {
		public boolean success;
		public int errCode;
		public String errMsg;
		public String code;
	}
}
