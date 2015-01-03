package com.wanxiang.recommandationapp.util;

public class AppConstants
{
    public static final String RECOMMENDATION = "recommendation";
    public static final String INTENT_CATEGORY = "categoryName";
    public static final String HEADER_REC_ID = "tuijianId";
	public static final String HEADER_USER_ID = "userId";
	public static final String HEADER_CATEGORY = "categoryName";
	public static final String HEADER_ENTITY = "entityName";
	public static final String HEADER_PAGE_SIZE = "pageSize";
	public static final String HEADER_PAGE_INDEX = "pageIndex";
	public static final String HEADER_DESCRIPTION = "description";
	public static final String HEADER_IMEI = "imei";
	public static final String HEADER_IS_NEW = "isNew";
	public static final String HEADER_CANCEL_PRAISE = "cancel";
	public static final String HEADER_COMMENT_CONTENT = "comment";
	public static final String HEADER_LEVEL = "level";
	public static final String HEADER_NEEDID = "needId";

	public static final String RESPONSE_HEADER_COMMENT = "comments";
	public static final String RESPONSE_HEADER_ID = "id";

	public static final String RESPONSE_HEADER_ERROR = "error";
	public static final String RESPONSE_HEADER_DATA = "data";
	public static final String RESPONSE_HEADER_RECOMMENDATION_ID = "contentId";
	public static final String RESPONSE_HEADER_UPDATE_TIME = "updateTime";
	public static final String RESPONSE_HEADER_USER_ID = "userId";
	public static final String RESPONSE_HEADER_REPLYED_USER_ID = "replyedUserId";

	public static final String RESPONSE_HEADER_USER_NAME = "userName";
	public static final String RESPONSE_HEADER_CATEGORY_ID = "categoryId";
	public static final String RESPONSE_HEADER_CATEGORY_NAME = "categoryName";
	
	public static final String RESPONSE_HEADER_ENTITY_ID = "entityId";
	public static final String RESPONSE_HEADER_CONTENT = "description";
	public static final String RESPONSE_HEADER_PRAISE_COUNT = "praiseCount";
	public static final String RESPONSE_HEADER_COMMENT_COUNT = "commentCount";
	public static final String RESPONSE_HEADER_NAME = "name";
	public static final String RESPONSE_HEADER_CATEGORY_PARENT = "parentId";
	public static final String RESPONSE_HEADER_CATEGORY_CHILDREN = "children";
	public static final String REQUEST_HEADER_CATEGORY_DEPTH = "maxDepth";

	public static final String RESPONSE_HEADER_ENTITY_NAME = "entityName";
	
	public static final String RESPONSE_HEADER_TYPE = "contentType";


	public static final String ACTION_SHOW_REC_LIST = "GetFeedList";
	public static final String ACTION_PUBLISH_REC = "PublishTuijian";
	public static final String ACTION_PUBLISH_ASK_REC = "PublishNeed";
	public static final String ACTION_SHOW_CATEGORY_DYNAMIC  = "GetCategoryFeedList";
	public static final String ACTION_PRAISE_REC = "PraiseTuijian";
	public static final String ACTION_COMMENT_REC = "CommentTuijian";

	public static final String ACTION_SHOW_REC_DETAIL = "GetTuijianDetail";
	public static final String ACTION_SHOW_CATEGORY = "GetCategoryList";
	public static final String ACTION_SHOW_ENTITY = "GetEntityFeedList";
	public static final String ACTION_LIKE_CATEGORY = "LikeCategory";
	public static final String ACTION_GET_NEED_MATCH_LIST = "GetNeedMatchList";
	
	public static final int RECOMMEDATION_TYPE = 1;
	public static final int ASK_RECOMMEDATION_TYPE = 2;
	
	public static final int SOURCE_HOME_PAGE = 1001;
	public static final int SOURCE_ENTITY_PAGE = 1002;
}
