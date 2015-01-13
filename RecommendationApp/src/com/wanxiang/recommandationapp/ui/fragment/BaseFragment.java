package com.wanxiang.recommandationapp.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.ui.PublishRecommendationActivity;

/**
 * Created by admin on 13-11-23.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class BaseFragment extends Fragment {
	protected ImageView mIvHeaderLeft;
	protected ImageView mIvHeaderRight;

    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mIvHeaderLeft = (ImageView) view.findViewById(R.id.ivLeftMenuBtn);
		if (mIvHeaderLeft != null) {
			mIvHeaderLeft.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
		}
		
		mIvHeaderRight = (ImageView) view.findViewById(R.id.ivRightMenuBtn);
		if (mIvHeaderRight != null) {
			mIvHeaderRight.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), PublishRecommendationActivity.class);
					startActivity(intent);
				}
			});
		}
	}



	public abstract String initContent();
}
