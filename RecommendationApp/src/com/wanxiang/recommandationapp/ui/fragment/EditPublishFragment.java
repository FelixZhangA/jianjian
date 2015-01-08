package com.wanxiang.recommandationapp.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.data.RecommendationDao;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.ui.OnFragmentChangeListener;
import com.wanxiang.recommandationapp.util.Utils;


public class EditPublishFragment extends Fragment implements OnTabChangeListener
{
	private EditText				mEditEntity;
	private AutoCompleteTextView	mAutoCompleteCategory;
	private EditText				mEditContent;
	private EditText				mEditAskContent;
	ArrayAdapter<CharSequence>		mCategoryAdapter;
	private String					mCategory	= null;
	private RecommendationDao		mDao		= null;
	private TabHost					mTabHost;
	private View 					mLayoutRec;
	private View 					mLayoutAskRec;
//	private RelativeLayout			mChooseCategoryLayout;
	private Button 					mBtnNext;
	private Button					mBtnBack;
	private boolean 				mIsPublish = true;

	private OnFragmentChangeListener mListener = null;
	
	private Entity					mEntity;
	private long					mNeedId;
	public EditPublishFragment(OnFragmentChangeListener listener, Entity entity, long needId)
	{
		mListener = listener;
		mEntity = entity;
		mNeedId = needId;
		
	}
	
	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mTabHost = (TabHost)view.findViewById(R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.publish_recommandation)).setContent(R.id.ll_recommandation));
		mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.ask_for_recommandation)).setContent(R.id.ll_ask_recommandation));
		mTabHost.setOnTabChangedListener(this);
		
		mLayoutRec = view.findViewById(R.id.ll_recommandation);
		mLayoutAskRec = view.findViewById(R.id.ll_ask_recommandation);
		mLayoutAskRec.setVisibility(View.GONE);
		
		mEditEntity = (EditText) mLayoutRec.findViewById(R.id.tv_entity);
		if (mEntity != null)
		{
			mEditEntity.setText(mEntity.getEntityName());
		}
		mEditContent = (EditText) mLayoutRec.findViewById(R.id.tv_content);
		mEditAskContent = (EditText) mLayoutAskRec.findViewById(R.id.tv_entity);

		mBtnNext = (Button) view.findViewById(R.id.btn_next);
		mBtnNext.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick( View v )
			{
				if (mIsPublish)
				{
					String entity = mEditEntity.getText().toString();
					if (TextUtils.isEmpty(entity)) 
					{
						Toast.makeText(getActivity(), "实体不能为空~", Toast.LENGTH_LONG).show();
						return;
					}
					Recommendation r = new Recommendation();
					
					r.setEntityName(entity);
					r.setDescription(mEditContent.getText().toString());
					r.setNeedId(mNeedId);
					mListener.swichFragment(2, r);
				} else
				{
					String content = mEditAskContent.getText().toString();
					if (TextUtils.isEmpty(content)) 
					{
						Toast.makeText(getActivity(), "求推荐内容不能为空~", Toast.LENGTH_LONG).show();
						return;
					}
					AskRecommendation r = new AskRecommendation();
					r.setDescription(content);
					mListener.swichFragment(2, r);
				}
				Utils.hideIMME(getActivity(), getView().getWindowToken());
			}
		});
		
		mBtnBack = (Button) view.findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick( View v )
			{
				mListener.swichFragment(0, null);
				Utils.hideIMME(getActivity(), getView().getWindowToken());
			}
		});
		
		Utils.showIMME(getActivity());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate(R.layout.fragment_edit_publish, null);
		return view;
	}

	@Override
	public void onTabChanged( String tabId )
	{
		if ("tab1".contentEquals(tabId))
		{
			mIsPublish = true;
			mLayoutRec.setVisibility(View.VISIBLE);
			mLayoutAskRec.setVisibility(View.GONE);
		} else {
			
			mIsPublish = false;
			mLayoutRec.setVisibility(View.GONE);
			mLayoutAskRec.setVisibility(View.VISIBLE);
		}
	}
}
