package com.mapabc.android.activity.base;

import com.mapabc.naviapi.MapView;

import android.content.Context;
import android.util.Log;
/**
 * @description: MapView管理，单例化
 * @author: changbao.wang 2011-09-08
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class AutoNaviMap {
	private static final String TAG="AutoNaviMap";
	private static AutoNaviMap mAutoNaviMap;
	private MapView mapView;
	public Context mContext;
	private AutoNaviMap(Context activity) {
//		this.mContext=activity.getApplicationContext();
		this.mContext = activity;
		mapView = new MapView(activity);
	}

	public static AutoNaviMap getInstance(Context context){
		if(mAutoNaviMap==null){
			Log.e(TAG, "new AutoNaviMap()");
			mAutoNaviMap=new AutoNaviMap(context);
			return mAutoNaviMap;
		}
		return mAutoNaviMap;
	}
	public static void clearThis(){
		mAutoNaviMap=null;
	}
	public MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}
}
