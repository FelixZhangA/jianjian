package com.wanxiang.recommandationapp.controller;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.wanxiang.recommandationapp.controller.annotation.Service;
import com.wanxiang.recommandationapp.http.HttpService;
import com.wanxiang.recommandationapp.service.db.DbService;

/**
 * FusionService管理器，负责管理FusionService
 * @author xianye
 *
 */
public class FusionServiceManager {
	
	private static final String Tag = "FusionServiceManager";

	private Map<String,FusionService> serviceMap = new HashMap<String,FusionService>();
	private Context mContext; 
	
	@Service(name="dbService" ,value=DbService.class)
	private String dbService;
//	
//	@Service(name="MtopHttpService" ,value=MtopHttpService.class)
//	private String MtopHttpService; 
//	
//	@Service(name="lbs" ,value=LocationService.class)
//	private String lbs; 
//
//	@Service(name="app" ,value=AppService.class)
//	private String app; 
//
//	@Service(name="addressService" ,value=TripAddressService.class)
//	private String addressService; 
//
	@Service(name="networkService" ,value=HttpService.class)
	private String networkService;
//
//	@Service(name="pushMessageService" ,value=PushMessageService.class)
//	private String pushMessageService;  
//
//	@Service(name="biz" ,value=MtopService.class)
//	private String biz; 
//	
//	@Service(name="loginService" ,value=TripLoginService.class)
//	private String loginService; 
//	
//	@Service(name="httpService" ,value=HttpService.class)
//	private String httpService; 
//	
//	@Service(name="upgradeService" ,value=UpgradeService.class)
//	private String upgradeService;   
//	
//	@Service(name="innRoomService" ,value=InnRoomService.class)
//	private String innRoomService;  
//	
//	@Service(name="guestInfoService" ,value=GuestInfoService.class)
//	private String guestInfoService;  
//	
//	@Service(name="innPmsOrderService" ,value=InnPmsOrderService.class)
//	private String innPmsOrderService; 
	
	
	public FusionServiceManager(Context context){
		init(context);
	}
	
	
	private void init(Context ctx){
		try {
			mContext = ctx.getApplicationContext();
//			InputStream in = UpdateManager.getInstance(ctx).getServiceContext();
//			readXML(in);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void putService(String key,FusionService service){
		 serviceMap.put(key,service);
	}
	
	public FusionService getService(String key){
		FusionService service  = serviceMap.get(key);
		Service s = null;
		if(service == null){
			Field[] fields = FusionServiceManager.class.getDeclaredFields();
			for(Field f:fields){
				s = f.getAnnotation(Service.class);
				if(s!=null && s.name().equals(key)){
					service = newAndPutService(s.name(),s.value());
					break;
				}
			}
		}
		return service;
	}
	
	private void readXML(InputStream in){
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();//创建解析器
			XMLFusionContextHandler handler = new XMLFusionContextHandler();
			saxParser.parse(in, handler);
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Object newAndPutService(String serviceName,String classFullName){
		Object service = null;
		try {
			service = Class.forName(classFullName).newInstance();
			if(service instanceof FusionService){
				FusionService s = (FusionService)service;
				s.init(mContext);
				serviceMap.put(serviceName, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return service;
		
	}
	private FusionService newAndPutService(String serviceName,Class<? extends FusionService> clazz){
		FusionService service = null;
		try {
			service = clazz.newInstance();
			service.init(mContext);
			serviceMap.put(serviceName, service);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return service;
	}
	
	private class XMLFusionContextHandler extends DefaultHandler{
		
		@Override
		public void startDocument() throws SAXException {
			
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if("service".equals(localName)){
				String serviceName = attributes.getValue("name");
				String clazz = attributes.getValue("class");
				if(serviceName ==null || serviceName.trim().length()==0 || clazz==null || clazz.trim().length() ==0){
					return;
				}
				newAndPutService(serviceName,clazz);
			}
		}
	}
}
