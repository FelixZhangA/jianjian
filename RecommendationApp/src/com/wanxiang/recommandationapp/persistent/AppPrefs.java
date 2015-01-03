package com.wanxiang.recommandationapp.persistent;

import java.util.ArrayList;

import android.content.Context;

import com.wanxiang.recommandationapp.model.Category;

public class AppPrefs extends SharedPreferencesProvider
{
	private static final String UPDATE_TIME = "update_time";
	private static final String OLDEST_UPDATE_TIME = "oldest_update_time";
	private static final String RECENT_USED_CATEGORY = "recent_used_category";
	private static final String FAVORITE_CATEGORY = "favorite_category";
	private static final String CATEGORY_UPDATE_TIME = "category_update_time";


	private static final String IMEI = "imei";

	private static AppPrefs mInstance = null;
	private static Context mContext;
	@SuppressWarnings("unchecked")
	public static final AppPrefs getInstance(Context context)
	{
		mContext = context;
		if (mInstance == null)
		{
			mInstance = new AppPrefs();
			mInstance.setContext(context.getApplicationContext());
		}
		return mInstance;
	}

	public AppPrefs()
	{
		/*
		 * Call Hierarchy will report that this constructor is not used.
		 * But if you remove it, this will fail:
		 * ConfigurationInstanceManager.getInstance().get(AppPrefs.class);
		 * 
		 * Since it uses the Class's default PUBLIC NO ARGUMENT constructor
		 */
	}

	protected AppPrefs(Context context)
	{
		super(context);
	}

	public boolean clearAll()
	{
		return edit().clear().commit();
	}


	public long getUpdateTime()
	{
		return get(UPDATE_TIME, System.currentTimeMillis());
	}

	public void setUpdateTime(long updateTime)
	{
		set(UPDATE_TIME, updateTime);
	}

	
	public long getOdestUpdateTime()
	{
		return get(OLDEST_UPDATE_TIME, System.currentTimeMillis());
	}

	public void setOdestUpdateTime(long updateTime)
	{
		set(OLDEST_UPDATE_TIME, updateTime);
	}
	
	public String getIMEI()
	{
		return get(IMEI, null);
	}

	public void setIMEI(String imei)
	{
		set(IMEI, imei);
	}
	
	public void setRecentUsedCategory(ArrayList<Category> categoryList)
	{
		saveObject(mContext, categoryList, RECENT_USED_CATEGORY);
	}
	
	public ArrayList<Category> getRecentUsedCategory()
	{
		return (ArrayList<Category>) readObject(mContext, RECENT_USED_CATEGORY);
	}
	
	
	public void setFavoriteCategory(ArrayList<Category> categoryList)
	{
		saveObject(mContext, categoryList, FAVORITE_CATEGORY);
	}
	
	public ArrayList<Category> getFavoriteCategory()
	{
		return (ArrayList<Category>) readObject(mContext, FAVORITE_CATEGORY);
	}
	
	public long getCategoryUpdateTime()
	{
		return getLong(CATEGORY_UPDATE_TIME, 0);
	}

	public void setCategoryUpdateTime(long updateTime)
	{
		set(CATEGORY_UPDATE_TIME, updateTime);
	}

}