package com.wanxiang.recommandationapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.service.db.CursorUtil;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseInterface;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseUtil;

public class CategoryDao implements SQLiteDatabaseInterface
{
	private final SQLiteDatabaseUtil	mDbHelper;
	
	private static CategoryDao	mDao	= null;
	private CategoryDao( Context context )
	{
		mDbHelper = new SQLiteDatabaseUtil(context, DatabaseConstants.DATABASE_NAME, DatabaseConstants.version, this);
	}
	

	public static synchronized CategoryDao getAdapterObject( Context context )
	{
		if (mDao == null)
		{
			mDao = new CategoryDao(context);
		}

		return mDao;
	}
	@Override
	public void onCreateTable( SQLiteDatabase db, Context context )
	{
		String init_category_table = "Create table " + DatabaseConstants.CATEGORY_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + 
									DatabaseConstants.COLUMN_CATEGORY_UID + " varchar(20) primary key," +
									DatabaseConstants.COLUMN_CATEGORY + " text," +
									DatabaseConstants.COLUMN_CATEGORY_COLOR + " integer);";
		db.execSQL(init_category_table);
		
		String init_apps_table = "Create table " + DatabaseConstants.RECOMMENDATION_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_ENTITY + " text, " + DatabaseConstants.COLUMN_CATEGORY + " text," + DatabaseConstants.COLUMN_CONTENT + " text,"+ DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_PRAISE_NUM + " integer," + DatabaseConstants.COLUMN_COMMENT_NUM + " integer," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_apps_table);
		
		String init_comment_table = "Create table " + DatabaseConstants.COMMENT_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_COMMENT_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20)," + DatabaseConstants.COLUMN_CONTENT + " text, " + DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_comment_table);
	}

	@Override
	public void onUpgradeDataBase( SQLiteDatabase db, int oldVersion, int newVersion, Context context )
	{
		String destroy_apps_table = "DROP TABLE IF EXISTS " + DatabaseConstants.CATEGORY_TABLE_NAME;
		db.execSQL(destroy_apps_table);
		
		String destroy_r_table = "DROP TABLE IF EXISTS " + DatabaseConstants.RECOMMENDATION_TABLE_NAME;
		db.execSQL(destroy_r_table);
		
		String destroy_comment_table = "DROP TABLE IF EXISTS " + DatabaseConstants.COMMENT_TABLE_NAME;
		db.execSQL(destroy_comment_table);
		onCreateTable(db, context);
	}
	
	public Cursor getAllCategoryCursor()
	{
		SQLiteDatabase db = null;
		Cursor c = null;
		try
		{
			db = mDbHelper.getDatabase(false);
			c = db.query(DatabaseConstants.CATEGORY_TABLE_NAME, null, null, null, null, null, DatabaseConstants.COLUMN_CATEGORY_UID + " DESC");
			return CursorUtil.createCopy(c);
		}
		finally
		{
			CursorUtil.safeClose(c);
			mDbHelper.close();
		}
	}

	public synchronized void insertCategory(Category cat)
	{
		SQLiteDatabase db = null;
		try
		{
			db = mDbHelper.getDatabase(true);
			db.insertWithOnConflict(DatabaseConstants.CATEGORY_TABLE_NAME, null, getAppContentValues(cat), SQLiteDatabase.CONFLICT_REPLACE);
		}
		finally
		{
			mDbHelper.close();
		}
	}

	public Category getCategoryFromCursor(Cursor cursor)
	{
		Category ret = null;
		if (cursor != null)
		{
			ret = new Category();
			ret.setCategoryId(cursor.getLong(cursor.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY_UID)));
			ret.setCategoryName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY)));
		}
		return ret;
	}
	
	private ContentValues getAppContentValues( Category cat )
	{
		ContentValues cv = new ContentValues();

		cv.put(DatabaseConstants.COLUMN_CATEGORY_UID, String.valueOf(System.currentTimeMillis()+1));
		cv.put(DatabaseConstants.COLUMN_CATEGORY, cat.getCategoryName());
		return cv;
	}
}
