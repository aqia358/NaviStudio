package com.mapabc.android.activity.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @��#��:DateTimeUtil.java
 * @description: ʱ�乤����
 * @author: changbao.wang
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */

public class DateTimeUtil {
	/*
	 * ��long��ʱ��ת��Ϊyyyy-MM-dd HH:mm:ssʱ��
	 * @para time
	 * @return String 
	 */
	public static String dateTimeToStr(long time) {
		String str = null;
		Date date =new Date(time);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date != null) {
			str = df.format(date);
		}
		return str;
	}
	/*
	 * ��ȡϵͳʱ�䣬ʱ���ʽ��yyyy-MM-dd HH:mm:ss
	 * 
	 */
	 public static String getSystemDateTime() {
		  // java.util.Date date=new java.util.Date(System.currentTimeMillis());
		   java.util.Calendar cal=java.util.Calendar.getInstance();
		   int year=cal.get(cal.YEAR);
		   int month=cal.get(cal.MONTH)+1;
		   int day=cal.get(cal.DAY_OF_MONTH);
		   int hours=cal.get(cal.HOUR_OF_DAY);
		   int min=cal.get(cal.MINUTE);
		   int sec=cal.get(cal.SECOND);
		  return year+"-"+format2Int(month)+"-"+format2Int(day)+" "+format2Int(hours)+":"+format2Int(min)+":"+format2Int(sec);
		 }
	 public static String getSystemDateTime1() {
		  // java.util.Date date=new java.util.Date(System.currentTimeMillis());
		   java.util.Calendar cal=java.util.Calendar.getInstance();
		   int year=cal.get(cal.YEAR);
		   int month=cal.get(cal.MONTH)+1;
		   int day=cal.get(cal.DAY_OF_MONTH);
		   int hours=cal.get(cal.HOUR_OF_DAY);
		   int min=cal.get(cal.MINUTE);
		   int sec=cal.get(cal.SECOND);
		  return year+format2Int(month)+format2Int(day)+format2Int(hours)+format2Int(min)+format2Int(sec);
		 }
	 private static String format2Int(int n){
		  String s=""+n;
		  if(s.length()<2){
		    s="0"+s;
		  }
		  return s;
		}
		private static String format3Int(int n){
		  String s=""+n;
		  if(s.length()<2){
		    s="00"+s;
		  }else if(s.length()<3){
		    s="0"+s;
		  }
		  return s;
		}
}
