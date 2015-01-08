package com.wanxiang.recommandationapp.service.category;

import java.io.Serializable;
import java.util.ArrayList;

import com.wanxiang.recommandationapp.model.Category;

public class CategoryData implements Serializable {
	private ArrayList<Category> listLike;
	private ArrayList<Category> listOther;

	public ArrayList<Category> getListLike() {
		return listLike;
	}

	public void setListLike(ArrayList<Category> listLike) {
		this.listLike = listLike;
	}

	public ArrayList<Category> getListOther() {
		return listOther;
	}

	public void setListOther(ArrayList<Category> listOther) {
		this.listOther = listOther;
	}

}
