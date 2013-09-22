package com.mapabc.android.activity.route.track;

import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.trace.TraceInfo;
import com.mapabc.naviapi.type.Const;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 轨迹回放界面
 */
public class TracePlayBackActivity extends TraceActivity{
	private static final int CAR_LAY = 100;// 汽车所在图层ID
	private static final int CARANDCENTER_OVERLAY_ID=20002;//汽车和中心点连线的ID
	private static final int CARANDCENTER_OVERLAY_TYPE = Const.MAP_OVERLAY_LINE;// 地图中心点连线的类型

	private static final String TAG="TracePlayBackActivity";
	private TraceInfo traceInfo=null;
	private MyTraceListener traceListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		speedBtn.setVisibility(View.VISIBLE);
		if(!MapAPI.getInstance().isCarInCenter()){
			back_car.setVisibility(View.VISIBLE);
		}
		traceListener=new MyTraceListener(this);
		Bundle bundle=getIntent().getBundleExtra("TRACE_BUNDLE");
		if(bundle!=null){
			traceInfo=(TraceInfo)bundle.getSerializable("TRACEINFO");
		}
		if(traceInfo!=null){
			MapAPI.getInstance().loadLineOverlays(101,1410,"Route_line",traceInfo.fileName,true,1,true);
		}
		TraceAPI.getInstance().setCallBack(traceListener);
		int speed=SettingForTrackTools.getSpeedValue(this);
		TraceAPI.getInstance().startTracePlay(traceInfo.id, speed);
		myapp.isTracePlay=true;
		back_car.setOnClickListener(onClickListener);
	}
	@Override
	public void onBackPressed() {
		if(MyTraceListener.TRACE_PLAY_MODE==Const.TRACE_PLAY_PAUSE){
			TraceAPI.getInstance().pauseOrResumeTracePlay(false);
			mapView.goBackCar();
			setButtonGone();
		}else {
			TraceAPI.getInstance().stopTracePlay();
			super.onBackPressed();
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
		}else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			
		}
	}
	
	
	OnClickListener onClickListener= new OnClickListener() {
		public void onClick(View v) {
			if(MyTraceListener.TRACE_PLAY_MODE==Const.TRACE_PLAY_PAUSE){
				TraceAPI.getInstance().pauseOrResumeTracePlay(false);
				mapView.goBackCar();
				setButtonGone();
			}else {
				TraceAPI.getInstance().stopTracePlay();
			}
		}
	};
	@Override
	protected void onPause() {
		super.onPause();
		if (TrackManagerActivity.isRecording) {
			TraceAPI.getInstance().restartTraceRecord(
					SettingForTrackTools.getTrackRule(TracePlayBackActivity.this), true);
		}
	}
	

}
