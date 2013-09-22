package com.mapabc.android.activity.search.adapter;


import com.mapabc.android.activity.R;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.AdminInfo;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * @description: 城市选择适配器
 * @author: guang.wang 2012-08-27
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class CityExpandListAdapter extends BaseExpandableListAdapter {
	@Override
	public int getGroupCount() {
		return SearchAPI.getInstance().getProviceCount();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		AdminInfo adInfo = new AdminInfo();
		SearchAPI.getInstance().getProvince(groupPosition, adInfo);
		return adInfo.subCount;
	}

	@Override
	public Object getGroup(int groupPosition) {
		AdminInfo adInfo = new AdminInfo();
		SearchAPI.getInstance().getProvince(groupPosition, adInfo);
		return adInfo.name;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		AdminInfo adInfo = new AdminInfo();
		SearchAPI.getInstance().getProvince(groupPosition, adInfo);
		SearchAPI.getInstance().getCity(adInfo.code, childPosition, adInfo);
		return adInfo.name;
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
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView parentView = null;
		if(convertView==null){
			parentView = getGenericView(parent.getContext());
		}else{
			parentView = (TextView)convertView;
		}
		AdminInfo adInfo = new AdminInfo();
		SearchAPI.getInstance().getProvince(groupPosition, adInfo);
		parentView.setText(adInfo.name);
		return parentView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		AdminInfo adInfo = new AdminInfo();
		SearchAPI.getInstance().getProvince(groupPosition, adInfo);
		SearchAPI.getInstance().getCity(adInfo.code, childPosition, adInfo);
		TextView childView = null;
		if(convertView==null){
			childView = getGenericView(parent.getContext());
		}else{
			childView = (TextView)convertView;
		}
		childView.setPadding(50, 0, 0, 0);
		childView.setText(adInfo.name);
		return childView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private TextView getGenericView(Context context) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(10, 0, 0, 0);
		textView.setTextAppearance(context, R.style.district_name);
		return textView;
	}

}
