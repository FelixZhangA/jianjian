package com.wanxiang.recommandationapp.service.login;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.service.login.RegisterMessage.RegisterResult;
import com.wanxiang.recommandationapp.util.AppConstants;

public class RegisterMessage extends JasonNetTaskMessage<RegisterResult> {

	public RegisterMessage(HTTP_TYPE httpType) {
		super(httpType);
		setRequestAction(AppConstants.ACTION_REGISTER);
	}

	@Override
	protected RegisterResult parseNetTaskResponse(JSONObject jsonObject)
			throws JSONException {
		RegisterResult result = new RegisterResult();
		int errCode = jsonObject.getInt("errCode");
		String errMsg = jsonObject.getString("errMsg");
		String token = "";
		if (errCode == 0) {
			JSONObject data = jsonObject.getJSONObject("data");
			if (data != null) {
				token = data.getString("token");
			}
		}
		result.errCode = errCode;
		result.errMsg = errMsg;
		result.token = token;
		return result;
	}

	public class RegisterResult {
		public int errCode;
		public String errMsg;
		public String token;
	}

}
