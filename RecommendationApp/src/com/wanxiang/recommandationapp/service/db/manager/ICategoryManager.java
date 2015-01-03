package com.wanxiang.recommandationapp.service.db.manager;

import java.util.ArrayList;

import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.service.db.bean.CategoryDataBean;

public interface ICategoryManager {
	public ArrayList<Category> getFavoriteCategory(boolean isFavorite);
	public ArrayList<Category> getRecentCategory(boolean isRecent);

	public void add(ArrayList<CategoryDataBean> category);
	
	public void markAsRecent(long categoryId);
	
	public void markAsFavorite(long category, boolean isFavorite);

	public void release();

	public void clearAll();
	
	public ArrayList<Category> getParentCategory();
	
	public Category getCategoryById(long category_id);
}
  