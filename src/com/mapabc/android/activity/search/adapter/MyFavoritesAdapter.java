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
import com.mapabc.android.activity.listener.AddFavoriteListener;
import com.mapabc.android.activity.listener.EndPoitListener;
import com.mapabc.android.activity.listener.LocationListener;
import com.mapabc.android.activity.listener.StartPointListener;
import com.mapabc.android.activity.search.MyFavoritesActivity;
import com.mapabc.android.activity.search.SearchUserEyeActivity;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.FavoriteAPI;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.favorite.FavoriteInfo;
import com.mapabc.naviapi.favorite.FavoritePageResults;
import com.mapabc.naviapi.search.AdminInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.type.PageOptions;
import com.mapabc.naviapi.utils.AndroidUtils;

/**
 * @description: ExpandableList适配器，装载收藏夹数据
 * @author menglin.cao 2012-08-24
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class MyFavoritesAdapter extends BaseExpandableListAdapter{
	private static final String TAG = "MyFavoritesAdapter";
	private NSLonLat mlonlat = (NSLonLat) MapAPI.getInstance().getVehiclePos();
	private List<Map<String, Object>>  mGroupData = null;
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
	private static FavoriteInfo poi;
	private static int width = 0;
	int ind = 0;
	private int searchType;//查询类别
	public MyFavoritesAdapter(Context context,
			List<Map<String, Object>> groupData, int expandedGroupLayout,
			String[] groupFrom, int[] groupTo,
			List<List<Map<String, Object>>> childData, int childLayout,
			String[] childFrom, int[] childTo,int searchType) {
		width = Utils.getCurScreenWidth(context);
		mGroupData = groupData;
		mExpandedGroupLayout = expandedGroupLayout;
		mGroupFrom = groupFrom;
		mGroupTo = groupTo;
		this.searchType = searchType;
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
		return mChildData.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
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
		View v;
		if (convertView == null) {
			v = newChildView(isLastChild, parent);
		} else {
			v = convertView;
		}
		MyFavoritesAdapter.poi = (FavoriteInfo) mChildData.get(groupPosition).get(
				childPosition).get(groupPosition + "");
		setButtonEvent(v);

		for (int i = 0; i < mChildTo.length; i++) {
			final TextView tv = (TextView) v.findViewById(mChildTo[i]);
			switch (i) {
			case 0:
				String temp = mChildData.get(groupPosition).get(childPosition)
						.get(mChildFrom[i])
						+ "";
				tv.setText(Html.fromHtml(context.getString(R.string.searchpoiresult_strTel)
						+ "<u>" + temp + "</u>"));
				if (temp != null && temp.length() > 0) {
					temptel = temp;
					Linkify.addLinks(tv, Linkify.PHONE_NUMBERS);
					temptel = "";
				} else {
				}
				break;
			case 1:
				tv.setText(context.getString(R.string.searchpoiresult_strAddr)
						+ mChildData.get(groupPosition).get(childPosition).get(
								mChildFrom[i]) + "");
				break;
			case 2:
				String district =  mChildData.get(groupPosition).get(childPosition).get(
						mChildFrom[i]) + "";
				String array_town [] = district.split(",");
				if(array_town.length==2){
					tv.setText(context.getString(R.string.searchpoiresult_strArea)
							+district.split(",")[1]);
				}else{
					tv.setText(context.getString(R.string.searchpoiresult_strArea)
							+district.split(",")[0]);
				}
				
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
		if(searchType==Constants.SEARCH_HISTORYARRIVE){
			b.setOnClickListener(new OnClickListener() {
				FavoriteInfo  poi = MyFavoritesAdapter.poi;
				@Override
				public void onClick(View v) {
					if(FavoriteAPI.getInstance().getFavoriteCount(Const.FAVORITE_HAUNT)>=200){
			    		Utils.showTipInfo(v.getContext(), R.string.favorite_full_tip);
			    	}else{
			    		poi.type = Const.FAVORITE_HAUNT;			    		
			    		if(FavoriteAPI.getInstance().addFavorite(poi)>0){
			    			Utils.showTipInfo(v.getContext(), R.string.favorite_successfully);
			    		}else{
			    			Utils.showTipInfo(v.getContext(), R.string.favorite_successfail);
			    		}
			    	}
				}
			});
		}else{			
			b.setEnabled(false);
		}
		// ////////删除按钮///////////
		b = (Button) v.findViewById(R.id.child_delete);
		b.setOnClickListener(new OnClickListener() {
			FavoriteInfo  poi = MyFavoritesAdapter.poi;

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				alertDialog(poi);
			}
		});
		//////////编辑按钮////////////
		b = (Button) v.findViewById(R.id.child_edit);
//		b.setOnClickListener(this);
		if(searchType==Constants.SEARCH_HISTORYARRIVE){
			b.setEnabled(false);
		}else{
			b.setOnClickListener(new OnClickListener() {
				FavoriteInfo  poi1 = MyFavoritesAdapter.poi;
				
				public void onClick(View arg0) {
					Intent i = new Intent(
							Constants.ACTIVITY_EDITPOI);
					Bundle b = new Bundle();
					b.putSerializable("poi", poi1);
					i.putExtras(b);
					context.startActivity(i);
				}
			});
		}
		SearchResultInfo poiInfo =new SearchResultInfo();
		Utils.getClsRearchResultInfo(poi, poiInfo);
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
	private void alertDialog(FavoriteInfo poi) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.common_tip);
		builder.setMessage(R.string.myfavorites_tip_message);
		MyFavoritesAdapter.poi = poi;
		builder.setPositiveButton(R.string.common_confirm,
				new DialogInterface.OnClickListener() {
			FavoriteInfo poi = MyFavoritesAdapter.poi;

					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						int i = FavoriteAPI.getInstance().delFavorite(poi.id);
						if (1==FavoriteAPI.getInstance().delFavorite(poi.id)) {
							Log.e(TAG, "删除的ID："+poi.id+","+poi.name);
							Toast toast=Toast.makeText(MyFavoritesAdapter.this.context,
									context.getText(R.string.myfavorites_delete_success),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
						} else {
							Toast toast=Toast.makeText(MyFavoritesAdapter.this.context,
									context.getText(R.string.myfavorites_delete_fail),
									Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
							toast.show();
						}
						MyFavoritesAdapter.this.reflashPOIData();
						
					}
				});
		builder.setNegativeButton(R.string.common_cancel, null);
		builder.show();
	}

	public int getChildrenCount(int groupPosition) {
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
		ViewHolder holder=null;
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
		FavoriteInfo favoriteinfo = (FavoriteInfo) mChildData.get(groupPosition).get(
				0).get(groupPosition + "");
		Log.e(TAG, "groupPosition:"+groupPosition+",name1:"+mGroupData.get(groupPosition).get(mGroupFrom[1]+ "")+",id:"+favoriteinfo.id+",favoriteinfo:"+favoriteinfo.name);
		
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

	public void reflashPOIData(){
		int count = FavoriteAPI.getInstance().getFavoriteCount(searchType);
		Log.e(TAG, "COUNT:"+count);
		mChildData=new ArrayList<List<Map<String, Object>>>();
		mGroupData = new ArrayList<Map<String, Object>>();
		ind = 0;
		getPOIData(1);
		MyFavoritesAdapter.this.notifyDataSetChanged();
	}
	/**
	 * 调用底层接口获取收藏夹内POI，技术当前车位点与该POI的距离
	 */
	public void getPOIData(int pageNo) {
		try {
			FavoritePageResults pageResults = new FavoritePageResults();
			PageOptions pageOptions = new PageOptions();
			pageOptions.pageNo =pageNo;
			FavoriteAPI.getInstance().getPageFavorites(pageOptions,searchType, pageResults);
			if(pageResults==null||pageResults.Favorites==null||pageResults.Favorites.length<1){
				((Activity)(this.context)).finish();
				return ;			}
			
			putInData(pageResults);
			if(pageNo == 2){
            	return ;
            }
			if(FavoriteAPI.getInstance().getFavoriteCount(searchType)>100){
				getPOIData(2);
			}
		} catch (Exception ex) {

		}
		
	}
private void putInData(FavoritePageResults pageResults){
    	
		for (FavoriteInfo data: pageResults.Favorites) {
			SearchResultInfo poi = new SearchResultInfo();
			Utils.getClsRearchResultInfo(data, poi);
			String district = Utils.getDistrictByAdCode(poi,context);
			
			Map<String, Object> curGroupMap = new HashMap<String, Object>();

			mGroupData.add(curGroupMap);
			float distance = UtilAPI.getInstance().calculateDis(MyFavoritesAdapter.this.mlonlat.x, MyFavoritesAdapter.this.mlonlat.y
					,data.longitude,data.latitude);
			String dis="";
			if(distance>1000){
			   dis =String.format("%5.1f", distance/1000.00f)+ "km";
			}else{
				dis =String.format("%5.1f", distance)+"m";
			}
			curGroupMap.put("list_mf_poi_position",
					R.drawable.common_list_icon);
			curGroupMap.put("list_poi_name", data.name);
			curGroupMap.put("list_poi_address",district);
			curGroupMap.put("list_route_length", dis);
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			{
				Map<String, Object> curChildMap = new HashMap<String, Object>();
				children.add(curChildMap);
				curChildMap.put("tv_child_tel", data.telephone);
				curChildMap.put("tv_child_address", data.address);
				String array_town [] = district.split("，");
				if(array_town.length==2){
				curChildMap.put("tv_child_area", district.split("，")[1]);
				}else{
					curChildMap.put("tv_child_area", district.split("，")[0]);
				}
				curChildMap.put(ind + "", data);
			}
			mChildData.add(children);
			ind+=1;
		}
    }
	class ViewHolder {
		public TextView txtDetailAddr,txtDist,txtName;
	}
}

