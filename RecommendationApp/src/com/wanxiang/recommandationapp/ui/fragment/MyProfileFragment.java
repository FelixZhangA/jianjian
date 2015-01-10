package com.wanxiang.recommandationapp.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.persistent.AppPrefs;

public class MyProfileFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_my_profile, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initUI(view);
	}

	private void initUI(View view) {
		view.findViewById(R.id.btn_logout).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						AppPrefs.getInstance(getActivity()).clearAll();
						getActivity().finish();
					}
				});
	}

}
