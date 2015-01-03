package com.wanxiang.recommandationapp.data;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.LikeRecommendationMessage;
import com.wanxiang.recommandationapp.ui.EntityDetailsActivity;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;

public class MatchNeedListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Recommendation> mListRec;

	public MatchNeedListAdapter(Context context,
			ArrayList<Recommendation> lstRec) {
		mContext = context;
		mListRec = lstRec;
	}

	@Override
	public int getCount() {
		return mListRec.size();
	}

	@Override
	public Recommendation getItem(int position) {
		return mListRec.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mListRec.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Recommendation rec = mListRec.get(position);
		if (convertView == null
				|| !(convertView.getTag() instanceof RecViewHolder)) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_recommandation_detail, null);
			RecViewHolder holder = new RecViewHolder();
			holder.tvEntityName = (TextView) convertView
					.findViewById(R.id.tv_entity_name);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tvDate = (TextView) convertView
					.findViewById(R.id.tv_timestamp);
			holder.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
			holder.tvComment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			holder.tvPhraise = (TextView) convertView
					.findViewById(R.id.tv_praise);
			holder.imgPhraise = (ImageView) convertView
					.findViewById(R.id.img_praise);

			convertView.setTag(holder);
		}
		final RecViewHolder holder = (RecViewHolder) convertView.getTag();
		holder.tvEntityName.setText(rec.getEntityName());
		holder.tvEntityName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Entity entity = new Entity(rec.getEntityName());
				entity.setId(rec.getEntityId());
				Intent intent = new Intent(mContext,
						EntityDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(
						AppConstants.RESPONSE_HEADER_ENTITY_NAME, entity);
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(intent);

			}
		});
		holder.tvUser.setText(mContext.getString(R.string.rec_detail_header, rec.getUserName(), rec.getCategoryName()));

		holder.tvContent.setText(rec.getDescription());
		holder.tvDate.setText(Utils.formatDate(mContext, rec.getUpdateTime()));
		holder.tvComment.setText(String.valueOf(rec.getCommentNum()));

		holder.tvPhraise.setText(String.valueOf(rec.getPhraiseNum()));
		holder.tvPhraise.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handleRec(rec, true, holder);
			}
		});

		holder.imgPhraise.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handleRec(rec, true, holder);
			}
		});

		return convertView;
	}

	private static class RecViewHolder {
		TextView tvEntityName;

		TextView tvCategory;

		TextView tvName;

		TextView tvContent;

		TextView tvDate;

		TextView tvPhraise;

		TextView tvComment;

		ImageView imgPhraise;

		ImageView imgComment;

		TextView tvUser;
	}

	private void handleRec(final Recommendation rec, boolean like,
			final RecViewHolder holder) {
		LikeRecommendationMessage message = new LikeRecommendationMessage(
				HTTP_TYPE.HTTP_TYPE_POST);
		message.addParams(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.addParams(AppConstants.HEADER_REC_ID,
				String.valueOf(rec.getId()));
		if (like) {
			message.addParams(AppConstants.HEADER_LEVEL, String.valueOf(1));
		}
		message.addHeader(AppConstants.HEADER_IMEI,
				AppPrefs.getInstance(mContext).getIMEI());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				int ret = (Integer) msg.getResponseData();
				if (ret == 0) {
					Toast.makeText(mContext, "点赞成功~", Toast.LENGTH_LONG).show();
					holder.tvPhraise.setText(String.valueOf(rec.getPhraiseNum() + 1));
				} else {
					Toast.makeText(mContext, "点赞失败……", Toast.LENGTH_LONG)
							.show();
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
				Toast.makeText(mContext, "点赞失败……", Toast.LENGTH_LONG).show();
			}

		});

		FusionBus.getInstance(mContext).sendMessage(message);
	}
}
