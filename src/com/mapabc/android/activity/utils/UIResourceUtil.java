package com.mapabc.android.activity.utils;

import com.mapabc.naviapi.MapAPI;

public class UIResourceUtil {
	
	public static String getScaleText(int iScale)
	{
    	switch(iScale)
    	{
    	case 3:
    		return "800km";
    	case 4:
    		return "400km";
    	case 5:
    		return "200km";
    	case 6:
    		return "100km";
    	case 7:
    		return "50km";
    	case 8:
    		return "25km";
    	case 9:
    		return "13km";
    	case 10:
    		return "6km";
    	case 11:
    		return "3km";
    	case 12:
    		return "1.5km";
    	case 13:
    		return "800m";
    	case 14:
    		return "400m";
    	case 15:
    		return "200m";
    	case 16:
    		return "100m";
    	case 17:
    		return "50m";
    	case 18:
    		return "25m";
    	case 19:
    		return "10m";
    	case 20:
    		return "5m";
    	default:
    		return "25m";
    	}
	}
}
