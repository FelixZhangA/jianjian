package com.wanxiang.recommandationapp.ui.fragment;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryMessage;
import com.wanxiang.recommandationapp.service.publish.PublishRecMessage;
import com.wanxiang.recommandationapp.ui.ChooseCategoryItemView;
import com.wanxiang.recommandationapp.ui.OnFragmentChangeListener;
import com.wanxiang.recommandationapp.util.AppConstants;


public class ChooseCategoryFragment extends Fragment
{

	private Button								mBtnBack;
	private Button								mBtnNext;
	private OnFragmentChangeListener			mListener;
	private ListView							mCategoryList;
	private ListView							mCagetoryDetailList;
	private LinearLayout						mAllCategory;
	private LinearLayout						mRecentUsedCategory;
	private int									mCurrentPos;

	private ArrayList<Category>					mRecentCategoryList		= new ArrayList<Category>();
	private ArrayList<Category>					mChildList				= new ArrayList<Category>();
	private ArrayList<ChooseCategoryItemView>	mCategoryItemViewList	= new ArrayList<ChooseCategoryItemView>();
	private ArrayList<ChooseCategoryItemView>	mRecentItemViewList		= new ArrayList<ChooseCategoryItemView>();

	private Category							mSelectedCategory;

	private AbstractRecommendation				mRec;
	

	public ChooseCategoryFragment( OnFragmentChangeListener listener, AbstractRecommendation aRec, Category category )
	{
		mListener = listener;
		mRec = aRec;
		mSelectedCategory = category;
	}

	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		return inflater.inflate(R.layout.fragment_choose_category, container, false);
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated(view, savedInstanceState);
		mBtnBack = (Button)view.findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				mListener.swichFragment(1, null);
			}
		});

		mBtnNext = (Button)view.findViewById(R.id.btn_next);
		mBtnNext.setText(getActivity().getString(R.string.publish_rec));
		mBtnNext.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				if (mSelectedCategory == null)
				{
					Toast.makeText(getActivity(), "频道不能为空~", Toast.LENGTH_LONG).show();
					return;
				}
				publishRec();

			}
		});
		mAllCategory = (LinearLayout)view.findViewById(R.id.ll_all_item);

		mRecentUsedCategory = (LinearLayout)view.findViewById(R.id.ll_recent_used_item);
		mRecentCategoryList = AppPrefs.getInstance(getActivity()).getRecentUsedCategory();
		setupCategoryUI(mRecentCategoryList, mRecentUsedCategory, true);
		startQuery();

	}

	protected void publishRec()
	{
		// 发推荐
		if (mRec instanceof Recommendation)
		{
			String entity = ((Recommendation)mRec).getEntityName();
			long category = mSelectedCategory.getCagetoryId();
			String content = ((Recommendation)mRec).getDescription();
			long needId = ((Recommendation)mRec).getNeedId();
			PublishRecMessage message = new PublishRecMessage(HTTP_TYPE.HTTP_TYPE_POST);
			message.addParams(AppConstants.HEADER_USER_ID, String.valueOf(1));
			message.addParams(AppConstants.RESPONSE_HEADER_TYPE, String.valueOf(1));
			message.addParams(AppConstants.RESPONSE_HEADER_CATEGORY_ID, String.valueOf(category));
			message.addParams(AppConstants.HEADER_ENTITY, entity);
			message.addParams(AppConstants.HEADER_DESCRIPTION, content);
			message.addParams(AppConstants.HEADER_NEEDID, String.valueOf(needId));
			message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(getActivity()).getIMEI());
			message.setFusionCallBack(new FusionCallBack()
			{

				@Override
				public void onFinish( FusionMessage msg )
				{
					super.onFinish(msg);
					int ret = (Integer)msg.getResponseData();
					if (ret == 0)
					{
						Toast.makeText(getActivity(), "发布推荐成功", Toast.LENGTH_LONG).show();
						insertRecentCategory(mSelectedCategory);
						mListener.swichFragment(0, null);

					}
					else
					{
						Toast.makeText(getActivity(), "发布推荐失败", Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailed( FusionMessage msg )
				{
					// TODO Auto-generated method stub
					super.onFailed(msg);
				}

			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		}
		else
		{
			String desc = ((AskRecommendation)mRec).getDescription();
			long category = mSelectedCategory.getCagetoryId();

			PublishRecMessage message = new PublishRecMessage(HTTP_TYPE.HTTP_TYPE_POST, 2);
			message.addParams(AppConstants.HEADER_USER_ID, String.valueOf(1));
			message.addParams(AppConstants.RESPONSE_HEADER_TYPE, String.valueOf(2));
			message.addParams(AppConstants.RESPONSE_HEADER_CATEGORY_ID, String.valueOf(category));
			message.addParams(AppConstants.HEADER_DESCRIPTION, desc);
			message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(getActivity()).getIMEI());
			message.setFusionCallBack(new FusionCallBack()
			{

				@Override
				public void onFinish( FusionMessage msg )
				{
					super.onFinish(msg);
					int ret = (Integer)msg.getResponseData();
					if (ret == 0)
					{
						Toast.makeText(getActivity(), "发布求推荐成功", Toast.LENGTH_LONG).show();
						mListener.swichFragment(0, null);
					}
					else
					{
						Toast.makeText(getActivity(), "发布求推荐失败", Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailed( FusionMessage msg )
				{
					// TODO Auto-generated method stub
					super.onFailed(msg);
				}

			});
			FusionBus.getInstance(getActivity()).sendMessage(message);
		}
	}

	private void startQuery()
	{
		CategoryMessage message = new CategoryMessage(HTTP_TYPE.HTTP_TYPE_GET);

		message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.setParam(AppConstants.REQUEST_HEADER_CATEGORY_DEPTH, String.valueOf(2));
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(getActivity()).getIMEI());
		message.setFusionCallBack(new FusionCallBack()
		{

			@Override
			public void onFinish( FusionMessage msg )
			{
				super.onFinish(msg);
				ArrayList<Category> cat = (ArrayList<Category>)msg.getResponseData();
				if (cat != null && cat.size() > 0)
				{
					// mParentList.addAll(cat);
					// CategoryAdapter adapter = new
					// CategoryAdapter(getActivity(), cat);
					// mCategoryList.setAdapter(adapter);
					for (Category tmp : cat)
					{
						if (TextUtils.equals("全部", tmp.getCategoryName()))
						{
							mChildList = tmp.getChildrenList();
							setupCategoryUI(mChildList, mAllCategory, false);
							setSelectedCategory();
							break;
						}

					}
				}
			}

			@Override
			public void onFailed( FusionMessage msg )
			{
				super.onFailed(msg);
			}

		});
		FusionBus.getInstance(getActivity()).sendMessage(message);
	}

	private void clearSelection()
	{

		for (ChooseCategoryItemView item : mRecentItemViewList)
		{
			item.setItemChecked(false);
		}
		for (ChooseCategoryItemView item : mCategoryItemViewList)
		{
			item.setItemChecked(false);
		}

	}

	private void insertRecentCategory( Category category )
	{
		if (mRecentCategoryList == null)
		{
			mRecentCategoryList = new ArrayList<Category>();
		}
		for (Category cat : mRecentCategoryList)
		{
			if (cat.getCagetoryId() == category.getCagetoryId())
			{
				return;
			}
		}
		mRecentCategoryList.add(category);
		AppPrefs.getInstance(getActivity()).setRecentUsedCategory(mRecentCategoryList);

	}

	private void setSelectedCategory()
	{
		if (mSelectedCategory != null)
		{
			for (ChooseCategoryItemView view : mCategoryItemViewList)
			{
				if (TextUtils.equals(mSelectedCategory.getCategoryName(), view.getCategoryName()))
				{
					view.setItemChecked(true);
					break;
				}
			}
			
		}
	}
	private void setupCategoryUI( ArrayList<Category> list, LinearLayout holder, final boolean isRecent )
	{
		if (list == null || list.size() <= 0) return;
		int rowCount = list.size() / 3;
		for (int i = 0; i <= rowCount; i++)
		{
			LinearLayout row = new LinearLayout(getActivity());
			row.setOrientation(LinearLayout.HORIZONTAL);
			for (int j = i * 3; j < i * 3 + 3; j++)
			{
				if (j >= 0 && j < list.size())
				{
					final Category item = list.get(j);
					String name = item.getCategoryName();
					final ChooseCategoryItemView itemView = new ChooseCategoryItemView(getActivity());
					itemView.setCategoryName(name);
					itemView.setOnClickListener(new View.OnClickListener()
					{

						@Override
						public void onClick( View v )
						{
							clearSelection();
							mSelectedCategory = item;
							itemView.setItemChecked(true);
						}
					});
					row.addView(itemView);
					if (isRecent)
					{
						mRecentItemViewList.add(itemView);
					}
					else
					{
						mCategoryItemViewList.add(itemView);
					}
				}
			}
			holder.addView(row);
		}
	}
}
