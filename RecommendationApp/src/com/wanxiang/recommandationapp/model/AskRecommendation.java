package com.wanxiang.recommandationapp.model;


import java.io.Serializable;


/*
 * [content_type] => 2 [content_id] => 3 [update_time] => 1417921177 [user_id]
 * => 1 [category_id] => 25 [description] => 非常好看,与史实基本附合,还想看吗 [user_name] =>
 * 万象小助手 [category_name] => 电影
 */
public class AskRecommendation extends AbstractRecommendation implements Serializable
{
	public int		content_type	= 2;
	private long	categoryId;
	private String	description;
	private String categoryName;

	public long getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId( long categoryId )
	{
		this.categoryId = categoryId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}


	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName( String categoryName )
	{
		this.categoryName = categoryName;
	}


}
