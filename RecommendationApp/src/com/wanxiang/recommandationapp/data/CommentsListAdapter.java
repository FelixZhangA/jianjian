package com.wanxiang.recommandationapp.data;

import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.model.Recommendation;
import com.wanxiang.recommandationapp.model.User;
import com.wanxiang.recommandationapp.util.Utils;

public class CommentsListAdapter extends BaseAdapter {
	private static final int MAX_SIZE = 4;
	private ArrayList<Comment> mList;
	private Context mContext;
	private RecommendationDetail mRecommendation;
	private int mSource;

	public CommentsListAdapter(Context context, ArrayList<Comment> commentList,
			RecommendationDetail rec, int source) {
		mContext = context;
		mList = commentList;
		mRecommendation = rec;
		mSource = source;
	}

	@Override
	public int getCount() {
		return mList.size() + 1;
	}

	@Override
	public Comment getItem(int position) {
		if (position <= 0) {
			return null;
		} else {

			return mList.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return mList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 第一个item加载推荐详情。 其他加载评论
		if (position == 0) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_recommandation_detail, null);
			TextView tvEntityName = (TextView) convertView
					.findViewById(R.id.tv_entity_name);
			TextView tvName = (TextView) convertView.findViewById(R.id.tv_user);
			TextView tvContent = (TextView) convertView
					.findViewById(R.id.tv_content);
			TextView tvDate = (TextView) convertView
					.findViewById(R.id.tv_timestamp);
			TextView tvPraise = (TextView) convertView
					.findViewById(R.id.tv_praise);
			TextView tvComment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			LinearLayout llPraise = (LinearLayout) convertView
					.findViewById(R.id.ll_praise_stub);
			
			LinearLayout llPraiseLayout = (LinearLayout) convertView.findViewById(R.id.ll_praise_detail);
			tvEntityName.setText(mRecommendation.getEntityName());
			tvName.setText(mContext.getString(R.string.rec_detail_header,
					mRecommendation.getUser().getName(),
					mRecommendation.getCategoryName()));
			tvContent.setText(mRecommendation.getDescription());
			long date = mRecommendation.getUpdateTime();
			String dateTxt = Utils.formatDate(mContext, date);
			tvDate.setText(dateTxt);
			tvPraise.setText(String.valueOf(mRecommendation.getPhraiseCount()));
			tvComment.setText(String.valueOf(mRecommendation.getCommentCount()));
			setupPraiseUser(llPraise, llPraiseLayout);

		} else {
			if (convertView == null || convertView.getTag() == null) {
				ViewHolder holder;
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.layout_comment_item, null);
				holder = new ViewHolder();

				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_username);

				holder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_rc_content);

				holder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_timestamp);
				convertView.setTag(holder);
			}

			Comment comment = mList.get(position - 1);
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.tvName.setText(comment.getUser().getName());
			holder.tvContent.setText(comment.getContent());
			long date = comment.getCommentDate();
			holder.tvDate.setText(Utils.formatDate(mContext, date));
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvContent;
		TextView tvDate;

	}

	private void setupPraiseUser(LinearLayout stub, LinearLayout parent) {
		ArrayList<User> praiseUser = mRecommendation.getPraiseUser();
		if (praiseUser != null && praiseUser.size() > 0) {
			int i=1;
			for (User tmp : praiseUser) {
				String text = tmp.getName();
				int color = mContext.getResources().getColor(R.color.BG_GREEN);
				if (i >= MAX_SIZE) {
					text = "等" + String.valueOf(praiseUser.size()) + "个好友推荐";
					color = mContext.getResources().getColor(R.color.BG_BLACK);
				} 
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(20, 0, 0, 0);
				TextView textView = new TextView(mContext);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				textView.setText(text);
				textView.setTextColor(color);
				textView.setLayoutParams(params);
				stub.addView(textView);
				
				if (i >= MAX_SIZE) {
					break;
				}
				i++;
			}
			parent.setVisibility(View.VISIBLE);
		}
	}

}
