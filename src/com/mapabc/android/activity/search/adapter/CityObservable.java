package com.mapabc.android.activity.search.adapter;

import java.util.Observable;

/**
 * @description: 
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class CityObservable extends Observable {
	private static CityObservable cityObservable;
	private int cityNum;
	private CityObservable(){}
	public static CityObservable newInstance(){
		if(cityObservable == null){
			synchronized(CityObservable.class){
				if(cityObservable == null){
					cityObservable = new CityObservable();
				}
			}
		}
		return cityObservable;
	}
	public int getCityNum() {
		return cityNum;
	}
	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
		setChanged();
		notifyObservers(cityNum);
	}
	
}
