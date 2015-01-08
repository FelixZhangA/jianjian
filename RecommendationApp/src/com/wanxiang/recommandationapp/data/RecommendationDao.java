package com.wanxiang.recommandationapp.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.service.db.CursorUtil;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseInterface;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseUtil;


public class RecommendationDao implements SQLiteDatabaseInterface
{

	private final SQLiteDatabaseUtil	mDbHelper;
	private static RecommendationDao	mDao	= null;

	private RecommendationDao( Context context )
	{
		mDbHelper = new SQLiteDatabaseUtil(context, DatabaseConstants.DATABASE_NAME, DatabaseConstants.version, this);
	}

	public static synchronized RecommendationDao getAdapterObject( Context context )
	{
		if (mDao == null)
		{
			mDao = new RecommendationDao(context);
		}

		return mDao;
	}

	@Override
	public void onCreateTable( SQLiteDatabase db, Context context )
	{
		String init_apps_table = "Create table " + DatabaseConstants.RECOMMENDATION_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_ENTITY + " text, " + DatabaseConstants.COLUMN_CATEGORY + " text," + DatabaseConstants.COLUMN_CONTENT + " text,"+ DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_PRAISE_NUM + " integer," + DatabaseConstants.COLUMN_COMMENT_NUM + " integer," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_apps_table);
		

		String init_category_table = "Create table " + DatabaseConstants.CATEGORY_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + 
									DatabaseConstants.COLUMN_CATEGORY_UID + " varchar(20) primary key," +
									DatabaseConstants.COLUMN_CATEGORY + " text," +
									DatabaseConstants.COLUMN_CATEGORY_COLOR + " integer);";
		db.execSQL(init_category_table);
		
		String init_comment_table = "Create table " + DatabaseConstants.COMMENT_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_COMMENT_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20)," + DatabaseConstants.COLUMN_CONTENT + " text, " + DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_comment_table);
	}

	@Override
	public void onUpgradeDataBase( SQLiteDatabase db, int oldVersion, int newVersion, Context context )
	{
		String destroy_apps_table = "DROP TABLE IF EXISTS " + DatabaseConstants.RECOMMENDATION_TABLE_NAME;
		db.execSQL(destroy_apps_table);
		onCreateTable(db, context);
	}
	
	public synchronized Recommendation insertRecommandation(Recommendation r)
	{
		SQLiteDatabase db = null;
		try
		{
			db = mDbHelper.getDatabase(true);
			db.insertWithOnConflict(DatabaseConstants.RECOMMENDATION_TABLE_NAME, null, getAppContentValues(r), SQLiteDatabase.CONFLICT_REPLACE);
		}
		finally
		{
			mDbHelper.close();
		}
		return r;
	}

	public synchronized Cursor getAllRecommendationsCursor()
	{
		SQLiteDatabase db = null;
		Cursor c = null;
		try
		{
			db = mDbHelper.getDatabase(false);
			c = db.query(DatabaseConstants.RECOMMENDATION_TABLE_NAME, null, null, null, null, null, DatabaseConstants.COLUMN_DATE + " DESC");
			c.moveToFirst();
			return CursorUtil.createCopy(c);
		}
		finally
		{
			CursorUtil.safeClose(c);
			mDbHelper.close();
		}
	}

	public synchronized void updatePraiseCount(String recID)
	{
		SQLiteDatabase db = null;
		try
		{
			db = mDbHelper.getDatabase(false);
			Recommendation r = getRecommendationByID(recID);
			if (r != null)
			{
				int current = r.getPhraiseNum();
				ContentValues cv = new ContentValues();
				cv.put(DatabaseConstants.COLUMN_PRAISE_NUM, current++);
				db.update(DatabaseConstants.RECOMMENDATION_TABLE_NAME, cv, " recommandation_id=?", new String[]{recID});
			}
		}
		finally
		{
			mDbHelper.close();
		}		
	}
	
	public synchronized Recommendation getRecommendationByID(String recID)
	{
		SQLiteDatabase db = null;
		Cursor c = null;
		try
		{
			db = mDbHelper.getDatabase(false);
			c = db.query(DatabaseConstants.RECOMMENDATION_TABLE_NAME, null, " recommandation_id=?", new String[]{recID}, null, null, " DESC");
			return getRecommendationFromCursor(c);
		}
		finally
		{
			CursorUtil.safeClose(c);
			mDbHelper.close();
		}		
	}
	
	public synchronized Cursor getRecommendationsByCategoryCursor(String category)
	{
		SQLiteDatabase db = null;
		Cursor c = null;
		try
		{
			db = mDbHelper.getDatabase(false);
			c = db.query(DatabaseConstants.RECOMMENDATION_TABLE_NAME, null, " category =?", new String[]{category}, null, null, DatabaseConstants.COLUMN_DATE + " DESC");

			return CursorUtil.createCopy(c);
		}
		catch (Exception ex)
		{
			Log.e("wanxiang", ex.getMessage());
		}
		finally
		{
			CursorUtil.safeClose(c);
			mDbHelper.close();
		}
		return c;
	}
	public Recommendation getRecommendationFromCursor(Cursor c)
	{
		if (c == null)
		{
			return null;
		}
		final Recommendation r = new Recommendation();
		r.setContentId(c.getLong(c.getColumnIndex(DatabaseConstants.COLUMN_RECOMMANDATION_UID)));
		r.setEntityName(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_ENTITY)));
		r.setCategoryName(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY)));
		r.setDescription(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_CONTENT)));
		r.setUpdateTime(c.getLong(c.getColumnIndex(DatabaseConstants.COLUMN_DATE)));
		r.setPhraiseNum(c.getInt(c.getColumnIndex(DatabaseConstants.COLUMN_PRAISE_NUM)));
		r.setCommentNum(c.getInt(c.getColumnIndex(DatabaseConstants.COLUMN_COMMENT_NUM)));
//		r.setUserName(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_USER)));

		return r;
	}
	
	private ContentValues getAppContentValues(Recommendation r)
	{
		ContentValues cv = new ContentValues();

		cv.put(DatabaseConstants.COLUMN_RECOMMANDATION_UID, r.getId());
		cv.put(DatabaseConstants.COLUMN_ENTITY, r.getEntityName());
		cv.put(DatabaseConstants.COLUMN_CATEGORY, r.getCategoryName());
		cv.put(DatabaseConstants.COLUMN_CONTENT, r.getDescription());
		cv.put(DatabaseConstants.COLUMN_DATE, r.getUpdateTime());
		cv.put(DatabaseConstants.COLUMN_USER, r.getUser().getName());
		cv.put(DatabaseConstants.COLUMN_PRAISE_NUM, r.getPhraiseNum());
		cv.put(DatabaseConstants.COLUMN_COMMENT_NUM, r.getCommentNum());

		return cv;
	}
	


}
