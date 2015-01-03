package com.wanxiang.recommandationapp.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;

import android.util.Log;

import com.wanxiang.recommandationapp.util.IO;
import com.wanxiang.recommandationapp.util.NoContentHttpResponseException;
import com.wanxiang.recommandationapp.util.RequestFailedException;
import com.wanxiang.recommandationapp.util.StreamUtil;


public class HttpHelper
{
	private static final int		SOCKET_TIMEOUT_MILLIS		= 60000 * 3;
	private static final int		CONNECTION_TIMEOUT_MILLIS	= 60000 * 3;

	private static final String		URL							= "http://182.92.187.217/index.php?module=core&action=";
	private static EasyHttpClient	sessionClient;

	public static String sendRequestForString( final HttpRequestBase httpRequest ) throws RequestFailedException, NoContentHttpResponseException
	{
		return sendRequestForString(httpRequest, CreateDefaultHttpClient());
	}

	public static String sendRequestForString( final HttpRequestBase httpRequest, final AbstractHttpClient httpClient ) throws RequestFailedException, NoContentHttpResponseException
	{
		HttpResponse response = sendRequest(httpRequest, httpClient);

		final InputStream inputStream;
		try
		{
			inputStream = response.getEntity().getContent();
		}
		catch (IllegalStateException e)
		{
			throw new RequestFailedException("Failed to get content", e);
		}
		catch (IOException e)
		{
			throw new RequestFailedException("Failed to get content", e);
		}

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			StreamUtil.copyInputStreamToOutputStream(inputStream, baos, 1024);
		}
		catch (IOException e)
		{
			throw new RequestFailedException("Failed to copy stream contents", e);
		}
		finally
		{
			IO.safeClose(inputStream);
		}

		return new String(baos.toByteArray());
	}

	public static HttpResponse sendRequest( final HttpRequestBase httpRequest, final HttpClient httpClient ) throws RequestFailedException, NoContentHttpResponseException
	{
		Log.d("wanxiang", "Executing request: " + httpRequest.getURI().toASCIIString());

		/*
		 * Make sure cookies are stored in a local context (as opposed to the
		 * shared global context) so all requests don't reuse the same
		 * sessionid.
		 */

		final BasicHttpContext httpContext = new BasicHttpContext();
		final CookieStore cookieStore = new BasicCookieStore();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpResponse response;
		try
		{
			response = (HttpResponse)httpClient.execute(httpRequest, httpContext);
		}
		catch (ClientProtocolException e)
		{
			throw new RequestFailedException("Failed to send request", e);
		}
		catch (IOException e)
		{
			throw new RequestFailedException("Failed to send request", e);
		}
		catch (Exception e)
		{
			throw new RequestFailedException("Failed to send request", e);
		}

		final int statusCode = response.getStatusLine().getStatusCode();

		Log.d("wanxiang", "Received response: " + response.toString() + " to request: " + httpRequest.getURI().toASCIIString() + " Status code: " + statusCode);

		if (statusCode == HttpStatus.SC_NO_CONTENT)
		{
			throw new NoContentHttpResponseException();
		}
		if (statusCode != HttpStatus.SC_OK)
		{
			throw new RequestFailedException("Received an invalid status code: " + statusCode, statusCode);

		}
		return response;
	}

	private static AbstractHttpClient CreateDefaultHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MILLIS);
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_MILLIS);
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);

		httpClient.setParams(params);

		return httpClient;
	}

	private static BasicHttpParams getHTTPParams()
	{
		BasicHttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 70000);
		httpParams.setParameter(HttpConnectionParams.SO_TIMEOUT, 70000);
		return httpParams;
	}

	public static final EasyHttpClient createHttpClient( boolean maintainSession )
	{
		EasyHttpClient httpClient = new EasyHttpClient(getHTTPParams());

		// dont retry requests
		httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

		// dont reuse connections
		httpClient.setReuseStrategy(new NoConnectionReuseStrategy());

		if (!maintainSession)
		{
			sessionClient = httpClient;
		}
		else if (null != sessionClient)
		{
			httpClient.setCookieStore(sessionClient.getCookieStore());
		}

		return httpClient;
	}

	public static HttpPost createHttpPost( String requestType, final Map<String, String> uploadProperties, final Map<String, ?> additionalHeaders )
	{
		StringBuilder sb = new StringBuilder(URL);
		sb.append(requestType);
		if (uploadProperties != null && uploadProperties.size() > 0)
		{
			for (String key : uploadProperties.keySet())
			{		
				sb.append("&");
				sb.append(key);
				sb.append("=");
				sb.append(uploadProperties.get(key));
			}
		}

		Log.d("wanxiang", "request URL is " + sb.toString());
		HttpPost request = new HttpPost(sb.toString());
		
		for (String propertyName : additionalHeaders.keySet())
		{
			request.addHeader(propertyName, (String) additionalHeaders.get(propertyName));
		}
		
		return request;
	}
}
