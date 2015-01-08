package com.wanxiang.recommandationapp.data;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.model.Category;


public class CategoryAdapter extends BaseAdapter
{

	private Context	mContext;
	private ArrayList<Category>	mCategoryList;

	public CategoryAdapter( Context context, ArrayList<Category> categoryList )
	{
		mContext = context;
		mCategoryList = categoryList;
	}

	@Override
	public int getCount()
	{
		return mCategoryList.size();
	}

	@Override
	public Object getItem( int position )
	{
		return mCategoryList.get(position);
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
			
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder)convertView.getTag();
		holder.tvCategory.setText(mCategoryList.get(position).getCategoryName());
		return convertView;
	}
	private class ViewHolder
	{
		TextView tvCategory;

	}
	
}
