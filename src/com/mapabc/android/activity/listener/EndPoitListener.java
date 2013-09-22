package com.mapabc.android.activity.listener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.naviapi.search.SearchResultInfo;

/**
 * @description: POI显示页面终点按钮监听器
 * @author menglin.cao 2012-08-30
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class EndPoitListener extends POIListener {
	private SearchResultInfo poiInfo;
	
	public EndPoitListener(SearchResultInfo poi) {
		this.poiInfo = poi;
	}


	@Override
	public void onClick(View v) {

		Bundle extra = new Bundle();
		extra.putInt(Constants.INTENT_ACTION, Constants.INTENT_TYPE_SETENDPOINT);
		extra.putSerializable(Constants.POI_DATA, poiInfo);
		Intent intent = new Intent(Constants.ACTIVITY_NAVISTUDIOACTIVITY);
		intent.putExtras(extra);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		v.getContext().startActivity(intent);
		ActivityStack.newInstance().cleanHistory();
	}

}