package com.wanxiang.recommandationapp.ui.base;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.ui.ActivityManager;
import com.wanxiang.recommandationapp.ui.base.TripBaseFragment.DialogClickListener;
import com.wanxiang.recommandationapp.util.Constants;
import com.wanxiang.recommandationapp.util.Utils;

public class TripBaseActivity extends FragmentActivity {

	private static final String TAG = Constants.TAG;
//	protected TripBaseFragment mActiveFragment;
	public boolean mIsActivityFinish = false;
//	public final static String DELAY_SET_PAGE_NAME = "DELAY_SET_PAGE_NAME";

	@Override
	public void onBackPressed() {
		if (this.getSupportFragmentManager().getBackStackEntryCount() == 1) {
			this.finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.d("TripBaseActivity", "onConfigurationChanged");
	}



	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface SaveWithActivity {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null != savedInstanceState) {
			loadActivitySavedData(savedInstanceState);
		}
//		if (Constants.MEMORY_CHECK_OPEN) {
//			MemoryChecker.getInstance().addCreatedObject(this);
//		}
		ActivityManager.getInstance().addActicity(this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause------------");
		super.onPause();
		TripBaseFragment activeFragment = getActiveFragment();
		if (null != activeFragment) {
			activeFragment.onHiddenChanged(true);
		}
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop------------");
		super.onStop();

//		if (!Utils.isAppOnForeground(this)) { // 切换到后台
//			TripApplication.setBackgroundTime(System.nanoTime());
//			TripApplication.setIsBackground(true);
//		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
	}

	public TripBaseFragment getActiveFragment() {
		if(this.isFinishing()){
			return null;
		}
		FragmentManager manager = this.getSupportFragmentManager();
		if(manager!=null){
			int nCount = manager.getBackStackEntryCount();
			if (nCount > 0) {
				String tag = manager.getBackStackEntryAt(nCount - 1).getName();
				return (TripBaseFragment) manager.findFragmentByTag(tag);
			}
		}
		return null;
	}

//	protected void refreshActiveFragment() {
//		TripBaseFragment active = getActiveFragment();
//		if (active == null) {
//			mActiveFragment = null;
//		}
//	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Field[] fields = this.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		Annotation[] ans;
		for (Field f : fields) {
			ans = f.getDeclaredAnnotations();
			for (Annotation an : ans) {
				if (an instanceof SaveWithActivity) {
					try {
						Object o = f.get(this);
						if (o == null) {
							continue;
						}
						String fieldName = f.getName();
						if (o instanceof Integer) {
							outState.putInt(fieldName, f.getInt(this));
						} else if (o instanceof String) {
							outState.putString(fieldName, (String) f.get(this));
						} else if (o instanceof Long) {
							outState.putLong(fieldName, f.getLong(this));
						} else if (o instanceof Short) {
							outState.putShort(fieldName, f.getShort(this));
						} else if (o instanceof Boolean) {
							outState.putBoolean(fieldName, f.getBoolean(this));
						} else if (o instanceof Byte) {
							outState.putByte(fieldName, f.getByte(this));
						} else if (o instanceof Character) {
							outState.putChar(fieldName, f.getChar(this));
						} else if (o instanceof CharSequence) {
							outState.putCharSequence(fieldName, (CharSequence) f.get(this));
						} else if (o instanceof Float) {
							outState.putFloat(fieldName, f.getFloat(this));
						} else if (o instanceof Double) {
							outState.putDouble(fieldName, f.getDouble(this));
						} else if (o instanceof String[]) {
							outState.putStringArray(fieldName, (String[]) f.get(this));
						} else if (o instanceof Parcelable) {
							outState.putParcelable(fieldName, (Parcelable) f.get(this));
						} else if (o instanceof Serializable) {
							outState.putSerializable(fieldName, (Serializable) f.get(this));
						} else if (o instanceof Bundle) {
							outState.putBundle(fieldName, (Bundle) f.get(this));
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		super.onSaveInstanceState(outState);
	}

	private void loadActivitySavedData(Bundle savedInstanceState) {
		Field[] fields = this.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		Annotation[] ans;
		for (Field f : fields) {
			ans = f.getDeclaredAnnotations();
			for (Annotation an : ans) {
				if (an instanceof SaveWithActivity) {
					try {
						String fieldName = f.getName();
						@SuppressWarnings("rawtypes")
						Class cls = f.getType();
						if (cls == int.class || cls == Integer.class) {
							f.setInt(this, savedInstanceState.getInt(fieldName));
						} else if (String.class.isAssignableFrom(cls)) {
							f.set(this, savedInstanceState.getString(fieldName));
						} else if (Serializable.class.isAssignableFrom(cls)) {
							f.set(this, savedInstanceState.getSerializable(fieldName));
						} else if (cls == long.class || cls == Long.class) {
							f.setLong(this, savedInstanceState.getLong(fieldName));
						} else if (cls == short.class || cls == Short.class) {
							f.setShort(this, savedInstanceState.getShort(fieldName));
						} else if (cls == boolean.class || cls == Boolean.class) {
							f.setBoolean(this, savedInstanceState.getBoolean(fieldName));
						} else if (cls == byte.class || cls == Byte.class) {
							f.setByte(this, savedInstanceState.getByte(fieldName));
						} else if (cls == char.class || cls == Character.class) {
							f.setChar(this, savedInstanceState.getChar(fieldName));
						} else if (CharSequence.class.isAssignableFrom(cls)) {
							f.set(this, savedInstanceState.getCharSequence(fieldName));
						} else if (cls == float.class || cls == Float.class) {
							f.setFloat(this, savedInstanceState.getFloat(fieldName));
						} else if (cls == double.class || cls == Double.class) {
							f.setDouble(this, savedInstanceState.getDouble(fieldName));
						} else if (String[].class.isAssignableFrom(cls)) {
							f.set(this, savedInstanceState.getStringArray(fieldName));
						} else if (Parcelable.class.isAssignableFrom(cls)) {
							f.set(this, savedInstanceState.getParcelable(fieldName));
						} else if (Bundle.class.isAssignableFrom(cls)) {
							f.set(this, savedInstanceState.getBundle(fieldName));
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		TripBaseFragment activeFragment = getActiveFragment();
		boolean bHandle = false;
		if (activeFragment != null) {
			bHandle = activeFragment.onKeyDown(keyCode, event);
		}
		if (!bHandle) {
			return super.onKeyDown(keyCode, event);
		} else {
			return bHandle;
		}
	}

	@Override
	protected void onDestroy() {
//		if (Constants.MEMORY_CHECK_OPEN) {
//			MemoryChecker.getInstance().addFinishedObject(this);
//		}
		mIsActivityFinish = true;
		super.onDestroy();
//		TBS.Page.destroy(this.getClass().getName());

		ActivityManager.getInstance().removeActivity(this);
	}

	public void showTwoButtonDialog(String title, String tip, String btnStr, String btnStr1, boolean bCancelAble, DialogClickListener postiClickListener, DialogClickListener negtiClickListener) {
		showAlertDialog(title, btnStr, btnStr1, tip, bCancelAble, 1, postiClickListener, negtiClickListener);
	}

	protected void showAlertDialog(String title, String btnStrLeft, String btnStrRight, String msg_code, boolean bCancelAble, int style, DialogClickListener postiClickListener,
			DialogClickListener negtiClickListener) {
//		LayoutInflater mInflater = LayoutInflater.from(this);
//		LinearLayout infoView = null;
//		infoView = (LinearLayout) mInflater.inflate(R.layout.dialog_text_list_item, null);
//		// dialog 内容区域文本
//		DialogTextView tv_dialog_text = (DialogTextView) infoView.findViewById(R.id.tv_dialog_text);
//		tv_dialog_text.setText(msg_code);
//		// dialog 初始化
//		CustomAlertDialog mDialog = new CustomAlertDialog(this, 0);
//		mDialog.setBottonText(btnStrLeft, btnStrRight);
//		mDialog.setCancelable(bCancelAble);
//
//		mDialog.setCustomDialog(infoView, postiClickListener, negtiClickListener, style);
//		mDialog.show();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume------------");
		mIsActivityFinish = false;
		super.onResume();
//		TBS.Page.enter(this.getClass().getName());
		TripBaseFragment mActiveFragment =  getActiveFragment();
		if (null != mActiveFragment) {
			mActiveFragment.onPageResume();
		}

//		if (TripApplication.isBackground()) {
//			TripApplication.setIsBackground(false);
//
//			long elapsedTime = System.nanoTime() - TripApplication.getBackgroundTime();
//			if (elapsedTime / 1000000 > CommonDefine.sBackgroundTimeInteval) { // 超过半小时
//				TripApplication.updateLBS();
//			}
//
//		}
	}

	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Exception e) {
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		try {
			super.startActivityForResult(intent, requestCode);
		} catch (Exception e) {
		}
	}

}
