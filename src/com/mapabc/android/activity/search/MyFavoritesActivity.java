package com.mapabc.android.activity.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.log.Logger;
import com.mapabc.android.activity.search.adapter.MyFavoritesAdapter;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.FavoriteAPI;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.favorite.FavoriteInfo;
import com.mapabc.naviapi.favorite.FavoritePageResults;
import com.mapabc.naviapi.search.AdminInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.PageOptions;
import com.mapabc.naviapi.utils.AndroidUtils;
/**
 * 我的收藏夹
 * @author menglin.cao 2012-08-24
 *
 */
public class MyFavoritesActivity extends BaseActivity {
	private static final String TAG = "MyFavoritesActivity";
	private ExpandableListView favoritesListExpandableListView;
	MyFavoritesAdapter mAdapter;
	List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String, Object>>>();
	List<Map<String, Object>> groupData = new ArrayList<Map<String, Object>>();
	NSLonLat mlonlat = (NSLonLat) MapAPI.getInstance().getVehiclePos();
	private static  int idicateWidth=50;//下拉按钮的宽度
	private int searchType = 4;//4：我的收藏，5：历史目的地
	int ind = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		searchType = extras.getInt(Constants.SEARCHTYPE_KEYWORD);
		setContentView(R.layout.myfavorites_main);
		initTop();
		favoritesListExpandableListView = (ExpandableListView) findViewById(R.id.elv_favorites_list);
		Drawable drawable = getResources().getDrawable(R.drawable.myfavoritesactivity_list_bgcolor);
		this.getWindow().setBackgroundDrawable(drawable);
		idicateWidth = Utils.getIndicatorPosition(this);
		init();
	}
    private void init(){
    	int count = FavoriteAPI.getInstance().getFavoriteCount(searchType);
		Log.e(TAG, "COUNT____:"+count);
    	if(!getPOIData(1)){
			Toast toast = Toast.makeText(MyFavoritesActivity.this,
					MyFavoritesActivity.this.getText(R.string.common_no_data),
					2000);
			toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
			toast.show();
			this.finish();
			return ;
		}
		mAdapter = new MyFavoritesAdapter(MyFavoritesActivity.this, groupData,
				R.layout.myfavorites_grouplayout, new String[] {
						"list_mf_poi_position", "list_poi_name",
						"list_poi_address", "list_route_length" }, new int[] {
						R.id.iv_mf_poi_position},
				childData, R.layout.myfavorites_childlayout,new String[] {
						"tv_child_tel", "tv_child_address", "tv_child_area" },
				new int[] { R.id.tv_child_tel, R.id.tv_child_address,
						R.id.tv_child_area },searchType);
		favoritesListExpandableListView.setAdapter(mAdapter);
		setIndicatorPosition();
    }
	/**
	 * 调用底层接口获取收藏夹内POI，计算当前车位点与该POI的距离
	 */
//    private FavoritePageResults pageResults;
	private boolean getPOIData(int pageNo) {
		try {
			FavoritePageResults pageResults = new FavoritePageResults();
			PageOptions pageOptions = new PageOptions();
			pageOptions.pageNo = pageNo;
			int type = 0;
			if(searchType==Constants.SEARCH_MYFAVORITES){//收藏夹
				type = Const.FAVORITE_HAUNT;
			}else if(searchType==Constants.SEARCH_HISTORYARRIVE){//目的地
				type = Const.FAVORITE_HISTORYDES;
			}
			FavoriteAPI.getInstance().getPageFavorites(pageOptions,type, pageResults);
			if(pageResults==null||pageResults.Favorites==null||pageResults.Favorites.length<1){
				Log.e(TAG, "poi is null , go back");
				return false;
			}
			putInData(pageResults);
            if(pageNo == 2){
            	return true;
            }
			if(FavoriteAPI.getInstance().getFavoriteCount(type)>100){
				getPOIData(2);
			}
			return true;
		} catch (Exception ex) {
			Log.e(TAG,"gethistory DATA ERROR",ex);
			return false;
		}
	}
    private void putInData(FavoritePageResults pageResults){
    	
		for (FavoriteInfo data: pageResults.Favorites) {
			Log.e(TAG, "ID:"+data.id+",NAME:"+data.name);
			SearchResultInfo poi = new SearchResultInfo();
			Utils.getClsRearchResultInfo(data, poi);
			String district = Utils.getDistrictByAdCode(poi, MyFavoritesActivity.this);
			
			Map<String, Object> curGroupMap = new HashMap<String, Object>();

			groupData.add(curGroupMap);
			float distance = UtilAPI.getInstance().calculateDis(MyFavoritesActivity.this.mlonlat.x, MyFavoritesActivity.this.mlonlat.y
					,data.longitude,data.latitude);
			String dis="";
			if(distance>1000){
			   dis =String.format("%5.1f", distance/1000.00f)+ "km";
			}else{
				dis =String.format("%5.1f", distance)+"m";
			}
			curGroupMap.put("list_mf_poi_position",
					R.drawable.common_list_icon);
			curGroupMap.put("list_poi_name", data.name);
			curGroupMap.put("list_poi_address",district);
			curGroupMap.put("list_route_length", dis);
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			{
				Map<String, Object> curChildMap = new HashMap<String, Object>();
				children.add(curChildMap);
				curChildMap.put("tv_child_tel", data.telephone);
				curChildMap.put("tv_child_address", data.address);
				String array_town [] = district.split("，");
				if(array_town.length==2){
				curChildMap.put("tv_child_area", district.split("，")[1]);
				}else{
					curChildMap.put("tv_child_area", district.split("，")[0]);
				}
				curChildMap.put(ind + "", data);
			}
			childData.add(children);
			ind+=1;
		}
    }
	/**
	 * 设置显示器位置
	 */
	public void setIndicatorPosition() {

		DisplayMetrics dm = new DisplayMetrics(); // 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		int screenWidth = dm.widthPixels;
		favoritesListExpandableListView.setIndicatorBounds(screenWidth - idicateWidth, screenWidth);
	}
	public boolean onPrepareOptionsMenu(Menu menu) {   
		Log.e(TAG, "onPrepareOptionsMenu");
		menu.clear();
		menu.add(0, 0, 0, this.getResources().getString(R.string.menu_deleteAll)).setIcon(R.drawable.common_menu_icon_clear);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        
        String clear = this.getResources().getString(R.string.menu_deleteAll);
        if(item.getTitle().toString().equals(clear)){
        	AlertDialog.Builder mBuilder=new AlertDialog.Builder(MyFavoritesActivity.this);
			 mBuilder.setTitle(this.getResources().getString(R.string.common_tip));
			 mBuilder.setMessage(this.getResources().getString(R.string.myfavorites_delete_poi_message));
			  	mBuilder.setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
							
						if(FavoriteAPI.getInstance().delAllFavorites(searchType)==1){
							Toast toast = Toast.makeText(MyFavoritesActivity.this,
									MyFavoritesActivity.this.getResources().getString(R.string.myfavorites_delete_success),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
							Intent it = new Intent(Constants.ACTIVITY_SEARCH_SEARCHLOCATION);
							MyFavoritesActivity.this.startActivity(it);
							MyFavoritesActivity.this.finish();
						}else{
							Toast toast = Toast.makeText(MyFavoritesActivity.this,
									MyFavoritesActivity.this.getResources().getString(R.string.myfavorites_delete_fail),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
						}
						
					}
				}).setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
			  	mBuilder.show();
        	return false;
        }
        return true;
	}

	@Override
	public void onBackPressed() {
		Activity a = ActivityStack.newInstance().pop();
		if(a instanceof MyFavoritesActivity){
			Logger.e(TAG,"类型一致");
		}else{
			Logger.e(TAG,"类型不一致");
		}
		if(a.equals(this)){
			Logger.e(TAG,"指针一致");
		}else{
			Logger.e(TAG,"指针不一致");
			a.finish();
		}
		favoritesListExpandableListView = null;
		this.finish();
		super.onBackPressed();
	}
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//		Log.e(TAG,"__onNewIntent__");
//		if(mAdapter!=null){
//		  mAdapter.reflashPOIData();
//		  Log.e(TAG,"__onNewIntent1__");
//		}
//	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(mAdapter!=null){
			  mAdapter.reflashPOIData();
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		if(searchType==4){
			topicTextView.setText(this.getResources().getStringArray(R.array.searchlocitems)[4]);
		}else if(searchType==5){
			topicTextView.setText(this.getResources().getStringArray(R.array.searchlocitems)[5]);
		}
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this));
	}
}
