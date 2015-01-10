package com.wanxiang.recommandationapp.service.publish;


import org.json.JSONException;
import org.json.JSONObject;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.util.AppConstants;


/*
 * "PublishTuijian" => array( array("POST", "user_id", "int"), array("POST",
 * "category_id", "int"), array("POST", "category_name", "string"),
 * array("POST", "entity_id", "int"), array("POST", "entity_name", "string"),
 * array("POST", "description", "string"), array("POST", "need_id", "int") ),
 */
public class PublishRecMessage extends JasonNetTaskMessage<Integer>
{

	public PublishRecMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_PUBLISH_REC);

	}
	
	public PublishRecMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType, int content_type )
	{
		super(httpType);
		if (content_type == 2)
		{
			setRequestAction(AppConstants.ACTION_PUBLISH_ASK_REC);
		} 
		else
		{
			setRequestAction(AppConstants.ACTION_PUBLISH_REC);
		}

	}


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
