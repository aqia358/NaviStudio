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
 * desciption:更多下的导航设置
 * 
 */
public class SettingRouteActivity extends BaseActivity implements
OnClickListener, OnItemClickListener{

	private static final String TAG = "SettingRouteActivity";
	private ListView SettingRouteListView;
	private SettingListAdapter itemAdapter;
	private static int TEMPINDEX = 0;// 临时保存系统参数索引
	public static boolean PRIORITYPOI[] = null;// 临时保存优先显示的POI值
	private static boolean isclick = false;
	private  String[] nameArray =null;
	private  int[] imgArray = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingforlike_main);
		initTop();
		nameArray= this.getResources().getStringArray(
				R.array.settingforroute);
		imgArray=new int[]{
			 	R.drawable.settingforlike_talkdirection,
				R.drawable.settingforlike_demospeed,
				R.drawable.settingforlike_ttsstate,
				R.drawable.settingforlike_overspeed,
				R.drawable.settingforlike_voicetype, 
				R.drawable.settingforlike_edog
				};
		
		
		SettingRouteListView = (ListView)findViewById(R.id.lv_settingForLikeItem);
		itemAdapter = new SettingListAdapter(this,nameArray,imgArray,"SettingRouteActivity");
		SettingRouteListView.setAdapter(itemAdapter);
		SettingRouteListView.setOnItemClickListener(this);
	}
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop() {
		ImageButton resetImageButton = (ImageButton) findViewById(R.id.ib_reset);
		resetImageButton.setOnClickListener(this);
		TextView topicTextView = (TextView) findViewById(R.id.tv_topic);
		topicTextView.setText(this.getResources().getStringArray(
				R.array.otherfunctionitems)[1]);
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
			SettingForLikeTools.setRouteSettingState(SettingRouteActivity.this, 1);
			SettingForLikeTools.resetPara(SettingRouteActivity.this,"routesetting");

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
			
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					SettingRouteActivity.this);
			mBuilder.setTitle(SettingRouteActivity.this.getResources().getString(R.string.common_tip));
			mBuilder.setMessage(SettingRouteActivity.this.getResources().getString(R.string.reset_tip));
			mBuilder.setPositiveButton(R.string.common_btn_positive,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) 
						{
							// TODO Auto-generated method stub
							SettingRouteActivity.this.reset();
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
			case 0:// 是否播报道路方向提示
				SettingForLikeTools.setTalkDirection(this);
				this.refresh();
				break;
			case 1:// 模拟时速
				this.demoSpeedDialog(position);
				break;
			case 2:// 语音开关
				SettingForLikeTools.setTTSState(this);
				this.refresh();
				break;
			case 3:// 超速提醒
				SettingForLikeTools.setOverSpeed(this);
				this.refresh();
				break;
			case 4:// 语音类别
				this.voiceTypeDialog(position);
				break;
			case 5:// 电子眼开关
				SettingForLikeTools.setEDogEnable(this);
				this.refresh();
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			// SysLog.e(TAG, ex.toString());
		}
	}
	
	/**
	 * 模拟时速对话框
	 * 
	 * @param position
	 *            索引
	 * @return
	 */
	public void demoSpeedDialog(int position) {
		isclick = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.nameArray[position]);
		builder.setSingleChoiceItems(
				this.getResources().getStringArray(R.array.demospeeditem),
				SettingForLikeTools.getDemoSpeed(SettingRouteActivity.this),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int pos) {
						SettingRouteActivity.TEMPINDEX = pos;
						isclick = true;
					}

				});
		builder.setPositiveButton(R.string.common_confirm,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int pos) {
						if (!isclick) {
							return;
						}
						try {
							SettingForLikeTools.setDemoSpeed(
									SettingRouteActivity.TEMPINDEX,
									SettingRouteActivity.this);
							SettingRouteActivity.this.refresh();
						} catch (Exception ex) {

						}
					}
				});
		builder.setNegativeButton(R.string.common_cancel, null);
		builder.show();
	}
	
	/**
	 * 语音类别设置对话框
	 * 
	 * @param position
	 *            索引
	 * @return
	 */
	public void voiceTypeDialog(int position) {
		isclick = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.nameArray[position]);
		builder.setSingleChoiceItems(
				this.getResources().getStringArray(R.array.voicetypeitem),
				SettingForLikeTools.getRole(SettingRouteActivity.this),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int pos) {
						SettingRouteActivity.TEMPINDEX = pos;
						isclick = true;
					}

				});
		builder.setPositiveButton(R.string.common_confirm,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int pos) {
						if (!isclick) {
							return;
						}
						try {
							SettingForLikeTools.setRole(
									SettingRouteActivity.TEMPINDEX,
									SettingRouteActivity.this);
							SettingRouteActivity.this.refresh();
						} catch (Exception ex) {

						}
					}
				});
		builder.setNegativeButton(R.string.common_cancel, null);
		builder.show();
	}
	
}
