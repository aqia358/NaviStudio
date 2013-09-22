package com.mapabc.android.activity.route.adapter;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.route.track.TraceListActivity;
import com.mapabc.android.activity.route.track.TrackManagerActivity;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.trace.TraceInfo;
/**
 * @description: 杆塔查询结果适配器
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class TracesAdapter extends BaseExpandableListAdapter {
	private static final String TAG="InfoExpandListAdapter";
	private LayoutInflater layoutFlater;
	private ExpandableListView expListView;
	private ViewHolder holder;
	private ChildViewHolder holder2;
	private TraceListActivity activity;
	ArrayList<TraceInfo> mTraces=new ArrayList<TraceInfo>();
	public TracesAdapter(TraceListActivity context,ExpandableListView expListView,ArrayList<TraceInfo> traces){
		this.activity=context;
		layoutFlater = LayoutInflater.from(context);
		this.expListView = expListView;
		this.mTraces=traces;

	}
	@Override
	public int getGroupCount() {
		return mTraces.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mTraces.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
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
//		try{
			if(convertView == null){
				holder=new ViewHolder();
				view = layoutFlater.inflate(R.layout.tracelist_group_layout, parent,false);
				holder.traceName=(TextView)view.findViewById(R.id.trace_name);
				view.setTag(holder); 
			}else {
				view=convertView;
				holder = (ViewHolder)view.getTag(); 
			}
			String trace_name=mTraces.get(groupPosition).name;
			holder.traceName.setText(trace_name);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
			View view;
			if(convertView == null){
				holder2=new ChildViewHolder();
				view = layoutFlater.inflate(R.layout.tracelist_child_layout, parent,false);
				holder2.btndelete=(Button)view.findViewById(R.id.btn_delete);
				holder2.btnpreview=(Button)view.findViewById(R.id.btn_preview);
				holder2.btncomeback=(Button)view.findViewById(R.id.btn_comeback);
				view.setTag(holder2);
			}else {
				view=convertView;
				holder2 = (ChildViewHolder)view.getTag(); 
			}
			holder2.btndelete.setOnClickListener(new MyBtnOnClickListener(groupPosition));
			holder2.btnpreview.setOnClickListener(new MyBtnOnClickListener(groupPosition));
			holder2.btncomeback.setOnClickListener(new MyBtnOnClickListener(groupPosition));
		   return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	class ViewHolder {
		public TextView traceName;
	}
	class ChildViewHolder {
		public Button btndelete,btnpreview,btncomeback;
	}
	class MyBtnOnClickListener implements android.view.View.OnClickListener{
		public int position;
		public MyBtnOnClickListener(int id){
			this.position=id;
		}
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_delete){
				Log.e("", "======onClick======");
				AlertDialog.Builder mBuilder=new AlertDialog.Builder(v.getContext());
				 mBuilder.setTitle(v.getContext().getResources().getString(R.string.common_tip));
				 mBuilder.setMessage(v.getContext().getResources().getString(R.string.trace_list_delete_trace));
				  	mBuilder.setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(position==0&&TrackManagerActivity.isRecording){
								//删除轨迹时，停止记录轨迹
								TraceAPI.getInstance().stopTraceRecord();
								TrackManagerActivity.isRecording=false;
								TrackManagerActivity.myapp.isRecordTrack=true;
								MapAPI.getInstance().delLayer(Constants.TRACE_POINT);
							}
							TraceAPI.getInstance().delTrace(mTraces.get(position).id);
							mTraces.clear();
							activity.getTraceInfo(mTraces);
							if(mTraces.size()==0){
								activity.finish();
								mTraces=null;
								return;
							}
							notifyDataSetChanged();
						}
					}).setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
				  	mBuilder.show();
			}else if(v.getId()==R.id.btn_preview){
				if (NaviControl.getInstance().naviStatus ==NaviControl. NAVI_STATUS_SIMNAVI||NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_REALNAVI){
					Toast.makeText(activity,"正在进行导航或模拟导航，不能预览轨迹" , Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("TRACEINFO", mTraces.get(position));
				intent.putExtra("TRACE_BUNDLE", bundle);
				intent.setClass(activity,com.mapabc.android.activity.route.track.TracePreviewActivity.class);
				activity.startActivity(intent);
				
			}else if(v.getId()==R.id.btn_comeback){
				
				if (NaviControl.getInstance().naviStatus ==NaviControl. NAVI_STATUS_SIMNAVI||NaviControl.getInstance().naviStatus==NaviControl.NAVI_STATUS_REALNAVI){
					Toast.makeText(activity,"正在进行导航或模拟导航，不能回放轨迹" , Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("TRACEINFO", mTraces.get(position));
				intent.putExtra("TRACE_BUNDLE", bundle);
				intent.setClass(activity,com.mapabc.android.activity.route.track.TracePlayBackActivity.class);
				activity.startActivity(intent);
			}
			
		}

		
	}

}
