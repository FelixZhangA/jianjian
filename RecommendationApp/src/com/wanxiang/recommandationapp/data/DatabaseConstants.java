package com.wanxiang.recommandationapp.data;


public class DatabaseConstants
{
	public static String	DATABASE_NAME								= "wanxiang";
	public static int		version										= 1;
	public static String 	RECOMMENDATION_TABLE_NAME					= "recommendation_info";
	public static String 	CATEGORY_TABLE_NAME							= "category_info";
	public static String 	CATEGORY_DETAILS_TABLE_NAME					= "category_details_info";
	public static String 	COMMENT_TABLE_NAME							= "comment_info";
	public static String 	USER_TABLE_NAME								= "user";

	public static String	COLUMN_APP_ID								= "_id";
	public static String 	COLUMN_RECOMMANDATION_UID					= "recommandation_id";
	public static String 	COLUMN_COMMENT_UID							= "comment_id";
	public static String	COLUMN_ENTITY								= "entity";
	public static String	COLUMN_CATEGORY								= "category";
	public static String	COLUMN_CATEGORY_COLOR						= "category_color";
	public static String	COLUMN_CONTENT								= "content";
	public static String	COLUMN_DATE									= "date";
	public static String	COLUMN_PRAISE_NUM							= "phraise_num";
	public static String	COLUMN_COMMENT_NUM							= "comment_num";
	public static String	COLUMN_USER									= "user";
	public static String 	COLUMN_CATEGORY_UID							= "category_id";
	public static String 	COLUMN_USER_UID								= "user_id";
	public static String 	COLUMN_USER_IMEI							= "user_imei";
	public static String 	COLUMN_USER_MDN								= "user_mdn";

	
	public static long 		TWENTY_FOUR_HOUR							= 24*60*60*1000;
	public static String 	MESSAGE_CATEGORY_LIST						= "category_list";
	public static String 	MESSAGE_QUERY								= "query";
	public static int 		FIRST_LEVEL_CATEGORY_MAX					= 1000;
	public static int	 	MESSAGE_QUERY_PARENT						= 1001;
	public static int 		MESSAGE_QUERY_CHILDREN						= 1002;
	public static int	 	MESSAGE_QUERY_RECENT						= 1003;
	
	public static String	MESSAGE_UPDATE								= "update";
	public static int 		MESSAGE_UPDATE_FAVORITE						= 1004;
	public static int	 	MESSAGE_UPDATE_RECENT						= 1005;


}
