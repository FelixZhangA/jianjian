package com.wanxiang.recommandationapp.service.db.manager;

import java.util.ArrayList;

import com.wanxiang.recommandationapp.service.db.bean.RecommendationDataBean;

public interface IRecommendationManager {

	public ArrayList<RecommendationDataBean> getRecommendations(long entityID);

	public void add(ArrayList<RecommendationDataBean> recList);

	public void release();

	public void clearAll();
}
