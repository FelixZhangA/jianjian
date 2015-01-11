package com.wanxiang.recommandationapp.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.category.LikeCategoryMessage;
import com.wanxiang.recommandationapp.ui.fragment.CategoryListFragment.OnChannelFavioratedListener;
import com.wanxiang.recommandationapp.util.AppConstants;

public class CategoryDetailsAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Category> mCategoryList;
	private OnChannelFavioratedListener mListener;

	public CategoryDetailsAdapter(Context context,
			ArrayList<Category> categoryList) {
		mContext = context;
		mCategoryList = categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		mCategoryList = categoryList;
	}

	public void setOnCategoryFavoriteListener(
			OnChannelFavioratedListener listener) {
		mListener = listener;
	}

	@Override
	public int getCount() {
		return mCategoryList.size();
	}

	@Override
	public Category getItem(int arg0) {
		return mCategoryList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			ViewHolder holder = new ViewHolder();
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.layout_category_detail_item, null);
			holder.tvCategory = (TextView) arg1.findViewById(R.id.tv_category);
			holder.tvFriends = (TextView) arg1
					.findViewById(R.id.tv_category_detail);
			// holder.tvUser = (TextView)
			// arg1.findViewById(R.id.tv_updater_name);
			// holder.ivBadge = (ImageView) arg1.findViewById(R.id.iv_badge);
			holder.btnJoin = (Button) arg1.findViewById(R.id.btn_join);
			holder.ivChecked = (ImageView) arg1.findViewById(R.id.img_checked);
			arg1.setTag(holder);
		}
		final Category current = getItem(arg0);
		if (current != null) {
			final ViewHolder holder = (ViewHolder) arg1.getTag();
			holder.tvCategory.setText(current.getCategoryName());
			// holder.tvFriends.setText(mContext.getString(R.string.category_friends_count,
			// current.getFriendsCount()));
			// holder.tvUser.setText(current.getLastUpdater().getName());
			// holder.ivBadge.setBackgroundResource(R.drawable.user_icon);
//			if (current.isFavor()) {
//				holder.ivChecked.setVisibility(View.VISIBLE);
//				holder.btnJoin.setVisibility(View.GONE);
//			} else {
//				holder.ivChecked.setVisibility(View.GONE);
//				holder.btnJoin.setVisibility(View.VISIBLE);
//			}

			holder.btnJoin.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					LikeCategoryMessage message = new LikeCategoryMessage(
							HTTP_TYPE.HTTP_TYPE_POST);
					message.setContext(mContext);
					message.setParam(AppConstants.HEADER_TOKEN,
							AppPrefs.getInstance(mContext).getSessionId());
					message.setParam(AppConstants.RESPONSE_HEADER_CATEGORY_ID,
							String.valueOf(current.getCagetoryId()));
					message.setFusionCallBack(new FusionCallBack() {

						@Override
						public void onFinish(FusionMessage msg) {
							super.onFinish(msg);
							int ret = (Integer) msg.getResponseData();
							if (ret == 0) {
								Toast.makeText(mContext, "收藏频道成功",
										Toast.LENGTH_LONG).show();
								FusionMessage message = new FusionMessage("dbService", "updateCategory");
								message.setParam(DatabaseConstants.MESSAGE_UPDATE, DatabaseConstants.MESSAGE_UPDATE_FAVORITE);
								message.setParam(DatabaseConstants.COLUMN_CATEGORY_UID, current.getCagetoryId());
								FusionBus.getInstance(mContext).sendMessage(message);
								mListener.onChannelFaviorated();
							} else {
								Toast.makeText(mContext, "收藏频道失败",
										Toast.LENGTH_LONG).show();
							}
						}

						@Override
						public void onFailed(FusionMessage msg) {
							super.onFailed(msg);
						}

					});
					FusionBus.getInstance(mContext).sendMessage(message);
					
				}
			});
		}

		return arg1;
	}

	private class ViewHolder {
		public TextView tvCategory;
		public TextView tvFriends;
		public ImageView ivBadge;
		public TextView tvUser;
		public Button btnJoin;
		public ImageView ivChecked;
	}

}
