package com.mapabc.android.activity;

import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.CarBackEvent;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.CurrentPointListener;
import com.mapabc.android.activity.base.ExitDialog;
import com.mapabc.android.activity.base.GpsControl;
import com.mapabc.android.activity.base.MapModelControl;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.RouteLayer;
import com.mapabc.android.activity.base.VolumeControl;
import com.mapabc.android.activity.base.ZoomControl;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.listener.NaviMapTouchListener;
import com.mapabc.android.activity.route.RouteManagerActivity;
import com.mapabc.android.activity.search.adapter.NaviResultListAdapter;
import com.mapabc.android.activity.setting.adapter.OtherFunctionListAdapter;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.TTSAPI;
import com.mapabc.naviapi.listener.DayOrNightListener;
import com.mapabc.naviapi.map.DayOrNightControl;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.utils.AndroidUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class NaviResultListActivity extends Activity implements OnItemClickListener{
	public ImageButton startNaviBtn,endNaviBtn;
	private final String TAG = "NaviResultListActivity";
	private ListView naviResultListView;
	NaviResultListAdapter itemadapter = null;
	public MapView mapView = null;// 地图实例
    private View oldItemView = null;
	public NaviControl naviControl;
	private String type;
	Bundle bundle;
	

	public ImageButton zoomin, zoomout;
	public LinearLayout zoomLayout;
	public ZoomControl zoomControl;
	private boolean flag = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_result_list);
        initResultListView(); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_navi_result_list, menu);
        return true;
    }
	private void initResultListView() {
		
		FrameLayout flo = (FrameLayout) findViewById(R.id.map);
		View mView = (View) findViewById(R.id.v_mapresult);
		int nIndex = flo.indexOfChild(mView);
		flo.removeView(mView);
		mapView = AutoNaviMap.getInstance(getApplicationContext()).getMapView();
		ViewParent parent = mapView.getParent();
		if (parent != null && (parent instanceof FrameLayout)) {
			((FrameLayout) parent).removeView(mapView);
		}
		flo.addView(mapView, nIndex, mView.getLayoutParams());
		naviControl = NaviControl.getInstance();	
		RouteAPI.getInstance().setCallBack(naviControl);
		naviControl.setLayout_ml(this, mapView);
		initComponents();
		addListener();
		int scale=SettingForLikeTools.getMapScale(this);
		MapAPI.getInstance().setMapScale(scale);
		
		if(NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_REALNAVI){
			NaviControl.getInstance().stopRealNavi();
		}else if(NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_SIMNAVI){
			NaviControl.getInstance().stopSimNavi();
			MapAPI.getInstance().setVehiclePosInfo(RouteAPI.getInstance().getStartPoint(), 0);
		}
		MapAPI.getInstance().setMapCenter(MapAPI.getInstance().getVehiclePos());
		if(RouteAPI.getInstance().clearRoute()){
			
			RouteLayer r = new RouteLayer();
			r.deleteLayer();
		}
		//第一次进入默认加载速度最快
		SettingForLikeTools.setRouteCalNetMode(
				NaviResultListActivity.this, 0);
		if(type.equals("input")){
			naviControl.calculatePath_ml(NaviStudioActivity.end_mPos,1);
		}else if(type.equals("map")){
			naviControl.calculatePath_ml(CurrentPointListener.endPoint,1);
		}
		resetStatus();

	}
	/**
	 * 初始化控件
	 */
	private void initComponents() {
		startNaviBtn = (ImageButton) findViewById(R.id.ib_startnavi);
		endNaviBtn = (ImageButton) findViewById(R.id.ib_cancelnavi);
		naviResultListView = (ListView) findViewById(R.id.lv_naviresultlist);
		zoomin = (ImageButton) this.findViewById(R.id.ib_btnZoomIn_list);
		zoomout = (ImageButton) this.findViewById(R.id.ib_btnZoomOut_list);
		zoomLayout = (LinearLayout) findViewById(R.id.ll_zoomlayout_list);
		//开始算路
		bundle = this.getIntent().getExtras();
        /*获取Bundle中的数据，注意类型和key*/
        type = bundle.getString("type");
		//naviResultListView.setSelection(0);
	}

	/**
	 * 给控件添加点击监听事件
	 */
	private void addListener() {
		
		//为主界面功能按钮添加监听
		endNaviBtn.setOnClickListener((OnClickListener) listener);
		startNaviBtn.setOnClickListener((OnClickListener) listener);
		itemadapter = new NaviResultListAdapter(this);
		naviResultListView.setAdapter(itemadapter);
		naviResultListView.setOnItemClickListener(this);
		naviResultListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		naviResultListView.setSelected(true);
		naviResultListView.setSelection(0);
		naviResultListView.setItemChecked(0, true);
		zoomControl = new ZoomControl(this, mapView);
		zoomin.setOnClickListener(zoomControl);
		zoomout.setOnClickListener(zoomControl);
		
		NaviMapTouchListener mapTouchListener = (NaviMapTouchListener) mapView.getMapTouchListener();
		mapTouchListener.m_r_activity = this;

	}
	
	/**
	 *主界面功能按钮的点击事件响应
	 */
	public OnClickListener listener=new OnClickListener() {   
	    public void onClick(View v) {   
	        ImageButton btn=(ImageButton) v;   
	        switch (btn.getId()) {   
	        case R.id.ib_cancelnavi:
	        {
	        	if(NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_REALNAVI){
					NaviControl.getInstance().stopRealNavi();
				}else if(NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_SIMNAVI){
					NaviControl.getInstance().stopSimNavi();
					MapAPI.getInstance().setVehiclePosInfo(RouteAPI.getInstance().getStartPoint(), 0);
				}
				MapAPI.getInstance().setMapCenter(MapAPI.getInstance().getVehiclePos());
				if(RouteAPI.getInstance().clearRoute()){
					
					RouteLayer r = new RouteLayer();
					r.deleteLayer();
				}
				onBackPressed();
				break;
				
	        }
	        case R.id.ib_startnavi:{
	        	TTSAPI.getInstance().addPlayContent(
						getString(R.string.navicontrol_startnavi),
						Const.AGPRIORITY_CRITICAL);
	        	if (mapView.isHasTip()) {
	        		mapView.hideTip();
				}
	        	onBackPressed();
	        	break;
	        }
	        }      
	    }   
	};
	
	/*
	 * 与黑夜白天相关控件样式更新
	 */
	public void updateUIStyle(boolean mdayOrNight) {
		zoomControl.initZoom();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(flag){
			parent.getChildAt(0).setBackgroundColor(Color.rgb(0,0,0));
			flag = false;
		}
		
		if(oldItemView != null && oldItemView != view){
		  view.setBackgroundColor(Color.rgb(75,191,245));//设置选中的背景颜色
		  oldItemView.setBackgroundColor(Color.TRANSPARENT);
		}else{
		  view.setBackgroundColor(Color.rgb(75,191,245));//设置选中的背景颜色
		}
		oldItemView = view;
		
		//记录用户选择的方案
		switch(position){
		case 0://速度最快
		{
			SettingForLikeTools.setRouteCalNetMode(
					NaviResultListActivity.this, 0);
			break;
		}
		case 1://费用最少
		{
			SettingForLikeTools.setRouteCalNetMode(
					NaviResultListActivity.this, 1);
			break;
		}
		case 2://距离最短
		{
			SettingForLikeTools.setRouteCalNetMode(
					NaviResultListActivity.this, 2);
			break;
		}
		case 3://不走高速
		{
			SettingForLikeTools.setRouteCalNetMode(
					NaviResultListActivity.this, 5);
			break;
		}
		case 4://国道优先
		{
			SettingForLikeTools.setRouteCalNetMode(
					NaviResultListActivity.this, 6);
			break;
		}
		case 5://省道优先
		{
			SettingForLikeTools.setRouteCalNetMode(
					NaviResultListActivity.this, 7);
			break;
		}
			default:
				break;
		}
		
		if(type.equals("input")){
			naviControl.calculatePath_ml(NaviStudioActivity.end_mPos,1);
		}else if(type.equals("map")){
			naviControl.calculatePath_ml(CurrentPointListener.endPoint,1);
		}
		resetStatus();
		
	} 
	
	public void resetStatus() {
		try {
			setIntent(null);
			NaviStudioActivity.executorService.execute(new Runnable() {
				@Override
				public void run() {
					ActivityStack stack = ActivityStack.newInstance();
					stack.finishAllExcludeMap();// 清空其它界面
				}
			});

			// mapView.hideTip();
			// blEnableSearch = true;// 地点搜索菜单可用

		} catch (Exception e) {
			Log.e("MapActivity::resetStatus", e.toString());
		}
	}
	@Override
	public void onBackPressed() {
		try{
			super.onBackPressed();
			Intent intent = new Intent(Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}catch(Exception e){
			
		}
	}
	    
}
