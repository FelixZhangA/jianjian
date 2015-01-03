package com.wanxiang.recommandationapp.controller;

import android.content.Context;
/**
 * 抽象公共的Actor，需要子类继承实现processFusionMessage
 * @author xianye
 *
 */
public abstract class FusionActor {

	protected Context context;
	public void init(Context context){
		this.context = context;
	}
	/**
	 * 
	 * @param msg
	 * @return 同步调用返回true,异步调用返回false
	 */
	public abstract boolean processFusionMessage(FusionMessage msg);
	
}
