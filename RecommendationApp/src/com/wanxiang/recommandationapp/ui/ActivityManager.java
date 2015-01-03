package com.wanxiang.recommandationapp.ui;

import java.util.Stack;

import android.app.Activity;

public class ActivityManager {

	private static ActivityManager instance;
	private Stack<Activity> mActivityStack = new Stack<Activity>();
	
	private ActivityManager(){
		
	}
	
	public static ActivityManager getInstance(){
		if(null == instance){
			instance = new ActivityManager();
		}
		return instance;
	}
	
	public void addActicity(Activity act){
		mActivityStack.push(act);
	}
	
	public void removeActivity(Activity act){
		mActivityStack.remove(act);
	}
	
	public void killMyProcess(){
		int nCount = mActivityStack.size();
		for (int i = nCount - 1; i >= 0; i--) {
        	Activity activity = mActivityStack.get(i);
        	activity.finish();
        }
		
		mActivityStack.clear();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
