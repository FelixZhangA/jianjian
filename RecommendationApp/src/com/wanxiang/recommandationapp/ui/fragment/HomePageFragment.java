package com.wanxiang.recommandationapp.ui.fragment;


import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.HomePageListAdapter;
import com.wanxiang.recommandationapp.data.RecommendationCursorAdapter;
import com.wanxiang.recommandationapp.data.RecommendationDao;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.HomePageMessage;
import com.wanxiang.recommandationapp.ui.CommentsActivity;
import com.wanxiang.recommandationapp.util.AppConstants;


public class HomePageFragment extends BaseFragment implements OnItemClickListener
{
	private PullToRefreshListView				mListView;
	private RecommendationCursorAdapter			mAdapter;
	private Cursor								mCursor;
	private HomePageListAdapter					mListAdapter;
	private int									mPageIndex = 1;
	private ArrayList<AbstractRecommendation>	mListRec		= new ArrayList<AbstractRecommendation>();
	private static final int					SUCCESS			= 1111;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate(R.layout.layout_homepage, null);
		return view;
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated(view, savedInstanceState);
		mListView = (PullToRefreshListView)getView().findViewById(R.id.lst_recomandation);
		mListView.setOnItemClickListener(this);
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>()
		{

			@Override
			public void onRefresh( PullToRefreshBase<ListView> refreshView )
			{
				// Do work to refresh the list here.
				// new FetchRecommendationAsyncTask(getActivity()).execute();

				long updateTime = System.currentTimeMillis() / 1000;

				if (mListView.isFooterShown())
				{
					updateTime = AppPrefs.getInstance(getActivity()).getOdestUpdateTime();
					startQuery(updateTime, false);
				} else
				{
					startQuery(updateTime, true);
				}

			}
		});

		startQuery(System.currentTimeMillis(), true);

	}

	private void startQuery( long updateTime, final boolean isPullDown )
	{
		HomePageMessage message = new HomePageMessage(HTTP_TYPE.HTTP_TYPE_GET);

		message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
		if (isPullDown)
		{
			mPageIndex = 1;
			mListRec.clear();
			message.setParam(AppConstants.HEADER_PAGE_INDEX, String.valueOf(0));
		} else
		{
			message.setParam(AppConstants.HEADER_PAGE_INDEX, String.valueOf(mPageIndex));
		}
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(getActivity()).getIMEI());

		message.setFusionCallBack(new FusionCallBack()
		{

			@Override
			public void onFinish( FusionMessage msg )
			{
				super.onFinish(msg);
				mListRec.addAll((ArrayList<AbstractRecommendation>)msg.getResponseData());

				AppPrefs appPrefs = AppPrefs.getInstance(getActivity());
				if (mListRec != null && mListRec.size() > 0)
				{
					fillData();
					// Get the last recommendation, which date is the oldest.
					appPrefs.setOdestUpdateTime(mListRec.get(mListRec.size() - 1).getUpdateTime() - 1);
				}
				mListView.onRefreshComplete();
				mPageIndex++;

			}

			@Override
			public void onFailed( FusionMessage msg )
			{
				super.onFailed(msg);
				mListView.onRefreshComplete();
			}

		});
		FusionBus.getInstance(getActivity()).sendMessage(message);
	}

	@Override
	public String initContent()
	{
		return null;
	}

	@Override
	public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 )
	{
		AbstractRecommendation rec = mListAdapter.getItem(arg2 - 1);
		if (rec != null && rec instanceof Recommendation)
		{
			Intent intent = new Intent();
			intent.setClass(getActivity(), CommentsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(AppConstants.RECOMMENDATION, (Recommendation)rec);
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}

	private void fillData()
	{
		if (mListAdapter == null)
		{
			mListAdapter = new HomePageListAdapter(getActivity(), mListRec);
			mListView.setAdapter(mListAdapter);
		}
		mListAdapter.notifyDataSetChanged();
			
	}

	private Cursor loadDataFromLocal()
	{
		RecommendationDao dao = RecommendationDao.getAdapterObject(getActivity());
		Cursor c = dao.getAllRecommendationsCursor();
		return c;
	}
}