package com.mapabc.android.activity.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.log.Logger;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapEngineManager;
import com.mapabc.naviapi.RouteAPI;

/**
 * @description: 系统退出资源销毁
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class ExitDialog {
	private String TAG = "ExitDialog";
	private AlertDialog.Builder builder;
    Handler h = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1){
				NaviControl.getInstance().stopSimNavi();
				NaviControl.getInstance().stopRealNavi();
				VolumeControl.resetVolume();
			}
		}
    };
	public ExitDialog(final Context context) {

		builder = new AlertDialog.Builder(context);
		builder.setTitle(Utils.getValue(context, R.string.navistudio_txtExit));
		builder.setMessage(Utils.getValue(context,
				R.string.navistudio_txtExitDesc));
		builder.setNegativeButton(
				Utils.getValue(context, R.string.common_btn_negative),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						builder.create().dismiss();
					}
				}).setPositiveButton(
				Utils.getValue(context, R.string.common_btn_positive),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e("ExitDialog", "=====点点点=====");
						synchronized (context.getApplicationContext()) {
							SettingForLikeTools
									.reSettingScreenBrightness(context);
							new Thread() {
								public void run() {
									try {
										Logger.e(TAG, "正常退出导航。。。。");
										h.sendEmptyMessage(1);
										Utils.saveRoute();
										ActivityStack.newInstance().finishAll();
										SettingForLikeTools.saveSystem(context);
										Utils.restoreScreenOff(context);
										MapEngineManager.release();

										Log.e(TAG, "MYPID:"
												+ android.os.Process.myPid());
										android.os.Process
												.killProcess(android.os.Process
														.myPid());
										System.exit(0);
									} catch (Exception ex) {
										Log.e(TAG, "ERROR", ex);
									}
								}
							}.start();
						}
					}
				});
	}

	public void show() {
		builder.show();
	}
}
