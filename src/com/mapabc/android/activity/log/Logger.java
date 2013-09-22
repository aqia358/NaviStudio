package com.mapabc.android.activity.log;

import com.mapabc.android.activity.utils.DateTimeUtil;
import com.mapabc.general.function.file.FileManager;
import com.mapabc.naviapi.utils.SysParameterManager;

import android.os.Environment;
import android.util.Log;

/**
 * 一个具有开关的日志工具 代替系统的Log类  开发阶段LOGLEVEL设置为6,发布时设置为0
 * @author: zhuhao 2012-08-09
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class Logger {
	public static boolean ISSAVELOG = true;
	static String logPath = SysParameterManager.getBasePath()+"/MapABC/log/mylog.log";

//	public static void v(String tag,String msg){
//		if(LOGLEVEL>VERBOSE)
//		Log.v(tag, msg);
//	}
//	public static void d(String tag,String msg){
//		if(LOGLEVEL>DEBUG)
//		Log.d(tag, msg);
//	}
//	public static void i(String tag,String msg){
//		if(LOGLEVEL>INFO)
//		Log.i(tag, msg);
//	}
//	public static void w(String tag,String msg){
//		if(LOGLEVEL>WARN)
//		Log.w(tag, msg);
//	}

	public static void e(String tag,String msg){
		if(ISSAVELOG){
			String systime = com.mapabc.android.activity.utils.DateTimeUtil
			.dateTimeToStr(System.currentTimeMillis());
			String data = systime + "  " + tag + "  " + msg+"\r\n";
			try {
				FileManager.writeFileData(logPath, data, true);
			} catch (Exception ex) {
				
			}
		}
	}
	/*
	 * GPS日志
	 */
	
}
