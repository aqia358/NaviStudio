package com.mapabc.android.activity.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.utils.IconManager;

/**
 * @description: 本地查询适配器
 * @author menglin.cao 2012-08-23
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchLocListAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private String[] searchlocitems;
	private IconManager iconMgr = IconManager.newInstance();
	public SearchLocListAdapter(Context context){
		layoutInflater = LayoutInflater.from(context);
		searchlocitems = context.getResources().getStringArray(R.array.searchlocitems);
	}
	@Override
	public int getCount() {
		return searchlocitems.length;
	}

	@Override
	public Object getItem(int position) {
		if(position < 0 || searchlocitems.length < position){
			return null;
		}
		return searchlocitems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.searchlocation_list_item, null);
			}
			
			ImageView categoryIconImageView = (ImageView) convertView.findViewById(R.id.iv_category_icon);
			categoryIconImageView.setImageResource(iconMgr.getSearchIconResId(position));
			TextView categoryNameTextView = (TextView) convertView.findViewById(R.id.tv_category_name);
			Object obj = getItem(position);
			if(obj != null){
				categoryNameTextView.setText(String.valueOf(obj));
			}
		}catch(Exception e){
		}
		
		return convertView;
	}

}