package com.wanxiang.recommandationapp.util;

public class NoContentHttpResponseException extends Exception
{
	private static final long serialVersionUID = -3659748483958904647L;

	public NoContentHttpResponseException(String message)
	{
		super(message);
	}

	public NoContentHttpResponseException(String message, Throwable t)
	{
		super(message, t);
	}

	public NoContentHttpResponseException() {}
}