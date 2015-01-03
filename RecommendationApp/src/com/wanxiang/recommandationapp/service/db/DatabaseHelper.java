package com.wanxiang.recommandationapp.service.db;

import java.io.File;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wanxiang.recommandationapp.service.db.bean.CategoryDataBean;
import com.wanxiang.recommandationapp.service.db.bean.RecommendationDataBean;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	public static boolean sHasInited;

	private static Context mContext;
	private static final String DATABASE_NAME = "wanxiang.db";
	private static final int DATABASE_VERSION = 1;
	private static String DATABASE_PATH = Environment
			.getExternalStorageDirectory() + "/" + DATABASE_NAME;

	private Dao<CategoryDataBean, Integer> mCategoryDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
		init();
	}

	private void init() {
		try {

			File f = new File(DATABASE_PATH);
			if (!f.exists()) {
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
						DATABASE_PATH, null);
				onCreate(db);
				db.close();
			}
		} catch (Exception e) {
		}
		sHasInited = true;

	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(connectionSource,
					RecommendationDataBean.class);
			TableUtils.createTable(connectionSource, CategoryDataBean.class);
		} catch (java.sql.SQLException e) {
			Log.e("wanxiang", "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {

	}

	public Dao<CategoryDataBean, Integer> getCategoryDao()
			throws SQLException {
		if (this.mCategoryDao == null) {
			mCategoryDao = getDao(CategoryDataBean.class);
		}
		return mCategoryDao;
	}
	
	@Override  
	public void close() {  
	    super.close();  
	}  

}
