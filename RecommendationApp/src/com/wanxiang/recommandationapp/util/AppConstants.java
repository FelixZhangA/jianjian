package com.wanxiang.recommandationapp.util;

public class AppConstants
{
    public static final String RECOMMENDATION = "recommendation";
    public static final String INTENT_CATEGORY = "categoryName";
    public static final String HEADER_REC_ID = "recommendId";
	public static final String HEADER_USER_ID = "userId";
	public static final String HEADER_CATEGORY = "categoryName";
	public static final String HEADER_ENTITY = "entityName";
	public static final String HEADER_PAGE_SIZE = "size";
	public static final String HEADER_NEXT_ID = "nextId";
	public static final String HEADER_PAGE_INDEX = "pageIndex";
	public static final String HEADER_DESCRIPTION = "description";
	public static final String HEADER_IMEI = "imei";
	public static final String HEADER_IS_NEW = "isNew";
	public static final String HEADER_CANCEL_PRAISE = "cancel";
	public static final String HEADER_COMMENT_CONTENT = "content";
	public static final String HEADER_LEVEL = "level";
	public static final String HEADER_NEEDID = "needId";
	public static final String HEADER_PHONE = "phone";
	public static final String HEADER_PASSWORD = "password";
	public static final String HEADER_USER_NAME = "name";
	public static final String HEADER_VERIFY_CODE = "code";
	public static final String HEADER_CONTACTS = "contacts";
	public static final String HEADER_TOKEN = "token";
	public static final String HEADER_HEAD_IMAGE = "headImage";
	public static final String HEADER_SIGNATURE = "signature";
	public static final String HEADER_REMARK = "remark";
	public static final String HEADER_USER = "user";

	
	public static final String RESPONSE_HEADER_COMMENT = "comments";
	public static final String RESPONSE_HEADER_ID = "id";
	public static final String RESPONSE_HEADER_SUCCESS = "success";
	public static final String RESPONSE_HEADER_ERROR = "error";
	public static final String RESPONSE_HEADER_DATA = "data";
	public static final String RESPONSE_HEADER_RECOMMENDATION_ID = "contentId";
	public static final String RESPONSE_HEADER_OWNER_ID = "ownerId";

	public static final String RESPONSE_HEADER_UPDATE_TIME = "updateTime";
	public static final String RESPONSE_HEADER_USER_ID = "userId";
	public static final String RESPONSE_HEADER_REPLYED_USER_ID = "replyedUserId";
	public static final String RESPONSE_HEADER_LIKE_LIST = "likeList";
	public static final String RESPONSE_HEADER_OTHER_LIST = "otherList";

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


	public static final String ACTION_PUBLISH_REC = "PublishTuijian";
	public static final String ACTION_PUBLISH_ASK_REC = "PublishNeed";
	public static final String ACTION_COMMENT_REC = "CommentTuijian";

	public static final String ACTION_LIKE_CATEGORY = "LikeCategory";
	public static final String ACTION_GET_NEED_MATCH_LIST = "GetNeedMatchList";
	
	
	public static final String ACTION_LOGIN = "account/login";
	public static final String ACTION_REGISTER = "account/register";
	public static final String ACTION_SEND_PINCODE = "account/sendcode";
	public static final String ACTION_CONTACTS_UPLOAD = "contacts/upload";
	public static final String ACTION_SHOW_REC_LIST = "feed/list";
	public static final String ACTION_SHOW_CATEGORY = "category/list";
	public static final String ACTION_SHOW_CATEGORY_DYNAMIC  = "feed/categorylist";
	public static final String ACTION_SHOW_REC_DETAIL = "recommend/detail";
	public static final String ACTION_SHOW_ENTITY = "feed/entitylist";
	public static final String ACTION_PRAISE_REC = "recommend/praise";

	public static final int RECOMMEDATION_TYPE = 1;
	public static final int ASK_RECOMMEDATION_TYPE = 2;
	
	public static final int SOURCE_HOME_PAGE = 1001;
	public static final int SOURCE_ENTITY_PAGE = 1002;
}
