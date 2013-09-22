/**
 * 
 */
package com.mapabc.android.activity.route.track;

import com.mapabc.naviapi.type.Const;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * desciption:
 * 
 */
public class SettingForTrackTools {

	private static final String SOFTINSHAREDNAME="MapabcAutoNavi";//在SharedPreferences里的名称
	public static final String TRACKSETTING="tracksetting";//轨迹显示
	public static final String TRACKRULESETTING="trackrulesetting";//记录密度设置
	public static String SPEEDVALUE="speedvalue";//轨迹播放速度值
	
	public static final int LOW_SPEED=1;//低速
	public static final int MID_SPEED=2;//中速
	public static final int HIGH_SPEED=3;//高速

	
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
	//获取轨迹显示设置
	public static int getTrackSetting(Context context){
		return getIntSysPara(context, TRACKSETTING, 1);
	
	}
	//设置轨迹显示
	public static void setTrackSetting(Context context, int value ){
		setIntSysPara(context, TRACKSETTING, value);
	}
	//获取记录密度设置
	public static int getTrackRule(Context context) {
		return getIntSysPara(context, TRACKRULESETTING, 0);
	}
	//设置记录密度
	public static void setTrackRule(Context context, int value ) {
		setIntSysPara(context, TRACKRULESETTING, value);
	}
	/**
	 * 设置轨迹播放的速度值
	 * 1-低速；2-中速；3-高速
	 */
	public static void setSpeedValue(Context context,int value){
		setIntSysPara(context, SPEEDVALUE, value);
	}
	/**
	 * 获取轨迹播放的速度值
	 */
	public static int getSpeedValue(Context context){
		return getIntSysPara(context, SPEEDVALUE, 1);
	}
}
