package com.wanxiang.recommandationapp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.controller.FusionMessage.SCHEME;

/**
 * Fusion协议管理类
 * @author xianye
 *
 */
public class FusionProtocolManager {
	
	public final static int ANIMATION_NONE = -1;
	/** value=0 ,city guide 应用默认动画 */
	public final static int ANIMATION_CITY_GUIDE = 0;
	/** value=1 , 由下到上动画 */
	public final static int ANIMATION_PRESENT = 1;
	/** value=2 ,present Android 不实现使用默认的 ANIMATION_CITY_GUIDE*/
	public final static int ANIMATION_SLIDE = 2;
	/** value=3 ,渐变*/
	public final static int ANIMATION_FADE = 3;
	/** value=4 ,zoom out Android 不实现使用默认的 ANIMATION_CITY_GUIDE*/
	public final static int ANIMATION_ZOOM_OUT = 4;

	public static FusionMessage.SCHEME getScheme(String url){
		if(url!=null){
			if(url.startsWith("page:")){
				return FusionMessage.SCHEME.Page;
			}else if(url.startsWith("native:")){
				return FusionMessage.SCHEME.Native;
			}else if(url.startsWith("bridge")){
				return FusionMessage.SCHEME.Bridge;
			}
		}
		return FusionMessage.SCHEME.Unknow;
	}
	
	public static String getPath(String page_url){
		if(TextUtils.isEmpty(page_url)){
			return null;
		}
		String[] urlArray = page_url.split("[?]");
		if(urlArray.length > 0){
			String prefix = urlArray[0];
			String[] prefix_array = prefix.split("://");
			if(prefix_array.length > 1){
				return prefix_array[1];
			}
		}
		return null;
	}
	
	/**根据url返回？后面的 参数 key-value 数组*/
	public static Map<String,Object> getUrlPageParamMaps(String page_url){
		if(TextUtils.isEmpty(page_url))
			return null;
		
		try {
			String url = URLDecoder.decode(page_url, "UTF-8");
			
			String[] urlArray = url.split("[?]");
			if(urlArray.length > 1){
				Map<String,Object> kvMap = new HashMap<String,Object>();			
				String params = urlArray[1];
				
				String[] param_array = params.split("[&]");
				for(String param : param_array){
					String[] paramSplit = param.split("[=]");
					String key = paramSplit[0];
					String value = paramSplit[1];
					
					if(paramSplit.length > 1){					
						kvMap.put(key, value);
					}else{
						if(!TextUtils.isEmpty(key)){
							kvMap.put(key, "");
						}
					}
				}
				
				return kvMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param page_url 
	 * 格式：page://goto?params={"page_name":"xxxx","data":{"xxx":"xxx",...},"navi_type":0|1,"anime_type":-1|0|1|2|3|4,"success_callback":"xxxxxx"}
	 *		 page://close?params={"data":{"xxx":"xxx",...}}						 
	 * 		 native://service/actor?params={"success_callback":"xxxxxxx","fail_callback":"xxxxx"}
	 * 		 bridge://bridgename?params={"xxx":"xxx",...}
	 * 		 
	 * @return FusionMessage
	 */
	public static FusionMessage parseURL(String url){
		FusionMessage msg  = null;
		if(TextUtils.isEmpty(url)){
			return null;
		}
		msg = new FusionMessage();
		try {
			url = URLDecoder.decode(url, "UTF-8");
			String tempUrl = packUrl(url);
			if(tempUrl!=null){
				url = tempUrl;
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		SCHEME scheme = getScheme(url);
		String path  = getPath(url);
		int paramsIndex = url.indexOf("?params=");
		String params = null;
		JSONObject paramsJSON = null;
		if(paramsIndex>0){
			params = url.substring(paramsIndex+8);
			if(params!=null){
				try {
					params = URLDecoder.decode(params, "UTF-8");
				} catch (Exception e) {
				}
				try {
					paramsJSON = JSON.parseObject(params);
				} catch (Exception e) {
					return null;
				}
			}
		}
		msg.setScheme(scheme);
		
		if(scheme == SCHEME.Page){
			msg.setService(path);//goto or close
			handleParams(msg,paramsJSON);
			handleAnimation(msg,paramsJSON);
			String pageName = (String)msg.getParam("page_name");
			if(pageName!=null){
				msg.setActor(pageName);
			}else{
				msg.setActor(path);
			}
			FusionPageManager.handleRedirect(msg);
		}else if(scheme == SCHEME.Native){
			if(path!=null){
				String[] p = path.split("/");
				if(p.length==2){
					msg.setService(p[0]);
					msg.setActor(p[1]);
				}
				handleParams(msg,paramsJSON);
			}
		}else if(scheme == SCHEME.Bridge){
			msg.setActor(path);
			handleParams(msg,paramsJSON);
		}
		return msg;
		
	}
	/**
	 * 将http标准的url转换为航旅协议的url：page://act_webview?param={"url":"http://m.taobao.com"}
	 * @param url http标准链接:http://m.taobao.com
	 * @return
	 */
	public static String packUrl(String url){
		if(url!=null ){
			String tempUrl = url.toLowerCase();
			if(tempUrl.startsWith("http://") || tempUrl.startsWith("https://")){
				return "page://act_webview?params={\"url\":\""+url+"\"}";
			}
		}
		return null;
	}
	
	public static void handleAnimation(FusionMessage msg, JSONObject paramsJSON){
		if(msg!=null && paramsJSON!=null){
			Integer animeType = paramsJSON.getInteger("anime_type");
			if (animeType != null) {
				// 页面跳转时的动画类型： -1 没有动画，0 city guide, 1 present, 2
				// slide, 3 fade, 4 zoom out
				handleAnimation(msg,animeType);
			}
		}
	}
	
	public static void handleAnimation(FusionMessage msg, Integer animeType){
		if(msg==null || animeType == null){
			return ;
		}
		
		if (ANIMATION_CITY_GUIDE == animeType ) {
			msg.setAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
		} else if (ANIMATION_PRESENT == animeType) {
			msg.setAnimations(R.anim.push_in_down, R.anim.push_out_down, R.anim.push_in_down, R.anim.push_out_down );
		} else if (ANIMATION_FADE == animeType) {
			msg.setAnimations(R.anim.alpha_in, R.anim.alpha_out,R.anim.alpha_in, R.anim.alpha_out);
		} else if(ANIMATION_SLIDE == animeType ){
			msg.setAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
		}else if(ANIMATION_ZOOM_OUT == animeType ){
			
		}
	}
	
	private static void handleParams(FusionMessage msg,JSONObject paramsJSON){
		if(paramsJSON!=null){
			
			Iterator<String> ite = paramsJSON.keySet().iterator();
			String key = null;
			Object value = null;
			while(ite.hasNext()){
				key = ite.next();
				if("data".equals(key)){
					value = paramsJSON.getJSONObject(key);
					if(value!=null){
						msg.setParams(((JSONObject)value).toJSONString());
					}
				}else{
					value = paramsJSON.get(key);
					msg.setParam(key,value);
				}
			}
		}
	}
	
	public static void convertParams(FusionMessage msg,Bundle bundle){
		if(bundle!=null){
			Set<String> keySet = bundle.keySet();
			Iterator<String> iterator = keySet.iterator();
			String key = null;
			while(iterator.hasNext()){
				key = iterator.next();
				msg.setParam(key, bundle.get(key));
			}
		}
	}

	
	public static FusionMessage parseNativeUrl(String native_url){
		Map<String, Object> params = getUrlNative(native_url);
		if(params!=null){
			String serviceName = (String)params.remove("service");
			String actorName = (String)params.remove("actor");
			if(serviceName!=null && actorName!=null){
				FusionMessage msg = new FusionMessage();
				msg.getParams().putAll(params);
				msg.setScheme(SCHEME.Native);
				return msg;
			}
		}
		return null;
	}
	
	public static HashMap<String, Object> getUrlNative(String native_url){
		if(TextUtils.isEmpty(native_url))
			return null;
		
		try {
			String url = URLDecoder.decode(native_url, "UTF-8");
			HashMap<String, Object> param_map = new HashMap<String, Object>();	
			
			String[] urlArray = url.trim().split("[?]");
			String prefix = urlArray[0];
			String[] prefix_array = prefix.split("://");
			if(prefix_array.length > 1){
				String service_action = prefix_array[1];
				String service[] = service_action.split("/");
				if(service.length > 0){
					param_map.put("service", service[0]);
					param_map.put("actor", service[1]);
				}
			}else{
				return null;
			}
			
			if(urlArray.length > 1){
						
				String params = urlArray[1];
				
				String[] param_array = params.split("[&]");
				for(String param : param_array){
					String[] paramSplit = param.split("[=]");
					String key = paramSplit[0];
					String value = paramSplit[1];
					
					if(paramSplit.length > 1){					
						param_map.put(key, value);
					}else{
						if(!TextUtils.isEmpty(key)){
							param_map.put(key, "");
						}
					}
				}							
			}
			
			return param_map;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
