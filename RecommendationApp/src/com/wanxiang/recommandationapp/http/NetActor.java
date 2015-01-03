package com.wanxiang.recommandationapp.http;

import java.util.LinkedList;

import android.os.Handler;
import android.os.Looper;

import com.wanxiang.recommandationapp.controller.FusionActor;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.util.Constants;
import com.wanxiang.recommandationapp.util.Utils;
import com.wanxiang.recommandationapp.util.memcheck.MemoryChecker;

public abstract class NetActor extends FusionActor {

	private static volatile boolean mIsShowLoginFragment = false;
	private static final String KEY_RELOADED = "key_reloaded";
	private static LinkedList<FusionMessage> mSSOInvalidMsgList = new LinkedList<FusionMessage>();

	protected boolean isSSOInvalid(int errorCode, String errorMsg, String errorDescription) {
		return Constants.SSO_INVALID.equalsIgnoreCase(errorMsg) || "ERRCODE_AUTH_REJECT".equalsIgnoreCase(errorMsg) || "FAIL_SYS_SESSION_EXPIRED:".equals(errorMsg)
				|| "ERR_SID_INVALID".equals(errorMsg);
	}

	private static Handler mSafeHandler = new Handler(Looper.getMainLooper());

	private void reloadNetTaskMsg() {
		synchronized (MemoryChecker.class) {
			for (FusionMessage msg : mSSOInvalidMsgList) {
				FusionBus.getInstance(context).sendMessage(msg);
			}
			if (mSSOInvalidMsgList != null) {
				mSSOInvalidMsgList.clear();
				mIsShowLoginFragment = false;
			}
		}
	}

	private void notifyNetTaskFailed(boolean isUserChanged) {
		synchronized (MemoryChecker.class) {
			for (FusionMessage msg : mSSOInvalidMsgList) {
				if (isUserChanged) {
					msg.setErrorWithoutNotify(FusionMessage.ERROR_CODE_USER_CHANGED, FusionMessage.ERROR_MSG_USER_CHANGED, FusionMessage.ERROR_MSG_USER_CHANGED);
				}
				msg.publishMessageError(msg.getErrorCode(), msg.getErrorMsg(), msg.getErrorDesp());
			}
			if (mSSOInvalidMsgList != null) {
				mSSOInvalidMsgList.clear();
				mIsShowLoginFragment = false;
			}
		}
	}

	protected void autologinAndAsyncNotifyError(final FusionMessage msg) {
		final int errorCode = msg.getErrorCode();
		final String errorMsg = msg.getErrorMsg();
		final String errorDescription = msg.getErrorDesp();
		if (!isSSOInvalid(errorCode, errorMsg, errorDescription)) {
			msg.publishMessageError(errorCode, errorMsg, errorDescription);
		} else {// 进行自动登录
			synchronized (MemoryChecker.class) {
				mSSOInvalidMsgList.add(msg);
				if (mIsShowLoginFragment) {
					return;
				}
				mIsShowLoginFragment = true;
//				mSafeHandler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						new LoginUtil(null).refreshLogin(new LoginListener() {
//							@Override
//							public void onSuccess(boolean isUserChanged) {
//								mIsShowLoginFragment = false;
//								if (isUserChanged) {
//									Utils.broadcastUserChanged();
//									// TODO:账户切换时
//									notifyNetTaskFailed(true);
//								} else {
//									Boolean reloaded = (Boolean) msg.getParam(KEY_RELOADED);
//									if (reloaded != null && reloaded) {
//										notifyNetTaskFailed(false);
//									} else {
//										msg.setParam(KEY_RELOADED, Boolean.TRUE);
//										reloadNetTaskMsg();
//									}
//								}
//							}
//
//							public void onCancel() {
//								notifyNetTaskFailed(false);
//							}
//
//							@Override
//							public void onStart() {
//								// mIsShowLoginFragment = true;
//							}
//						});
//					}
//				});
			}
		}
	}

}
