package com.mapabc.android.activity.setting.adapter;

import android.R.string;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.utils.SettingForLikeTools;

/**
 * 偏好设置数据适配器
 * @author menglin.cao 2012-08-22
 *
 */
public class SettingListAdapter extends BaseAdapter {
	private static final String TAG = "SettingListAdapter";
	int count = 0;
	int slecet_position;
	private Context context;
	private LayoutInflater inflater;
	private  String[] nameArray = null;
	private  int[] imgArray = null;
	private String classType=null;

	public SettingListAdapter(Context context,String[] nameArray,int[] imgArray,String classType){
		this.context = context;
		this.classType=classType;
		this.inflater = LayoutInflater.from(context);
		this.nameArray=nameArray;
		this.imgArray=imgArray;
		count=nameArray.length;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.settingforlike_list_item,
						null);
				holder = new ViewHolder();
				holder.list_settingforlike_name = (TextView) convertView
						.findViewById(R.id.tv_settingforlike_name);
				holder.list_settingforlike_state = (TextView) convertView
						.findViewById(R.id.tv_settingforlike_state);
				holder.list_settingforlike_image = (ImageView) convertView
						.findViewById(R.id.iv_settingforlike_image);
				holder.list_settingforlike_image2 = (ImageView) convertView
						.findViewById(R.id.iv_settingforlike_image2);

				// holder.list_settingforlike_state
				// .setText(SettingForLikeActivity.this.statearray[position]);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.list_settingforlike_name
					.setText(nameArray[position]);
			holder.list_settingforlike_image
					.setImageResource(imgArray[position]);
			if(setSwitchStatus(position) == 1){
				holder.list_settingforlike_image2
				.setImageResource(R.drawable.common_list_image);
				holder.list_settingforlike_state.setText(Html.fromHtml(setStateColor(position)));
			}else{
				holder.list_settingforlike_image2
				.setImageResource(setSwitchStatus(position));
				holder.list_settingforlike_state.setText("");

			}
		
		} catch (Exception ex) {
			Log.e(TAG, "____ERROR___", ex);
		}
		return convertView;
	}
	
	/**
	 * 设置开关的状态
	 * @param position
	 * @return
	 */
	private int setSwitchStatus(int position){
		String htmlStr = this.getSysCurrentPara(position);
		String[] stateArr = context.getResources().getStringArray(R.array.stateitem);
		if(htmlStr.equals(stateArr[1])){
			return R.drawable.switch_on;
			//htmlStr = "<font color='green'>"+htmlStr+"</font>";
		}else if(htmlStr.equals(stateArr[0])){
			//htmlStr = "<font color='red'>"+htmlStr+"</font>";
			return R.drawable.switch_off;
		}
		return 1;
	}
	/**
	 * 设置开关的颜色，开为红色，关为红色。
	 * @param position
	 * @return
	 */
	
	private String setStateColor(int position){
		String htmlStr = this.getSysCurrentPara(position);
		String[] stateArr = context.getResources().getStringArray(R.array.stateitem);
		if(htmlStr.equals(stateArr[1])){
			htmlStr = "<font color='green'>"+htmlStr+"</font>";
		}else if(htmlStr.equals(stateArr[0])){
			htmlStr = "<font color='red'>"+htmlStr+"</font>";
		}
		return htmlStr;
	}
	
	/**
	 * 获取当前系统生效参数
	 * 
	 *@param index
	 *            listView索引
	 *@return String 
	 *            系统参数对应的汉字描述
	 *       
	 */
	public  String getSysCurrentPara(int index) {
		String result = "";
		String[] sarray = null;
		if (classType=="SettingMapActivity") {
			switch (index) {
			case 0:
				sarray = context.getResources().getStringArray(
						R.array.displaymodelitem);
				result= sarray[SettingForLikeTools.getMapModel(context)];
				break;
			case 1:
				int id = SettingForLikeTools.getDayOrNight(context);
				sarray = context.getResources().getStringArray(
						R.array.dayornightitem);
				Log.e(TAG, "array_id:"+id);
				result= sarray[id];
				break;
			case 2:
				int state1 = SettingForLikeTools.getIntSysPara(context, SettingForLikeTools.POWERMODEL,1);
				sarray = context.getResources().getStringArray(
						R.array.stateitem);
				if(state1==1){result= sarray[1];}else{result= sarray[0];}
				break;
			case 3:
				int state2 = SettingForLikeTools.getAutoGoBackToCar(context);
				sarray = context.getResources().getStringArray(
						R.array.stateitem);
				if(state2==1){result= sarray[1];}else{result= sarray[0];}
				break;
			case 4:
				sarray = context.getResources().getStringArray(
						R.array.mapcoloritem);
				result= sarray[SettingForLikeTools.getMapColorIndex(context)];
				break;
			default:
				break;
			}
		}else if (classType=="SettingRouteActivity") {
			switch (index) {
			case 0:
			    boolean state4 = SettingForLikeTools.isTalkDirection(context);
			    sarray = context.getResources().getStringArray(
					R.array.stateitem);
			    if(state4){result= sarray[1];}else{result= sarray[0];}
			    break;
			case 1:
				sarray = context.getResources().getStringArray(
						R.array.demospeeditem);
				result= sarray[SettingForLikeTools.getDemoSpeed(context)];
				break;
			case 2:
				  boolean res = SettingForLikeTools.isTTSOpen(context);
				  sarray = context.getResources().getStringArray(
							R.array.stateitem);
				  if(res){result= sarray[1];}else{result= sarray[0];}
				  break;
			case 3:
				int state3 = SettingForLikeTools.getOverSpeed(context);
				sarray = context.getResources().getStringArray(
						R.array.stateitem);
				if(state3==1){result= sarray[1];}else{result= sarray[0];}
				break;
			case 4:
				sarray =context.getResources().getStringArray(
						R.array.voicetypeitem);
				result= sarray[SettingForLikeTools.getRole(context)];
				break;
			case 5:
				boolean state5 = SettingForLikeTools.isEDogEnable(context);
				sarray = context.getResources().getStringArray(
						R.array.stateitem);
				if(state5){result= sarray[1];}else{result= sarray[0];}
				break;
			default:
				break;
			}
		}else if (classType=="SettingTrafficRadioActivity") {
			switch (index) {
			case 0:
//				int state = SettingForLikeTools.getFrontTrafficRadio(context);
//				sarray = context.getResources().getStringArray(
//						R.array.stateitem);
//				if(state==1){result= sarray[1];}else{result= sarray[0];}
//				break;
				
				int state2 = SettingForLikeTools.getUpdateState(context);
				sarray = context.getResources().getStringArray(
						R.array.stateitem);
				if(state2==0){result= sarray[1];}else{result= sarray[0];}
				break;
//			case 1:
//				int state1 = SettingForLikeTools.getSurroundTrafficRadio(context);
//				sarray = context.getResources().getStringArray(
//						R.array.stateitem);
//				if(state1==1){result= sarray[1];}else{result= sarray[0];}
//				break;
//			case 2:
//				int state2 = SettingForLikeTools.getUpdateState(context);
//				sarray = context.getResources().getStringArray(
//						R.array.stateitem);
//				if(state2==0){result= sarray[1];}else{result= sarray[0];}
//				break;
			}
		}
		return result;
	}
}

final class ViewHolder {
	TextView list_settingforlike_name, list_settingforlike_state;
	ImageView list_settingforlike_image, list_settingforlike_image2;
}