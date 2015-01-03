package com.wanxiang.recommandationapp.ui.base.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AbsExpandableSelectorAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected SparseArray<T> mDataSet = new SparseArray<T>();
	protected int[] mResource;
	protected LayoutInflater mInflater;

	public AbsExpandableSelectorAdapter(Context context, int... resource) {
		super();
		this.mContext = context;
		// this.mDataSet = dataSet;
		this.mResource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setDataSet(SparseArray<T> dataSet) {
		mDataSet = dataSet;
	}

	public SparseArray<T> getDataSet() {
		return mDataSet;
	}

	@Override
	public int getCount() {
		if (mDataSet == null) {
			return 0;
		}
		return mDataSet.size();
	}

	@Override
	public T getItem(int position) {
		if (mDataSet == null) {
			return null;
		}
		return mDataSet.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	public int getViewResource(int position) {
		return mResource[0];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(getViewResource(position), parent,
					false);
		}
		return convertView;
	}
}
