package com.mapabc.android.activity.search;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.search.adapter.CityExpandListAdapter;
import com.mapabc.android.activity.search.adapter.CityObservable;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.AdminInfo;
import com.mapabc.naviapi.utils.AndroidUtils;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 城市选择UI
 * @author menglin.cao 2012-08-24
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class ChooseCityActivity extends Activity {
	private static final String TAG="ChooseCityActivity";
	private CityObservable cityObservable = CityObservable.newInstance();
	private ExpandableListView chooseCityExpandableListView;
	private int idicateWidth=50;//下拉按钮的宽度
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("===========ChooseCityActivity=============", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_search_choosecity);
//		adInit();
		initTop();
		chooseCityExpandableListView = (ExpandableListView) findViewById(R.id.elv_chooseCity);
		chooseCityExpandableListView.setIndicatorBounds(0, (int) (Utils.getCurScreenWidth(this)*1.8));
		chooseCityExpandableListView.setAdapter(new CityExpandListAdapter());
		idicateWidth = Utils.getIndicatorPosition(this);
//		int screenLevel=AndroidUtils.checkScreenResolution(this);
//		switch (screenLevel) {
//		case AndroidUtils.SCREEN_RESOLUTION_QVGA://低
//			break;
//		case AndroidUtils.SCREEN_RESOLUTION_HVGA://中
//			break;
//		case AndroidUtils.SCREEN_RESOLUTION_WVGA://高
//			idicateWidth=50;
//			break;
//		case AndroidUtils.SCREEN_RESOLUTION_WFVGA://高
//			break;
//		case AndroidUtils.SCREEN_RESOLUTION_UNKNOWN://未知
//			idicateWidth=30;
//			break;
//		case AndroidUtils.SCREEN_RESOLUTION_SVGA://超高
//			idicateWidth=30;
//			break;
//		default:
//			break;
//		}
		setIndicatorPosition();
		chooseCityExpandableListView.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				try{
					Log.e(TAG, "===groupPosition=="+groupPosition);
					Log.e(TAG, "===childPosition=="+childPosition);
					AdminInfo adInfo = new AdminInfo();
					SearchAPI.getInstance().getProvince(groupPosition, adInfo);
					SearchAPI.getInstance().getCity(adInfo.code, childPosition, adInfo);
					SettingForLikeTools.setADCode(ChooseCityActivity.this, adInfo.code);
					Log.e(TAG, "ADCODE:"+adInfo.code);
					if(adInfo.code.equals("710000")){
						Utils.showTipInfo(ChooseCityActivity.this, R.string.choosecityactivity_select_taiwan);
						return true;
					}
					Intent intent = new Intent(getIntent()
					.getStringExtra(Constants.BACK_TARGET));
					intent.putExtra(Constants.SELECT_CITY, adInfo.code);
					setResult(Constants.REQUEST_CITY, intent);
//					adExit();
					finish();
				}catch(Exception e){
//					SysLog.e("ExpandableListView onChildClick: ", e.toString());
				}
				return true;
			}});
		
	}
	
//	/**
//	* @Title: adInit
//	* @Description: TODO(初始化城市列表数据)
//	* @param     设定文件
//	* @return void    返回类型
//	* @throws
//	 */
//	private void adInit(){
//		SearchAPI.getInstance().adInit(SysParameterManager.getBasePath()+"/MapABC/Data/POI/DistBasicInfo.dat");
//	}
	
	/**
	 * 设置显示器位置
	 */
	public void setIndicatorPosition() {

		DisplayMetrics dm = new DisplayMetrics(); // 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		int screenWidth = dm.widthPixels;
		chooseCityExpandableListView.setIndicatorBounds(screenWidth - idicateWidth, screenWidth);
	}

	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.choosecity_topic);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this, false, false));
	}
}
