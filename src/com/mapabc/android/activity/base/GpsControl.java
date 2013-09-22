/**
 * 
 */
package com.mapabc.android.activity.base;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.listener.DisPatchInfo;
import com.mapabc.android.activity.listener.ReceiveInfo;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.route.GPSRouteInfo;
import com.mapabc.naviapi.route.GpsInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * desciption:Gps的控制器
 * 
 */
public class GpsControl implements OnClickListener ,ReceiveInfo{

	private NaviStudioActivity naviStudioActivity;
	public ImageButton gpsstate;
	public MapView mapView;
	private static final int UPDATEGPSSTATUS = 105;// 更新GPS信息
	private static final int NOGPS=108; //刷新
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATEGPSSTATUS:
//				if (naviStudioActivity.isThisView) {
					GpsInfo pstGPSInfo = (GpsInfo) msg.obj;
					int s_count = 0;
					if (pstGPSInfo != null) {
						s_count = (int) pstGPSInfo.useableSatNum / 2;
					}
//					Logger.e("GpsControl", "GpsControl被触发了,s_count:"+s_count);
					if (NaviControl.mgpsStatus == 3) {
						switch (s_count) {
//						case 0:
//							gpsstate.setImageResource(R.drawable.navistudio_gps_0_x);
//							break;
						case 1:
							gpsstate.setImageResource(R.drawable.navistudio_gps_a_1_x);
							break;
						case 2:
							gpsstate.setImageResource(R.drawable.navistudio_gps_a_2_x);
							break;
						case 3:
							gpsstate.setImageResource(R.drawable.navistudio_gps_a_3_x);
							break;
						case 4:
							gpsstate.setImageResource(R.drawable.navistudio_gps_a_4_x);
							break;
						default:
							gpsstate.setImageResource(R.drawable.navistudio_gps_a_4_x);
							break;
						}
					} else {
						gpsstate.setImageResource(R.drawable.navistudio_gps_0_x);
					}
//				}
				
				break;
			case NOGPS:
				gpsstate.setImageResource(R.drawable.navistudio_gps_0_x);
				break;
			default:
				break;
			}
			
			
		};
	};

	@Override
	public void DoGpsInfo(GpsInfo gpsInfo) {
		// TODO Auto-generated method stub
		Message msg=Message.obtain();
		msg.what=UPDATEGPSSTATUS;
		msg.obj=gpsInfo;
		handler.sendMessage(msg);
		
	}
	
	
	public GpsControl(NaviStudioActivity activity, MapView mapView) {
		this.naviStudioActivity = activity;
		gpsstate = this.naviStudioActivity.gpsstate;
		this.mapView=mapView;
		DisPatchInfo.getInstance().addGpsInfoListener("GpsControl", this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.equals(gpsstate)){
			gpsInfo();
		}
	}
		
	private void gpsInfo(){
		Intent in = new Intent(Constants.ACTIVITY_GPSINFO);
		this.naviStudioActivity.startActivity(in);
	}


	@Override
	public void DoRouteInfo(GPSRouteInfo routeInfo, boolean gpsNavi) {
		// TODO Auto-generated method stub
		
	}
	
	//在gps断开时刷新gps状态
	public void refreshStatus() {
		if (NaviControl.mgpsStatus!=3) {
			Message msg=Message.obtain();
			msg.what=NOGPS;
			handler.sendMessage(msg);
		}
	
	}
		

}




