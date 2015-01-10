package com.wanxiang.recommandationapp.service.publish;

import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.util.AppConstants;

public class PublishCommentsMessage extends JasonNetTaskMessage<Integer>
{

	public PublishCommentsMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_COMMENT_REC);
	}
// {"success":true,"errCode":0,"errMsg":"","data":{"commentId":138}}
	@Override
	protected Integer parseNetTaskResponse( JSONObject jsonObject ) throws JSONException
	{
		if (jsonObject != null)
		{
			boolean success = jsonObject.getBoolean(AppConstants.RESPONSE_HEADER_SUCCESS);
			if (success)
			{
				return 0;
			}
		}
		return -1;
	}

}
