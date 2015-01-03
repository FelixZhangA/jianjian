package com.wanxiang.recommandationapp.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.CategoryAdapter;
import com.wanxiang.recommandationapp.data.CategoryDetailsAdapter;
import com.wanxiang.recommandationapp.data.DatabaseConstants;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryMessage;
import com.wanxiang.recommandationapp.ui.CategoryDetailsActivity;
import com.wanxiang.recommandationapp.util.AppConstants;

public class CategoryListFragment extends Fragment implements
		OnItemClickListener, OnClickListener, OnItemSelectedListener {
	private ListView mCategoryList;
	private ListView mCagetoryDetailList;
	private int mCurrentPos = 0;
	private Button mJoinBtn;
	private ArrayList<Category> mParentList = new ArrayList<Category>();
	private ArrayList<Category> mDetailList = new ArrayList<Category>();
	private CategoryDetailsAdapter mAdapter;
	private OnChannelFavioratedListener mListener = new OnChannelFavioratedListener() {

		@Override
		public void onChannelFaviorated() {
			startQuery();
		}

	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_category_fragment, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mCategoryList = (ListView) view.findViewById(R.id.lst_category);
		mCategoryList.setOnItemClickListener(this);
		mCategoryList.setOnItemSelectedListener(this);
		mCagetoryDetailList = (ListView) view.findViewById(R.id.lst_detail);
		mCagetoryDetailList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						CategoryDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(AppConstants.INTENT_CATEGORY,
						mDetailList.get(arg2));
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}

		});
		startQuery();

	}

	private void startQuery() {
		mParentList.clear();
		final AppPrefs appPrefs = AppPrefs.getInstance(getActivity());
		long categoryUpdateTime = appPrefs.getCategoryUpdateTime();
		if (System.currentTimeMillis() - categoryUpdateTime > DatabaseConstants.TWENTY_FOUR_HOUR) { // 取服务器数据

			CategoryMessage message = new CategoryMessage(
					HTTP_TYPE.HTTP_TYPE_GET);

			message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
			message.setParam(AppConstants.REQUEST_HEADER_CATEGORY_DEPTH,
					String.valueOf(2));
			message.addHeader(AppConstants.HEADER_IMEI,
					AppPrefs.getInstance(getActivity()).getIMEI());
			message.setFusionCallBack(new FusionCallBack() {

				@Override
				public void onFinish(FusionMessage msg) {
					super.onFinish(msg);
					handleFusionResponse(msg);
					// 更新数据库
					FusionMessage addCategoryMsg = new FusionMessage(
							"dbService", "addCategory");
					addCategoryMsg.setParam(
							DatabaseConstants.MESSAGE_CATEGORY_LIST,
							getAllCategory());
					addCategoryMsg.setFusionCallBack(new FusionCallBack() {

						@Override
						public void onFinish(FusionMessage msg) {
							super.onFinish(msg);
							// 更新日期
							appPrefs.setCategoryUpdateTime(System
									.currentTimeMillis());
						}

					});
					FusionBus.getInstance(getActivity()).sendMessage(
							addCategoryMsg);
				}

				@Override
				public void onFailed(FusionMessage msg) {
					super.onFailed(msg);
				}

			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		} else // 取缓存数据
		{
			FusionMessage message = new FusionMessage("dbService",
					"queryCategory");
			message.setParam(DatabaseConstants.MESSAGE_QUERY,
					DatabaseConstants.MESSAGE_QUERY_PARENT);
			message.setFusionCallBack(new FusionCallBack() {

				@Override
				public void onFinish(FusionMessage msg) {
					super.onFinish(msg);
					handleFusionResponse(msg);
				}

				@Override
				public void onFailed(FusionMessage msg) {
					super.onFailed(msg);
					Log.d("wanxiang", msg.getErrorDesp());
				}
			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.getId() == R.id.lst_category) {
			View lastClickView = arg0.getChildAt(mCurrentPos);
			if (lastClickView != null) {
				lastClickView
						.setBackgroundResource(R.drawable.category_item_background);
			}
			arg1.setBackgroundResource(R.drawable.category_item_background_selected);
			mCurrentPos = arg2;
			mDetailList = mParentList.get(arg2).getChildrenList();
			if (mDetailList != null) {
				if (mAdapter == null) {
					mAdapter = new CategoryDetailsAdapter(getActivity(),
							mDetailList);
					mAdapter.setOnCategoryFavoriteListener(mListener);
					mCagetoryDetailList.setAdapter(mAdapter);
				}
				mAdapter.setCategoryList(mDetailList);
				mAdapter.notifyDataSetChanged();

			}
		}
	}

	@Override
	public void onClick(View v) {

	}

	public interface OnChannelFavioratedListener {
		public void onChannelFaviorated();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg0.getId() == R.id.lst_category) {
			View lastClickView = arg0.getChildAt(mCurrentPos);
			if (lastClickView != null) {
				lastClickView
						.setBackgroundResource(R.drawable.category_item_background);
			}
			arg1.setBackgroundResource(R.drawable.category_item_background_selected);
			mCurrentPos = arg2;
			mDetailList = mParentList.get(arg2).getChildrenList();
			if (mDetailList != null) {
				if (mAdapter == null) {
					mAdapter = new CategoryDetailsAdapter(getActivity(),
							mDetailList);
					mAdapter.setOnCategoryFavoriteListener(mListener);
					mCagetoryDetailList.setAdapter(mAdapter);
				}
				mAdapter.setCategoryList(mDetailList);
				mAdapter.notifyDataSetChanged();

			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void handleFusionResponse(FusionMessage message) {
		ArrayList<Category> cat = (ArrayList<Category>) message
				.getResponseData();
		if (cat != null && cat.size() > 0) {
			mParentList.addAll(cat);
			CategoryAdapter adapter = new CategoryAdapter(getActivity(), cat);

			mCategoryList.setAdapter(adapter);
			mCategoryList.requestFocusFromTouch();
			mCategoryList.setItemChecked(0, true);
			mDetailList = mParentList.get(0).getChildrenList();
			if (mDetailList != null && mDetailList.size() > 0) {
				if (mAdapter == null) {
					for (Category catFav : mDetailList) {
						catFav.setFavor(true);
					}
					mAdapter = new CategoryDetailsAdapter(getActivity(),
							mDetailList);
					mCagetoryDetailList.setAdapter(mAdapter);
					mAdapter.setOnCategoryFavoriteListener(mListener);
				}
				mAdapter.setCategoryList(mDetailList);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private ArrayList<Category> getAllCategory() {
		ArrayList<Category> ret = new ArrayList<Category>();
		// 我喜欢的 我不喜欢的 全部
		ret.addAll(mParentList);

		// 我喜欢的
		ArrayList<Category> favoriteList = mParentList.get(0).getChildrenList();

		// 全部
		ArrayList<Category> all = mParentList.get(2).getChildrenList();

		for (Category cat : all) {
			for (Category tmp : favoriteList) {
				if (tmp.getCagetoryId() == cat.getCagetoryId()) {
					cat.setFavor(true);
					break;
				}
			}
			ret.add(cat);
		}
		return ret;
	}
}
