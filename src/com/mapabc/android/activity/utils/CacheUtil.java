package com.mapabc.android.activity.utils;

import java.util.HashMap;
import java.util.Map;

public class CacheUtil {
	private static CacheUtil cache;
	private Map<String,Object> tmpCache = new HashMap<String,Object>();
	private CacheUtil(){}
	
	public static CacheUtil newInstance(){
		if(cache == null){
			synchronized(CacheUtil.class){
				if(cache == null){
					cache = new CacheUtil();
				}
			}
		}
		return cache;
	}
	
	public void put(String key,Object obj){
		tmpCache.put(key, obj);
	}
	
	public Object get(String key){
		return tmpCache.get(key);
	}
}
