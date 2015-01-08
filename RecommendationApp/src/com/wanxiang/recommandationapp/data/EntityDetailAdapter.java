package com.wanxiang.recommandationapp.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.LikeRecommendationMessage;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;

public class EntityDetailAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Recommendation> mListRec = new ArrayList<Recommendation>();

	public EntityDetailAdapter(Context context, ArrayList<Recommendation> lstRec) {
		mContext = context;
		mListRec.addAll(lstRec);
	}

	@Override
	public int getCount() {
		return mListRec.size();
	}

	@Override
	public Recommendation getItem(int arg0) {
		return mListRec.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return mListRec.get(arg0).getId();
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.layout_entity_detail_item, null);
			ViewHolder holder = new ViewHolder();
			holder.tvUserName = (TextView) arg1.findViewById(R.id.tv_username);
			holder.tvTime = (TextView) arg1.findViewById(R.id.tv_time);
			holder.tvContent = (TextView) arg1.findViewById(R.id.tv_content);
			holder.tvComment = (TextView)arg1.findViewById(R.id.tv_comment);
			holder.tvPhraise = (TextView)arg1.findViewById(R.id.tv_praise);
			holder.imgPhraise = (ImageView)arg1.findViewById(R.id.img_praise);
			arg1.setTag(holder);

		}
		final Recommendation r = getItem(arg0);
		final ViewHolder holder = (ViewHolder) arg1.getTag();
		holder.tvUserName.setText(mContext.getString(R.string.category_user,
				r.getUser().getName()));
		holder.tvTime.setText(Utils.formatDate(mContext, r.getUpdateTime()));
		holder.tvContent.setText(r.getDescription());
		holder.tvPhraise.setText(String.valueOf(r.getPhraiseNum()));
		holder.tvComment.setText(String.valueOf(r.getCommentNum()));
		holder.tvPhraise.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick( View v )
			{
				handleRec(r, true, holder);
			}
		});
		
		holder.imgPhraise.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick( View v )
			{
				handleRec(r, true, holder);
			}
		});
		
		return arg1;
	}

	private static class ViewHolder {
		TextView tvUserName;
		TextView tvTime;
		TextView tvContent;
		TextView tvPhraise;
		TextView tvComment;
		ImageView imgPhraise;
		ImageView imgComment;
	}
	
	private void handleRec( final Recommendation rec, boolean like, final ViewHolder holder )
	{
		LikeRecommendationMessage message = new LikeRecommendationMessage(HTTP_TYPE.HTTP_TYPE_POST);
		message.setParam(AppConstants.HEADER_REC_ID, String.valueOf(rec.getContentId()));
		message.setParam(AppConstants.HEADER_TOKEN, AppPrefs.getInstance(mContext).getSessionId());

		if (like)
		{
			message.addParams(AppConstants.HEADER_LEVEL, String.valueOf(1));
		}
		message.addHeader(AppConstants.HEADER_IMEI, AppPrefs.getInstance(mContext).getIMEI());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish( FusionMessage msg )
			{
				super.onFinish(msg);
				int ret = (Integer)msg.getResponseData();
				if (ret == 0)
				{
					Toast.makeText(mContext, "点赞成功~", Toast.LENGTH_LONG).show();
					holder.tvPhraise.setText(String.valueOf(rec.getPhraiseNum() + 1));
				}
				else
				{
					Toast.makeText(mContext, "点赞失败……", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailed( FusionMessage msg )
			{
				super.onFailed(msg);
				Toast.makeText(mContext, "点赞失败……", Toast.LENGTH_LONG).show();
			}
			
		});
		
		FusionBus.getInstance(mContext).sendMessage(message);
	}
}
