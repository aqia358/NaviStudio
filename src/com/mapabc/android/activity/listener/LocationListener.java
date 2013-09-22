package com.mapabc.android.activity.listener;


import android.os.Bundle;
import android.view.View;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.naviapi.search.SearchResultInfo;
/**
 * @description: POI显示页面按钮监听器
 * @author menglin.cao 2012-08-28
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class LocationListener extends POIListener {
	private SearchResultInfo poiInfo;
	
	public LocationListener(SearchResultInfo poi) {
		this.poiInfo = poi;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		Bundle extra = new Bundle();
		extra.putInt(Constants.INTENT_ACTION, Constants.INTENT_TYPE_LOOKPOI);
		extra.putSerializable(Constants.POI_DATA, poiInfo);
		forward(v.getContext(), extra);
	}

}
