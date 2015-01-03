package com.wanxiang.recommandationapp.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Color;

public class Category implements Serializable
{

    private int color;
    private String name;
    private long   id;
    
    private boolean isFavor;

    private User lastUpdater;
    
    private int friendsCount;
    private ArrayList<Category> childrenList;
    
    private long parentId;
    
    private boolean isRecent;
    
    public Category()
    {
    	
    }
    public Category(String name)
    {
        this.name = name;
        this.color = Color.WHITE;
        this.id = System.currentTimeMillis();
    }
    
    public Category(String name, long id)
    {
        this.name = name;
        this.color = Color.WHITE;
        this.id = id;
    }

    public Category(String name, int color)
    {
        this.name = name;
        this.color = color;
        this.id = System.currentTimeMillis();
    }
    
    public String getCategoryName()
    {
        return name;
    }

    public void setCategoryName(String name)
    {
        this.name = name;
    }

	public int getColor()
	{
		return color;
	}

	public void setColor( int color )
	{
		this.color = color;
	}

	public long getCagetoryId()
	{
		return id;
	}

	public void setCategoryId( long id )
	{
		this.id = id;
	}
	public boolean isFavor()
	{
		return isFavor;
	}
	public void setFavor( boolean isFavor )
	{
		this.isFavor = isFavor;
	}
	public User getLastUpdater()
	{
		return lastUpdater;
	}
	public void setLastUpdater( User lastUpdater )
	{
		this.lastUpdater = lastUpdater;
	}
	public int getFriendsCount()
	{
		return friendsCount;
	}
	public void setFriendsCount( int friendsCount )
	{
		this.friendsCount = friendsCount;
	}
	public ArrayList<Category> getChildrenList()
	{
		return childrenList;
	}
	public void setChildrenList( ArrayList<Category> childrenList )
	{
		this.childrenList = childrenList;
	}
	public long getParentId()
	{
		return parentId;
	}
	public void setParentId( long parentId )
	{
		this.parentId = parentId;
	}
	public boolean isRecent() {
		return isRecent;
	}
	public void setRecent(boolean isRecent) {
		this.isRecent = isRecent;
	}
}
