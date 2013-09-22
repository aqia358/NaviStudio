package com.mapabc.android.activity.search.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.NextRoadView;
import com.mapabc.android.activity.listener.AddFavoriteListener;
import com.mapabc.android.activity.listener.EndPoitListener;
import com.mapabc.android.activity.listener.LocationListener;
import com.mapabc.android.activity.listener.StartPointListener;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.search.AdminInfo;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.NSLonLat;
import com.mapabc.naviapi.utils.AndroidUtils;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 查询结果适配器
 * @author menglin.cao 2012-08-27
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class InfoExpandListAdapter extends BaseExpandableListAdapter {
	private static final String TAG="InfoExpandListAdapter";
	private List<SearchResultInfo> lstData;
	private LayoutInflater layoutFlater;
	private ExpandableListView expListView;
	private ViewHolder holder;
	private ChildViewHolder childViewHolder;
	NSLonLat mlonlat = (NSLonLat) MapAPI.getInstance().getVehiclePos();
	private Context context;
	public InfoExpandListAdapter(Context context, List<SearchResultInfo> lstData,ExpandableListView expListView){
		layoutFlater = LayoutInflater.from(context);
		this.lstData = lstData;
		this.expListView = expListView;
		this.context = context;
	}
	@Override
	public int getGroupCount() {
		return lstData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return lstData.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return lstData.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view;
			RelativeLayout.LayoutParams lp3=null;
			RelativeLayout.LayoutParams lp2=null;
			SearchResultInfo data= (SearchResultInfo) getGroup(groupPosition);
			RelativeLayout layout=null;
			if(convertView == null){
				holder=new ViewHolder();
				view = layoutFlater.inflate(R.layout.poi_result_info, parent,false);
				holder.detailAddrTextView=new NextRoadView(view.getContext());
				holder.distTextView=new TextView(view.getContext());
				holder.detailAddrTextView.setId(1);
				holder.nameTextView = (TextView) view.findViewById(R.id.tv_poi_result_name);
				view.setTag(holder); 
				
			}else {
				view=convertView;
				holder = (ViewHolder)view.getTag(); 
			}
			int width = Utils.getCurScreenWidth(view.getContext());
			holder.nameTextView.setText(data.name);
			holder.nameTextView.setWidth((int) (width*0.9));
			layout=(RelativeLayout)view.findViewById(R.id.rl_poi_result);
			String district = Utils.getDistrictByAdCode(data, context);
			if(Utils.isLand(view.getContext())){
				lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);				
				lp2.addRule(RelativeLayout.CENTER_VERTICAL);
				layout.removeAllViews();
				layout.addView(holder.detailAddrTextView, lp2);
				lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp3.addRule(RelativeLayout.ALIGN_RIGHT);
				lp3.rightMargin=50;
				lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				layout.addView(holder.distTextView, lp3);
			}else {
				int screenLevel=AndroidUtils.checkScreenResolution(view.getContext());
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
				holder.detailAddrTextView.setSingleLine();
				holder.detailAddrTextView.setEllipsize(TruncateAt.MARQUEE);
				holder.detailAddrTextView.setFreezesText(true);
				lp2.addRule(RelativeLayout.CENTER_VERTICAL);
				layout.removeAllViews();
				layout.addView(holder.detailAddrTextView, lp2);
				lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp3.addRule(Gravity.RIGHT);
				lp3.leftMargin=10;
				lp3.rightMargin=50;
				layout.addView(holder.distTextView, lp3);
				
			}
			holder.detailAddrTextView.setText(district);
			float dist = UtilAPI.getInstance().calculateDis(mlonlat.x, mlonlat.y, data.lon,data.lat);
			String strDist ="";
			if(dist < 1000){
				String temp=String.valueOf(dist);
				Log.e(TAG, "temp:"+temp);
				if(temp.contains(".")){
					int index=temp.indexOf(".");
					Log.e(TAG, "index:"+index);
					strDist=temp.substring(0, index)+Utils.getValue(view.getContext(),R.string.searchpoiresult_unit_m);
				}else {
					strDist = temp+Utils.getValue(view.getContext(),R.string.searchpoiresult_unit_m);
				}
			}else{
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("#.00");
				strDist = df.format((double) dist / 1000.0D)+Utils.getValue(view.getContext(),R.string.searchpoiresult_unit_km);
			}
			holder.distTextView.setText(strDist);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		try{
			if(convertView == null){
				childViewHolder=new ChildViewHolder();
				convertView = layoutFlater.inflate(R.layout.poi_result_sub_info, parent,false);
				childViewHolder.addrTextView=(TextView) convertView.findViewById(R.id.tv_subpoi_addr);
				childViewHolder.districtTextView=(TextView) convertView.findViewById(R.id.tv_subpoi_district);
				childViewHolder.telTextView = (TextView) convertView.findViewById(R.id.tv_subpoi_tel);
				childViewHolder.favoriteButton = (Button) convertView.findViewById(R.id.btn_subpoi_fav);
				childViewHolder.locationButton = (Button) convertView.findViewById(R.id.btn_subpoi_location);
				childViewHolder.startPointButton = (Button) convertView.findViewById(R.id.btn_subpoi_startPoint);
				childViewHolder.endPointButton = (Button) convertView.findViewById(R.id.btn_subpoi_endPoint);
				convertView.setTag(childViewHolder);
			}else {
				childViewHolder = (ChildViewHolder)convertView.getTag(); 
			}
			
			SearchResultInfo data = (SearchResultInfo) getChild(groupPosition,childPosition);
			String addr = data.addr;
			childViewHolder.addrTextView.setText(Utils.getValue(convertView.getContext(),
					R.string.searchpoiresult_strAddr) + addr);
	
			AdminInfo cityinfo = new AdminInfo();
			boolean re = SearchAPI.getInstance().getADInfoByCode(data.adCode+"", cityinfo);
			Log.e("***", re+"");
			String district = context.getString(R.string.searchresult_district);
			if(re){
				district =cityinfo.name;
			}
			childViewHolder.districtTextView.setText(Utils.getValue(convertView.getContext(),
					R.string.searchpoiresult_strArea) + district);
			
			childViewHolder.telTextView.setText(Utils.getValue(convertView.getContext(), R.string.searchpoiresult_strTel)
					+ data.telephone);
			Linkify.addLinks(childViewHolder.telTextView, Linkify.PHONE_NUMBERS);
			
			childViewHolder.favoriteButton.setOnClickListener(new AddFavoriteListener(data));
			childViewHolder.locationButton.setOnClickListener(new LocationListener(data));
			childViewHolder.startPointButton.setOnClickListener(new StartPointListener(data));
			childViewHolder.endPointButton.setOnClickListener(new EndPoitListener(data));
		}catch(Exception e){
			Log.e("poi info list", e.toString());
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	class ViewHolder {
		public TextView detailAddrTextView,distTextView,nameTextView;
	}
	class ChildViewHolder {
		public TextView addrTextView,districtTextView,telTextView;
		public Button favoriteButton,locationButton,startPointButton,endPointButton;
	}

}
