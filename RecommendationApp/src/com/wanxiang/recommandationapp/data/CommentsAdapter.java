package com.wanxiang.recommandationapp.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.util.Utils;

public class CommentsAdapter extends CursorAdapter
{

    private String             recommeder;

    private Context            mContext;
    private Cursor	mCursor;
	private CommentDao mDao;
	public CommentsAdapter( Context context, Cursor c, boolean autoRequery )
	{
		super(context, c, autoRequery);
		mDao = CommentDao.getAdapterObject(context);
		mCursor = c;
		mContext = context;
	}

    static class ViewHolder
    {
        TextView tvName;
        TextView tvContent;
        TextView tvDate;

    }

	@Override
	public void bindView( View arg0, Context arg1, Cursor arg2 )
	{
		ViewHolder holder = (ViewHolder)arg0.getTag();
		if (holder != null)
		{
			holder.tvName.setText(arg2.getString(arg2.getColumnIndex(DatabaseConstants.COLUMN_USER)));
			holder.tvContent.setText(arg2.getString(arg2.getColumnIndex(DatabaseConstants.COLUMN_CONTENT)));
			long date = arg2.getLong(arg2.getColumnIndex(DatabaseConstants.COLUMN_DATE));
			holder.tvDate.setText(Utils.formatDate(arg1, date));
		}
	}

	@Override
	public View newView( Context arg0, Cursor arg1, ViewGroup arg2 )
	{
		ViewHolder holder;
		View view = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_item, null);
		holder = new ViewHolder();

		holder.tvName = (TextView)view.findViewById(R.id.tv_username);

		holder.tvContent = (TextView)view.findViewById(R.id.tv_rc_content);

		holder.tvDate = (TextView)view.findViewById(R.id.tv_timestamp);
		view.setTag(holder);
		return view;

	}
}
