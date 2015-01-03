package com.wanxiang.recommandationapp.service.db.actor;

import java.util.ArrayList;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.controller.FusionActor;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.DatabaseConstants;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.service.db.bean.CategoryDataBean;
import com.wanxiang.recommandationapp.service.db.manager.ICategoryManager;
import com.wanxiang.recommandationapp.service.db.manager.impl.CategoryManager;

public class QueryCategoryActor extends FusionActor {

	public QueryCategoryActor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processFusionMessage(FusionMessage msg) {
		if (msg != null
				&& msg.getParam(DatabaseConstants.MESSAGE_QUERY) != null) {
			ICategoryManager catMan = new CategoryManager(context);
			int request = (Integer) msg
					.getParam(DatabaseConstants.MESSAGE_QUERY);
			if (request == DatabaseConstants.MESSAGE_QUERY_PARENT) {

				ArrayList<Category> result = catMan.getParentCategory();
				msg.setResponseData(result);
				return true;

			} else if (request == DatabaseConstants.MESSAGE_QUERY_RECENT) {

			}

			return true;
		}
		return false;
	}

}
