package com.mapabc.android.activity.listener;

import com.mapabc.naviapi.TTSAPI;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStatReceiver extends BroadcastReceiver{
    public static boolean status = false;//程序是否启动
    public static int volumn = -1;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(!status){
			return ;
		}
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			if(!TTSAPI.getInstance().isMuteVolumn()){
				TTSAPI.getInstance().setMuteVolumn(true);
				volumn = 0;
			}
		}else{
			TelephonyManager tm = (TelephonyManager) context
			.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				//来电
				if(!TTSAPI.getInstance().isMuteVolumn()){
					TTSAPI.getInstance().setMuteVolumn(true);
					volumn = 0;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//接通
				
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				//挂断
				if(volumn==0){
					TTSAPI.getInstance().setMuteVolumn(false);
				}
				break;
		}
			

			
		}
	}

}
