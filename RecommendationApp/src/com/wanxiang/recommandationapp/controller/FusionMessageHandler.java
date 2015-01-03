package com.wanxiang.recommandationapp.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.wanxiang.recommandationapp.controller.FusionMessage.STATE;
import com.wanxiang.recommandationapp.util.Constants;

/**
 * 消息处理线程
 * 
 * @author xianye
 * 
 */
public class FusionMessageHandler implements Runnable {

	private FusionService service;
	private FusionMessage msg;
	private FusionCallBack callback;
	private static long begin = -1;
	private static String serviceName= "";
	public FusionMessageHandler(FusionService service, FusionMessage msg,
			FusionCallBack callback) {
		this.service = service;
		this.msg = msg;
		this.callback = callback;
	}

	private static Handler sCallBackHandler = new Handler(
			Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
		
			Bundle bundle = msg.getData();

			Object o = bundle.get("fusionMsg");
			String state = bundle.getString("state");

			if (o != null && o instanceof FusionMessage) {

				FusionMessage fusionMsg = (FusionMessage) o;
				FusionCallBack callback = fusionMsg.getFusionCallBack();
				if (callback != null && state != null) {
					if (state.equals(STATE.start.name())) {
						callback.onStart();
					} else if (state.equals(STATE.cancel.name())) {
						callback.onCancel();
					} else if (state.equals(STATE.failed.name())) {
						callback.onFailed(fusionMsg);
					} else if (state.equals(STATE.finish.name()) && !fusionMsg.isCancel()) {
						callback.onFinish(fusionMsg);
						if(Constants.STATISTIC_OPEN){
//							StatManager.getInstance().addStat(fusionMsg, serviceName, System.currentTimeMillis() - begin);
						}
					} else if (state.equals(STATE.progress.name())) {
						callback.onProgress(fusionMsg);
					}
				}
			}
		}
	};

	@Override
	public void run() {
		
		begin = System.currentTimeMillis();
	
		if (checkCancelOrFailed(msg)) {
			return;
		}
		msg.start();// 开始

		if (checkCancelOrFailed(msg)) {
			Log.d(serviceName, "=====msg State is"+msg.getState().name()+"====");
			return;
		}
		boolean bSyn = true;
		if (this.service != null) {// 执行
			bSyn = this.service.processFusionMessage(msg);
			serviceName = service.getClass().getSimpleName();
			msg.setSynchronized(bSyn);
		} else {
			msg.setError(FusionMessage.ERROR_CODE_PARAMS_ERROR,
					FusionMessage.ERROR_MSG_PARAMS_ERROR,
					FusionMessage.ERROR_MSG_PARAMS_ERROR);
			return;
		}

		if (checkCancelOrFailed(msg)) {
			return;
		}
		if (this.callback != null && bSyn) { // 回调
			this.callback.processCallbackMessage(this.msg);
		}
		if (bSyn) {
			msg.finish();// 结束
			//用于统计业务逻辑性能	
		}
	}

	private boolean checkCancelOrFailed(FusionMessage msg) {
		if (msg != null) {
			if (msg.isCancel() || msg.isFailed()) {
				return true;
			}
		}
		return false;
	}

	public static void publishMessageStatus(FusionMessage fusionMessage,
			STATE state) {
		if (fusionMessage != null && fusionMessage.getFusionCallBack() != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("fusionMsg", fusionMessage);
			bundle.putString("state", state.name());
			Message msg = new Message();
			msg.setData(bundle);
			sCallBackHandler.sendMessage(msg);
		}

	}

}
