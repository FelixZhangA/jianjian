package com.wanxiang.recommandationapp.ui.base.adapter;

import java.util.List;

import android.widget.BaseAdapter;

/**
 * BaseAdapter的一个wrapper
 * @author qinyao.zzw
 *
 * @param <T>
 */
public abstract class AbstractAdapter<T> extends BaseAdapter {

	protected List<T> mData;

	public boolean hasData() {
		return mData != null;
	}

	public void setData(List<T> data) {
		mData = data;
		notifyDataSetChanged();
	}

	public List<T> getData() {
		return mData;
	}

	@Override
	public int getCount() {
		List<T> data = getData();
		return data == null ? 0 : data.size();
	}

	@Override
	public T getItem(int position) {
		List<T> data = getData();
		if (null == data) {
			return null;
		}
		if (position < 0 || position >= data.size()) {
			return null;
		}
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (long) position;
	}

}
