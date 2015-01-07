package com.wanxiang.recommandationapp.data;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.AskRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.homepage.LikeRecommendationMessage;
import com.wanxiang.recommandationapp.ui.CategoryDetailsActivity;
import com.wanxiang.recommandationapp.ui.EntityDetailsActivity;
import com.wanxiang.recommandationapp.ui.MatchNeedListActivity;
import com.wanxiang.recommandationapp.ui.PublishRecommendationActivity;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;


public class HomePageListAdapter extends BaseAdapter
{
	private Context								mContext;
	private ArrayList<AbstractRecommendation>	mListRec;

	public HomePageListAdapter( Context context, ArrayList<AbstractRecommendation> list )
	{
		mContext = context;
		mListRec = list;
	}

	@Override
	public int getCount()
	{
		return mListRec.size();
	}

	@Override
	public AbstractRecommendation getItem( int position )
	{
		return mListRec.get(position);
	}

	@Override
	public long getItemId( int position )
	{
		return mListRec.get(position).getId();
	}

	@Override
	public View getView( final int position, View convertView, ViewGroup parent )
	{
		AbstractRecommendation rec = mListRec.get(position);
		if (rec instanceof Recommendation)
		{
			final Recommendation r = (Recommendation)rec;
			if (convertView == null || !(convertView.getTag() instanceof RecViewHolder))
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_recommandation_item, null);
				RecViewHolder holder = new RecViewHolder();
				holder.tvEntityName = (TextView)convertView.findViewById(R.id.tv_entity_name);
				holder.tvCategory = (TextView)convertView.findViewById(R.id.tv_category);
				holder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
				holder.tvDate = (TextView)convertView.findViewById(R.id.tv_timestamp);
				holder.tvUser = (TextView)convertView.findViewById(R.id.tv_user);
				holder.tvComment = (TextView)convertView.findViewById(R.id.tv_comment);
				holder.tvPhraise = (TextView)convertView.findViewById(R.id.tv_praise);
				holder.imgPhraise = (ImageView)convertView.findViewById(R.id.img_praise);

				convertView.setTag(holder);
			}
			final RecViewHolder holder = (RecViewHolder)convertView.getTag();
			holder.tvEntityName.setText(r.getEntityName());
			holder.tvEntityName.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Entity entity = new Entity(r.getEntityName());
					entity.setId(r.getEntityId());
					Intent intent = new Intent(mContext, EntityDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(AppConstants.RESPONSE_HEADER_ENTITY_NAME, entity);
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					mContext.startActivity(intent);

				}
			});
			holder.tvUser.setText(mContext.getString(R.string.category_user, r.getUser().getName()));
			holder.tvCategory.setText(r.getCategoryName());
			holder.tvCategory.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick( View v )
				{
					Category cat = new Category();
					cat.setCategoryId(r.getCategoryId());
					cat.setCategoryName(r.getCategoryName());
					Intent intent = new Intent(mContext, CategoryDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(AppConstants.INTENT_CATEGORY, cat);
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					mContext.startActivity(intent);
				}
			});

			holder.tvContent.setText(r.getDescription());
			holder.tvDate.setText(Utils.formatDate(mContext, r.getUpdateTime()));
			holder.tvComment.setText(String.valueOf(r.getCommentNum()));
		
			holder.tvPhraise.setText(String.valueOf(r.getPhraiseNum()));
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
			
		}
		else
		{
			final AskRecommendation r = (AskRecommendation)rec;
			if (convertView == null || !(convertView.getTag() instanceof AskRecViewHolder))
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_ask_for_recommandation_item, null);
				AskRecViewHolder holder = new AskRecViewHolder();
				holder.tvUserEntity = (TextView)convertView.findViewById(R.id.tv_user_entity);
				holder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
				holder.tvResponse = (TextView)convertView.findViewById(R.id.tv_response);
				holder.tvCategory = (TextView)convertView.findViewById(R.id.tv_category);
				holder.btn_rec = (Button)convertView.findViewById(R.id.btn_rec);

				holder.btn_see_all = (Button)convertView.findViewById(R.id.btn_see_all);
				
				
//				holder.tvCategory.setOnClickListener(new View.OnClickListener()
//				{
//
//					@Override
//					public void onClick( View v )
//					{
//						Category cat = new Category();
//						cat.setCategoryId(r.getCategoryId());
//						cat.setCategoryName(r.getCategoryName());
//						Intent intent = new Intent(mContext, CategoryDetailsActivity.class);
//						Bundle bundle = new Bundle();
//						bundle.putSerializable(AppConstants.INTENT_CATEGORY, cat);
//						intent.putExtras(bundle);
//						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//						mContext.startActivity(intent);
//					}
//				});

				convertView.setTag(holder);
			}
			AskRecViewHolder holder = (AskRecViewHolder)convertView.getTag();
			holder.tvUserEntity.setText(mContext.getString(R.string.ask_rec_user, r.getUser().getName()));
			holder.tvContent.setText(r.getDescription());
			holder.tvCategory.setText(r.getCategoryName());
			holder.btn_rec.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick( View v )
				{
					Log.d("WANXIANG", "button click ");
					Intent intent = new Intent();
					intent.setClass(mContext, PublishRecommendationActivity.class);
					
					intent.putExtra(AppConstants.HEADER_NEEDID, r.getId());
					mContext.startActivity(intent);
				}
			});
			
			holder.btn_see_all.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick( View v )
				{
					Intent intent = new Intent();
					intent.setClass(mContext, MatchNeedListActivity.class);
					intent.putExtra(AppConstants.HEADER_NEEDID, r);
					mContext.startActivity(intent);
				}
			});
			// holder.tvResponse.setText(r.getUserName());
		}
		return convertView;
	}

	private static class RecViewHolder
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

		TextView	tvUser;
	}

	private static class AskRecViewHolder
	{
		TextView	tvUserEntity;

		TextView	tvContent;

		TextView	tvResponse;

		ImageView	imgBadge;

		Button		btn_rec;

		Button		btn_see_all;
		
		TextView	tvCategory;

	}

	private void handleRec( final Recommendation rec, boolean like, final RecViewHolder holder )
	{
		LikeRecommendationMessage message = new LikeRecommendationMessage(HTTP_TYPE.HTTP_TYPE_POST);
		message.addParams(AppConstants.HEADER_USER_ID, String.valueOf(1));
		message.addParams(AppConstants.HEADER_REC_ID, String.valueOf(rec.getId()));
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
