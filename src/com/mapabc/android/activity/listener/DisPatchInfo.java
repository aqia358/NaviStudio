package com.mapabc.android.activity.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.route.GPSRouteInfo;
import com.mapabc.naviapi.route.GpsInfo;

import android.util.Log;
import android.widget.ListView;

public class DisPatchInfo {
	
	private static DisPatchInfo disPatchInfo;
	private Map<String, ReceiveInfo> gpsInfoMap = new HashMap<String, ReceiveInfo>();
	private Map<String, ReceiveInfo> routeInfoMap = new HashMap<String, ReceiveInfo>();
	
	private DisPatchInfo(){}
	
	public static DisPatchInfo getInstance(){
		if(disPatchInfo==null){
			disPatchInfo =new DisPatchInfo();
		}
		return disPatchInfo;
	}
	
	
	public void addGpsInfoListener(String name, ReceiveInfo receiveGpsInfo) {
		gpsInfoMap.put(name, receiveGpsInfo);
	}

	public void removeGpsInfoListener(String name) {
		gpsInfoMap.remove(name);
	}

	public void addRouteInfoListener(String name, ReceiveInfo receiveGpsInfo) {
		routeInfoMap.put(name, receiveGpsInfo);
	}

	public void removeRouteInfoListener(String name) {
		routeInfoMap.remove(name);
	}
	
	
	public void disPatchGpsInfo(GpsInfo gpsInfo) {
		Set<String> keys = gpsInfoMap.keySet();
		for (String name : keys) {
			ReceiveInfo receiveGpsInfo = gpsInfoMap.get(name);
			receiveGpsInfo.DoGpsInfo(gpsInfo);
		}

	}

	public void disPatchRouteInfo(GPSRouteInfo routeInfo, boolean gpsNavi) {
		Set<String> keys = routeInfoMap.keySet();
		for (String name : keys) {
			ReceiveInfo receiveRouteInfo = routeInfoMap.get(name);
			receiveRouteInfo.DoRouteInfo(routeInfo, gpsNavi);
		}

	}
	
}
