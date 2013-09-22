package com.mapabc.android.activity.route;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.route.RouteSegInfo;
import com.mapabc.naviapi.type.NSLonLat;

public class CrossingViewActivity extends Activity {
	private static final String TAG = "CrossingViewActivity";
	MapView m_mapView;
	TextView txtTopic;
	Button button_up;
	Button button_down;
	ImageView crossing_image;
	TextView crossing_start,crossing_end,crossing_count,crossing_remain;
	int x,y;
	float mapscale;
	int mapModel;
	NSLonLat centerInfo;
	int remain_dis_array[]=null;
	RouteSegInfo routeSegInfo[] =null;
	class CrossingView extends View
	{
		public CrossingView(Context context)
		{
			super(context);
		}
		
		protected void onDraw(Canvas canvas)
		{
			int width = getWidth();
			int height = getHeight();
			float x0 = (width - m_CrossingBitmap.getWidth()) / 2.0F;
			float y0 = (height - m_CrossingBitmap.getHeight()) / 2.0F;
			canvas.drawBitmap(m_CrossingBitmap, x0,txtTopic.getHeight()+20, m_CrossingPaint);
		}
	}
	
	public CrossingViewActivity()
	{

		m_nCurCrossingIndex = -1;
		m_CrossingBitmap = null;
		m_CrossingCanvas = null;
		m_CrossingPaint = null;
		m_CrossingView = null;
    	m_DecimalFormat = (DecimalFormat)NumberFormat.getInstance();
    	m_DecimalFormat.setMaximumFractionDigits(2);
    	m_DecimalFormat.setGroupingSize(3);
	}
	
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.crossing_view);
		txtTopic = (TextView) findViewById(R.id.txtTopic);
		txtTopic.setText(R.string.crosing_title);
		ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new BackListener(this,false,false));
		button_up = (Button)this.findViewById(R.id.crossing_view_up);
		button_down = (Button)this.findViewById(R.id.crossing_view_down);

		if(!RouteAPI.getInstance().isRouteValid())
		{
			finish();
		}
		else
		{
			crossing_image = (ImageView)this.findViewById(R.id.crossing_image);
			crossing_start = (TextView)this.findViewById(R.id.crossing_start);
			crossing_end = (TextView)this.findViewById(R.id.crossing_end);
			crossing_count = (TextView)this.findViewById(R.id.crossing_count);
			crossing_remain = (TextView)this.findViewById(R.id.crossing_remain);
			
			//保留地图原有参数
			mapscale = MapAPI.getInstance().getMapScale();
			mapModel = MapAPI.getInstance().getMapView();
			centerInfo = MapAPI.getInstance().getMapCenter();
			m_mapView = AutoNaviMap.getInstance(getApplicationContext()).getMapView();
			

			
			Bundle b = this.getIntent().getExtras();
			if(b == null || !b.containsKey("crossingindex"))
			{
				m_nCurCrossingIndex = 0;
			}
			else
			{
				m_nCurCrossingIndex = b.getInt("crossingindex");
			}
			Log.e(TAG, "m_nCurCrossingIndex:"+m_nCurCrossingIndex);	
			m_nCrossingNum = RouteAPI.getInstance().getSegmentCount()+1;
			remain_dis_array=new int[m_nCrossingNum];
			routeSegInfo = new RouteSegInfo[m_nCrossingNum-1];
			remain_dis_array[m_nCrossingNum-1]=0;
			int remain_dis=0;
			for(int i=m_nCrossingNum-2;i>=0;i--){
			   RouteSegInfo segInfo = new RouteSegInfo();
			   RouteAPI.getInstance().getSegmentInfo(i, segInfo);
			   remain_dis+=segInfo.len;
			   remain_dis_array[i]=remain_dis;
			   routeSegInfo[i] = segInfo;
			}
			
			Button preBtn = (Button)findViewById(R.id.crossing_view_up);
			preBtn.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					if (NaviControl.getInstance().isReCaling) {
						String message=getString(R.string.crossingview_calculate);
						Toast toast = Toast.makeText(CrossingViewActivity.this, message, 1000);
						toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
						toast.show();
						
					}else {
						update();
						prev();
					}
					
					
				}
			});
			
			Button nextBtn = (Button)findViewById(R.id.crossing_view_down);
			nextBtn.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v)
				{
					if (NaviControl.getInstance().isReCaling) {
						String message=getString(R.string.crossingview_calculate);
						Toast toast = Toast.makeText(CrossingViewActivity.this, message, 1000);
						toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
						toast.show();
					}else {
						update();
						next();
					}
				}
			});
			
			
            MapAPI.getInstance().setMapView(0);
			MapAPI.getInstance().setMapScale(18);
//			MapAPI.getInstance().onlyShowMapRoute(true);
			MapAPI.getInstance().setVehicleShowStatus(true);
			int height = Utils.getCurScreenHeight(this);
			int width = Utils.getCurScreenWidth(this);
			height -= 42;
			int remain = width % 4; 
			if (remain != 0) {
				width = width - remain + 4;
			}
			m_mapView.destoryMap();
			m_mapView.initMap(width, height);
			

			m_CrossingCanvas = new Canvas();
//			m_CrossingCanvas.setBitmap(m_CrossingBitmap);
			m_CrossingBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
			
			LinearLayout llo = (LinearLayout)findViewById(R.id.crossing_view_linearlayout);
			View view = findViewById(R.id.crossing_view_map);
			m_CrossingView = new CrossingView(this);
			m_CrossingView.setId(R.id.crossing_view_map);
			int nIndex = llo.indexOfChild(view);
			llo.indexOfChild(view);
			llo.addView(m_CrossingView, nIndex, view.getLayoutParams());
			
			
			if(m_nCurCrossingIndex==0){
				this.button_up.setEnabled(false);
				this.button_down.setEnabled(true);
			}else 
			if(m_nCurCrossingIndex==(m_nCrossingNum-1)){
				this.button_up.setEnabled(true);
				this.button_down.setEnabled(false);
			}else{
				this.button_up.setEnabled(true);
				this.button_down.setEnabled(true);
			}
			view();
		}
	}
	/**
	 * 
	 * @Copyright:mapabc
	 * @description:
	 * @author fei.zhan
	 * @date 2012-9-7  void
	 */
	
	private void update(){
		m_nCrossingNum = RouteAPI.getInstance().getSegmentCount()+1;
		remain_dis_array=new int[m_nCrossingNum];
		routeSegInfo = new RouteSegInfo[m_nCrossingNum-1];
		remain_dis_array[m_nCrossingNum-1]=0;
		int remain_dis=0;
		for(int i=m_nCrossingNum-2;i>=0;i--){
		   RouteSegInfo segInfo = new RouteSegInfo();
		   RouteAPI.getInstance().getSegmentInfo(i, segInfo);
		   remain_dis+=segInfo.len;
		   remain_dis_array[i]=remain_dis;
		   routeSegInfo[i] = segInfo;
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		MapAPI.getInstance().onlyShowMapRoute(false);
		MapAPI.getInstance().setVehicleShowStatus(false);
		MapAPI.getInstance().setMapScale(mapscale);
		MapAPI.getInstance().setMapView(mapModel);
		
		MapAPI.getInstance().setMapCenter(centerInfo);
		int height = Utils.getCurScreenHeight(this);
		int width = Utils.getCurScreenWidth(this);
		m_mapView.destoryMap();
		m_mapView.initMap(width, height);
	}

	/**
	 * 前一路口
	 */
	protected void prev()
	{
		if(m_nCurCrossingIndex > 0)
		{
			m_nCurCrossingIndex--;
			view();
			if(m_nCurCrossingIndex==0){
				this.button_up.setEnabled(false);
				this.button_down.setEnabled(true);
			}else{
				this.button_up.setEnabled(true);
				this.button_down.setEnabled(true);
			}
			m_CrossingView.invalidate();
		}
	}
	
	/**
	 * 后一路口
	 */
	protected void next()
	{
		if(m_nCurCrossingIndex < (m_nCrossingNum-1))
		{
			m_nCurCrossingIndex++;
			view();
			if(m_nCurCrossingIndex==(m_nCrossingNum-1)){
				this.button_up.setEnabled(true);
				this.button_down.setEnabled(false);
			}else{
				this.button_up.setEnabled(true);
				this.button_down.setEnabled(true);
			}
			m_CrossingView.invalidate();
		}
	}
	
	protected void returnRouteDescription()
	{
		finish();
	}
	
	protected void view()
	{
		RouteSegInfo segInfo = new RouteSegInfo();
		RouteSegInfo segInfo_1 = new RouteSegInfo();
		if(m_nCurCrossingIndex<m_nCrossingNum-1)
			segInfo = routeSegInfo[m_nCurCrossingIndex];
		if(m_nCurCrossingIndex>0)
			segInfo_1 = routeSegInfo[m_nCurCrossingIndex-1];		
		if(m_nCurCrossingIndex==m_nCrossingNum-1){
			NSLonLat end = new NSLonLat();
			end= RouteAPI.getInstance().getEndPoint();
			MapAPI.getInstance().setMapCenter(end);
		}else{
			MapAPI.getInstance().setMapCenter(segInfo.start);
		}
		String desc = "";
		if (m_nCurCrossingIndex == 0) {
			desc = getString(R.string.crossingview_start_point);//出发地
		} else {
			desc = segInfo_1.segName;
		}
        crossing_start.setText(desc);
		
		if(m_nCurCrossingIndex==this.m_nCrossingNum-1){
			desc = getString(R.string.crossingview_end_point);//目的地
		}else{
		    desc = segInfo.segName;
		}
        crossing_end.setText(desc);

//		m_mapView.myDraw(m_CrossingCanvas, m_CrossingBitmap);
		m_mapView.drawMap(m_CrossingBitmap);


    	if (m_nCrossingNum - 1 == m_nCurCrossingIndex) {
    		crossing_image.setImageResource(R.drawable.common_tothis);
		} else if (m_nCurCrossingIndex == 0) {
			crossing_image.setImageResource(R.drawable.common_fromthis);
		} else {
			crossing_image
					.setImageResource(RouteDescriptionActivity.getIconId((int) segInfo_1.naviAction));
		}
    	
    	String s = "";
		if (m_nCurCrossingIndex == 0) {
			s = "0 m";
		} else {
			s=getLenth(segInfo_1.len);
		}
        crossing_count.setText(getString(R.string.crossingview_current_route)+s);//("当前路段:"+s);
        s="";
//        if (m_nCurCrossingIndex == 0) {
//			s = "0 m";
//		} else if(m_nCurCrossingIndex == m_nCrossingNum - 1) {
//			s= getLenth(0);
//		}else {
			s=getLenth(remain_dis_array[m_nCurCrossingIndex]);
//		}
        crossing_remain.setText(getString(R.string.crossingview_remainder_route)+s);//("剩余路段:"+s);
	}
	private String getLenth(int len){
		String s="";
		if(len>1000){
        	double d = (double)len / 1000.0D;
        	String s0 = m_DecimalFormat.format(d);
        	s = s0+" km";
        }else{
        	s = len+" m";
        }
		return s;
	}

    DecimalFormat  m_DecimalFormat;
	
	Bitmap m_CrossingBitmap;
	
	Canvas m_CrossingCanvas;
	
	Paint  m_CrossingPaint;
	
	//当前正在浏览的路口位置
	int m_nCurCrossingIndex;
	
	int m_nCrossingNum;
	
	CrossingView m_CrossingView;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int height = Utils.getCurScreenHeight(this);
		int width = Utils.getCurScreenWidth(this);
		height -= 42;
		int remain = width % 4; 
		if (remain != 0) {
			width = width - remain + 4;
		}
		m_mapView.destoryMap();
		m_mapView.initMap(width, height);
		m_CrossingBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		m_mapView.drawMap(m_CrossingBitmap);
	}
	
}
