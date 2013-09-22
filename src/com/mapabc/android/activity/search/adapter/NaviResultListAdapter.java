package com.mapabc.android.activity.search.adapter;


import com.mapabc.android.activity.R;
import com.mapabc.android.activity.setting.adapter.OtherFunctionListAdapter;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.AdminInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @description: 导航路线查询结果选择适配器
 * @author: guang.wang 2012-08-27
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class NaviResultListAdapter extends BaseAdapter {
	private boolean flag = true;
	private int count = 0;
	private Context context;
	private LayoutInflater inflater;
	private static String[] naviResultNameArr = null;
	private static int imageIdArr[] = { R.drawable.otherfunction_mapset,
		R.drawable.otherfunction_routeset,R.drawable.otherfunction_trafficradioset, R.drawable.otherfunction_help,
            R.drawable.otherfunction_about, R.drawable.otherfunction_exit };// 图片数组
	
	public NaviResultListAdapter(Context context){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		naviResultNameArr = context.getResources().getStringArray(
				R.array.naviresultitems);
		count = this.imageIdArr.length;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
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
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.naviresult_list_item, null);
		}
		if(flag){
			convertView.setBackgroundColor(Color.rgb(75,191,245));
			flag = false;
		}
		ImageView imageview = (ImageView) convertView
				.findViewById(R.id.iv_settingforlike_image);
		imageview.setImageResource(NaviResultListAdapter.imageIdArr[position]);
		convertView.setTag(R.id.iv_settingforlike_image, imageview);
		ImageView imageviewindicate = (ImageView) convertView.findViewById(R.id.iv_settingforlike_image2);
		imageviewindicate.setImageResource(R.drawable.common_list_image);
		convertView.setTag(R.id.iv_settingforlike_image2, imageviewindicate);
		TextView textview = (TextView) convertView
				.findViewById(R.id.tv_settingforlike_name);
		textview
				.setText(NaviResultListAdapter.naviResultNameArr[position]);
		convertView.setTag(R.id.tv_settingforlike_name, textview);
		return convertView;
	}

}
