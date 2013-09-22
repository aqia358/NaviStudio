/**
 * 
 */
package com.mapabc.android.activity.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.setting.adapter.SettingListAdapter;
import com.mapabc.android.activity.utils.SettingForLikeTools;

/**
 * desciption:更多下的交通雷达设置
 * 
 */
public class SettingTrafficRadioActivity extends BaseActivity implements
OnClickListener, OnItemClickListener{

	private static final String TAG = "SettingTrafficRadioActivity";
	private ListView SettingTrafficRadioListView;
	private SettingListAdapter itemAdapter;
	private static int TEMPINDEX = 0;// 临时保存系统参数索引
	public static boolean PRIORITYPOI[] = null;// 临时保存优先显示的POI值
	private static boolean isclick = false;
	private  String[] nameArray = null;
	private  int[] imgArray = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingforlike_main);
		initTop();
		nameArray=this.getResources().getStringArray(
				R.array.settingfortrafficradio);
//		imgArray=new int[]{
//				R.drawable.settingforlike_fronttrafficradio,
//				R.drawable.settingforlike_surroundtrafficradio,
//				R.drawable.settingforlike_update_sys
//				};
		imgArray=new int[]{
				R.drawable.settingforlike_update_sys
				};
		
		SettingTrafficRadioListView = (ListView)findViewById(R.id.lv_settingForLikeItem);
		itemAdapter = new SettingListAdapter(this,nameArray,imgArray,"SettingTrafficRadioActivity");
		SettingTrafficRadioListView.setAdapter(itemAdapter);
		SettingTrafficRadioListView.setOnItemClickListener(this);
	}
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop() {
		ImageButton resetImageButton = (ImageButton) findViewById(R.id.ib_reset);
		resetImageButton.setOnClickListener(this);
		TextView topicTextView = (TextView) findViewById(R.id.tv_topic);
		topicTextView.setText(this.getResources().getStringArray(
				R.array.otherfunctionitems)[2]);
		ImageButton backImageButton = (ImageButton) findViewById(R.id.ib_menu_back);
		backImageButton
				.setOnClickListener(new BackListener(this, false, false));
	}

	/**
	 * 重置恢复出厂设置
	 */
	private void reset() {
		Log.e(TAG, "恢复出厂设置");
		try {
			SettingForLikeTools.setTrafficSettingState(SettingTrafficRadioActivity.this, 1);
			SettingForLikeTools.resetPara(SettingTrafficRadioActivity.this,"trafficradiosetting");

		} catch (Exception ex) {
			Log.e(TAG, "ERROR", ex);
		}
		this.refresh();
	}

	/**
	 * 数据刷新
	 */
	public void refresh() {
		if (itemAdapter != null)
			itemAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_reset:
			// TODO Auto-generated method stub
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					SettingTrafficRadioActivity.this);
			mBuilder.setTitle(SettingTrafficRadioActivity.this.getResources().getString(R.string.common_tip));
			mBuilder.setMessage(SettingTrafficRadioActivity.this.getResources().getString(R.string.reset_tip));
			mBuilder.setPositiveButton(R.string.common_btn_positive,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) 
						{
							// TODO Auto-generated method stub
							SettingTrafficRadioActivity.this.reset();
						}
					});
			mBuilder.setNegativeButton(R.string.common_btn_negative,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

						}
					});
			mBuilder.show();
			
			
			break;

		default:
			break;
		}
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		try {
			switch (position) {
			case 0://前方路况播报
				SettingForLikeTools.setUpdateState(this);
//				SettingForLikeTools.setFrontTrafficRadio(this);
				break;
//			case 1://周边路况播报
//				
//				SettingForLikeTools.setSurroundTrafficRadio(this);
//				break;
//			case 2:
//				SettingForLikeTools.setUpdateState(this);
//				break;
			}
			this.refresh();
		} catch (Exception ex) {
			this.refresh();
			ex.printStackTrace();
			// SysLog.e(TAG, ex.toString());
		}
	}
}
