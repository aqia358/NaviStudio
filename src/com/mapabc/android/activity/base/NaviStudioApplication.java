/**
 * 
 */
package com.mapabc.android.activity.base;

import com.mapabc.naviapi.type.Const;

import android.app.Application;

/**
 * desciption:
 * 
 */
public class NaviStudioApplication extends Application {

	public boolean isRecordTrack=true;
	public boolean isTracePlay=false;
	public int TRACE_PLAY_MODE=Const.TRACE_PLAY_STOP;
	
}
