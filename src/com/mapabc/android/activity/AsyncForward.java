package com.mapabc.android.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.PhoneStatReceiver;
import com.mapabc.android.activity.log.Logger;
import com.mapabc.android.activity.utils.CrashHandler;
import com.mapabc.android.activity.utils.IconManager;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapEngineManager;
import com.mapabc.naviapi.TTSAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.map.MapOptions;
import com.mapabc.naviapi.route.GPSExOptions;
import com.mapabc.naviapi.route.RouteOptions;
import com.mapabc.naviapi.search.SearchOption;
import com.mapabc.naviapi.tts.TTSOptions;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.utils.SysParameterManager;


/**
 * 系统初始化线程
 */
public class AsyncForward extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "AsyncForward";
	private Activity act;
	private String target;
	TTSOptions ttsOptions = new TTSOptions();
	public AsyncForward(Activity act,String target){
		this.act= act;
		this.target = target;
	}			
				
	/*
	 * 初始化系统参数
	 */
	@Override
	protected Void doInBackground(Void... params) {
		IconManager.newInstance().fetchIconsResId(act,"com.mapabc.android.activity");
		if(act!=null){
			/////////////写日志控制//////////////
			GPSExOptions gpsOption =new GPSExOptions();
			SysParameterManager.getCGPSExOptions(gpsOption);
			Logger.ISSAVELOG = gpsOption.saveNmea;
			////////////////////////////////////
			PhoneStatReceiver.status=true;
		    MapEngineManager mapEngineManager =new MapEngineManager(act);
			MapOptions mapOptions = new MapOptions();
			RouteOptions routeOption = new RouteOptions();
			SearchOption clsSearchOption = new SearchOption();
			mapOptions.screenWidth = Utils.getCurScreenWidth(act);
			mapOptions.screenHeight = Utils.getCurScreenHeight(act);
			SysParameterManager.getCMapOption(mapOptions,act);
			SysParameterManager.getSearchOpt(clsSearchOption);
			SysParameterManager.getTTSOptions(ttsOptions);
			String adcode = SettingForLikeTools.getADCode(act);
			clsSearchOption.strADCode=adcode;
			//获取路口放大图路径
			SysParameterManager.getRouteOpt(routeOption);
			routeOption.httpCrossImgPath = routeOption.crossImgPath;
			String x = SettingForLikeTools.getVehicleX(act, "0");
			String y = SettingForLikeTools.getVehicleY(act, "0");
			float x0 = Float.parseFloat(x);
			float y0 = Float.parseFloat(y);
			if(x0>0){
				 mapOptions.mapCenter.x =x0;
				 mapOptions.mapCenter.y =y0;
			}
			//初始化
			boolean res = mapEngineManager.initMapEngine(mapOptions, clsSearchOption,routeOption);
			if(!res){
				Log.e(TAG, "initeMapEngine false");
			}else{
				Log.e(TAG, "initeMapEngine true");
			}

		}else{
			Log.e(TAG, "act is null");
		}
		/**读取配置文件*/

		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		Logger.e(TAG, "启动导航。。。。");
		if(UtilAPI.getInstance().verifyUserKey()!=0){
			showExitTip(Utils.getValue(act,R.string.asyncforward_registe_tip));
			return ;
		}
//		初始化系统参数
        boolean tts_res = TTSAPI.getInstance().init(ttsOptions);
        Log.e(TAG, "tts init is:"+tts_res);
		SettingForLikeTools.initSystem(act);
		String welcome = act.getString(R.string.navilogo_welcomevoice);
        TTSAPI.getInstance().addPlayContent(welcome, Const.AGPRIORITY_CRITICAL);

		SettingForLikeTools.systemStart_SetScreenBrightness(act);
		CrashHandler.newInstance().init(act.getApplicationContext());
		Utils.turnScreen_OffDown(act);
		Utils.intentEvent(Constants.INTENT_ACTION, Constants.INTENT_TYPE_NEWNAVISTUDIO, null, null, act, target);
		act.finish();
	}
	private  void showExitTip(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(act)
		.setTitle(R.string.common_tip)
		.setIcon(R.drawable.alert_dialog_icon)
		.setMessage(msg)
		.setPositiveButton(R.string.common_confirm, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MapEngineManager.release();
				act.finish();
				System.exit(0);
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
	}

}
