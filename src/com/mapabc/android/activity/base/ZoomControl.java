package com.mapabc.android.activity.base;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.mapabc.android.activity.NaviResultListActivity;
import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.route.track.TraceActivity;
import com.mapabc.android.activity.route.track.TracePreviewActivity;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.map.DayOrNightControl;

/**
 * 放大缩小控件
 */
public class ZoomControl implements OnClickListener {
	private NaviStudioActivity naviStudioActivity;
	private TraceActivity tracePreviewActivity;
	private NaviResultListActivity naviResultListActivity;
	
	public ImageButton zoomin, zoomout;
	public MapView mapView;
	
	public ZoomControl(NaviStudioActivity activity, MapView mapView) {
		this.naviStudioActivity = activity;
		zoomin = this.naviStudioActivity.zoomin;
		zoomout = this.naviStudioActivity.zoomout;
		this.mapView=mapView;
		initZoom();
	}
	public ZoomControl(TraceActivity activity, MapView mapView) {
		this.tracePreviewActivity = activity;
		zoomin = this.tracePreviewActivity.zoomin;
		zoomout = this.tracePreviewActivity.zoomout;
		this.mapView=mapView;
		initZoom();
	}
	public ZoomControl(NaviResultListActivity activity, MapView mapView) {
		this.naviResultListActivity = activity;
		zoomin = this.naviResultListActivity.zoomin;
		zoomout = this.naviResultListActivity.zoomout;
		this.mapView=mapView;
		initZoom();
	}

	public void initZoom() {
		if (DayOrNightControl.mdayOrNight) {
			if (MapAPI.getInstance().canZoomIn()) {
				zoomin.setEnabled(true);
				zoomin.setBackgroundResource(R.drawable.navistudio_dec_day);
			} else {
				zoomin.setEnabled(false);
				zoomin.setBackgroundResource(R.drawable.navistudio_dec_day_disable);
			}
			if (MapAPI.getInstance().canZoomOut()) {
				zoomout.setEnabled(true);
				zoomout.setBackgroundResource(R.drawable.navistudio_inc_day);
			} else {
				zoomout.setEnabled(false);
				zoomout.setBackgroundResource(R.drawable.navistudio_inc_day_disable);
			}
		} else {
			if (MapAPI.getInstance().canZoomOut()) {
				zoomout.setEnabled(true);
				zoomout.setBackgroundResource(R.drawable.navistudio_inc_night);
			} else {
				zoomout.setEnabled(false);
				zoomout.setBackgroundResource(R.drawable.navistudio_inc_night_disable);
			}
			if (MapAPI.getInstance().canZoomIn()) {
				zoomin.setEnabled(true);
				zoomin.setBackgroundResource(R.drawable.navistudio_dec_night);
			} else {
				zoomin.setEnabled(false);
				zoomin.setBackgroundResource(R.drawable.navistudio_dec_night_disable);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(zoomin)){
			MapAPI.getInstance().zoomIn();
			mapView.invalidate();
		}else if(v.equals(zoomout)){
			MapAPI.getInstance().zoomOut();
			mapView.invalidate();
		}
	}
	public void setButtonVisible(){
		if(naviStudioActivity.zoomLayout.getVisibility()==View.GONE){
			naviStudioActivity.zoomLayout.startAnimation(ZoomControl.this.naviStudioActivity.mShowAction);
			naviStudioActivity.zoomLayout.setVisibility(View.VISIBLE);
		}
	}
	public void setButtonGone(){
		if(naviStudioActivity.zoomLayout.getVisibility()==View.VISIBLE){
			naviStudioActivity.zoomLayout.startAnimation(ZoomControl.this.naviStudioActivity.mHiddenAction);
			naviStudioActivity.zoomLayout.setVisibility(View.GONE);
		}
	}

	
}
