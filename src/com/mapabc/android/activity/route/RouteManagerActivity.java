package com.mapabc.android.activity.route;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.AutoNaviMap;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.MenuActivityFactory;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.base.RouteLayer;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.utils.ActivityStack;
import com.mapabc.android.activity.utils.SettingForLikeTools;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.MapView;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.type.Const;

/**
 * @description: 路径管理菜单UI
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class RouteManagerActivity extends BaseActivity implements OnItemClickListener{

	private static final String TAG = "RouteManagerActivity";
	private static String namearray[] = null;
	private static String descarray[] = null;
	ItemAdapter itemadapter = null;
	ProgressDialog pdg = null;
	private ListView routeListView;
	private LayoutInflater inflater;
	private static int INDEX;
	Handler r_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				refresh();
			}else if(msg.what==100){
				NaviControl.getInstance().isTalkStartNavi=true;
				NaviControl.getInstance().showNaviInfo1();//刷新导航界面
			}else if(msg.what==101){//修改规划原则算路失败
				RouteLayer routeLayer = new RouteLayer();
				routeLayer.deleteLayer();
				Utils.showTipInfo(RouteManagerActivity.this, R.string.navicontrol_find_path_failed);
			}
		}
	};

	public RouteManagerActivity() {

	}

	/**
	 * 数据刷新
	 */
	public void refresh() {
		if (itemadapter != null)
			itemadapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
//		NaviControl.getInstance().setR_Handler(null);
		super.onPause();
		//以下代码：解决从"路径详情"和"全程概览"横竖屏切换后再跳回到地图主界面时地图下方出现空白的bug.
		MapView m_mapView = AutoNaviMap.getInstance(getApplicationContext()).getMapView();
		int height = Utils.getCurScreenHeight(this);
		int width = Utils.getCurScreenWidth(this);
		m_mapView.destoryMap();
		m_mapView.initMap(width, height);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		NaviControl.getInstance().setR_Handler(r_handler);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setlayout();
	}

	public void setlayout() {
		setContentView(R.layout.routemanager_main_layout);
		TextView txtTopic = (TextView) findViewById(R.id.tv_topic);
		txtTopic.setText(R.string.menu_routeManager);
		this.inflater = LayoutInflater.from(this);
		namearray = getResources().getStringArray(R.array.routemanager_items);
		descarray = getResources()
				.getStringArray(R.array.routemanager_desc_items);
		ImageButton btnBack = (ImageButton) findViewById(R.id.ib_menu_back);
		btnBack.setOnClickListener(new BackListener(this, false, false));

		routeListView = (ListView) findViewById(R.id.lv_lstview_routeitem);
		itemadapter = new ItemAdapter();
		itemadapter.count = RouteManagerActivity.namearray.length;
		routeListView.setAdapter(itemadapter);
		routeListView.setOnItemClickListener(this);
	}

	/**
	 * 相应listview内的点击事件
	 * 
	 * @param position
	 *            索引
	 *@return
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.e(TAG, "position:" + position);
		/*
		if (position == 0) // 路径规划原则
		{
			final int planRule = SettingForLikeTools.getRouteCalcMode(this);
			INDEX = planRule;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getStringArray(
					R.array.routemanager_items)[0]);
			builder.setSingleChoiceItems(getResources().getStringArray(
					R.array.routemanager_routeplan_way), planRule, new OnClickListener() {

				public void onClick(DialogInterface arg0, int pos) {
					// TODO Auto-generated method stub
					INDEX = pos;
				}
			});
			builder.setPositiveButton(R.string.common_confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int pos) {
							// 规划原则是否改变
							if (planRule != INDEX) {
								SettingForLikeTools.setRouteCalcMode(
										RouteManagerActivity.this, INDEX);
								// //如果有路径需要重新计算路径
								if (RouteAPI.getInstance()
										.isRouteValid()&&NaviControl.getInstance().getCalculateType()==0) {
									alertDialog(INDEX);
								}
							}
						}
					});
			builder.setNegativeButton(R.string.common_cancel, null);
			builder.show();
		} else if(position == 1){//网络算路规则
			final int planRule = SettingForLikeTools.getRouteCalNetMode(this);
			INDEX = planRule;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getStringArray(
					R.array.routemanager_items)[1]);
			builder.setSingleChoiceItems(getResources().getStringArray(
					R.array.routemanager_netrouteplan_way), planRule, new OnClickListener() {

				public void onClick(DialogInterface arg0, int pos) {
					// TODO Auto-generated method stub
					INDEX = pos;
				}
			});
			builder.setPositiveButton(R.string.common_confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int pos) {
							// 规划原则是否改变
								if(INDEX>7){
									INDEX=+1;
								}
							if (planRule != INDEX) {
								SettingForLikeTools.setRouteCalNetMode(
										RouteManagerActivity.this, INDEX);
								// //如果有路径需要重新计算路径
								if (RouteAPI.getInstance()
										.isRouteValid()&&NaviControl.getInstance().getCalculateType()==1) {
									alertDialog(INDEX);
								}
							}
						}
					});
			builder.setNegativeButton(R.string.common_cancel, null);
			builder.show();
		}else */
		if (position == 0) // 开始模拟导航或者停止模拟导航
		{
			if (!RouteAPI.getInstance().isRouteValid()) {
				return;
			}
			if(NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI){
				//停止模拟导航
				NaviControl.getInstance().stopSimNavi();
				NaviControl.getInstance().routeInfo = null;
				Utils.intentEvent(Constants.INTENT_ACTION,
				Constants.INTENT_TYPE_STOP_SIMNAVI, null, null,
				RouteManagerActivity.this,
				Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			}else{
				//开始模拟导航
				Utils.intentEvent(Constants.INTENT_ACTION,
				Constants.INTENT_TYPE_START_SIMNAVI, null, null,
				RouteManagerActivity.this,
				Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			}
		}else if (position == 1) // 删除当前路径
		{

			if (!RouteAPI.getInstance().isRouteValid()) {
				return;
			}
			deleteRoute();
			
		}else if(position == 2){//轨迹管理
			Intent intent2 = new Intent();
			intent2.setAction(MenuActivityFactory
					.getRouteManagerActivityIntent(position+4));
			this.startActivity(intent2);
		}else if(position == 3){//交互式导航
			Intent intent2 = new Intent();
			intent2.setAction(MenuActivityFactory
					.getRouteManagerActivityIntent(position+4));
			this.startActivity(intent2);
		}

	}
	/**
	 * 删除当前路径
	 */
    private void deleteRoute(){
    	AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle(this.getResources().getString(R.string.common_tip));
		mBuilder.setMessage(this.getResources().getString(
				R.string.routemanager_delete_route));
		mBuilder.setPositiveButton(R.string.common_confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_REALNAVI){
							NaviControl.getInstance().stopRealNavi();
						}else if(NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_SIMNAVI){
							NaviControl.getInstance().stopSimNavi();
							MapAPI.getInstance().setVehiclePosInfo(RouteAPI.getInstance().getStartPoint(), 0);
						}
						MapAPI.getInstance().setMapCenter(MapAPI.getInstance().getVehiclePos());
						if(RouteAPI.getInstance().clearRoute()){
							
							RouteLayer r = new RouteLayer();
							r.deleteLayer();
						}
						itemadapter.notifyDataSetChanged();
					}
				});
		mBuilder.setNegativeButton(R.string.common_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		mBuilder.show();

    }
	private void alertDialog(final int index) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle(getResources().getString(R.string.common_tip));
		mBuilder.setMessage(getResources().getString(R.string.routemanager_routeplan_tip));
		mBuilder.setPositiveButton(R.string.common_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 重新计算路径
						reCalculatePath();
						
						// activity.resetTime();
					}
				}).setNegativeButton(R.string.common_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		mBuilder.show();
	}

	/**
	 * Listview适配器
	 */
	public class ItemAdapter extends BaseAdapter {
		int count = 0;
		int slecet_position;

		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * 给ListView内显示的对象赋值
		 * 
		 *@param position
		 *            索引
		 *@param convertView
		 *            显示的对象
		 *@param parent
		 *@return View 显示的对象
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			try {
				slecet_position = position;
				Holder holder = new Holder();
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.routemanager_list_layout,
							null);
				} else {
					holder = (Holder) convertView.getTag();
				}
				holder.list_name = (TextView) convertView
						.findViewById(R.id.tv_list_name);
				holder.list_desc = (TextView) convertView
						.findViewById(R.id.tv_list_desc);
				if (position == 0) {
					if (NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI) {
						holder.list_name.setText(R.string.routemanager_simnaviname);
						holder.list_desc.setText(R.string.routemanager_simnavidesc);
					} else {
						holder.list_name
								.setText(RouteManagerActivity.namearray[position]);
						holder.list_desc
								.setText(RouteManagerActivity.descarray[position]);
					}
				} else {
					holder.list_name
							.setText(RouteManagerActivity.namearray[position]);
					holder.list_desc
							.setText(RouteManagerActivity.descarray[position]);
				}

				convertView.setTag(holder);
			} catch (Exception ex) {
				Log.e(TAG, "ERROR", ex);
			}
			return convertView;
		}
	}

	final class Holder {
		TextView list_name, list_desc;
	}

	public void reCalculatePath() {
		// 第一步停止模拟导航
		if(NaviControl.getInstance().naviStatus == NaviControl.NAVI_STATUS_SIMNAVI){
            NaviControl.getInstance().stopSimNavi();
            MapAPI.getInstance().setVehiclePosInfo(RouteAPI.getInstance().getStartPoint(),MapAPI.getInstance().getVehicleAngle());
		}
        NaviControl.getInstance().stopRealNavi();
		// 清除路径
		RouteAPI.getInstance().clearRoute();
		NaviControl.getInstance().naviStatus = 0;

		pdg = new ProgressDialog(this);
		pdg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pdg.setIndeterminate(false);
		pdg.setCancelable(false);
		pdg.setMessage(getResources().getString(R.string.navicontrol_calculate_path));
		pdg.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Utils.intentEvent(Constants.INTENT_ACTION,
						Constants.INTENT_TYPE_CHANGEROUTEMODE, null, null,
						RouteManagerActivity.this,
						Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			}
		});
		pdg.show();

		new Thread() {
			@Override
			public void run() {
				RouteAPI.getInstance().setStartPoint(MapAPI.getInstance().getVehiclePos());
				int res = 0;
				if(NaviControl.getInstance().getCalculateType()==0){
					float angle =MapAPI.getInstance().getVehicleAngle();
					if(angle==0){
						angle=-1;
					}
				    res = RouteAPI.getInstance().routeCalculation(angle,
						SettingForLikeTools.getRouteCalcMode(RouteManagerActivity.this));
				}else{
					res = RouteAPI.getInstance().httpRouteCalculation(SettingForLikeTools.getRouteCalNetMode(RouteManagerActivity.this));
				}				
				if (res == 1) {
					NaviControl.getInstance().drawRoute();
					pdg.cancel();
					r_handler.sendEmptyMessage(100);
				} else {
					pdg.cancel();
					r_handler.sendEmptyMessage(101);
				}
			}
		}.start();
	}
	@Override
	public void onBackPressed() {
		try{
			super.onBackPressed();
			Intent intent = new Intent(Constants.ACTIVITY_NAVISTUDIOACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}catch(Exception e){
			
		}
	}
	
}
