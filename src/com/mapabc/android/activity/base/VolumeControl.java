package com.mapabc.android.activity.base;

import java.io.File;
import java.io.FileOutputStream;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.log.Logger;
import com.mapabc.android.activity.utils.BitmapUitls;
import com.mapabc.android.activity.utils.DateTimeUtil;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.general.function.file.FileManager;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.route.SystemTime;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.utils.SysParameterManager;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * 声音控制器
 */
public class VolumeControl implements OnClickListener{

	private NaviStudioActivity naviStudioActivity;
	public ImageButton volume;
	public MapView mapView;
	private static int streamVolume = 0;
	public static AudioManager audioManager;
	public static boolean isFirstSaveVolume = false;//是否第一次进入系统，是保存音量
	public static int Sys_volume = 0;
	public VolumeControl(NaviStudioActivity activity, MapView mapView) {
		this.naviStudioActivity = activity;
		volume = this.naviStudioActivity.volume;
		this.mapView=mapView;
		audioManager=this.naviStudioActivity.audioManager;
		if(!isFirstSaveVolume){			
			Sys_volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		}
		isFirstSaveVolume = true;
		
	}
	
    
	@Override
	public void onClick(View v) {
		if(v.equals(volume)){
			voidControl();
//            saveLog();
		}
	}
    
	public void voidControl() {
		audioManager.setMode(AudioManager.MODE_NORMAL);
		int volume_count = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (volume_count > 0) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,0);
			volume.setBackgroundResource(R.drawable.navistudio_volumemute);
			streamVolume = volume_count;
		} else if (volume_count == 0) {
			volume.setBackgroundResource(R.drawable.navistudio_volumemax);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,streamVolume,0);
//			streamVolume = volume_count;
		}
	}	
	public static void resetVolume(){
	    if(audioManager!=null){
	    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,Sys_volume,0);
	    }
	}
	private Bitmap shot() {  
        View view = naviStudioActivity.getWindow().getDecorView();  
        Display display = naviStudioActivity.getWindowManager().getDefaultDisplay();  
        view.layout(0, 0, display.getWidth(), display.getHeight());  
        view.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap   
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());  
        return bmp;  
    }  
	private void saveLog(){
		Bitmap m = shot();
		if(m!=null){
			String path = SysParameterManager.getBasePath()+"/MapABC/log";
			File f = new File(path);
			if(!f.exists()){
				boolean re = f.mkdir();
				if(!re){
					return ;
				}
			}
			byte[] b = BitmapUitls.bitmapToBytes(m);
			try{
				String filepath = path+"/"+DateTimeUtil.getSystemDateTime1()+".bmp";
				File ff = new File(filepath);
				if(!ff.exists()){
					ff.createNewFile();
				}
				FileOutputStream bitmapfile = new FileOutputStream(ff);
				bitmapfile.write(b);
				bitmapfile.flush();
				bitmapfile.close();
			}catch(Exception ex){
				Log.e("saveLog", "ERROR",ex);
			}
		}
		if(!RouteAPI.getInstance().isRouteValid()){
			return ;
		}
		NSLonLat vehicelpos = MapAPI.getInstance().getVehiclePos();
		Logger.e("saveLog","车辆位置："+vehicelpos.x+","+vehicelpos.y);
		Logger.e("saveLog", "规划原则："+SettingForLikeTools.getRouteCalcMode(naviStudioActivity));
		NSLonLat start = RouteAPI.getInstance().getStartPoint();
		Logger.e("saveLog", "起点："+start.x+","+start.y);
		NSLonLat [] passpoint = RouteAPI.getInstance().getPassPoints();
		if(passpoint!=null)
		for(int i=0;i<passpoint.length;i++){
			Logger.e("saveLog", "途经点："+passpoint[i].x+","+passpoint[i].y);
		}
		NSLonLat [] avoidpoint = RouteAPI.getInstance().getAvoidPoints();
		if(avoidpoint!=null)
		for(int i=0;i<avoidpoint.length;i++){
			Logger.e("saveLog", "避让点："+avoidpoint[i].x+","+avoidpoint[i].y);
		}
		NSLonLat end = RouteAPI.getInstance().getEndPoint();
		Logger.e("saveLog", "终点："+end.x+","+end.y);
	}
}
