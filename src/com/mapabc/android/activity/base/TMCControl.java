package com.mapabc.android.activity.base;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.TMCAPI;
/**
 * 实时交通管理类
 */
public class TMCControl extends Handler{
	
	public static int TMC_STATUS=0;
	public static final int TMC_CLOSED=0;
	public static final int TMC_OPENED=1;
	private static TMCControl mTmcControl;
	private int mUpdateTime=5*1000;//刷新时间
	private MapView mMapView;
	private TMCControl(MapView mapView){
		this.mMapView=mapView;
	}
	public static TMCControl getInstance(MapView mapView){
		if(mTmcControl==null){
			mTmcControl =new TMCControl(mapView);
		}
		return mTmcControl;
	}
	
	public void startTMC(){
		TMC_STATUS = TMC_OPENED;
		TMCAPI.getInstance().setUpdateTime(180000);
		TMCAPI.getInstance().startUpdate();
		Message mess =Message.obtain();
		sendMessageDelayed(mess,0);
		mMapView.postInvalidate();
	}
	public void handleMessage(Message message)
    {
		if(TMC_STATUS == TMC_OPENED)
		{
		try{
			int  haveNewTraffic=TMCAPI.getInstance().isNewDataAvalable();
			Log.e("TMCControl", "=====havenewTraffic======"+haveNewTraffic);
			if(haveNewTraffic>0){
				mMapView.postInvalidate();
			}
		  Message mess =Message.obtain();
		  sendMessageDelayed(mess, mUpdateTime);
		}catch(Exception ex){
			Log.e("TMCControl", "error",ex);
		}
		}
    }
	public void stopTMC(){
		TMC_STATUS=TMC_CLOSED;
		new Thread(){
			public void run(){
				TMCAPI.getInstance().stopUpdate();
			}
		}.start();
		Message mess =Message.obtain();
		sendMessageDelayed(mess, 0);
		mMapView.postInvalidate();
	}
}
