package com.mapabc.android.activity.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import com.mapabc.android.activity.log.Logger;

import android.content.Context;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {

	private Context context;
	private UncaughtExceptionHandler defUncaughtExcep;
	private static CrashHandler crashHandler;
	private CrashHandler(){}
	
	public static CrashHandler newInstance(){
		if(crashHandler == null){
			synchronized (CrashHandler.class) {
				if(crashHandler == null){
					crashHandler = new CrashHandler();
				}
			}
		}
		return crashHandler;
	}
	
	public void init(Context context){
		this.context = context;
		defUncaughtExcep = Thread.currentThread().getDefaultUncaughtExceptionHandler();
		Thread.currentThread().setDefaultUncaughtExceptionHandler(this);
//		Thread.currentThread().setUncaughtExceptionHandler(this);
	}
	
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
		
		/*if(defUncaughtExcep != null){
			defUncaughtExcep.uncaughtException(thread, ex);
		}*/
		ActivityStack.newInstance().finishAll();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	
	private boolean handleException(Throwable ex){
		boolean blHanlder = false;
		if(ex == null){
			return blHanlder;
		}
		StackTraceElement[] stackTraceElements = ex.getStackTrace();
		int size = stackTraceElements.length;
//		Logger.e("Exception", ex.getMessage());
		for(int s = 0; s<size;s++){
			StackTraceElement element = stackTraceElements[s];
			Log.e("Exception", element.toString());
		}
		
		return blHanlder;
	}

}
