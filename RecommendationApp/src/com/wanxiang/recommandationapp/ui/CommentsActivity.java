package com.wanxiang.recommandationapp.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.CommentsListAdapter;
import com.wanxiang.recommandationapp.data.RecommendationDetail;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.LikeRecommendationMessage;
import com.wanxiang.recommandationapp.service.publish.PublishCommentsMessage;
import com.wanxiang.recommandationapp.service.recommend.RecommendationDetailMessage;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;

public class CommentsActivity extends Activity implements OnClickListener {
	private Recommendation mRecommendation;
	private PullToRefreshListView mListView;
	private Comment mReplyComment;
	private EditText mEditContent;
	private Button mBtnSend;
	private Cursor mCursor;
	private long mRecID;
	private TextView mPraiseText;
	private TextView mCommentsText;
	private ImageView mImgPraise;
	private ImageView mImgComment;
	private int mCommentNum;
	private int mSource = AppConstants.SOURCE_HOME_PAGE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_comment_page);
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);
		actionBar.setTitle(Html.fromHtml("<b>"
				+ getString(R.string.main_action_bar_name) + "</b>"));
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = this.getIntent();
		mRecommendation = (Recommendation) intent
				.getSerializableExtra(AppConstants.RECOMMENDATION);
		mSource = intent.getIntExtra("source", AppConstants.SOURCE_HOME_PAGE);
		if (mRecommendation == null) {
			return;
		}
		mRecID = mRecommendation.getId();
		mListView = (PullToRefreshListView) findViewById(R.id.list_comments);

		mEditContent = (EditText) findViewById(R.id.edit_content);
		mEditContent.setHint(getString(R.string.string_send_hint,
				mRecommendation.getUser().getName()));

		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);

//		initRecommendation();
		getComments();

	}

//	private void initRecommendation() {
//		View rLayout = findViewById(R.id.recommandation_item);
//		TextView tvEntityName = (TextView) rLayout
//				.findViewById(R.id.tv_entity_name);
//		tvEntityName.setText(mRecommendation.getEntityName());
//		tvEntityName.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if (mSource == AppConstants.SOURCE_ENTITY_PAGE) {
//					onBackPressed();
//				} else {
//					Entity entity = new Entity(mRecommendation.getEntityName());
//					entity.setId(mRecommendation.getEntityId());
//					Intent intent = new Intent(CommentsActivity.this,
//							EntityDetailsActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putSerializable(
//							AppConstants.RESPONSE_HEADER_ENTITY_NAME, entity);
//					intent.putExtras(bundle);
//					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					startActivity(intent);
//				}
//			}
//		});
//
//		TextView tvName = (TextView) rLayout.findViewById(R.id.tv_user);
//		tvName.setText(getString(R.string.rec_detail_header, mRecommendation
//				.getUser().getName(), mRecommendation.getCategoryName()));
//
//		TextView tvContent = (TextView) rLayout.findViewById(R.id.tv_content);
//		tvContent.setText(mRecommendation.getDescription());
//
//		TextView tvDate = (TextView) rLayout.findViewById(R.id.tv_timestamp);
//		long date = mRecommendation.getUpdateTime();
//		String dateTxt = Utils.formatDate(this, date);
//		tvDate.setText(dateTxt);
//
//		mPraiseText = (TextView) rLayout.findViewById(R.id.tv_praise);
//		mPraiseText.setText(String.valueOf(mRecommendation.getPhraiseNum()));
//		mPraiseText.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				handleRec(mRecommendation, true, mPraiseText);
//
//			}
//		});
//		mImgPraise = (ImageView) rLayout.findViewById(R.id.img_praise);
//		mImgPraise.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				handleRec(mRecommendation, true, mPraiseText);
//
//			}
//		});
//		mCommentNum = mRecommendation.getCommentNum();
//		mCommentsText = (TextView) rLayout.findViewById(R.id.tv_comment);
//		mCommentsText.setText(String.valueOf(mCommentNum));
//
//	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		String content = mEditContent.getText().toString();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(CommentsActivity.this,
					R.string.error_publish_no_text, Toast.LENGTH_LONG).show();
		} else {
			// PublishCommentTask task = new PublishCommentTask(content);
			// task.execute();
			publishRecommendation(content);
		}
	}

	private void publishRecommendation(String content) {
		PublishCommentsMessage message = new PublishCommentsMessage(
				HTTP_TYPE.HTTP_TYPE_GET);
		message.setParam(AppConstants.HEADER_REC_ID,
				String.valueOf(mRecommendation.getContentId()));
		message.setParam(AppConstants.HEADER_TOKEN,
				AppPrefs.getInstance(CommentsActivity.this).getSessionId());
		message.setParam(AppConstants.HEADER_COMMENT_CONTENT, content);
		if (mReplyComment != null) {
			message.setParam(AppConstants.RESPONSE_HEADER_REPLYED_USER_ID,
					mReplyComment.getUserId());
		}
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(this)
				.getIMEI());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				int ret = (Integer) msg.getResponseData();
				if (ret == 0) {
					Toast.makeText(CommentsActivity.this, "发布评论成功",
							Toast.LENGTH_LONG).show();
					getComments();
					Utils.hideIMME(CommentsActivity.this, CommentsActivity.this
							.getCurrentFocus().getWindowToken());
					mEditContent.getText().clear();
					mReplyComment = null;
				} else {
					Toast.makeText(CommentsActivity.this, "发布评论失败",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}

		});
		FusionBus.getInstance(CommentsActivity.this).sendMessage(message);
	}

	private void getComments() {
		RecommendationDetailMessage message = new RecommendationDetailMessage(
				HTTP_TYPE.HTTP_TYPE_GET);
		message.setParam(AppConstants.HEADER_REC_ID,
				String.valueOf(mRecommendation.getContentId()));
		message.setParam(AppConstants.HEADER_TOKEN, AppPrefs.getInstance(this)
				.getSessionId());

		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				RecommendationDetail ret = (RecommendationDetail) msg
						.getResponseData();
				if (ret != null) {
					ArrayList<Comment> comments = ret.getCommentList();
					final CommentsListAdapter adapter = new CommentsListAdapter(
							CommentsActivity.this, comments, ret, mSource);
					mListView.setAdapter(adapter);
					mListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Comment replyComment = adapter.getItem(arg2 - 1);
							if (replyComment != null) {
								mEditContent.setHint(getString(
										R.string.string_send_hint, replyComment
												.getUser().getName()));
								mReplyComment = replyComment;
							}
						}
					});
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}

		});
		FusionBus.getInstance(CommentsActivity.this).sendMessage(message);

	}

	private void handleRec(final Recommendation rec, boolean like,
			final TextView holder) {
		LikeRecommendationMessage message = new LikeRecommendationMessage(
				HTTP_TYPE.HTTP_TYPE_POST);
		message.setParam(AppConstants.HEADER_REC_ID,
				String.valueOf(rec.getContentId()));
		message.setParam(AppConstants.HEADER_TOKEN,
				AppPrefs.getInstance(CommentsActivity.this).getSessionId());

		if (like) {
			message.addParams(AppConstants.HEADER_LEVEL, String.valueOf(1));
		}
		message.addHeader(AppConstants.HEADER_IMEI,
				AppPrefs.getInstance(CommentsActivity.this).getIMEI());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				int ret = (Integer) msg.getResponseData();
				if (ret == 0) {
					Toast.makeText(CommentsActivity.this, "点赞成功~",
							Toast.LENGTH_LONG).show();
					holder.setText(String.valueOf(rec.getPhraiseNum() + 1));
				} else {
					Toast.makeText(CommentsActivity.this, "点赞失败……",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
				Toast.makeText(CommentsActivity.this, "点赞失败……",
						Toast.LENGTH_LONG).show();
			}

		});

		FusionBus.getInstance(CommentsActivity.this).sendMessage(message);
	}

}
