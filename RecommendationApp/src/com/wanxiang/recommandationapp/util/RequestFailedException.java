package com.wanxiang.recommandationapp.util;

public class RequestFailedException extends Exception
{
	private static final long serialVersionUID = -4570146600814638902L;

	public int StatusCode;

	public RequestFailedException(String message)
	{
		super(message);
	}

	public RequestFailedException(String message, int statusCode)
	{
		super(message);
		StatusCode = statusCode;
	}

	public RequestFailedException(String message, Throwable t)
	{
		super(message, t);
	}


}