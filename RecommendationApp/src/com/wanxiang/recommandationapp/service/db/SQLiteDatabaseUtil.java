package com.wanxiang.recommandationapp.service.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDatabaseUtil extends SQLiteOpenHelper
{
	private SQLiteDatabaseInterface SQLiteDatabaseInterfacecallback;
	
	private Context context;

	public SQLiteDatabaseUtil(Context context, String dbName, int dbVersion, SQLiteDatabaseInterface SQLiteDatabaseInterfacecallback)
	{
		super(context, dbName, null, dbVersion);
		this.SQLiteDatabaseInterfacecallback = SQLiteDatabaseInterfacecallback;
		this.context = context;
	}

	public synchronized SQLiteDatabase getDatabase(boolean writable)
	{
		return writable ? getWritableDatabase() : getReadableDatabase();
	}

	@Override
	public synchronized void close()
	{
		super.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion)
	{
		SQLiteDatabaseInterfacecallback.onUpgradeDataBase(db2, oldVersion, newVersion , context);

	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		SQLiteDatabaseInterfacecallback.onCreateTable(db ,context);

	}

	public synchronized List<String> getColumns(SQLiteDatabase database, String table)
	{
		Cursor cursor = database.rawQuery("PRAGMA TABLE_INFO(" + table + ")", null);
		List<String> rows = new ArrayList<String>();
		try
		{
			if (cursor != null && cursor.moveToFirst())
			{
				do
				{
					rows.add(cursor.getString(1));
				}
				while (cursor.moveToNext());
			}
			return rows;
		}
		finally
		{
			safeClose(cursor);
		}
	}
	
	private void safeClose(Cursor cursor)
	{
		if (cursor != null && !cursor.isClosed())
		{
			try
			{
				cursor.close();
			}
			catch (Exception e)
			{
				Log.e("WANXIANG", "Error closing " + cursor.getClass().getSimpleName(), e);
			}
		}
	}

}
