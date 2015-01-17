package com.wanxiang.recommandationapp.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.MatchNeedListAdapter;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.GetNeedMatchListMessage;
import com.wanxiang.recommandationapp.util.AppConstants;

public class MatchNeedListActivity extends Activity {

	private AskRecommendation mAskRec;
	private int mPageIndex;
	private MatchNeedListAdapter mAdapter;
	private ArrayList<Recommendation> mListRec = new ArrayList<Recommendation>();
	private PullToRefreshListView mDynamicListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_match_need_list);
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		mAskRec = (AskRecommendation) getIntent().getSerializableExtra(
				AppConstants.HEADER_NEEDID);
		if (mAskRec == null) {
			return;
		}

		initUI();
		startQuery(true);
	}

	private void initUI() {
		View rLayout = findViewById(R.id.ask_recommandation_item);
		TextView tvUserEntity = (TextView) rLayout
				.findViewById(R.id.tv_user_entity);
		TextView tvContent = (TextView) rLayout.findViewById(R.id.tv_content);
		TextView tvResponse = (TextView) rLayout.findViewById(R.id.tv_response);
		TextView tvCategory = (TextView) rLayout.findViewById(R.id.tv_category);

		tvUserEntity.setText(MatchNeedListActivity.this.getString(
				R.string.ask_rec_user, mAskRec.getUser().getName()));
		tvContent.setText(mAskRec.getDescription());
		tvCategory.setText(mAskRec.getCategoryName());
		Button btn_rec = (Button) rLayout.findViewById(R.id.btn_rec);
		btn_rec.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MatchNeedListActivity.this,
						PublishRecommendationActivity.class);
				intent.putExtra(AppConstants.HEADER_NEEDID, mAskRec.getId());
				startActivity(intent);
			}
		});
		Button btn_favorite = (Button) rLayout.findViewById(R.id.btn_see_all);
		btn_favorite.setText("收藏");

		mDynamicListView = (PullToRefreshListView) findViewById(R.id.lst_dynamic);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void startQuery(final boolean isPullDown) {
		GetNeedMatchListMessage message = new GetNeedMatchListMessage(
				HTTP_TYPE.HTTP_TYPE_GET);
		message.setContext(this);

		message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.setParam(AppConstants.HEADER_NEEDID,
				String.valueOf(mAskRec.getId()));
		int pageIndex = isPullDown ? 1 : mPageIndex;
		message.setParam(AppConstants.HEADER_PAGE_INDEX, pageIndex);
		message.addHeader(AppConstants.HEADER_IMEI,
				AppPrefs.getInstance(MatchNeedListActivity.this).getIMEI());

		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				if (isPullDown) {
					mListRec.clear();
				}
				mListRec.addAll((ArrayList<Recommendation>) msg
						.getResponseData());
				if (mListRec != null && mListRec.size() > 0) {
					fillData();
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}
		});

		FusionBus.getInstance(MatchNeedListActivity.this).sendMessage(message);

	}

	protected void fillData() {
		if (mAdapter == null) {
			mAdapter = new MatchNeedListAdapter(MatchNeedListActivity.this,
					mListRec);
			mDynamicListView.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
		mDynamicListView.setVisibility(View.VISIBLE);
	}
}
