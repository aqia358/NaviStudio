package com.mapabc.android.activity;

import java.io.File;

import com.mapabc.android.activity.base.Constants;
import com.mapabc.naviapi.utils.SysParameterManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;


/**
 * @description: 系统启动
 * @author: zhuhao 2012-08-09
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class AutoNaviStartActicity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//遍历mnt，查找数据文件所在目录
		File file = new File("/mnt");
		File files[] = file.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory()){
				String mapabcpath = files[i].getAbsolutePath();
				if(new File(mapabcpath+"/MapABC").exists()){
					SysParameterManager.setBasePath(mapabcpath);
					break;
				}
			}
		}
		final Context context = this;
		new Handler().postDelayed(new Runnable(){
			public void run() {
				context.startActivity(new Intent(Constants.ACTIVITY_START_SPLASH));
				finish();
			}}, 50);
	}

	@Override
	public void onBackPressed() {
	}
}

