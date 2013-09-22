package com.mapabc.android.activity.utils;

import java.io.File;
import java.io.Serializable;

import org.dom4j.Element;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.SettingNotFoundException;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.general.function.file.FileManager;
import com.mapabc.general.function.xml.XmlManager;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.TipParams;
import com.mapabc.naviapi.favorite.FavoriteInfo;
import com.mapabc.naviapi.route.AvoidLine;
import com.mapabc.naviapi.route.SearchLineInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.StringValue;
import com.mapabc.naviapi.utils.SysParameterManager;
/**
 * 常用功能方法类
 */
public class Utils {
	

	
	
	
	public static final int LDPI=120;
	public static final int MDPI=160;
	public static final int HDPI=240;
	public static final int XDPI=320;
	/**
	 * 获取设备DPI
	 * @param activity
	 * @return
	 */
	public static int getDPI(Activity activity){
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		int dpi = LDPI;
		if(metrics.densityDpi<MDPI){
			dpi = LDPI;
		}else if(metrics.densityDpi>=MDPI&&metrics.densityDpi<HDPI){
			dpi = MDPI;
		}else if(metrics.densityDpi>=HDPI&&metrics.densityDpi<XDPI){
			dpi = HDPI;
		}else if(metrics.densityDpi>=XDPI){
			dpi = XDPI;
		}
		return dpi;
	}
	
	public static int getIndicatorPosition(Activity activity){
		int dpi = getDPI(activity);
		switch(dpi){
		case LDPI:
			return 50;
		case MDPI:
			return 50;
		case HDPI:
			return 50;
		case XDPI:
			return 70;
		default:
			return 50;
		}
	}
	/**
	 * 获取字符串的值
	 * @param context
	 * @param resId
	 * @return
	 */
	public static String getValue(Context context,int resId){
		return context.getResources().getString(resId);
	}
	
	private static DisplayMetrics getDisplayMetrics(Context context){
		WindowManager winMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		winMgr.getDefaultDisplay().getMetrics(dm);
		return dm;
	}
	
	/**
	 * 获取当前屏幕的宽度
	 * @param context
	 * @return
	 */
	public static int getCurScreenWidth(Context context){
		return getDisplayMetrics(context).widthPixels;
	}
	
	public static int getCurScreenDensity(Context context){
		return getDisplayMetrics(context).densityDpi;
	}
	
	/**
	 * 获取当前屏幕的高度
	 * @param context
	 * @return
	 */
	public static int getCurScreenHeight(Context context){
		return getDisplayMetrics(context).heightPixels;
	}


	/**
	 * 按角度旋转图片
	 * @param resource
	 * @param resId
	 * @param degree
	 * @return
	 */
	public static Bitmap rotateIcon(Resources resource,int resId,float degree){
		Bitmap bitOrg = BitmapFactory.decodeResource(resource, resId);
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap bitNew = Bitmap.createBitmap(bitOrg, 0, 0,bitOrg.getWidth(), bitOrg.getHeight(), matrix, false);
		return bitNew;
	}
	
	/**
	 * 获取EditText的文本信息
	 * @param etxt
	 * @return
	 */
	public static String getEditTextValue(EditText etxt){
		Editable editable = etxt.getEditableText();
		return String.valueOf(editable.subSequence(0, editable.length()));
	}
	
	/**
	 * 隐藏键盘
	 * @param context
	 * @param view
	 */
	public static void hideSoftKeyBorad(Context context,View view){
		 InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
	
	/**
	 * 是否存在网络
	 * @param context 上下文
	 * @return true 正常,false 不正常 
	 */
	public static boolean checkNetWork(Context context){
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean hasNetWork = true;
		if(networkInfo == null || !networkInfo.isAvailable()){
			hasNetWork = false;
		}
		return hasNetWork;
	}
	
	public static boolean isHaveGPS(Context context) {
		
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {
			boolean bGpsEnable = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!bGpsEnable) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 是否存在sdcard
	 * @return true 存在，false 不存在
	 */
	public static boolean hasSDCard(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/**
	 * 判断是否横屏
	 * @param context 上下文
	 * @return true 横屏,false 竖屏
	 */
	public static boolean isLand(Context context){
		Configuration cf= context.getResources().getConfiguration();
		int ori = cf.orientation ;
		if(ori == cf.ORIENTATION_LANDSCAPE){
			return true;
		}else if(ori == cf.ORIENTATION_PORTRAIT){
			return false;
		}
		return false;
	}
	/**
	 * Toast提示
	 * @param context 上下文
	 * @param message 提示的信息ID
	 */
	public static void showTipInfo(Context context,int resId){
		if(context == null ){
			return;
		}
		Toast toast = Toast.makeText(context,
				context.getText(resId),
				2000);
		toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
		toast.show();
	}
	/**
	 * Toast提示
	 * @param context 上下文
	 * @param message 提示的信息
	 */
	public static void showTipInfo(Context context,String message){
		if(context == null ){
			return;
		}
		Toast toast = Toast.makeText(context,
				message,
				2000);
		toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
		toast.show();
	}
	public static void intentEvent(String keyname0,int keyvalue0,String keyname1,Serializable obj
			,Context context,String action){
		Bundle extra = new Bundle();
		if(keyname0!=null){
		  extra.putInt(keyname0, keyvalue0);
		}
		if(keyname1!=null){
			extra.putSerializable(keyname1, obj);
		}
		extra.putBoolean("fromTipSearch", true);
		Intent intent=new Intent(action);
		intent.putExtras(extra);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.setClass(RouteManagerActivity.this, com.mapabc.android.activity.NaviStudioActivity.class);
		context.startActivity(intent);
    }
	public static boolean pathExist(String path){
		File file = new File(path);
		return file.exists();
	}
	
	private static CacheUtil cache = CacheUtil.newInstance();
	
	
	public static void saveSysValue(ContentResolver resolver,String key, int value) {
        Uri uri = android.provider.Settings.System
                .getUriFor(key);
        android.provider.Settings.System.putInt(resolver, key,
        		value);
        resolver.notifyChange(uri, null);
    }
	
	/**
	 * 显示POI的TIP
	 * @param m_mapView
	 * @param context
	 * @param x
	 * @param y
	 * @param name
	 */
	public static void showPoiTip(MapView m_mapView,Context context,float x,float y,String name){
		View tipView;
		LayoutInflater mInflater = LayoutInflater.from(context);
		tipView = mInflater.inflate(R.layout.common_poi_tip, null);
		TextView tvTipRoadNam = (TextView) tipView.findViewById(R.id.tv_poi_road_name);
		int width = 141;
		tvTipRoadNam.setText(name);
		tvTipRoadNam.setTextSize(16);
		Paint paint = new Paint();
		paint.setTextSize(tvTipRoadNam.getTextSize());
		float size = paint.measureText(tvTipRoadNam.getText().toString());
		if(size<141){
			
		}else {
			width = (int)size;
		}
		tvTipRoadNam.setWidth(width);
		tvTipRoadNam.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		tvTipRoadNam.setSingleLine(true);
		TipParams params = new TipParams(width + 40, 100, x, y);
		if(m_mapView.isHasTip()){
			m_mapView.hideTip();
		}
		m_mapView.showTip(tipView, params);
	}
	/**
	 * 
	 * @Copyright:mapabc
	 * @description:
	 * @author fei.zhan
	 * @date 2012-9-3 
	 * @param centerinfo
	 * @param poi void
	 */
	public static void getFavoriteInfo(NSLonLat centerinfo,FavoriteInfo poi){
//		poi.fFavoriteLat = centerinfo.mPos.fY;
//		poi.fFavoriteLon = centerinfo.mPos.fX;
//		poi.cFavoriteAdress = centerinfo.cAddress;
//		poi.cFavoriteName = centerinfo.cName;
//		poi.cFavoriteTEL = centerinfo.cPhone;
//		poi.cFavoriteADCode = centerinfo.cADCode;
		String roadName=Utils.getRoadName(centerinfo, 50);
		if(roadName.equals("")){
			roadName="未知道路";
		}
		poi.longitude=centerinfo.x;
		poi.latitude=centerinfo.y;
		poi.name=roadName;
		
	}
	/**
	 * 
	 * @Copyright:mapabc
	 * @description:根据经纬度查找最近的道路名称
	 * @date 2012-9-3 
	 * @param lonLat
	 * @param distance
	 * @return String
	 */
	public static String getRoadName(NSLonLat lonLat,int distance){
		String roadName="";
		SearchLineInfo searchLine=new SearchLineInfo();
		boolean isOk= RouteAPI.getInstance().getNearRoute(lonLat, distance, searchLine);
		if(isOk){
			roadName=searchLine.label;
		}
		return roadName;
	}
	
	/**
	 * 把POI信息转为收藏夹信息
	 * @param poiInfo
	 * @param poi
	 */
	public static void getFavoriteInfo(SearchResultInfo poiInfo,FavoriteInfo poi){
		poi.latitude = poiInfo.lat;
		poi.longitude = poiInfo.lon;
		poi.address = poiInfo.addr;
		poi.name = poiInfo.name;
		poi.telephone = poiInfo.telephone;
		poi.adCode = poiInfo.adCode + "";
	}
	
	/**
	 * 收藏夹信息转化为POI信息
	 * @param poi
	 * @param poiInfo
	 */
	public static void getClsRearchResultInfo(FavoriteInfo poi,SearchResultInfo poiInfo){
		poiInfo.lat = poi.latitude;
		poiInfo.lon = poi.longitude;
		poiInfo.addr = poi.address;
		poiInfo.name = poi.name;
		poiInfo.telephone = poi.telephone;
		if(null!=poi.adCode&&(!(poi.adCode).equals(""))){
			poiInfo.adCode = poi.adCode;
		}
	}
	
	/**
	 * 根据POI点获取行政区划
	 * @param data
	 * @param context
	 * @return
	 */
	public static String getDistrictByAdCode(SearchResultInfo data,Context context){
		String district = context.getResources().getString(R.string.searchresult_district);
		StringValue prov = new StringValue();
		StringValue city = new StringValue();
		StringValue county = new StringValue();
		boolean re = SearchAPI.getInstance().getADNameByCode(data.adCode+"",prov,city,county);
		if(re){
			district =  city.strValue+","+county.strValue;
		}
		return district;
	}
	public static void restoreScreenOff(Context context){
		try{
			Object timeout = cache.get(android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
			if(timeout == null){
				return;
			}
			int oldOff = Integer.valueOf(String.valueOf(timeout));
			android.provider.Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, oldOff);			
			Log.e("restoreScreenOff", "restoreScreenOff:"+oldOff);
			saveSysValue(context.getContentResolver(),"screen_off_timeout",oldOff);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void turnScreen_OffDown(Context context){
		int off;
		try {
			off = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
			Log.e("turnScreen_OffDown", "turnScreen_OffDown:"+off);
			CacheUtil.newInstance().put(android.provider.Settings.System.SCREEN_OFF_TIMEOUT,off);
			android.provider.Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -1);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static String getUpdateSysURL(){
		String urlpath=SysParameterManager.getBasePath()+"/MapABC/Config/update.xml";
		byte xmldata[];
		String url="";
   		try {
   			xmldata = FileManager.getFileData(urlpath);
   			if (xmldata != null) {
   				Element rootEle = XmlManager.getEle(xmldata, "");
   				Element versionurlEle = rootEle.element("versionurl");
   				if(versionurlEle!=null){
   					url = versionurlEle.getText();
   				}
   			}
   		}catch(Exception ex){
   			Log.i("getUpdateSysURL", "ERR",ex);
   		}
		return url;
	}//4.1.2
	public static int getIntVersion(String version){
		String v[] = version.split("\\.");
		int intversion = Integer.parseInt(v[0])*1000;
		intversion = intversion+ Integer.parseInt(v[1])*100;
		intversion = intversion+ Integer.parseInt(v[2]);
		return intversion;
	}
	
	public static Animation setShowAnimation(Context context){
		Animation mShowAction = new TranslateAnimation(70.0f, 0.0f, 0.0f, 0.0f);
		mShowAction.setDuration(700);
		mShowAction.setStartOffset(100);
		mShowAction.setInterpolator(AnimationUtils.loadInterpolator(context,
				android.R.anim.bounce_interpolator));
		return mShowAction;
	}
	public static Animation setHideAnimation(Context context){
		Animation mHiddenAction = new TranslateAnimation(0.0f, 70.0f, 0.0f, 0.0f);
		mHiddenAction.setDuration(700);
		mHiddenAction.setStartOffset(100);
		mHiddenAction.setInterpolator(AnimationUtils.loadInterpolator(context,
				android.R.anim.bounce_interpolator));
		return mHiddenAction;
	}
	/**
	 * 获取保存的路径
	 * @return 路径信息，内容见saveRoute方法
	 */
	public static java.util.ArrayList  continueRoute(){
		String path = SysParameterManager.getBasePath()+"/MapABC/route.dat";
		java.io.ObjectInputStream ois = null;
		java.util.ArrayList list = null;
		try{
			File file = new File(path);
			if(file.exists()){
				ois = new java.io.ObjectInputStream(new java.io.FileInputStream(file));
				list = (java.util.ArrayList)ois.readObject();
				ois.close();
			}
			return list;
		}catch(Exception ex){
			return null;
		}
	}
	/**
	 * 保存路径信息
	 */
	public static void saveRoute(){
		try{
			String path = SysParameterManager.getBasePath()+"/MapABC/route.dat";
			File file = new File(path);
			if(RouteAPI.getInstance().isRouteValid()){
				boolean re =false;
			    if(!file.exists()){
			    	re = file.createNewFile();
			    }else{
			    	re = true;
			    }
			    if(re){
			    	java.util.ArrayList list = new java.util.ArrayList();
			    	NSLonLat startpoint = RouteAPI.getInstance().getStartPoint();
			    	NSLonLat endpoint  = RouteAPI.getInstance().getEndPoint();
			    	list.add(NaviControl.getInstance().calculateType);
			    	list.add(startpoint);
			    	list.add(endpoint);
			    	NSLonLat [] passpoint = null;
			    	if(RouteAPI.getInstance().getPassPointCount()>0){
			    		passpoint = RouteAPI.getInstance().getPassPoints();
			    		list.add(passpoint);
			    	}else{
			    	    list.add(0);
			    	}	
			    	NSLonLat[] avodArray = null;
					if(RouteAPI.getInstance().getAvoidPointCount()>0){			
						avodArray = RouteAPI.getInstance().getAvoidPoints();
						list.add(avodArray);
					}else{
			    	    list.add(0);
			    	}	
					java.io.ObjectOutputStream oos =new java.io.ObjectOutputStream(new java.io.FileOutputStream(file));
					oos.writeObject(list);
					oos.close();
			    }
			}else{
				if(file.exists()){
					file.delete();
				}
			}
		}catch(Exception ex){
			
		}
	}
}
