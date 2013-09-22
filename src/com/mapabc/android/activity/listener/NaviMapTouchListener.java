package com.mapabc.android.activity.listener;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.trafficguidance.GPSTool;
import com.mapabc.android.activity.NaviResultListActivity;
import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.RouteLayer;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.TTSAPI;
import com.mapabc.naviapi.TipParams;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.listener.MapTouchListener;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.NSPoint;

/**
 * 长按，短按监听
 * 
 */
public class NaviMapTouchListener implements MapTouchListener,OnClickListener {
	private static final String TAG = "NaviMapTouchListener";
	public NaviStudioActivity m_activity;
	public NaviResultListActivity m_r_activity;
	MapView m_mapView;
	private LayoutInflater mInflater;
	int pos = -1;//保存摄像头的位置
	View tipView;
	View imgView;
	TextView tvTipRoadNam;
	ImageButton searchButton,endPointButton;
	private static final int H_SHOW_TIP = 100;// 弹出TIP
	private static final int C_SHOW_TIP = 101;// 弹出TIP
	// private static final int H_
    private NSLonLat lonlat = null;
	Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == H_SHOW_TIP) {
				if (m_mapView.isHasTip()) {
					m_mapView.hideTip();
				}
				NSPoint point = new NSPoint();
				point.x = msg.arg1;
				point.y = msg.arg2;
				NSLonLat lonlat = new NSLonLat();
				MapAPI.getInstance().screenCoordToWorldCoord(point, lonlat);
				NaviMapTouchListener.this.lonlat = lonlat;
				String roadName = Utils.getRoadName(lonlat, 100);
				if (roadName.length() > 0) {
					tvTipRoadNam.setText(roadName);
				} else {
					tvTipRoadNam
							.setText(R.string.navimaptouchlistener_noroadname);
				}
				int width = 160;
				int height = 100;
				if(Utils.getDPI(m_activity)==Utils.XDPI){
					width = 210;
					height = 130;
				}
				Paint paint = new Paint();
				paint.setTextSize(tvTipRoadNam.getTextSize());
				int size = (int) paint.measureText(tvTipRoadNam.getText()
						.toString());
				width = width + size;
				Log.i("x "+lonlat.x,"y "+ lonlat.y);
				TipParams params = new TipParams(width, height, lonlat.x, lonlat.y);
				m_mapView.showTip(tipView, params);
			}else if(msg.what == C_SHOW_TIP){
				if(m_r_activity != null){
					Log.i("lhl", "activity is not null");
					LinearLayout l = (LinearLayout)m_r_activity.findViewById(R.id.rightframe);
					ImageButton img = (ImageButton) m_r_activity.findViewById(R.id.imageframe);
//					l.setOnClickListener(listener);
					l.setVisibility(View.VISIBLE);
					TextView temperature1 = (TextView)m_r_activity.findViewById(R.id.tempeture);
					TextView temperature2 = (TextView)m_r_activity.findViewById(R.id.tempeture2);
					String s = "温度：32°C";
					String s2 = "湿度：25%";
					
					img.setOnClickListener(listener);
//					img.setVisibility(View.VISIBLE);
					if(pos%2 == 0)
					{
						s = "温度：29°C";
					    s2 = "湿度：33%";
						img.setBackgroundResource(R.drawable.testimage);
					}
					else
						img.setBackgroundResource(R.drawable.roadhole);
					
					temperature1.setText(s);
					temperature2.setText(s2);
				}else{
					Log.i("lhl", "activity is null");
				}
			}
		}

	};
	public OnClickListener listener=new OnClickListener() {   
	    public void onClick(View v) {  
	    	LinearLayout l = (LinearLayout)m_r_activity.findViewById(R.id.rightframe);
	    	l.setVisibility(View.GONE);
//	        v.setVisibility(View.GONE);
	    }   
	};

	public NaviMapTouchListener(NaviStudioActivity activity, MapView mapView) {
		m_activity = activity;
		m_mapView = mapView;
		
		mInflater = LayoutInflater.from(m_activity);
		tipView = mInflater.inflate(R.layout.navistudio_tip_layout, null);
		imgView = mInflater.inflate(R.layout.navistudio_tip_layout, null);
		tvTipRoadNam = (TextView) tipView.findViewById(R.id.tv_tip_road_name);
		searchButton = (ImageButton)tipView.findViewById(R.id.ib_search_around);
		searchButton.setOnClickListener(this);
		endPointButton = (ImageButton)tipView.findViewById(R.id.ib_set_end_point);
		endPointButton.setOnClickListener(this);
	}

	/**
	 * 短按监听方法
	 */
	@Override
	public void onClickTouch(int x, int y) {
		// TODO Auto-generated method stub
//		GPSTool g = new GPSTool();
//		g.start();
		pos = -1;
		NSLonLat cameraLonLat[] = NaviControl.cameraLonLat;
		if(cameraLonLat == null)return;
		float distance[] = new float[cameraLonLat.length];//保存点击点到所有摄像头的距离

		Log.i("lhl", "onclick touch "+x+" "+y );
		NSPoint p = new NSPoint(x,y); 
		NSPoint p1 = new NSPoint(x+40,y); 
		NSLonLat l = new NSLonLat();
		NSLonLat l1 = new NSLonLat();
		MapAPI.getInstance().screenCoordToWorldCoord(p,l);
		MapAPI.getInstance().screenCoordToWorldCoord(p1,l1);
		float r = UtilAPI.getInstance().calculateDis(l1.x, l1.y, l.x, l.y);
		float min = r;//保存最小距离
		Log.i("lhl", "workd coord "+l.x+" "+l.y);
		for(int i=0;i<cameraLonLat.length;i++){
			if(cameraLonLat[i] == null)break;
			float d = UtilAPI.getInstance().calculateDis(cameraLonLat[i].x, cameraLonLat[i].y, l.x, l.y);
			if(l.y < cameraLonLat[i].y)d=d+r;
			distance[i] = d;
			Log.i("lhl", d+"");
		}
		for(int j=0;j<distance.length;j++){//求出最近的点并保存位置
			if(distance[j]<min){
				min = distance[j];
				if(min < r)
					pos = j;
			}
		}
		if(pos != -1){//最后的结果为一个点cameraLonLat[pos]
			Message msg = Message.obtain();
			msg.what = C_SHOW_TIP;
			h.sendMessage(msg);
		}
		if(m_mapView.isHasTip()){
					m_mapView.hideTip();
			}
		}


	/**
	 * 长按监听方法
	 */
	@Override
	public void onLongTouch(int x, int y) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = H_SHOW_TIP;
		msg.arg1 = x;
		msg.arg2 = y;
		h.sendMessage(msg);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.equals(this.searchButton)){
			Utils.intentEvent(null,0,Constants.SEARCHAROUND_CENTER_POINT, lonlat, m_activity, Constants.ACTIVITY_SEARCHAROUND);
		}else if(arg0.equals(this.endPointButton)){
			m_activity.naviControl.calculatePath(lonlat,0);
		}
		m_mapView.hideTip();
	}
}
