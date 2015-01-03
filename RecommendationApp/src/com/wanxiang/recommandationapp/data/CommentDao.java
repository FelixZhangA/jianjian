package com.wanxiang.recommandationapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.service.db.CursorUtil;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseInterface;
import com.wanxiang.recommandationapp.service.db.SQLiteDatabaseUtil;

public class CommentDao implements SQLiteDatabaseInterface
{
	private final SQLiteDatabaseUtil	mDbHelper;
	
	private static CommentDao	mDao	= null;
	private CommentDao( Context context )
	{
		mDbHelper = new SQLiteDatabaseUtil(context, DatabaseConstants.DATABASE_NAME, DatabaseConstants.version, this);
	}
	

	public static synchronized CommentDao getAdapterObject( Context context )
	{
		if (mDao == null)
		{
			mDao = new CommentDao(context);
		}

		return mDao;
	}

	public boolean insertComment(Comment com)
	{
		boolean ret = false;
		SQLiteDatabase db = null;

		try
		{
			db = mDbHelper.getDatabase(false);
			long row = db.insert(DatabaseConstants.COMMENT_TABLE_NAME, null, getAppContentValues(com));

			if (row != -1)
			{
				ret = true;
			}
		}
		catch (Exception ex)
		{
			Log.e("wanxiang", ex.getMessage());
		}
		finally
		{
			mDbHelper.close();
		}
		return ret;
	}
	
	public Cursor getAllCommentsByIdCursor(long recommandation_id)
	{
		SQLiteDatabase db = null;
		Cursor c = null;
		try
		{
			db = mDbHelper.getDatabase(false);
			c = db.query(DatabaseConstants.COMMENT_TABLE_NAME, null, DatabaseConstants.COLUMN_RECOMMANDATION_UID + "=" + recommandation_id, null, null, null, DatabaseConstants.COLUMN_DATE + " DESC");
			return CursorUtil.createCopy(c);
		}
		finally
		{
			CursorUtil.safeClose(c);
			mDbHelper.close();
		}
	}
	
	private ContentValues getAppContentValues( Comment com )
	{
		ContentValues cv = new ContentValues();
		cv.put(DatabaseConstants.COLUMN_COMMENT_UID, String.valueOf(com.getId()));
		cv.put(DatabaseConstants.COLUMN_RECOMMANDATION_UID, String.valueOf(com.getRecommendationId()));
		cv.put(DatabaseConstants.COLUMN_CONTENT, com.getCommentContent());
		cv.put(DatabaseConstants.COLUMN_USER, com.getUserName());
		cv.put(DatabaseConstants.COLUMN_DATE, String.valueOf(System.currentTimeMillis()));
		return cv;
	}
	
	@Override
	public void onCreateTable( SQLiteDatabase db, Context context )
	{
		String init_comment_table = "Create table " + DatabaseConstants.COMMENT_TABLE_NAME + "(" + DatabaseConstants.COLUMN_APP_ID + " integer," + DatabaseConstants.COLUMN_COMMENT_UID + " varchar(20) primary key," + DatabaseConstants.COLUMN_RECOMMANDATION_UID + " varchar(20)," + DatabaseConstants.COLUMN_CONTENT + " text, " + DatabaseConstants.COLUMN_DATE + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," + DatabaseConstants.COLUMN_USER + " text);";
		db.execSQL(init_comment_table);		
	}

	@Override
	public void onUpgradeDataBase( SQLiteDatabase db, int oldVersion, int newVersion, Context context )
	{
		String destroy_comment_table = "DROP TABLE IF EXISTS " + DatabaseConstants.COMMENT_TABLE_NAME;
		db.execSQL(destroy_comment_table);
		onCreateTable(db, context);
	}

}
