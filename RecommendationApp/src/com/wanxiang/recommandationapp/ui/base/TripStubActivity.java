package com.wanxiang.recommandationapp.ui.base;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionPageManager;
import com.wanxiang.recommandationapp.ui.base.TripBaseFragment.Anim;
import com.wanxiang.recommandationapp.util.Constants;

/**
 * 页面跳转都通过TripStubActivity 嵌套Fragment来实现。这样之前Fragment
 * 的跳转逻辑很好移植，并且后续要动态替换fragment或插件框架都只需要指定相应的参数。 避免Activity 需要再manifest中注册的问题。
 * 
 * 1.管理应用中所有TripStubActivity 实例。 2.管理TripStubActivity 实例和fragment的跳转
 * 
 * 
 * @author changlin.dcl 2013.11.22
 */
public class TripStubActivity extends TripBaseActivity implements IPageSwitcher {
	private static final String TAG = Constants.TAG;
	private static ArrayList<WeakReference<TripStubActivity>> mCustomActivities = new ArrayList<WeakReference<TripStubActivity>>();
	protected PageSwitchBean mFirstPageSwitchBean;
	protected WeakReference<TripStubActivity> mCurrentInstance = null;
	protected Handler mSafeHandler = null;
	private TripBaseFragment mFragmentForResult = null;
	private int mFragmentRequestCode = -1;
	private boolean mActivityKilled = false;
	private final String KILLED_KEY = "mActivityKilled";

	private long mLastClickTime = -1;
	private final int CLICK_DELAY = 500;
	private final static int MOVE_OFFSET = 20;
	private float mLastMotionY;
	private float mLastMotionX;
	private static String sLastOpenPageName;
	
	public boolean needCheckClick = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent mNewIntent = getIntent();
		mSafeHandler = new Handler(getMainLooper());
		Log.d(TAG, "StubJumpActivity onCreate------------");
		setContentView(R.layout.stub_activity_layout);
		initApplication();// 解决进程被kill时，恢复后Mtop奇怪地不能连接网络的问题

		if (savedInstanceState != null) {
			mActivityKilled = savedInstanceState.getBoolean(KILLED_KEY);
		}

		mCurrentInstance = new WeakReference<TripStubActivity>(this);
		mCustomActivities.add(mCurrentInstance);
		init(mNewIntent);
		addBackStackChangedListener(getSupportFragmentManager());
		printAll();
		Log.d(TAG, "mainactivity" + (System.currentTimeMillis() - Constants.beginTime) + "");

		// 接收程序退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_EXIT_APP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(mExitReceiver, filter);
	}

	private void initApplication() {
//		TripApplication.getInstance().initApplication();
		FusionBus.getInstance(this);
	}

	protected int getActivityCount() {
		return mCustomActivities != null ? mCustomActivities.size() : -1;
	}

	protected TripStubActivity getRootActivity() {
		return mCustomActivities != null && mCustomActivities.size() > 0 ? mCustomActivities.get(0).get() : null;
	}

	private void printAll() {
		Log.d(TAG, "------------StubJumpActivity print all------------" + mCustomActivities.size());
		for (WeakReference<TripStubActivity> ref : mCustomActivities) {
			if (ref != null) {
				TripStubActivity item = ref.get();
				if (item != null) {
					Log.d(TAG, item.toString());
				}
			}
		}
	}

	public String toString() {
		return super.toString() + "  firstPageName:" + getPageName();
	}

	protected String getPageName() {
		TripBaseFragment frg = super.getActiveFragment();
		if (frg != null) {
			return frg.getPageName();
		}
		return "";
	}

	/**
	 * 仅用于接受应用退出广播，程序退出时有机会做一些必要的清理工作
	 */
	private BroadcastReceiver mExitReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.ACTION_EXIT_APP)) {
				finish();
			}
		}
	};

	private void init(Intent mNewIntent) {
		try {
			PageSwitchBean page = mNewIntent.getParcelableExtra("PageSwitchBean");
			String startActivityForResult = mNewIntent.getStringExtra("startActivityForResult");
			this.mFirstPageSwitchBean = page;
			Log.d(TAG, "StubJumpActivity init-------FusionMessage-----");
			if (page != null && !mActivityKilled) {// 避免两次创建
				TripBaseFragment frg = null;
				boolean addToBackStack = page.isAddToBackStack();
				String pageName = page.getPageName();
				Bundle bundle = page.getBundle();
				frg = (TripBaseFragment) FusionPageManager.getInstance().openPageWithNewFragmentManager(false, getSupportFragmentManager(), pageName, bundle, null, addToBackStack);
				if (frg != null ) {
					if("true".equalsIgnoreCase(startActivityForResult)){
						frg.setRequestCode(page.getRequestCode());
						frg.setFragmentFinishListener(new TripBaseFragment.onFragmentFinishListener() {
							@Override
							public void onFragmentResult(int requestCode, int resultCode, Intent intent) {
								TripStubActivity.this.setResult(resultCode, intent);
							}
						});
					}
				}else{
					this.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			this.finish();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(KILLED_KEY, true);

	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		Log.d(TAG, "StubJumpActivity onAttachFragment---------" + getPageName());
		if (fragment != null && fragment instanceof TripBaseFragment) {
			((TripBaseFragment) fragment).setPageSwitcher(this);
		}
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "StubJumpActivity onDestroy-----------" + getPageName());
		removeBackStackChangedListener(this.getSupportFragmentManager());
		if (mCurrentInstance != null) {
			mCustomActivities.remove(mCurrentInstance);
		}
		printAll();
		unregisterReceiver(mExitReceiver);
		super.onDestroy();
	}

	public void addBackStackChangedListener(FragmentManager mFragmentManager) {
		if (mFragmentManager != null) {
			mFragmentManager.addOnBackStackChangedListener(onBackStackChangedListener);
		}
	}

	public void removeBackStackChangedListener(FragmentManager mFragmentManager) {
		if (mFragmentManager != null) {
			mFragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener);
		}
	}

	public static TripStubActivity getTopActivity() {
		if (mCustomActivities != null) {
			int size = mCustomActivities.size();
			if (size >= 1) {
				WeakReference<TripStubActivity> ref = mCustomActivities.get(size - 1);
				if (ref != null) {
					return ref.get();
				}
			}
		}
		return null;
	}

	public static void unInit() {
		if (mCustomActivities != null) {
			mCustomActivities.clear();
		}
	}

	private OnBackStackChangedListener onBackStackChangedListener = new OnBackStackChangedListener() {

		@Override
		public void onBackStackChanged() {
			final FragmentManager mFragmentManager = getSupportFragmentManager();
			if (mFragmentManager != null) {
				int nCount = mFragmentManager.getBackStackEntryCount();
				if (nCount > 0) {
					TripBaseFragment frg = (TripBaseFragment) mFragmentManager.findFragmentById(R.id.fragment_container);
					if (null != frg) {
						frg.onPageResume();
					}
				}
			}
		}
	};

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed");
		if (this.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
			finishActivity(this, true);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "StubJumpActivity onKeyDown------" + getPageName());
		return super.onKeyDown(keyCode, event);
	}

	private void finishActivity(TripStubActivity act, boolean showAnimation) {
		if (act != null) {
			act.finish();
		}
		if (showAnimation) {
			int[] animations = null;
			if (act.mFirstPageSwitchBean != null && act.mFirstPageSwitchBean.getAnims() != null) {
				animations = act.mFirstPageSwitchBean.getAnims();
			}
			if (animations != null && animations.length >= 4) {
				overridePendingTransition(animations[2], animations[3]);
			}
		}
	}

	private boolean isMainThread() {
		return Thread.currentThread() == this.getMainLooper().getThread();
	}

	private void popOrFinishActivity() {
		if (this.isFinishing()) {
			return;
		}
		if (this.getSupportFragmentManager().getBackStackEntryCount() > 1) {
			if (isMainThread()) {
				this.getSupportFragmentManager().popBackStackImmediate();
			} else {
				this.mSafeHandler.post(new Runnable() {
					@Override
					public void run() {
						getSupportFragmentManager().popBackStackImmediate();
					}
				});
			}
		} else {
			finishActivity(this, true);
		}
	}

	protected boolean popFragmentInActivity(final String pageName, Bundle bundle, TripStubActivity findAcitivity) {
		if (pageName == null || findAcitivity == null || findAcitivity.isFinishing()) {
			return false;
		} else {
			final FragmentManager fragmentManager = findAcitivity.getSupportFragmentManager();
			if (fragmentManager != null) {
				Fragment frg = fragmentManager.findFragmentByTag(pageName);
				if (frg != null && frg instanceof TripBaseFragment) {
					if (fragmentManager.getBackStackEntryCount() > 1 && mSafeHandler != null) {
						mSafeHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								fragmentManager.popBackStack(pageName, 0);
							}
						}, 100);
					}
					((TripBaseFragment) frg).onFragmentDataReset(bundle);
					return true;
				}
			}
		}
		return false;
	}

	protected void refreshFragmentDataSet(Bundle bundle) {
		TripBaseFragment activeFragment = this.getActiveFragment();
		if (activeFragment != null) {
			Log.d(TAG, "refreshFragmentDataSet:" + bundle.toString());
			activeFragment.onFragmentDataReset(bundle);
		}
	}

	private void popAndRefreshData(PageSwitchBean page) {
		if (page == null || TextUtils.isEmpty(page.getPageName())) {
			Log.e(TAG, "popActivity name empty");
			return;
		}
		String pageName = page.getPageName();
		if (!findPage(pageName)) {
			Log.e(TAG, "Be sure you have the right pageName" + pageName);
			this.openPage(page);
			return;
		}

		int size = mCustomActivities.size();
		int i = size - 1;
		for (; i >= 0; i--) {
			WeakReference<TripStubActivity> ref = mCustomActivities.get(i);
			if (ref != null) {
				TripStubActivity item = ref.get();
				if (item == null) {
					Log.e(TAG, "item null");
					continue;
				}

				boolean findInActivity = popFragmentInActivity(pageName, page.getBundle(), item);
				if (findInActivity) {
					break;
				} else {// 找不到就弹出
					item.finish();
				}
			}
		}
	}

	public boolean findPage(final String pageName) {
		int size = mCustomActivities.size();
		int j = size - 1;
		boolean bFind = false;
		for (; j >= 0; j--) {
			WeakReference<TripStubActivity> ref = mCustomActivities.get(j);
			if (ref != null) {
				TripStubActivity item = ref.get();
				if (item == null) {
					Log.e(TAG, "item null");
					continue;
				}

				final FragmentManager mFragmentManager = item.getSupportFragmentManager();
				int nCount = mFragmentManager.getBackStackEntryCount();
				for (int i = nCount - 1; i >= 0; i--) {
					String name = mFragmentManager.getBackStackEntryAt(i).getName();
					if (name.equalsIgnoreCase(pageName)) {
						bFind = true;
						break;
					}
				}
				if (bFind) {
					break;
				}
			}
		}
		return bFind;
	}

	private void gotoAndRefreshData(PageSwitchBean page) {
		final String pageName = page.getPageName();
		final Bundle bundle = page.getBundle();
		Log.d(TAG, "popActivity name:" + pageName + "");
		if (TextUtils.isEmpty(pageName)) {
			Log.e(TAG, "popActivity name empty");
			return;
		}

		boolean find = findPage(pageName);
		if (!find) {// 没有找到时在当前activity上创建
			Log.e(TAG, "Be sure you have the right pageName" + pageName);
			// popOrFinishActivity(jumpBean);//避免死锁，弹出一层
			openPage(page);
			return;
		} else {
			int size = mCustomActivities.size();
			int i = size - 1;
			for (; i >= 0; i--) {
				WeakReference<TripStubActivity> ref = mCustomActivities.get(i);
				if (ref != null) {
					TripStubActivity item = ref.get();
					if (item == null) {
						Log.e(TAG, "item null");
						continue;
					}

					boolean findInActivity = popFragmentInActivity(pageName, bundle, item);
					if (findInActivity) {
						break;
					} else {
						finishActivity(item, false);
					}
				}
			}
		}
	}
	
	private boolean checkDoubleOpenPage(PageSwitchBean page){
		String lastOpenPageName = page.getPageName();
		long time = System.currentTimeMillis();
		long delayTime = time - mLastClickTime;
		mLastClickTime = time;
		if (0 < delayTime && delayTime < CLICK_DELAY) {
			if(sLastOpenPageName!=null && sLastOpenPageName.equals(lastOpenPageName)){
				return true;
			}
		}
		sLastOpenPageName = lastOpenPageName;
		return false;
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		Log.d("dispatchTouchEvent", "=====dispatchTouchEvent======="+needCheckClick);
//		if(needCheckClick){
//			
//			final float y = event.getY();
//			final float x = event.getX();
//			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//				mLastMotionY = y;
//				mLastMotionX = x;
//
//				long time = System.currentTimeMillis();
//				long delayTime = time - mLastClickTime;
//				mLastClickTime = time;
//				Log.d("dispatchTouchEvent", "=====dispatchTouchEvent======="+delayTime);
//				if (0 < delayTime && delayTime < CLICK_DELAY) {
//					return true;
//				}
//			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
////				final int yDiff = (int) Math.abs(y - mLastMotionY);  
////				final int xDiff = (int) Math.abs(x - mLastMotionX);  
////	            boolean yMoved = yDiff > MOVE_OFFSET;
////	            boolean xMoved = xDiff > MOVE_OFFSET;
////	            // 判断是否是移动  
////	            if (yMoved || xMoved) { 
////	            	Log.d(" dispatchTouchEvent", "=======2===========");
////	            	return true;
////	            } 
//			}		
//		}
//
//		return super.dispatchTouchEvent(event);
//	}

	@Override
	public Fragment openPageForResult(final PageSwitchBean page, final TripBaseFragment fragment) {
		if (page != null) {
			if (page.isNewActivity()) {
				mFragmentForResult = fragment;
				mFragmentRequestCode = page.getRequestCode();

				startActivityForResult(page);
				return null;
			} else {
				String pageName = page.getPageName();
				Bundle bundle = page.getBundle();
				int[] animations = page.getAnims();
				boolean addBackStack = page.isAddToBackStack();

				TripBaseFragment frg = (TripBaseFragment) FusionPageManager.getInstance().openPageWithNewFragmentManager(true, this.getSupportFragmentManager(), pageName, bundle, animations,
						addBackStack);
				if (frg == null) {
					return frg;
				}
				final TripBaseFragment opener = fragment;
				frg.setRequestCode(page.getRequestCode());
				frg.setFragmentFinishListener(new TripBaseFragment.onFragmentFinishListener() {
					@Override
					public void onFragmentResult(int requestCode, int resultCode, Intent intent) {
						opener.onFragmentResult(requestCode, resultCode, intent);
					}
				});
				return frg;

			}
		} else {
			Log.d(TAG, "openPageForResult.FusionMessage is null");
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult init-----------" + requestCode + " " + resultCode);
		if (mFragmentRequestCode == requestCode && mFragmentForResult != null) {
			mFragmentForResult.onFragmentResult(mFragmentRequestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startActivityForResult(PageSwitchBean page) {
		try {
			Intent intent = new Intent(this, TripStubActivity.class);
			intent.putExtra("PageSwitchBean", page);
			intent.putExtra("startActivityForResult", "true");
			this.startActivityForResult(intent, page.getRequestCode());

			int[] animations = page.getAnims();
			if (animations != null && animations.length >= 2) {
				this.overridePendingTransition(animations[0], animations[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startActivity(PageSwitchBean page) {
		try {
			Intent intent = new Intent(this, TripStubActivity.class);
			intent.putExtra("PageSwitchBean", page);
			this.startActivity(intent);

			int[] animations = page.getAnims();
			if (animations != null && animations.length >= 2) {
				this.overridePendingTransition(animations[0], animations[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public Fragment openPage(String pageName, Bundle bundle, int[] anim, boolean newActivity, boolean addToBackStack, boolean checkDoubleClick) {
		PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, newActivity, addToBackStack);
		page.setCheckDoubleClick(checkDoubleClick);
		return openPage(page);
	}

	@Override
	public Fragment openPage(String pageName, Bundle bundle, Anim anim, boolean newActivity, boolean addToBackStack, boolean checkDoubleClick) {
		PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, newActivity, addToBackStack);
		page.setCheckDoubleClick(checkDoubleClick);
		return openPage(page);
	}

	@Override
	public Fragment openPage(PageSwitchBean page) {
		if (page.checkDoubleClick()) {
			boolean isDoubleClick = checkDoubleOpenPage(page);
			Log.e(TAG, "openPage.isDoubleClick="+isDoubleClick);
			if (isDoubleClick) {
				Log.e(TAG, "isDoubleClick true");
				return null;
			}
		}
		boolean addToBackStack = page.isAddToBackStack();
		boolean newActivity = page.isNewActivity();
		boolean showAnimation = page.getAnims() != null;
		Bundle bundle = page.getBundle();
		int[] animations = null;
		if (showAnimation) {
			animations = page.getAnims();
		}
		if (newActivity) {
			startActivity(page);
			return null;
		} else {
			String pageName = page.getPageName();
			return FusionPageManager.getInstance().openPageWithNewFragmentManager(showAnimation, getSupportFragmentManager(), pageName, bundle, animations, addToBackStack);
		}
	}

	@Override
	public boolean isFragmentTop(String fragmentTag) {
		int size = mCustomActivities.size();
		if (size > 0) {
			WeakReference<TripStubActivity> ref = mCustomActivities.get(size - 1);
			TripStubActivity item = ref.get();
			if (item != null && item == this) {// 位于顶部
				FragmentActivity activity = (FragmentActivity) item;
				FragmentManager mFragmentManager = activity.getSupportFragmentManager();
				if (mFragmentManager != null) {
					int count = mFragmentManager.getBackStackEntryCount();
					if (count >= 1) {
						BackStackEntry entry = mFragmentManager.getBackStackEntryAt(count - 1);
						if (entry.getName().equalsIgnoreCase(fragmentTag)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void popPage() {
		popOrFinishActivity();
	}

	@Override
	public Fragment gotoPage(PageSwitchBean page) {
		if (page == null) {
			// 后退一步，没有fragment时关闭activity
			popOrFinishActivity();
		} else {
			popAndRefreshData(page);
		}
		return null;
	}

	public void removeUselessFragment(ArrayList<String> fragmentList) {
		if (this instanceof FragmentActivity && !mIsActivityFinish) {
			FragmentActivity activity = (FragmentActivity) this;
			FragmentManager mFragmentManager = activity.getSupportFragmentManager();

			if (mFragmentManager != null) {
				FragmentTransaction trans = mFragmentManager.beginTransaction();
				for (String tag : fragmentList) {
					Fragment frg = mFragmentManager.findFragmentByTag(tag);
					if (frg != null) {
						trans.remove(frg);
					}
				}
				trans.commitAllowingStateLoss();
				int count = mFragmentManager.getBackStackEntryCount();
				if (count == 0) {
					this.finish();
				}
			}
		}
	}

}
