package com.wanxiang.recommandationapp.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.wanxiang.recommandationapp.util.Utils;

/**
 * 消息对象
 * 
 * @author 弦叶
 * @date 2013-8-16
 */

public class FusionMessage implements Serializable {

	private static final long serialVersionUID = -532960037948673963L;

	public static enum STATE {
		origin, start, finish, cancel, failed, progress
	}

	public static enum SCHEME {
		Page, Native,Bridge, Unknow
	}

	public static final String SCHEME_PAGE = "page";
	public static final String SCHEME_NATIVE = "native";
	public static final String SCHEME_BRIDGE = "bridge";
	public static final String MESSAGE_RETURN_ERROR_CODE = "error_code";
	public static final String MESSAGE_RETURN_ERROR_MSG = "error_msg";
	public static final String MESSAGE_RETURN_INFO = "info";

	// 错误码
	public static final int ERROR_CODE_PARAMS_ERROR = 1;
	public static final int ERROR_CODE_NET_ERROR = 2;
	public static final int ERROR_CODE_PARSER_ERROR = 3;
	public static final int ERROR_CODE_FILE_SYSTEM_ERROR = 4;
	public static final int ERROR_CODE_DATABASE_ERROR = 5;
	public static final int ERROR_CODE_LOCATION_ERROR = 6;
	public static final int ERROR_CODE_SYS_ERROR = 7;
	public static final int ERROR_CODE_SERVER_ERROR = 8;
	public static final int ERROR_CODE_SSO_INVALID = 9;
	public static final int ERROR_CODE_OK = 10;
	public static final int ERROR_CODE_USER_CHANGED = 11;

	// 错误信息
	public static final String ERROR_MSG_PARAMS_ERROR = "params error";
	public static final String ERROR_MSG_NET_ERROR = "似乎未连接到互联网";
	public static final String ERROR_MSG_PARSER_ERROR = "parser error";
	public static final String ERROR_MSG_FILE_SYSTEM_ERROR = "file system error";
	public static final String ERROR_MSG_DATABASE_ERROR = "db error";
	public static final String ERROR_MSG_CODE_LOCATION_ERROR = "location error";
	public static final String ERROR_MSG_SYS_ERROR = "system error";
	public static final String ERROR_MSG_SERVER_ERROR = "server error";
	public static final String ERROR_MSG_SSO_INVALID = "sso invalid";
	public static final String ERROR_MSG_OK = "Ok";
	public static final String ERROR_MSG_USER_CHANGED = "user changed";
	public static final String ERROR_MSG_JSON_BLANK = "ANDROID_SYS_JSONDATA_BLANK";
	
	protected Context context;
	private SCHEME mScheme = SCHEME.Native; // page or native
	private String mService;
	private String mActor;
	private String mParamsForJsonString ;
	
	protected Map<String, Object> mParams = new HashMap<String, Object>();
	private volatile STATE mState = FusionMessage.STATE.origin;
	
	//页面跳转的动画
	private int[] mAnimations = null;
	/**默认值为 -2*/
	private int mRequestCode = -2;

	private FusionCallBack mFusionCallBack;

	// private FusionMessage mParent;
	// private List<FusionMessage> mChildren ;
	// private boolean mSingleFinish = false;

	private boolean syn = true;
	private Object mResponseData;
	private int mErrorCode = ERROR_CODE_OK;
	private String mErrorMsg;
	private String mErrorDesc;
	protected ProgressBean mProgressBean;

	private boolean isSuppourtJson = false;

	public FusionMessage() {
	}

	public FusionMessage(Context context, String service, String actor) {
		this(service, actor, null);

		this.context = context.getApplicationContext();
	}

	/**
	 * 
	 * @param service 执行Actor的相应服务类（此Service不是Android的Service）
	 * @param actor  具体的Actor执行类
	 */
	public FusionMessage(String service, String actor) {
		this(service, actor, null);
	}
	
	public FusionMessage(String pageName){ 
		this.mScheme = SCHEME.Page;
		this.mActor = pageName;
	}
	public FusionMessage(String pageName,int enter, int exit, int popEnter, int popExit){ 
		this.mScheme = SCHEME.Page;
		this.mActor = pageName;
		int[] animations = {enter, exit, popEnter, popExit};
		this.mAnimations = animations;
	}
	public FusionMessage(String pageName,int requestCode ){ 
		this.mScheme = SCHEME.Page;
		this.mActor = pageName;
		this.mRequestCode = requestCode;
	}
	
	public FusionMessage(String pageName,int requestCode, int enter, int exit, int popEnter, int popExit){ 
		this.mScheme = SCHEME.Page;
		this.mActor = pageName;
		int[] animations = {enter, exit, popEnter, popExit};
		this.mAnimations = animations;
		this.mRequestCode = requestCode;
	}

	public FusionMessage(String service, String actor, FusionCallBack callback) {
		this(null, service, actor, callback);
	}

	public FusionMessage(SCHEME scheme, String service, String actor,
			FusionCallBack callback) {
		this.mScheme = scheme;
		this.mService = service;
		this.mActor = actor;
		this.mFusionCallBack = callback;
	}

	public void setServiceAndActor(String service, String actor) {
		this.mService = service;
		this.mActor = actor;
	}

	public void reset() {
		mState = STATE.origin;
		mFusionCallBack = null;
		if (mParams != null) {
			mParams.clear();
			mParamsForJsonString = null;
		}
	}

	public SCHEME getScheme() {
		return mScheme;
	}

	public void setScheme(SCHEME scheme) {
		this.mScheme = scheme;
	}
	
	public void setScheme(String scheme) {
		if(scheme.equals(this.SCHEME_PAGE)){
			this.mScheme = SCHEME.Page;
		}else if(scheme.equals(this.SCHEME_NATIVE)){
			this.mScheme = SCHEME.Native;
		}else if(scheme.equals(this.SCHEME_BRIDGE)){
			this.mScheme = SCHEME.Bridge;
		}else{
		}
	}

	public String getService() {
		return mService;
	}

	public void setService(String serviceName) {
		this.mService = serviceName;
	}

	public String getActor() {
		return mActor;
	}

	public void setActor(String actor) {
		this.mActor = actor;
	}

	public Map<String, Object> getParams() {
		return mParams;
	}
	
	public String getParamsForJsonString() {
		return JSON.toJSONString(mParams);
	}
	

	public void setParam(String key, Object value) {
		this.mParams.put(key, value);
	}
	
	public boolean containParam(String key){
		return mParams.containsKey(key);
	}
	
	public void setParams(Map<String, Object> params) {
		if(this.mParams != null){
			this.mParams.putAll(params);
		}
	}
	
	public void setParams(String params) {
		if(this.mParams != null){
			this.mParams.putAll(Utils.getHashMapFromJson(params));
		}
	}
	
	public void addParamsWithNoCheck(String key, String value) {
		if(this.mParams != null){
			this.mParams.put(key, value);
		}
	}
	
	
	public void setAnimations(int enter, int exit, int popEnter, int popExit){
		int[] animations = {enter, exit, popEnter, popExit};
		this.mAnimations = animations;
	}
	
	public int[] getAnimations(){
		return this.mAnimations;
	}
	
	public void setRequestCode(int requestCode){
		this.mRequestCode = requestCode;
	}
	
	public int getRequestCode(){
		return this.mRequestCode;
	}

	public Object getParam(String key) {
		return this.mParams.get(key);
	}

	public STATE getState() {
		return mState;
	}

	public boolean isCancel() {
		return FusionMessage.STATE.cancel == this.mState;
	}

	public boolean isFailed() {
		return FusionMessage.STATE.failed == this.mState;
	}

	protected boolean isSynchronized() {
		return syn;
	}

	public ProgressBean getProgressBean() {
		return mProgressBean;
	}

	protected void setSynchronized(boolean syn) {
		this.syn = syn;
	}

	protected void start() {
		if (this.mState != FusionMessage.STATE.start) {
			changeState(FusionMessage.STATE.start);
		}
	}

	public void setSupportJson(boolean isSuppourtJson) {
		this.isSuppourtJson = isSuppourtJson;
	}

	/**
	 * TODO:
	 */
	@Deprecated
	public synchronized void finish() {
		// this.mSingleFinish = true;
		//
		// if(!this.syn && this.mFusionCallBack!=null){
		// this.mFusionCallBack.processCallbackMessage(this);
		// }
		//
		// if(this.getChildren()==null){
		// if(this.mState != FusionMessage.STATE.finish){
		// this.mState = FusionMessage.STATE.finish;
		// FusionMessageHandler.publishProgress(this,STATE.finish);
		// }
		// }
		// else{
		// for(FusionMessage msg: this.mChildren){
		// if(msg.getState()!=FusionMessage.STATE.finish){
		// return;
		// }
		// }
		// if(this.mState != FusionMessage.STATE.finish){
		// this.mState = FusionMessage.STATE.finish;
		// FusionMessageHandler.publishProgress(this,STATE.finish);
		// }
		//
		// }
		// if(this.getParent()!=null){
		// this.getParent().addChildFinishCount();
		// }

		// this.mSingleFinish = true;

		if (!this.syn && this.mFusionCallBack != null) {
			this.mFusionCallBack.processCallbackMessage(this);
		}

		if (this.mState != FusionMessage.STATE.finish && this.mState!=STATE.cancel) {
			this.mState = FusionMessage.STATE.finish;
			FusionMessageHandler.publishMessageStatus(this, STATE.finish);
		}
	}

	protected void setTaskfailed() {
		if (this.mState != FusionMessage.STATE.failed) {
			changeState(FusionMessage.STATE.failed);
		}
	}

	
	public void setErrorWithoutNotify(int errorCode, String errorMsg, String errorDesc) {
		this.mErrorCode = errorCode;
		this.mErrorMsg = errorMsg;
		this.mErrorDesc = errorDesc;
	}
	
	
	public void setError(int errorCode, String errorMsg, String errorDesc) {
		this.mErrorCode = errorCode;
		this.mErrorMsg = errorMsg;
		this.mErrorDesc = errorDesc;

		if (!this.syn && this.mFusionCallBack != null) {
			this.mFusionCallBack.processCallbackMessage(this);
		}

		this.setTaskfailed();
	}

	protected void cancel() {
		if(this.mState != FusionMessage.STATE.cancel){
			changeState(FusionMessage.STATE.cancel);
		}
	}
	private void changeState(FusionMessage.STATE state) {
		// if(mState != state)
		{
			mState = state;
			FusionMessageHandler.publishMessageStatus(this, state);
		}
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	// TODO
	public Object getResponseData() {
		if (isSuppourtJson) {
			// 需要转json串
			return JSON.toJSONString(mResponseData);
		} else {
			return mResponseData;
		}
	}

	public void setResponseData(Object responseData) {
		this.mResponseData = responseData;
	}

	public void setFusionCallBack(FusionCallBack callBack) {
		this.mFusionCallBack = callBack;
	}

	public FusionCallBack getFusionCallBack() {
		return mFusionCallBack;
	}

	public int getErrorCode() {
		return this.mErrorCode;
	}

	public String getErrorMsg() {
		return this.mErrorMsg;
	}

	public String getErrorDesp() {
		return this.mErrorDesc;
	}

	public void publishMessageProgress(String desc, int size, int mTotal) {
		if (this.mProgressBean == null) {
			mProgressBean = new ProgressBean(desc, size, mTotal);
		} else {
			mProgressBean.setDescription(desc);
			mProgressBean.setSize(size);
			mProgressBean.setTotal(mTotal);
		}
		FusionMessageHandler.publishMessageStatus(this,
				FusionMessage.STATE.progress);
	}

	public void publishMessageError(int errorCode, String errorMsg,
			String errorDesc) {
		setError(errorCode, errorMsg, errorDesc);
	}

	public void publishMessageFinished() {
		finish();
	}

	public static class ProgressBean implements Serializable {
		private String mDescription;
		private int mSize;
		private int mTotal;

		public ProgressBean(String mDescription, int mSize, int mTotal) {
			super();
			this.mDescription = mDescription;
			this.mSize = mSize;
			this.mTotal = mTotal;
		}

		public String getDescription() {
			return mDescription;
		}

		public void setDescription(String mDescription) {
			this.mDescription = mDescription;
		}

		public int getSize() {
			return mSize;
		}

		public void setSize(int mSize) {
			this.mSize = mSize;
		}

		public int getTotal() {
			return mTotal;
		}

		public void setTotal(int mTotal) {
			this.mTotal = mTotal;
		}

	}

}
