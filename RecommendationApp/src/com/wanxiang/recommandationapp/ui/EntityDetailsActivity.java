package com.wanxiang.recommandationapp.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.EntityDetailAdapter;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.entity.EntityMessage;
import com.wanxiang.recommandationapp.util.AppConstants;

public class EntityDetailsActivity extends Activity implements
		OnItemClickListener {
	private static final int PUBLISH_REQUEST = 2;

	private Entity mEntity;
	private PullToRefreshListView mDynamicListView;
	private int mPageIndex = 1;
	private ArrayList<Recommendation> mListRec = new ArrayList<Recommendation>();
	private EntityDetailAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_entity_detail);
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		mEntity = (Entity) getIntent().getSerializableExtra(
				AppConstants.RESPONSE_HEADER_ENTITY_NAME);
		if (mEntity == null) {
			return;
		}
		initUI();
		startQuery(true);
	}

	private void initUI() {

		TextView tvEntity = (TextView) findViewById(R.id.tv_entity_name);
		tvEntity.setText(mEntity.getEntityName());

		Button btnView = (Button) findViewById(R.id.btn_view);
		btnView.setOnClickListener(mClickListner);
		btnView.setVisibility(View.GONE);

		Button btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(mClickListner);

		Button btnForward = (Button) findViewById(R.id.btn_forward);
		btnForward.setOnClickListener(mClickListner);

		mDynamicListView = (PullToRefreshListView) findViewById(R.id.lst_dynamic);
		mDynamicListView.setOnItemClickListener(this);
		mDynamicListView.setMode(Mode.BOTH);
		mDynamicListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (mDynamicListView.isFooterShown()) {
							startQuery(false);
						} else {
							startQuery(true);

						}

					}
				});

	}

	protected void startQuery(boolean isPullDown) {
		EntityMessage message = new EntityMessage(HTTP_TYPE.HTTP_TYPE_GET);
		message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.setParam(AppConstants.RESPONSE_HEADER_ENTITY_ID, mEntity.getId());
		if (isPullDown) {
			mPageIndex = 1;
			mListRec.clear();
			message.setParam(AppConstants.HEADER_PAGE_INDEX, String.valueOf(1));
		} else {
			message.setParam(AppConstants.HEADER_PAGE_INDEX,
					String.valueOf(mPageIndex));
		}
		message.addHeader(AppConstants.HEADER_IMEI,
				AppPrefs.getInstance(EntityDetailsActivity.this).getIMEI());

		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				mListRec.addAll((ArrayList<Recommendation>) msg
						.getResponseData());

				if (mListRec != null && mListRec.size() > 0) {
					fillData();
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
		FusionBus.getInstance(EntityDetailsActivity.this).sendMessage(message);
	}

	protected void fillData() {
		if (mAdapter == null)
		{
			mAdapter = new EntityDetailAdapter(EntityDetailsActivity.this, mListRec);
			mDynamicListView.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_publish) {
			Intent intent = new Intent();
			intent.setClass(this, PublishRecommendationActivity.class);
			intent.putExtra("entity", mEntity);
			startActivityForResult(intent, PUBLISH_REQUEST);
		}
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onMenuItemSelected(featureId, item);
	}
	private View.OnClickListener mClickListner = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if (R.id.btn_view == arg0.getId()) {

			} else if (R.id.btn_search == arg0.getId()) {
				//http://m.baidu.com/s?word=%E8%80%81%E7%94%B7%E5%AD%A9
				Uri uri = Uri.parse("http://m.baidu.com/s?word=" + Uri.encode(mEntity.getEntityName()));
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setData(uri);
				startActivity(intent);
			} else if (R.id.btn_forward == arg0.getId()) {
				Intent intent = new Intent();
				intent.setClass(EntityDetailsActivity.this, PublishRecommendationActivity.class);
				intent.putExtra("entity", mEntity);
				startActivityForResult(intent, PUBLISH_REQUEST);
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Recommendation rec = mAdapter.getItem(arg2 - 1);
		if (rec != null && rec instanceof Recommendation)
		{
			Intent intent = new Intent();
			intent.setClass(this, CommentsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(AppConstants.RECOMMENDATION, rec);
			intent.putExtras(bundle);
			intent.putExtra("source", AppConstants.SOURCE_ENTITY_PAGE);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}
}
