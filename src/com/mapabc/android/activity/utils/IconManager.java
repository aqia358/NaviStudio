package com.mapabc.android.activity.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import com.mapabc.android.activity.R;

public class IconManager {
	private static IconManager iconMgr;
	private ArrayList<Integer> searchAroundTypeIconResId = new ArrayList<Integer>();
	private ArrayList<Integer> lstSearchIconResId = new ArrayList<Integer>();
	private IconManager(){}
	public static IconManager newInstance(){
		if(iconMgr == null){
			synchronized (IconManager.class) {
				if(iconMgr == null){
					iconMgr = new IconManager();
				}
			}
		}
		return iconMgr;
	}
	
	public void fetchIconsResId(Context context,String defType){
		fetchSearchAroundTypeIconsResId(context,defType);
		fetchSearchIconsResId(context,defType);
	}
	
	public void fetchSearchAroundTypeIconsResId(Context context,String defType){
		searchAroundTypeIconResId.addAll(fetchResId(context,defType,R.array.searcharound_type_icons));
	}
	
	public void fetchSearchIconsResId(Context context,String defType){
		lstSearchIconResId.addAll(fetchResId(context,defType,R.array.search_icons));
	}
	
	private ArrayList<Integer> fetchResId(Context context,String defType,int strArray){
		Resources res = context.getResources();
		ArrayList<Integer> lstTmp = new ArrayList<Integer>();
		String[] strIcons = res.getStringArray(strArray);
		if(strIcons == null){
			return lstTmp;
		}
		for(String iconname : strIcons){
			lstTmp.add(res.getIdentifier(iconname, "drawable", "com.mapabc.android.activity"));
		}
		return lstTmp;
	}
	
	public int getSearchAroundTypeIconResId(int pos){
		if(pos < 0 || pos >= searchAroundTypeIconResId.size()){
			return R.drawable.icon;
		}
		return searchAroundTypeIconResId.get(pos);
	}
	
	public int getSearchIconResId(int pos){
		if(pos < 0 || pos >= lstSearchIconResId.size()){
			return R.drawable.icon;
		}
		return lstSearchIconResId.get(pos);
	}

}
