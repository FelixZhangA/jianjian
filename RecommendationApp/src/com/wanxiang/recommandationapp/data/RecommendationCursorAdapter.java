package com.wanxiang.recommandationapp.data;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.http.HttpHelper;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;


public class RecommendationCursorAdapter extends CursorAdapter
{
	private Cursor				mCursor;
	private RecommendationDao	mDao;
	private Context				mContext;

	public RecommendationCursorAdapter( Context context, Cursor c, boolean autoRequery )
	{
		super(context, c, autoRequery);
		mCursor = c;
		mDao = RecommendationDao.getAdapterObject(context);
		mContext = context;

	}

	@Override
	public Cursor swapCursor( Cursor newCursor )
	{
		mCursor = newCursor;

		return super.swapCursor(newCursor);
	}

	@Override
	public int getCount()
	{
		return mCursor != null ? mCursor.getCount() : 0;
	}

	@Override
	public Recommendation getItem( int position )
	{
		if (position > -1)
		{
			mCursor.moveToPosition(position);
			return mDao.getRecommendationFromCursor(mCursor);
		}
		return null;
	}

	@Override
	public long getItemId( int position )
	{
		return super.getItemId(position);
	}

	@Override
	public void bindView( View arg0, Context arg1, Cursor c )
	{
		ViewHolder holder = (ViewHolder)arg0.getTag();
		if (holder != null)
		{
			holder.tvEntityName.setText(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_ENTITY)));
			String category = arg1.getString(R.string.category_user, c.getColumnIndex(DatabaseConstants.COLUMN_USER), c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY)));
			holder.tvCategory.setText(category);
//			holder.tvName.setText(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_USER)));
			holder.tvContent.setText(c.getString(c.getColumnIndex(DatabaseConstants.COLUMN_CONTENT)));

			long date = c.getLong(c.getColumnIndex(DatabaseConstants.COLUMN_DATE));
			String dateTxt = Utils.formatDate(mContext, date);
			holder.tvDate.setText(dateTxt);
			
			long rec_id = c.getLong(c.getColumnIndex(DatabaseConstants.COLUMN_RECOMMANDATION_UID));
			int praise_num = c.getInt(c.getColumnIndex(DatabaseConstants.COLUMN_PRAISE_NUM));
			int comment_num = c.getInt(c.getColumnIndex(DatabaseConstants.COLUMN_COMMENT_NUM));
//			holder.imgPhraise.setOnClickListener(new InteractionOnClickListener(rec_id, praise_num, holder.tvPhraise));
//
//			holder.tvComment.setText(String.valueOf(comment_num));
//			holder.tvPhraise.setText(String.valueOf(praise_num));

		}

	}

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parent )
	{
		final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_recommandation_item, null);
		ViewHolder holder = new ViewHolder();

		holder = new ViewHolder();

		holder.tvEntityName = (TextView)view.findViewById(R.id.tv_entity_name);
		holder.tvCategory = (TextView)view.findViewById(R.id.tv_category);
		// holder.tvName = (TextView)view.findViewById(R.id.tv_username);
		holder.tvContent = (TextView)view.findViewById(R.id.tv_content);
		holder.tvDate = (TextView)view.findViewById(R.id.tv_timestamp);
		// holder.tvPhraise = (TextView)view.findViewById(R.id.tv_praise);
		// holder.tvComment = (TextView)view.findViewById(R.id.tv_comment);
		// holder.imgPhraise = (ImageView)view.findViewById(R.id.img_praise);
		// holder.imgComment = (ImageView)view.findViewById(R.id.img_comment);
		view.setTag(holder);
		return view;
	}

	static class ViewHolder
	{
		TextView	tvEntityName;

		TextView	tvCategory;

		TextView	tvName;

		TextView	tvContent;

		TextView	tvDate;

		TextView	tvPhraise;

		TextView	tvComment;

		ImageView	imgPhraise;

		ImageView	imgComment;
	}

	private void checkStatusCode( HttpResponse response )
	{
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			Log.d("wanxiang", "publish failed. Error is " + response.getStatusLine().getStatusCode());
		}
		else
		{
			try
			{
				InputStream is = response.getEntity().getContent();
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader bis = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();
				String s;

				while (true)
				{
					s = bis.readLine();
					if (TextUtils.isEmpty(s))
					{
						break;
					}
					sb.append(s);
				}
				Log.d("wanxiang", "Response is " + sb.toString());
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

	private class InteractionOnClickListener implements View.OnClickListener
	{
		private long		mRecID			= 0;
		private int			mPraiseCount	= 0;
		private TextView	mPraiseTextView;

		public InteractionOnClickListener( long recID, int praiseCount, TextView tvPraise )
		{
			mRecID = recID;
			mPraiseCount = praiseCount;
			mPraiseTextView = tvPraise;
		}

		@Override
		public void onClick( View v )
		{
			InteractionAsycTask task = new InteractionAsycTask(mPraiseTextView, mPraiseCount);
			task.execute(mRecID);
		}

	}
	private class InteractionAsycTask extends AsyncTask<Long, Object, Boolean>
	{
		private TextView	mView;
		private int			mPraiseCount;
		private boolean		mPraised	= false;

		public InteractionAsycTask( View v, int praiseCount )
		{
			mView = (TextView)v;
			mPraiseCount = praiseCount;
		}

		@Override
		protected void onPostExecute( Boolean result )
		{
			super.onPostExecute(result);
			if (result != null && result)
			{
				if (mPraised)
				{
					mPraiseCount--;
				}
				else
				{
					mPraiseCount++;
				}
				mView.setText(String.valueOf(mPraiseCount));
				mPraised = true;
			}
		}

		@Override
		protected Boolean doInBackground( Long... arg0 )
		{
			HttpClient client = null;

			try
			{
				Map<String, String> headers = new HashMap<String, String>();
				headers.put(AppConstants.HEADER_IMEI, AppPrefs.getInstance(mContext).getIMEI());
				HttpPost request = HttpHelper.createHttpPost(AppConstants.ACTION_PRAISE_REC, null, headers);

				NameValuePair nameValuePair1 = new BasicNameValuePair(AppConstants.HEADER_USER_ID, "1");
				NameValuePair nameValuePair2 = new BasicNameValuePair(AppConstants.RESPONSE_HEADER_RECOMMENDATION_ID, String.valueOf(arg0[0]));
				NameValuePair nameValuePair3 = new BasicNameValuePair(AppConstants.HEADER_CANCEL_PRAISE, String.valueOf(mPraised ? 1 : 0));
				List list = new ArrayList();
				list.add(nameValuePair1);
				list.add(nameValuePair2);
				list.add(nameValuePair3);
				request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
				client = HttpHelper.createHttpClient(false);

				HttpResponse response = client.execute(request);
				checkStatusCode(response);

				// RecommendationDao dao =
				// RecommendationDao.getAdapterObject(mContext);
				// dao.updatePraiseCount(arg0[0]);
				return true;
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

			return null;
		}

	}
}
