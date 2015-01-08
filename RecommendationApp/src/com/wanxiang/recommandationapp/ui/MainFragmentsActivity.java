package com.wanxiang.recommandationapp.ui;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.ui.fragment.CategoryListFragment;
import com.wanxiang.recommandationapp.ui.fragment.HomePageFragment;
import com.wanxiang.recommandationapp.ui.fragment.MyProfileFragment;


public class MainFragmentsActivity extends FragmentActivity implements TabListener
{
	public interface MyOnTouchListener
	{
		public boolean onTouch( MotionEvent ev );
	}
//	private AppSectionsPagerAdapter			mPageAdapter;
	private ViewPager						mViewPager;
	private ArrayList<MyOnTouchListener>	onTouchListeners;
	private TabHost							mTabHost;
	private FragmentManager	fragmentManager;
	private RadioGroup	radioGroup;
	private static final int	PUBLISH_REQUEST = 5;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main_action_bar);
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical parent.
		actionBar.setHomeButtonEnabled(false);
		actionBar.setTitle(Html.fromHtml("<small><b>" + getString(R.string.main_action_bar_name) + "</b></small>"));

		fragmentManager = getSupportFragmentManager();
		radioGroup = (RadioGroup)findViewById(R.id.rg_tab);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged( RadioGroup group, int checkedId )
			{
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				Fragment fragment = null;
				switch (checkedId) {
					case R.id.radio_home:
						fragment = new HomePageFragment();
						break;
					case R.id.radio_message:
					case R.id.radio_myProfile:
						fragment = new MyProfileFragment();
						break;
					case R.id.radio_category:
						fragment = new CategoryListFragment();
						break;
				
				}
				transaction.replace(R.id.content, fragment);
				transaction.commit();
			}
		});
		
		FusionBus.getInstance(MainFragmentsActivity.this);
		switchFragment(new HomePageFragment());
		// setUpFragments();
		// onTouchListeners = new
		// ArrayList<MainFragmentsActivity.MyOnTouchListener>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	@Override
	public boolean onMenuItemSelected( int featureId, MenuItem item )
	{
		if (item.getItemId() == R.id.action_publish)
		{
			Intent intent = new Intent();
			intent.setClass(this, PublishRecommendationActivity.class);
			startActivityForResult(intent, PUBLISH_REQUEST );
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void registerMyOnTouchListener( MyOnTouchListener myOnTouchListener )
	{
		onTouchListeners.add(myOnTouchListener);
	}

	public void unregisterMyOnTouchListener( MyOnTouchListener myOnTouchListener )
	{
		onTouchListeners.remove(myOnTouchListener);
	}


	@Override
	public void onTabReselected( Tab tab, android.app.FragmentTransaction ft )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected( Tab tab, android.app.FragmentTransaction ft )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected( Tab tab, android.app.FragmentTransaction ft )
	{
		// TODO Auto-generated method stub
		
	}
	
	public void switchFragment(Fragment fragment)
	{
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.content, fragment);
		transaction.commit();
	}
}
