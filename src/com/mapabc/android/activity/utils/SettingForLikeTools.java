package com.mapabc.android.activity.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManager;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.TTSAPI;
import com.mapabc.naviapi.map.DayOrNightControl;
import com.mapabc.naviapi.route.RoutePlayOptions;
import com.mapabc.naviapi.route.SystemTime;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;

/**
 * 系统底层参数获取，参数设置交互
 */
public class SettingForLikeTools {
	private static final String TAG="SettingForLikeTools";
	public static final String ISfIRST_START = "isFirstStart";//是否第一次安装并启动系统
	public static final String MAPSETTINGSTATUS = "isFirstMapSetting";//是否第一次初始化地图设置
	public static final String ROUTESETTINGSTATUS = "isFirstRouteSetting";//是否第一次初始化导航设置
	public static final String TRAFFICRADIOSETTING = "isFirstTrafficSetting";//是否第一次初始化交通雷达设置
	public static final String SCALE="scale";
	public static final String Disclaimer="isShowDisclaimer";//启动声明
	private static final String SOFTINSHAREDNAME="MapabcAutoNavi";//在SharedPreferences里的名称
	public static final String DAYORNIGHT="dayornight";//昼夜模式
    public static final String ADCODE="ADCode";//查询默认城市
    public static final String VEHICLE_X="vehiclex";
    public static final String VEHICLE_Y="vehicley";
	public static final String AUTOGOBACKTOCAR="AutoGoBackToCar";//自动回车位
	public static boolean AUTOGOBACKTOCAR_BOOLEAN = true;
    public static final String NAVI_STATUS="navistatus";//导航状态
    public static final String ROUTECALCMODE="RouteCalcMode";//路径计算模式
    public static final String ROUTECALCNETMODE="RouteCalNetMode";//路径网络计算模式
	public static final String DEMOSPEED="DemoSpeed";//模拟导航速度
	public static final String DISPLAYMODEL ="DisplayModel";//地图模式
	public static boolean ISAUTOBRIGHTNESS=false;//是否开启了亮度自动调节，记录了本软件启动前的系统状态
	public static int SCREENBRIGHTNESS=0;//记录了本软件启动前的背景灯亮度值
	public static final String BRIGHTNESS="Brightness";//背景灯
	public static final String POWERMODEL="PowerModel";//省电模式
    public static final String OVERSPEED="OverSpeed";//超速报警
    public static final String EDOGENABLE="EDogEnable";//电子眼开关
	public static final String ROADDIRECTION="RoadDirection";//是否播放道路方向提示
	public static final String TTSROLE="TTSRole";//语音类别
	public static final String MAPPALETTE="MapPalette";//当前系统生效色盘
	public static final String TTSSTATE="TTSState";//语音开关
	public static final String FRONTTRAFFICRADIO="FrontTrafficRadio";//前方路况播报
	public static boolean FRONTTRAFFICRADIO_BOOLEAN = false; 
	public static final String SURROUNDTRAFFICRADIO="SurroundTrafficRadio";//周边路况播报
	public static boolean SURROUNDTRAFFICRADIO_BOOLEAN = false; 
	public static final String UPDATE_SYSTEM="update_system";//系统升级
	
	/**
	 * 设置软件升级状态
	 * @param context 
	 */
	public static void setUpdateState(Context context){
		if(getUpdateState(context)==0){
			SettingForLikeTools.setIntSysPara(context, UPDATE_SYSTEM, 1);
		}else{
			SettingForLikeTools.setIntSysPara(context, UPDATE_SYSTEM, 0);
		}
	}
	/**
	 * 获取软件升级状态
	 * @param context 
	 * @return 0联网，1不联网
	 */
	public static int getUpdateState(Context context){
		return getIntSysPara(context,UPDATE_SYSTEM,0);
	}
	/**
	 * 
	 * @Copyright:mapabc
	 * @description:设置更新原始状态
	 * @author fei.zhan
	 * @date 2013-4-11 
	 * @param context void
	 */
	public static void setInitUpdateState(Context context){
		SettingForLikeTools.setIntSysPara(context, UPDATE_SYSTEM, 0);
	}
	/*
	 * 设置TTS状态
	 */
	public static void setTTSState(Context context){
			if(isTTSOpen(context)){
				TTSAPI.getInstance().setMuteVolumn(true); //静音
				SettingForLikeTools.setIntSysPara(context, TTSSTATE, 1);
			}else{
				TTSAPI.getInstance().setMuteVolumn(false); //不静音
				SettingForLikeTools.setIntSysPara(context, TTSSTATE, 0);
			}
			Log.e(TAG, "TTSSTATE:"+SettingForLikeTools.isTTSOpen(context));
	}
	/*
	 * 设置TTS原始状态
	 */
	public static void setInitTTSState(Context context){
		TTSAPI.getInstance().setMuteVolumn(false);
		SettingForLikeTools.setIntSysPara(context, TTSSTATE, 0);
	}
	/**
	 * 获取TTS语音状态
	 * @return boolean true 表示不静音，false静音
	 */
	public static boolean isTTSOpen(Context context){

			if(SettingForLikeTools.getIntSysPara(context, TTSSTATE,0)==0){
			   return true;	
			}else{
				return false;
			}
	}
	
	
	public static void setInitTalkDirection(Context context) {
		RoutePlayOptions options = new RoutePlayOptions();
		RouteAPI.getInstance().getRoutePlayOptions(options);
		options.playRoadName=true;
		RouteAPI.getInstance().setRoutePlayOptions(options);
		SettingForLikeTools.setIntSysPara(context, ROADDIRECTION, 1);
	}
	
    /**
	 * 是否播放道路方向提示
	 *  
	 *@return boolean true已开，false已关
	 */
	public static boolean isTalkDirection(Context context){
		if(SettingForLikeTools.getIntSysPara(context,ROADDIRECTION,1)==1){
			return true;
		}else{
			return false;
		}
	}
    /**
	 * 设置是否播放道路方向提示
	 *  
	 */
	public static void setTalkDirection(Context context){

		boolean state = isTalkDirection(context);
		RoutePlayOptions options = new RoutePlayOptions();
		RouteAPI.getInstance().getRoutePlayOptions(options);
		if(state){
			options.playRoadName=false;
			RouteAPI.getInstance().setRoutePlayOptions(options);
			SettingForLikeTools.setIntSysPara(context, ROADDIRECTION, 0);
		}else{
			options.playRoadName=true;
			RouteAPI.getInstance().setRoutePlayOptions(options);
			SettingForLikeTools.setIntSysPara(context, ROADDIRECTION, 1);
		}
	}
    /**
	 * 获取电子眼开关状态
	 *  
	 *@return boolean true已开，false已关
	 */
	public static boolean isEDogEnable(Context context){
		if(SettingForLikeTools.getIntSysPara(context,EDOGENABLE,1)==1){
			return true;
		}else{
			return false;
		}
	}
	 /**
	 * 获取电子眼开关状态
	 *  
	 *@return boolean true已开，false已关
	 */
//	public static boolean isEDogEnable0(Context context){
//		if(SettingForLikeTools.getIntSysPara(context,EDOGENABLE,0)==0){
//			return false;
//		}else{
//			return true;
//		}
//	}
	/**设置 语音类别*/
	public static void setRole(int index,Context context){
		setIntSysPara(context,TTSROLE,index);
		String[] voiceTypes=context.getResources().getStringArray(R.array.voicetypeitem);
		String voiceType=voiceTypes[index];
		if(voiceType.equals("普通话")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_MANDARIN);
		}else if(voiceType.equals("粤语")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_CANTONESE);
		}else if(voiceType.equals("四川话")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_SICHUAN);
		}else if(voiceType.equals("东北话")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_NORTHEAST);
		}else if(voiceType.equals("台湾话")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_TAIWANESE);
		}else if(voiceType.equals("湖南话")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_HUNAN);
		}else if(voiceType.equals("河南话")){
			TTSAPI.getInstance().setPlayRole(Const.ROLE_HENAN);
		}
//		TTSManager.getInstance().setPlayRole(EnumPlayRole.ROLE_HENAN);
	}
	public static int getRole(Context context){
		return getIntSysPara(context, TTSROLE, 0);
	}
	
	
	/*
	 * 设置电子眼原始状态
	 */
	public static void setInitEDogState(Context context){
		RoutePlayOptions options = new RoutePlayOptions();
		RouteAPI.getInstance().getRoutePlayOptions(options);
		options.playCamera=true;
		RouteAPI.getInstance().setRoutePlayOptions(options);
		SettingForLikeTools.setIntSysPara(context, EDOGENABLE, 1);
	}
	
    /**
	 * 设置电子眼开关
	 *  
	 */
	public static void setEDogEnable(Context context){

		boolean state = isEDogEnable(context);
		RoutePlayOptions options = new RoutePlayOptions();
		RouteAPI.getInstance().getRoutePlayOptions(options);
		if(state){
			options.playCamera=false;
			RouteAPI.getInstance().setRoutePlayOptions(options);
			SettingForLikeTools.setIntSysPara(context, EDOGENABLE, 0);
		}else{
			options.playCamera=true;
			RouteAPI.getInstance().setRoutePlayOptions(options);
			SettingForLikeTools.setIntSysPara(context, EDOGENABLE, 1);
		}
	}
	
	/*
	 * 设置超速报警原始状态
	 */
	public static void setInitOverSpeed(Context context){
		setIntSysPara(context, OVERSPEED, 1); 
	}
	/*
	 * 设置是否超速报警，0不报警，1报警
	 */
    public static void setOverSpeed(Context context){
    	int res = getOverSpeed(context);
    	if(res==1){
    		setIntSysPara(context, OVERSPEED, 0); 
    	}else{
    		setIntSysPara(context, OVERSPEED, 1); 
    	}
    }
	/*
	 * 设置是否超速报警，0不报警，1报警
	 */
//    public static void setOverSpeed0(Context context){
//    	int res = getOverSpeed0(context);
//    	if(res==1){
//    		setIntSysPara(context, OVERSPEED, 1); 
//    	}else{
//    		setIntSysPara(context, OVERSPEED, 0); 
//    	}
//    }
	/*
	 * 设置是否超速报警，0不报警，1报警
	 */
	public static int getOverSpeed(Context context){
        return SettingForLikeTools.getIntSysPara(context, OVERSPEED, 1); 
	}
	/*
	 * 设置是否超速报警，0不报警，1报警
	 */
	public static int getOverSpeed0(Context context){
        return SettingForLikeTools.getIntSysPara(context, OVERSPEED, 0); 
	}
	/*
	 * 
	 * 恢复系统亮度
	 */
	public static void reSettingScreenBrightness(Context context){
		if(SCREENBRIGHTNESS==0){
			return ;
		}
		int brightness = 0;
         if(ISAUTOBRIGHTNESS){
        	 startAutoBrightness( context);
         }else{
        	 stopAutoBrightness(context);
         }
         brightness = SCREENBRIGHTNESS;
         WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
         lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
         ((Activity) context).getWindow().setAttributes(lp);
         saveBrightness(context.getContentResolver(),brightness);
	}
	/*
	 * 系统启动时记录系统当前亮度，并设置背景灯亮度
	 * 
	 */
	public static void systemStart_SetScreenBrightness(Context context){
		SCREENBRIGHTNESS = SettingForLikeTools.getScreenBrightness(context);
		ContentResolver aContentResolver = context.getContentResolver();
		ISAUTOBRIGHTNESS = SettingForLikeTools.isAutoBrightness(aContentResolver);
		int index = SettingForLikeTools.getIntSysPara(context, POWERMODEL, 0);
   	    if(index==1){
		    SettingForLikeTools.setIntSysPara(context, POWERMODEL, 0);
   	    }else{
		    SettingForLikeTools.setIntSysPara(context, POWERMODEL, 1);
   	    }
		SettingForLikeTools.setPowerModel(context);
	}
	/*
     * 保存背景灯设置	
     */
	private static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = android.provider.Settings.System
                .getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(resolver, "screen_brightness",
                brightness);
        resolver.notifyChange(uri, null);
    }
	/**
     * 获取屏幕的亮度
     * 
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }
    /**
	 * 获取背景灯状态
	 *@param context 上下文  
	 *@return int 0自动；1常量
	 */
	public static int getBrightness(Context context){
        return SettingForLikeTools.getIntSysPara(context, BRIGHTNESS, 0); 
	}
	
	/**
	 * 设置背景灯状态
	 *  
	 *@param context 上下文
	 *@param index 索引
	 */
	public static void setBrightness(Context context,int index) {   
		if(SettingForLikeTools.getBrightness(context)==index){
			return ;
		}
		int brightness = 0;
         if(index==0){
        	 startAutoBrightness( context);
        	 SettingForLikeTools.setIntSysPara(context, BRIGHTNESS, 0); 
//        	 return ;
         }else{
        	 stopAutoBrightness(context);
        	 SettingForLikeTools.setIntSysPara(context, BRIGHTNESS, 1); 
         }
         brightness = getScreenBrightness(context);
         Log.e(TAG, "brightness:"+brightness);
         WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
         lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
         Log.e(TAG,"setBrightness:"+lp.screenBrightness);
         ((Activity) context).getWindow().setAttributes(lp);
         saveBrightness(context.getContentResolver(),brightness);
	}
	/**
     * 判断是否开启了自动亮度调节
     * 
     * @param aContext
     * @return
     */
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }
	/**
     * 开启亮度自动调节
     * 
     * @param activity
     */
	private static void startAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
    /**
     * 停止自动亮度调节
     * 
     * @param activity
     */
    private static void stopAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }
    /**
	 * 是否点亮屏幕
     *@param context 上下文
	 */
	public static void setPowerModel(Context context){
		int index = SettingForLikeTools.getIntSysPara(context, POWERMODEL, 1);
		int brightness = 255;
         if(index==1){
        	 stopAutoBrightness(context);
        	 SettingForLikeTools.setIntSysPara(context, POWERMODEL, 0); 
        	 brightness = 255;
         }else{
        	 stopAutoBrightness(context);
        	 SettingForLikeTools.setIntSysPara(context, POWERMODEL, 1); 
        	 brightness = 70;
         }
         

         Log.e(TAG, "brightness:"+brightness);
         WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
         lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
         Log.e(TAG,"setBrightness:"+lp.screenBrightness);
         ((Activity) context).getWindow().setAttributes(lp);
         saveBrightness(context.getContentResolver(),brightness);

    }
    /**
     * @brief 获取导航路径计算模式。
	 * @return 0最优策略、1快速路优先、2近距离优先.
     */
    public static int getRouteCalcMode(Context context){
    	return SettingForLikeTools.getIntSysPara(context, ROUTECALCMODE, 0);
    }
    /**
	* @brief 设置导航路径默认计算模式（0最优策略、1快速路优先、2近距离优先）。
	* @param enCalcMode 导航路径计算模式。
	*/
    public static void setRouteCalcMode(Context context,int mode){
    	SettingForLikeTools.setIntSysPara(context, ROUTECALCMODE, mode);
    }
    /**
     * @brief 获取导航路径网络计算模式。
	 * @return 0速度优先、1费用优先、2距离优先、3TMC耗油最少、4TMC最快、5不走快速路、6国道优先、7省道优先、9多策略、10多策略(针对audi)
     */
    public static int getRouteCalNetMode(Context context){
    	return SettingForLikeTools.getIntSysPara(context, ROUTECALCNETMODE, 0);
    }
    /**
     * 设置导航路径网络计算模式
     * @param context
     * @param mode 0速度优先、1费用优先、2距离优先、3TMC耗油最少、4TMC最快、5不走快速路、6国道优先、7省道优先、9多策略、10多策略(针对audi)
     */
    public static void setRouteCalNetMode(Context context,int mode){
    	SettingForLikeTools.setIntSysPara(context, ROUTECALCNETMODE, mode);
    }
    
	/*
	 * 设置前方播报原始状态
	 */
	public static void setInitFrontTrafficRadio(Context context){
		SettingForLikeTools.setIntSysPara(context, FRONTTRAFFICRADIO, 0);
		FRONTTRAFFICRADIO_BOOLEAN = false;
//		FrontTrafficRadio.getInstance().closeTrafficRadio();
	}
    
    /**
	 * 设置是否播报前方路况
	 */
    public static void setFrontTrafficRadio(Context context) {
    	int id = SettingForLikeTools.getIntSysPara(context,FRONTTRAFFICRADIO,0);
		if(id==0){
			//设置为播报
			SettingForLikeTools.setIntSysPara(context, FRONTTRAFFICRADIO, 1);
			FRONTTRAFFICRADIO_BOOLEAN = true;
//			FrontTrafficRadio.getInstance().openTrafficRadio();
		}else{
			SettingForLikeTools.setIntSysPara(context, FRONTTRAFFICRADIO, 0);
			FRONTTRAFFICRADIO_BOOLEAN = false;
//			FrontTrafficRadio.getInstance().closeTrafficRadio();
		}
	}
    /**
	 * 返回是否播报前方路况
	 * 0 false;1 true
	 */
    public static int getFrontTrafficRadio(Context context){
		return SettingForLikeTools.getIntSysPara(context,FRONTTRAFFICRADIO,0);
	}
	/*
	 * 设置周边播报原始状态
	 */
	public static void setInitSurroundTrafficRadio(Context context){
		SettingForLikeTools.setIntSysPara(context, SURROUNDTRAFFICRADIO, 0);
		SURROUNDTRAFFICRADIO_BOOLEAN = false;
//		SurroundTrafficRadio.getInstance().closeTrafficRadio();
	}
    /**
	 * 设置是否播报周边路况
	 */
    public static void setSurroundTrafficRadio(Context context) {
    	int id = SettingForLikeTools.getIntSysPara(context,SURROUNDTRAFFICRADIO,0);
		if(id==0){
			//设置为播报
			SettingForLikeTools.setIntSysPara(context, SURROUNDTRAFFICRADIO, 1);
			SURROUNDTRAFFICRADIO_BOOLEAN = true;
//			SurroundTrafficRadio.getInstance().openTrafficRadio();
		}else{
			SettingForLikeTools.setIntSysPara(context, SURROUNDTRAFFICRADIO, 0);
			SURROUNDTRAFFICRADIO_BOOLEAN = false;
//			SurroundTrafficRadio.getInstance().closeTrafficRadio();
		}
	}
    /**
	 * 返回是否播报周边路况
	 * 0 false;1 true
	 */
    public static int getSurroundTrafficRadio(Context context){
		return SettingForLikeTools.getIntSysPara(context,SURROUNDTRAFFICRADIO,0);
	}
    
	/*
	 * 设置自动回车位原始状态
	 */
	public static void setInitGoBackCar(Context context){
		SettingForLikeTools.setIntSysPara(context, AUTOGOBACKTOCAR, 1);
		AUTOGOBACKTOCAR_BOOLEAN = true;
	}
	/**
	 * 设置是否自动回车位
	 */
	public static void setAutoGoBackToCar(Context context){
		int id = SettingForLikeTools.getIntSysPara(context,AUTOGOBACKTOCAR,1);
		if(id==0){
			//设置为自动回车位
			SettingForLikeTools.setIntSysPara(context, AUTOGOBACKTOCAR, 1);
			AUTOGOBACKTOCAR_BOOLEAN = true;
		}else{
			SettingForLikeTools.setIntSysPara(context, AUTOGOBACKTOCAR, 0);
			AUTOGOBACKTOCAR_BOOLEAN = false;
		}
	}
    /**
	 * 返回是否自动回车位
	 * 0 false;1 true
	 */
	public static int getAutoGoBackToCar(Context context){
		return SettingForLikeTools.getIntSysPara(context,AUTOGOBACKTOCAR,1);
	}
    /**
	 * 保存车位点X
	 */
    public static void setVehicleX(Context context,String value){
    	setStrSysPara(context,VEHICLE_X,value);
    }
    /**
	 * 保存车位点Y
	 */
    public static void setVehicleY(Context context,String value){
    	setStrSysPara(context,VEHICLE_Y,value);
    }
    /**
	 * 获取车位点Y
	 */
    public static String getVehicleY(Context context,String default_value){
    	return getStrSysPara(context, VEHICLE_Y, default_value);
    }
    /**
	 * 获取车位点X
	 */
    public static String getVehicleX(Context context,String default_value){
    	return getStrSysPara(context, VEHICLE_X, default_value);
    }
    /**
	 * 查询搜索默认城市
	 */
    public static void setADCode(Context context,String value){
		SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
		Editor editor = preferences.edit();
		editor.putString(ADCODE, value);
		editor.commit();
    }
	/**
	 * 昼夜模式设置
	 * 
	 *@return String 获取城市编码
	 */
    public static String getADCode(Context context){
    	SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
        String state = preferences.getString(ADCODE, "110100");
		return state;
    }
    /**
	 * 设置POI密度
	 *@param index 0慢速；1中速；2快速
	 */
	public static void setDemoSpeed(int index,Context context){
		RouteAPI.getInstance().setSimNaviSpeed(index);
		SettingForLikeTools.setIntSysPara(context, DEMOSPEED,index);
	}
	/**
	 * 获取模拟速度
	 *  
	 *@return int 0慢速；1中速；2快速
	 */
	public static int getDemoSpeed(Context context){
		return SettingForLikeTools.getIntSysPara(context, DEMOSPEED,1);
	}
	
	/**
	 * 初始化昼夜模式,默认为自动
	 */
	public static void setInitDayOrNight(Context context){
		SystemTime object = new SystemTime();
		RouteAPI.getInstance().getSystemTime(object);
		NSLonLat pos = MapAPI.getInstance().getVehiclePos();
		if (RouteAPI.getInstance().isDayTime(pos.x,pos.y,object)) {
			DayOrNightControl.mdayOrNight=true;
			MapAPI.getInstance().setDayOrNightMode(Const.MAP_STYLE_DAY);
		}else {
			DayOrNightControl.mdayOrNight=false;
			MapAPI.getInstance().setDayOrNightMode(Const.MAP_STYLE_NIGHT);
		}
		DayOrNightControl.setDayNightMode(2);
		setIntSysPara(context,DAYORNIGHT,2);
	}
	/**
	 * 昼夜模式设置
	 * 
	 *@return int 0白天；1夜晚；2自动
	 */
	public static int getDayOrNight(Context context){
		return getIntSysPara(context,DAYORNIGHT,2);
	}
	
	/**
	 * 设置昼夜模式
	 *  
	 *@param index 0白天；1夜晚；2自动
	 */
	public static void setDayOrNight(Context context,int index){
		if(index == 0){
			MapAPI.getInstance().setDayOrNightMode(Const.MAP_STYLE_DAY);
			DayOrNightControl.setDayNightMode(Const.MAP_STYLE_DAY);
		}else if(index == 1){
			MapAPI.getInstance().setDayOrNightMode(Const.MAP_STYLE_NIGHT);
			DayOrNightControl.setDayNightMode(Const.MAP_STYLE_NIGHT);
		}else if(index == 2){
			SystemTime object = new SystemTime();
			RouteAPI.getInstance().getSystemTime(object);
			NSLonLat pos = MapAPI.getInstance().getVehiclePos();
			if(pos.x>0){
			if (RouteAPI.getInstance().isDayTime(pos.x,pos.y,object)) {
				DayOrNightControl.mdayOrNight=true;
				MapAPI.getInstance().setDayOrNightMode(Const.MAP_STYLE_DAY);
			}else {
				DayOrNightControl.mdayOrNight=false;
				MapAPI.getInstance().setDayOrNightMode(Const.MAP_STYLE_NIGHT);
			}
			}
			DayOrNightControl.setDayNightMode(2);
		}
		setIntSysPara(context,DAYORNIGHT,index);
		
	}
	/**
	 * 设置地图显示模式
	 * 
	 *@param newindex
	 *            地图显示模式数组索引 0 北首朝上，1 车首朝上，23D模式
	 *@return
	 */
	public static void setMapModel(int newindex,Context context) {
		SettingForLikeTools.setIntSysPara(context, SettingForLikeTools.DISPLAYMODEL, newindex);
		MapAPI.getInstance().setMapView(newindex);
	}
	/**
	 * 获取地图显示模式索引
	 * 
	 *@return int 地图显示模式数组索引
	 */
	public static int getMapModel(Context context) {
		return SettingForLikeTools.getIntSysPara(context, SettingForLikeTools.DISPLAYMODEL, 0);
	}
	
	
	
	/**恢复出厂设置*/
	public static void resetPara(Context context,String type){
		SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
		if(preferences!=null){
		  Editor editor = preferences.edit();
		  if (type=="mapsetting") {
			editor.remove(DISPLAYMODEL);
			editor.remove(DISPLAYMODEL);
			editor.remove(POWERMODEL);
			editor.remove(AUTOGOBACKTOCAR);
			editor.remove(MAPPALETTE);
			editor.commit();
			setMapModel(getMapModel(context), context);//DISPLAYMODEL ="DisplayModel";//视图模式
			setInitDayOrNight(context);//DISPLAYMODEL ="DisplayModel";//昼夜模式
			setPowerModel(context);					//POWERMODEL="PowerModel";//省电模式
			setInitGoBackCar(context);			//AUTOGOBACKTOCAR="AutoGoBackToCar";//自动回车位
			setMapColor(getMapColorIndex(context), context);//MAPPALETTE="MapPalette";// 地图配色
			
			}else if (type=="routesetting") {
				editor.remove(ROADDIRECTION);
				editor.remove(DEMOSPEED);
				editor.remove(TTSSTATE);
				editor.remove(OVERSPEED);
				editor.remove(TTSROLE);
				editor.remove(EDOGENABLE);
				editor.commit();
				setInitTalkDirection(context);					//ROADDIRECTION 是否播放道路方向提示
				setDemoSpeed(getDemoSpeed(context), context);//DEMOSPEED="DemoSpeed";//模拟导航速度
				setInitTTSState(context);				//TTSSTATE="TTSState";//语音开关
				setInitOverSpeed(context);				//OVERSPEED="OverSpeed";//超速报警
				setRole(getRole(context), context);		//TTSROLE="TTSRole";//语音类别
				setInitEDogState(context);				//EDOGENABLE="EDogEnable";//电子眼开关
			}else if (type=="trafficradiosetting") {
				editor.remove(FRONTTRAFFICRADIO);
				editor.remove(SURROUNDTRAFFICRADIO);
				editor.remove(UPDATE_SYSTEM);
				editor.commit();
//				setInitFrontTrafficRadio(context);		//FRONTTRAFFICRADIO="FrontTrafficRadio";//前方路况播报
//				setInitSurroundTrafficRadio(context);	//SURROUNDTRAFFICRADIO="SurroundTrafficRadio";//周边路况播报
//				SettingForLikeTools.setUpdateState(context);
				setInitUpdateState(context);
			} 
		  
		}
	}
	/**
	 * 获取保存系统参数
	 *@param context 上下文
	 *@param name 参数名称
	 *@param value 参数值
	 */
	public static void setIntSysPara(Context context,String name,int value){
		SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
		Editor editor = preferences.edit();
		editor.putInt(name, value);
		editor.commit();
	}
	/**
	 * 获取保存系统参数
	 *@param context 上下文
	 *@param name 参数名称
	 *@param default_value 默认值
	 *@return int 参数值
	 */
	public static int getIntSysPara(Context context,String name,int default_value){
		SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
        int state = preferences.getInt(name, default_value);
		return state;
	}
	public static void setStrSysPara(Context context,String name,String value){
		SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
		Editor editor = preferences.edit();
		editor.putString(name, value);
		editor.commit();
	}
	public static String getStrSysPara(Context context,String name,String default_value){
		SharedPreferences preferences =context.getSharedPreferences(SOFTINSHAREDNAME, Context.MODE_PRIVATE);   
		String state = preferences.getString(name, default_value);
		return state;
	}
	/*
	 * 设置系统是否第一次运行参数
	 * @return int 0新安装的系统，1非新安装系统
	 */
	public static int getSystemState(Context context){
		return SettingForLikeTools.getIntSysPara(context, ISfIRST_START, 0);
	}

	/*
	 * 设置系统是否第一次运行参数
	 */
	public static void setSystemState(Context context,int state){
		SettingForLikeTools.setIntSysPara(context, ISfIRST_START,state);
	}
	
	
	/*
	 * 获取系统是否第一次初始化地图设置
	 * @return int 1新安装的系统，0非新安装系统
	 */
	public static int getMapSettingState(Context context){
		return SettingForLikeTools.getIntSysPara(context, MAPSETTINGSTATUS, 0);
	}

	/*
	 * 设置系统是否第一次初始化地图设置
	 */
	public static void setMapSettingState(Context context,int state){
		SettingForLikeTools.setIntSysPara(context, MAPSETTINGSTATUS,state);
	}
	
	/*
	 * 获取系统是否第一次初始化导航设置
	 * @return int 1新安装的系统，0非新安装系统
	 */
	public static int getRouteSettingState(Context context){
		return SettingForLikeTools.getIntSysPara(context, ROUTESETTINGSTATUS, 0);
	}

	/*
	 * 设置系统是否第一次初始化导航设置
	 */
	public static void setRouteSettingState(Context context,int state){
		SettingForLikeTools.setIntSysPara(context, ROUTESETTINGSTATUS,state);
	}
	
	/*
	 * 获取系统是否第一次初始化交通雷达设置
	 * @return int 1新安装的系统，0非新安装系统
	 */
	public static int getTrafficSettingState(Context context){
		return SettingForLikeTools.getIntSysPara(context, TRAFFICRADIOSETTING, 0);
	}

	/*
	 * 设置系统是否第一次初始化交通雷达设置
	 */
	public static void setTrafficSettingState(Context context,int state){
		SettingForLikeTools.setIntSysPara(context, TRAFFICRADIOSETTING,state);
	}
	
	
	/*
	 * 系统参数初始化
	 */
	public static void initSystem(Context context){
		SettingForLikeTools.setDayOrNight(context, SettingForLikeTools.getDayOrNight(context));
	
		//初始化自动回车位
		if (getAutoGoBackToCar(context)==1) {
			AUTOGOBACKTOCAR_BOOLEAN=true;
		}else {
			AUTOGOBACKTOCAR_BOOLEAN=false;
		}
		//初始化前方播报
		if (getFrontTrafficRadio(context)==0) {
			FRONTTRAFFICRADIO_BOOLEAN=false;
		}else {
			FRONTTRAFFICRADIO_BOOLEAN=true;
//			FrontTrafficRadio.getInstance().openTrafficRadio();
		}
		//初始化周边播报
		if (getSurroundTrafficRadio(context)==0) {
			SURROUNDTRAFFICRADIO_BOOLEAN=false;
		}else {
			SURROUNDTRAFFICRADIO_BOOLEAN=true;
//			SurroundTrafficRadio.getInstance().openTrafficRadio();
		}
		SettingForLikeTools.setDemoSpeed(SettingForLikeTools.getDemoSpeed(context), context);
		
		RoutePlayOptions options = new RoutePlayOptions();
		RouteAPI.getInstance().getRoutePlayOptions(options);
		options.playRoadName = isTalkDirection(context);
		options.playCamera = isEDogEnable(context);
		RouteAPI.getInstance().setRoutePlayOptions(options);
		
		if(SettingForLikeTools.isTTSOpen(context)){
			TTSAPI.getInstance().setMuteVolumn(false); //不静音
			SettingForLikeTools.setIntSysPara(context, TTSSTATE, 0);
		}else{
			TTSAPI.getInstance().setMuteVolumn(true); //静音
			SettingForLikeTools.setIntSysPara(context, TTSSTATE, 1);
		}
//		SettingForLikeTools.setOverSpeed0(context);
		
		SettingForLikeTools.setRole(getIntSysPara(context, TTSROLE, 0), context);
		setMapColor(getMapColorIndex(context), context);
	}
	/*
	 * 系统退出时保存参数
	 */
	public static void saveSystem(Context context){
		NSLonLat mlonlat=null;
		if(NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI){
			mlonlat = RouteAPI.getInstance().getStartPoint();
		}else{			
			mlonlat = MapAPI.getInstance().getVehiclePos();
		}
		if(mlonlat.x>0){
			SettingForLikeTools.setVehicleX(context, mlonlat.x+"");
			SettingForLikeTools.setVehicleY(context, mlonlat.y+"");
		}
		
		int scale = (int) MapAPI.getInstance().getMapScale();
		SettingForLikeTools.saveMapScale(context, scale);
	}
	
	
	public static void saveMapScale(Context context, int value) {
		setIntSysPara(context, SCALE, value);
	}
	
	public static int getMapScale(Context context) {
		return getIntSysPara(context, SCALE, 16);
	}
	
	/**
	 * 返回系统内所有色盘
     *@return String[] 色盘数组
	 */
	public static String[] getPaletteNames(){
//		if(MapActivity.mapView==null){
//			return null;
//		}
//		MapPaletteManager mpm = MapActivity.mapView.getMap().getMapPaletteManager();
//		int num = mpm.getPaletteNum(MapPaletteManager.PALETTE_MODE_DAY);
//		String []re = new String[num];
//		for(int i = 0;i < num;i++)
//		{
//		re[i] = mpm.getPaletteName(MapPaletteManager.PALETTE_MODE_DAY, i);
//		}
		String[] mapPatternColorName={"爪黄飞电","赤炭火龙"};
		return mapPatternColorName;
	}
	public static void setMapColor(int index,Context context){
//		String color=context.getResources().getStringArray(R.array.mapcoloritem)[index];
		String color = "";
		if(index==0){
			color = Const.MAP_STYLE_TYPE1;
		}else{
			color = Const.MAP_STYLE_TYPE2;
		}
		Log.e(TAG, "=========color======"+color);
		setIntSysPara(context, MAPPALETTE, index);
		MapAPI.getInstance().setMapStyle(color);
	}
	public static int getMapColorIndex(Context context){
		return getIntSysPara(context, MAPPALETTE, 0);
	}

	/**
	 * 是否显示声明  true 显示   false 不显示
	 * */
	public static boolean getDisclaimerStates(Context context) {
		if (getIntSysPara(context, Disclaimer , 1)==1) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 设置显示声明  1 显示   0 不显示
	 * */
	public static void setDisclaimerStates(Context context,int value) {
		setIntSysPara(context, Disclaimer, value);
	}
}