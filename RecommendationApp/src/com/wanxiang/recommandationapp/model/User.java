package com.wanxiang.recommandationapp.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class User implements Serializable
{
    private String name;
    private Bitmap badge;
    private ArrayList<Category> listFavoriteCategory;
    private ArrayList<Entity> listRecommandEntity;
    public User(String userName)
    {
        this.name = userName;
        this.badge = null;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public Bitmap getBadge()
    {
        return badge;
    }
    public void setBadge(Bitmap badge)
    {
        this.badge = badge;
    }
    public ArrayList<Category> getListFavoriteCategory()
    {
        return listFavoriteCategory;
    }
    public void setListFavoriteCategory(ArrayList<Category> listFavoriteCategory)
    {
        this.listFavoriteCategory = listFavoriteCategory;
    }
    public ArrayList<Entity> getListRecommandEntity()
    {
        return listRecommandEntity;
    }
    public void setListRecommandEntity(ArrayList<Entity> listRecommandEntity)
    {
        this.listRecommandEntity = listRecommandEntity;
    }
    
}

