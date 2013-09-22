package com.mapabc.android.activity.route.track;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.NaviStudioApplication;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.route.track.adapter.TrackManagerAdapter;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.type.TimeFormat;
import com.mapabc.naviapi.utils.AndroidUtils;
import com.mapabc.naviapi.utils.SysParameterManager;

public class TrackManagerActivity extends BaseActivity implements
		OnItemClickListener {

	private static final String TAG = "TrackManagerActivity";
	private ListView functionListView;
	TrackManagerAdapter itemadapter = null;
	private boolean isclick = false;
	private static int TEMPINDEX = 0;// 临时保存系统参数索引
	public static NaviStudioApplication myapp = null;
	private MyTraceListener traceListener;
	public static boolean isRecording = false;// 是否正在记录轨迹

	public String[] trackManagerNameArr = null;
	public int[] imageIdArr = { R.drawable.routemanager_tracklist,
			R.drawable.routemanager_recordsetting,
			R.drawable.routemanager_startrecord,
			R.drawable.routemanager_trackdisplay };// 图片数组

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otherfunction_main);
		initTop();
		traceListener = new MyTraceListener(this);
		myapp = (NaviStudioApplication) getApplication();
		trackManagerNameArr = getResources().getStringArray(
				R.array.trackmanager_items);
		
		if (myapp.isRecordTrack) {
			trackManagerNameArr[2]=getResources().getString(R.string.trackmanager_startrecord);
			imageIdArr[2]=R.drawable.routemanager_startrecord;
		}else {
			trackManagerNameArr[2]=getResources().getString(R.string.trackmanager_stoprecord);
			imageIdArr[2]=R.drawable.routemanager_stoprecord;
		}
		functionListView = (ListView) findViewById(R.id.lv_otherfunctionitem);
		itemadapter = new TrackManagerAdapter(this, trackManagerNameArr,
				imageIdArr);
		functionListView.setAdapter(itemadapter);
		functionListView.setOnItemClickListener(this);
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (myapp.isRecordTrack) {
			trackManagerNameArr[2]=getResources().getString(R.string.trackmanager_startrecord);
			imageIdArr[2]=R.drawable.routemanager_startrecord;
		}else {
			trackManagerNameArr[2]=getResources().getString(R.string.trackmanager_stoprecord);
			imageIdArr[2]=R.drawable.routemanager_stoprecord;
		}
		itemadapter.notifyDataSetChanged();
		
	}



	/**
	 * 初使化顶部控件
	 */
	private void initTop() {
		TextView topicTextView = (TextView) findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.trackmanager_title);
		ImageButton backImageButton = (ImageButton) findViewById(R.id.ib_menu_back);
		backImageButton
				.setOnClickListener(new BackListener(this, false, false));
	}

	/**
	 * 相应listview内的点击事件
	 * 
	 *@param position
	 *            索引
	 *@return
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			Intent moreIntent = new Intent(Constants.ACTIVITY_TRACELIST);
			startActivity(moreIntent);
			break;
		case 1:
			trackRecordSetting();
			break;
		case 2:// 开始记录轨迹
			if (NaviControl.haveGPS||isRecording) {//增加isRecording解决gps丢失信号时无法停止记录轨迹的bug
				startOrStopRecordTrack();
			} else 
			{
				Utils.showTipInfo(this, "当前无GPS信号，不能录制轨迹!");
			}
			break;
		case 3:
			displayTrackSetting();
			break;
		default:
			break;
		}

	}

	/**
	 * 数据刷新
	 */
	public void refresh() {
		if (itemadapter != null)
			itemadapter.notifyDataSetChanged();
	}

	// 开始,停止 记录轨迹
	private void startOrStopRecordTrack() {
		// TODO Auto-generated method stub
		if (myapp.isRecordTrack) {
			// 开始记录轨迹
			// String traceName=getTraceName();
			long time = System.currentTimeMillis();
			String traceName = dateTimeToStr(time);
			// boolean showTrace=false;
			// int res= SettingForTrackTools.getTrackSetting(this);
			// if (res==0) {
			// showTrace=true;
			// }else {
			// showTrace=false;
			// }
			String path = SysParameterManager.getBasePath()
					+ "/MapABC/Resource/Trace" + "/" + traceName + ".dat";
			TraceAPI.getInstance().setCallBack(traceListener);
			TraceAPI.getInstance().startTraceRecord(traceName, path,
					SettingForTrackTools.getTrackRule(this), true);
			trackManagerNameArr[2] = getResources().getString(
					R.string.trackmanager_stoprecord);
			imageIdArr[2] = R.drawable.routemanager_stoprecord;
			myapp.isRecordTrack = false;
			isRecording = true;
		} else {
			// 停止记录轨迹
			TraceAPI.getInstance().stopTraceRecord();
			MapAPI.getInstance().delLayer(Constants.TRACE_POINT);
			trackManagerNameArr[2] = getResources().getString(
					R.string.trackmanager_startrecord);
			imageIdArr[2] = R.drawable.routemanager_startrecord;
			myapp.isRecordTrack = true;
			isRecording = false;
		}
		refresh();
	}

	private String getTraceName() {
		TimeFormat time = new TimeFormat(System.currentTimeMillis());
		String TraceName = AndroidUtils.getDay(time);
		TraceName = TraceName + "---" + AndroidUtils.getTime(time);
		return TraceName;
	}

	// 轨迹显示设置
	public void displayTrackSetting() {
		isclick = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getResources().getString(
				R.string.routemanager_isdisplaytrack));
		builder.setSingleChoiceItems(this.getResources().getStringArray(
				R.array.trackmanager_yesno), SettingForTrackTools
				.getTrackSetting(TrackManagerActivity.this),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int pos) {
						TrackManagerActivity.TEMPINDEX = pos;
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
							SettingForTrackTools.setTrackSetting(
									TrackManagerActivity.this,
									TrackManagerActivity.TEMPINDEX);
							if (TrackManagerActivity.TEMPINDEX == 0) {// yes

							} else {
								// 删除轨迹点的图层
								MapAPI.getInstance().delLayer(
										Constants.TRACE_POINT);
							}

						} catch (Exception ex) {
							Log.e(TAG, "error", ex);
						}
					}
				});
		builder.setNegativeButton(R.string.common_cancel, null);
		builder.show();
	}

	// 记录密度设置
	public void trackRecordSetting() {
		isclick = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getResources().getString(
				R.string.routemanager_trackrule));
		builder.setSingleChoiceItems(this.getResources().getStringArray(
				R.array.trackmanager_trackrule), SettingForTrackTools
				.getTrackRule(TrackManagerActivity.this),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int pos) {
						TrackManagerActivity.TEMPINDEX = pos;
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
							SettingForTrackTools.setTrackRule(
									TrackManagerActivity.this,
									TrackManagerActivity.TEMPINDEX);
							if (isRecording) {
								TraceAPI
										.getInstance()
										.restartTraceRecord(
												SettingForTrackTools
														.getTrackRule(TrackManagerActivity.this),
												true);
							}
						} catch (Exception ex) {
							Log.e(TAG, "error", ex);
						}
					}
				});
		builder.setNegativeButton(R.string.common_cancel, null);
		builder.show();
	}

	public static String dateTimeToStr(long time) {
		String str = null;
		Date date = new Date(time);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		if (date != null) {
			str = df.format(date);
		}
		return str;
	}
}
