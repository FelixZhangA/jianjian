package com.wanxiang.recommandationapp.controller;

/**
 * 消息回调类，处理返回的数据和UI状态的更新
 * 
 * @author xianye
 * 
 */
public abstract class FusionCallBack {

	/**
	 * 异步任务开始执行时回调
	 */
	public void onStart() {
	};

	/**
	 * 异步任务结束执行时回调，可以通过FusionMessage类的 getResponseData()方法获取相应的结果数据
	 */
	public void onFinish(final FusionMessage msg) {
	};

	/**
	 * 任务执行失败时回调，可以通过FusionMessage 获取相应的 mErrorCode、mErrorMsg和mErrorDesc;
	 * @param msg
	 */
	public void onFailed(final FusionMessage msg) {
	};

	/**
	 * 取消任务开始执行时回调
	 */
	public void onCancel() {
	};

	@Deprecated
	/**
	 * 将要去掉该函数，请不要使用
	 * @param msg
	 */
	public void processCallbackMessage(final FusionMessage msg) {
	};

	/**
	 * 任务执行时回调，可以通过FusionMessage类的getProgressBean（）方法获取相应的进度信息
	 */
	public void onProgress(final FusionMessage msg) {
	};
}
