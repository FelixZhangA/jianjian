package com.wanxiang.recommandationapp.ui;


import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianjianapp.R;


public class ChooseCategoryItemView extends RelativeLayout
{

	private Context		mContext;
	private TextView	mCategoryName;
	private ImageView	mCheckedView;

	public ChooseCategoryItemView( Context context )
	{
		super(context);
		((LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_choose_category_item, this);
		initUI();

	}

	private void initUI()
	{
		mCategoryName = (TextView)findViewById(R.id.tv_category_item);
		mCheckedView = (ImageView)findViewById(R.id.img_checked);
	}

	public ChooseCategoryItemView( Context context, AttributeSet attributeSet )
	{
		super(context, attributeSet);
		this.mContext = context;
		((LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_choose_category_item, this);
		initUI();
	}

	public void setCategoryName( String name )
	{
		mCategoryName.setText(name);
	}

	public String getCategoryName()
	{
		return mCategoryName.getText().toString();
	}
	public void setItemChecked( boolean isChecked )
	{
		mCheckedView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
	}
}
