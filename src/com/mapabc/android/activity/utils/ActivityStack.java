package com.mapabc.android.activity.utils;

import java.util.Iterator;
import java.util.LinkedList;

import com.mapabc.android.activity.log.Logger;

import android.app.Activity;
import android.util.Log;

/**
 * @description: ACTIVITY堆栈
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class ActivityStack {
	private LinkedList<Activity> linkedStack = new LinkedList<Activity>();
	private static ActivityStack activityStack;
	private LinkedList<Activity> mapStack = new LinkedList<Activity>();
//	private Activity mapactivity;
	private int mapdeep;//地图在linkedStack中的深度
	private boolean blMapBack;//是否从地图返回
	private ActivityStack(){}
	
	public static ActivityStack newInstance(){
		if(activityStack == null){
			synchronized (ActivityStack.class) {
				if(activityStack == null){
					activityStack = new ActivityStack();
				}
			}
		}
		return activityStack;
	}
	
	public void push(Activity activity){
		linkedStack.addFirst(activity);
	}
	
	public void pushMap(Activity activity){
//		mapactivity = activity;
		finishAllMap();
		mapStack.addFirst(activity);
		mapdeep = linkedStack.size();
		Log.e("ActivityStack", "mapdeep:"+mapdeep);
	}
	
	public void cleanHistory(){
		Iterator<Activity> iter = linkedStack.iterator();
		while(iter.hasNext()){
			Activity act = iter.next();
			if(act == null){
				continue;
			}
			
			act.finish();
			iter.remove();
		}
	}
	
	public void finishAllExcludeMap(){
		if(blMapBack){
			return;
		}
		Iterator<Activity> iter = linkedStack.iterator();
		int count = 0;
		boolean blRemovable = false;
		while(iter.hasNext()){
			Activity act = iter.next();
			if(act == null){
				continue;
			}
			
			if((linkedStack.size()-count) <= mapdeep){
				act.finish();
				iter.remove();
				blRemovable = true;
			}
			count++;
		}
		if(blRemovable){
			mapdeep =0;
		}
		
	}
	
	public void finishAllMap(){
		Iterator<Activity> iter = mapStack.iterator();
		while(iter.hasNext()){
			Activity act = iter.next();
			if(act == null){
				Log.e("ActivityStack", "mapStack，continue");
				continue;
			}
			Log.e("ActivityStack", "mapStack，act.finish();");
			act.finish();
			iter.remove();
		}
	}
	
	public void finishAll(){
		cleanHistory();
		finishAllMap();
	}
	
	public Activity pop(){
		Logger.e("ActivityStack", "SIZE:"+linkedStack.size());
		Activity a =linkedStack.poll();
		Logger.e("ActivityStack", "name:"+a.getLocalClassName());
		return a;
	}
	
	public Activity popMap(){
		return mapStack.poll();
	}

	public boolean isBlMapBack() {
		return blMapBack;
	}

	public void setBlMapBack(boolean blMapBack) {
		this.blMapBack = blMapBack;
	}
	
	
}
