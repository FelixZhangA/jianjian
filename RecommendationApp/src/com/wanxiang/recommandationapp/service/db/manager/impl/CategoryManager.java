package com.wanxiang.recommandationapp.service.db.manager.impl;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.service.db.DatabaseHelper;
import com.wanxiang.recommandationapp.service.db.bean.CategoryDataBean;
import com.wanxiang.recommandationapp.service.db.manager.ICategoryManager;

public class CategoryManager implements ICategoryManager {

	private Context mContext;
	private DatabaseHelper databaseHelper;
	private Dao<CategoryDataBean, Integer> mCategoryDao;

	public CategoryManager(Context context) {
		this.mContext = context;
		try {
			mCategoryDao = getHelper().getCategoryDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(mContext,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}


	@Override
	public ArrayList<Category> getParentCategory() {

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = mCategoryDao
					.queryRaw("select * from category_info where category_id < 1000");
			return getCategory(rawResults);

		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;

	}

	@Override
	public Category getCategoryById(long category_id) {

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = mCategoryDao
					.queryRaw("select * from category_info where category_id ="
							+ category_id);
			ArrayList<Category> list = getCategory(rawResults);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public ArrayList<Category> getFavoriteCategory(boolean isFavorite) {

		int favorite = isFavorite ? 1 : 0;
		GenericRawResults<String[]> rawResults;
		try {
			rawResults = this.mCategoryDao
					.queryRaw("select * from category_info where favorite= '"
							+ favorite + "'");

			return getCategory(rawResults);
		} catch (SQLException e) {
			e.printStackTrace();
		} 

		return null;
	}

	@Override
	public ArrayList<Category> getRecentCategory(boolean isRecent) {
		int recent = isRecent ? 1 : 0;
		GenericRawResults<String[]> rawResults;
		try {
			rawResults = this.mCategoryDao
					.queryRaw("select * from category_info where recent= '"
							+ recent + "'");

			return getCategory(rawResults);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			release();
		}

		return null;
	}

	@Override
	public void add(ArrayList<CategoryDataBean> category) {
		clearAll();

		for (CategoryDataBean tmp : category) {
			try {
				mCategoryDao.createIfNotExists(tmp);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void markAsRecent(long categoryId) {

		try {
			mCategoryDao
					.updateRaw(
							"UPDATE category_info SET recent = 1 WHERE category_id = ?",
							String.valueOf(categoryId));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			release();
		}
	}

	@Override
	public void markAsFavorite(long categoryId, boolean isFavorite) {
		try {
			int favorite = isFavorite ? 1 : 0;
			mCategoryDao.updateRaw("UPDATE category_info SET favorite = "
					+ favorite + " WHERE category_id = ?",
					String.valueOf(categoryId));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release();
		}
	}

	@Override
	public void release() {
//		if (databaseHelper != null) {
//			databaseHelper.close();
//			databaseHelper = null;
//		}

	}

	@Override
	public void clearAll() {
		try {
			mCategoryDao.executeRaw("delete from category_info");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private ArrayList<Category> getCategory(
			GenericRawResults<String[]> rawResults) {
		ArrayList<Category> ret = new ArrayList<Category>();
		for (String[] list : rawResults) {
			if (list != null && list.length > 0) {
				Category cat = new Category();
				String[] columns = rawResults.getColumnNames();
				int i = 0;
				for (String tmp : columns) {
					if (TextUtils.equals("category_id", tmp)) {
						cat.setCategoryId(Long.parseLong(list[i]));
					} else if (TextUtils.equals("category_name", tmp)) {
						cat.setCategoryName(list[i]);
					} else if (TextUtils.equals("favorite", tmp)) {
						String favorite = list[i];
						cat.setFavor(favorite.equals("0") ? false : true);
					} else if (TextUtils.equals("children", tmp)) {
						if (!TextUtils.isEmpty(list[i])) {
							ArrayList<Category> children = new ArrayList<Category>();
							String[] arrChildrenIds = list[i].split(";");
							for (String id : arrChildrenIds) {
								Category child = getCategoryById(Long.parseLong(id));
								children.add(child);
							}
							cat.setChildrenList(children);
						}
					} else if (TextUtils.equals("recent", tmp)) {
						cat.setRecent(Boolean.parseBoolean(list[i]));
					} else if (TextUtils.equals("parent_id", tmp)) {
						cat.setParentId(Long.parseLong(list[i]));
					}
					i++;
				}
				ret.add(cat);
			}
		}
		return ret;
	}
}
