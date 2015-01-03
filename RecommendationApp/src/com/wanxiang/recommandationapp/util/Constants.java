package com.wanxiang.recommandationapp.util;


public class Constants {
	public static String TAG = "ali_trip";
	public static final int POISEARCH = 1000;

	public static final int ERROR = 1001;
	public static final int FIRST_LOCATION = 1002;

	public static final int ROUTE_START_SEARCH = 2000;
	public static final int ROUTE_END_SEARCH = 2001;
	public static final int ROUTE_SEARCH_RESULT = 2002;
	public static final int ROUTE_SEARCH_ERROR = 2004;

	public static final int REOCODER_RESULT = 3000;
	public static final int DIALOG_LAYER = 4000;
	public static final int POISEARCH_NEXT = 5000;

	public static final int BUSLINE_RESULT = 6000;
	public static final int BUSLINE_DETAIL_RESULT = 6001;
	public static final int BUSLINE_ERROR_RESULT = 6002;

	/*public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
	public static final LatLng ZHONGGUANCUN = new LatLng(39.983456, 116.3154950);// 北京市中关村经纬�?
	public static final LatLng SHANGHAI = new LatLng(31.239879, 121.499674);// 上海市经纬度
	public static final LatLng FANGHENG = new LatLng(39.989614, 116.481763);// 方恒国际中心经纬�?
	public static final LatLng CHENGDU = new LatLng(30.679879, 104.064855);// 成都市经纬度
	public static final LatLng XIAN = new LatLng(34.341568, 108.940174);// 西安市经纬度
	public static final LatLng ZHENGZHOU = new LatLng(34.7466, 113.625367);// 郑州市经纬度
*/	
	public static  long beginTime = 0;
	
	/**
	 * 是否开启Actor相关统计（包括网络）
	 */
	public static  boolean STATISTIC_OPEN = false;
	public static  String IS_NET_API = "IS_NET_API";
	public static  String API_NAME = "API_NAME";
	public static  String CONTENT_ENCODING = "content-encoding";
	public static  String CONTENT_LEGHTH = "content-length";
	public static  String BYTE_LEGHTH = "byte_length";
	
	/**
	 * 是否开启内存泄露检测
	 */
	public static  boolean MEMORY_CHECK_OPEN = false;
	
	
	/**
	 * SSO 失效
	 */
	public static final String SSO_INVALID = "ERR_SID_INVALID";
	
	public static final String REFRESH_ORDER_STATUE_CANCED = "REFRESH_ORDER_STATUE_CANCED";
	public static final String REFRESH_ORDER_STATUE_BUYED = "REFRESH_ORDER_STATUE_BUYED";
	public static final String REFRESH_ORDER_STATUE_REFUND = "REFRESH_ORDER_STATUE_REFUND";
	public static final String REFRESH_USER_CHANGED = "REFRESH_USER_CHANGED";
	public static final String MARK_NEW_FEED_COUNT = "MARK_NEW_FEED_COUNT";
	public static final String MARK_NEW_DISCOVER_SHOW = "MARK_NEW_DISCOVER_SHOW";
	public static final String MARK_NEW_PUSH_MSG = "com.ali.trip.NEW_PUSH_MSG";
	public static final String MARK_NEW_REMIND_SUBSCRIPTION = "com.ali.trip.MARK_NEW_REMIND_SUBSCRIPTION";
	public static final String MARK_UPGRADE_NEEDED_MSG = "com.ali.trip.UPGRADE_NEEDED_MSG";
	public static final String REFRESH_SUBSCRIBE_CHANGED = "REFRESH_SUBSCRIBE_CHANGED";
	public static final String ADD_BACK_STACK = "ADD_BACK_STACK";
	public static final String ANIM = "ANIM";
	public static final String NEW_ACTIVITY = "NEW_ACTIVITY";
	public final static boolean NEW_ACTIVITY_FOR_ORDER = true;// 1 pop ;
	public final static boolean NEW_ACTIVITY_FOR_ORDER_LIST = true;// 1 pop ;
	public final static String  ACTION_EXIT_APP = "com.ali.trip.exit";
	public final static String  PUSH_ACTION = "com.ali.trip";
	public final static String  SMART_BANNER_ACTION = "smartbanner";
	
	public final static String  PUSH_ACTION_CALLING_INTENT = "callIntent";
	public final static String  PUSH_FROM_KEY = "isFromPush";
    public static final String WUA = "wua";
    public static final String T = "t";
    public static final String APPKEY = "appKey";
    public static final String SECSIGNITURE = "security";
	public final static String  NEED_DOUBLECHECK = "needDoubleCheck";
	public static  boolean JUMP_TO_USERCENTER = false;
	public static  boolean HOST_WHITE_LIST = false;
	
//	public static int[] faceArray = {R.drawable.f001,R.drawable.f002,R.drawable.f003,R.drawable.f004,R.drawable.f005,
//		R.drawable.f006,R.drawable.f007,R.drawable.f008,R.drawable.f009,R.drawable.f010,
//		R.drawable.f011,R.drawable.f012,R.drawable.f013,R.drawable.f014,R.drawable.f015,
//		R.drawable.f016,R.drawable.f017,R.drawable.f018,R.drawable.f019,R.drawable.f020,
//		R.drawable.f021,R.drawable.f022,R.drawable.f023,R.drawable.f024,R.drawable.f025,
//		R.drawable.f026,R.drawable.f027,R.drawable.f028,R.drawable.f029,R.drawable.f030,
//		R.drawable.f031,R.drawable.f032,R.drawable.f033,R.drawable.f034,R.drawable.f035,
//		R.drawable.f036,R.drawable.f037,R.drawable.f038,R.drawable.f039,R.drawable.f040,
//		R.drawable.f041,R.drawable.f042,R.drawable.f043,R.drawable.f044,R.drawable.f045,
//		R.drawable.f046,R.drawable.f047,R.drawable.f048,R.drawable.f049,R.drawable.f050,
//		R.drawable.f051,R.drawable.f052,R.drawable.f053,R.drawable.f054,R.drawable.f055,
//		R.drawable.f056,R.drawable.f057,R.drawable.f058,R.drawable.f059,R.drawable.f060,
//		R.drawable.f061,R.drawable.f062,R.drawable.f063,R.drawable.f064,R.drawable.f065,
//		R.drawable.f066,R.drawable.f067,R.drawable.f068,R.drawable.f069,R.drawable.f070,
//		R.drawable.f071,R.drawable.f072,R.drawable.f073,R.drawable.f074,R.drawable.f075,
//		R.drawable.f076,R.drawable.f077,R.drawable.f078,R.drawable.f079,R.drawable.f080,
//		R.drawable.f081,R.drawable.f082,R.drawable.f083,R.drawable.f084,R.drawable.f085,
//		R.drawable.f086,R.drawable.f087,R.drawable.f088,R.drawable.f089,R.drawable.f090,
//		R.drawable.f091,R.drawable.f092,R.drawable.f093,R.drawable.f094,R.drawable.f095,
//		R.drawable.f096,R.drawable.f097,R.drawable.f098,R.drawable.f099};

	
}
