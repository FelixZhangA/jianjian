package com.wanxiang.recommandationapp.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.cache.CacheManager;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.CategoryAdapter;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryData;
import com.wanxiang.recommandationapp.service.category.CategoryMessage;
import com.wanxiang.recommandationapp.service.publish.PublishRecMessage;
import com.wanxiang.recommandationapp.ui.OnFragmentChangeListener;
import com.wanxiang.recommandationapp.util.AppConstants;

public class ChooseCategoryFragment extends Fragment {

	private Button mBtnBack;
	private Button mBtnNext;
	private OnFragmentChangeListener mListener;
	private ListView mLvParent;
	private ListView mLvChildren;

	private ArrayList<Category> mRecentCategoryList = new ArrayList<Category>();
	private ArrayList<Category> mLikeCategoryList = new ArrayList<Category>();
	private ArrayList<Category> mAllCategoryList = new ArrayList<Category>();

	// private ArrayList<ChooseCategoryItemView> mCategoryItemViewList = new
	// ArrayList<ChooseCategoryItemView>();
	// private ArrayList<ChooseCategoryItemView> mRecentItemViewList = new
	// ArrayList<ChooseCategoryItemView>();

	private Category mSelectedCategory;
	private int mCurrentParent = 0;
	private AbstractRecommendation mRec;

	public ChooseCategoryFragment(OnFragmentChangeListener listener,
			AbstractRecommendation aRec, Category category) {
		mListener = listener;
		mRec = aRec;
		mSelectedCategory = category;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_choose_category, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBtnBack = (Button) view.findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.swichFragment(1, null);
			}
		});

		mBtnNext = (Button) view.findViewById(R.id.btn_next);
		mBtnNext.setText(getActivity().getString(R.string.publish_rec));
		mBtnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSelectedCategory == null) {
					Toast.makeText(getActivity(), "频道不能为空~", Toast.LENGTH_LONG)
							.show();
					return;
				}
				publishRec();

			}
		});

		mRecentCategoryList = AppPrefs.getInstance(getActivity())
				.getRecentUsedCategory();
		mLvParent = (ListView) view.findViewById(R.id.lv_parent);
		mLvChildren = (ListView) view.findViewById(R.id.lv_child);
		setupCategoryUI();
		startQuery();

	}

	protected void publishRec() {
		// 发推荐
		if (mRec instanceof Recommendation) {
			String entity = ((Recommendation) mRec).getEntityName();
			long entityId = ((Recommendation) mRec).getEntityId();

			long category = mSelectedCategory.getCagetoryId();
			String content = ((Recommendation) mRec).getDescription();
			long needId = ((Recommendation) mRec).getNeedId();
			PublishRecMessage message = new PublishRecMessage(
					HTTP_TYPE.HTTP_TYPE_POST);
			message.setParam(AppConstants.HEADER_TOKEN,
					AppPrefs.getInstance(getActivity()).getSessionId());
			message.addParams(AppConstants.RESPONSE_HEADER_CATEGORY_ID,
					String.valueOf(category));
			message.setParam(AppConstants.HEADER_ENTITY, entity);
			if (entityId > 0) {
				// 在实体列表里发表推荐，自动带入entityId
				message.setParam(AppConstants.RESPONSE_HEADER_ENTITY_ID,
						entityId);

			}
			message.setParam(AppConstants.HEADER_DESCRIPTION, content);
			message.setParam(AppConstants.HEADER_NEEDID, String.valueOf(needId));
			message.addHeader(AppConstants.HEADER_IMEI,
					AppPrefs.getInstance(getActivity()).getIMEI());
			message.setFusionCallBack(new FusionCallBack() {

				@Override
				public void onFinish(FusionMessage msg) {
					super.onFinish(msg);
					int ret = (Integer) msg.getResponseData();
					if (ret == 0) {
						Toast.makeText(getActivity(), "发布推荐成功",
								Toast.LENGTH_LONG).show();
						insertRecentCategory(mSelectedCategory);
						mListener.swichFragment(0, null);

					} else {
						Toast.makeText(getActivity(), "发布推荐失败",
								Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailed(FusionMessage msg) {
					// TODO Auto-generated method stub
					super.onFailed(msg);
				}

			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		} else {
			String desc = ((AskRecommendation) mRec).getDescription();
			long category = mSelectedCategory.getCagetoryId();

			PublishRecMessage message = new PublishRecMessage(
					HTTP_TYPE.HTTP_TYPE_POST, 2);
			message.setParam(AppConstants.HEADER_TOKEN,
					AppPrefs.getInstance(getActivity()).getSessionId());
			message.setParam(AppConstants.RESPONSE_HEADER_TYPE,
					String.valueOf(2));
			message.setParam(AppConstants.RESPONSE_HEADER_CATEGORY_ID,
					String.valueOf(category));
			message.setParam(AppConstants.HEADER_DESCRIPTION, desc);
			message.addHeader(AppConstants.HEADER_IMEI,
					AppPrefs.getInstance(getActivity()).getIMEI());
			message.setFusionCallBack(new FusionCallBack() {

				@Override
				public void onFinish(FusionMessage msg) {
					super.onFinish(msg);
					int ret = (Integer) msg.getResponseData();
					if (ret == 0) {
						Toast.makeText(getActivity(), "发布求推荐成功",
								Toast.LENGTH_LONG).show();
						mListener.swichFragment(0, null);
					} else {
						Toast.makeText(getActivity(), "发布求推荐失败",
								Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailed(FusionMessage msg) {
					// TODO Auto-generated method stub
					super.onFailed(msg);
				}

			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		}
	}

	private void startQuery() {
		CategoryData data = (CategoryData) CacheManager.loadCache(getActivity(), AppConstants.CACHE_CATEGORY_DATA);
		if (data != null) {
			handleCategoryData(data);
		} else {
			
			CategoryMessage message = new CategoryMessage(
					HTTP_TYPE.HTTP_TYPE_GET);
			message.setContext(getActivity());
			message.setParam(AppConstants.HEADER_TOKEN,
					AppPrefs.getInstance(getActivity()).getSessionId());
			message.setParam(AppConstants.REQUEST_HEADER_CATEGORY_DEPTH,
					String.valueOf(2));
			message.setFusionCallBack(new FusionCallBack() {

				@Override
				public void onFinish(FusionMessage msg) {
					super.onFinish(msg);
					CategoryData data = (CategoryData) msg.getResponseData();
					handleCategoryData(data);
				}

				@Override
				public void onFailed(FusionMessage msg) {
					super.onFailed(msg);
				}

			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		}
	}

	private void handleCategoryData(CategoryData cat) {
		if (cat != null) {
			mLikeCategoryList = cat.getListLike();
			mAllCategoryList = cat.getListOther();
		}
	}

	// private void clearSelection() {
	//
	// for (ChooseCategoryItemView item : mRecentItemViewList) {
	// item.setItemChecked(false);
	// }
	// for (ChooseCategoryItemView item : mCategoryItemViewList) {
	// item.setItemChecked(false);
	// }
	//
	// }

	private void insertRecentCategory(Category category) {
		if (mRecentCategoryList == null) {
			mRecentCategoryList = new ArrayList<Category>();
		}
		for (Category cat : mRecentCategoryList) {
			if (cat.getCagetoryId() == category.getCagetoryId()) {
				return;
			}
		}
		mRecentCategoryList.add(category);
		AppPrefs.getInstance(getActivity()).setRecentUsedCategory(
				mRecentCategoryList);

	}

	private void setSelectedCategory() {
		// if (mSelectedCategory != null) {
		// for (ChooseCategoryItemView view : mCategoryItemViewList) {
		// if (TextUtils.equals(mSelectedCategory.getCategoryName(),
		// view.getCategoryName())) {
		// view.setItemChecked(true);
		// break;
		// }
		// }
		//
		// }
	}

	private void setupCategoryUI() {
		ArrayList<Category> data = new ArrayList<Category>();
		data.add(new Category("最近使用"));
		data.add(new Category("我加入的"));
		data.add(new Category("其他圈子"));
		CategoryAdapter adapter = new CategoryAdapter(getActivity(), data);
		mLvParent.setAdapter(adapter);
		mLvChildren.setAdapter(new CategoryAdapter(getActivity(),
				mRecentCategoryList));
		mLvChildren.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		mLvChildren.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (mCurrentParent) {
				case 0:
					mSelectedCategory = mRecentCategoryList.get(position);
					break;
				case 1:
					mSelectedCategory = mLikeCategoryList.get(position);
					break;
				case 2:
					mSelectedCategory = mAllCategoryList.get(position);
					break;
				}
				CategoryAdapter.setSelection(position);
				mLvChildren.invalidate();
			}
		});
		mLvParent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CategoryAdapter childAdapter = null;
				switch (position) {
				case 0:
					childAdapter = new CategoryAdapter(getActivity(),
							mRecentCategoryList, true);
					break;
				case 1:
					childAdapter = new CategoryAdapter(getActivity(),
							mLikeCategoryList, true);
					break;
				case 2:
					childAdapter = new CategoryAdapter(getActivity(),
							mAllCategoryList, true);
					break;
				default:
					childAdapter = new CategoryAdapter(getActivity(),
							mRecentCategoryList);
					break;
				}
				mCurrentParent = position;
				CategoryAdapter.setSelection(-1);

				mLvChildren.setAdapter(childAdapter);

			}
		});

	}
}
