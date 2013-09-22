package com.mapabc.android.activity.base;


/**
 * @description: 获取菜单项对应的Action
 * @author: changbao.wang 2011-05-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class MenuActivityFactory {
	private static String[] searchlocActivities = {	
		Constants.ACTIVITY_SEARCH_FIRSTALPHABET,
		Constants.ACTIVITY_SEARCHPOIBYKEYWORD,
		Constants.ACTIVITY_SEARCH_FIRSTALPHABET,
		Constants.ACTIVITY_SEARCHAROUND,
		Constants.ACTIVITY_MYFAVORITES,
		Constants.ACTIVITY_MYFAVORITES,
		null,
		Constants.ACTIVITY_USEREYE};
	
	private static String[] ROUTEMANAGERACTIVITIES = { 
		null,
		null, 
		null, 
		Constants.ACTIVITY_GLANCEROUTE,
		Constants.ACTIVITY_ROUTEDESCRIPTION,
		null,
		Constants.ACTIVITY_TRACK,
		Constants.ACTIVITY_WZTACTIVITY };// 路径管理
	
	private static String[] OTHERFUNCTIONACTIVITYES = {
		Constants.ACTIVITY_SETTINGFORLIKE,
		null,
		Constants.ACTIVITY_ABOUTAUTONAVI,
		null };// 更多功能
	
	private MenuActivityFactory() {
	}

	public static String getSearchActivityIntent(int pos) {
		String intent = null;
		if (pos >= 0 || pos < searchlocActivities.length) {
			intent = searchlocActivities[pos];
		}
		return intent;
	}

	/**
	 * 获取路径管理菜单项中Action
	 * 
	 * 
	 *@param pos
	 *            菜单序号
	 *@return String
	 */
	public static String getRouteManagerActivityIntent(int pos) {
		String intent = null;
		if (pos >= 0 || pos < ROUTEMANAGERACTIVITIES.length) {
			intent = ROUTEMANAGERACTIVITIES[pos];
		}
		return intent;
	}

	/**
	 * 获取更多功能菜单项中Action
	 * 
	 * 
	 *@param pos
	 *            菜单序号
	 *@return String
	 */
	public static String getOtherFunctionActivityIntent(int pos) {
		String intent = null;
		if (pos >= 0 || pos < OTHERFUNCTIONACTIVITYES.length) {
			intent = OTHERFUNCTIONACTIVITYES[pos];
		}
		return intent;
	}
}

