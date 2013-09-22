package com.mapabc.android.activity.search;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.DataBaseHelper;
import com.mapabc.android.activity.base.NextRoadView;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.AdminInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.utils.SysParameterManager;
/**
 * @description: 查询基类
 * @author menglin.cao 2012-08-24
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public abstract class SearchActivity extends Activity implements Observer,OnClickListener,OnEditorActionListener {

	protected AutoCompleteTextView keywordAutoCompleteTextView;
	protected ImageButton searchImageButton,changeCityImageButton,voiceImageButton;
	private static final String TAG = "SearchActivity";
	protected String target;
	private ProgressDialog progress;

	protected ArrayList<SearchResultInfo> lstPOI = new ArrayList<SearchResultInfo>();
	protected Context context;
//	protected DataBaseHelper databaseHelper = new DataBaseHelper(this);
//	protected Cursor cursor;
	protected SimpleCursorAdapter  cursorAdapter;
	protected String adCode="110100";
	protected boolean bRest = false;// 第一次查询
	private final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			if(lstPOI.size()<=0){
				Toast toast = Toast.makeText(context, R.string.common_no_data, 1500);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				cleanInput();
			}else{
				Bundle extras = new Bundle();
				extras.putSerializable(Constants.SEARCH_RESULT, lstPOI);
				forwardIntent(extras);
			}
			if(progress != null){
				progress.dismiss();
			}
			
		}
		
	};
	//
	protected abstract void setLayout();
	
	public SearchActivity() {
		context = this;
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((requestCode == Constants.REQUEST_CITY) && (data != null)){
			updateCity();
		}
	}

	abstract protected boolean validateInput();
	protected void showInfo(int resId){
		Toast toast =Toast.makeText(this, Utils.getValue(this, resId), 1000);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
//		startSynchronizedLoad();
	}

	protected void startSynchronizedLoad(){
		if(!validateInput()){
			return;
		}
		if(!isFinishing()){
			progress = ProgressDialog.show(this, "", Utils.getValue(this, R.string.commmon_loading_data));
		}
		
		new Thread(){
			public void run(){
				searchPOI();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	protected void cleanInput(){
		keywordAutoCompleteTextView.getEditableText().clear();
	}
	
	protected void registerSearchBar(final String target){
		keywordAutoCompleteTextView.setOnEditorActionListener(this);
		searchImageButton = (ImageButton) findViewById(R.id.ib_search);
		searchImageButton.setOnClickListener(this);
		changeCityImageButton = (ImageButton) findViewById(R.id.ib_changeCity);
		changeCityImageButton.setOnClickListener(this);
		voiceImageButton = (ImageButton)findViewById(R.id.ib_voiceSearch);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_DONE ){
			Utils.hideSoftKeyBorad(v.getContext(), keywordAutoCompleteTextView);
			return true;
		}
		return false;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_search:
			Utils.hideSoftKeyBorad(v.getContext(), searchImageButton);
			startSynchronizedLoad();
			break;
		case R.id.ib_changeCity:
			Intent intent = new Intent(Constants.ACTIVITY_CHOOSE_CITIES);
			intent.putExtra(Constants.BACK_TARGET, SearchActivity.this.target);
			startActivityForResult(intent, Constants.REQUEST_CITY);
			break;
//		case R.id.actv_search_keyword:
//			keywordAutoCompleteTextView.showDropDown();
//			break;
		default:
			break;
		}
	}
//	protected void registerDropDown(final String res){
//		cursor = databaseHelper.query("name = '"+res+"'");
//		startManagingCursor(cursor);
//		cursorAdapter = new SimpleCursorAdapter(
//				this,
//				R.layout.searchactivity_rememberkeyword,
//				cursor,
//				new String[]{"keyword"},
//				new int[]{R.id.tv_keyword});
//		cursorAdapter.setCursorToStringConverter(new CursorToStringConverter(){
//			@Override
//			public CharSequence convertToString(Cursor cursor) {
//				return cursor.getString(2);
//			}});
//		
//		keywordAutoCompleteTextView.setAdapter(cursorAdapter);
//		keywordAutoCompleteTextView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				cursor = databaseHelper.query("name = '"+res+"'");
//				if(cursor != null){
//					cursorAdapter.changeCursor(cursor);
//				}
//				
//				keywordAutoCompleteTextView.showDropDown();
//			}
//		});
//		keywordAutoCompleteTextView.setDropDownHeight((int) (Utils.getCurScreenHeight(this)*0.5));
//	}

	protected void updateCity(){
		try{
			String cityNameStr = "";
			adCode = SettingForLikeTools.getADCode(SearchActivity.this);
			boolean bInt = SearchAPI.getInstance().adInit(SysParameterManager.getBasePath()+"/MapABC/Data/POI/DistBasicInfo.dat");
			if(bInt){
				AdminInfo adInfo = new AdminInfo();
				boolean bSuccess = SearchAPI.getInstance().getADInfoByCode(adCode, adInfo);
				if(bSuccess){
					cityNameStr = adInfo.name;
				}
				SearchAPI.getInstance().adExit();
			}
			NextRoadView txtCity = (NextRoadView) findViewById(R.id.nrv_city_name);
			if(cityNameStr!=null&&cityNameStr.length()>0){
				txtCity.setText(cityNameStr);
			}else{
				txtCity.setText("不在范围内");
			}
			
		}catch(Exception e){
//			SysLog.e("updateCity 204", e.toString());
		}
	}
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable observable, Object data) {
		if(data instanceof Integer){
		}
	}
	protected abstract void searchPOI();
	protected abstract void returnSearchActivity();
	protected abstract void forwardIntent(Bundle extras);

	@Override
	public void onBackPressed() {
//		if(cursor != null){
//			cursor.close();
//		}
		ActivityStack.newInstance().pop();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		lstPOI = null;
//		if(cursor != null){
//		   cursor.close();
//	    }
//		databaseHelper.close();
		super.onDestroy();
		
	}
	
	protected void registerBackAction(ImageButton btnBack){
		btnBack.setOnClickListener(new BackListener(this));
	}
}
