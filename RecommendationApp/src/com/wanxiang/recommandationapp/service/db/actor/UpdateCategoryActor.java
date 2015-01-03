package com.wanxiang.recommandationapp.service.db.actor;

import com.wanxiang.recommandationapp.controller.FusionActor;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.DatabaseConstants;
import com.wanxiang.recommandationapp.service.db.manager.ICategoryManager;
import com.wanxiang.recommandationapp.service.db.manager.impl.CategoryManager;

public class UpdateCategoryActor extends FusionActor {

	public UpdateCategoryActor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processFusionMessage(FusionMessage msg) {
		if (msg != null && msg.getParam(DatabaseConstants.MESSAGE_UPDATE) != null) {
			int request = (Integer)msg.getParam(DatabaseConstants.MESSAGE_UPDATE);
			if (request == DatabaseConstants.MESSAGE_UPDATE_FAVORITE) {
				long categoryId  = (Long)msg.getParam(DatabaseConstants.COLUMN_CATEGORY_UID);
				ICategoryManager catMan = new CategoryManager(context);
				catMan.markAsFavorite(categoryId, true);
			}
			return true;
		}
		return false;
	}

}
