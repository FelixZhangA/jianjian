package com.wanxiang.recommandationapp.ui.base;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wanxiang.recommandationapp.ui.base.TripBaseFragment.Anim;

/**
 * 页面跳转接口，用于控制页面跳转或启动新的activity
 * @author 弦叶
 * @date 2014-5-22
 */

public interface IPageSwitcher {

	/**
	 * 返回到某一个页面。jumpBean null时后退一步（只有一个fragment时会关闭Activityt）
	 * 
	 * @param msg
	 */
	public void popPage();

	/**
	 * 是否查找到某个page
	 */
	public boolean findPage(final String pageName);

	/**
	 * 跳转到某一个页面。
	 */
	public Fragment gotoPage(PageSwitchBean page);

	/**
	 * 打开一个新的页面
	 * 
	 * @param msg
	 * @return
	 */
	public Fragment openPage(PageSwitchBean page);
	
	public Fragment openPage(String pageName,Bundle bundle,Anim anim,boolean newActivity,boolean addToBackStack,boolean checkDoubleClick);

	public Fragment openPage(String pageName,Bundle bundle,int[] anim,boolean newActivity,boolean addToBackStack,boolean checkDoubleClick);

	/**
	 * fragmentTag 是否在当前顶上activity上的最顶上的fragment
	 * 
	 * @param fragmentTag
	 * @return
	 */
	public boolean isFragmentTop(String fragmentTag);

	/**
	 * 移除当前Acitivity不需要的fragment
	 * 
	 * @param fragmentList
	 */
	public void removeUselessFragment(ArrayList<String> fragmentList);

	/**
	 * 页面跳转，支持跨Activity进行传递数据
	 * 
	 * @param jumpBean
	 * @param fragment
	 * @return
	 */
	public Fragment openPageForResult(final PageSwitchBean page, final TripBaseFragment fragment);
	

}
