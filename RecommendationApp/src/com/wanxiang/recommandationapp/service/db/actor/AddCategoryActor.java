package com.wanxiang.recommandationapp.service.db.actor;

import java.util.ArrayList;

import com.wanxiang.recommandationapp.controller.FusionActor;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.DatabaseConstants;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.service.db.bean.CategoryDataBean;
import com.wanxiang.recommandationapp.service.db.manager.ICategoryManager;
import com.wanxiang.recommandationapp.service.db.manager.impl.CategoryManager;

public class AddCategoryActor extends FusionActor {

	public AddCategoryActor() {
	}

	@Override
	public boolean processFusionMessage(FusionMessage msg) {

		if (msg != null) {
			ArrayList<Category> list = (ArrayList<Category>) msg
					.getParam(DatabaseConstants.MESSAGE_CATEGORY_LIST);
			if (list != null && list.size() > 0) {
				ICategoryManager manager = new CategoryManager(context);
				ArrayList<CategoryDataBean> beanList = new ArrayList<CategoryDataBean>();
				for (Category cat : list) {
					CategoryDataBean bean = new CategoryDataBean();
					bean.setCategoryId(cat.getCagetoryId());
					bean.setCategoryName(cat.getCategoryName());
					bean.setChildrenList(formateChildrenList(cat
							.getChildrenList()));
					bean.setFavorite(cat.isFavor());
					bean.setRecent(false);
					beanList.add(bean);
				}

				manager.add(beanList);
				return true;

			}
		}
		return false;
	}

	private String formateChildrenList(ArrayList<Category> children) {
		if (children == null || children.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Category cat : children) {
			sb.append(cat.getCagetoryId());
			sb.append(";");
		}
		return sb.substring(0, sb.length() - 1);
	}
}
