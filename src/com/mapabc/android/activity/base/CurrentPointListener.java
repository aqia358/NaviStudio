package com.mapabc.android.activity.base;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.FavoriteAPI;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.UserEyeAPI;
import com.mapabc.naviapi.favorite.FavoriteInfo;
import com.mapabc.naviapi.map.DayOrNightControl;
import com.mapabc.naviapi.route.AvoidLine;
import com.mapabc.naviapi.route.SearchLineInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.UserEventPot;

/**
 * 当前点按钮事件
 */
public class CurrentPointListener implements OnTouchListener{
	private static final String TAG="CurrentPointListener";
	public static final int STATUS_CAR_AT_CENTER = 0;// 中心点为车位点
	public static final int STATUS_HAS_NO_ROUTE = 1;// 中心点非车位点且无路径
	public static final int STATUS_HAS_ROUTE = 2;// 中心点非车位点有路径
	private ImageButton currentPointBtn ;
	private ImageButton plantRouteBtn;
	private NaviStudioActivity activity;
	public AlertDialog ad;
	private MapView mMapView;
	private ProgressDialog pdg;
	public static NSLonLat endPoint;//静态变量终点，用于向其他页面传值
	private Handler h = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0){
				Log.e(TAG, "reCalculatePath18");
				if(pdg!=null){
					pdg.dismiss();
					pdg = null;
					Log.e(TAG, "reCalculatePath19");
				}
			}
		}
		
	};
	public CurrentPointListener(NaviStudioActivity activity,MapView mapView ){
		this.activity=activity;
		this.currentPointBtn=this.activity.currentPointBtn;
		this.plantRouteBtn=this.activity.plantRouteBtn;
		this.mMapView=mapView;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btn_current_position:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				currentPointBtn.setClickable(false);
				updateCurrentPointPress_Style();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				updateCurrentPoint_Style();
				openAlertDialog();// 点击执行弹出登录对话框
				currentPointBtn.setClickable(true);
			}
			break;
		case R.id.btn_calculate:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				plantRouteBtn.setClickable(false);
				updateCalculateBtnP_Style();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				updateCalculateBtn_Style();
				NSLonLat lonlat = MapAPI.getInstance().getMapCenter();
				endPoint = lonlat;
				Intent naviResultList = new Intent(Constants.ACTIVITY_NAVIRESULTLIST);
				Bundle bundle = new Bundle();
				bundle.putString("type", "map");
				naviResultList.putExtras(bundle);
				activity.startActivity(naviResultList);
				
				//setDestination(lonlat,0);
				plantRouteBtn.setClickable(true);
			}
			break;
		default:
			break;
		}
		return true;
	}
	private void updateCurrentPointPress_Style() {
		if (getCurrentPointStatus() == STATUS_CAR_AT_CENTER) {
			// 中心点为车位点
			if (DayOrNightControl.mdayOrNight) {
				// 白天
				currentPointBtn.setBackgroundResource(R.drawable.cp_day_press);
			} else {
				currentPointBtn
						.setBackgroundResource(R.drawable.cp_night_press);
		}
		} else {
			// 中心点非车位点
			if (DayOrNightControl.mdayOrNight) {
				// 白天
				currentPointBtn
						.setBackgroundResource(R.drawable.cp_extra_day_press);
			} else {
				currentPointBtn
						.setBackgroundResource(R.drawable.cp_extra_night_press);
			}
		}
	}
	private void updateCalculateBtnP_Style() {
		if (MapAPI.getInstance().getDayOrNightMode()==0) {
			// 白天
			plantRouteBtn.setBackgroundResource(R.drawable.extra_day_press);
		} else if(MapAPI.getInstance().getDayOrNightMode()==1){
			plantRouteBtn.setBackgroundResource(R.drawable.extra_night_press);
		}
	}
	public void updateCalculateBtn_Style() {
		if (MapAPI.getInstance().getDayOrNightMode()==0) {
			// 白天
			plantRouteBtn.setBackgroundResource(R.drawable.extra_day_normal);
		} else if(MapAPI.getInstance().getDayOrNightMode()==1) {
			plantRouteBtn.setBackgroundResource(R.drawable.extra_night_normal);
		}
	}
	public void updateCurrentPoint_Style() {
		if (MapAPI.getInstance().isCarInCenter()) {
			// 中心点为车位点
			if (MapAPI.getInstance().getDayOrNightMode()==0) {
				// 白天
				currentPointBtn.setBackgroundResource(R.drawable.cp_day_normal);
			} else {
				currentPointBtn.setBackgroundResource(R.drawable.cp_night_normal);
			}
		} else {
			// 中心点非车位点
			if (MapAPI.getInstance().getDayOrNightMode()==0) {
				// 白天
				currentPointBtn.setBackgroundResource(R.drawable.cp_extra_day_normal);
			} else {
				currentPointBtn.setBackgroundResource(R.drawable.cp_extra_night_normal);
			}
		}
	}
	
	public void setFootView() {
		boolean isMapMoved =!MapAPI.getInstance().isCarInCenter();
		int dayOrNightMode=MapAPI.getInstance().getDayOrNightMode();
		if (isMapMoved) {
			switch (dayOrNightMode) {
			case 0:// day
				currentPointBtn
				.setBackgroundResource(R.drawable.cp_extra_day_normal);
				break;
			case 1://night
				currentPointBtn
				.setBackgroundResource(R.drawable.cp_extra_night_normal);
				break;
			default:
				break;
			}
			plantRouteBtn.setVisibility(View.VISIBLE);
		} else {
			plantRouteBtn.setVisibility(View.GONE);
			switch (dayOrNightMode) {
			case 0:// day
				currentPointBtn.setBackgroundResource(R.drawable.cp_day_normal);
				break;
			case 1://night
				currentPointBtn
				.setBackgroundResource(R.drawable.cp_night_normal);
				break;
			default:
				break;
			}
		}
	}
	public void openAlertDialog() {
		final NSLonLat nsLonLat_mapCenter=MapAPI.getInstance().getMapCenter();
		String titleName=Utils.getRoadName(nsLonLat_mapCenter, 100);
		if(titleName.equals("")){
			titleName="未知道路";
		}
		final int currentpointstatus = getCurrentPointStatus();
		ad = new AlertDialog.Builder(activity).setTitle(titleName)
				.setAdapter(getAdapter(getCurrentPointStatus()),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int position) {
								if (currentpointstatus == STATUS_CAR_AT_CENTER) {
									// 中心点为车位点
								      switch(position){
								      case 0:
								    	  addPOIToFavorites(nsLonLat_mapCenter);
								    	  break;
								      case 1:
								    	  setHomePoint(nsLonLat_mapCenter);
								    	  break;
								      case 2:
								    	  addPOIToUserEye(nsLonLat_mapCenter);
								    	  break;
								      }
								} else if (currentpointstatus == STATUS_HAS_NO_ROUTE) {
									// 中心点非车位点且无路径
									switch(position){
								      case 0:
								    	  setDestination(nsLonLat_mapCenter);
								    	  Intent naviResultList = new Intent(Constants.ACTIVITY_NAVIRESULTLIST);
								    	  Bundle bundle = new Bundle();
									      bundle.putString("type", "map");
										  naviResultList.putExtras(bundle);
										  activity.startActivity(naviResultList);										  activity.startActivity(naviResultList);
								    	 
								    	  break;
								    	  /*
								      case 1:
								    	  setDestination(nsLonLat_mapCenter,1);
								    	  break;
								    	  */
								      case 1:
								    	  setVehiclePosition(nsLonLat_mapCenter);
								    	  break;
								      case 2:
								    	  addPOIToFavorites(nsLonLat_mapCenter);
								    	  break;
								      case 3:
								    	  setHomePoint(nsLonLat_mapCenter);
								    	  break;
								      case 4:
								    	  addPOIToUserEye(nsLonLat_mapCenter);
								    	  break;
								      }
								
								} else if (currentpointstatus == STATUS_HAS_ROUTE) {
									// 中心点非车位点有路径
									switch(position){
								      case 0:
								    	  setDestination(nsLonLat_mapCenter);
								    	  Intent naviResultList = new Intent(Constants.ACTIVITY_NAVIRESULTLIST);
								    	  Bundle bundle = new Bundle();
									      bundle.putString("type", "map");
										  naviResultList.putExtras(bundle);
										  activity.startActivity(naviResultList);										  activity.startActivity(naviResultList);
								    	  //setDestination(nsLonLat_mapCenter,0);
								    	  break;
								      /*  
								      case 1:
								    	  setDestination(nsLonLat_mapCenter,1);
								    	  break;
								    	  */
								      case 1:
								    	  setPassPoint(nsLonLat_mapCenter);
								    	  break;
								      case 2:
								    	  setAvoidLine(nsLonLat_mapCenter);
								    	  break;
								      case 3:
								    	  setVehiclePosition(nsLonLat_mapCenter);
								    	  break;
								      case 4:
								    	  addPOIToFavorites(nsLonLat_mapCenter);
								    	  break;
								      case 5:
								    	  setHomePoint(nsLonLat_mapCenter);
								    	  break;
								      case 6:
								    	  addPOIToUserEye(nsLonLat_mapCenter);
								    	  break;
								    }
								}
							}
						}).show();
	}
	
	public ListAdapter getAdapter(int button_type) {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		if (button_type == STATUS_CAR_AT_CENTER) {
			// 中心点为车位点
			listItem.add(getHashMap(R.drawable.currentpoint_add_to_favorite, R.string.currentpoint_add_to_favorite));
			listItem.add(getHashMap(R.drawable.currentpoint_set_home,R.string.currentpoint_set_home));
			listItem.add(getHashMap(R.drawable.currentpoint_usereye, R.string.currentpoint_usereye));
		} else if (button_type == STATUS_HAS_NO_ROUTE) {
			// 中心点非车位点且无路径
			listItem.add(getHashMap(R.drawable.common_tothis,R.string.currentpoint_tothis));
			//listItem.add(getHashMap(R.drawable.currentpoint_get_net_route, R.string.currentpoint_get_net_route));
			listItem.add(getHashMap(R.drawable.common_fromthis, R.string.currentpoint_fromthis));
			listItem.add(getHashMap(R.drawable.currentpoint_add_to_favorite,  R.string.currentpoint_add_to_favorite));
			listItem.add(getHashMap(R.drawable.currentpoint_set_home, R.string.currentpoint_set_home));
			listItem.add(getHashMap(R.drawable.currentpoint_usereye, R.string.currentpoint_usereye));
		} else if (button_type == STATUS_HAS_ROUTE) {
			// 中心点非车位点有路径
			listItem.add(getHashMap(R.drawable.common_tothis, R.string.currentpoint_tothis));
			//listItem.add(getHashMap(R.drawable.currentpoint_get_net_route, R.string.currentpoint_get_net_route));
			listItem.add(getHashMap(R.drawable.currentpoint_jingguothis, R.string.currentpoint_jingguothis));
			listItem.add(getHashMap(R.drawable.currentpoint_avoidpoint,R.string.currentpoint_avoidpoint));
			listItem.add(getHashMap(R.drawable.common_fromthis,R.string.currentpoint_fromthis));
			listItem.add(getHashMap(R.drawable.currentpoint_add_to_favorite, R.string.currentpoint_add_to_favorite));
			listItem.add(getHashMap(R.drawable.currentpoint_set_home, R.string.currentpoint_set_home));
			listItem.add(getHashMap(R.drawable.currentpoint_usereye,  R.string.currentpoint_usereye));
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(activity, listItem,
				R.layout.currentpoint_list_items, new String[] {
						"ImageManager", "ItemManager" }, new int[] {
						R.id.ImageManager, R.id.ItemManager });
		return listItemAdapter;
	}
	private HashMap<String, Object> getHashMap(int id, int nameId) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ImageManager", id);
		map.put("ItemManager", activity.getString(nameId));
		return map;
	}
	/**
	 * 添加避让点
	 * @param centerinfo 避让点
	 * @return true 成功，false 失败
	 */
	private boolean setAvoidLine(NSLonLat centerinfo){
		try{
		if(!RouteAPI.getInstance().canAddAvoidPoint()){
    		Utils.showTipInfo(activity, R.string.currentpoint_add_avoidpoint_fail);
			return false;
		}
		if(RouteAPI.getInstance().addAvoidPoint(centerinfo)){
			reCalculatePath(1);
			return true;
		}else{
		    Utils.showTipInfo(activity, R.string.currentpoint_addavoidline_failed);
		    return false;
		}
		}catch(Exception ex){
			Log.e(TAG, "ERR",ex);
		}
		return true;
	}
	/*
	 * 设置出发点
	 */
    public boolean setVehiclePosition(NSLonLat centerinfo){
    	NaviControl.getInstance().vehiclePosition=null;
    	NaviControl.getInstance().stopSimNavi();
    	NaviControl.getInstance().stopRealNavi();
    	RouteAPI.getInstance().clearRoute();
    	NaviControl.getInstance().guideEnd();
    	RouteLayer routeLayer = new RouteLayer();
    	routeLayer.deleteLayer();
    	RouteAPI.getInstance().deletePassPoint(-1);
    	RouteAPI.getInstance().deleteAvoidPoint(-1);
    	MapAPI.getInstance().setVehiclePosInfo(centerinfo,MapAPI.getInstance().getVehicleAngle());
    	mMapView.goBackCar();
    	return true;
    }
    /*
     * 设置家
     */
    private boolean setHomePoint(NSLonLat centerinfo){
//    	if(FavoriteAPI.getInstance().delFavorite_ALL(FavoriteConst.FAVORITE_HOME)==1){
    	if(FavoriteAPI.getInstance().delAllFavorites(Const.FAVORITE_HOME)==1){
    		FavoriteInfo favoriteInfo = new FavoriteInfo();
    		favoriteInfo.type=Const.FAVORITE_HOME;
    		Utils.getFavoriteInfo(centerinfo, favoriteInfo);
    		if(favoriteInfo.name.equals("")){
    			Utils.showTipInfo(activity, R.string.roadname_null);
    			return false;
    		}else if(favoriteInfo.longitude<0.0000001f||favoriteInfo.latitude<0.0000001f){
    			Utils.showTipInfo(activity, R.string.lonlat_null);
    			return false;
    		}
    		if(FavoriteAPI.getInstance().addFavorite(favoriteInfo)!=0){
    			Utils.showTipInfo(activity, R.string.sethome);
    		}else{
    			Utils.showTipInfo(activity, R.string.sethomefail);
    		}
    	}
    	return true;
    }
    /*
     * 收藏POI
     */
    private boolean addPOIToFavorites(NSLonLat centerinfo){
    	if(FavoriteAPI.getInstance().getFavoriteCount(Const.FAVORITE_HAUNT)>=200){
    		Utils.showTipInfo(activity, R.string.favorite_full_tip);
    	}else{
    		FavoriteInfo favoriteInfo = new FavoriteInfo();
    		favoriteInfo.type = Const.FAVORITE_HAUNT;
    		if(activity.poiInfo!=null&&activity.poiInfo.lat==centerinfo.y&&centerinfo.x == activity.poiInfo.lon){
    			Utils.getFavoriteInfo(activity.poiInfo, favoriteInfo);
    		}else{
    			Utils.getFavoriteInfo(centerinfo, favoriteInfo);
    		}
    		Log.e(TAG, "当前点收藏："+favoriteInfo.longitude+","+favoriteInfo.latitude);
    		if(FavoriteAPI.getInstance().addFavorite(favoriteInfo)!=0){
    			Utils.showTipInfo(activity, R.string.favorite_successfully);
    		}else{
    			Utils.showTipInfo(activity, R.string.favorite_successfail);
    		}
    	}
    	return true;
    }
    /*
     * 收藏历史目的地
     */
    public boolean addPOIToHistory(NSLonLat centerinfo){
    	Log.e(TAG, "AddPOIToHistory start");
    	if(FavoriteAPI.getInstance().getFavoriteCount(Const.FAVORITE_HISTORYDES)>=200){
    		Utils.showTipInfo(activity, R.string.favorite_full_tip1);
    	}else{
    		FavoriteInfo favoriteInfo = new FavoriteInfo();
    		favoriteInfo.type = Const.FAVORITE_HISTORYDES;
    		Utils.getFavoriteInfo(centerinfo, favoriteInfo);
    		int res = FavoriteAPI.getInstance().addFavorite(favoriteInfo);
    		Log.e(TAG, "RES:"+res);
    	}
    	Log.e(TAG, "AddPOIToHistory end");
    	return true;
    }
    /*
     * 设置目的地
     */
    public boolean setDestination(NSLonLat centerinfo){
    	if(activity.poiInfo!=null&&activity.poiInfo.lat==centerinfo.y&&centerinfo.x == activity.poiInfo.lon){
    		NaviControl.getInstance().end_poiInfo = activity.poiInfo;    		
    	}
    	endPoint = centerinfo;
		//NaviControl.getInstance().calculatePath(centerinfo,type);
    	return true;
    }
    /*
     * 设置途经点
     */
    private boolean setPassPoint(NSLonLat centerinfo){
    	try{
    	if(RouteAPI.getInstance().canAddPassPoint()){
    		NSLonLat[] passpoints = RouteAPI.getInstance().getPassPoints();
    		int count = passpoints.length;
    		for(int i=0;i<count;i++){
    			if(centerinfo.x == passpoints[i].x&&centerinfo.y == passpoints[i].y){
    				Utils.showTipInfo(activity, R.string.currentpoint_addsamplepasspointtip);
    				return false;
    			}
    		}
    		int nType = SettingForLikeTools.getRouteCalcMode(activity);
	    	NaviControl.getInstance().stopSimNavi();
	    	NaviControl.getInstance().stopRealNavi();
	    	RouteAPI.getInstance().clearRoute();
    	    boolean addres = RouteAPI.getInstance().addPassPoint(centerinfo);
    	    if(addres){
    	    	reCalculatePath(0);    	    	
    	    }
    	}else{
    		Utils.showTipInfo(activity, R.string.currentpoint_add_passpoint_fail);
    	}
    	}catch(Exception ex){
    		Log.e(TAG, "setPassPoint", ex);
    	}
    	return true;
    }
    /**
     * 重新计算道路
     * @param type 0为途经点，1为避让点
     */
    private void reCalculatePath(final int type){
    	Log.e(TAG, "reCalculatePath1");
    	NaviControl.getInstance().stopSimNavi();
    	NaviControl.getInstance().stopRealNavi();
    	
		this.pdg = new ProgressDialog(this.activity);
		this.pdg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.pdg.setIndeterminate(false);
		this.pdg.setCancelable(false);
		this.pdg.setMessage(this.activity.getResources().getString(
				R.string.navicontrol_calculate_path));
		this.pdg.show();
		Log.e(TAG, "reCalculatePath2");
		activity.resetStatus();
		Log.e(TAG, "reCalculatePath3");
		NaviStudioActivity.executorService.execute(new Runnable() {
			@Override
			public void run() {
				try{
					Log.e(TAG, "reCalculatePath4");
					RouteAPI.getInstance().setStartPoint(MapAPI.getInstance().getVehiclePos());
					Log.e(TAG, "____start routeCalculation_____");
					int res = 0;
					float angle =MapAPI.getInstance().getVehicleAngle();
					if(angle==0){
						angle=-1;
					}
					Log.e(TAG, "reCalculatePath5");
					if(NaviControl.getInstance().getCalculateType()==0){
                    	Log.e(TAG, "reCalculatePath6_");
                    	res = RouteAPI.getInstance().routeCalculation(angle,
    							SettingForLikeTools.getRouteCalcMode(activity));
                    	Log.e(TAG, "reCalculatePath6");
                	}else{
                		Log.e(TAG, "reCalculatePath7_");

                		res = RouteAPI.getInstance().httpRouteCalculation(SettingForLikeTools.getRouteCalNetMode(activity));
                		Log.e(TAG, "reCalculatePath7");
                	}
                    if(res==1){
                    	Log.e(TAG, "reCalculatePath8");
                    	NaviControl.getInstance().drawRoute();
    					Message msg =  Message.obtain();
    					msg.what = NaviControl.H_FIN_DPATH_SUCCESS;
    					NaviControl.getInstance().mHandler.sendMessage(msg);
    					Log.e(TAG, "reCalculatePath9");
                    }else{
                    	if(type==0){
                    		Log.e(TAG, "reCalculatePath9");
                    	  int count = RouteAPI.getInstance().getPassPointCount();
                    	  RouteAPI.getInstance().deletePassPoint(count-1);
                    	}else{
                    	  int count = RouteAPI.getInstance().getAvoidPointCount();
                    	  RouteAPI.getInstance().deleteAvoidPoint(count-1);
                    	}
                    	Log.e(TAG, "reCalculatePath10");
                    	if(NaviControl.getInstance().getCalculateType()==0){
                        	res = RouteAPI.getInstance().routeCalculation(angle,
        							SettingForLikeTools.getRouteCalcMode(activity));
                    	}else{
                    		res = RouteAPI.getInstance().httpRouteCalculation(SettingForLikeTools.getRouteCalNetMode(activity));
                    	}
                    	Log.e(TAG, "reCalculatePath11");
                    	if(res!=1){
                    		Log.e(TAG, "reCalculatePath12");
                    		RouteLayer routeLayer = new RouteLayer();
                    		RouteAPI.getInstance().deletePassPoint(-1);
            				RouteAPI.getInstance().deleteAvoidPoint(-1);
                    		routeLayer.deleteLayer();
                    		Log.e(TAG, "reCalculatePath13");
                    	}else{  
                    		NaviControl.getInstance().drawRoute();
                    		Log.e(TAG, "reCalculatePath14");
                    	}
    					Message msg = Message.obtain();
    					msg.what = NaviControl.H_FIND_PATH_FAILED2;
    					NaviControl.getInstance().mHandler.sendMessage(msg);
    					Log.e(TAG, "reCalculatePath15");
                    }
				}catch(Exception ex){
					Log.e(TAG, "ERR",ex);
					Log.e(TAG, "reCalculatePath16");
				}
				h.sendEmptyMessage(0);
				Log.e(TAG, "reCalculatePath17");
			}
		});
	}
    /**
     * 添加到电子眼
     */
    private void addPOIToUserEye(NSLonLat centerinfo){
    	String titleName=Utils.getRoadName(centerinfo, 100);
		if(titleName.equals("")){
			titleName=this.activity.getString(R.string.navistudio_road_has_no_name);
		}
    	UserEventPot userEyeInfo = new UserEventPot();
    	userEyeInfo.longitude = centerinfo.x;
    	userEyeInfo.latitude = centerinfo.y;
    	userEyeInfo.symbol = '0';
    	userEyeInfo.type = 1;
    	userEyeInfo.name = titleName;
    	userEyeInfo.angle = (long)MapAPI.getInstance().getVehicleAngle();
    	userEyeInfo.limitSpeed = 40;
    	int id = UserEyeAPI.getInstance().addUserEye(userEyeInfo);
		if(id!=-1){
			Utils.showTipInfo(activity, R.string.addusereye_successfully);
			userEyeInfo.id = id;
			userEyeInfo.time = "2012-09-29 12:12:12";
			boolean b = RouteAPI.getInstance().addUserEyeData(userEyeInfo);
			System.out.println("********"+b);
		}else{
			Utils.showTipInfo(activity, R.string.addusereye_successfail);
		}
    }
    
    /*
     * 收藏历史目的地
     */
    public boolean addPOIToHistory(SearchResultInfo poi){
    	Log.e(TAG, "AddPOIToHistory start");
    	if(FavoriteAPI.getInstance().getFavoriteCount(Const.FAVORITE_HISTORYDES)>=200){
    		Utils.showTipInfo(activity, R.string.favorite_full_tip1);
    	}else{
    		FavoriteInfo favoriteInfo = new FavoriteInfo();
    		favoriteInfo.type = Const.FAVORITE_HISTORYDES;
    		Utils.getFavoriteInfo(poi, favoriteInfo);
    		int res = FavoriteAPI.getInstance().addFavorite(favoriteInfo);
    		Log.e(TAG, "RES:"+res);
    	}
    	Log.e(TAG, "AddPOIToHistory end");
    	return true;
    }
    
    public int getCurrentPointStatus(){
    	int currentpoint_status;
    	if(MapAPI.getInstance().isCarInCenter()){
			currentpoint_status = STATUS_CAR_AT_CENTER;
		}else if(RouteAPI.getInstance().isRouteValid()){
			currentpoint_status = STATUS_HAS_ROUTE;
		}else{
			currentpoint_status = STATUS_HAS_NO_ROUTE;
		}
    	return currentpoint_status;
    }
}
