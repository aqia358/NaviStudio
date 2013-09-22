package com.mapabc.android.activity.setting.adapter;

import com.mapabc.android.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 更多功能数据适配器
 * @author menglin.cao 2012-08-21
 *
 */
public class OtherFunctionListAdapter extends BaseAdapter {

	private int count = 0;
	private Context context;
	private LayoutInflater inflater;
	private static String[] otherFunctionNameArr = null;
	private static int imageIdArr[] = { R.drawable.otherfunction_mapset,
		R.drawable.otherfunction_routeset,R.drawable.otherfunction_trafficradioset, R.drawable.otherfunction_help,
            R.drawable.otherfunction_about, R.drawable.otherfunction_exit };// 图片数组
	
	public OtherFunctionListAdapter(Context context){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		otherFunctionNameArr = context.getResources().getStringArray(
				R.array.otherfunctionitems);
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
					.inflate(R.layout.otherfunction_list_item, null);
		}
		ImageView imageview = (ImageView) convertView
				.findViewById(R.id.iv_function_img);
		imageview.setImageResource(OtherFunctionListAdapter.imageIdArr[position]);
		convertView.setTag(R.id.iv_function_img, imageview);
		TextView textview = (TextView) convertView
				.findViewById(R.id.tv_function_name);
		textview
				.setText(OtherFunctionListAdapter.otherFunctionNameArr[position]);
		convertView.setTag(R.id.tv_function_name, textview);
		return convertView;
	}

}
