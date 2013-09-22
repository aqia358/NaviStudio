package com.mapabc.android.activity.search.adapter;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.utils.IconManager;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.TypeInfo;

/**
 * @description: 查询类别选择适配器
 * @author: guang.wang 2012-09-03
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class TypeExpandListAdapter extends BaseExpandableListAdapter {
	private LayoutInflater layoutInflater;
	private IconManager iconMgr = IconManager.newInstance();
	@Override
	public int getGroupCount() {
		return SearchAPI.getInstance().getOTypeCount();//得到主类别的个数
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		TypeInfo typeInfo = new TypeInfo();
		SearchAPI.getInstance().getOType(groupPosition,typeInfo);
		return typeInfo.subCount;
	}

	@Override
	public Object getGroup(int groupPosition) {
		TypeInfo typeInfo = new TypeInfo();
		SearchAPI.getInstance().getOType(groupPosition,typeInfo);
		return typeInfo.name;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		TypeInfo typeInfo = new TypeInfo();
		SearchAPI.getInstance().getOType(groupPosition,typeInfo);
		SearchAPI.getInstance().getSType(typeInfo.code, childPosition, typeInfo);
		return typeInfo.name;
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
		if(convertView == null){
			layoutInflater = LayoutInflater.from(parent.getContext());
			convertView = layoutInflater.inflate(R.layout.typeexpandlist, null);
		}
		TypeInfo typeInfo = new TypeInfo();
		SearchAPI.getInstance().getOType(groupPosition,typeInfo);
		TextView categoryNameTextView = (TextView) convertView.findViewById(R.id.tv_searcharoundtype_name);
		categoryNameTextView.setText(typeInfo.name);
		ImageView categoryIconImageView = (ImageView) convertView.findViewById(R.id.iv_searcharoundtype_icon);
		categoryIconImageView.setImageResource(iconMgr.getSearchAroundTypeIconResId(groupPosition));
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView childView=null;
		if(convertView==null){
			childView=getGenericView(parent.getContext());
		}else{
			childView =(TextView) convertView;
		}
		TypeInfo typeInfo = new TypeInfo();
		SearchAPI.getInstance().getOType(groupPosition,typeInfo);
		SearchAPI.getInstance().getSType(typeInfo.code, childPosition, typeInfo);
		 childView = getGenericView(parent.getContext());
		childView.setPadding(50, 0, 0, 0);
		childView.setText(typeInfo.name);
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
