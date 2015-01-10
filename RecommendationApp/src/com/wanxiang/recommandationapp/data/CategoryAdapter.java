package com.wanxiang.recommandationapp.data;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.model.Category;


public class CategoryAdapter extends BaseAdapter
{

	private Context	mContext;
	private ArrayList<Category>	mCategoryList;
	private static int selection = -1;
	private boolean showIcon = false;
	public CategoryAdapter( Context context, ArrayList<Category> categoryList )
	{
		mContext = context;
		mCategoryList = categoryList;
	}
	
	public CategoryAdapter( Context context, ArrayList<Category> categoryList, boolean showIcon )
	{
		mContext = context;
		mCategoryList = categoryList;
		this.showIcon = showIcon;
	}

	@Override
	public int getCount()
	{
		return mCategoryList == null ? 0 : mCategoryList.size();
	}

	@Override
	public Category getItem( int position )
	{
		return mCategoryList == null ? null : mCategoryList.get(position);
	}

	@Override
	public long getItemId( int position )
	{
		return mCategoryList.get(position).getCagetoryId();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(
	                R.layout.layout_category_item, null);
			ViewHolder holder = new ViewHolder();
			holder.tvCategory = (TextView) convertView.findViewById(R.id.tv_category);
			holder.ivChecked = (ImageView) convertView.findViewById(R.id.img_checked);

			convertView.setTag(holder);
		}
		final ViewHolder holder = (ViewHolder)convertView.getTag();
		holder.tvCategory.setText(mCategoryList.get(position).getCategoryName());
		if (selection == position && showIcon) {
			
			holder.ivChecked.setVisibility(View.VISIBLE);
		} else {
			holder.ivChecked.setVisibility(View.GONE);
		}
		return convertView;
	}

	public static void setSelection(int selection) {
		CategoryAdapter.selection = selection;
	}
	private class ViewHolder
	{
		TextView tvCategory;
		ImageView ivChecked;

	}
	
}
