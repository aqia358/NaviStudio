package com.mapabc.android.activity.listener;

import com.mapabc.naviapi.route.GPSRouteInfo;
import com.mapabc.naviapi.route.GpsInfo;

public interface ReceiveInfo {
	public void DoGpsInfo(GpsInfo gpsInfo);
	
	public void DoRouteInfo(GPSRouteInfo routeInfo, boolean gpsNavi);
	
}
