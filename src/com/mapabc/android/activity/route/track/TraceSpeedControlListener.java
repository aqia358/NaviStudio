package com.mapabc.android.activity.route.track;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import com.mapabc.android.activity.R;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.type.Const;

public class TraceSpeedControlListener implements OnTouchListener{
	private TraceActivity mActivity;
	private ImageButton speedBtn;
	private int SPEED=0;
	public TraceSpeedControlListener(TraceActivity activity){
		this.mActivity=activity;
		this.speedBtn=mActivity.speedBtn;
		setSpeedMode();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			setSpeedDownBackground();
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			setSpeedUpBackground();
		}
		return true;
	}
	private void setSpeedMode(){
		int speed=SettingForTrackTools.getSpeedValue(mActivity);
		switch (speed) {
		case SettingForTrackTools.LOW_SPEED:
			speedBtn.setBackgroundResource(R.drawable.one_x2);
			break;
		case SettingForTrackTools.MID_SPEED:
			speedBtn.setBackgroundResource(R.drawable.two_x2);
			break;
		case SettingForTrackTools.HIGH_SPEED:
			speedBtn.setBackgroundResource(R.drawable.three_x2);
			break;

		default:
			break;
		}
	}
	private void setSpeedUpBackground(){
		int speed=SettingForTrackTools.getSpeedValue(mActivity);
		switch (speed) {
		case SettingForTrackTools.LOW_SPEED:
			speedBtn.setBackgroundResource(R.drawable.one_x2);
			break;
		case SettingForTrackTools.MID_SPEED:
			speedBtn.setBackgroundResource(R.drawable.two_x2);
			break;
		case SettingForTrackTools.HIGH_SPEED:
			speedBtn.setBackgroundResource(R.drawable.three_x2);
			break;

		default:
			break;
		}
	}
	private void setSpeedDownBackground(){
		int speed=SettingForTrackTools.getSpeedValue(mActivity);
		switch (speed) {
		case SettingForTrackTools.LOW_SPEED:
			speedBtn.setBackgroundResource(R.drawable.one_x1);
			SettingForTrackTools.setSpeedValue(mActivity, SettingForTrackTools.MID_SPEED);
			TraceAPI.getInstance().setTracePlaySpeed(Const.TRACE_PLAY_SPEED_NORMAL);
			break;
		case SettingForTrackTools.MID_SPEED:
			speedBtn.setBackgroundResource(R.drawable.two_x1);
			SettingForTrackTools.setSpeedValue(mActivity, SettingForTrackTools.HIGH_SPEED);
			TraceAPI.getInstance().setTracePlaySpeed(Const.TRACE_PLAY_SPEED_HIGH);
			break;
		case SettingForTrackTools.HIGH_SPEED:
			speedBtn.setBackgroundResource(R.drawable.three_x1);
			SettingForTrackTools.setSpeedValue(mActivity, SettingForTrackTools.LOW_SPEED);
			TraceAPI.getInstance().setTracePlaySpeed(Const.TRACE_PLAY_SPEED_LOW);
			break;
		
		}
	}

}
