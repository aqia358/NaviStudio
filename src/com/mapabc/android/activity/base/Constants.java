package com.mapabc.android.activity.base;
/**
 * @description: 常量
 * @author: zhuhao 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class Constants {
	public static final String ACTIVITY_ENTER_SEQNO = "com.mapabc.android.activity.ActActivity";
    public static final String ACTIVITY_NAVISTUDIOACTIVITY="com.mapabc.android.activity.NaviStudioActivity";
    public static final String ACTIVITY_START_SPLASH ="com.mapabc.android.activity.AutoNaviSplashActicity";
    public static final String ACTIVITY_LOADING_LOGO = "com.mapabc.android.activity.AutoNaviLogoActivity";
    public static final String ACTIVITY_SEARCH_SEARCHLOCATION="com.mapabc.android.activity.search.SearchLocationActivity";
    public static final String ACTIVITY_SEARCH_FIRSTALPHABET = "com.mapabc.android.activity.search.SearchPOIByFirstAlphaBetActivity";
    public static final String ACTIVITY_RES_FIRSTALPHABET = "com.mapabc.android.activity.search.SearchResultFirstAlphaBetActivity";   
    public static final String ACTIVITY_CHOOSE_CITIES = "com.mapabc.android.activity.search.ChooseCityActivity";
    public static final String ACTIVITY_SEARCHPOIBYKEYWORD="com.mapabc.android.activity.search.SearchPOIByKeyWordActivity";
    public static final String ACTIVITY_SEARCHRESULTOFKEYWORD="com.mapabc.android.activity.search.SearchResultOfKeyWordActivity";
    public static final String ACTIVITY_OTHERFUNCTION ="com.mapabc.android.activity.setting.OtherFunctionActivity";
    public static final String ACTIVITY_SETTINGFORLIKE="com.mapabc.android.activity.setting.SettingForLikeActivity";
    public static final String ACTIVITY_SETTINGFORMAP="com.mapabc.android.activity.setting.SettingMapActivity";
    public static final String ACTIVITY_SETTINGFORROUTE="com.mapabc.android.activity.setting.SettingRouteActivity";
    public static final String ACTIVITY_SETTINGFORTRAFFICRADIO="com.mapabc.android.activity.setting.SettingTrafficRadioActivity";
    public static final String ACTIVITY_SEARCHAROUND = "com.mapabc.android.activity.search.SearchAroundPOIActivity";
    public static final String ACTIVITY_SEARCHAROUNDRESULT="com.mapabc.android.activity.search.SearchAroundResultActivity";
    public static final String ACTIVITY_ROUTE_MANAGER ="com.mapabc.android.activity.route.RouteManagerActivity";
    public static final String ACTIVITY_GLANCEROUTE="com.mapabc.android.activity.route.GlanceRouteActivity";
    public static final String ACTIVITY_ROUTEDESCRIPTION="com.mapabc.android.activity.route.RouteDescriptionActivity";
    public static final String ACTIVITY_CROSSINGVIEW="com.mapabc.android.activity.route.CrossingViewActivity";
    public static final String ACTIVITY_GPSINFO="com.mapabc.android.activity.GPSInfoActivity";
    public static final String ACTIVITY_MYFAVORITES="com.mapabc.android.activity.search.MyFavoritesActivity";
    public static final String ACTIVITY_HISTORYARRIVE="com.mapabc.android.activity.search.HistoryArriveActivity";
    public static final String ACTIVITY_ABOUTAUTONAVI="com.mapabc.android.activity.setting.AboutAutonaviActivity";
    public static final String ACTIVITY_EDITPOI="com.mapabc.android.activity.search.EditPOIInfoActivity";
    public static final String ACTIVITY_USEREYE = "com.mapabc.android.activity.search.SearchUserEyeActivity";
    public static final String ACTIVITY_TRACK = "com.mapabc.android.activity.route.track.TrackManagerActivity";
    public static final String ACTIVITY_NAVIRESULTLIST = "com.mapabc.android.activity.NaviResultListActivity";
    public static final String KEYWORD_ID = "_id";
    public static final String KEYWORD_NAME = "name";
    public static final String KEYWORD_MY_KEYWORD = "keyword";
    public static final String ACTIVITY_TRACELIST = "com.mapabc.android.activity.route.track.TraceListActivity";
    
    public static final String ACTIVITY_EDITUSEREYE = "com.mapabc.android.activity.search.EditUserEyeActivity";
    public static final String INTENT_ACTION="myaction";
    public static String CENTER_POINT = "center point";
    public static String CUR_SELECT_CATEGORY = "current category";
    public static String CUR_POI = "current poi";
    public static String RESULT_NAME = "name";
    public static String RESULT_ADDR = "address";
    public static String RESULT_DIST = "dist";
    public static String RESULT_TEL = "tel";
    public static String ADMIN_CODE = "admin code";
    public static String CUR_INPUT = "current enter string";
    public static String CUR_CITY = "current city";
    public static double K = 1000.0D;
    public static String RESULT_DISTRICT = "district";
    public static String POI_EVENT = "event";
    public static String POI_DATA = "data";
    public static String SEARCH_RESULT = "search result";
    public static String BACK_TARGET = "back target";
    public static String SELECT_CITY = "select city";
    public static int REQUEST_CITY = 2;
    public static final String SEARCHTYPE_KEYWORD="SearchType";
    public static final int INTENT_TYPE_NEWNAVISTUDIO=0;//程序启动
    public static final int INTENT_TYPE_CHANGEROUTEMODE=1;//修改路径规划原则
    public static final int INTENT_TYPE_STOP_SIMNAVI=2;//停止模拟导航
    public static final int INTENT_TYPE_START_SIMNAVI=3;//启动模拟导航
    public static final int INTENT_TYPE_SETSTARTPOINT=4;//设置起点
    public static final int INTENT_TYPE_SETENDPOINT=5;//设置终点
    public static final int INTENT_TYPE_LOOKPOI=6;//查看POI
    public final static int SEARCH_MYFAVORITES = 4;//收藏夹类别
	public final static int SEARCH_HISTORYARRIVE = 5;//目的地类别
	public final static String SEARCHAROUND_CENTER_POINT = "center";//中心点参数
	public final static String SEARCHAROUND_OTYPE = "Otype";//周边查询一级分类
	public final static String SEARCHAROUND_STYPE = "Stype";//周边查询二级分类
	
	public static final String WZT_DATA="wzt_data";
    public static final String WZT_DAVIGATE="wzt_navigate_data";
    public static final int INTENT_TYPE_LOOKDESTTASK=7;//查看目的地任务
    public static final int INTENT_TYPE_NAVIGATION=8;//目的地任务导航
    
    public static final String ACTIVITY_WZTACTIVITY="com.mapabc.android.activity.wzt.WZTActivity";
    
    public static final int TRACE_POINT=8;//录制轨迹时打点图层id
}
