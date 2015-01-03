package com.wanxiang.recommandationapp.util.memcheck;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.util.Log;

/**
 * 内存泄露检测
 * 
 * @author changlin.dcl 2014.1.13
 */
public class MemoryChecker {
	private static final String TAG = MemoryChecker.class.getSimpleName();
	private static MemoryChecker mInstance;
	private ArrayList<ReferenceObject> mAllFinishedObjList = null;
	private ArrayList<ReferenceObject> mAllCreatedObjList = null;
	private volatile boolean mExitCheckThread = false;
	private volatile boolean mCheckThreadIsRunning = false;
	private static final int GC_COUNT = 3;
	private static final int GAP_TIME = 1000 * 10;

	private MemoryChecker() {
		mAllCreatedObjList = new ArrayList<ReferenceObject>();
		mAllFinishedObjList = new ArrayList<ReferenceObject>();
		mExitCheckThread = false;
	}

	public static MemoryChecker getInstance() {
		if (mInstance == null) {
			synchronized (MemoryChecker.class) {
				if (mInstance == null) {
					mInstance = new MemoryChecker();
				}
			}
		}
		return mInstance;
	}

	
	public   void destroy(){
		if(mAllFinishedObjList!=null){
			mAllFinishedObjList.clear();
		}
		if(mAllCreatedObjList!=null){
			mAllCreatedObjList.clear();
		}
	}
	
	public void startMemoryMonitor() {
		mExitCheckThread = false;
		if(!mCheckThreadIsRunning){
			mCheckThreadIsRunning = true;
			new MemoryCheckThread().start();
		}else{
			Log.d(TAG, "-----------MemoryCheckThread already run---------");
		}
	}

	public void stopMemoryMonitor() {
		mExitCheckThread = true;
	}
	
	public void addFinishedObject(Object obj){
		if(obj!=null){
			mAllFinishedObjList.add(new ReferenceObject(obj));
		}
	}
	
	public void addCreatedObject(Object obj){
		if(obj!=null){
			mAllCreatedObjList.add(new ReferenceObject(obj));
		}
	}

	
	public void reportMonitorResult(HashSet<Object> leakedObjectSet,ArrayList<Object> remainObjectList ){
		if(leakedObjectSet!=null){
			Iterator<Object> iter = leakedObjectSet.iterator();
			Log.d(TAG, "-----------following objects may leak------------");
			while(iter.hasNext()){
				Log.d(TAG, iter.next().toString());
			}
			Log.d(TAG, "-----------following objects  leak end------------");
		}	
		
		if(remainObjectList!=null){
			int size = remainObjectList.size();
			Log.d(TAG, "-----------following objects still exist,you need check------------");
			for(int i=0;i<size;i++){
				Log.d(TAG, remainObjectList.get(i).toString());
			}
			Log.d(TAG, "-----------following objects still exist end------------");
		}	
	}
	
	class ReferenceObject  {
		public ReferenceObject(Object mRefobject) {
			super();
			this.mRefobject = new WeakReference<Object>(mRefobject);
			this.mAddedTime = System.currentTimeMillis();
		}
		WeakReference<Object> mRefobject;
		long  mAddedTime;
	}
	
	 boolean isGreaterGCTime(ReferenceObject referenceObject){
		 //确保要经历GC_COUNT次垃圾回收
		 Log.d(TAG, "-----------MemoryCheckThread time------------"+(System.currentTimeMillis() - referenceObject.mAddedTime));
		return (System.currentTimeMillis() - referenceObject.mAddedTime) >= (GC_COUNT)*GAP_TIME;
	}
	
	class MemoryCheckThread extends Thread {
		public void run() {
			//先执行多次垃圾回收,确保能回收的都回收
			Log.d(TAG, "-----------MemoryCheckThread begin------------");
			int curTimes = 0;
			for(;curTimes< GC_COUNT&&!mExitCheckThread;curTimes++ ){
				System.gc();
				if(mExitCheckThread){
					break;
				}
				try {
					Thread.sleep(GAP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			
			
			if(!mExitCheckThread&&curTimes>=GC_COUNT){//没有退出且已经执行好几次垃圾回收了，则可以进行判断
				HashSet<Object> leakObjectList = new  HashSet<Object>();
				if(mAllFinishedObjList!=null){
					int size = mAllFinishedObjList.size();
					for(int i=0;i<size;i++){
						ReferenceObject weakObj = mAllFinishedObjList.get(i);
						WeakReference<Object> mRefobject= weakObj.mRefobject;
						Object realObject = mRefobject.get();
						if(isGreaterGCTime(weakObj)&&realObject!=null){
							leakObjectList.add(realObject);
						}
					}
				}
				
				ArrayList<Object> remainObjectList = new  ArrayList<Object>();
				if(mAllCreatedObjList!=null){
					int size = mAllCreatedObjList.size();
					for(int i=0;i<size;i++){
						ReferenceObject weakObj = mAllCreatedObjList.get(i);
						WeakReference<Object> mRefobject= weakObj.mRefobject;
						Object realObject = mRefobject.get();
						if(isGreaterGCTime(weakObj)&&realObject!=null&&!leakObjectList.contains(realObject)){
							remainObjectList.add(realObject);
						}
					}
				}
				
				reportMonitorResult(leakObjectList,remainObjectList);
			}	
			
			mCheckThreadIsRunning = false;
			Log.d(TAG, "-----------MemoryCheckThread end------------");
		}
	}

}
