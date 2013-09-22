package com.mapabc.android.activity.route.track;

import com.mapabc.android.activity.base.NaviStudioApplication;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.listener.MapListener;
import com.mapabc.naviapi.map.DayOrNightControl;
import com.mapabc.naviapi.type.Const;

/**
 * desciption:
 * 
 */
public class TraceMapListener implements MapListener {

	private TraceActivity mTraceActivity;
	public TraceMapListener(TraceActivity activity){
		super();
		this.mTraceActivity=activity;
	}

	@Override
	public void onMapStatusChanged(int paramInt) {
		// TODO Auto-generated method stub
		switch (paramInt) {
		case MapListener.MAP_STATUS_SCALE:
			mTraceActivity.zoomControl.initZoom();
			if(mTraceActivity!=null){
				mTraceActivity.setScaleText();
			}
			break;
		case MapListener.MAP_STATUS_MOVE:
			TraceAPI.getInstance().pauseOrResumeTracePlay(true);
			if(MyTraceListener.TRACE_PLAY_MODE==Const.TRACE_PLAY_STOP){
				mTraceActivity.myapp.isTracePlay=false;
			}
			if(mTraceActivity instanceof TracePreviewActivity){
				
			}else {
				mTraceActivity.setButtonVisible();
			}
			break;
		case MapListener.MAP_STATUS_CAR:
			mTraceActivity.updateUIStyle(DayOrNightControl.mdayOrNight);
			break;
		case MapListener.MAP_STATUS_ANGLE:
			break;
		default:
			break;
		}
	}


}
