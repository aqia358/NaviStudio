package com.mapabc.android.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable.ConstantState;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trafficguidance.GPSTool;
import com.mapabc.android.activity.R.drawable;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.CarBackEvent;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.CurrentPointListener;
import com.mapabc.android.activity.base.ExitDialog;
import com.mapabc.android.activity.base.GpsControl;
import com.mapabc.android.activity.base.MapModelControl;
import com.mapabc.android.activity.base.MenuActivityFactory;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.RouteLayer;
import com.mapabc.android.activity.base.TMCControl;
import com.mapabc.android.activity.base.VolumeControl;
import com.mapabc.android.activity.base.ZoomControl;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.listener.MyMapListener;
import com.mapabc.android.activity.listener.NaviMapTouchListener;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.UIResourceUtil;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.TTSAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.listener.DayOrNightListener;
import com.mapabc.naviapi.listener.MapListener;
import com.mapabc.naviapi.map.DayOrNightControl;
import com.mapabc.naviapi.route.RouteSegInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;

public class NaviStudioActivity extends BaseActivity {
	private static final String TAG = "NaviStudioActivity";
	public static ExecutorService executorService = Executors
			.newFixedThreadPool(10);
	public MapView mapView = null;// 地图实例
	public ImageButton zoomin, zoomout, volume, ibMapModel, gpsstate, back_car;
	public ImageButton currentPointBtn;
	public ImageButton plantRouteBtn;
	public ImageButton mainSearchBtn, mainManageBtn, mainMoreBtn, mainExitBtn;
	public ImageButton realTimeRoadBtn, descriptionbtn, mapdescriptionBtn;
	public ImageView weatherPic;
	public TextView location, weatherRange, currentWeather;
	private Animation alphaAnimation = null; // 渐变动画
	public static NSLonLat end_mPos;// 静态变量终点，用于向其他页面传值

	public RelativeLayout mapbarLayout;
	public LinearLayout zoomLayout;
	public Animation mShowAction = null; // 动态效果（显示）
	public Animation mHiddenAction = null; // 动态效果（隐藏）
	public ZoomControl zoomControl;
	public VolumeControl volumeControl;
	public static AudioManager audioManager;
	public MapModelControl mapModelControl;
	public GpsControl gpsControl;
	public TextView scaleTextView;
	private NaviMapTouchListener mapTouchListener = null;
	public CarBackEvent carBackEvent;
	private MyMapListener mapListener = new MyMapListener(this);
	public CurrentPointListener currentPointListener;
	public NaviControl naviControl;
	public ProgressDialog pdg;
	public AlertDialog dialog;
	public SearchResultInfo poiInfo;
	// private Animation left_in, right_in;

	// boolean isTMCOpen=false;
	public static final int REALTIMETRAFFIC = 201;// 加载实时交通
	public static final int CARREALTIMESTATUS = 202;// 捕获当前的车辆信息
	private BackListener back;
	public boolean blEnableSearch = true;// 是否禁用地点搜索菜单
	public static boolean displayWarningOrNot = false;// 控制只警告一次

	int watchPoint = -1;// 监视点
	private ImageButton warningBannerCloseBtn;
	private NSLonLat currentCarPoint;
	NSLonLat lastCarPoint = null;// 记录上一次的车位点，用于判断车辆是否移动了
	private static TimerTask task;
	private static Timer timer;
	private GPSTool g = new GPSTool();
	public int delay = 20;
	
	/*
	 * 处理其他对象或者线程发送过来的消息
	 */
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REALTIMETRAFFIC:// 打开或者关闭实时交通
				if (!(TMCControl.TMC_STATUS == TMCControl.TMC_OPENED)
						&& Utils.checkNetWork(NaviStudioActivity.this)) {
					TMCControl.getInstance(mapView).startTMC();
				} else {
					TMCControl.getInstance(mapView).stopTMC();
				}
				break;
			case CARREALTIMESTATUS:
				if (g.isDangerous()) {
					if (!displayWarningOrNot) {
//						if(watchPoint > 3){
//							watchPoint = 0;
//						}else{
//							watchPoint++;
//						}
						watchPoint = 0;
						switch (watchPoint) {
						case 0:
							TTSAPI.getInstance()
									.addPlayContent(
											"警告！前方八百米路口右侧道路有车辆接近路口，保持原速度行驶将发生碰撞,请减速慢行！",
											Const.AGPRIORITY_CRITICAL);
							showWarningWindow();
							displayWarningOrNot = true;
							break;
						case 1:
							TTSAPI.getInstance()
									.addPlayContent(
											"警告！前方八百米道路发生塌陷，请减速慢行注意安全！路口温度：27摄氏度，路口湿度：百分之27。",
											Const.AGPRIORITY_CRITICAL);
							showWarningWindow();
							displayWarningOrNot = true;
							break;
						case 2:
							TTSAPI.getInstance()
									.addPlayContent(
											"警告！后方200米道路右侧车道有车辆加速行驶，请不要变更车道，以防发生，追尾事故！",
											Const.AGPRIORITY_CRITICAL);
							showWarningWindow();
							displayWarningOrNot = true;
							break;
						default: break;
						}
					}else{
						if(displayWarningOrNot){
							getAroundCarsPositionAndDraw();
//							displayWarningOrNot = false;
						}
//							hideWarningWindow();
					}
				}else{
					Log.i("receive gps", "----safe----");
				}
//				else {
//					if(displayWarningOrNot){
//						hideWarningWindow();
//						displayWarningOrNot = false;
//					}
//				}
				break;
			default:
				break;
			}
		}
	};
	// 自动判断下昼夜模式监听
	DayOrNightListener dayOrNightListener = new DayOrNightListener() {
		@Override
		public void changestatus(boolean status) {
			// true白天，false黑夜
			updateUIStyle(status);
		}
	};

	/*
	 * 与黑夜白天相关控件样式更新
	 */
	public void updateUIStyle(boolean mdayOrNight) {

		currentPointListener.updateCalculateBtn_Style();
		currentPointListener.updateCurrentPoint_Style();
		this.zoomControl.initZoom();
		if (mdayOrNight) {
			// 白天
			if (Utils.isLand(this)) {
				mapbarLayout
						.setBackgroundResource(R.drawable.navistudio_roadname_land_day);
			} else {
				mapbarLayout
						.setBackgroundResource(R.drawable.navistudio_roadname_port_day);
			}

			scaleTextView.setTextColor(Color.BLACK);
			if (blEnableSearch) {
				back_car.setBackgroundResource(R.drawable.navistudio_backcar_day);
			} else {
				back_car.setBackgroundResource(R.drawable.navistudio_backbutton_day);
			}
		} else {
			// 黑夜
			if (Utils.isLand(this)) {
				mapbarLayout
						.setBackgroundResource(R.drawable.navistudio_roadname_land_night);
			} else {
				mapbarLayout
						.setBackgroundResource(R.drawable.navistudio_roadname_port_night);
			}
			scaleTextView.setTextColor(Color.WHITE);
			if (blEnableSearch) {
				back_car.setBackgroundResource(R.drawable.navistudio_backcar_night);
			} else {
				back_car.setBackgroundResource(R.drawable.navistudio_backbutton_night);
			}
		}
		// 更新声音按钮样式
		if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
			volume.setBackgroundResource(R.drawable.navistudio_volumemute);
		} else {
			volume.setBackgroundResource(R.drawable.navistudio_volumemax);
		}
		if (naviControl != null) {
			naviControl.setColor(mdayOrNight);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		audioManager = (AudioManager) getSystemService(Activity.AUDIO_SERVICE);
		initMapView();
		back = new BackListener(this, true);

		Utils.continueRoute();

	}

	@Override
	protected void onPause() {

		mapListener.isStart = false;// 暂时关闭自动回车位功能
		/*************** 离开当前界面保存地图的比例尺 ******************/
		int scale = (int) MapAPI.getInstance().getMapScale();
		SettingForLikeTools.saveMapScale(this, scale);
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapListener.isStart = true;// 重新开启自动回车位功能
		if (SettingForLikeTools.AUTOGOBACKTOCAR_BOOLEAN) {// 解决重置后不回车位的问题
			mapListener.resetTime();
		}
		mapModelControl.setMapMode();// 设置地图模式显示图片
		audioManager = (AudioManager) getSystemService(Activity.AUDIO_SERVICE);
		if (RouteAPI.getInstance().isRouteValid()) {
			if (NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI) {
				// NaviControl.getInstance().guideBegin();
				// this.mapView.goBackCar();
			} else {
				if (NaviControl.getInstance().naviStatus != NaviControl.NAVI_STATUS_REALNAVI) {
					NaviControl.getInstance().startNavigate();
				} else {
					this.mapView.goBackCar();
					NaviControl.getInstance().showNaviInfo();
				}
			}
		}
		receiveEvent(getIntent());
		updateUIStyle(DayOrNightControl.mdayOrNight);
		currentPointListener.setFootView();
		setMapCenterRoadName();
		NaviControl.getInstance().fleshLane();// 解决从其他页面跳转到主界面时车道线不显示的问题
		// ///////注册陀螺仪////////////
		// OrientationSensorManager.getInstance(this,NaviControl.getInstance()).register();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DayOrNightControl.getIntance().removieDayOrNightListener(
				dayOrNightListener);
		mapListener = null;
		naviControl = null;
	}

	/**
	 * 初始化layout type == 0 onCreate调用 type == 1 onConfigurationChanged调用
	 */
	private void initMapView() {
		if (Utils.isLand(this)) {
			setContentView(R.layout.navistudio_map_land_layout);
		} else {
			setContentView(R.layout.navistudio_map_port_layout);
		}
		FrameLayout flo = (FrameLayout) findViewById(R.id.fl_mapview);
		View mView = (View) findViewById(R.id.v_mapsview);
		int nIndex = flo.indexOfChild(mView);
		flo.removeView(mView);
		mapView = AutoNaviMap.getInstance(this).getMapView();
		ViewParent parent = mapView.getParent();
		if (parent != null && (parent instanceof FrameLayout)) {
			((FrameLayout) parent).removeView(mapView);
		}
		flo.addView(mapView, nIndex, mView.getLayoutParams());

		naviControl = NaviControl.getInstance();
		RouteAPI.getInstance().setCallBack(naviControl);
		naviControl.setLayout(findViewById(R.id.fl_navilayout), this, mapView);

		setAnimation();
		initComponents();
		addListener();
		int scale = SettingForLikeTools.getMapScale(this);
		MapAPI.getInstance().setMapScale(scale);
		checkRealTimeRoadStatus();// 检查实时路况的开关状态

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			// adjustStreamVolume: 调整指定声音类型的音量
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);// 调低声音
			if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
				volume.setBackgroundResource(R.drawable.navistudio_volumemute);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			// 第一个参数：声音类型
			// 第二个参数：调整音量的方向
			// 第三个参数：可选的标志位
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI); // 调高声音
			if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
				volume.setBackgroundResource(R.drawable.navistudio_volumemax);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			// mapView.getTip().hide();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		/***************** 横竖屏切换时保存当前地图比例尺 *********************/
		int scale = (int) MapAPI.getInstance().getMapScale();
		SettingForLikeTools.saveMapScale(this, scale);
		initMapView();
		if (RouteAPI.getInstance().isRouteValid()) {
			if (MapAPI.getInstance().isCarInCenter()) {
				if (NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI) {
					NaviControl.getInstance().guideBegin();
				} else {
					NaviControl.getInstance().startNavigate();
				}
			}
			NaviControl.getInstance().fleshLane();// 解决横竖屏切换时车道线不显示的问题
		}
		updateUIStyle(DayOrNightControl.mdayOrNight);
		currentPointListener.setFootView();
		setMapCenterRoadName();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
		setScaleText();

	}

	public void setAnimation() {
		mShowAction = new TranslateAnimation(70.0f, 0.0f, 0.0f, 0.0f);
		mShowAction.setDuration(700);
		mShowAction.setStartOffset(100);
		mShowAction.setInterpolator(AnimationUtils.loadInterpolator(this,
				android.R.anim.bounce_interpolator));
		mHiddenAction = new TranslateAnimation(0.0f, 70.0f, 0.0f, 0.0f);
		mHiddenAction.setDuration(700);
		mHiddenAction.setStartOffset(100);
		mHiddenAction.setInterpolator(AnimationUtils.loadInterpolator(this,
				android.R.anim.bounce_interpolator));
	}

	/**
	 * 初始化控件
	 */
	private void initComponents() {
		mainSearchBtn = (ImageButton) findViewById(R.id.ib_main_search);
		mainManageBtn = (ImageButton) findViewById(R.id.ib_main_info);
		mainMoreBtn = (ImageButton) findViewById(R.id.ib_main_set);
		mainExitBtn = (ImageButton) findViewById(R.id.ib_main_exit);
		realTimeRoadBtn = (ImageButton) findViewById(R.id.ib_btn_realtimeroad);
		descriptionbtn = (ImageButton) findViewById(R.id.ib_btn_description);
		mapdescriptionBtn = (ImageButton) findViewById(R.id.ib_btn_mapdescription);
		weatherPic = (ImageView) findViewById(R.id.iv_weather);
		weatherRange = (TextView) findViewById(R.id.tv_weatherrange);
		;
		currentWeather = (TextView) findViewById(R.id.tv_currentweather);

		scaleTextView = (TextView) this.findViewById(R.id.tv_scale_num);
		zoomLayout = (LinearLayout) findViewById(R.id.ll_zoomlayout);
		zoomin = (ImageButton) this.findViewById(R.id.ib_btnZoomIn);
		zoomout = (ImageButton) this.findViewById(R.id.ib_btnZoomOut);
		volume = (ImageButton) findViewById(R.id.ib_btn_volume);
		ibMapModel = (ImageButton) findViewById(R.id.ib_map_model);
		gpsstate = (ImageButton) findViewById(R.id.ib_gpsstate);
		back_car = (ImageButton) findViewById(R.id.ib_btn_car);
		currentPointBtn = (ImageButton) findViewById(R.id.btn_current_position);
		plantRouteBtn = (ImageButton) findViewById(R.id.btn_calculate);
		mapbarLayout = (RelativeLayout) findViewById(R.id.rl_mapbar);
		currentPointListener = new CurrentPointListener(this, mapView);
		/*
		 * if (RouteAPI.getInstance().isRouteValid()){//显示文字按钮
		 * descriptionbtn.setVisibility(View.VISIBLE); }else{
		 * descriptionbtn.setVisibility(View.GONE); }
		 */
	}

	/**
	 * 给控件添加点击监听事件
	 */
	private void addListener() {

		zoomControl = new ZoomControl(this, mapView);
		setScaleText();
		zoomin.setOnClickListener(zoomControl);
		zoomout.setOnClickListener(zoomControl);

		volumeControl = new VolumeControl(this, mapView);
		volume.setOnClickListener(volumeControl);

		gpsControl = new GpsControl(this, mapView);
		gpsstate.setOnClickListener(gpsControl);
		// DisPatchInfo.getInstance().addGpsInfoListener("GpsControl",
		// gpsControl);

		carBackEvent = new CarBackEvent(this, mapView);

		mapModelControl = new MapModelControl(this, mapView);
		ibMapModel.setOnTouchListener(mapModelControl);

		// 白天黑夜模式监听
		DayOrNightControl.getIntance()
				.addDayOrNightListener(dayOrNightListener);

		MapAPI.getInstance().addMapListenter(mapListener);
		// 长按短按事件监听
		mapTouchListener = new NaviMapTouchListener(this, mapView);
		mapView.setMapTouchListener(mapTouchListener);

		currentPointBtn.setOnTouchListener(currentPointListener);
		plantRouteBtn.setOnTouchListener(currentPointListener);

		// 为主界面功能按钮添加监听
		realTimeRoadBtn.setOnClickListener((OnClickListener) listener);
		descriptionbtn.setOnClickListener((OnClickListener) listener);
		mapdescriptionBtn.setOnClickListener((OnClickListener) listener);

		mainSearchBtn.setOnClickListener((OnClickListener) listener);
		mainManageBtn.setOnClickListener((OnClickListener) listener);
		mainMoreBtn.setOnClickListener((OnClickListener) listener);
		mainExitBtn.setOnClickListener((OnClickListener) listener);

	}

	/**
	 * 主界面功能按钮的点击事件响应
	 */
	private OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			ImageButton btn = (ImageButton) v;
			switch (btn.getId()) {
			case R.id.ib_btn_realtimeroad: {
				alphaAnimation = new AlphaAnimation(0.4f, 1.0f);
				// 设置动画时间
				alphaAnimation.setDuration(500);
				btn.startAnimation(alphaAnimation);
				setRealTimeRoadStatus();
				break;
			}
			case R.id.ib_btn_description: {
				if (RouteAPI.getInstance().isRouteValid()) {
					Intent intent2 = new Intent();
					intent2.setAction(MenuActivityFactory
							.getRouteManagerActivityIntent(4));
					startActivity(intent2);
				}
				break;
			}
			case R.id.ib_btn_mapdescription: {
				if (RouteAPI.getInstance().isRouteValid()) {
					Log.e("RouteManager", "======全程概览 ======");
					Intent intent2 = new Intent();
					intent2.setAction(MenuActivityFactory
							.getRouteManagerActivityIntent(3));
					startActivity(intent2);
				}
				break;
			}
			case R.id.ib_main_search: {
				Intent locIntent = new Intent(
						Constants.ACTIVITY_SEARCH_SEARCHLOCATION);
				startActivity(locIntent);

				break;
			}
			case R.id.ib_main_info: {
				Intent routeIntent = new Intent(
						Constants.ACTIVITY_ROUTE_MANAGER);
				startActivity(routeIntent);
				break;

			}
			case R.id.ib_main_set: {
				Intent moreIntent = new Intent(Constants.ACTIVITY_OTHERFUNCTION);
				startActivity(moreIntent);
				break;
			}
			case R.id.ib_main_exit: {// 点击线路查询按钮之后执行的动作,在这个界面将用户键入的开始和结束站点传值到下一个界面
				ExitDialog exitDailog = new ExitDialog(NaviStudioActivity.this);
				exitDailog.show();
				break;
			}

			case R.id.warningbannerclosebutton: {// 关闭警告窗口
				displayWarningOrNot = false;
				hideWarningWindow();
				break;
			}

			}
		}
	};

	// 在页面载入的时候就判断实时路况是否处于打开状态
	public void checkRealTimeRoadStatus() {
		if (!(TMCControl.TMC_STATUS == TMCControl.TMC_OPENED)) {
			realTimeRoadBtn.setImageResource(R.drawable.map_light);
		} else {
			realTimeRoadBtn.setImageResource(R.drawable.map_light_on);
		}
	}

	public void setRealTimeRoadStatus() {
		ActivityStack.newInstance().setBlMapBack(false);
		if (TMCControl.TMC_STATUS == TMCControl.TMC_CLOSED) {
			boolean hasNetWork = Utils.checkNetWork(this);
			if (!hasNetWork) {
				Toast.makeText(this,
						Utils.getValue(this, R.string.common_nonetwork),
						Toast.LENGTH_LONG).show();

			} else {
				TTSAPI.getInstance().addPlayContent(
						getString(R.string.open_realroadstatus),
						Const.AGPRIORITY_CRITICAL);
				realTimeRoadBtn.setImageResource(R.drawable.map_light_on);
				Toast.makeText(
						NaviStudioActivity.this,
						Utils.getValue(
								NaviStudioActivity.this,
								R.string.navistudioactivity_loadingrealtimetraffic),
						Toast.LENGTH_LONG).show();

				executorService.execute(new Runnable() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = REALTIMETRAFFIC;
						mHandler.sendMessage(msg);
					}
				});

			}
		} else {
			realTimeRoadBtn.setImageResource(R.drawable.map_light);
			TTSAPI.getInstance().addPlayContent(
					getString(R.string.close_realroadstatus),
					Const.AGPRIORITY_CRITICAL);
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = REALTIMETRAFFIC;
					mHandler.sendMessage(msg);
				}
			});

		}
	}

	/*
	 * 设置比例尺TEXTVIEW的文本
	 */
	public void setScaleText() {
		int iScale = (int) MapAPI.getInstance().getMapScale();
		String sScaleText = UIResourceUtil.getScaleText(iScale);
		scaleTextView.setText(sScaleText);
		Log.e(TAG, "======Mapsize======" + iScale);
	}

	@Override
	public void onBackPressed() {
		if (!blEnableSearch) {
			ActivityStack.newInstance().setBlMapBack(true);// 来自地图返回
			blEnableSearch = true;
			mapView.hideTip();
			mapListener.isStart = true;// 重新开启自动回车位功能
			super.onBackPressed();
			return;
		}
		if (!MapAPI.getInstance().isCarInCenter()) {
			mapView.goBackCar();
			return;
		}
		resetStatus();
		ExitDialog exitDailog = new ExitDialog(this);
		exitDailog.show();
		// super.onBackPressed();
	}

	public void resetStatus() {
		try {
			setIntent(null);
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					ActivityStack stack = ActivityStack.newInstance();
					stack.finishAllExcludeMap();// 清空其它界面
				}
			});

			// mapView.hideTip();
			// blEnableSearch = true;// 地点搜索菜单可用

		} catch (Exception e) {
			Log.e("MapActivity::resetStatus", e.toString());
		}
	}

	/**
	 * 由其它界面跳转到地图界面后进行的操作。
	 * 
	 * @param intent
	 */
	private void receiveEvent(Intent intent) {
		if (intent == null) {
			return;
		}
		Bundle extras = intent.getExtras();
		if (extras != null) {
			int intentAction = extras.getInt(Constants.INTENT_ACTION);
			switch (intentAction) {
			case Constants.INTENT_TYPE_LOOKPOI:// 查看POI点
				blEnableSearch = false;
				poiInfo = (SearchResultInfo) extras
						.getSerializable(Constants.POI_DATA);
				NSLonLat center = new NSLonLat(poiInfo.lon, poiInfo.lat);
				MapAPI.getInstance().setMapCenter(center);
				mapListener.onMapStatusChanged(MapListener.MAP_STATUS_MOVE);
				mapListener.isStart = false; // 暂时关闭自动回车位
				Utils.showPoiTip(mapView, this, poiInfo.lon, poiInfo.lat,
						poiInfo.name);

				break;
			case Constants.INTENT_TYPE_SETSTARTPOINT:// 设置起点
				NaviControl.getInstance().stopSimNavi();
				NaviControl.getInstance().stopRealNavi();
				RouteAPI.getInstance().clearRoute();
				NaviControl.getInstance().guideEnd();
				RouteLayer routeLayer = new RouteLayer();
				routeLayer.deleteLayer();
				RouteAPI.getInstance().deletePassPoint(-1);
				RouteAPI.getInstance().deleteAvoidPoint(-1);

				SearchResultInfo start_poiInfo = (SearchResultInfo) extras
						.getSerializable(Constants.POI_DATA);
				NSLonLat start_mPos = new NSLonLat();
				start_mPos.x = start_poiInfo.lon;
				start_mPos.y = start_poiInfo.lat;
				float fAngle = MapAPI.getInstance().getVehicleAngle();
				MapAPI.getInstance().setVehiclePosInfo(start_mPos, fAngle);
				mapView.goBackCar();
				resetStatus();
				break;
			case Constants.INTENT_TYPE_SETENDPOINT:// 设置终点
				// 设置终点
				SearchResultInfo end_poiInfo = (SearchResultInfo) extras
						.getSerializable(Constants.POI_DATA);
				end_mPos = new NSLonLat();
				end_mPos.x = end_poiInfo.lon;
				end_mPos.y = end_poiInfo.lat;
				naviControl.end_poiInfo = end_poiInfo;
				new Intent(Constants.ACTIVITY_ROUTE_MANAGER);

				Intent naviResultList = new Intent(
						Constants.ACTIVITY_NAVIRESULTLIST);
				Bundle bundle = new Bundle();
				bundle.putString("type", "input");
				naviResultList.putExtras(bundle);
				startActivity(naviResultList);

				// 这儿设置好起点和终点之后跳转到显示界面
				// naviControl.calculatePath(end_mPos,0);
				// resetStatus();
				break;
			case Constants.INTENT_TYPE_START_SIMNAVI:// 开始模拟导航
				displayWarningOrNot = false;
				watchPoint = -1;
				MapAPI.getInstance().setMapView(1);
				CKLstartSimNavi();
				NaviControl.getInstance().startSimulate();
				NaviControl.getInstance().drawRoute();
				MapAPI.getInstance().setMapScale(16);
				mapView.goBackCar();
				break;
			case Constants.INTENT_TYPE_STOP_SIMNAVI:// 停止模拟导航
				CKLstopSimNavi();
				displayWarningOrNot = false;
				NaviControl.getInstance().drawRoute();
				mapView.goBackCar();
				break;

			case Constants.INTENT_TYPE_CHANGEROUTEMODE:
				mapView.goBackCar();
				break;
			case Constants.INTENT_TYPE_NEWNAVISTUDIO:// 第一次启动应用程序
				java.util.ArrayList list = Utils.continueRoute();
				if (list != null && list.size() == 5) {
					NaviControl.getInstance().continueRoute(list);
				}
				this.isHaveGPS();
				break;
			case Constants.INTENT_TYPE_LOOKDESTTASK:// 查看目的地任务
				// Log.e(TAG, "=============查看目的地任务================");
				// DestInfo
				// destInfo=(DestInfo)extras.getSerializable(Constants.WZT_DATA);
				// NSLonLat mPos = new NSLonLat();
				// mPos.x =destInfo.getX();
				// mPos.y =destInfo.getY();
				// NSLonLat mlonlat=new NSLonLat(destInfo.getX(),
				// destInfo.getY());
				// String roadName = "";
				// String destDes=destInfo.getDestDescription();
				// Log.e(TAG,
				// "=============查看目的地任务destDes================"+destDes);
				//
				// MapAPI.getInstance().setMapCenter(mPos);
				// blEnableSearch = false;
				// mapListener.onMapStatusChanged(MapListener.MAP_STATUS_MOVE);
				// mapListener.isStart=false; //暂时关闭自动回车位

				break;
			case Constants.INTENT_TYPE_NAVIGATION:// 从任务列表开启导航
				// Log.e(TAG, "=============从任务列表开启导航================");
				// RouteInfo
				// routeInfo=(RouteInfo)extras.getSerializable(Constants.WZT_DAVIGATE);
				// float[] lon=routeInfo.getLon();
				// float[] lat=routeInfo.getLat();
				//
				// if(lon.length==1){
				// NSLonLat mPos1 = new NSLonLat();
				// mPos1.x =lon[0];
				// mPos1.y =lat[0];
				// currentPointListener.setDestination(mPos1,1);
				// }else if(lon.length==2){
				// for(int i=0;i<lon.length;i++){
				// NSLonLat mPos1 = new NSLonLat();
				// mPos1.x =lon[i];
				// mPos1.y =lat[i];
				// if(i==0){
				// currentPointListener.setVehiclePosition(mPos1);
				// }else if(i==lon.length-1){
				// currentPointListener.setDestination(mPos1,1);
				// }else {
				//
				// }
				// }
				// }else {
				// NSLonLat mPos_start = new NSLonLat();
				// mPos_start.x =lon[0];
				// mPos_start.y =lat[0];
				// NSLonLat mPos_end = new NSLonLat();
				// mPos_end.x=lon[lon.length-1];
				// mPos_end.y=lat[lon.length-1];
				// currentPointListener.setVehiclePosition(mPos_start);
				// currentPointListener.setDestination(mPos_end,1);
				// for(int i=1;i<(lon.length-1);i++){
				// NSLonLat mPos_pass = new NSLonLat();
				// mPos_pass.x=lon[i];
				// mPos_pass.y=lat[i];
				// int nType =
				// SettingForLikeTools.getRouteCalcMode(NaviStudioActivity.this);
				// RouteAPI.getInstance().addPassPoint(mPos_pass);
				// }
				// }

				break;

			default:
				break;
			}
		}
		this.setIntent(null);
	}

	// private void isStartFrontTrafficRadio() {
	//
	// FrontTrafficRadio frontTrafficRadio = FrontTrafficRadio.getInstance();
	// frontTrafficRadio.openTrafficRadio();
	// }

	public void isHaveGPS() {
		// 判断系统的GPS位置服务是否打开
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {
			boolean bGpsEnable = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!bGpsEnable) {
				enableGPS();
			}
		}
	}

	// 启用GPS
	private void enableGPS() {
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(
				NaviStudioActivity.this);
		dlgBuilder.setTitle(R.string.navistudio_startGPSTitle);
		dlgBuilder.setMessage(R.string.navistudio_GPSTipMessage);
		dlgBuilder.setNegativeButton(
				Utils.getValue(this, R.string.common_btn_negative),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton(
				Utils.getValue(this, R.string.common_btn_positive),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 0);
					}
				});
		dlgBuilder.create().show();
	}

	/**
	 * 
	 * @Copyright:mapabc
	 * @description:设置道路中心点名称
	 * @author fei.zhan
	 * @date 2012-9-7 void
	 */
	public void setMapCenterRoadName() {
		String roadName = "";
		roadName = Utils.getRoadName(MapAPI.getInstance().getMapCenter(), 100);
		if (!roadName.equals("")) {

		} else {
			roadName = this.getString(R.string.navistudio_road_has_no_name);
		}
		NaviControl.getInstance().m_roadName = "";
		NaviControl.getInstance().setRoadName(roadName);
	}

	/**
	 * 
	 * @Copyright:陈科良
	 * @description:测试模拟导航，自己添加
	 * @author 陈科良
	 * @date 2013-08-06 void
	 */
	public void CKLstartSimNavi() {
		// 获取车辆需要行进的路线的坐标
		if (task == null) {
			task = new TimerTask() {
				public void run() {
					currentCarPoint = new NSLonLat();
					currentCarPoint = MapAPI.getInstance().getVehiclePos();
					Message message = new Message();
					message.what = CARREALTIMESTATUS;
					mHandler.sendMessage(message);
					// System.gc();
				}
			};
		}
		if (timer == null) {
			timer = new Timer(true);
		}
		if (timer != null && task != null) {
			timer.schedule(task, 0, 1000);
		}
	}

	public void CKLstopSimNavi() {
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

	}

	/**
	 * 
	 * @Copyright:陈科良
	 * @description:界面操作，遇到路口弹出警告窗口
	 * @author 陈科良
	 * @date 2013-08-06 void
	 */

	public void showWarningWindow() {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.rl_navinfo);
		RelativeLayout warningBanner = (RelativeLayout) layoutInflater.inflate(
				R.layout.cameraimage_layout, null);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp1.leftMargin = 0;
		lp1.topMargin = 0;
		mainLayout.addView(warningBanner, lp1);
		Animation translateAnimation = new TranslateAnimation(-400.0f, 0.0f,
				0.0f, 0.0f);
		translateAnimation.setDuration(500);
		warningBanner.startAnimation(translateAnimation);
		ImageView crossRoadBg = (ImageView) findViewById(R.id.imageframe);
		switch (watchPoint % 3) {
		case 0:
			crossRoadBg.setBackgroundResource(R.drawable.crossroad_2);
			break;
		case 1:
			crossRoadBg.setBackgroundResource(R.drawable.roadhole);
			break;
		case 2:
			crossRoadBg.setBackgroundResource(R.drawable.crossroad_3);
			break;
		}
		warningBannerCloseBtn = (ImageButton) findViewById(R.id.warningbannerclosebutton);
		warningBannerCloseBtn.setOnClickListener((OnClickListener) listener);

		// 转向标志移动
		LinearLayout change1 = (LinearLayout) findViewById(R.id.ll_navi_turn_layout);
		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) change1
				.getLayoutParams();
		linearParams.leftMargin = 355;
		change1.setLayoutParams(linearParams);
		// 当前点按钮移动
		RelativeLayout change2 = (RelativeLayout) findViewById(R.id.operatelayout);
		FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) change2
				.getLayoutParams();
		frameParams.leftMargin = 300;
		change2.setLayoutParams(frameParams);
		// 主地图变小

		// 摄像头和超速警告以及速度显示表盘移动

		ImageView change4 = (ImageView) findViewById(R.id.iv_navi_camera);
		change4.setPadding(change4.getPaddingLeft() + 355,
				change4.getPaddingTop(), change4.getPaddingRight(),
				change4.getPaddingBottom());
		ImageView change5 = (ImageView) findViewById(R.id.iv_navi_overspeed);
		change5.setPadding(change5.getPaddingLeft() + 355,
				change5.getPaddingTop(), change5.getPaddingRight(),
				change5.getPaddingBottom());

		TextView change6 = (TextView) findViewById(R.id.tv_navi_speed);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) change6
				.getLayoutParams();
		params.leftMargin = 355;// 通过自定义坐标来放置你的控件
		change6.setLayoutParams(params);

		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

		});

		// 修改layout的宽度
	}

	public void hideWarningWindow() {
		RelativeLayout warningBanner = (RelativeLayout) findViewById(R.id.imagedisplaybox);
		Animation translateAnimation = new TranslateAnimation(0.0f, -400.0f,
				0.0f, 0.0f);
//		translateAnimation.setDuration(500);
		warningBannerCloseBtn = (ImageButton) findViewById(R.id.warningbannerclosebutton);
		warningBannerCloseBtn.setOnClickListener(null);
		translateAnimation.setFillAfter(true);
		warningBanner.startAnimation(translateAnimation);
		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.rl_navinfo);
		mainLayout.removeView(warningBanner);
		lastCarPoint = null;// 将历史车位信息置空

		// 转向标志移动
		LinearLayout change1 = (LinearLayout) findViewById(R.id.ll_navi_turn_layout);
		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) change1
				.getLayoutParams();
		linearParams.leftMargin = 0;
		change1.setLayoutParams(linearParams);
		// 当前点按钮移动
		RelativeLayout change2 = (RelativeLayout) findViewById(R.id.operatelayout);
		FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) change2
				.getLayoutParams();
		frameParams.leftMargin = 0;
		change2.setLayoutParams(frameParams);
		// 主地图变小

		// 摄像头和超速警告移动
		ImageView change4 = (ImageView) findViewById(R.id.iv_navi_camera);
		change4.setPadding(change4.getPaddingLeft() - 355,
				change4.getPaddingTop(), change4.getPaddingRight(),
				change4.getPaddingBottom());
		ImageView change5 = (ImageView) findViewById(R.id.iv_navi_overspeed);
		change5.setPadding(change5.getPaddingLeft() - 355,
				change5.getPaddingTop(), change5.getPaddingRight(),
				change5.getPaddingBottom());
		TextView change6 = (TextView) findViewById(R.id.tv_navi_speed);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) change6
				.getLayoutParams();
		params.leftMargin = 0;//
		change6.setLayoutParams(params);

		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

		});

	}

	/**
	 * 
	 * @Copyright:陈科良
	 * @description:获取当前周边车辆的位置，并在界面上实时更新
	 * @author 陈科良
	 * @date 2013-08-06 void
	 */
	public void getAroundCarsPositionAndDraw() {
		switch (watchPoint % 3) {
		case 0:
			RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.imagedisplaybox);
			if(lastCarPoint == null){
			TextView DrivingWarningTip = new TextView(this);
			RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			DrivingWarningTip.setText("警告！前方八百米路口右侧道路有车辆\n接近路口，保持原速度行驶将发生碰撞\n，请减速慢行！");
			DrivingWarningTip.setTextColor(getResources().getColor(R.color.white));
			DrivingWarningTip.setTextSize(26);
			lay.leftMargin=5;  
			lay.topMargin =365; 
			DrivingWarningTip.setId(999);
	        mainLayout.addView(DrivingWarningTip,lay); 
				
			ImageView CarElement = new ImageView(this);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			CarElement.setBackgroundResource(R.drawable.whitecar_2);
			lp.leftMargin=20;  
	        lp.topMargin =198; 
	        CarElement.setId(1000);
	        mainLayout.addView(CarElement,lp); 
	        
	        ImageView CarElement1 = new ImageView(this);
			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			CarElement1.setBackgroundResource(R.drawable.redcar_1);
			lp1.leftMargin=170;  
		    lp1.topMargin =310; 
		    CarElement1.setId(1001);
		    mainLayout.addView(CarElement1,lp1); 
		    
		    ImageView CarElement3 = new ImageView(this);
			RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		    CarElement3.setBackgroundResource(R.drawable.whilecar_3);
			lp3.leftMargin=300;  
		    lp3.topMargin =186; 
		    CarElement3.setId(1003);
		    mainLayout.addView(CarElement3,lp3); 
		    
		    lastCarPoint = currentCarPoint;
			}else{
				TextView warningInfo = (TextView)findViewById(999);//文字变色
				if(warningInfo.getTextColors()==ColorStateList.valueOf(getResources().getColor(R.color.white)))
					warningInfo.setTextColor(getResources().getColor(R.color.color_red));
				else
					warningInfo.setTextColor(getResources().getColor(R.color.white));
				
				//左车右移
				ImageView car1 = (ImageView)findViewById(1000);
				int left = car1.getLeft() + 1;  
				RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)car1.getLayoutParams();  
				params1.leftMargin=left;//改变位置   
				car1.setLayoutParams(params1);
				
				ImageView car2 = (ImageView)findViewById(1003);//右车左移
				if(car2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.whilecar_3_warning).getConstantState())){
					car2.setBackgroundResource(R.drawable.whilecar_3);
		        }else{
		        	car2.setBackgroundResource(R.drawable.whilecar_3_warning);
		        }
				int left1 = car2.getLeft() - 4;  
				RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)car2.getLayoutParams();  
				params2.leftMargin=left1;//改变位置   
				car2.setLayoutParams(params2);
				
				if(left1 <=220&&left1>120){
					if(left1 == 220){
						RouteAPI.getInstance().pauseOrResumeSimNavi(true);
						TTSAPI.getInstance().addPlayContent(
								"警告！前方十字路口南侧有车辆往北行驶，欲穿过十字路口，请立即停车让行！",
								Const.AGPRIORITY_CRITICAL);	
						warningInfo.setText("警告！前方十字路口南侧有车辆往北行\n驶，欲穿过十字路口，请立即停车让行！");
					}
				}else{
					if(left1 == 120){
						if(displayWarningOrNot){
							g.list.clear();
							Log.e("receive gps", "解除危险"+g.list.size());
							hideWarningWindow();	
							displayWarningOrNot = false;
							RouteAPI.getInstance().pauseOrResumeSimNavi(false);
							TTSAPI.getInstance().addPlayContent(
									"危险已经解除，请继续行驶！",
									Const.AGPRIORITY_CRITICAL);	
						}
					}else{
						ImageView Mycar = (ImageView)findViewById(1001);
						int top = Mycar.getTop() -4;  
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)Mycar.getLayoutParams();  
						params.topMargin=top;//改变位置   
						Mycar.setLayoutParams(params);
					}
				}
			}
	        break;
		case 1:
        	if(lastCarPoint == null){
        	lastCarPoint = currentCarPoint;
        	RelativeLayout mainLayout1 = (RelativeLayout)findViewById(R.id.imagedisplaybox);
			TextView DrivingWarningTip = new TextView(this);
			RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			DrivingWarningTip.setText("警告！前方八百米路口道路发生塌陷，\n请减速慢行注意安全！\n  路口温度：27℃    路口湿度：27%");
			DrivingWarningTip.setTextColor(getResources().getColor(R.color.white));
			DrivingWarningTip.setTextSize(26);
			lay.leftMargin=5;  
			lay.topMargin =365; 
			DrivingWarningTip.setId(999);
			mainLayout1.addView(DrivingWarningTip,lay);
        	}else{
        		
        		TextView warningInfo = (TextView)findViewById(999);//文字变色
				if(warningInfo.getTextColors()==ColorStateList.valueOf(getResources().getColor(R.color.white)))
					warningInfo.setTextColor(getResources().getColor(R.color.color_red));
				else
					warningInfo.setTextColor(getResources().getColor(R.color.white));
				if(displayWarningOrNot&&UtilAPI.getInstance().calculateDis(currentCarPoint.x, currentCarPoint.y, NaviControl.cameraLonLat[watchPoint].x, NaviControl.cameraLonLat[watchPoint].y) <200&&UtilAPI.getInstance().calculateDis(currentCarPoint.x, currentCarPoint.y, NaviControl.cameraLonLat[watchPoint].x, NaviControl.cameraLonLat[watchPoint].y) >180){
					warningInfo.setText("警告！前方200米路口道路发生塌陷，\n请立即减速慢行注意安全！\n  路口温度：26℃    路口湿度：22%");
					TTSAPI.getInstance().addPlayContent(
							"警告！前方200米路口道路发生塌陷，请减速慢行注意安全！路口温度：26℃    路口湿度：22%",
							Const.AGPRIORITY_CRITICAL);	
				
				}
				if(delay > 0){
					delay --;
				}else{
					delay = 20;
					if(g.list.size() > 0){
						Log.e("receive gps", "clear data");
						g.list.clear();
					}
					hideWarningWindow();	
					displayWarningOrNot = false;
				}
//				if(displayWarningOrNot&&UtilAPI.getInstance().calculateDis(currentCarPoint.x, currentCarPoint.y, NaviControl.cameraLonLat[watchPoint].x, NaviControl.cameraLonLat[watchPoint].y) <50){
//					hideWarningWindow();	
//					displayWarningOrNot = false;
//					}
        	}
        	break;
        	
        case 2:
			RelativeLayout mainLayout2 = (RelativeLayout)findViewById(R.id.imagedisplaybox);
			if(lastCarPoint == null){
				 lastCarPoint = currentCarPoint;
			TextView DrivingWarningTip = new TextView(this);
			RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			DrivingWarningTip.setText("警告！后方200米道路右侧车道有车辆\n加速行驶，请不要变更车道，以防发生\n，追尾事故！");
			DrivingWarningTip.setTextColor(getResources().getColor(R.color.white));
			DrivingWarningTip.setTextSize(26);
			lay.leftMargin=5;  
			lay.topMargin =365; 
			DrivingWarningTip.setId(999);
			mainLayout2.addView(DrivingWarningTip,lay); 
				
			ImageView CarElement = new ImageView(this);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			CarElement.setBackgroundResource(R.drawable.redcar_1);
			lp.leftMargin=200;  
	        lp.topMargin =250; 
	        CarElement.setId(1000);
	        mainLayout2.addView(CarElement,lp); 
	        
	        ImageView CarElement1 = new ImageView(this);
			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			CarElement1.setBackgroundResource(R.drawable.whitecar_1);
			lp1.leftMargin=235;  
		    lp1.topMargin =310; 
		    CarElement1.setId(1001);
		    mainLayout2.addView(CarElement1,lp1); 
		    
			}else{
				TextView warningInfo = (TextView)findViewById(999);//文字变色
				if(warningInfo.getTextColors()==ColorStateList.valueOf(getResources().getColor(R.color.white)))
					warningInfo.setTextColor(getResources().getColor(R.color.color_red));
				else
					warningInfo.setTextColor(getResources().getColor(R.color.white));
				
				ImageView car1 = (ImageView)findViewById(1001);//下车上移
				 if(car1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.whitecar_1_warning).getConstantState())){
			        	car1.setBackgroundResource(R.drawable.whitecar_1);
			        }else{
			        	car1.setBackgroundResource(R.drawable.whitecar_1_warning);
			        }
				int top = car1.getTop() - 6;  
				RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)car1.getLayoutParams();  
				params1.topMargin=top;//改变位置   
				car1.setLayoutParams(params1);
				
				ImageView Mycar = (ImageView)findViewById(1000);
				int top1 = Mycar.getTop() - 2;  
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)Mycar.getLayoutParams();  
				params.topMargin=top1;//改变位置   
				Mycar.setLayoutParams(params);
				
				if(top ==142){
						if(displayWarningOrNot){
							g.list.clear();
							hideWarningWindow();	
							displayWarningOrNot = false;
							TTSAPI.getInstance().addPlayContent(
									"危险已经解除，您可以根据需要变更车道！",
									Const.AGPRIORITY_CRITICAL);	
						}
				}
						
				}
								
	        break;
		}
	}
}
