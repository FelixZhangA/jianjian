package com.wanxiang.recommandationapp.controller;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.wanxiang.recommandationapp.ui.base.IPageSwitcher;
import com.wanxiang.recommandationapp.ui.base.PageSwitchBean;
import com.wanxiang.recommandationapp.ui.base.TripBaseFragment.Anim;
import com.wanxiang.recommandationapp.ui.base.TripStubActivity;

/**
 * 总线服务，供UI层调用，负责处理message的分发
 * 
 * @author xianye
 * 
 */
public class FusionBus {

	private static FusionBus sFusionBus;
	private static final Boolean sBlock = true;
	private static final String TAG = "FusionBus";
	private Context mContext;

	private Queue<FusionMessage> mFusionMessageQueue;

	// private int mHttpThreadCount = 3;//线程池数量

	private final ThreadFactory mThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "FusionBusThread #"
					+ mCount.getAndIncrement());
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return thread;
		}
	};

	private final ExecutorService mExecutor = Executors
			.newCachedThreadPool(mThreadFactory);

	private FusionServiceManager mServiceManager;
//	private FragmentManager mFragmentManager;
	private volatile boolean mRunning = true;

	private final Thread mBusThread = new Thread() {

		@Override
		public void run() {

			if (mServiceManager == null) {
				mServiceManager = new FusionServiceManager(mContext);
			}

			while (true) {
				while (mRunning && mFusionMessageQueue.isEmpty()) {
					try {
						synchronized (this) {
							this.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (!mRunning) {
					break;
				} else if (!mFusionMessageQueue.isEmpty()) {
					FusionMessage msg = mFusionMessageQueue.remove();
					FusionMessageHandler handler = new FusionMessageHandler(
							mServiceManager.getService(msg.getService()), msg,
							msg.getFusionCallBack());
					mExecutor.execute(handler);
				}
			}
		}
	};

	public void shutDown() {
		mRunning = false;
		mFusionMessageQueue.clear();
		if (mExecutor != null) {
			mExecutor.shutdownNow();
		}
		synchronized (mBusThread) {
			mBusThread.notifyAll();
		}
	}

	private void init() {
		FusionPageManager.getInstance().init(mContext);

		mFusionMessageQueue = new ConcurrentLinkedQueue<FusionMessage>();

		mBusThread.setDaemon(true);
		mBusThread.setPriority(Thread.NORM_PRIORITY + 2);
		mBusThread.start();

		// while(!mBusThread.isAlive());
	}

	public static FusionBus getInstance(Context context) {
		synchronized (sBlock) {
			if (sFusionBus == null) {
				sFusionBus = new FusionBus(context);
				sFusionBus.init();
			}
		}
		return sFusionBus;
	}

	private FusionBus(Context context) {
		this.mContext = context.getApplicationContext();
	}

//	
//	public Fragment openPage(boolean newActivity, Context context,
//			FusionMessage msg, boolean addToBackStack) {
//		if (msg != null && msg.getScheme() == FusionMessage.SCHEME.Page) {
//			if (context instanceof IJumpListerner) {
//				msg.setParam(Constants.ADD_BACK_STATE, addToBackStack);
//				msg.setParam(Constants.NEW_ACTIVITY, newActivity);
//				if(!msg.containParam(Constants.NEED_DOUBLECHECK)){
//					msg.setParam(Constants.NEED_DOUBLECHECK, "true");
//				}
//				return ((IJumpListerner) context).openPage(msg);
//			} else {
//				Log.e(TAG, "context not IJumpListerner");
//			}
//		}
//
//		Log.e(TAG, "context not IJumpListerner");
//		return null;
//	}
	

	public Fragment openPage(Context context, FusionMessage msg,boolean addToBackStack) {
		return openPage(false, context, msg, addToBackStack);
	}


	/**
	 * 异步发送消息
	 * @param msg
	 */
	public void sendMessage(FusionMessage msg) {
		if (FusionMessage.SCHEME.Page.equals(msg.getScheme())) {
			// openPage(this.mContext,msg,true);
			Log.d(TAG, "use openPage instead");
		} else {
			mFusionMessageQueue.add(msg);
			synchronized (mBusThread) {
				mBusThread.notifyAll();
			}
		}
	}

	public void sendMessageArray(List<FusionMessage> list) {
		if (list != null) {
			for (FusionMessage msg : list) {
				sendMessage(msg);
			}
		}
	}

	public void cancelMessage(FusionMessage msg) {
		cancelMsg(msg);
	}

	public void cancelMessageArray(List<FusionMessage> list) {
		cancelMsgList(list);
	}

	private void cancelMsg(FusionMessage msg) {
		if (msg != null && !msg.isCancel()) {
			mFusionMessageQueue.remove(msg);
			msg.cancel();

			// cancelMsgList(msg.getChildren());//取消子项消息
			// cancelMsg(msg.getParent());//取消父项消息
		}
	}

	private void cancelMsgList(List<FusionMessage> list) {
		if (list != null) {
			for (FusionMessage msgItem : list) {
				cancelMsg(msgItem);
			}
		}
	}


	public IPageSwitcher getPageSwitcher(Context context) {
		IPageSwitcher pageSwitcher = null;
		synchronized (FusionBus.this) {//加强保护，保证mIJumpListerner 不为null
			if(pageSwitcher==null){
				if(context instanceof  IPageSwitcher){
					pageSwitcher = (IPageSwitcher) context ; 
				}
				if(pageSwitcher == null){
					pageSwitcher = TripStubActivity.getTopActivity();
				}
			}
		}	
		return pageSwitcher;
	}


	public Fragment gotoPage(Context context, String pageName, Bundle bundle, Anim anim) {
		IPageSwitcher pageSwitcher = this.getPageSwitcher(context);
		if (pageSwitcher !=null) {
			PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, false, false);
			return pageSwitcher.gotoPage(page);
		} else {
			Log.e(TAG, "context not IJumpListerner");
		}

		return null;
	}

	public Fragment openPage(Context context, String pageName, Bundle bundle, Anim anim, boolean addToBackStack) {
		return this.openPage(false, context, pageName, bundle, anim, addToBackStack);
	}
	
	/**
	 * 新建一个页面（Fragemnt）
	 * @param newActivity  该页面是否新建一个Activity
	 * @param context 所要跳转的Activity环境
	 * @param pageName  Fragemnt 名，在在configure.zip 的pageContext.txt中配置。
	 * @param bundle  页面跳转时传递的参数
	 * @param anim  指定的动画理性  none/slide(左右平移)/present(由下向上)/fade(fade 动画)
	 * @param addToBackStack  是否添加到用户操作栈中
	 * @return
	 */
	public Fragment openPage(boolean newActivity, Context context, String pageName, Bundle bundle, Anim anim, boolean addToBackStack) {
		return this.openPage(newActivity, context, pageName, bundle, anim, addToBackStack, true);
	}
	
	public Fragment openPage(boolean newActivity, Context context,FusionMessage msg, boolean addToBackStack) {
		if(msg!=null){
			IPageSwitcher pageSwitcher = this.getPageSwitcher(context);
			if (pageSwitcher !=null) {
				String pageName = msg.getActor();
				Bundle bundle = FusionPageManager.getBundleFromMsg(msg);
				int[] anims = msg.getAnimations();
				PageSwitchBean page = new PageSwitchBean(pageName, bundle, anims, newActivity, addToBackStack);
				return pageSwitcher.openPage(page);
			}else{
				Log.e(TAG, "context not pageSwitcher");
			}
		}else{
			Log.e(TAG, "FusionMessage is null");
		}
		return null;
	}
	
	public Fragment openPage(boolean newActivity, Context context, String pageName, Bundle bundle, Anim anim, boolean addToBackStack,boolean needDoubleCheck) {
		IPageSwitcher pageSwitcher = this.getPageSwitcher(context);
		if (pageSwitcher !=null) {
			PageSwitchBean page = new PageSwitchBean(pageName, bundle, anim, newActivity, addToBackStack);
			page.setCheckDoubleClick(needDoubleCheck);
			return pageSwitcher.openPage(page);
		} else {
			Log.e(TAG, "context not pageSwitcher");
		}
		return null;
	}

}
