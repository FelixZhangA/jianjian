package com.wanxiang.recommandationapp.model;


import java.io.Serializable;
import java.util.ArrayList;


/*
 * 
 * ( [update_time] => 1418220999 [content_type] => 1 [content_id] => 178
 * [user_id] => 1 [category_id] => 10001 [entity_id] => 99 [description] => 狙击手
 * [praise_count] => 0 [comment_count] => 0 [user_name] => 万象小助手 [category_name]
 * => 综艺 [entity_name] => 兵临城下 )
 */
public class Recommendation extends AbstractRecommendation implements Serializable
{
	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;
	private long				updateTime;

	private long				categoryId;
	private long				entityId;
	private String				description;

	private ArrayList<Comment>	comments;
	private int					phraise_num			= 0;
	private int					comment_num			= 0;
	private String				entityName;
	private String				categoryName;
	
	private long 				needId				= 0;

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime( long updateTime )
	{
		this.updateTime = updateTime;
	}

	public long getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId( long categoryId )
	{
		this.categoryId = categoryId;
	}

	public long getEntityId()
	{
		return entityId;
	}

	public void setEntityId( long entityId )
	{
		this.entityId = entityId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public Recommendation()
	{
		// For cursor transfer use;
	}

	public ArrayList<Comment> getComments()
	{
		return comments;
	}

	public void setComments( ArrayList<Comment> comments )
	{
		this.comments = comments;
	}

	public int getPhraiseNum()
	{
		return phraise_num;
	}

	public void setPhraiseNum( int num )
	{
		this.phraise_num = num;
	}

	public int getCommentNum()
	{
		return comment_num;
	}

	public void setCommentNum( int num )
	{
		this.comment_num = num;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName( String entityName )
	{
		this.entityName = entityName;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName( String categoryName )
	{
		this.categoryName = categoryName;
	}

	public long getNeedId() {
		return needId;
	}

	public void setNeedId(long needId) {
		this.needId = needId;
	}

}
