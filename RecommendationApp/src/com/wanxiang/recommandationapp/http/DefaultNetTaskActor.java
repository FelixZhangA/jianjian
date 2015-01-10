package com.wanxiang.recommandationapp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;

import android.text.TextUtils;
import android.util.Log;

import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.util.Constants;

// import mtopsdk.mtop.util.HttpHeaderConstant;

/**
 * 基于TaoSdk的同步处理Actor
 * 
 * @author changlin.dcl 2013.9.8
 */
public class DefaultNetTaskActor extends NetActor {
	private static final String TAG = DefaultNetTaskActor.class.getSimpleName();
	private String apiName = "";

	@Override
	public boolean processFusionMessage(final FusionMessage msg) {

		if (!(msg instanceof NetTaskMessage)) {
			Log.e(TAG, " msg not NetTaskMessage");
			msg.publishMessageError(FusionMessage.ERROR_CODE_SYS_ERROR,
					FusionMessage.ERROR_MSG_SYS_ERROR,
					FusionMessage.ERROR_MSG_SYS_ERROR);
			return true;
		}

		@SuppressWarnings("rawtypes")
		final NetTaskMessage netTaskMessage = (NetTaskMessage) msg;
		// if (!NetworkInfo.is(context))
		// {
		// Log.d(TAG, "isNetworkAvailable:false");
		// netTaskMessage.publishMessageError(FusionMessage.ERROR_CODE_NET_ERROR,
		// FusionMessage.ERROR_MSG_NET_ERROR,
		// FusionMessage.ERROR_MSG_NET_ERROR);
		// return true;
		// }

		Object retObject = getNetTaskResponse(netTaskMessage);
		if (Constants.STATISTIC_OPEN) {
			msg.setParam(Constants.IS_NET_API, "true");
		}
		if (netTaskMessage.isCancel()) {
			return true;
		}
		if (retObject != null) {// TODO: ret null 的处理
			msg.setResponseData(retObject);
			return true;
		} else {// 避免UI层因为没有空指针判断而crash
				// msg.publishMessageError(FusionMessage.ERROR_CODE_SYS_ERROR,
				// FusionMessage.ERROR_MSG_SYS_ERROR,
				// FusionMessage.ERROR_MSG_SYS_ERROR);
			return true;
		}

	}

	@SuppressWarnings("rawtypes")
	protected Object getNetTaskResponse(final NetTaskMessage netTaskMessage) {
		HttpClient client = null;
		try {

			HttpPost httpRequest = createCommonHttpRequest(netTaskMessage);
			client = HttpHelper.createHttpClient(false);
			HttpResponse response = client.execute(httpRequest);

			return syncPaser(netTaskMessage, response, "UTF-8");
		} catch (Exception e) {
			String logStr = "invoke network.syncSend error.";
			Log.e(TAG, logStr);

			netTaskMessage.publishMessageError(-1, e.getMessage(),
					e.getLocalizedMessage());

		}

		return null;
	}

	private HttpPost createCommonHttpRequest(NetTaskMessage netTaskMessage) {
		HttpPost httpRequest = null;
		if (netTaskMessage != null) {
			StringBuilder sb = new StringBuilder(
					netTaskMessage.getRequestBaseUrl());
			sb.append(netTaskMessage.getRequestAction());

			if (netTaskMessage.getHttpType() == HTTP_TYPE.HTTP_TYPE_POST) {
				httpRequest = new HttpPost(sb.toString());

				httpRequest.setEntity(netTaskMessage.getPostEntity());

			} else {
				if (netTaskMessage.getParams() != null
						&& netTaskMessage.getParams().size() > 0) {
					sb.append("?");
					for (String key : netTaskMessage.getParams().keySet()) {
						sb.append("&");
						sb.append(key);
						sb.append("=");
						sb.append(netTaskMessage.getParams().get(key));
					}
				}
				httpRequest = new HttpPost(sb.toString());
			}
			Log.d("wanxiang", "request URL is " + sb.toString());

			HashMap<String, String> additionalHeaders = netTaskMessage
					.getHeaders();
			for (String propertyName : additionalHeaders.keySet()) {
				httpRequest.addHeader(propertyName,
						(String) additionalHeaders.get(propertyName));
			}
		}

		return httpRequest;
	}

	/**
	 * @param httpParamsMap
	 * @param property
	 * @return
	 */
	// private static Map<String, String> prepareRequestHeaders(Map<String,
	// String> httpParamsMap, Map<String, String> headers){
	//
	// if (headers == null) {
	// headers=new HashMap<String, String>();
	// }
	// //添加sdk版本标识
	// headers.put(HttpHeaderConstant.M_SDKVER,HttpHeaderConstant.M_SDKVER_VALUE);
	//
	// //协议参数上移到request header，同时从httpParamsMap中移除
	// for (MtopHeaderFieldEnum headerField : MtopHeaderFieldEnum.values()) {
	// String value= httpParamsMap.remove(headerField.getXstateKey());
	// if (StringUtils.isNotBlank(value)) {
	// try {
	// headers.put(headerField.getHeadField(),URLEncoder.encode(value,
	// "utf-8"));
	// } catch (UnsupportedEncodingException e) {
	// }
	// }
	// }
	//
	// //m-location
	// String lng=httpParamsMap.remove(XStateConstants.KEY_LNG);
	// String lat=httpParamsMap.remove(XStateConstants.KEY_LAT);
	//
	// if (StringUtils.isNotBlank(lng)&&StringUtils.isNotBlank(lat)) {
	// StringBuilder location=new StringBuilder();
	// location.append(lng);
	// location.append(",");
	// location.append(lat);
	// try {
	// headers.put(HttpHeaderConstant.M_LOCATION,URLEncoder.encode(location.toString(),
	// "utf-8"));
	// } catch (UnsupportedEncodingException e) {
	// }
	//
	// }
	//
	// return headers;
	//
	// }

	private Object syncPaser(NetTaskMessage netTaskMessage,
			HttpResponse response, String charSetName) {
		boolean hasException = false;
		int errorCode = FusionMessage.ERROR_CODE_NET_ERROR;
		String errorMsg = FusionMessage.ERROR_MSG_NET_ERROR;
		String errorDescription = FusionMessage.ERROR_MSG_NET_ERROR;
		if (response != null) {// 返回状态码不是200 //TODO:网络错误还不能检测

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				netTaskMessage.publishMessageError(errorCode, errorMsg,
						errorDescription);
			} else {
				try {
					InputStream is = response.getEntity().getContent();
					InputStreamReader isr = new InputStreamReader(is, "UTF-8");
					BufferedReader bis = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					String s;

					while (true) {
						s = bis.readLine();
						if (TextUtils.isEmpty(s)) {
							break;
						}
						sb.append(s);
					}
					Log.d("wanxiang", "Response is " + sb.toString());
					return netTaskMessage.parseNetTaskResponse(sb.toString());
				} catch (JSONException e) {
					hasException = true;
					e.printStackTrace();
				} catch (IllegalStateException e) {
					hasException = true;
					e.printStackTrace();
				} catch (IOException e) {
					hasException = true;
					e.printStackTrace();
				}
			}

		}

		// 成功则同步返回，失败则异步返回
		if (hasException) {

			if (netTaskMessage.IsAutoLoginRefresh()) {
				netTaskMessage.setErrorWithoutNotify(errorCode, errorMsg,
						errorDescription);
				autologinAndAsyncNotifyError(netTaskMessage);
			} else {
				netTaskMessage.publishMessageError(errorCode, errorMsg,
						errorDescription);
			}
		}

		return null;// 异步返回
	}

}
