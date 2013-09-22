package com.mapabc.android.activity.route.track;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.trace.TraceInfo;
import com.mapabc.naviapi.type.Const;

/**
 * 轨迹概览界面
 */
public class TracePreviewActivity extends TraceActivity{
	private static final String TAG="TracePreviewActivity";
	private TraceInfo traceInfo=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		speedBtn.setVisibility(View.INVISIBLE);
		Bundle bundle=getIntent().getBundleExtra("TRACE_BUNDLE");
		
		MapAPI.getInstance().setVehicleShowStatus(true);//隐藏汽车所在图层
		
		if(bundle!=null){
			traceInfo=(TraceInfo)bundle.getSerializable("TRACEINFO");
		}
		if(traceInfo!=null){
			 MapAPI.getInstance().loadLineOverlays(101,1410,"Route_line",traceInfo.fileName,true,1,true);
		}
//		MapAPI.getInstance().setPrewMode(true);
		back_car.setVisibility(View.GONE);
//		back_car.setOnClickListener(onClickListener);
	}
	@Override
	public void onBackPressed() {
		this.finish();
		mapView.goBackCar();
		MapAPI.getInstance().delLayer(101);
		routeLayer.ShowLayer();
		MapAPI.getInstance().setVehicleShowStatus(false);//显示汽车所在图层
//		super.onBackPressed();
//		setButtonGone();
//		MapAPI.getInstance().setPrewMode(false);
	}
	OnClickListener onClickListener= new OnClickListener() {
		public void onClick(View v) {
				mapView.goBackCar();
				setButtonGone();
		}
	};
	
	
}
