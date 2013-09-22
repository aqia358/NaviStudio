package com.mapabc.android.activity.route.track.adapter;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.setting.adapter.OtherFunctionListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackManagerAdapter extends BaseAdapter {
	private int count = 0;
	private Context context;
	private LayoutInflater inflater;
	private String[] trackManagerNameArr;
	private int[] imageIdArr;
	
	public TrackManagerAdapter(Context context,String[] trackManagerNameArr,int[] imageIdArr){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.trackManagerNameArr=trackManagerNameArr;
		this.imageIdArr=imageIdArr;
		count = trackManagerNameArr.length;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.otherfunction_list_item, null);
		}
		ImageView imageview = (ImageView) convertView
				.findViewById(R.id.iv_function_img);
		imageview.setImageResource(imageIdArr[position]);
		convertView.setTag(R.id.iv_function_img, imageview);
		TextView textview = (TextView) convertView
				.findViewById(R.id.tv_function_name);
		textview
				.setText(trackManagerNameArr[position]);
		convertView.setTag(R.id.tv_function_name, textview);
		return convertView;
	}

}
