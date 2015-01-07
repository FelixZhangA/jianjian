package com.wanxiang.recommandationapp.service.category;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wanxiang.recommandationapp.http.impl.JasonNetTaskMessage;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.util.AppConstants;


public class CategoryMessage extends JasonNetTaskMessage<ArrayList<Category>>
{

	public CategoryMessage( com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE httpType )
	{
		super(httpType);
		setRequestAction(AppConstants.ACTION_SHOW_CATEGORY);

	}

	@Override
	protected ArrayList<Category> parseNetTaskResponse( JSONObject jsonObject ) throws JSONException
	{
		ArrayList<Category> ret = new ArrayList<Category>();
		JSONArray array = jsonObject.getJSONArray(AppConstants.RESPONSE_HEADER_DATA);
		for (int i = 0; i < array.length(); i++)
		{
			JSONObject tmp = (JSONObject)array.get(i);
			Category r = translate(tmp);
			if (r != null && !TextUtils.isEmpty(r.getCategoryName()))
			{
				ret.add(r);
			}
		}
		return ret;

	}
/*
 * data": [
        {
            "id": 1,
            "name": "我喜欢",
            "children": [
                {
                    "id": 10001,
                    "name": "追剧",
                    "children": null
                },
                {
                    "id": 10005,
                    "name": "淘智能设备",
                    "children": null
                },
                {
                    "id": 10002,
                    "name": "玩游戏",
                    "children": null
                },
                {
                    "id": 10006,
                    "name": "炒股",
                    "children": null
                },
                {
                    "id": 10007,
                    "name": "听音乐",
                    "children": null
                },
                {
                    "id": 10008,
                    "name": "读书",
                    "children": null
                },
                {
                    "id": 10003,
                    "name": "下应用",
                    "children": null
                },
                {
                    "id": 10000,
                    "name": "看电影",
                    "children": null
                },
                {
                    "id": 10004,
                    "name": "爱美丽",
                    "children": null
                }
            ]
        },
        {
            "id": 2,
            "name": "我不喜欢",
            "children": []
        },
        {
            "id": 3,
            "name": "全部",
            "children": [
                {
                    "id": 10000,
                    "name": "看电影",
                    "children": null
                },
                {
                    "id": 10001,
                    "name": "追剧",
                    "children": null
                },
                {
                    "id": 10002,
                    "name": "玩游戏",
                    "children": null
                },
                {
                    "id": 10003,
                    "name": "下应用",
                    "children": null
                },
                {
                    "id": 10004,
                    "name": "爱美丽",
                    "children": null
                },
                {
                    "id": 10005,
                    "name": "淘智能设备",
                    "children": null
                },
                {
                    "id": 10006,
                    "name": "炒股",
                    "children": null
                },
                {
                    "id": 10007,
                    "name": "听音乐",
                    "children": null
                },
                {
                    "id": 10008,
                    "name": "读书",
                    "children": null
                },
                {
                    "id": 10009,
                    "name": "看网络小说",
                    "children": null
                },
                {
                    "id": 10010,
                    "name": "看动漫",
                    "children": null
                },
                {
                    "id": 10011,
                    "name": "下馆子",
                    "children": null
                },
                {
                    "id": 10012,
                    "name": "旅行游玩",
                    "children": null
                },
                {
                    "id": 10013,
                    "name": "看演出",
                    "children": null
                }
            ]
        }
    ]*/
	private Category translate( JSONObject jsonObject )
	{
		Category ret = new Category();
		try
		{
			ret.setCategoryId(jsonObject.getLong(AppConstants.RESPONSE_HEADER_ID));
			ret.setCategoryName(jsonObject.getString(AppConstants.RESPONSE_HEADER_NAME));

			ArrayList<Category> childList = new ArrayList<Category>();
			JSONArray array = jsonObject.getJSONArray(AppConstants.RESPONSE_HEADER_CATEGORY_CHILDREN);
			if (array != null && array.length() > 0)
			{
				for (int i = 0; i < array.length(); i++)
				{
					JSONObject tmp = (JSONObject)array.get(i);
					Category child = translate(tmp);
					childList.add(child);
				}
			}
			
			ret.setChildrenList(childList);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return ret;
	}

}
