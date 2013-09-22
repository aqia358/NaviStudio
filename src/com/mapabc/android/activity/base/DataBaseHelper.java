package com.mapabc.android.activity.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @description: 查询关键字入库，查询管理
 * @author menglin.cao 2012-08-24
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "autonavi.db";
	private static final int DB_VERSION = 1;
	private static final String TB_NAME = "mapabc";
	
	public DataBaseHelper(Context ctx){
		super(ctx,DB_NAME,null,DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE ").append(TB_NAME).append(" (");
		sql.append(Constants.KEYWORD_ID+" INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,");
		sql.append(Constants.KEYWORD_NAME+" TEXT NOT NULL,");
		sql.append(Constants.KEYWORD_MY_KEYWORD+" TEXT NOT NULL").append(") ");
		db.execSQL(sql.toString());
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		StringBuffer sql = new StringBuffer();
		sql.append("DROP TABLE IF EXISTS ").append(TB_NAME);
		db.execSQL(sql.toString());
		onCreate(db);
	}
	
	public long insert(String[] info){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constants.KEYWORD_NAME, info[0]);
		values.put(Constants.KEYWORD_MY_KEYWORD, info[1]);
		String condition = Constants.KEYWORD_NAME + "='" + info[0] 
		                 +"' and "+ Constants.KEYWORD_MY_KEYWORD + "= '"+ info[1]+"'";
		Cursor cursor = query(condition);
		long result = -1;
		if(cursor != null && cursor.getCount()==0){
			result = db.insert(TB_NAME, null, values);
		}
		if(cursor != null){
			cursor.close();
		}
		/*if(db != null && db.isOpen()){
			db.close();
		}*/
		
		return result;
	}
	
	public Cursor query(String condition){
		Cursor cursor = null;
		try{
			SQLiteDatabase db = getWritableDatabase();
			String[] columns = {Constants.KEYWORD_ID,Constants.KEYWORD_NAME,Constants.KEYWORD_MY_KEYWORD};
			cursor = db.query(TB_NAME, columns, condition, null, null, null, Constants.KEYWORD_ID+" desc");
			if(cursor != null){
				cursor.moveToFirst();
			}
			
		}catch(Exception e){
			
		}
		
		return cursor;
	}

	@Override
	public synchronized void close() {
		try{
			SQLiteDatabase db = getWritableDatabase();
			if(db != null && db.isOpen()){
				db.close();
			}
			super.close();
		}catch(Exception e){
			
		}
		
	}
	
	

}
