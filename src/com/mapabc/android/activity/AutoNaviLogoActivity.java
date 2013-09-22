package com.mapabc.android.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapEngineManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @description: 系统logo显示
 * @author: zhuhao 2012-08-09
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class AutoNaviLogoActivity extends Activity {
	private final String TAG = "AutoNaviLogoActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Utils.isLand(this)){
			setContentView(R.layout.navilogo_logoscreen_land);
		}else{
			setContentView(R.layout.navilogo_logoscreen);
		}
		playLoadingAnimtion();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			public void run() {
				AsyncForward asyncForward = new AsyncForward(AutoNaviLogoActivity.this,Constants.ACTIVITY_NAVISTUDIOACTIVITY);
				asyncForward.execute();
			}
		}, 1000);
	}
	
	private void playLoadingAnimtion(){
		final ImageView imgLoading = (ImageView) findViewById(R.id.iv_imgLoading);
		ViewGroup.MarginLayoutParams marginParams = (MarginLayoutParams) imgLoading.getLayoutParams();
		marginParams.topMargin = (Utils.getCurScreenHeight(this)/2-50);
		imgLoading.setLayoutParams(marginParams);
		TextView txtLoading = (TextView) findViewById(R.id.tv_txtLoading);
		ViewGroup.MarginLayoutParams marginTxtParams = (MarginLayoutParams) txtLoading.getLayoutParams();
		marginTxtParams.topMargin = (Utils.getCurScreenHeight(this)/2-16);
		txtLoading.setLayoutParams(marginTxtParams);
		
		imgLoading.setBackgroundResource(R.drawable.navilogo_loading_ani);
		final AnimationDrawable animation = (AnimationDrawable) imgLoading.getBackground();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				animation.start();
			}
		}, 30);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.navilogo_logoscreen_land);
		}else{
			setContentView(R.layout.navilogo_logoscreen);
		}
		playLoadingAnimtion();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		System.exit(0);
	}

}
