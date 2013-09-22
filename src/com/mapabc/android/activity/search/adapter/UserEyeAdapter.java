package com.mapabc.android.activity.search.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.NextRoadView;
import com.mapabc.android.activity.listener.EndPoitListener;
import com.mapabc.android.activity.listener.LocationListener;
import com.mapabc.android.activity.listener.StartPointListener;
import com.mapabc.android.activity.search.SearchUserEyeActivity;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.FavoriteAPI;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.UserEyeAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.favorite.FavoriteInfo;
import com.mapabc.naviapi.favorite.FavoritePageResults;
import com.mapabc.naviapi.search.AdminInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.PageOptions;
import com.mapabc.naviapi.type.UserEventPot;
import com.mapabc.naviapi.ue.UserEventPotPageResults;
import com.mapabc.naviapi.utils.AndroidUtils;

/**
 * @description: ExpandableList适配器，装载收藏夹数据
 * @author menglin.cao 2012-08-24
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class UserEyeAdapter extends BaseExpandableListAdapter{
	private static final String TAG = "UserEyeAdapter";
	private NSLonLat mlonlat = (NSLonLat) MapAPI.getInstance().getVehiclePos();
	private List<Map<String, Object>> mGroupData = null;
	private int mExpandedGroupLayout;
	private String[] mGroupFrom;
	private int[] mGroupTo;
	private List<List<Map<String, Object>>> mChildData = null;
	private int mChildLayout;
	private String[] mChildFrom;
	private int[] mChildTo;
	private Context context;
	private LayoutInflater mInflater;
	private static String temptel = "";
	private static UserEventPot poi;
	private static int width = 0;
	private ViewHolder holder;
	int ind = 0;
	public UserEyeAdapter(Context context,
			List<Map<String, Object>> groupData, int expandedGroupLayout,
			String[] groupFrom, int[] groupTo,
			List<List<Map<String, Object>>> childData, int childLayout,
			String[] childFrom, int[] childTo) {
		width = Utils.getCurScreenWidth(context);
		mGroupData = groupData;
		mExpandedGroupLayout = expandedGroupLayout;
		mGroupFrom = groupFrom;
		mGroupTo = groupTo;
		mChildData = childData;
		mChildLayout = childLayout;
		mChildFrom = childFrom;
		mChildTo = childTo;
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 获取子对象
	 */
	public Object getChild(int groupPosition, int childPosition) {
		Log.e(TAG, "=====getChild=====");
		return mChildData.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		Log.e(TAG, "=====getChildId=====");
		
		return childPosition;
	}

	/**
	 * 获取子layout
	 */
	public View newChildView(boolean isLastChild, ViewGroup parent) {
		return mInflater.inflate(mChildLayout, parent, false);
	}

	/**
	 * 创建偏好设置UI
	 * 
	 * 
	 *@param groupPosition
	 *            父ViewID
	 *@param childPosition
	 *            当前ViewID
	 *@param isLastChild
	 *            是否是最后一个View
	 *@param convertView
	 *            当前View对象
	 *@param parent
	 *            父View对象
	 *@return View 当前View对象
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Log.e(TAG, "======getChildView======");
		View v;
		if (convertView == null) {
			v = newChildView(isLastChild, parent);
		} else {
			v = convertView;
		}
		UserEyeAdapter.poi = (UserEventPot) mChildData.get(groupPosition).get(
				childPosition).get(groupPosition + "");
		setButtonEvent(v);

		for (int i = 0; i < mChildTo.length; i++) {
			final TextView tv = (TextView) v.findViewById(mChildTo[i]);
			switch (i) {
			case 0:
				String type = mChildData.get(groupPosition).get(childPosition).get(
						mChildFrom[i]).toString();
				String typeIdArr[] = context.getResources().getStringArray(R.array.usereyetypeid);
				String typeArr[] = context.getResources().getStringArray(R.array.usereyetype);
				int count = typeIdArr.length;
				for(int index =0;index<count;index++){
					if(typeIdArr[index].equals(type)){
						type = typeArr[index].toString();
					}
				}
				tv.setText(context.getString(R.string.editusereye_type)+type);
				break;
			case 1:
				int speed =(Short)(mChildData.get(groupPosition).get(childPosition).get(
						mChildFrom[i]));
				String speedStr="监控电子眼";
				if(speed!=0){
					speedStr += speed + "km";
				}
				tv.setText(context.getString(R.string.editusereye_speed)+speedStr);
				break;
			case 2:
				tv.setVisibility(View.VISIBLE);
				tv.setText(context.getString(R.string.editusereye_direct)+mChildData.get(groupPosition).get(childPosition).get(
						mChildFrom[i]));
				break;
			case 3:
				tv.setText(context.getString(R.string.searchpoiresult_strArea)
						+ mChildData.get(groupPosition).get(childPosition).get(
								mChildFrom[i]) + "");
				break;
			}
		}
		return v;
	}

	/**
	 * 为View内组建设置监听器
	 */
	private void setButtonEvent(View v) {
		// ////////收藏按钮///////////
		Button b = (Button) v.findViewById(R.id.child_save);
		b.setEnabled(false);
		// ////////删除按钮///////////
		b = (Button) v.findViewById(R.id.child_delete);
//		b.setOnClickListener(this);
		b.setOnClickListener(new OnClickListener() {
			UserEventPot  poi = UserEyeAdapter.poi;

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				alertDialog(poi);
			}
		});
		//////////编辑按钮////////////
		b = (Button) v.findViewById(R.id.child_edit);
//		b.setOnClickListener(this);
		b.setOnClickListener(new OnClickListener() {
			UserEventPot  poi = UserEyeAdapter.poi;
			public void onClick(View arg0) {
				Intent i = new Intent(
						Constants.ACTIVITY_EDITUSEREYE);
				Bundle b = new Bundle();
				b.putSerializable("poi", poi);
				i.putExtras(b);
				context.startActivity(i);
			}
		});
		SearchResultInfo poiInfo =new SearchResultInfo();
		poiInfo.lat = poi.latitude;
		poiInfo.lon = poi.longitude;
		poiInfo.name = poi.name;
		/////////////设置出发点///////////////////
		b = (Button) v.findViewById(R.id.child_startpoint);
		b.setOnClickListener(new StartPointListener(poiInfo));
		/////////////设置终点///////////////////////
		b = (Button)v.findViewById(R.id.child_endpoint);
		b.setOnClickListener(new EndPoitListener(poiInfo));
		b = (Button)v.findViewById(R.id.child_find);
		b.setOnClickListener(new LocationListener(poiInfo));
	}

	/**
	 * 创建提示窗口是否删除POI
	 */
	private void alertDialog(UserEventPot poi) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.common_tip);
		builder.setMessage(R.string.myfavorites_tip_message);
		UserEyeAdapter.poi = poi;
		builder.setPositiveButton(R.string.common_confirm,
				new DialogInterface.OnClickListener() {
			UserEventPot poi = UserEyeAdapter.poi;

					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if (1==UserEyeAPI.getInstance().delUserEye((int)poi.id)) {
							Toast toast=Toast.makeText(UserEyeAdapter.this.context,
									context.getText(R.string.myfavorites_delete_success),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
							RouteAPI.getInstance().delUserEyeData(poi.id, poi.longitude, poi.latitude);
						} else {
							Toast toast=Toast.makeText(UserEyeAdapter.this.context,
									context.getText(R.string.myfavorites_delete_fail),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
						}
						UserEyeAdapter.this.refrashData();
						
					}
				});
		builder.setNegativeButton(R.string.common_cancel, null);
		builder.show();
	}

	public int getChildrenCount(int groupPosition) {
		Log.e(TAG, "====getChildrenCount======");
		return mChildData.get(groupPosition).size();
	}

	/**
	 * 获取父对象
	 */
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mGroupData.get(groupPosition);
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mGroupData.size();
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	/**
	 * 获取顶层layout
	 */
	public View newGroupView(boolean isExpanded, ViewGroup parent) {
		return mInflater.inflate(mExpandedGroupLayout, parent, false);
	}

	public View getGroupView(int groupPosition, final boolean isExpanded,
			View convertView, ViewGroup parent) {
		RelativeLayout.LayoutParams lp1=null;//名称
		RelativeLayout.LayoutParams lp2=null;//行政区域
		RelativeLayout.LayoutParams lp3=null;//距离
		RelativeLayout layout=null;
		View v;
		if (convertView == null) {
			v = newGroupView(isExpanded, parent);
			holder=new ViewHolder();
			holder.txtDetailAddr=new NextRoadView(v.getContext());
			holder.txtDist=new TextView(v.getContext());
			holder.txtName=new TextView(v.getContext());
			v.setTag(holder); 
		} else {
			v = convertView;
			holder = (ViewHolder)v.getTag(); 
		}
		layout=(RelativeLayout)v.findViewById(R.id.poi_details);
		holder.txtName.setText(mGroupData.get(groupPosition).get(mGroupFrom[1])
				+ "");
		holder.txtName.setTextSize(20);
		holder.txtName.setTextColor(Color.WHITE);
		holder.txtName.setId(100);
		holder.txtDetailAddr.setWidth(width*9/10);
		holder.txtDetailAddr.setText(mGroupData.get(groupPosition).get(mGroupFrom[2])
				+ "");
		holder.txtDetailAddr.setTextSize(15);
		holder.txtDetailAddr.setTextColor(Color.GRAY);
		holder.txtDetailAddr.setId(101);
		holder.txtDist.setText(mGroupData.get(groupPosition).get(mGroupFrom[3])
				+ "");
		
		holder.txtDist.setTextSize(15);
		holder.txtDist.setTextColor(Color.GRAY);
		ImageView iv = (ImageView) v.findViewById(mGroupTo[0]);
		iv.setImageResource(R.drawable.common_list_icon);
		if(Utils.isLand(v.getContext())){
			lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);				
			layout.removeAllViews();
			layout.addView(holder.txtName,lp1);
			
			lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);				
			lp2.addRule(RelativeLayout.CENTER_VERTICAL);
			lp2.addRule(RelativeLayout.BELOW,100);
			layout.addView(holder.txtDetailAddr, lp2);
			
			lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp3.addRule(RelativeLayout.ALIGN_RIGHT);
			lp3.rightMargin=50;
			lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp3.addRule(RelativeLayout.BELOW,100);
			layout.addView(holder.txtDist, lp3);
		}else {
			int screenLevel=AndroidUtils.checkScreenResolution(v.getContext());
			switch (screenLevel) {
			case AndroidUtils.SCREEN_RESOLUTION_QVGA://低
				lp2 = new RelativeLayout.LayoutParams(130, RelativeLayout.LayoutParams.WRAP_CONTENT);
				break;
			case AndroidUtils.SCREEN_RESOLUTION_HVGA://中
				lp2 = new RelativeLayout.LayoutParams(160, RelativeLayout.LayoutParams.WRAP_CONTENT);
				break;
			case AndroidUtils.SCREEN_RESOLUTION_WVGA://高
				lp2 = new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT);
				break;
			case AndroidUtils.SCREEN_RESOLUTION_WFVGA://高
				lp2 = new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT);
				break;
			case AndroidUtils.SCREEN_RESOLUTION_UNKNOWN://未知
				lp2 = new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT);
				break;
			case AndroidUtils.SCREEN_RESOLUTION_SVGA://超高
				lp2 = new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT);
				break;
			default:
				break;
			}
			holder.txtDetailAddr.setSingleLine();
			holder.txtDetailAddr.setEllipsize(TruncateAt.MARQUEE);
			holder.txtDetailAddr.setFreezesText(true);
			layout.removeAllViews();
			lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);				
			layout.addView(holder.txtName,lp1);
			lp2.addRule(RelativeLayout.CENTER_VERTICAL);
			lp2.addRule(RelativeLayout.BELOW,100);
			lp2.addRule(RelativeLayout.CENTER_VERTICAL);
			layout.addView(holder.txtDetailAddr, lp2);
			
			lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp3.addRule(RelativeLayout.BELOW,100);
			lp3.addRule(Gravity.RIGHT);
			lp3.leftMargin=10;
			lp3.rightMargin=50;
			layout.addView(holder.txtDist, lp3);
			
		}
		return v;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
   public void refrashData(){
	   ind = 0;
	   mChildData=new ArrayList<List<Map<String, Object>>>();
	   mGroupData = new ArrayList<Map<String, Object>>() ;
	   getPOIData(1);
	   UserEyeAdapter.this.notifyDataSetChanged();
   }
	/**
	 * 调用底层接口获取收藏夹内POI，技术当前车位点与该POI的距离
	 */
	public  void getPOIData(int pageNo) {
		try {
			final UserEventPotPageResults pageResults = new UserEventPotPageResults();
			PageOptions pageoption = new PageOptions();
			pageoption.pageNo = pageNo;
			UserEyeAPI.getInstance().getPageUserEyes(new PageOptions(), pageResults);
			if(pageResults==null||pageResults.userEyes==null||pageResults.userEyes.length<1){
				((Activity)(this.context)).finish();
				return ;			}
			
					for (UserEventPot poi1: pageResults.userEyes) {
						SearchResultInfo poi = new SearchResultInfo();
						poi.lat = poi1.latitude;
						poi.lon = poi1.longitude;
						poi.name = poi1.name;
						String str_town = Utils.getDistrictByAdCode(poi, context);
						
						Map<String, Object> curGroupMap = new HashMap<String, Object>();
						mGroupData.add(curGroupMap);
						float distance = UtilAPI.getInstance().calculateDis(poi1.longitude,
								poi1.latitude,UserEyeAdapter.this.mlonlat.x, UserEyeAdapter.this.mlonlat.y);
						String dis="";
						if(distance>1000){
						   dis =String.format("%5.1f", distance/1000.00f)+ "km";
						}else{
							dis =String.format("%5.1f", distance)+ "m";
						}
						curGroupMap.put("list_mf_poi_position",
								R.drawable.common_list_icon);
						curGroupMap.put("list_poi_name", poi1.name);
						curGroupMap.put("list_poi_address",str_town);
						curGroupMap.put("list_route_length", dis);
						List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
						{
							Map<String, Object> curChildMap = new HashMap<String, Object>();
							children.add(curChildMap);
							curChildMap.put("tv_child_tel", poi1.type);
							curChildMap.put("tv_child_address", poi1.limitSpeed);
							curChildMap.put("tv_child_direction", poi1.angle);
							String array_town [] = str_town.split("，");
							if(array_town.length==2){
							curChildMap.put("tv_child_area", str_town.split("，")[1]);
							}else{
								curChildMap.put("tv_child_area", str_town.split("，")[0]);
							}
							curChildMap.put(ind + "", poi1);
						}
						mChildData.add(children);
						ind+=1;
					}
					if(pageNo<pageResults.pageCount){
						getPOIData(pageNo+1);
					}
		} catch (Exception ex) {

		}
		
	}
	class ViewHolder {
		public TextView txtDetailAddr,txtDist,txtName;
	}
	
}

