package com.wanxiang.recommandationapp.controller;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.wanxiang.recommandationapp.controller.annotation.Actor;
import com.wanxiang.recommandationapp.controller.annotation.Service;
import com.wanxiang.recommandationapp.util.Constants;

/**
 * FusionService基类，负责管理Actor
 * @author xianye
 *
 */
public  class FusionService {
	
	private static final String TAG = "FusionService";
	protected Map<String,FusionActor> actorMap = new HashMap<String,FusionActor>(); 
	protected Context context;
	
	
	public void init(Context context){
		this.context = context;
	}
	
	public void putActor(String actorName,FusionActor actor){
		this.actorMap.put(actorName, actor);
	}
	
	private FusionActor getAndNewActor(String key){
		FusionActor act = this.actorMap.get(key);
		if(act == null){
			Service service = this.getClass().getAnnotation(Service.class);
			Actor[] actorList = service.actorList();
			for(Actor actor:actorList){
				if(actor!=null){
					String actorName = actor.name();
					Class<? extends FusionActor> clazz = actor.value();
					if((actorName!=null  && actorName.length()>0) && actorName.equals(key)){
						try {
							act = clazz.newInstance();
							act.init(context);
							actorMap.put(actorName, act);
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return act;
	}
	
	public boolean processFusionMessage(FusionMessage msg){
		
		if(msg!=null){
			FusionActor actor = getAndNewActor(msg.getActor());
			String isNetApi = "false";
			if(actor!= null){
				try{	
					return actor.processFusionMessage(msg);
				}catch(Exception e){
					e.printStackTrace();
					msg.setError(FusionMessage.ERROR_CODE_SYS_ERROR, FusionMessage.ERROR_MSG_SYS_ERROR, e.getMessage());
				}finally{
					if(Constants.STATISTIC_OPEN){
						if(!msg.containParam(Constants.IS_NET_API)){
							Class<?>[] allSubClasses = actor.getClass().getDeclaredClasses();
							for (Class<?> subclass : allSubClasses) {
//								if(IMTOPDataObject.class.isAssignableFrom(subclass)){
//									isNetApi  = "true";
//									break;
//								}
							}
							msg.addParamsWithNoCheck(Constants.IS_NET_API, isNetApi);
						}	
					}
				}
			}
			else{
				msg.setError(FusionMessage.ERROR_CODE_SYS_ERROR, FusionMessage.ERROR_MSG_SYS_ERROR, "["+msg.getActor()+"] actor is null.");
			}
		}
		
		return true;
	}
	
}
