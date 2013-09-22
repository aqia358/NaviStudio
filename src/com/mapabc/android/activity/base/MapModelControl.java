package com.mapabc.android.activity.base;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.type.Const;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
/**
 * 地图模式切换控制类
 */
public class MapModelControl implements OnTouchListener{
	private static String TAG="MapModelControl";
	NaviStudioActivity m_activity;
	MapView m_MapView = null;
	private ImageButton ibMapModel;
	
	public MapModelControl(NaviStudioActivity activity, MapView vMapView){
		this.m_activity = activity;
		ibMapModel = this.m_activity.ibMapModel;
        m_MapView = vMapView;
        setMapMode();
	}
	/**
	 * 点击地图模式切换按钮响应方法
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(SettingForLikeTools.getMapModel(m_activity)==Const.MAP_VIEWSTATE_3D){
				ibMapModel.setBackgroundResource(R.drawable.navistudio_view_3d1);
			}else{
				ibMapModel.setBackgroundResource(R.drawable.navistudio_view_north1);
			}
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			updateMapMode();
			m_MapView.invalidate();
		}
		return true;
	}
	/**
	 * 更新地图模式，并保存当前模式
	 */
	private void updateMapMode(){
		if(SettingForLikeTools.getMapModel(m_activity)==Const.MAP_VIEWSTATE_NORTH){
			//设置为图随车转
			SettingForLikeTools.setMapModel(Const.MAP_VIEWSTATE_VEHICLE, m_activity);
			ibMapModel.setBackgroundResource(R.drawable.navistudio_view_car_north_01);
			float carAngle = MapAPI.getInstance().getVehicleAngle();
			
			MapAPI.getInstance().setMapAngle(360-carAngle);
			MapAPI.getInstance().setVehiclePosInfo(MapAPI.getInstance().getVehiclePos(),0);
		}else if(SettingForLikeTools.getMapModel(m_activity)==Const.MAP_VIEWSTATE_VEHICLE){
			//设置为3D模式
			float mapScale = MapAPI.getInstance().getMapScale();
			float newMapScale = mapScale;
			if(mapScale>MapView.M_3DMAPSCALEMAX){
				newMapScale = MapView.M_3DMAPSCALEMAX;
			}else if(mapScale < MapView.M_3DMAPSCALEMIN){
				newMapScale = MapView.M_3DMAPSCALEMIN;
			}
			if(newMapScale!=mapScale){
				MapAPI.getInstance().setMapScale(newMapScale);
			}
			SettingForLikeTools.setMapModel(Const.MAP_VIEWSTATE_3D, m_activity);
			//修改按钮状态
			m_activity.zoomControl.initZoom();
			ibMapModel.setBackgroundResource(R.drawable.navistudio_view_3d);
		}else{
			//设置为正北向上
			float mapScale = MapAPI.getInstance().getMapAngle();
			SettingForLikeTools.setMapModel(Const.MAP_VIEWSTATE_NORTH, m_activity);
			float newMapScale = 360-mapScale;
			MapAPI.getInstance().setVehiclePosInfo(MapAPI.getInstance().getVehiclePos(), newMapScale);
			//修改按钮状态
			m_activity.zoomControl.initZoom();
			ibMapModel.setBackgroundResource(R.drawable.navistudio_view_north);
		}	
		NaviControl.getInstance().setNormalPoint();
	}
	/**
	 * 程序启动时，初始化模式按钮，初始化底层地图模式
	 */
	public void setMapMode(){
		if(SettingForLikeTools.getMapModel(m_activity)==Const.MAP_VIEWSTATE_VEHICLE){
			//图随车转
			MapAPI.getInstance().setMapView(Const.MAP_VIEWSTATE_VEHICLE);
			float angle = MapAPI.getInstance().getMapAngle();
			updateMapModel(360-angle);
		}else if(SettingForLikeTools.getMapModel(m_activity)==Const.MAP_VIEWSTATE_3D){
			MapAPI.getInstance().setMapView(Const.MAP_VIEWSTATE_3D);
			ibMapModel.setBackgroundResource(R.drawable.navistudio_view_3d);
		}else{
			MapAPI.getInstance().setMapView(Const.MAP_VIEWSTATE_NORTH);
			ibMapModel.setBackgroundResource(R.drawable.navistudio_view_north);
		}
		NaviControl.getInstance().setNormalPoint();
	}
	/*
	 * 当前地图模式为车首朝上时，指北针需改变
	 */
	public void updateMapModel(float angle) {
		if (SettingForLikeTools.getMapModel(m_activity) == 1) {
			int n = (int) (angle / 22.5);
			float angle_n =(float)(22.5*n);
			float angle_n1 = (float)(22.5*(n+1));
			if((angle_n1-angle)<(angle-angle_n)){
				n+=1;
			}
			Log.e(TAG, "angle:"+angle);
			Log.e(TAG, "map_angle _n:"+n);
			switch (n) {
			case 0:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_01);
				break;
			case 1:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_02);
				break;
			case 2:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_03);
				break;
			case 3:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_04);
				break;
			case 4:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_05);
				break;
			case 5:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_06);
				break;
			case 6:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_07);
				break;
			case 7:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_08);
				break;
			case 8:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_09);
				break;
			case 9:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_10);
				break;
			case 10:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_11);
				break;
			case 11:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_12);
				break;
			case 12:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_13);
				break;
			case 13:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_14);
				break;
			case 14:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_15);
				break;
			case 15:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_16);
			case 16:
				ibMapModel
						.setBackgroundResource(R.drawable.navistudio_view_car_north_01);
				break;
			}
		}
	}
}
