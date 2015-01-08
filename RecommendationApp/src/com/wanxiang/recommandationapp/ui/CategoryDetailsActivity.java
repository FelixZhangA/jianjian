package com.wanxiang.recommandationapp.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.HomePageListAdapter;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.CategoryDetailDynamicMessage;
import com.wanxiang.recommandationapp.util.AppConstants;

public class CategoryDetailsActivity extends Activity implements
		OnItemClickListener, OnClickListener, OnTabChangeListener {
	private static final int SUCCESS = 1111;
	private static final int PUBLISH_REQUEST = 1;
	private Category mCategory;
	private long mCategoryId;
	private PullToRefreshListView mRankListView;
	private PullToRefreshListView mDynamicListView;
	private Button mJoinButton;
	private TextView mEmptyTextView;
	private TextView mTextCategory;
	private int mPageIndex = 1;
	private HomePageListAdapter mAdapter;
	private Cursor mCursor;

	private ArrayList<AbstractRecommendation> mListRec = new ArrayList<AbstractRecommendation>();

	// private Handler mHandler = new Handler()
	// {
	// @Override
	// public void handleMessage( Message msg )
	// {
	// if (msg.what == SUCCESS)
	// {
	// if (mCursor == null || mCursor.getCount() <= 0)
	// {
	// mEmptyTextView.setVisibility(View.VISIBLE);
	// }
	// else
	// {
	// mEmptyTextView.setVisibility(View.GONE);
	// mAdapter = new RecommendationCursorAdapter(CategoryDetailsActivity.this,
	// mCursor, false);
	// mListView.setAdapter(mAdapter);
	// }
	//
	// }
	// }
	//
	// };
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_category_detail);
		mCategory = (Category) getIntent().getSerializableExtra(
				AppConstants.INTENT_CATEGORY);
		if (mCategory == null) {
			return;
		}
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(true);		
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(Html.fromHtml("<b>" + mCategory.getCategoryName()
				+ "</b>"));

		initUI();
		startQuery(true);
	}

	private void initUI() {
		// mTabHost = (TabHost)view.findViewById(R.id.tabhost);
		// mTabHost.setup();
		// mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.category_tab_dynamic)).setContent(R.id.lst_dynamic));
		// mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.category_tab_rank)).setContent(R.id.lst_rank));
		// mTabHost.setOnTabChangedListener(this);
		//
		// mRankListView =
		// (PullToRefreshListView)mTabHost.findViewById(R.id.lst_rank);

		mTextCategory = (TextView) findViewById(R.id.tv_category_name);
		mTextCategory.setText(mCategory.getCategoryName());
		mDynamicListView = (PullToRefreshListView) findViewById(R.id.lst_dynamic);
		mDynamicListView.setOnItemClickListener(this);
		mDynamicListView.setMode(Mode.BOTH);
		mDynamicListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// Do work to refresh the list here.
						// new
						// FetchRecommendationAsyncTask(getActivity()).execute();

						long updateTime = System.currentTimeMillis() / 1000;

						if (mDynamicListView.isFooterShown()) {
							startQuery(false);
						} else {
							startQuery(true);

						}

					}
				});
		mJoinButton = (Button) findViewById(R.id.btn_join);
		mJoinButton.setOnClickListener(this);

	}

	// @Override
	// public View onCreateView( LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState )
	// {
	// return inflater.inflate(R.layout.layout_category_detail, container,
	// false);
	//
	// }
	//
	// @Override
	// public void onViewCreated( View view, Bundle savedInstanceState )
	// {
	// super.onViewCreated(view, savedInstanceState);
	// initUI(view);
	// mCategory =
	// (Category)getArguments().getSerializable(AppConstants.INTENT_CATEGORY);
	// if (mCategory == null)
	// {
	// return;
	// }
	// startQuery(System.currentTimeMillis());
	// }

	private void startQuery(final boolean isPullDown) {
		final AppPrefs appPrefs = AppPrefs
				.getInstance(CategoryDetailsActivity.this);
		CategoryDetailDynamicMessage message = new CategoryDetailDynamicMessage(
				HTTP_TYPE.HTTP_TYPE_GET);

		if (!isPullDown) {
			message.setParam(AppConstants.HEADER_NEXT_ID,
					String.valueOf(appPrefs.getLastRecommendationId()));
		}
		message.setParam(AppConstants.RESPONSE_HEADER_CATEGORY_ID,
				String.valueOf(mCategory.getCagetoryId()));
		message.setParam(AppConstants.HEADER_TOKEN, appPrefs.getSessionId());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);

				if (isPullDown) {
					mListRec.clear();
				}
				mListRec.addAll((ArrayList<AbstractRecommendation>) msg
						.getResponseData());

				if (mListRec != null && mListRec.size() > 0) {
					fillData();
					// Get the last recommendation, which date is the oldest.
					appPrefs.setLastRecommendationId(mListRec.get(
							mListRec.size() - 1).getId());
				}
				mDynamicListView.onRefreshComplete();
				mPageIndex++;
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
				mDynamicListView.onRefreshComplete();
			}

		});
		FusionBus.getInstance(CategoryDetailsActivity.this)
				.sendMessage(message);
	}

	// private class FetchRecommendationAsyncTask extends AsyncTask<Object,
	// Object, Exception>
	// {
	// private Context context;
	//
	// public FetchRecommendationAsyncTask( Context context )
	// {
	// this.context = context;
	// }
	//
	// @Override
	// protected Exception doInBackground( Object... params )
	// {
	// try
	// {
	// RecommendationDao dao = RecommendationDao.getAdapterObject(context);
	// mCursor = dao.getRecommendationsByCategoryCursor(mCategory);
	// }
	// catch (Exception ex)
	// {
	// return ex;
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute( Exception result )
	// {
	// super.onPostExecute(result);
	// mHandler.sendEmptyMessage(SUCCESS);
	// mListView.onRefreshComplete();
	// if (result != null)
	// {
	// Log.e("wanxiang", result.getMessage());
	// }
	// }
	// }

	protected void fillData() {
		if (mAdapter == null) {
			mAdapter = new HomePageListAdapter(CategoryDetailsActivity.this,
					mListRec);
			mDynamicListView.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
		mDynamicListView.setVisibility(View.VISIBLE);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.d("WANXIANG", "Item click listener " + arg2);
		// get recommendation id and send to comment activity
		Intent intent = new Intent();
		intent.setClass(CategoryDetailsActivity.this, CommentsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(AppConstants.RECOMMENDATION,
				mAdapter.getItem(arg2 - 1));
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_publish) {
			Intent intent = new Intent();
			intent.setClass(this, PublishRecommendationActivity.class);
			intent.putExtra("category", mCategory);
			startActivityForResult(intent, PUBLISH_REQUEST);
		}
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onClick(View v) {
		if (R.id.btn_recommand == v.getId()) {
			Log.d("WANXIANG", "button click ");
			Intent intent = new Intent();
			intent.setClass(CategoryDetailsActivity.this,
					PublishRecommendationActivity.class);
			intent.putExtra(AppConstants.INTENT_CATEGORY, mCategory);
			startActivityForResult(intent, PUBLISH_REQUEST);
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		// if ("tab1".contentEquals(tabId))
		// {
		// mRankListView.setVisibility(View.GONE);
		// mDynamicListView.setVisibility(View.VISIBLE);
		// }
		// else
		// {
		// mRankListView.setVisibility(View.VISIBLE);
		// mDynamicListView.setVisibility(View.GONE);
		// }
	}
}
