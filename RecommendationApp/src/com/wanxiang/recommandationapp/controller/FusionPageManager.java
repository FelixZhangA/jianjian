package com.wanxiang.recommandationapp.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jianjianapp.R;
import com.wanxiang.recommandationapp.ui.base.IPageSwitcher;
import com.wanxiang.recommandationapp.ui.base.TripBaseFragment;
import com.wanxiang.recommandationapp.ui.base.TripBaseFragment.onFragmentFinishListener;
import com.wanxiang.recommandationapp.ui.base.TripStubActivity;
//import com.ali.innpms.model.address.TripAddress;
//import com.ali.innpms.model.flight.TripActivity;
//import com.ali.innpms.model.flight.TripFlightCabinInfo;
//import com.ali.innpms.model.flight.TripFlightDynamicInfo;
//import com.ali.innpms.model.flight.TripFlightInsureInfoList;
//import com.ali.innpms.model.flight.TripFlightRoundInfo;
//import com.ali.innpms.model.flight.TripFlightSearch;
//import com.ali.innpms.model.flight.TripFlightSuperSearchData.Rt_outbound;
//import com.ali.innpms.model.hotel.TripHotelDetailProxyData.ProxyData;
//import com.ali.innpms.model.hotel.TripHotelInfo;
//import com.ali.innpms.service.db.bean.TripDomesticFlightCity;
//import com.ali.innpms.service.db.bean.TripDomesticHotelCity;
//import com.ali.innpms.service.db.bean.TripKuaidiOrder;
//import com.ali.innpms.service.db.bean.TripKuaidiOrder.DriverInfo;

/**
 * native或者H5 跳转页面管理
 * 
 * @author xianye
 * 
 */
public class FusionPageManager {

	private static final String TAG = FusionPageManager.class.getSimpleName();

	private Context mContext;
	private Map<String, FusionPage> mPageMap = new HashMap<String, FusionPage>();

	public static FusionPageManager mInstance;

	public static FusionPageManager getInstance() {
		if (mInstance == null) {
			mInstance = new FusionPageManager();
		}
		return mInstance;
	}

	private FusionPageManager() {
		// mPageMap.put("main", new
		// FusionPage("main","com.ali.trip.ui.MenuFragment",null));
	}

	public void init(Context ctx) {
		try {
			mContext = ctx.getApplicationContext();
//			readJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Fragment openPage(FragmentManager mFragmentManager, String pageName, Bundle bundle, int[] animations, boolean addToBackStack) {
		return openPageWithNewFragmentManager(true, mFragmentManager, pageName, bundle, animations, addToBackStack);
	}

	public Fragment openPageWithNewFragmentManager(boolean showAnimation, FragmentManager mFragmentManager, String pageName, Bundle bundle, int[] animations, boolean addToBackStack) {
		if (!checkGapTime()) {
			return null;
		}
		TripBaseFragment frg = null;
		try {
			FusionPage page = this.mPageMap.get(pageName);
			if (page == null) {
				Log.e(TAG, "FusionPage:" + pageName + " is null.");
				return null;
			}
			frg = (TripBaseFragment) Class.forName(page.getClazz()).newInstance();

			Bundle pageBundle = buildBundle(null, page);
			if (bundle != null) {
				pageBundle.putAll(bundle);
			}
			frg.setArguments(pageBundle);
			frg.setPageName(pageName);

			FragmentTransaction ft = mFragmentManager.beginTransaction();
//			if (animations != null && showAnimation) {
//				ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);
//				Fragment fragmentContainer = mFragmentManager.findFragmentById(R.id.fragment_container);
//				if (fragmentContainer != null) {
//					if (R.anim.slide_out_left == animations[1] || R.anim.slide_out_right == animations[3]) {
//						ft.hide(fragmentContainer);
//					}
//				}
//			}
//			if (addToBackStack) {
//				ft.add(R.id.fragment_container, frg, page.getName());
//				ft.addToBackStack(page.getName());
//			} else {
//				ft.replace(R.id.fragment_container, frg, page.getName());
//			}
			ft.commitAllowingStateLoss();
			// ft.commit();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Fragment.error:" + e.getMessage());
			return null;
		}
		return frg;
	}

	public Fragment gotoPage(FragmentManager mFragmentManager, String pageName, Bundle bundle, int[] animations) {
		Fragment frg = null;
		if (mFragmentManager != null) {
			frg = mFragmentManager.findFragmentByTag(pageName);
		}
		if (frg != null) {
			if (!checkGapTime()) {
				return null;
			}
//			// 返回Fragment栈里的页面
//			FusionPage page = this.mPageMap.get(pageName);
//			if (page == null) {
//				Log.e(TAG, "FusionPage:" + pageName + " is null.");
//				return null;
//			}
			mFragmentManager.popBackStackImmediate(pageName, 0);
//			Bundle pageBundle = buildBundle(null, page);
//			if (bundle != null) {
//				pageBundle.putAll(bundle);
//			}
			((TripBaseFragment) frg).onFragmentDataReset(bundle);

		} else {
			this.openPage(mFragmentManager, pageName, bundle, animations, true);
		}

		return frg;
	}

	// public void removeNoNeedFragment(Context context) {
	// if (CommonDefine.ACTIVITY_ONCE_REMOVED) {
	// return;
	// } else {
	// CommonDefine.ACTIVITY_ONCE_REMOVED = true;
	// }
	//
	// if (context instanceof FragmentActivity) {
	// FragmentActivity activity = (FragmentActivity) context;
	// FragmentManager mFragmentManager = activity
	// .getSupportFragmentManager();
	//
	// if (mFragmentManager != null) {
	// FragmentTransaction trans = mFragmentManager.beginTransaction();
	//
	// Fragment frg = mFragmentManager.findFragmentByTag("welcome");
	// if (frg != null) {
	// trans.remove(frg);
	// }
	// if (Preferences.getPreferences(mContext).getIsFirstUseApp()) {
	// frg = mFragmentManager.findFragmentByTag("guide");
	// if (frg != null) {
	// trans.remove(frg);
	// }
	// Preferences.getPreferences(mContext)
	// .setIsFirstUseApp(false);
	// }
	// trans.commitAllowingStateLoss();
	// }
	// }
	// }

	public boolean isFragmentTop(Context context, String fragmentTag) {
		if (context != null && context instanceof IPageSwitcher) {
			return ((IPageSwitcher) context).isFragmentTop(fragmentTag);
		} else {
			IPageSwitcher pageSwitcher = TripStubActivity.getTopActivity();
			if (pageSwitcher != null) {
				return pageSwitcher.isFragmentTop(fragmentTag);
			} else {
				return false;
			}
		}
	}

	/**
	 * 跳转页面，native或者H5
	 * 
	 * @param msg
	 */
	public Fragment openPage(boolean showAnimation, FragmentManager mFragmentManager, final FusionMessage msg, boolean addToBackStack) {
		if (mFragmentManager == null) {
			Log.e(TAG, "FragmentManager: is null.");
			return null;
		}
		if (null == msg || msg.getScheme() != FusionMessage.SCHEME.Page)
			return null;

		handleRedirect(msg);
		String actor = msg.getActor();
		if (TextUtils.isEmpty(actor)) {
			return null;
		}
		Bundle bundle = buildBundle(msg, null);
		FusionProtocolManager.handleAnimation(msg, (Integer) msg.getParam("anime_type"));
		TripBaseFragment frg = (TripBaseFragment) this.openPageWithNewFragmentManager(showAnimation, mFragmentManager, actor, bundle, msg.getAnimations(), addToBackStack);
		this.handCallback(frg, msg);
		return frg;
	}

	/**
	 * 另外一个Activity 跳转fragment时，需指定新的mFragmentManager
	 * 
	 * @param mFragmentManager
	 * @param msg
	 * @param addToBackStack
	 * @return
	 */
	public Fragment gotoPageWithFragmentManager(boolean showAnimation, FragmentManager mFragmentManager, final FusionMessage msg, boolean addToBackStack) {
		if (mFragmentManager == null) {
			Log.e(TAG, "FragmentManager: is null.");
			return null;
		}
		if (null == msg || msg.getScheme() != FusionMessage.SCHEME.Page) {
			return null;
		}
		handleRedirect(msg);
		String pageName = msg.getActor();
		if (TextUtils.isEmpty(pageName)) {
			return null;
		}
		Bundle bundle = this.buildBundle(msg, null);
		FusionProtocolManager.handleAnimation(msg, (Integer) msg.getParam("anime_type"));

		TripBaseFragment frg = (TripBaseFragment) openPageWithNewFragmentManager(showAnimation, mFragmentManager, pageName, bundle, msg.getAnimations(), addToBackStack);
		this.handCallback(frg, msg);
		return frg;
	}

	public Fragment gotoPageWithFragmentManager(FragmentManager mFragmentManager, final FusionMessage msg, boolean addToBackStack) {
		return gotoPageWithFragmentManager(false, mFragmentManager, msg, addToBackStack);
	}

	private boolean checkGapTime() {
		return true;
		// long currentTime = System.currentTimeMillis();
		// if((currentTime - this.mOperationTime )> this.mGapTime){
		// this.mOperationTime = currentTime;
		// return true;
		// }
		// Log.e(TAG, "GapTime is too short.");
		// return false;
	}

	public static void handleRedirect(FusionMessage msg) {
		String pageName = msg.getActor();
		if ("home".equals(pageName)) {// 微旅行首页
			msg.setActor("home");
			msg.setParam("tab_index", 1);
		} else if ("specials_home".equals(pageName)) {//特惠首页
			msg.setActor("home");
			msg.setParam("tab_index", 2);
		} else if ("discovery_home".equals(pageName)) {// 发现首页
			msg.setActor("home");
			msg.setParam("tab_index", 3);
		}else if ("user_center".equals(pageName)) {// 个人中心首页
			msg.setActor("home");
			msg.setParam("tab_index", 4);
		} else if ("flight_search".equals(pageName)) {// 机票首页
			msg.setActor("flight_home");
		} else if ("flight_onsale".equals(pageName)) {// 特价机票首页
			msg.setActor("flight_home");
			msg.setParam("tab_index", 2);
		}else if ("flight_subscribe_list".equals(pageName)) {// 低价提醒
			msg.setActor("flight_home");
			msg.setParam("tab_index", 3);
		} else if ("flight_dynamic".equals(pageName)) {// 航班动态首页
			msg.setActor("flight_home");
			msg.setParam("tab_index", 4);
		} else if ("feed_detail".equals(pageName)) {
			msg.setActor("little_travel_detail");
			Object feedId = msg.getParam("feed_id");
			if (feedId != null) {
				msg.setParam("feedId", Long.parseLong(String.valueOf(feedId)));
			}
		}
		// 【订单详情-机票】page://flight_order_detail?params={"orderId":28179355,"tab_type”:0}
		// 【订单详情-火车票】page://train_order_detail?params={"orderId":192275953664505,"tab_type":1}
		// 【订单详情-其他】page://ticket_order_detail?params={“orderId":414020436167455}

		// 【订单列表】page://order_list
	}

	private void handCallback(TripBaseFragment fragment, final FusionMessage msg) {
		if (msg.getRequestCode() != -2) {
			fragment.setRequestCode(msg.getRequestCode());
			fragment.setFragmentFinishListener(new onFragmentFinishListener() {

				@Override
				public void onFragmentResult(int requestCode, int resultCode, Intent data) {
					if (msg.getFusionCallBack() != null && data != null) {
						data.putExtra("resultCode", resultCode);
						data.putExtra("requestCode", requestCode);
						msg.setResponseData(data);
						msg.finish();
					}
				}
			});
		}
	}

	public static Bundle getBundleFromMsg(FusionMessage msg) {
		return buildBundle(msg, null);
	}

	private static Bundle buildBundle(FusionMessage msg, FusionPage page) {
		Bundle bundle = new Bundle();

		Object v = null;
		String k = null;
		if (page != null && page.getParams() != null) {
			JSONObject j = JSONObject.parseObject(page.getParams());
			if (j != null) {
				Set<String> keySet = j.keySet();
				if (keySet != null) {
					Iterator<String> ite = keySet.iterator();
					while (ite.hasNext()) {
						k = ite.next();
						v = j.get(k);
						putValue(bundle, k, v);
					}
				}
			}
		}
		if (msg != null) {
			for (Entry<String, Object> entry : msg.getParams().entrySet()) {
				v = entry.getValue();
				k = entry.getKey();
				putValue(bundle, k, v);
			}
		}
		return bundle;
	}

	private static void putValue(Bundle bundle, String k, Object v) {
		if (v == null) {
			return;
		}
		if (v instanceof String) {
			mapString2Object(k, (String) v, bundle);
		} else if (v instanceof Integer) {
			bundle.putInt(k, (Integer) v);
		} else if (v instanceof Boolean) {
			bundle.putBoolean(k, (Boolean) v);
		} else if (v instanceof Long) {
			bundle.putLong(k, (Long) v);
		} else if (v instanceof Double) {
			bundle.putDouble(k, (Double) v);
		} else if (v instanceof Short) {
			bundle.putShort(k, (Short) v);
		} else if (v instanceof Float) {
			bundle.putFloat(k, (Float) v);
		} else if (v instanceof Map) {
			mapString2Object(k, JSON.toJSONString(v), bundle);
		} else if (v instanceof Serializable) {
			bundle.putSerializable(k, (Serializable) v);
		} else if (v instanceof Parcelable) {
			bundle.putParcelable(k, (Parcelable) v);
		} else if (v instanceof String[]) {
			bundle.putStringArray(k, (String[]) v);
		} else if (v instanceof int[]) {
			bundle.putIntArray(k, (int[]) v);
		} else {
			mapString2Object(k, JSON.toJSONString(v), bundle);
		}

	}

	// http://10.232.127.67/rest/api3.do?api=com.taobao.client.sys.login&appKey=4272&v=v3&data=%7B%22username%22%3A%22wq123%22%2C%22password%22%3A%220000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000180d76c362b79e0db62e0754f6880fbce93d5e00f6be144545b512ae3540%22%2C%22token%22%3A%220ff4afb2f19ad1f98ddc86aee8e5abe7%22%2C%22appKey%22%3A%224272%22%2C%22topToken%22%3A%2234e79d7b901e0003d10757358c9a25c8%22%2C%22needSSOToken%22%3A%22true%22%7D&imei=866324012536063&imsi=000000000000000&ttid=990001@travel_android_3.0.0&t=1384235441&deviceId=AvTJ7F-ubeXJSjFIGiYMlXzE0AZr1e-KdkxHymFMfEqL&sign=8c388b7a14603dd9f09f958dde9b5728
	// http://10.232.127.67/rest/api3.do?api=com.taobao.client.sys.login&appKey=4272&v=v3&data=%7B%22username%22%3A%22wq123%22%2C%22password%22%3A%220000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000180d76c362b79e0db62e0754f6880fbce93d5e00f6be144545b512ae3540%22%2C%22token%22%3A%22085f18bbbad6d94513dd771bbdc9ac1c%22%2C%22appKey%22%3A%224272%22%2C%22topToken%22%3A%2244e72685c691204af9e7c73f89a8f863%22%2C%22needSSOToken%22%3A%22true%22%7D&imei=866324012536063&imsi=000000000000000&ttid=990001@travel_android_3.0.0&t=1384237047&deviceId=AvTJ7F-ubeXJSjFIGiYMlXzE0AZr1e-KdkxHymFMfEqL&sign=2e86d2378c2d51b0120d3e360b431eef

//	private void readJson() {
//		long t = System.currentTimeMillis();
//		try {
//			InputStream in = UpdateManager.getInstance(mContext).getPageContext();
//			int i = -1;
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			while ((i = in.read()) != -1) {
//				baos.write(i);
//			}
//			in.close();
//			String content = baos.toString();
//			JSONArray jsonArray = JSONArray.parseArray(content);
//			Iterator<Object> iterator = jsonArray.iterator();
//			JSONObject jsonPage = null;
//			while (iterator.hasNext()) {
//				jsonPage = (JSONObject) iterator.next();
//				String pageName = jsonPage.getString("name");
//				String clazz = jsonPage.getString("class");
//				String params = jsonPage.getString("params");
//				if (TextUtils.isEmpty(pageName) || TextUtils.isEmpty(clazz)) {
//					return;
//				}
//				mPageMap.put(pageName, new FusionPage(pageName, clazz, params));
//			}
//			Log.e("解包", mPageMap.size() + "");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	private static void mapString2Object(String key, String value, Bundle bundle) {
		if (key.equals("depart_city") || key.equals("arrive_city")) {
			JSONObject jv = JSON.parseObject(value);
			if (jv.containsKey("iata_code") || jv.containsKey("city_name")) {
				jv.put("iataCode", jv.getString("iata_code"));
				jv.put("cityName", jv.getString("city_name"));
				jv.remove("iata_code");
				jv.remove("city_name");
				value = jv.toJSONString();
			}
//			bundle.putSerializable(key, JSON.parseObject(value, TripDomesticFlightCity.class));
		} else if (key.equals("address")) {
//			bundle.putParcelable(key, JSON.parseObject(value, TripAddress.class));
		} else if (key.equals("address_list")) {
//			bundle.putParcelableArrayList(key, (ArrayList<TripAddress>) JSON.parseArray(value, TripAddress.class));
		} else if (key.equals("city_list")) {
//			bundle.putSerializable(key, (ArrayList<TripDomesticFlightCity>) JSON.parseArray(value, TripDomesticFlightCity.class));
		} else if (key.equals("searchDic")) {
			//将往返程机票的参数往上提一层
			JSONObject dicJson = JSON.parseObject(value);
			if (dicJson != null) {
				Set<String> keySet = dicJson.keySet();
				if (keySet != null) {
					Iterator<String> ite = keySet.iterator();
					while (ite.hasNext()) {
						String k = ite.next();
						Object v = dicJson.get(k);
						putValue(bundle, k, v);
					}
				}
			}
		} else {
			bundle.putString(key, value);
		}

	}
	
	public FusionPage getFusionPage(String pageName) {
		FusionPage page = this.mPageMap.get(pageName);
		return page;
	}
}
