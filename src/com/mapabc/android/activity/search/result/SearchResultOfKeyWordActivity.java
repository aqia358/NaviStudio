package com.mapabc.android.activity.search.result;

import android.os.Bundle;
import android.util.Log;

/**
 * @description: 关键字查询结果UI
 * @author menglin.cao 2012-08-29
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchResultOfKeyWordActivity extends SearchPOIResultActivity {

	private static final String TAG = "SearchResultofKeyWordActivity";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.startSynchronizedLoad();
		Log.e(TAG, "=====TaskId====="+getTaskId());
	}
	/* (non-Javadoc)
	 * @see com.mapabc.android.activity.menu.SearchPOIResultActivity#searchPOI()
	 */
	@Override
	protected void searchPOI() {

	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e(TAG, "=====onRestart=====");
		Log.e(TAG, "=====TaskId====="+getTaskId());
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "=====onDestroy=====");
		Log.e(TAG, "=====TaskId====="+getTaskId());
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "=====onStop=====");
		Log.e(TAG, "=====TaskId====="+getTaskId());
	}
	
	
}
