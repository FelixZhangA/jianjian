package com.wanxiang.recommandationapp.http.impl;


import org.json.JSONException;
import org.json.JSONObject;


/**
 * 基于Jason的NetTaskMessage
 * 
 * @author changlin.dcl 2013.9.8
 */
public abstract class JasonNetTaskMessage<T> extends NetTaskMessage<T>
{
	private static final long	serialVersionUID	= 1L;

	public JasonNetTaskMessage( HTTP_TYPE httpType )
	{
		super(httpType);
	}

	public JasonNetTaskMessage( String requestUrl, HTTP_TYPE httpType, boolean isTaoBaoApi )
	{
		super(requestUrl, httpType, isTaoBaoApi);
	}

	@Override
	public T parseNetTaskResponse( String content ) throws JSONException
	{

		return parseNetTaskResponse(new JSONObject(content));
	}

	protected abstract T parseNetTaskResponse( JSONObject jsonObject ) throws JSONException;
}
