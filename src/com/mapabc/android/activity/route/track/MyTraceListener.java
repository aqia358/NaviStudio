package com.mapabc.android.activity.route.track;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.NaviStudioApplication;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.listener.TraceListener;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.Overlay;

public class MyTraceListener implements TraceListener{
	private static final String TAG="MyTraceListener";
	NSLonLat vehicleLonLat=null;
	private Context context;
	public static int TRACE_PLAY_MODE=-1;
	private long count=1420;
//	private boolean showTrace=false;
	
	
	public MyTraceListener(Context context){
		this.context=context;
	}
	public Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				AutoNaviMap.getInstance(context).getMapView().invalidate();
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	public void onTracePlayMode(int status) {
		switch (status) {
		case Const.TRACE_PLAY_START:
			TRACE_PLAY_MODE=Const.TRACE_PLAY_START;
			
			break;
		case Const.TRACE_PLAY_STOP:
			TRACE_PLAY_MODE=Const.TRACE_PLAY_STOP;
			
			break;
		case Const.TRACE_PLAY_PAUSE:
			TRACE_PLAY_MODE=Const.TRACE_PLAY_PAUSE;
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onTracePlayPoint(float longitude, float latitude,
			int direction, String time, int percent) {
//		Log.e(TAG, "======longitude======="+longitude);
//		Log.e(TAG, "======latitude======="+latitude);
		vehicleLonLat=new NSLonLat(longitude, latitude);
		if (MapAPI.getInstance().getMapView() == Const.MAP_VIEWSTATE_NORTH) {
			if (MapAPI.getInstance().isCarInCenter()) {
				MapAPI.getInstance().setVehiclePosInfo(vehicleLonLat,
						direction);
				MapAPI.getInstance().setMapCenter(vehicleLonLat);
			} else {
				MapAPI.getInstance().setVehiclePosInfo(vehicleLonLat,
						direction);
				MapAPI.getInstance().setMapCenter(vehicleLonLat);
			}
		} else {
			MapAPI.getInstance().setMapAngle(360 - direction);
			if (MapAPI.getInstance().isCarInCenter()) {
				MapAPI.getInstance().setVehiclePosInfo(vehicleLonLat,
						0);
				MapAPI.getInstance().setMapCenter(vehicleLonLat);
			} else {
				MapAPI.getInstance().setMapCenter(vehicleLonLat);
				MapAPI.getInstance().setVehiclePosInfo(vehicleLonLat,
						0);
			}
		}
		
//		MapAPI.getInstance().setVehiclePosInfo(vehicleLonLat,
//				direction);
		Log.e(TAG, "==play:direction======="+direction);
//		MapAPI.getInstance().setMapCenter(vehicleLonLat);
		mHandler.sendEmptyMessage(1000);
	}

	@Override
	public void onTraceSavePoint(float longitude, float latitude,
			int direction, String time) {
		
		int res= SettingForTrackTools.getTrackSetting(context);
		if (res==0) {//在地图上显示轨迹点
			count++;
			Log.e(TAG, "==save:direction======="+direction);
			Overlay overlay = new Overlay();
			overlay.id = count;
			overlay.type = Const.MAP_OVERLAY_POI;
			overlay.hide = false;
			overlay.lons = new float[]{longitude};
			overlay.lats = new float[]{latitude};
			overlay.labelText = "";
			overlay.painterName = "";
			overlay.labelName = "trace_point";		
			MapAPI.getInstance().addOverlay(Constants.TRACE_POINT, overlay);
			
		}
		
	}

}
