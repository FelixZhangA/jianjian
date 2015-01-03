package com.wanxiang.recommandationapp.model;


import java.io.Serializable;


public class AbstractRecommendation implements Serializable
{
	public int content_type;
	private long id;
	private long	updateTime;
	private String	userName;
	private long	userId;

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime( long updateTime )
	{
		this.updateTime = updateTime;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId( long userId )
	{
		this.userId = userId;
	}

	public long getId()
	{
		return id;
	}

	public void setId( long id )
	{
		this.id = id;
	}

}
