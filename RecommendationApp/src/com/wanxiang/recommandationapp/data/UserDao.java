package com.wanxiang.recommandationapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wanxiang.recommandationapp.model.User;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseInterface;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseUtil;


public class UserDao implements SQLiteDatabaseInterface
{
	private final SQLiteDatabaseUtil	mDbHelper;

	private static UserDao	mDao	= null;
	private UserDao( Context context )
	{
		mDbHelper = new SQLiteDatabaseUtil(context, DatabaseConstants.DATABASE_NAME, DatabaseConstants.version, this);
	}
	

	public static synchronized UserDao getAdapterObject( Context context )
	{
		if (mDao == null)
		{
			mDao = new UserDao(context);
		}

		return mDao;
	}
	@Override
	public void onCreateTable( SQLiteDatabase db, Context context )
	{
		String init_user_table = "Create table " + DatabaseConstants.USER_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_USER_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_USER + " text," + DatabaseConstants.COLUMN_USER_MDN + " text," + DatabaseConstants.COLUMN_USER_IMEI + " text);";
		db.execSQL(init_user_table);

		String init_category_table = "Create table " + DatabaseConstants.CATEGORY_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_CATEGORY_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_CATEGORY + " text," + DatabaseConstants.COLUMN_CATEGORY_COLOR + " integer);";
		db.execSQL(init_category_table);

		String init_apps_table = "Create table " + DatabaseConstants.RECOMMENDATION_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_ENTITY + " text, " + DatabaseConstants.COLUMN_CATEGORY + " text," + DatabaseConstants.COLUMN_CONTENT + " text," + DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_PRAISE_NUM + " integer," + DatabaseConstants.COLUMN_COMMENT_NUM + " integer," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_apps_table);

		String init_comment_table = "Create table " + DatabaseConstants.COMMENT_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20)," + DatabaseConstants.COLUMN_CONTENT + " text, " + DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_comment_table);
	}

	@Override
	public void onUpgradeDataBase( SQLiteDatabase db, int oldVersion, int newVersion, Context context )
	{

	}
	
	public void addUser(User user)
	{ 
		
	}
	
	public void getUser(long id)
	{
		
	}
}
