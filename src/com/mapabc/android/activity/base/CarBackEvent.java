package com.mapabc.android.activity.base;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.mapabc.android.activity.NaviResultListActivity;
import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
/**
 * @description: 回车位按钮事件管理
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class CarBackEvent {
	private NaviStudioActivity naviStudioActivity;
	private MapView mapView;
	OnClickListener onClickListener= new OnClickListener() {
		public void onClick(View v) {
			if(!naviStudioActivity.blEnableSearch){
				
				naviStudioActivity.onBackPressed();
				return ;
			}
			if (naviStudioActivity.back_car.equals(v)) {
				goBackCar();
			}
		}
	};
	
	public  CarBackEvent(NaviStudioActivity activity,MapView mapView){
		  this.naviStudioActivity = activity;
		  this.mapView=mapView;
		  naviStudioActivity.back_car.setOnClickListener(onClickListener);
		  init();
	}

	public void goBackCar() {
		mapView.goBackCar();
	}


	public void init(){
		  boolean inCenter = MapAPI.getInstance().isCarInCenter();
		  if(inCenter){
			  naviStudioActivity.back_car.setVisibility(View.GONE);
		  }else{
			  naviStudioActivity.back_car.setVisibility(View.VISIBLE);
		  }
	}
	public void setButtonVisible(){
		if(naviStudioActivity.back_car.getVisibility()==View.GONE){
			naviStudioActivity.back_car.startAnimation(CarBackEvent.this.naviStudioActivity.mShowAction);
		    naviStudioActivity.back_car.setVisibility(View.VISIBLE);
		}
	}
	public void setButtonGone(){
		if(naviStudioActivity.back_car.getVisibility()==View.VISIBLE){
			naviStudioActivity.back_car.startAnimation(CarBackEvent.this.naviStudioActivity.mHiddenAction);
		    naviStudioActivity.back_car.setVisibility(View.GONE);
		}
	}

}
