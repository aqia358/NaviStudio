package com.mapabc.android.activity.listener;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.base.CurrentPointListener;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.listener.MapListener;
import com.mapabc.naviapi.map.DayOrNightControl;
import com.mapabc.naviapi.route.RouteSegInfo;
import com.mapabc.naviapi.type.Const;

/**
 * desciption:
 * 
 */
public class MyMapListener implements MapListener {

	private NaviStudioActivity naviStudioActivity;
	private BackCarTimer timer;
	public long currentTime;// 当前时间
	public long originalTime;// 开始时间
	public static int BACKCARDELAYTIME = 20000;// 回车位持续时间
	public static boolean CARNOTINCENTER=true;//车位在地图中心点
	private static final int AUTO_HIDE = 100;// 定时自动隐藏
	public boolean isStart = true;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AUTO_HIDE: // 自动回车位
				naviStudioActivity.carBackEvent.goBackCar();
				break;
			}
		};
	};

	/**
	 * @param naviStudioActivity
	 */
	public MyMapListener(NaviStudioActivity naviStudioActivity) {
		super();
		this.naviStudioActivity = naviStudioActivity;
	}

	@Override
	public void onMapStatusChanged(int paramInt) {
		// TODO Auto-generated method stub
		switch (paramInt) {
		case MapListener.MAP_STATUS_SCALE:
			naviStudioActivity.setScaleText();
			naviStudioActivity.zoomControl.initZoom();
			break;
		case MapListener.MAP_STATUS_MOVE:
			CARNOTINCENTER=false;
			if (naviStudioActivity.carBackEvent != null) {
				naviStudioActivity.carBackEvent.setButtonVisible();
			}
			naviStudioActivity.setMapCenterRoadName();
			if (SettingForLikeTools.AUTOGOBACKTOCAR_BOOLEAN) {
				resetTime();
				timer.setStart(true);
			}
			if (RouteAPI.getInstance().isRouteValid()) {
				NaviControl.getInstance().isShowNaviInfo=false;
				NaviControl.getInstance().hideLineInfo();
				if (NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI) {
					RouteAPI.getInstance().pauseOrResumeSimNavi(true);
				}
				NaviControl.getInstance().guideEnd();
			}
			naviStudioActivity.currentPointListener.setFootView();
			break;
		case MapListener.MAP_STATUS_CAR:
			CARNOTINCENTER=true;
			NaviControl.getInstance().isShowNaviInfo=true;
			naviStudioActivity.carBackEvent.setButtonGone();
			naviStudioActivity.currentPointListener.setFootView();
			if (RouteAPI.getInstance().isRouteValid()) {
				if (NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI) {
					RouteAPI.getInstance().pauseOrResumeSimNavi(false);
				}
				if(NaviControl.getInstance().routeInfo!=null){
					NaviControl.getInstance().setRoadName(NaviControl.getInstance().routeInfo.nextRoadName);
				}
			    NaviControl.getInstance().showNaviInfo();
			}else{				
				naviStudioActivity.setMapCenterRoadName();
			}
			if(!naviStudioActivity.blEnableSearch){
				naviStudioActivity.resetStatus();
				naviStudioActivity.blEnableSearch = true;
				naviStudioActivity.updateUIStyle(DayOrNightControl.mdayOrNight);
				if(naviStudioActivity.mapView.isHasTip()){
					naviStudioActivity.mapView.hideTip();
				}
			}
			break;
		case MapListener.MAP_STATUS_ANGLE:
			if(MapAPI.getInstance().getMapView()==Const.MAP_VIEWSTATE_VEHICLE){
				float angle = MapAPI.getInstance().getMapAngle();
				naviStudioActivity.mapModelControl.updateMapModel(360-angle);				
			}
			break;
		case MapListener.MAP_STATUS_SIZE:
			if(MapAPI.getInstance().getMapView()==Const.MAP_VIEWSTATE_3D){
				NaviControl.getInstance().setHotPoint();
			}
			break;
		default:
			break;
		}
	}

	public void resetTime() {
		originalTime = System.currentTimeMillis();// 计时开始
		if (timer != null) {
			if (timer.isAlive()) {

			} else {
				timer = null;
				timer = new BackCarTimer("Timer");
				timer.start();
			}
		} else {
			timer = new BackCarTimer("Timer");
			timer.start();
		}
	}

	class BackCarTimer extends Thread {
		
		public String name;

		public BackCarTimer(String name) {
			this.name = name;
		}

		public void run() {
			while (isStart) {
				currentTime = System.currentTimeMillis();
				if (currentTime - originalTime > BACKCARDELAYTIME) {
					Message msg = Message.obtain();
					msg.what = AUTO_HIDE;
					handler.sendMessage(msg);
					originalTime = System.currentTimeMillis();
					isStart = false;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public boolean isStart() {
			return isStart;
		}

		public void setStart(boolean start) {
			isStart = start;
		}

	}

}
