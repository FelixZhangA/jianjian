package com.wanxiang.recommandationapp.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.model.Category;

public class CategoryCursorAdapter extends CursorAdapter
{

	private CategoryDao mDao;
	public CategoryCursorAdapter( Context context, Cursor c, boolean autoRequery )
	{
		super(context, c, autoRequery);
		mDao = CategoryDao.getAdapterObject(context);
	}

	@Override
	public void bindView( View arg0, Context arg1, Cursor arg2 )
	{
		ViewHolder holder = (ViewHolder)arg0.getTag();
		if (holder != null)
		{
			holder.tvCategory.setText(arg2.getString(arg2.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY)));
//			holder.tvCategory.setBackgroundColor(arg2.getInt(arg2.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY_COLOR)));
		}
	}

	@Override
	public View newView( Context arg0, Cursor arg1, ViewGroup arg2 )
	{
		final View view = LayoutInflater.from(mContext).inflate(
                R.layout.layout_category_item, null);
		ViewHolder holder = new ViewHolder();
		holder.tvCategory = (TextView) view.findViewById(R.id.tv_category);
		
		view.setTag(holder);
		return view;
	}
	
	@Override
	public Category getItem(int position)
	{
		if (position > -1)
		{
			mCursor.moveToPosition(position);
			return mDao.getCategoryFromCursor(mCursor);
		}
		return null;
	}
	
	private class ViewHolder
	{
		TextView tvCategory;
	}

}
