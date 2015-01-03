package com.wanxiang.recommandationapp.ui;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.data.CommentDao;
import com.wanxiang.recommandationapp.data.CommentsAdapter;
import com.wanxiang.recommandationapp.data.CommentsListAdapter;
import com.wanxiang.recommandationapp.data.RecommendationDetail;
import com.wanxiang.recommandationapp.http.CommentsParser;
import com.wanxiang.recommandationapp.http.EasyHttpClient;
import com.wanxiang.recommandationapp.http.HttpHelper;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.LikeRecommendationMessage;
import com.wanxiang.recommandationapp.service.publish.PublishCommentsMessage;
import com.wanxiang.recommandationapp.service.recommend.RecommendationDetailMessage;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.IO;
import com.wanxiang.recommandationapp.util.Utils;


public class CommentsActivity extends Activity implements OnClickListener
{
	private Recommendation			mRecommendation;
	private PullToRefreshListView	mListView;
	private CommentsAdapter			mAdapter;
	private Comment					mReplyComment;
	private EditText				mEditContent;
	private Button					mBtnSend;
	private Cursor					mCursor;
	private long					mRecID;
	private TextView				mPraiseText;
	private TextView				mCommentsText;
	private ImageView				mImgPraise;
	private ImageView				mImgComment;
	private int						mCommentNum;
	private int 					mSource = AppConstants.SOURCE_HOME_PAGE;
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_comment_page);
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);
		actionBar.setTitle(Html.fromHtml("<b>" + getString(R.string.main_action_bar_name) + "</b>"));
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = this.getIntent();
		mRecommendation = (Recommendation)intent.getSerializableExtra(AppConstants.RECOMMENDATION);
		mSource = intent.getIntExtra("source", AppConstants.SOURCE_HOME_PAGE);
		if (mRecommendation == null)
		{
			return;
		}
		mRecID = mRecommendation.getId();
		mListView = (PullToRefreshListView)findViewById(R.id.list_comments);

		mEditContent = (EditText)findViewById(R.id.edit_content);
		mEditContent.setHint(getString(R.string.string_send_hint, mRecommendation.getUserName()));

		mBtnSend = (Button)findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		
		
		initRecommendation();
		getComments();

	}

	private void initRecommendation()
	{
		View rLayout = findViewById(R.id.recommandation_item);
		TextView tvEntityName = (TextView)rLayout.findViewById(R.id.tv_entity_name);
		tvEntityName.setText(mRecommendation.getEntityName());
		tvEntityName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mSource == AppConstants.SOURCE_ENTITY_PAGE)
				{
					onBackPressed();
				} else
				{
					Entity entity = new Entity(mRecommendation.getEntityName());
					entity.setId(mRecommendation.getEntityId());
					Intent intent = new Intent(CommentsActivity.this, EntityDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(AppConstants.RESPONSE_HEADER_ENTITY_NAME, entity);
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		});

		TextView tvName = (TextView)rLayout.findViewById(R.id.tv_user);
		tvName.setText(getString(R.string.rec_detail_header, mRecommendation.getUserName(), mRecommendation.getCategoryName()));

		TextView tvContent = (TextView)rLayout.findViewById(R.id.tv_content);
		tvContent.setText(mRecommendation.getDescription());

		TextView tvDate = (TextView)rLayout.findViewById(R.id.tv_timestamp);
		long date = mRecommendation.getUpdateTime();
		String dateTxt = Utils.formatDate(this, date);
		tvDate.setText(dateTxt);

		mPraiseText = (TextView)rLayout.findViewById(R.id.tv_praise);
		mPraiseText.setText(String.valueOf(mRecommendation.getPhraiseNum()));
		mPraiseText.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick( View v )
			{
				handleRec(mRecommendation, true, mPraiseText);

			}
		});
		mImgPraise = (ImageView)rLayout.findViewById(R.id.img_praise);
		mImgPraise.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick( View v )
			{
				handleRec(mRecommendation, true, mPraiseText);
				
			}
		});
		mCommentNum = mRecommendation.getCommentNum();
		mCommentsText = (TextView)rLayout.findViewById(R.id.tv_comment);
		mCommentsText.setText(String.valueOf(mCommentNum));

	}

	public boolean onOptionsItemSelected( MenuItem item )
	{
		if (item.getItemId() == android.R.id.home)
		{
			onBackPressed();
		}
		return true;
	}

	@Override
	public void onClick( View v )
	{
		String content = mEditContent.getText().toString();
		if (TextUtils.isEmpty(content))
		{
			Toast.makeText(CommentsActivity.this, R.string.error_publish_no_text, Toast.LENGTH_LONG).show();
		}
		else
		{
			// PublishCommentTask task = new PublishCommentTask(content);
			// task.execute();
			publishRecommendation(content);
		}
	}

	private void publishRecommendation( String content )
	{
		PublishCommentsMessage message = new PublishCommentsMessage(HTTP_TYPE.HTTP_TYPE_GET);
		message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.setParam(AppConstants.HEADER_REC_ID, String.valueOf(mRecommendation.getId()));
		message.setParam(AppConstants.HEADER_COMMENT_CONTENT, content);
		if (mReplyComment != null)
		{
			message.setParam(AppConstants.RESPONSE_HEADER_REPLYED_USER_ID, mReplyComment.getUserId());
		}
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(this).getIMEI());
		message.setFusionCallBack(new FusionCallBack()
		{

			@Override
			public void onFinish( FusionMessage msg )
			{
				super.onFinish(msg);
				int ret = (Integer)msg.getResponseData();
				if (ret == 0)
				{
					Toast.makeText(CommentsActivity.this, "发布评论成功", Toast.LENGTH_LONG).show();
					getComments();
					Utils.hideIMME(CommentsActivity.this, CommentsActivity.this.getCurrentFocus().getWindowToken());
					mEditContent.getText().clear();
					mReplyComment = null;
				}
				else
				{
					Toast.makeText(CommentsActivity.this, "发布评论失败", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailed( FusionMessage msg )
			{
				super.onFailed(msg);
			}

		});
		FusionBus.getInstance(CommentsActivity.this).sendMessage(message);
	}

	private void getComments()
	{
		RecommendationDetailMessage message = new RecommendationDetailMessage(HTTP_TYPE.HTTP_TYPE_GET);
		message.setParam(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.setParam(AppConstants.HEADER_REC_ID, String.valueOf(mRecommendation.getId()));
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(this).getIMEI());

		message.setFusionCallBack(new FusionCallBack()
		{

			@Override
			public void onFinish( FusionMessage msg )
			{
				super.onFinish(msg);
				RecommendationDetail ret = (RecommendationDetail)msg.getResponseData();
				if (ret != null)
				{
					ArrayList<Comment> comments = ret.getCommentList();
					final CommentsListAdapter adapter = new CommentsListAdapter(CommentsActivity.this, comments);
					mListView.setAdapter(adapter);
					mListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Comment replyComment = adapter.getItem(arg2-1);
							if (replyComment != null)
							{
								mEditContent.setHint(getString(R.string.string_send_hint, replyComment.getUserName()));
								mReplyComment = replyComment;
							}
						}
					});
				}
			}

			@Override
			public void onFailed( FusionMessage msg )
			{
				super.onFailed(msg);
			}

		});
		FusionBus.getInstance(CommentsActivity.this).sendMessage(message);

	}
	public class FetchCommentAsynTask extends AsyncTask<Object, Object, Object>
	{

		private Context			context;
		private CommentsAdapter	adapter;
		private ProgressDialog	pd;

		public FetchCommentAsynTask( Context context )
		{
			this.context = context;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected void onPostExecute( Object result )
		{
			if (result != null)
			{
				CommentDao dao = CommentDao.getAdapterObject(CommentsActivity.this);
				mCursor = dao.getAllCommentsByIdCursor(mRecID);
			}
			super.onPostExecute(result);
			if (pd != null && pd.isShowing())
			{
				pd.dismiss();
			}
			if (mAdapter == null)
			{
				mAdapter = new CommentsAdapter(context, mCursor, true);
				mListView.setAdapter(mAdapter);
			}
			else
			{
				mAdapter.swapCursor(mCursor);
			}

		}

		@Override
		protected Object doInBackground( Object... params )
		{
			// try
			// {
			// CommentDao dao = CommentDao.getAdapterObject(context);
			// mCursor = dao.getAllCommentsByIdCursor(mRecommendation.getId());
			// }
			// catch (Exception ex)
			// {
			// Log.e("wanxiang", ex.getMessage());
			// }
			// return null;

			HttpClient client = null;
			try
			{
				// Send recommendation to server;
				Map<String, String> properties = new HashMap<String, String>();
				properties.put(AppConstants.HEADER_USER_ID, String.valueOf(1));
				properties.put(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID, String.valueOf(mRecID));

				Map<String, String> headers = new HashMap<String, String>();
				headers.put(AppConstants.HEADER_IMEI, AppPrefs.getInstance(CommentsActivity.this).getIMEI());

				HttpPost request = HttpHelper.createHttpPost(AppConstants.ACTION_SHOW_REC_DETAIL, properties, headers);
				client = HttpHelper.createHttpClient(false);
				HttpResponse response = client.execute(request);
				checkStatusCode(response);

			}
			catch (Exception ex)
			{
				return ex;
			}
			finally
			{
				client.getConnectionManager().shutdown();
			}
			return null;
		}

	}

	private void handleRec( final Recommendation rec, boolean like, final TextView holder )
	{
		LikeRecommendationMessage message = new LikeRecommendationMessage(HTTP_TYPE.HTTP_TYPE_POST);
		message.addParams(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.addParams(AppConstants.HEADER_REC_ID, String.valueOf(rec.getId()));
		if (like)
		{
			message.addParams(AppConstants.HEADER_LEVEL, String.valueOf(1));
		}
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(CommentsActivity.this).getIMEI());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish( FusionMessage msg )
			{
				super.onFinish(msg);
				int ret = (Integer)msg.getResponseData();
				if (ret == 0)
				{
					Toast.makeText(CommentsActivity.this, "点赞成功~", Toast.LENGTH_LONG).show();
					holder.setText(String.valueOf(rec.getPhraiseNum() + 1));
				}
				else
				{
					Toast.makeText(CommentsActivity.this, "点赞失败……", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailed( FusionMessage msg )
			{
				super.onFailed(msg);
				Toast.makeText(CommentsActivity.this, "点赞失败……", Toast.LENGTH_LONG).show();
			}
			
		});
		
		FusionBus.getInstance(CommentsActivity.this).sendMessage(message);
	}
	private class PublishCommentTask extends AsyncTask<Object, Object, Boolean>
	{
		private String	content;

		public PublishCommentTask( String content )
		{
			this.content = content;
		}

		@Override
		protected void onPostExecute( Boolean result )
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result)
			{
				mEditContent.setText(null);
				Toast.makeText(CommentsActivity.this, R.string.string_comment_succ, Toast.LENGTH_LONG).show();
				FetchCommentAsynTask task = new FetchCommentAsynTask(CommentsActivity.this);
				task.execute();
				mCommentsText.setText(String.valueOf(++mCommentNum));
			}
			else
			{
				Toast.makeText(CommentsActivity.this, R.string.string_comment_fail, Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected Boolean doInBackground( Object... params )
		{
			Boolean ret = false;
			// CommentDao dao =
			// CommentDao.getAdapterObject(CommentsActivity.this);
			String user = "Comment";
			String content = mEditContent.getText().toString();
			EasyHttpClient client = null;
			try
			{
				Map<String, String> headers = new HashMap<String, String>();
				headers.put(AppConstants.HEADER_IMEI, AppPrefs.getInstance(CommentsActivity.this).getIMEI());
				HttpPost request = HttpHelper.createHttpPost(AppConstants.ACTION_COMMENT_REC, null, headers);
				NameValuePair nameValuePair1 = new BasicNameValuePair(AppConstants.HEADER_USER_ID, "1");
				NameValuePair nameValuePair2 = new BasicNameValuePair(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID, String.valueOf(mRecID));
				NameValuePair nameValuePair3 = new BasicNameValuePair(AppConstants.HEADER_COMMENT_CONTENT, content);
				List list = new ArrayList();
				list.add(nameValuePair1);
				list.add(nameValuePair2);
				list.add(nameValuePair3);
				request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
				client = HttpHelper.createHttpClient(false);
				HttpResponse response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					return true;
				}
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			catch (ClientProtocolException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				client.getConnectionManager().shutdown();
			}
			// ret = dao.insertComment(new Comment(mRecommendation.getId(),
			// user, content, System.currentTimeMillis()));
			return ret;
		}

	}

	public void checkStatusCode( HttpResponse response )
	{
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			Log.d("wanxiang", "GET COMMENT failed. Error is " + response.getStatusLine().getStatusCode());
		}
		else
		{
			try
			{
				InputStream inputStream = response.getEntity().getContent();
				CommentsParser parser = new CommentsParser(new String(IO.getStreamAsBytes(inputStream)));
				ArrayList<Comment> rec = parser.parseCommentsFromResponse();
				AppPrefs appPrefs = AppPrefs.getInstance(CommentsActivity.this);
				CommentDao dao = CommentDao.getAdapterObject(CommentsActivity.this);

				for (Comment r : rec)
				{
					dao.insertComment(r);
				}

				mCursor = dao.getAllCommentsByIdCursor(mRecID);

			}
			catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
