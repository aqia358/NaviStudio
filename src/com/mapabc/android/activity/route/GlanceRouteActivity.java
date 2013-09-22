package com.mapabc.android.activity.route;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.log.Logger;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.NSRect;
import com.mapabc.naviapi.utils.AndroidUtils;

/**
 * @（#）:GlanceRouteActivity.java
 * @description: 全程浏览-展现导航路径全图
 * @author: changbao.wang 2011-05-23
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class GlanceRouteActivity extends Activity {

	private static final String TAG = "GlanceRouteActivity";
	private MapView m_mapView;
	private GlanceRouteView mglanceRouteView;
	private TextView txtTopic;
	private Bitmap m_Bitmap;
//	private Canvas m_Canvas;
	private Paint m_Paint;
	private float mapscale;
	private int mapModel;
	private NSLonLat m_CenterInfo;
	private boolean isVehicleInCenter;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.glanceroutelayout);
		txtTopic = (TextView) findViewById(R.id.txtTopic);
		txtTopic.setText("全程概览");
		ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new BackListener(this, false, false));
		if (!RouteAPI.getInstance().isRouteValid()) {
			Log.e(TAG, " not has route ,finish()");
			finish();
			return;
		}
		Logger.e(TAG, "isRouteValid over");
		//保留地图原有参数
		mapscale = MapAPI.getInstance().getMapScale();
		mapModel = MapAPI.getInstance().getMapView();
		m_CenterInfo = MapAPI.getInstance().getMapCenter();
		isVehicleInCenter = MapAPI.getInstance().isCarInCenter();
		m_mapView = AutoNaviMap.getInstance(getApplicationContext())
				.getMapView();
		MapAPI.getInstance().setMapView(0);
		LinearLayout llo = (LinearLayout) findViewById(R.id.glanceroute_view_layout);
		View view = findViewById(R.id.glanceroute_view_map);
		mglanceRouteView = new GlanceRouteView(this);
		mglanceRouteView.setId(R.id.glanceroute_view_map);
		int height = Utils.getCurScreenHeight(this);
		int width = Utils.getCurScreenWidth(this);
		int screen = AndroidUtils.checkScreenResolution(this);
		if(screen==AndroidUtils.SCREEN_RESOLUTION_WFVGA||
				screen==AndroidUtils.SCREEN_RESOLUTION_WVGA||
				screen==AndroidUtils.SCREEN_RESOLUTION_UNKNOWN){
			height -= 102;
		}else{
		    height -= 65;
		}
		int remain = width % 4;
		if (remain != 0) {
			width = width - remain + 4;
		}
		m_mapView.destoryMap();
		m_mapView.initMap(width, height);

		MapAPI.getInstance().setVehicleShowStatus(true);
		setMapSize();
		m_Bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
//		m_Canvas = new Canvas();
		int nIndex = llo.indexOfChild(view);
		llo.indexOfChild(view);
		llo.addView(mglanceRouteView, nIndex, view.getLayoutParams());
		m_mapView.drawMap(m_Bitmap);
	}


	private void setMapSize() {
		NSRect fRect = new NSRect(0, 0, 0, 0);
		boolean res = RouteAPI.getInstance().getRouteExtent(fRect);
		if (!res) {
			Logger.e(TAG, " ns_ROUTE_GetRouteExtent false ,finish()");
			finish();
			return;
		} else {
			if(fRect==null){
				Logger.e(TAG, "fRect is null");
			}else{
				Logger.e(TAG, fRect.left+","+fRect.right+","+fRect.bottom+","+fRect.top);
			}
			NSLonLat start = RouteAPI.getInstance().getStartPoint();
			NSLonLat end = RouteAPI.getInstance().getEndPoint();
			updateBounds(start,fRect);
			updateBounds(end,fRect);
			Logger.e(TAG, fRect.left+","+fRect.right+","+fRect.bottom+","+fRect.top);
			MapAPI.getInstance().setMapExtent(fRect);
			float temp= MapAPI.getInstance().getMapScale();
			Logger.e(TAG, "getMapScale:"+temp);
		}
	}
    private void updateBounds(NSLonLat lonlat,NSRect fRect){
    	if(lonlat.x<fRect.left){
    		Logger.e(TAG, "区域异常left");
    		fRect.left = lonlat.x-0.005f;
    	}
    	if(lonlat.x>fRect.right){
    		Logger.e(TAG, "区域异常right");
    		fRect.right = lonlat.x+0.005f;
    	}
    	if(lonlat.y>fRect.top){
    		Logger.e(TAG, "区域异常top");
    		fRect.top = lonlat.y+0.005f;
    	}
    	if(lonlat.y<fRect.bottom){
    		Logger.e(TAG, "区域异常top");
    		fRect.bottom = lonlat.y-0.005f;
    	}
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// setMapSize();
		super.onResume();
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
//		MapAPI.getInstance().setVehicleShowStatus(false);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MapAPI.getInstance().setVehicleShowStatus(false);
		MapAPI.getInstance().setMapScale(mapscale);
		MapAPI.getInstance().setMapView(mapModel);
		if(this.isVehicleInCenter){
//			  MapAPI.getInstance().setMapCenterInfo(MapAPI.getInstance().getVehiclePos(), null, null, null,null);
			  MapAPI.getInstance().setMapCenter(MapAPI.getInstance().getVehiclePos());
			}else{
//			  MapAPI.getInstance().setMapCenterInfo(this.m_CenterInfo.mPos, 
//					  this.m_CenterInfo.cName, this.m_CenterInfo.cAddress, this.m_CenterInfo.cPhone,this.m_CenterInfo.cADCode);
				MapAPI.getInstance().setMapCenter(this.m_CenterInfo);
			}
		int height = Utils.getCurScreenHeight(this);
		int width = Utils.getCurScreenWidth(this);
		m_mapView.destoryMap();
		m_mapView.initMap(width, height);
	}

	class GlanceRouteView extends View {

		public GlanceRouteView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		protected void onDraw(Canvas canvas) {
			int width = getWidth();
			float x0 = (width - m_Bitmap.getWidth()) / 2.0F;
			canvas.drawBitmap(m_Bitmap, x0, txtTopic.getHeight()+20, m_Paint);
		}
	}
}
