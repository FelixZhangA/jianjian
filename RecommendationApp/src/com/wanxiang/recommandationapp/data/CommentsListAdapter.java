package com.wanxiang.recommandationapp.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.model.Comment;
import com.wanxiang.recommandationapp.util.Utils;

public class CommentsListAdapter extends BaseAdapter
{
	private ArrayList<Comment> mList;
	private Context mContext;

	public CommentsListAdapter(Context context, ArrayList<Comment> commentList)
	{
		mContext = context;
		mList = commentList;
	}
	@Override
	public int getCount()
	{
		return mList.size();
	}

	@Override
	public Comment getItem( int position )
	{
		return mList.get(position);
	}

	@Override
	public long getItemId( int position )
	{
		return mList.get(position).getId();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		if (convertView == null)
		{
			ViewHolder holder;
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_item, null);
			holder = new ViewHolder();

			holder.tvName = (TextView)convertView.findViewById(R.id.tv_username);

			holder.tvContent = (TextView)convertView.findViewById(R.id.tv_rc_content);

			holder.tvDate = (TextView)convertView.findViewById(R.id.tv_timestamp);
			convertView.setTag(holder);
		}
		
		Comment comment = mList.get(position);
		ViewHolder holder = (ViewHolder)convertView.getTag();
		holder.tvName.setText(comment.getUser().getName());
		holder.tvContent.setText(comment.getContent());
		long date = comment.getCommentDate();
		holder.tvDate.setText(Utils.formatDate(mContext, date));
		return convertView;
	}

	

    static class ViewHolder
    {
        TextView tvName;
        TextView tvContent;
        TextView tvDate;

    }
}
