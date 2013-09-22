package com.mapabc.android.activity.search.result;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.search.adapter.InfoExpandListAdapter;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.utils.AndroidUtils;

/**
 * @description: POI查询结果基类
 * @author menglin.cao 2012-08-27
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public abstract class SearchPOIResultActivity extends Activity {
	private ProgressDialog progress;
	protected int defaultRadius = 5000;
	private ExpandableListView lstResult;
	protected ArrayList<SearchResultInfo> lstPOI = new ArrayList<SearchResultInfo>();
	private Spinner spnDistance;
	private static  int idicateWidth=50;//下拉按钮的宽度
	protected InfoExpandListAdapter infoExpAdapter;
	private final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			listLoadPOI();
			if(progress != null){
				progress.dismiss();
			}
		}
		
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchpoiresult_main);
		initTop();
		spnDistance = (Spinner) findViewById(R.id.spnDistance);
		spnDistance.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				try{
					String dist = String.valueOf(spnDistance.getAdapter().getItem(position));
					defaultRadius = (int) (Double.valueOf(dist.trim())*1000);
				}catch(Exception e){
					defaultRadius = 5000;
				}
				
				startSynchronizedLoad();
			}

			public void onNothingSelected(AdapterView<?> parent) {
				
			}});
		idicateWidth = Utils.getIndicatorPosition(this);
		
		lstResult = (ExpandableListView) findViewById(R.id.lstSearchResult);
		findViewById(R.id.resultbar).setVisibility(View.GONE);
	}
	
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.common_result);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this));
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(lstResult != null){
			setIndicatorPosition();
		}
		if(infoExpAdapter!=null){
			infoExpAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed() {
		ActivityStack.newInstance().pop();
		super.onBackPressed();
	}



	protected void startSynchronizedLoad(){
		if(!isFinishing()){
			progress = ProgressDialog.show(this,"", Utils.getValue(this, R.string.commmon_loading_data));
		}
		
//		lstResult.clearChoices();
		new Thread(){
			public void run(){
				searchPOI();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	private void listLoadPOI(){
		Log.d("SearchPOI", "listLoadPOI");
		Bundle extras = getIntent().getExtras();
		if(extras == null){
			if(progress != null){
				progress.dismiss();
			}
			return;
		}else{
			if(extras.getSerializable(Constants.SEARCH_RESULT) != null){
				lstPOI = (ArrayList<SearchResultInfo>) extras.getSerializable(Constants.SEARCH_RESULT);
				extras.putSerializable(Constants.SEARCH_RESULT, null);
			}
		}
		int poiCount = lstPOI.size();
		TextView txtCount = (TextView) findViewById(R.id.txtCount);
		txtCount.setVisibility(View.VISIBLE);
		txtCount.setText(Utils.getValue(this, R.string.common_total)+poiCount+Utils.getValue(this, R.string.common_record));
		infoExpAdapter = new InfoExpandListAdapter(this,lstPOI,lstResult);
		lstResult.setAdapter(infoExpAdapter);
		setIndicatorPosition();
	}
	
	protected abstract void searchPOI();
	
	protected void onDestroy() {
		lstPOI = null;
		infoExpAdapter = null;
		lstResult = null;
		super.onDestroy();
		
	}
	
	/**
	 * 设置显示器位置
	 */
	public void setIndicatorPosition() {

		DisplayMetrics dm = new DisplayMetrics(); // 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		int screenWidth = dm.widthPixels;
		lstResult.setIndicatorBounds(screenWidth - idicateWidth, screenWidth);
	}
}