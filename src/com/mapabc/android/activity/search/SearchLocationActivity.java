package com.mapabc.android.activity.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.MenuActivityFactory;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.search.adapter.SearchLocListAdapter;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.FavoriteAPI;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.favorite.FavoriteInfo;
import com.mapabc.naviapi.favorite.FavoritePageResults;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.PageOptions;

/**
 * @description: 地点查询UI
 * @author menglin.cao 2012-08-23
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchLocationActivity extends Activity implements OnItemClickListener{
	private ListView searchLocationListView;
	private SearchLocListAdapter searchLocAdapter;
	private static final String TAG="SearchLocationActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchlocation_main);
		initTop();
		searchLocAdapter = new SearchLocListAdapter(this);
		searchLocationListView = (ListView) findViewById(R.id.lv_searchlocation);
		searchLocationListView.setAdapter(searchLocAdapter);
		searchLocationListView.setOnItemClickListener(this);
	}
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.menu_locationSearch);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this));
	}
	
	@Override
	public void onBackPressed() {
		try{
			ActivityStack.newInstance().pop();
			finish();
			super.onBackPressed();
			Intent intent = new Intent(Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			if (!MapAPI.getInstance().isCarInCenter()) {
				AutoNaviMap.getInstance(this).getMapView().goBackCar();
			}
		}catch(Exception e){
			
		}
	}
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	@Override
	protected void onDestroy() {
		searchLocAdapter = null;
		searchLocationListView = null;
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.e(TAG, "position:"+position);
		if(position==6){
			final FavoritePageResults pageResults = new FavoritePageResults();
			FavoriteAPI.getInstance().getPageFavorites(new PageOptions(),Const.FAVORITE_HOME, pageResults);
			if(pageResults==null||pageResults.Favorites==null||pageResults.Favorites.length<1){
				Utils.showTipInfo(this, R.string.searchlocationactivity_has_no_homepostion);
				return ;
			}
			FavoriteInfo poiInfo  = pageResults.Favorites[0];
			SearchResultInfo clspoiInfo =new SearchResultInfo();
			Utils.getClsRearchResultInfo(poiInfo, clspoiInfo);
			Bundle extra = new Bundle();
			extra.putInt(Constants.INTENT_ACTION, Constants.INTENT_TYPE_SETENDPOINT);
			extra.putSerializable(Constants.POI_DATA, clspoiInfo);
			Intent intent = new Intent(Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			intent.putExtras(extra);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			ActivityStack.newInstance().cleanHistory();
			return ;
		}
		String intent = MenuActivityFactory.getSearchActivityIntent(position);
//		if(intent != null){
//			if(position==3){
//				int x=
//				(int)(MapAPI.getInstance().getVehiclePos().fX*(3600000));
//				int y=
//				(int)(MapAPI.getInstance().getVehiclePos().fY*(3600000));
//				SearchAPI.getInstance().setAroundSearchCenter(x,y);
//				Log.e(TAG, "=======x======="+x);
//				Log.e(TAG, "=======y======="+y);
//			}
			Intent target = new Intent(intent);
			target.putExtra(Constants.SEARCHTYPE_KEYWORD, position);
			startActivity(target);
//		}
		
	}
	

}
