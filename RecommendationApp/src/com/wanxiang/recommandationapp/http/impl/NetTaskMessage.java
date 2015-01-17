package com.wanxiang.recommandationapp.http.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import com.wanxiang.recommandationapp.controller.FusionMessage;


/**
 * 网络消息的基类
 * 
 * @author changlin.dcl 2013.9.8
 */
public abstract class NetTaskMessage<T> extends FusionMessage
{

//	private static final String	BASE_URL			= "http://182.92.187.217/index.php?module=core&action=";
	private static final String	BASE_URL_API			= "http://182.92.187.217/api/";
	private static final String BASE_URL_DEV			= "http://182.92.187.217/dev/";
	private static final int SERVER_TYPE			= 1; // 1:DEV  2:API
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2455718451577304839L;

	public enum HTTP_TYPE
	{
		HTTP_TYPE_GET,
		HTTP_TYPE_POST,
		HTTP_TYPE_PATCH
	}

	public final static String	DEFAULT_NETWORK_SERVICE_NAME			= "networkService";
	public final static String	DEFAULT_NETWORK_SYNC_ACTOR_NAME			= "defaultNetTaskActor";
	public final static String	DEFAULT_NETWORK_MTOP_ASYNC_ACTOR_NAME	= "defaultMTopAyncActor";

	protected String			mRequestBaseUrl;
	private String				mRequestAction;

	public String getRequestAction()
	{
		return mRequestAction;
	}

	protected void setRequestAction( String requestAction )
	{
		mRequestAction = requestAction;
	}

	public String getRequestBaseUrl()
	{
		return mRequestBaseUrl;
	}

	public void setRequestBaseUrl( String mRequestBaseUrl )
	{
		this.mRequestBaseUrl = mRequestBaseUrl;
	}

	protected String	mApiName;
	protected String	mVersion	= "1.0";
	protected HTTP_TYPE	mHttpType	= HTTP_TYPE.HTTP_TYPE_GET;

	public HTTP_TYPE getHttpType()
	{
		return mHttpType;
	}

	public void setHttpType( HTTP_TYPE mHttpType )
	{
		this.mHttpType = mHttpType;
	}

	/**
	 * Post中要传递的内容
	 */
	// private HttpEntity mBodyEntity;
	public static String				mBaseUrl;
	protected boolean					mIsTaoBaoApi		= true;
	/**
	 * ecode ,api ,v
	 */
	protected HashMap<String, String>	mCommonParams;
	/***
	 * 
	 */
	protected RequestParams				mRequestParams;
	protected RequestParams				mRequestCommonParams;

	protected HashMap<String, String>	mHeaders;
	private boolean						mIsAutoLoginRefresh	= false;

	public NetTaskMessage( String requestUrl, HTTP_TYPE httpType, boolean isTaoBaoApi )
	{
		super();
		mRequestBaseUrl = requestUrl;
		mHttpType = httpType;
		mIsTaoBaoApi = isTaoBaoApi;
		init();
	}

	public NetTaskMessage( HTTP_TYPE type )
	{
		mIsTaoBaoApi = false;
		mHttpType = type;
		init();
	}

	private String getBaseUrl()
	{

		return SERVER_TYPE == 1 ? BASE_URL_DEV : BASE_URL_API;
	}

	public void init()
	{
		mRequestParams = new RequestParams();
		mRequestCommonParams = new RequestParams();
		mHeaders = new HashMap<String, String>();
		addCommonParams();
		initServiceAndActor();

		mRequestBaseUrl = getBaseUrl();
	}

	public boolean needSession()
	{
		return true;
	}

	public RequestParams getRequestCommonParams()
	{
		return mRequestCommonParams;
	}

	/**
	 * 扩展以支持采用不同的Service/Actor 实现形式
	 */
	protected void initServiceAndActor()
	{
		this.setServiceAndActor(DEFAULT_NETWORK_SERVICE_NAME, DEFAULT_NETWORK_SYNC_ACTOR_NAME);
	}

	/**
	 * 如果commonParams 和默认不一样，请在NetTaskMessage 中重载该方法。
	 */
	protected void addCommonParams()
	{
		if (mIsTaoBaoApi)
		{
			// 在执行时添加addpara
			// mRequestCommonParams.put("api", mApiName);
			// mRequestCommonParams.put("v", mVersion);
			// mRequestCommonParams.put("sid", LoginManager.getToken() == null ?
			// "" : LoginManager.getToken());
			// boolean isEcode = needEcode();
			// if(isEcode){//Ecode 签名
			// mRequestCommonParams.put("ecode",
			// Preferences.getPreferences(context)
			// .getUserEcode());
			// }
			// mRequestCommonParams.put(CommonDefine.CLIENT_VERSION,
			// Utils.GetAppVersion(TripApplication.getInstance()));
			// mRequestCommonParams.put(CommonDefine.CLIENT_TYPE,
			// CommonDefine.mClientType);
		}
	}

	public HashMap<String, String> getHeaders()
	{
		return mHeaders;
	}

	public byte[] getPostData()
	{
		sign();
		HttpEntity postEntity = paramsToEntity();
		byte[] retBytes = null;
		try
		{
			retBytes = postEntity != null ? EntityUtils.toString(postEntity).getBytes(HTTP.UTF_8) : null;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return retBytes;
	}

	public void sign()
	{

	}

	public HttpEntity getPostEntity()
	{
		return paramsToEntity();
	}

	public boolean isTaoBaoApi()
	{
		return mIsTaoBaoApi;
	}

	public void setIsTaoBaoApi( boolean mIsTaoBaoApi )
	{
		this.mIsTaoBaoApi = mIsTaoBaoApi;
	}

	public String getUrlWithQueryString()
	{
		String url = mRequestBaseUrl;
		if (mRequestParams != null && HTTP_TYPE.HTTP_TYPE_GET.equals(mHttpType))
		{
			String paramString = mRequestParams.getParamString();
			if (url.indexOf("?") == -1)
			{
				url += "?" + paramString;
			}
			else
			{
				url += "&" + paramString;
			}
		}
		return url;
	}

	private HttpEntity paramsToEntity()
	{
		HttpEntity entity = null;
		if (mRequestParams != null)
		{
			entity = mRequestParams.getEntity();
		}
		return entity;
	}

	public RequestParams getRequestParams()
	{
		return mRequestParams;
	}

	public void addHeader( String key, String value )
	{
		mHeaders.put(key, value);
	}

	/**
	 * Adds a key/value string pair to the
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value string for the new param.
	 */
	public void addParams( String key, String value )
	{
		if (key != null && value != null)
		{
			mRequestParams.put(key, value);
		}
	}

	/**
	 * 
	 */
	public void setParam( String key, Object value )
	{
		super.setParam(key, value);
		addParams(key, value.toString());// TODO 当object 不能转成string 存在问题
	}

	/**
	 * Adds a file to the
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 */
	public void put( String key, File file ) throws FileNotFoundException
	{
		mRequestParams.put(key, new FileInputStream(file), file.getName());
	}

	/**
	 * Adds param with more than one value.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param values
	 *            is the ArrayList with values for the param.
	 */
	public void put( String key, ArrayList<String> values )
	{
		if (key != null && values != null)
		{
			mRequestParams.put(key, values);
		}
	}

	/**
	 * Adds an input stream to the
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param stream
	 *            the input stream to add.
	 */
	public void put( String key, InputStream stream )
	{
		mRequestParams.put(key, stream, null);
	}

	/**
	 * Adds an input stream to the
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param stream
	 *            the input stream to add.
	 * @param fileName
	 *            the name of the file.
	 */
	public void put( String key, InputStream stream, String fileName )
	{
		mRequestParams.put(key, stream, fileName, null);
	}

	/**
	 * Adds an input stream to the
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param stream
	 *            the input stream to add.
	 * @param fileName
	 *            the name of the file.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 */
	public void put( String key, InputStream stream, String fileName, String contentType )
	{
		mRequestParams.put(key, stream, fileName, contentType);
	}

	/**
	 * Removes a parameter from the
	 * 
	 * @param key
	 *            the key name for the parameter to remove.
	 */
	public void remove( String key )
	{
		mRequestParams.remove(key);
	}

	public String getmApiName()
	{
		return mApiName;
	}

	public void setmApiName( String mApiName )
	{
		this.mApiName = mApiName;
	}

	public String getmVersion()
	{
		return mVersion;
	}

	public void setmVersion( String mVersion )
	{
		this.mVersion = mVersion;
	}

	public abstract T parseNetTaskResponse( String content ) throws JSONException;

	public boolean IsAutoLoginRefresh()
	{
		return mIsAutoLoginRefresh;
	}

	public void setIsAutoLoginRefresh( boolean mIsAutoLoginRefresh )
	{
		this.mIsAutoLoginRefresh = mIsAutoLoginRefresh;
	}
}
