package com.wanxiang.recommandationapp.service.friends;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.service.friends.UploadContactMessage.UploadContactResults;
import com.wanxiang.recommandationapp.util.AppConstants;

public class UploadContactMessage extends JasonNetTaskMessage<UploadContactResults> {

	public UploadContactMessage(
			com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType) {
		super(httpType);
		setRequestAction(AppConstants.ACTION_CONTACTS_UPLOAD);
	}

	@Override
	protected UploadContactResults parseNetTaskResponse(JSONObject jsonObject)
			throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	public class UploadContactResults {
		
	}
}
