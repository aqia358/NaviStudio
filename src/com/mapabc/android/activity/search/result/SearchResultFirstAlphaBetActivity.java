package com.mapabc.android.activity.search.result;

import android.os.Bundle;

/**
 * @description: 首字母查询结果UI
 * @author menglin.cao 2012-08-27
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchResultFirstAlphaBetActivity extends
		SearchPOIResultActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.startSynchronizedLoad();
	}
	@Override
	protected void searchPOI() {
		
	}

}