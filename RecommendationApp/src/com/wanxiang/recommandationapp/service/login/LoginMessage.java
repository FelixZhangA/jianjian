package com.wanxiang.recommandationapp.service.login;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.service.login.LoginMessage.LoginResult;
import com.wanxiang.recommandationapp.util.AppConstants;

public class LoginMessage extends JasonNetTaskMessage<LoginResult> {

	public LoginMessage(HTTP_TYPE httpType) {
		super(httpType);
		setRequestAction(AppConstants.ACTION_LOGIN);
	}

	// {"success":true,"errCode":0,"errMsg":"","data":{"token":"c0YrA7G0oyJwdroJ7h3LXiPfH2C38XBu"}}
	@Override
	protected LoginResult parseNetTaskResponse(JSONObject jsonObject)
			throws JSONException {
		LoginResult result = new LoginResult();
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

	public class LoginResult {
		public int errCode;
		public String errMsg;
		public String token;
	}
}
