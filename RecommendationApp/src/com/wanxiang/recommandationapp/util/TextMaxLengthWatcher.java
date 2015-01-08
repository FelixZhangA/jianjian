package com.wanxiang.recommandationapp.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.jianjianapp.R;

public class TextMaxLengthWatcher implements TextWatcher 
{

	private int maxLen = 0;
	private EditText editText = null;
	private TextView mBinderText = null;
	private Context context = null;
	public TextMaxLengthWatcher(Context context, int maxLen, EditText editText, TextView bindertv) 
	{
		this.maxLen = maxLen;
		this.editText = editText;
		this.mBinderText = bindertv;
		this.context = context;
	}

	@Override
	public void afterTextChanged(Editable s) 
	{

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) 
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		Editable editable = editText.getText();
		int len = editable.length();
		int remain = maxLen - len;
		mBinderText.setText(String.valueOf(remain));
		if (remain < 0)
		{
			mBinderText.setTextColor(context.getResources().getColor(R.color.BG_RED));
		} else
		{
			mBinderText.setTextColor(context.getResources().getColor(R.color.BG_GRAY));
		}
	}

}
