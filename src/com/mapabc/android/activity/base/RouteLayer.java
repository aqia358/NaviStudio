package com.mapabc.android.activity.base;

import android.util.Log;

import com.mapabc.android.activity.log.Logger;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.Overlay;

public class RouteLayer {
	private static final int ROUTE_LAY = 10;// 路径所在图层ID
	public static final String PAINTER_ROUTE_LINE="Route_line";//规划路径样式
	public static final String PAINTER_PASS_LINE="Pass_Line";
	public static final int POS_SEG_OVERLAY_INDEX=1199;
	public static final int ROUTE_POINT_PASS1=102;//途经点1
	public static final int ROUTE_POINT_PASS2=103;//途经点2
	public static final int ROUTE_POINT_PASS3=104;//途经点3
	public static final int ROUTE_POINT_PASS4=105;//途经点4
	public static final int ROUTE_POINT_AVOID=106;//避让点10006-10009
	public static final int ROUTE_POINT_START=110;//起点
	public static final int ROUTE_POINT_END=111;//终点
	public static final int ELE_EYE=119;//电子眼
	public static final int ROUTE_POINT_CAMERA=11;//摄像头
	public static final int ROUTE_POINT_CAMERA_RED=12;//警示摄像头
	private static final String LABLE_POINT_START="route_start";//起点图标
	private static final String LABLE_POINT_PASS="route_via";//途经点图标
	private static final String LABLE_POINT_END="route_end";//终点图标
	private static final String LABLE_POINT_AVOID="route_avoid";//避让点图标
	private static final String LABLE_POINT_CAMERA="route_camera";//摄像头图标
	private static final String LABLE_POINT_CAMERA_RED="route_camera_red";//警示摄像头图标


	public boolean addRoute(float [] lons, float [] lats ,int overlayID,String painterName){
		Overlay overlay = new Overlay();
		overlay.id = overlayID;
		overlay.type = Const.MAP_OVERLAY_LINE;
		overlay.hide = false;
		overlay.lons = lons;
		overlay.lats = lats;
		overlay.labelText = "";
		overlay.painterName = painterName;
		overlay.labelName = ""+overlayID;
		boolean addres = MapAPI.getInstance().addOverlay(ROUTE_LAY, overlay);
		return addres;
	}
	public boolean deleteOverlay(int overlayID){
		return MapAPI.getInstance().delOverlay(ROUTE_LAY, overlayID, Const.MAP_OVERLAY_LINE);
	}
	public boolean deleteLayer(){
		return MapAPI.getInstance().delLayer(ROUTE_LAY);
	}
	public boolean unShowLayer(){
		return MapAPI.getInstance().updateOverlays(ROUTE_LAY, false);
	}
	public boolean ShowLayer(){
		return MapAPI.getInstance().updateOverlays(ROUTE_LAY, true);
	}
	public boolean addCurSegOverlay(float [] lons , float [] lats,String painterName){
		Overlay overlay = new Overlay();
		overlay.id = POS_SEG_OVERLAY_INDEX;
		overlay.type = Const.MAP_OVERLAY_LINE;
		overlay.hide = false;
		overlay.lons = lons;
		overlay.lats = lats;
		overlay.labelText = "";
		overlay.painterName = painterName;
		overlay.labelName = ""+POS_SEG_OVERLAY_INDEX;
		boolean addres = MapAPI.getInstance().addOverlay(ROUTE_LAY, overlay);
		return addres;
	}
	public boolean deleteCurSegOverlay(){
		return MapAPI.getInstance().delOverlay(ROUTE_LAY, POS_SEG_OVERLAY_INDEX, Const.MAP_OVERLAY_LINE);
	}
	/**
	 * 设置起点、途经点、终点
	 * 
	 * */
	public boolean addRoutePos(NSLonLat pos, int type) {
		Overlay overlay = new Overlay();
		overlay.id = type;
		overlay.type = Const.MAP_OVERLAY_POI;
		overlay.hide = false;
		overlay.lons = new float[]{pos.x};
		overlay.lats = new float[]{pos.y};
		overlay.labelText = "";
		overlay.painterName = "";
		overlay.labelName = getLabelName(type);		
		boolean addret = MapAPI.getInstance().addOverlay(ROUTE_LAY, overlay);
		return addret;
	}
	
	/**
	 * 添加自定义图标
	 * 
	 * */
	public boolean addCustomWidget(NSLonLat pos, int id,int type) {
		Overlay overlay = new Overlay();
		overlay.id = id;
		overlay.type = Const.MAP_OVERLAY_POI;
		overlay.hide = false;
		overlay.lons = new float[]{pos.x};
		overlay.lats = new float[]{pos.y};
		overlay.labelText = "";
		overlay.painterName = "";
		overlay.labelName = getCustomWidgetsName(type);		
		boolean addret = MapAPI.getInstance().addOverlay(ROUTE_LAY, overlay);
		return addret;
	}
		
	public boolean addEleEye(NSLonLat pos, int type) {
		Overlay overlay = new Overlay();
		overlay.id = type;
		overlay.type = Const.MAP_OVERLAY_POI;
		overlay.hide = false;
		overlay.lons = new float[]{pos.x};
		overlay.lats = new float[]{pos.y};
		overlay.labelText = "";
		overlay.painterName = "";
		overlay.labelName = "elec_eye";		
		boolean addret = MapAPI.getInstance().addOverlay(ROUTE_LAY, overlay);
		return addret;
	}
	public boolean deleteRoutePoint(int type){
		return MapAPI.getInstance().delOverlay(ROUTE_LAY,type,Const.MAP_OVERLAY_POI);
	}
	public boolean deleteEleEyePoint(int type){
		return MapAPI.getInstance().delOverlay(ROUTE_LAY,type,Const.MAP_OVERLAY_POI);
	}
	private String getCustomWidgetsName(int type){
		switch(type){
		case ROUTE_POINT_CAMERA:
		return LABLE_POINT_CAMERA;
		case ROUTE_POINT_CAMERA_RED:
		return LABLE_POINT_CAMERA_RED;
		default :
		return LABLE_POINT_CAMERA;
		}
	}
	
	private String getLabelName(int type){
		switch(type){
		case ROUTE_POINT_START:
			return LABLE_POINT_START;
		case ROUTE_POINT_END:
			return LABLE_POINT_END;
		case ROUTE_POINT_AVOID:
			return LABLE_POINT_AVOID;
		case ROUTE_POINT_PASS1:
			return LABLE_POINT_PASS;
		case ROUTE_POINT_PASS2:
			return LABLE_POINT_PASS;
		case ROUTE_POINT_PASS3:
			return LABLE_POINT_PASS;
		case ROUTE_POINT_PASS4:
			return LABLE_POINT_PASS;
		default :
			return LABLE_POINT_AVOID;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
