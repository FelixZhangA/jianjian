package com.wanxiang.recommandationapp.service.friends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.wanxiang.recommandationapp.cache.CacheManager;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryData;
import com.wanxiang.recommandationapp.service.category.CategoryMessage;
import com.wanxiang.recommandationapp.util.AppConstants;

public class UploadContactService extends IntentService {

	public UploadContactService() {
		super("UploadContactService");
	}

	public UploadContactService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		UploadContactMessage message = new UploadContactMessage(
				HTTP_TYPE.HTTP_TYPE_POST);
		message.setParam(AppConstants.HEADER_CONTACTS,
				getContactsJSONString(UploadContactService.this));
		message.setParam(AppConstants.HEADER_TOKEN,
				AppPrefs.getInstance(UploadContactService.this).getSessionId());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				// TODO Auto-generated method stub
				super.onFinish(msg);
			}

			@Override
			public void onFailed(FusionMessage msg) {
				// TODO Auto-generated method stub
				super.onFailed(msg);
			}
		});
		FusionBus.getInstance(UploadContactService.this).sendMessage(message);
	}

	private String getContactsJSONString(Context context) {
		Cursor c = context.getContentResolver().query(Phone.CONTENT_URI, null,
				null, null, null);
		String phoneNum;
		String name;
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObj;

		while (c.moveToNext()) {
			phoneNum = c.getString(c.getColumnIndex(Phone.NUMBER));

			if (phoneNum.charAt(0) == '+' && phoneNum.charAt(1) == '8'
					&& phoneNum.charAt(2) == '6') {
				phoneNum = phoneNum.substring(3);
			}
			phoneNum = phoneNum.replaceAll("\\s", "");
			name = c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
			jsonObj = new JSONObject();
			try {
				jsonObj.put(AppConstants.HEADER_PHONE, phoneNum);
				jsonObj.put(AppConstants.HEADER_USER_NAME, name);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			jsonArr.put(jsonObj);
		}

		return jsonArr.toString();
	}

}
