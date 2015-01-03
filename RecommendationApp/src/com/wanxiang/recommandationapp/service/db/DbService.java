package com.wanxiang.recommandationapp.service.db;


import com.wanxiang.recommandationapp.controller.FusionService;
import com.wanxiang.recommandationapp.controller.annotation.Actor;
import com.wanxiang.recommandationapp.controller.annotation.Service;
import com.wanxiang.recommandationapp.service.db.actor.AddCategoryActor;
import com.wanxiang.recommandationapp.service.db.actor.QueryCategoryActor;
import com.wanxiang.recommandationapp.service.db.actor.UpdateCategoryActor;

@Service(actorList={
//		@Actor(name="division" ,value= DivisionActor.class),
//		@Actor(name="selectAllFlightCity" ,value= SelectAllFlightCityActor.class),
//		@Actor(name="selectAllStation" ,value= SelectAllStationActor.class),
//		@Actor(name="selectFlightCityByChar" ,value= SelectFlightCityByCharActor.class),
//		@Actor(name="selectFlightCityBySearchKey" ,value= SelectFlightCityBySearchKeyActor.class),
//		@Actor(name="selectNearFlightCityBySearchKey" ,value= SelectNearFlightCityBySearchKeyActor.class),
//		@Actor(name="selectFlightCityByIataCodes" ,value= SelectFlightCityByIataCodesActor.class),
//		@Actor(name="selectFlightCityByCityName" ,value= SelectFlightCityByCityNameActor.class),
//		@Actor(name="selectStationByChar" ,value= SelectStationByCharActor.class),
//		@Actor(name="selectStationBySearchKey" ,value= SelectStationBySearchKeyActor.class),
//		@Actor(name="selectSpecialCityActor" ,value= SelectSpecialCityActor.class),
//		@Actor(name="addValue" ,value= AddValueActor.class),
//		@Actor(name="getValue" ,value= GetValueActor.class),
//		@Actor(name="delValue" ,value= DelValueActor.class),
//		@Actor(name="setConfig" ,value= SetConfigActor.class),
//		@Actor(name="getConfig" ,value= GetConfigActor.class),
//		@Actor(name="addTripMsg" ,value= AddTripMessageActor.class),
//		@Actor(name="delTripMsg" ,value= DelTripMessageActor.class),
//		@Actor(name="selectTripMsg" ,value= SelectTripMessageActor.class),
//		@Actor(name="readTripMsg" ,value= ReadTripMessageActor.class),
//		@Actor(name="countUnReadTripMsg" ,value= SelectUnReadTripMessageCountActor.class),
//		@Actor(name="updateDataBase" ,value= UpdateDataBaseActor.class),
//		@Actor(name="selectAllHotelCity" ,value= SelectAllHotelCityActor.class),
//		@Actor(name="selectHotelCityBySearchKey" ,value= SelectHotelCityBySearchKeyActor.class),
//		@Actor(name="selectHotelCityArea" ,value= SelectHotelCityAreaActor.class),
//		@Actor(name="selectHotelCityPoi" ,value= SelectHotelCityPoiActor.class),
//		@Actor(name="init", value=InitDbActor.class),
		@Actor(name="addCategory" ,value= AddCategoryActor.class),
		@Actor(name="queryCategory" ,value= QueryCategoryActor.class),
		@Actor(name="updateCategory" ,value= UpdateCategoryActor.class),


	})
/**
 * 数据库服务
 * @author xianye
 *
 */
public class DbService extends FusionService{
	
	private static final Object sLock = new Object();

//	public void init(Context context){
//		super.init(context);
//		DatabaseHelper.init(context);
//		synchronized (sLock) {
//			sLock.notifyAll();
//		}
//	}
//	@Override
//	public boolean processFusionMessage(FusionMessage msg) {
//		if(!DatabaseHelper.sHasInited){
//			synchronized (sLock) {
//				try {
//					sLock.wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return super.processFusionMessage(msg);
//	}
}
