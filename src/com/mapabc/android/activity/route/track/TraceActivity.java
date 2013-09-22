package com.mapabc.android.activity.route.track;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.CarBackEvent;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.NaviStudioApplication;
import com.mapabc.android.activity.base.RouteLayer;
import com.mapabc.android.activity.base.VolumeControl;
import com.mapabc.android.activity.base.ZoomControl;
import com.mapabc.android.activity.listener.MyMapListener;
import com.mapabc.android.activity.utils.UIResourceUtil;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.listener.DayOrNightListener;
import com.mapabc.naviapi.listener.MapListener;
import com.mapabc.naviapi.map.DayOrNightControl;

public class TraceActivity extends Activity{
	public MapView mapView;
	public ZoomControl zoomControl;
	public VolumeControl volumeControl;
	private TextView scaleTextView;
	public ImageButton zoomin, zoomout,back_car;
	public ImageButton speedBtn;
	public NaviStudioApplication myapp;
	RouteLayer routeLayer = new RouteLayer();
	private MapListener mapListener = new TraceMapListener(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMapView();
		
		routeLayer.unShowLayer();
//		MapAPI.getInstance().setPrewMode(true);
		boolean inCenter = MapAPI.getInstance().isCarInCenter();
		  if(inCenter){
			  setButtonGone();
		  }else{
			  setButtonVisible();
	   }
	}
	@Override
	protected void onResume() {
		updateUIStyle(DayOrNightControl.mdayOrNight);
//		MapAPI.getInstance().setPrewMode(true);
		super.onResume();
	}
	/**
	 * 初始化layout
	 */
	private void initMapView() {
		if (Utils.isLand(this)) {
			setContentView(R.layout.trace_preview_map_land_layout);
		} else {
			setContentView(R.layout.trace_preview_map_port_layout);
		}

		FrameLayout flo = (FrameLayout) findViewById(R.id.fl_mapview);
		View mView = (View) findViewById(R.id.v_mapsview);
		int nIndex = flo.indexOfChild(mView);
		flo.removeView(mView);
		mapView = AutoNaviMap.getInstance(this).getMapView();
		ViewParent parent = mapView.getParent();
		if (parent != null && (parent instanceof FrameLayout)) {
			((FrameLayout) parent).removeView(mapView);
		}
		flo.addView(mapView, nIndex, mView.getLayoutParams());
		zoomin=(ImageButton)findViewById(R.id.ib_btnZoomIn_trace);
		zoomout=(ImageButton)findViewById(R.id.ib_btnZoomOut_trace);
		back_car = (ImageButton) findViewById(R.id.ib_btn_car_trace);
		speedBtn=(ImageButton)findViewById(R.id.trace_ib_map_model);
		scaleTextView=(TextView)findViewById(R.id.tv_scale_num_trace);
		addListener();
		myapp = (NaviStudioApplication) getApplication();
		
		mapView.setMapTouchListener(null);//轨迹管理相关页面禁用地图长按短按事件
	}
	/**
	 * 给控件添加点击监听事件
	 */
	private void addListener() {
		zoomControl = new ZoomControl(this, mapView);
		setScaleText();
		DayOrNightControl.getIntance().addDayOrNightListener(dayOrNightListener);
		MapAPI.getInstance().addMapListenter(mapListener);
		zoomin.setOnClickListener(zoomControl);
		zoomout.setOnClickListener(zoomControl);
		speedBtn.setOnTouchListener(new TraceSpeedControlListener(this));
	}
	/*
	 * 设置比例尺TEXTVIEW的文本
	 */
	public void setScaleText() {
		int iScale = (int) MapAPI.getInstance().getMapScale();
		String sScaleText = UIResourceUtil.getScaleText(iScale);
		scaleTextView.setText(sScaleText);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapListener = null;
	}
	/*
	 * 与黑夜白天相关控件样式更新
	 */
	public void updateUIStyle(boolean mdayOrNight) {
		zoomControl.initZoom();
		if (mdayOrNight) {
			scaleTextView.setTextColor(Color.BLACK);
			back_car.setBackgroundResource(R.drawable.navistudio_backcar_day);
		} else {
			back_car.setBackgroundResource(R.drawable.navistudio_backcar_night);
			scaleTextView.setTextColor(Color.WHITE);
		}
	}
	// 自动判断下昼夜模式监听
	DayOrNightListener dayOrNightListener = new DayOrNightListener() {
		@Override
		public void changestatus(boolean status) {
			// true白天，false黑夜
			updateUIStyle(status);
		}
	};

	@Override
	public void onBackPressed() {
		if (!MapAPI.getInstance().isCarInCenter()) {
			mapView.goBackCar();
			return;
		}
		super.onBackPressed();
		MapAPI.getInstance().delLayer(101);
		routeLayer.ShowLayer();
//		MapAPI.getInstance().setPrewMode(false);
		myapp.isTracePlay=false;
	}
	
	public void setButtonVisible(){
		if(back_car.getVisibility()==View.GONE){
			back_car.startAnimation(Utils.setShowAnimation(this));
		    back_car.setVisibility(View.VISIBLE);
		}
	}
	public void setButtonGone(){
		if(back_car.getVisibility()==View.VISIBLE){
			back_car.startAnimation(Utils.setHideAnimation(this));
		    back_car.setVisibility(View.GONE);
		}
	}
	
}
