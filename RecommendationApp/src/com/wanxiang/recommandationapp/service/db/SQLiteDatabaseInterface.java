package com.wanxiang.recommandationapp.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public interface SQLiteDatabaseInterface
{

	void onCreateTable(SQLiteDatabase db ,Context context);

	void onUpgradeDataBase(SQLiteDatabase db, int oldVersion, int newVersion , Context context);
}
