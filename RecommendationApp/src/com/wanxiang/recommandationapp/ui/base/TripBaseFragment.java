package com.wanxiang.recommandationapp.ui.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.controller.FusionPageManager;
import com.wanxiang.recommandationapp.util.Constants;
import com.wanxiang.recommandationapp.util.Utils;

public class TripBaseFragment extends Fragment {

	private static final String TAG = TripBaseFragment.class.getSimpleName();

	public static enum Anim {
		none, /** value=-1, 没有动画 */
		city_guide, /** value=0 ,city guide 应用默认动画 */
		present, /** value=1 , 由下到上动画 */
		slide, /** value=2 ,present Android 不实现使用默认的 ANIMATION_CITY_GUIDE */
		fade, /** value=3 ,渐变 */
		zoom_out,
		/** value=4 ,zoom out Android 不实现使用默认的 ANIMATION_CITY_GUIDE */
		slide_inverse
	};

	private onFragmentFinishListener mFragmentFinishListener;

	private int mRequestCode;
	protected Activity mAct;
	private String mPageName;
	public boolean mIsFragmentFinish = false;
	private List<FusionMessage> msgList = new ArrayList<FusionMessage>();

	private String mOpenPageName;
	private long mOpenPageTime = 0l;
	private boolean hasAutoLogin =false;

	/**
	 * 页面跳转接口
	 */
	private IPageSwitcher mPageSwitcher;

	public void setPageSwitcher(IPageSwitcher pageSwitcher) {
		this.mPageSwitcher = pageSwitcher;
	}

	public IPageSwitcher getPageSwitcher() {
		synchronized (TripBaseFragment.this) {// 加强保护，保证pageSwitcher 不为null
			if (mPageSwitcher == null) {
				if (this.mAct != null && this.mAct instanceof IPageSwitcher) {
					mPageSwitcher = (IPageSwitcher) this.mAct;
				}
				if (mPageSwitcher == null) {
					TripStubActivity topActivity = TripStubActivity.getTopActivity();
					if (topActivity != null && topActivity instanceof IPageSwitcher) {
						mPageSwitcher = (IPageSwitcher) topActivity;
					}
				}
			}
		}
		return mPageSwitcher;
	}

	public void setPageName(String pageName) {
		this.mPageName = pageName;
	}

	protected String getPageName() {
		return this.mPageName;
	}

	public interface onFragmentFinishListener {
		void onFragmentResult(int requestCode, int resultCode, Intent intent);
	}

	public void setFragmentFinishListener(onFragmentFinishListener listener) {
		this.mFragmentFinishListener = listener;
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		return super.onCreateAnimation(transit, enter, nextAnim);
	}

	public void setRequestCode(int code) {
		this.mRequestCode = code;
	}

	public int getRequestCode() {
		return this.mRequestCode;
	}

	public void onFragmentResult(int requestCode, int resultCode, Intent data) {
	}

	public void setFragmentResult(int resultCode, Intent intent) {
		if (mFragmentFinishListener != null) {
			mFragmentFinishListener.onFragmentResult(mRequestCode, resultCode, intent);
		}
	}

	public void sendMessage(FusionMessage msg) {
		msgList.add(msg);
		FusionBus.getInstance(mAct).sendMessage(msg);
	}

	public void cancelMessage(FusionMessage msg) {
		FusionBus.getInstance(mAct).cancelMessage(msg);
	}

	/**
	 * 跳转到用户操作栈中的某一个fragemnt（可以跨越Activity栈）
	 * 
	 * @param pageName
	 *            fragemnt名
	 * @param bundle
	 *            跳转到fragemnt后同时传递的参数
	 */
	public final void popToBack(String pageName, Bundle bundle) {
		IPageSwitcher pageSwitcher = getPageSwitcher();
		if (pageSwitcher != null) {
			if (pageName == null) {
				pageSwitcher.popPage();
			} else {
				if(this.findPage(pageName)){
					PageSwitchBean page = new PageSwitchBean(pageName, bundle);
					pageSwitcher.gotoPage(page);
				}else{
					pageSwitcher.popPage();
				}
			}
		} else {
			Log.d(TAG, "pageSwitcher null");
		}

	}

	/**
	 * 弹出栈顶的Fragment。如果Activity中只有一个Fragemnt时，Acitivity也退出。
	 */
	public void popToBack() {
		popToBack(true);
	}

	public void popToBack(boolean needDoubleChedk) {
//		if (needDoubleChedk && Utils.isFastDoubleClick()) {// 连续点击取消时，多余出现输入框
//			Log.d(TAG, "isFastDoubleClick true");
//			return;
//		}
		this.popToBack(null, null);
	}

	public Fragment openPageWithNewFragmentManager(FragmentManager mFragmentManager, String pageName, Bundle bundle, int[] animations, boolean addToBackStack) {
		if (!checkGapTime(pageName)) {
			return null;
		}
		Fragment frg = FusionPageManager.getInstance().openPageWithNewFragmentManager(false, mFragmentManager, pageName, bundle, animations, addToBackStack);
		return frg;
	}

	public final Fragment openPageForResultWithNewFragmentManager(FragmentManager mFragmentManager, String pageName, Bundle bundle, Anim anim, boolean addToBackStack, int requestCode) {
		TripBaseFragment frg = (TripBaseFragment) this.openPageWithNewFragmentManager(mFragmentManager, pageName, bundle, getAnimations(anim), addToBackStack);
		if (frg == null) {
			return frg;
		}
		final TripBaseFragment opener = this;
		frg.setRequestCode(requestCode);
		frg.setFragmentFinishListener(new onFragmentFinishListener() {
			@Override
			public void onFragmentResult(int requestCode, int resultCode, Intent intent) {
				opener.onFragmentResult(requestCode, resultCode, intent);
			}
		});
		return frg;
	}

	public final Fragment openPage(String pageName, Bundle bundle, Anim anim, boolean addToBackStack) {

		return openPage(false, pageName, bundle, anim, addToBackStack);
	}

	public final Fragment openPage(String pageName, Bundle bundle, Anim anim) {
		return this.openPage(false, pageName, bundle, anim, true);
	}

	public final Fragment openPage(boolean newActivity, String pageName, Bundle bundle, Anim anim) {
		return this.openPage(newActivity, pageName, bundle, anim, true);
	}

	/**
	 * 新建一个页面（Fragemnt）
	 * 
	 * @param newActivity
	 *            该页面是否新建一个Activity
	 * @param pageName
	 *            Fragemnt 名，在在configure.zip 的pageContext.txt中配置。
	 * @param bundle
	 *            页面跳转时传递的参数
	 * @param anim
	 *            指定的动画理性 none/slide(左右平移)/present(由下向上)/fade(fade 动画)
	 * @param addToBackStack
	 *            是否添加到用户操作栈中
	 * @return
	 */
	public final Fragment openPage(boolean newActivity, String pageName, Bundle bundle, Anim anim, boolean addToBackStack) {
		if (pageName == null) {
			Log.d(TAG, "pageName is null");
			return null;
		}
		IPageSwitcher pageSwitcher = this.getPageSwitcher();
		if (pageSwitcher != null) {
			PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, newActivity, addToBackStack);
			return pageSwitcher.openPage(page);
		} else {
			Log.d(TAG, "pageSwitcher is null");
			return null;
		}
	}

	public boolean isFragmentTop(String fragmentTag) {
		IPageSwitcher pageSwitcher = this.getPageSwitcher();
		if (pageSwitcher != null) {
			return pageSwitcher.isFragmentTop(fragmentTag);

		} else {
			Log.d(TAG, "pageSwitcher is null");
			return false;
		}
	}

	protected void login(final int requestCode) {
		if(false && hasAutoLogin){
			openLoginPage(requestCode);
		}else{
			FusionMessage loginMsg = new FusionMessage("loginService", "pmslogin");
			loginMsg.setFusionCallBack(new FusionCallBack() {
				@Override
				public void onFinish(FusionMessage msg) {
//					if (LoginManager.hasLogin()) {
//						onLoginFinish(requestCode, Activity.RESULT_OK);
//					} else {
//						openLoginPage(requestCode);
//					}
				}

				@Override
				public void onFailed(FusionMessage msg) {
					openLoginPage(requestCode);
				}
			});
			FusionBus.getInstance(mAct).sendMessage(loginMsg);
			hasAutoLogin = true;
		}
	}

	protected void openLoginPage(final int requestCode) {
		if (FusionPageManager.getInstance().isFragmentTop(mAct, "login")) {
			return;
		}
		TripBaseFragment newFragment = (TripBaseFragment) this.openPage(false,"login", null, Anim.present);
		if (newFragment != null) {
			newFragment.setRequestCode(requestCode);
			newFragment.setFragmentFinishListener(new onFragmentFinishListener() {
				@Override
				public void onFragmentResult(int requestCode, int resultCode, Intent intent) {
					onLoginFinish(requestCode, resultCode);
				}
			});
		}
	}

	protected void logout(FusionCallBack callback) {
		FusionMessage logoutMsg = new FusionMessage("loginService", "logout");
		logoutMsg.setFusionCallBack(callback);
		FusionBus.getInstance(mAct).sendMessage(logoutMsg);
	}

	protected void onLoginFinish(int requestCode, int resultCode) {

	}

	public final Fragment openPageForResult(boolean newActivity, String pageName, Bundle bundle, Anim anim, int requestCode) {
		if (!checkGapTime(pageName)) {
			return null;
		}
		IPageSwitcher pageSwitcher = this.getPageSwitcher();
		if (pageSwitcher != null) {
			PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, newActivity);
			page.setRequestCode(requestCode);

			return pageSwitcher.openPageForResult(page, this);
		} else {
			Log.d(TAG, "pageSwitcher is null");
			return null;
		}

	}

	public final Fragment openPageForResult(String pageName, Bundle bundle, Anim anim, int requestCode) {
		return this.openPageForResult(false, pageName, bundle, anim, requestCode);
	}

	/**
	 * 新建或跳转到一个页面（Fragment）。找不到pageName Fragment时，就新建Fragment。找到pageName
	 * Fragment时,则弹出该Fragement到栈顶上的所有actvity和fragment
	 * 
	 * @param newActivity
	 *            该页面是否新建一个Activity
	 * @param pageName
	 *            Fragemnt 名，在在configure.zip 的pageContext.txt中配置。
	 * @param bundle
	 *            页面跳转时传递的参数
	 * @param anim
	 *            指定的动画理性 none/slide(左右平移)/present(由下向上)/fade(fade 动画)
	 * @return
	 */
	public Fragment gotoPage(boolean newActivity, String pageName, Bundle bundle, Anim anim) {
		if (!checkGapTime(pageName)) {
			return null;
		}
		IPageSwitcher pageSwitcher = this.getPageSwitcher();
		if (pageSwitcher != null) {
			PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, newActivity);
			return pageSwitcher.gotoPage(page);
		} else {

			Log.d(TAG, "pageSwitcher is null");
			return null;
		}
	}

	public Fragment gotoPage(String pageName, Bundle bundle, Anim anim) {
//		if (!checkGapTime(pageName)) {
//			return null;
//		}
		return this.gotoPage(false, pageName, bundle, anim);

	}

	public boolean findPage(String pageName) {
		if (pageName == null) {
			Log.d(TAG, "pageName is null");
			return false;
		}
		IPageSwitcher pageSwitcher = this.getPageSwitcher();
		if (pageSwitcher != null) {
			return pageSwitcher.findPage(pageName);
		} else {
			Log.d(TAG, "pageSwitcher is null");
			return false;
		}

	}

	public Fragment openPage(FusionMessage msg) {
		return openPage(msg, true);
	}

	public Fragment openPage(FusionMessage msg, boolean addToBackStack) {
		return openPage(false, msg, addToBackStack);
	}

	public Fragment openPage(boolean newActivity, FusionMessage msg, boolean addToBackStack) {
		if (msg != null && msg.getScheme() == FusionMessage.SCHEME.Page) {
			IPageSwitcher pageSwitcher = this.getPageSwitcher();
			if (pageSwitcher != null && msg != null) {
				Bundle bundle = FusionPageManager.getBundleFromMsg(msg);
				PageSwitchBean page = new PageSwitchBean(msg.getActor(), bundle, msg.getAnimations(), newActivity, addToBackStack);
				return pageSwitcher.openPage(page);
			} else {
				Log.d(TAG, "pageSwitcher is null");
				return null;
			}
		} else {
			Log.e(TAG, "msg SCHEME is error or null");
		}
		return null;
	}

	private boolean checkGapTime(String pageName) {
		long currentTime = System.currentTimeMillis();
		if (pageName != null && pageName.equals(mOpenPageName) && Math.abs(currentTime - this.mOpenPageTime) < 500) {
			Log.d(TAG, "GapTime is too short.");
			return false;
		}
		this.mOpenPageName = pageName;
		this.mOpenPageTime = currentTime;
		return true;
	}

	protected int[] getAnimations(Anim anim) {

		if (anim == Anim.city_guide) {
			int[] animations = { R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right };
			return animations;
		} else if (anim == Anim.present) {
			int[] animations = { R.anim.push_in_down, R.anim.push_out_down, R.anim.push_in_down, R.anim.push_out_down };
			return animations;
		} else if (anim == Anim.fade) {
			int[] animations = { R.anim.alpha_in, R.anim.alpha_out, R.anim.alpha_in, R.anim.alpha_out };
			return animations;
		} else if (anim == Anim.slide) {
			int[] animations = { R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right };
			return animations;
		} else if (anim == Anim.slide_inverse) {
			int[] animations = { R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left };
			return animations;
		}
		return null;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAct = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("TBS.Page", "------TBS.Page.create( " + mPageName + " )-------");
//		if (Constants.MEMORY_CHECK_OPEN) {
//			MemoryChecker.getInstance().addCreatedObject(this);
//		}
		if(getPageName()!=null){
			Log.d("TBS", "====TBS.onCreate====" + getPageName());
//			TBS.Page.enterWithPageName(this.getClass().getName(), getPageName());
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		FusionBus.getInstance(mAct).cancelMessageArray(msgList);
	}

	@Override
	public void onDestroy() {
		if (Constants.MEMORY_CHECK_OPEN) {
//			MemoryChecker.getInstance().addFinishedObject(this);
		}
		mIsFragmentFinish = true;
		super.onDestroy();
		
		if(getPageName()!=null){
			Log.d("TBS", "====TBS.onDestroy====" + getPageName());
//			TBS.Page.leave(this.getClass().getName());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		mIsFragmentFinish = false;
		super.onResume();
	}

	public void onPageResume() {
		if (!TextUtils.isEmpty(this.getPageName())) {
			// TBS.Page.enter(this.getClass().getName());
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	public void onFragmentDataReset(Bundle bundle) {

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	public void setTitle(int center) {
		if (this.getView() == null) {
			return;
		}
//		View view = this.getView().findViewById(R.id.trip_tv_title);
//		if (null != view) {
//			if (view instanceof TextSwitcher) {
//				TextSwitcher ts = (TextSwitcher) view;
//				if (ts.getChildCount() < 1) {
//					ts.setFactory(mTitleSwitchFactory);
//				}
//				ts.setText(getString(center));
//			} else if (view instanceof TextView) {
//				TextView tv = (TextView) view;
//				tv.setText(getString(center));
//			}
//		}
	}

	public void setTitle(String center) {
		if (this.getView() == null) {
			return;
		}
//		TextSwitcher textView = (TextSwitcher) this.getView().findViewById(R.id.trip_tv_title);
//		if (null != textView) {
//			if (textView.getChildCount() < 1) {
//				textView.setFactory(mTitleSwitchFactory);
//			}
//
//			textView.setText(center);
//		}
	}

	public void onActionbarHomeBack() {
		popToBack();
	}

	public void setTitle(int left, int center, int right) {
		if (this.getView() == null) {
			return;
		}
//		ImageButton leftBtn = (ImageButton) this.getView().findViewById(R.id.trip_btn_title_left);
//		if (leftBtn != null) {
//			leftBtn.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (!TextUtils.isEmpty(getPageName())) {
//						TBS.Adv.ctrlClicked(getPageName(), CT.Button, "return");
//					}
//					onActionbarHomeBack();
//				}
//			});
//
//			if (0 == left) {
//				leftBtn.setVisibility(View.GONE);
//			} else {
//				leftBtn.setVisibility(View.VISIBLE);
//				leftBtn.setImageResource(left);
//			}
//		}
//
//		TextSwitcher textView = (TextSwitcher) this.getView().findViewById(R.id.trip_tv_title);
//		if (null != textView) {
//			if (textView.getChildCount() < 1) {
//				textView.setFactory(mTitleSwitchFactory);
//			}
//			textView.setText(getString(center));
//		}
//
//		TextView subTitleTV = (TextView) this.getView().findViewById(R.id.trip_tv_subTitle);
//		if (null != subTitleTV) {
//			subTitleTV.setVisibility(View.GONE);
//		}
//
//		ImageButton rightBtn = (ImageButton) this.getView().findViewById(R.id.trip_btn_title_right);
//		if (null != rightBtn) {
//			if (0 == right) {
//				rightBtn.setVisibility(View.GONE);
//			} else {
//				rightBtn.setVisibility(View.VISIBLE);
//				rightBtn.setImageResource(right);
//			}
//		}
	}

	public void setTitle(View view, int left, String center, int right) {
		if (view == null) {
			return;
		}
//		ImageButton leftBtn = (ImageButton) view.findViewById(R.id.trip_btn_title_left);
//		if (leftBtn != null) {
//			if (0 == left) {
//				leftBtn.setVisibility(View.GONE);
//			} else {
//				leftBtn.setVisibility(View.VISIBLE);
//				leftBtn.setImageResource(left);
//			}
//
//			leftBtn.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (!TextUtils.isEmpty(getPageName())) {
//						TBS.Adv.ctrlClicked(getPageName(), CT.Button, "Back");
//					}
//					onActionbarHomeBack();
//				}
//
//			});
//		}
//
//		TextSwitcher textView = (TextSwitcher) view.findViewById(R.id.trip_tv_title);
//		if (null != textView) {
//			if (textView.getChildCount() < 1) {
//				textView.setFactory(mTitleSwitchFactory);
//			}
//			if (!TextUtils.isEmpty(center)) {
//				textView.setText(center);
//			}
//		}
//
//		TextView subTitleTV = (TextView) this.getView().findViewById(R.id.trip_tv_subTitle);
//		if (null != subTitleTV) {
//			subTitleTV.setVisibility(View.GONE);
//		}
//
//		ImageButton rightBtn = (ImageButton) view.findViewById(R.id.trip_btn_title_right);
//		if (null != rightBtn) {
//			if (0 == right) {
//				rightBtn.setVisibility(View.GONE);
//			} else {
//				rightBtn.setVisibility(View.VISIBLE);
//				rightBtn.setImageResource(right);
//			}
//		}
	}

	public void setTitle(int left, String center, int right) {
		this.setTitle(getView(), left, center, right);
	}

	public void setTitle(int left, int center, int right, int animIn, int animOut) {
		if (this.getView() == null) {
			return;
		}

//		setTitle(left, center, right);
//		TextSwitcher textView = (TextSwitcher) this.getView().findViewById(R.id.trip_tv_title);
//
//		textView.setInAnimation(AnimationUtils.loadAnimation(mAct, animIn));
//		textView.setOutAnimation(AnimationUtils.loadAnimation(mAct, animOut));
	}

	public void setTitle(int left, String center, int right, int animIn, int animOut) {
		setTitle(left, center, right);
//		TextSwitcher textView = (TextSwitcher) this.getView().findViewById(R.id.trip_tv_title);
//
//		textView.setInAnimation(AnimationUtils.loadAnimation(mAct, animIn));
//		textView.setOutAnimation(AnimationUtils.loadAnimation(mAct, animOut));
	}

	private ViewFactory mTitleSwitchFactory = new ViewFactory() {

		@Override
		public View makeView() {

			TextView mTextView = new TextView(mAct);
			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//			mTextView.setTextColor(mAct.getResources().getColor(R.color.title_font_color));
			mTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
			// mTextView.setShadowLayer(1.0f, 0, 2,
			// Color.parseColor("#bfffffff"));
			return mTextView;
		}

	};

	public void removeSubTitle() {
		if (this.getView() == null) {
			return;
		}
//		TextView subTitleTV = (TextView) this.getView().findViewById(R.id.trip_tv_subTitle);
//		if (subTitleTV != null) {
//			subTitleTV.setVisibility(View.GONE);
//
//			TextSwitcher textView = (TextSwitcher) this.getView().findViewById(R.id.trip_tv_title);
//			if (textView != null && textView.getCurrentView() != null) {
//				((TextView) textView.getCurrentView()).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//			}
//		}
	}

	public void setSubTitle(String subTitle) {
//		if (this.getView() == null) {
//			return;
//		}
//		TextView subTitleTV = (TextView) this.getView().findViewById(R.id.trip_tv_subTitle);
//		if (subTitleTV != null) {
//			subTitleTV.setVisibility(View.VISIBLE);
//			subTitleTV.setText(subTitle);
//
//			View textView = this.getView().findViewById(R.id.trip_tv_title);
//			if (textView != null && textView instanceof TextSwitcher) {
//				((TextView) ((TextSwitcher) textView).getCurrentView()).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//			}
//		}
	}

	public void setSubTitle(View view, String subTitle) {

//		if (view == null) {
//			return;
//		}
//
//		TextView subTitleTV = (TextView) view.findViewById(R.id.trip_tv_subTitle);
//		if (subTitleTV != null) {
//			subTitleTV.setVisibility(View.VISIBLE);
//			subTitleTV.setText(subTitle);
//
//			TextSwitcher textView = (TextSwitcher) view.findViewById(R.id.trip_tv_title);
//			((TextView) textView.getCurrentView()).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private static DialogClickListener mDialogClickListener;
	private static DialogClickListener mDialogNegativeClickListener;

	public static void setDialogClickListener(DialogClickListener mlistener) {
		mDialogClickListener = mlistener;
	}

	public static void setDialogNegativeClickListener(DialogClickListener mlistener) {
		mDialogNegativeClickListener = mlistener;
	}

	public interface DialogClickListener {
		public abstract void onDialogClickListener();
	}

	public void enableDisableViewGroup(View view, boolean enabled) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			int childCount = viewGroup.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View childView = viewGroup.getChildAt(i);
				childView.setClickable(enabled);
				if (childView instanceof ViewGroup) {
					enableDisableViewGroup((ViewGroup) childView, enabled);
				}
			}
		} else {
			view.setClickable(enabled);
		}
	}

	/**
	 * 左线框，右蓝色按钮dialog
	 * 
	 * @param tip
	 * @param btnStr
	 * @param btnStr1
	 * @param bCancelAble
	 * @param postiClickListener
	 * @param negtiClickListener
	 */
//	public void showTwoButtonBlueDialog(String tip, String btnStr, String btnStr1, DialogClickListener postiClickListener, DialogClickListener negtiClickListener) {
//		showAlertDialog(btnStr, btnStr1, tip, false, 11, postiClickListener, negtiClickListener);
//	}
//
//	/**
//	 * 左线框，右蓝色按钮dialog,可控制cancelable
//	 * 
//	 * @param tip
//	 * @param btnStr
//	 * @param btnStr1
//	 * @param bCancelAble
//	 * @param postiClickListener
//	 * @param negtiClickListener
//	 */
//	public void showTwoButtonBlueDialog(String tip, String left, String right, boolean cancelable, DialogClickListener postiClickListener, DialogClickListener negtiClickListener) {
//		showAlertDialog(left, right, tip, cancelable, 11, postiClickListener, negtiClickListener);
//	}
//
//	/**
//	 * 左线框，右红色按钮dialog
//	 * 
//	 * @param tip
//	 * @param btnStr
//	 * @param btnStr1
//	 * @param bCancelAble
//	 * @param postiClickListener
//	 * @param negtiClickListener
//	 */
//	public void showTwoButtonRedDialog(String tip, String btnStr, String btnStr1, boolean bCancelAble, DialogClickListener postiClickListener, DialogClickListener negtiClickListener) {
//		showAlertDialog(btnStr, btnStr1, tip, bCancelAble, 12, postiClickListener, negtiClickListener);
//	}
//
//	private CustomAlertDialog showAlertDialog(String btnStrLeft, String btnStrRight, String msg_code, boolean bCancelAble, int style, DialogClickListener postiClickListener, DialogClickListener negtiClickListener) {
//		if (mAct.isFinishing()) {
//			return null;
//		}
//
//		mDialogClickListener = null;
//		LayoutInflater mInflater = LayoutInflater.from(mAct);
//		LinearLayout infoView = null;
//		infoView = (LinearLayout) mInflater.inflate(R.layout.dialog_text_list_item, null);
//		// dialog 内容区域文本
//		DialogTextView tv_dialog_text = (DialogTextView) infoView.findViewById(R.id.tv_dialog_text);
//		tv_dialog_text.setText(msg_code);
//		// dialog 初始化
//		CustomAlertDialog mDialog = new CustomAlertDialog(mAct, 0);
//		mDialog.setBottonText(btnStrLeft, btnStrRight);
//		mDialog.setCancelable(bCancelAble);
//
//		mDialog.setCustomDialog(infoView, postiClickListener, negtiClickListener, style);
//		mDialog.show();
//		return mDialog;
//	}
//
//	/**
//	 * 自定义按钮文字单对话框
//	 * 
//	 * @param btnStr
//	 * @param msg_code
//	 * @param style
//	 * @param cancelable
//	 * @param postiClickListener
//	 */
//	public CustomAlertDialog showAlertDialog(String btnStr, String msg_code, int style, boolean cancelable, DialogClickListener postiClickListener) {
//		if (mAct == null || mAct.isFinishing()) {
//			return null;
//		}
//
//		mDialogClickListener = null;
//		LayoutInflater mInflater = LayoutInflater.from(mAct);
//		LinearLayout infoView = null;
//		infoView = (LinearLayout) mInflater.inflate(R.layout.dialog_text_list_item, null);
//		// dialog 内容区域文本
//		TextView tv_dialog_text = (TextView) infoView.findViewById(R.id.tv_dialog_text);
//		tv_dialog_text.setText(msg_code);
//		// dialog 初始化
//		CustomAlertDialog mDialog = new CustomAlertDialog(mAct, 0);
//		mDialog.setButtonText(btnStr);
//		mDialog.setCancelable(cancelable);
//		// mDialog.setCustomDialog(infoView, clickHandler, style);
//		mDialog.setCustomDialog(infoView, postiClickListener, null, style);
//		mDialog.show();
//		return mDialog;
//	}
//
//	/**
//	 * 自定义按钮文字单对话框
//	 * 
//	 * @param btnStr
//	 * @param msg_code
//	 * @param style
//	 * @param cancelable
//	 */
//	public CustomAlertDialog showAlertDialog(String btnStr, String msg_code, int style, boolean cancelable) {
//		if (mAct.isFinishing()) {
//			return null;
//		}
//
//		mDialogClickListener = null;
//		LayoutInflater mInflater = LayoutInflater.from(mAct);
//		LinearLayout infoView = null;
//		infoView = (LinearLayout) mInflater.inflate(R.layout.dialog_text_list_item, null);
//		// dialog 内容区域文本
//		TextView tv_dialog_text = (TextView) infoView.findViewById(R.id.tv_dialog_text);
//		tv_dialog_text.setText(msg_code);
//		// dialog 初始化
//		CustomAlertDialog mDialog = new CustomAlertDialog(mAct, 0);
//		mDialog.setButtonText(btnStr);
//		mDialog.setCancelable(cancelable);
//		mDialog.setCustomDialog(infoView, clickHandler, style);
//		mDialog.show();
//		return mDialog;
//	}
//
//	/**
//	 * 对外暴露单按钮，确定按钮
//	 * 
//	 * @param msg_code
//	 * @param style
//	 * @param cancelable
//	 */
//	public CustomAlertDialog showAlertDialog(String msg_code, int style, boolean cancelable) {
//		mDialogClickListener = null;
//		if (!isDetached()) {
//			return showAlertDialog(getString(R.string.dialog_ok), msg_code, style, cancelable);
//		}
//		return null;
//	}
//
//	private Handler clickHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case -1:
//				if (mDialogClickListener != null) {
//					mDialogClickListener.onDialogClickListener();
//				}
//				// 左侧操作
//				break;
//			case 0:
//				if (mDialogNegativeClickListener != null) {
//					mDialogNegativeClickListener.onDialogClickListener();
//				}
//				// 右侧完成
//				break;
//			case 1:
//				// section处理
//				break;
//			}
//
//		}
//	};
//
//	/**
//	 * 中提示
//	 * 
//	 * @param message
//	 */
//	public void showMiddleTips(String message) {
//		View tipsView = LayoutInflater.from(mAct).inflate(R.layout.trip_flight_second_list_tips, null);
//		TextView tips = (TextView) tipsView.findViewById(R.id.trip_tv_tips);
//		Toast toast = new Toast(mAct);
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		tips.setText(message);
//		toast.setView(tipsView);
//		toast.show();
//	}

	/**
	 * Tab 页面切换时，tab 显示时刷新Tab页数据的回调函数
	 */
	public void onPageRefresh() {

	}

	public void popToUserCenter() {
		Bundle bundle = new Bundle();
		bundle.putInt("tab_index", 4);// 跳到第一个tab
		Constants.JUMP_TO_USERCENTER = true;
		popToBack("home", bundle);
	}

	public void popToMainPage() {
		Bundle bundle = new Bundle();
		bundle.putInt("tab_index", 1);// 跳到第一个tab
		popToBack("home", bundle);
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
