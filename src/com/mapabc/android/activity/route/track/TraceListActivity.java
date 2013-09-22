package com.mapabc.android.activity.route.track;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.route.adapter.TracesAdapter;
import com.mapabc.naviapi.MapAPI;
import com.mapabc.naviapi.TraceAPI;
import com.mapabc.naviapi.trace.TraceInfo;
import com.mapabc.naviapi.trace.TraceInfoPageResults;
import com.mapabc.naviapi.type.PageOptions;

public class TraceListActivity extends Activity{
	private static final String TAG="TraceListActivity";
	private ExpandableListView traceLV;
	ArrayList<TraceInfo> traces=new ArrayList<TraceInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "==========onCreate============");
		setContentView(R.layout.tracelist_activity);
		initTop();
		traceLV=(ExpandableListView)findViewById(R.id.trace_elv);
		getTraceInfo(traces);
		if(traces.size()==0){
			Toast.makeText(this, getString(R.string.trace_notrace), Toast.LENGTH_SHORT).show();
			finish();
			return ;
		}
		TracesAdapter adapter=new TracesAdapter(this, traceLV,traces);
		traceLV.setAdapter(adapter);
		traceLV.setIndicatorBounds(1000, 1000);
	}
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.trace_list);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this, false, false));
	}
	public boolean onPrepareOptionsMenu(Menu menu) {   
		menu.clear();
		menu.add(0, 0, 0, this.getResources().getString(R.string.menu_deleteAll)).setIcon(R.drawable.common_menu_icon_clear);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        
        String clear = this.getResources().getString(R.string.menu_deleteAll);
        if(item.getTitle().toString().equals(clear)){
        	AlertDialog.Builder mBuilder=new AlertDialog.Builder(TraceListActivity.this);
			 mBuilder.setTitle(this.getResources().getString(R.string.common_tip));
			 mBuilder.setMessage(this.getResources().getString(R.string.myfavorites_delete_poi_message));
			  	mBuilder.setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(traces!=null&&traces.size()>0){
							int size=traces.size();
							for(int i=0;i<size;i++){
								TraceAPI.getInstance().delTrace(traces.get(i).id);
							}
							traces.clear();
							traces=null;
							if(TrackManagerActivity.isRecording){
								//删除轨迹时，停止记录轨迹
								TraceAPI.getInstance().stopTraceRecord();
								TrackManagerActivity.isRecording=false;
								TrackManagerActivity.myapp.isRecordTrack=true;
								MapAPI.getInstance().delLayer(Constants.TRACE_POINT);
							}
							finish();
						}
						
					}
				}).setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
			  	mBuilder.show();
        	return false;
        }
        return true;
	}
	public TraceInfoPageResults getTraceInfo(ArrayList<TraceInfo> traces){
		PageOptions pageOptions=new PageOptions();
		pageOptions.pageNo=1;
		pageOptions.pageSize=100;
		TraceInfoPageResults pageResults=new TraceInfoPageResults();
		int a=TraceAPI.getInstance().getPageTrace(pageOptions, pageResults);
		Log.e(TAG, "============="+a);
		if(a==1){
			if(pageResults.traceInfos!=null&&pageResults.traceInfos.length>0){
				int size=pageResults.traceInfos.length;
				for(int i=0;i<size;i++){
					traces.add(pageResults.traceInfos[i]);
				}
			}else {
				return null;
			}
		}
		return pageResults;
	}
	

}
