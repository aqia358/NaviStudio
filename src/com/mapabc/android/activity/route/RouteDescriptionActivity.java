package com.mapabc.android.activity.route;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.NextRoadView;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.route.RouteSegInfo;
import com.mapabc.naviapi.type.IntValue;
import com.mapabc.naviapi.type.NSLonLat;

/**
 * 路线描述，显示当前规划路线的总距离，经过的收费站个数，并以列表形式显示途径的各个道路和转向信息，点击 任意路段，可以在地图上查看该路段
 * 
 * @author Administrator
 * 
 */
public class RouteDescriptionActivity extends Activity {
	int screenWidth = 0;
	int screenHigth = 0;
	int width = 0;
	RouteDescriptionAdapter routedescriptionadapter;
	DecimalFormat m_DecimalFormat;
	private ListView listView;
	private float mapscale;
	private int mapModel;
	private boolean isVehicleInCenter;
	private NSLonLat m_CenterInfo=null; 
	private static final String TAG = "RouteManagerActivity";
	boolean isCarInCenter = false;
	private HashMap<Integer, RouteSegInfo> setInfoMap=new HashMap<Integer, RouteSegInfo>();
	
	private  static int lastPosition=0;//记录横竖屏切换前列表的位置
	private  boolean back=false;
	
	class RouteDescriptionAdapter extends BaseAdapter {
//		String sub_road_name[] = null;
		int count = RouteAPI.getInstance().getSegmentCount()+1;

		public RouteDescriptionAdapter() {
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int i) {
			RouteSegInfo segInfo = new RouteSegInfo();
			RouteAPI.getInstance().getSegmentInfo(i, segInfo);
			return segInfo;
		}

		public long getItemId(int i) {
			return (long) i;
		}

		public View getView(int i, View view, ViewGroup viewgroup) {
			
			if (view == null) {
				if (RouteDescriptionActivity.this.screenHigth < RouteDescriptionActivity.this.screenWidth) {
					view = LayoutInflater.from(viewgroup.getContext()).inflate(
							R.layout.route_view_row_horizontal, null);
				} else {
					view = LayoutInflater.from(viewgroup.getContext()).inflate(
							R.layout.route_view_row, null);
				}
			}
			RouteSegInfo segInfo = new RouteSegInfo();
			RouteSegInfo segInfo_1 = new RouteSegInfo();
			if(i<count-1)
			RouteAPI.getInstance().getSegmentInfo(i, segInfo);
			if(i>0)
			RouteAPI.getInstance().getSegmentInfo(i-1, segInfo_1);
			ImageView imageview = (ImageView) view
					.findViewById(R.id.route_view_row_turn);

			if (this.getCount() - 1 == i) {
				imageview.setImageResource(R.drawable.common_tothis);
			} else if (i == 0) {
				imageview.setImageResource(R.drawable.common_fromthis);
			} else {
				if(segInfo_1.naviAssist==36||segInfo_1.naviAssist==35){
					imageview.setImageResource(R.drawable.currentpoint_jingguothis);
				}else {
					imageview.setImageResource(getIconId((int) segInfo_1.naviAction));
				}
			}
			NextRoadView tv0 = (NextRoadView) view
					.findViewById(R.id.route_view_row_start_view);
			String desc = "";
			if (i == 0) {
				desc = "出发地";
			} else {
				desc=segInfo_1.segName;
				if(segInfo_1.naviAssist==35){
					desc="途经点";
				}
			}
			Log.e(TAG, "TextView WIDTH:" + width / 2);
			tv0.setWidth(width / 2-10);
			tv0.setText(desc);
			TextView tv1 = (TextView) view
					.findViewById(R.id.route_view_row_dis);
			String s = "";
			if (i == 0) {
				s = "0 m";
			} else {
				if (segInfo_1.len > 1000) {
					double d = (double) segInfo_1.len / 1000.0D;
					String s0 = m_DecimalFormat.format(d);
					s = (new StringBuilder(s0)).append(" km").toString();
				} else {
					s = (new StringBuilder()).append(segInfo_1.len).append(" m")
							.toString();
				}
			}
			tv1.setText(s);
			NextRoadView tv2 = (NextRoadView) view
					.findViewById(R.id.route_view_row_end_view);
			if(i==this.getCount()-1){
				desc = "目的地";
			}else{
			    desc = segInfo.segName;
			}
			if (RouteDescriptionActivity.this.screenHigth < RouteDescriptionActivity.this.screenWidth) {
                
				tv2.setWidth(width / 2);
			}
			tv2.setText(desc);
			return view;
		}
	}

	public static int getIconId(int nNaviAction) {
		switch (nNaviAction) {
		case 0: // 无动作
			return R.drawable.navicontrol_goto_front;
			// naviInfo += " 转向:" + "无动作";
		case 1: // 左转

			return R.drawable.navicontrol_goto_left;
			// naviInfo += " 转向:" + "左转";
		case 2: // 右转

			return R.drawable.navicontrol_goto_right;
			// naviInfo += " 转向:" + "右转";
		case 3: // 左前方

			return R.drawable.navicontrol_goto_lf;

			// naviInfo += " 转向:" + "左前方";
		case 4: // 右前方

			return R.drawable.navicontrol_goto_rf;

			// naviInfo += " 转向:" + "右前方";
		case 5: // 左后转

			return R.drawable.navicontrol_goto_lb;
			// naviInfo += " 转向:" + "左后转";
		case 6: // 右后转

			return R.drawable.navicontrol_goto_rb;
			// naviInfo += " 转向:" + "右后转";
		case 7: // 掉头

			return R.drawable.navicontrol_goto_back;
			// naviInfo += " 转向:" + "掉头";
		case 8: // 直行

			return R.drawable.navicontrol_goto_front;
			// naviInfo += " 转向:" + "直行";
		case 9: // 靠左

			return R.drawable.navicontrol_keep_left;
			// naviInfo += " 转向:" + "靠左";
		case 10: // 靠右

			return R.drawable.navicontrol_keep_right;
			// naviInfo += " 转向:" + "靠右";
		case 11: // 进入环岛

			return R.drawable.navicontrol_roundabout;
			// naviInfo += " 转向:" + "进入环岛";
		case 12: // 离开环岛
			return R.drawable.navicontrol_roundabout;
			// naviInfo += " 转向:" + "离开环岛";
		case 13: // 减速行
			return R.drawable.navicontrol_slowdown;
			// naviInfo += " 转向:" + "减速行";
		case 14: // 特有的插入直行
			// naviInfo += " 转向:" + "特有的插入直行";
			break;
		}
		return 0;
	}

	public RouteDescriptionActivity() {
		m_DecimalFormat = (DecimalFormat) NumberFormat.getInstance();
		m_DecimalFormat.setMaximumFractionDigits(2);
		m_DecimalFormat.setGroupingSize(3);
	}

	private void addRouteHeader() {
		TextView tv1 = (TextView) findViewById(R.id.route_view_total_distance);
//		GPSRouteInfo gpsInfo = new GPSRouteInfo();
//		RouteAPI.getInstance().getRouteNaviInfo(gpsInfo);
		IntValue distance=new IntValue();
		IntValue time=new IntValue();
		RouteAPI.getInstance().getDistanceAndTime(distance, time);
		String s = "总距离: ";
		if (distance.value > 1000) {
			double d = (double) distance.value  / 1000.0D;
			s += m_DecimalFormat.format(d);
			s += " km";
		} else {
			s += distance.value ;
			s += " m";
		}
		if(time.value<60){
			s+="  "+"\n"+"总时间: "+time.value+"秒";
		}else if((time.value>60||time.value==60)&&time.value<3600){
			int min=(int)(time.value/60);
			int sec=time.value-60*min;
			s+="  "+"\n"+"总时间: "+min+"分"+sec+"秒";
		}else {
			int hour=(int)(time.value/3600);
			int min=(int)((time.value-3600*hour)/60);
			int sec=time.value-3600*hour-60*min;
			
			s+="  "+"\n"+"总时间: "+hour+"小时"+min+"分"+sec+"秒";
		}
		tv1.setText(s);
		//
		// TextView tv2 = (TextView) findViewById(R.id.route_view_toll_num);
		// String s1 = "收费站数:";
		// s1 += psi.lTotalTollGate;
		// tv2.setText(s1);
	}

	public static void showRouteDescription(Activity activity) {
		Intent intent = new Intent(activity, RouteDescriptionActivity.class);
		activity.startActivity(intent);
	}

	public static void showRouteDescription(Activity activity, int i) {
		Intent intent = new Intent(activity, RouteDescriptionActivity.class);
		activity.startActivityForResult(intent, i);
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_view);
		//保留地图原有参数
		
		mapscale = MapAPI.getInstance().getMapScale();
		mapModel = MapAPI.getInstance().getMapView();
		MapAPI.getInstance().setMapView(0);
		isVehicleInCenter = MapAPI.getInstance().isCarInCenter();
		m_CenterInfo = MapAPI.getInstance().getMapCenter();
		DisplayMetrics dm = new DisplayMetrics(); // 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		this.screenWidth = dm.widthPixels;
		this.screenHigth = dm.heightPixels;

		TextView txtTopic = (TextView) findViewById(R.id.txtTopic);
		txtTopic.setText("行程描述");
		ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new BackListener(this, false, false));

//		if (!RouteAPI.getInstance().isRouteValid()) {
//			Log.e(TAG, "has not route");
//			finish();
//			return;
//		} else {
//			addRouteHeader();
//			listView = (ListView) findViewById(R.id.route_description);
//			routedescriptionadapter = new RouteDescriptionAdapter();
//			listView.setAdapter(routedescriptionadapter);
//		}
//		
//
//		
//		Resources resource = this.getResources();
//		int bitmapwidth = BitmapFactory.decodeResource(resource, R.drawable.fromthis).getWidth()*2;
//		width = RouteDescriptionActivity.this.screenWidth - bitmapwidth;
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView adaperview, View view, int i,
//					long l) {
//				 Intent intent = new Intent(RouteDescriptionActivity.this,
//				 CrossingViewActivity.class);
//				 Bundle b = new Bundle();
//				 b.putInt("crossingindex", i);
//				 intent.putExtras(b);
//				 startActivity(intent);
//			}
//		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MapAPI.getInstance().setMapScale(mapscale);
		MapAPI.getInstance().setMapView(mapModel);
		if(this.isVehicleInCenter){
			MapAPI.getInstance().setMapCenter(MapAPI.getInstance().getVehiclePos());
		}else{
		    MapAPI.getInstance().setMapCenter(this.m_CenterInfo);
		}
		
		//以下代码：解决从"路径详情"和"全程概览"横竖屏切换后再跳回到地图主界面时地图下方出现空白的bug.
		MapView m_mapView = AutoNaviMap.getInstance(getApplicationContext()).getMapView();
		int height = Utils.getCurScreenHeight(this);
		int width = Utils.getCurScreenWidth(this);
		m_mapView.destoryMap();
		m_mapView.initMap(width, height);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!RouteAPI.getInstance().isRouteValid()) {
			Log.e(TAG, "has not route");
			finish();
			return;
		} else {
			addRouteHeader();
			listView = (ListView) findViewById(R.id.route_description);
			listView.setFocusable(true);
			listView.setClickable(true);
			
			routedescriptionadapter = new RouteDescriptionAdapter();
			listView.setAdapter(routedescriptionadapter);
		}
		

		
		Resources resource = this.getResources();
		int bitmapwidth = BitmapFactory.decodeResource(resource, R.drawable.common_fromthis).getWidth()*2;
		width = RouteDescriptionActivity.this.screenWidth - bitmapwidth;
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView adaperview, View view, int i,
					long l) {
				 Intent intent = new Intent(RouteDescriptionActivity.this,
				 CrossingViewActivity.class);
				 Bundle b = new Bundle();
				 b.putInt("crossingindex", i);
				 intent.putExtras(b);
				 startActivity(intent);
			}
		});
		listView.setSelectionFromTop(lastPosition, 0);//恢复横竖屏切换前浏览列表时的位置
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(!back){
			lastPosition=listView.getFirstVisiblePosition();//记录横竖屏切换前浏览列表时的位置
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		lastPosition=0;
		back=true;
	}

}
