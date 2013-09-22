package com.mapabc.android.activity;

import java.util.Iterator;

import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.listener.DisPatchInfo;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * @description: 星历图Activity
 * @author: changbao.wang 2011-11-22
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class GPSInfoActivity extends BaseActivity {
	private static final String TAG = "GPSInfoActivity";
    private GPSInfoView gpsinfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpsinfoactivity_layout);
		TextView txtTopic = (TextView) findViewById(R.id.txtTopic);
		txtTopic.setText(R.string.gpsinfo_title);
		gpsinfo =(GPSInfoView) this.findViewById(R.id.gpsinfo_view);
		ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new BackListener(this,false,false));
		if(NaviControl.getInstance().gpsInfo!=null){
			gpsinfo.pstGPSInfo = NaviControl.getInstance().gpsInfo;
			gpsinfo.getGPSDesc();
//			gpsinfo.invalidate();
		}
		Log.e(TAG, "onCreate");
	}
	/**
	 * 删除卫星状态监听
	 */
	@Override
	protected void onPause() {
		super.onPause();	
//		NaviControl.getInstance().setS_Handler(null);
		DisPatchInfo.getInstance().removeGpsInfoListener("GPSInfoView");
	}

	
	/**
	 * 增加卫星状态信息监听
	 */
	@Override
	protected void onResume() {
		super.onResume();
//		NaviControl.getInstance().setS_Handler(gpsinfo.mHandler);
		DisPatchInfo.getInstance().addGpsInfoListener("GPSInfoView",gpsinfo);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
	}
	
}
