package com.mapabc.android.activity.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import com.mapabc.android.activity.utils.ActivityStack;

/**
 * @description 返回键的监听事件
 * @author menglin.cao
 *
 */
public class BackListener implements OnClickListener {
	private ActivityStack activityStack = ActivityStack.newInstance();
	private Activity activity;
	private boolean blMap;
	private boolean blStack;
	public BackListener(Activity activity){
		this(activity, false);
	}
	
	public BackListener(Activity activity,boolean blMap){
		this(activity,blMap,true);
	}
	
	public BackListener(Activity activity,boolean blMap,boolean blStack){
		this.activity = activity;
		this.blMap = blMap;
		this.blStack = blStack;
		if(blStack){
			if(blMap){
				activityStack.pushMap(activity);
			}else{
				activityStack.push(activity);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		if(activity == null){
			return;
		}
		activity.onBackPressed();
		if(!blStack){
			return;
		}
		if(blMap){ 
			activityStack.setBlMapBack(true);
			activityStack.popMap();
		}
//		else{
//			activityStack.setBlMapBack(false);
//			activityStack.pop();
//		}
	}

}
