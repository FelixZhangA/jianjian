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

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.CategoryAdapter;
import com.wanxiang.recommandationapp.data.CategoryDetailsAdapter;
import com.wanxiang.recommandationapp.data.DatabaseConstants;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryData;
import com.wanxiang.recommandationapp.service.category.CategoryMessage;
import com.wanxiang.recommandationapp.ui.CategoryDetailsActivity;
import com.wanxiang.recommandationapp.ui.widget.ListViewForScrollView;
import com.wanxiang.recommandationapp.util.AppConstants;

public class CategoryListFragment extends Fragment {
	private ListViewForScrollView mLikeList;
	private ListViewForScrollView mOtherList;
	private int mCurrentPos = 0;
	private Button mJoinBtn;
	private ArrayList<Category> mCategoryLikeList = new ArrayList<Category>();
	private ArrayList<Category> mCategoryOtherList = new ArrayList<Category>();
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
		mLikeList = (ListViewForScrollView) view.findViewById(R.id.lst_like);
		mLikeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						CategoryDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(AppConstants.INTENT_CATEGORY,
						mCategoryLikeList.get(arg2));
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}

		});
		mOtherList = (ListViewForScrollView) view.findViewById(R.id.lst_other);
		mOtherList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						CategoryDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(AppConstants.INTENT_CATEGORY,
						mCategoryOtherList.get(arg2));
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}

		});
		startQuery();

	}

	private void startQuery() {
		mCategoryLikeList.clear();
		final AppPrefs appPrefs = AppPrefs.getInstance(getActivity());
		long categoryUpdateTime = appPrefs.getCategoryUpdateTime();
		if (true) {// (System.currentTimeMillis() - categoryUpdateTime >
					// DatabaseConstants.TWENTY_FOUR_HOUR) {

			CategoryMessage message = new CategoryMessage(
					HTTP_TYPE.HTTP_TYPE_GET);

			message.setParam(AppConstants.HEADER_TOKEN,
					AppPrefs.getInstance(getActivity()).getSessionId());
			message.setFusionCallBack(new FusionCallBack() {

				@Override
				public void onFinish(FusionMessage msg) {
					super.onFinish(msg);
					handleFusionResponse(msg);
					FusionMessage addCategoryMsg = new FusionMessage(
							"dbService", "addCategory");
					addCategoryMsg.setParam(
							DatabaseConstants.MESSAGE_CATEGORY_LIST,
							getAllCategory());
					addCategoryMsg.setFusionCallBack(new FusionCallBack() {

						@Override
						public void onFinish(FusionMessage msg) {
							super.onFinish(msg);
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
		} else {
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

	public interface OnChannelFavioratedListener {
		public void onChannelFaviorated();
	}

	private void handleFusionResponse(FusionMessage message) {
		CategoryData cat = (CategoryData) message.getResponseData();
		if (cat != null) {
			mCategoryLikeList.clear();
			mCategoryLikeList = cat.getListLike();
			CategoryAdapter adapter = new CategoryAdapter(getActivity(),
					mCategoryLikeList);

			mLikeList.setAdapter(adapter);

			mCategoryOtherList.clear();
			mCategoryOtherList = cat.getListOther();
			if (mAdapter == null) {
				mAdapter = new CategoryDetailsAdapter(getActivity(),
						mCategoryOtherList);
				mOtherList.setAdapter(mAdapter);
				mAdapter.setOnCategoryFavoriteListener(mListener);
			}
			mAdapter.setCategoryList(mCategoryOtherList);
			mAdapter.notifyDataSetChanged();

		}
	}

	private ArrayList<Category> getAllCategory() {
		ArrayList<Category> ret = new ArrayList<Category>();
		// 鎴戝枩娆㈢殑 鎴戜笉鍠滄鐨� 鍏ㄩ儴
		ret.addAll(mCategoryLikeList);

		// 鎴戝枩娆㈢殑
		ArrayList<Category> favoriteList = mCategoryLikeList.get(0)
				.getChildrenList();

		// 鍏ㄩ儴
		ArrayList<Category> all = mCategoryLikeList.get(2).getChildrenList();

		// for (Category cat : all) {
		// for (Category tmp : favoriteList) {
		// if (tmp.getCagetoryId() == cat.getCagetoryId()) {
		// cat.setFavor(true);
		// break;
		// }
		// }
		// ret.add(cat);
		// }
		return ret;
	}
}
