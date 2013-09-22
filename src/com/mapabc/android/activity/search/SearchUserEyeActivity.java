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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.search.adapter.UserEyeAdapter;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.UserEyeAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.PageOptions;
import com.mapabc.naviapi.type.UserEventPot;
import com.mapabc.naviapi.ue.UserEventPotPageResults;
import com.mapabc.naviapi.utils.AndroidUtils;

/**
 * 电子眼
 * @author menglin.cao 2012-09-11
 *
 */
public class SearchUserEyeActivity extends Activity {
	private static final String TAG = "SearchUserEyeActivity";
	private ExpandableListView userEyesListExpandableListView;
	UserEyeAdapter mAdapter;
	List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String, Object>>>();
	List<Map<String, Object>> groupData =  new ArrayList<Map<String, Object>>();
	NSLonLat mlonlat = (NSLonLat) MapAPI.getInstance().getVehiclePos();
	private static  int idicateWidth=50;//下拉按钮的宽度
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usereye_main);
		initTop();
		userEyesListExpandableListView = (ExpandableListView) findViewById(R.id.elv_usereye_list);
		Drawable drawable = getResources().getDrawable(R.drawable.myfavoritesactivity_list_bgcolor);
		this.getWindow().setBackgroundDrawable(drawable);
		
		int screenLevel=AndroidUtils.checkScreenResolution(this);
		idicateWidth = Utils.getIndicatorPosition(this);
		init();
	}
    private void init(){
    	if(!getPOIData(1)){
			Utils.showTipInfo(SearchUserEyeActivity.this, R.string.common_no_data);
			this.finish();
			return ;
		}
    	Log.e(TAG, "_____IND_____:"+ind+"_____COUNT:"+UserEyeAPI.getInstance().getUserEyeCount());
		mAdapter = new UserEyeAdapter(SearchUserEyeActivity.this, groupData,
				R.layout.myfavorites_grouplayout, new String[] {
						"list_mf_poi_position", "list_poi_name",
						"list_poi_address", "list_route_length" }, new int[] {
						R.id.iv_mf_poi_position},
				childData, R.layout.myfavorites_childlayout,new String[] {
						"tv_child_tel", "tv_child_address","tv_child_direction", "tv_child_area" },
				new int[] { R.id.tv_child_tel, R.id.tv_child_address, R.id.tv_child_direction,
						R.id.tv_child_area });
		userEyesListExpandableListView.setAdapter(mAdapter);
		setIndicatorPosition();
    }
	/**
	 * 调用底层接口获取收藏夹内POI，计算当前车位点与该POI的距离
	 */
    int pageSize = 1;
    int ind = 0;
    UserEventPotPageResults pageResults=null;
	private boolean getPOIData(int pageNo) {
		try {
			pageResults = new UserEventPotPageResults();
			PageOptions pageOptions = new PageOptions();
			pageOptions.pageNo = pageNo;
			UserEyeAPI.getInstance().getPageUserEyes(pageOptions, pageResults);
			if(pageResults==null||pageResults.userEyes==null||pageResults.userEyes.length<1){
				Log.e(TAG, "poi is null , go back");
				return false;
			}
				{
					for (UserEventPot data: pageResults.userEyes) {
						SearchResultInfo poi = new SearchResultInfo();
						poi.lat = data.latitude;
						poi.lon = data.longitude;
						poi.name = data.name;
						String district = Utils.getDistrictByAdCode(poi, SearchUserEyeActivity.this);
            			Log.e(TAG, "====district====="+district);
						Map<String, Object> curGroupMap = new HashMap<String, Object>();
						groupData.add(curGroupMap);
						float distance = UtilAPI.getInstance().calculateDis(SearchUserEyeActivity.this.mlonlat.x, SearchUserEyeActivity.this.mlonlat.y
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
							curChildMap.put("tv_child_tel", data.type);
							curChildMap.put("tv_child_address", data.limitSpeed);
							curChildMap.put("tv_child_direction", data.angle);
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
				if(pageNo<pageResults.pageCount){
					getPOIData(pageNo+1);
				}
			return true;
		} catch (Exception ex) {
			Log.e(TAG,"gethistory DATA ERROR",ex);
			return false;
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
		userEyesListExpandableListView.setIndicatorBounds(screenWidth - idicateWidth, screenWidth);
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
        	AlertDialog.Builder mBuilder=new AlertDialog.Builder(SearchUserEyeActivity.this);
			 mBuilder.setTitle(this.getResources().getString(R.string.common_tip));
			 mBuilder.setMessage(this.getResources().getString(R.string.myfavorites_delete_poi_message));
			  	mBuilder.setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
							
						if(UserEyeAPI.getInstance().delAllUserEyes()==1){
							Toast toast = Toast.makeText(SearchUserEyeActivity.this,
									SearchUserEyeActivity.this.getResources().getString(R.string.myfavorites_delete_success),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
							Intent it = new Intent(Constants.ACTIVITY_SEARCH_SEARCHLOCATION);
							SearchUserEyeActivity.this.startActivity(it);
							SearchUserEyeActivity.this.finish();
						}else{
							Toast toast = Toast.makeText(SearchUserEyeActivity.this,
									SearchUserEyeActivity.this.getResources().getString(R.string.myfavorites_delete_fail),
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
		ActivityStack.newInstance().pop();
		super.onBackPressed();
	}
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//		Log.e(TAG,"__onNewIntent__");
//		if(mAdapter!=null){
//		  mAdapter.refrashData();
//		  Log.e(TAG,"__onNewIntent1__");
//		}
//	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(mAdapter!=null){
		  mAdapter.refrashData();
		}
	}
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(this.getResources().getStringArray(R.array.searchlocitems)[7]);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this));
	}
}
