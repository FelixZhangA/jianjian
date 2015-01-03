package com.wanxiang.recommandationapp.model;


/*
 * [id] => 56 [tuijian_id] => 168 [user_id] => 3 [replyed_user_id] => 0
 * [comment] => 儿子每天回来都看 [update_time] => 1417976164 [user_name] => 邹剑
 */

public class Comment
{
	private long	userId;
	private String	userName;
	private long	replyedUserId;
	private String	content;
	private long	time;
	private long	rId;
	private long	id;

	public Comment()
	{

	}

	public Comment( long recommendationId, String user, String content, long currentTimeMillis )
	{
		this.rId = recommendationId;
		this.userName = user;
		this.content = content;
		this.time = currentTimeMillis;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId( long userId )
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public long getReplyedUserId()
	{
		return replyedUserId;
	}

	public void setReplyedUserId( long replyedUserId )
	{
		this.replyedUserId = replyedUserId;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent( String content )
	{
		this.content = content;
	}

	public long getrId()
	{
		return rId;
	}

	public void setrId( long rId )
	{
		this.rId = rId;
	}

	public String getCommentContent()
	{
		return content;
	}

	public void setCommentContent( String content )
	{
		this.content = content;
	}

	public long getCommentDate()
	{
		return time;
	}

	public void setCommentDate( long time )
	{
		this.time = time;
	}

	public long getRecommendationId()
	{
		return rId;
	}

	public void setRecommendationId( long rId )
	{
		this.rId = rId;
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
