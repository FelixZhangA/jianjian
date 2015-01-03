package com.wanxiang.recommandationapp.http;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

public class EasyHttpClient extends DefaultHttpClient
{
	public EasyHttpClient()
	{
		super();
	}

	public EasyHttpClient(HttpParams params)
	{
		super(params);
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager()
	{
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), schemeRegistry);
	}
}