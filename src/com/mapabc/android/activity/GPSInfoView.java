package com.mapabc.android.activity;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.mapabc.android.activity.base.NaviControl;
import com.mapabc.android.activity.listener.DisPatchInfo;
import com.mapabc.android.activity.listener.ReceiveInfo;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.route.GPSRouteInfo;
import com.mapabc.naviapi.route.GpsInfo;
import com.mapabc.naviapi.route.SatInfo;
import com.mapabc.naviapi.route.SystemTime;
import com.mapabc.naviapi.utils.AndroidUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.GpsStatus.NmeaListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * @description: 绘制星历图
 * @author: changbao.wang 2011-11-22
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class GPSInfoView extends View implements ReceiveInfo {
	private static final String TAG = "GPSInfoView";
	
	private static final int UPDATEGPSINFO = 106;// 更新GPS信息
	

	Toast mStatusPrompt = null;
	Bitmap earth = null;
	Bitmap satelliteImg = null;
	int screenWidth, screenHeight;

	Context context;
	public static float[] mElevation_90 = new float[15];
	public static float[] mX = new float[15];
	public static float[] mY = new float[15];
//	public static float mBitmapAdjustment;
	private Paint mLinePaint;
	private Paint mThinLinePaint;
	private Paint mBarPaintUsed;
	private Paint mBarPaintUnused;
	private Paint mBarPaintNoFix;
	private Paint mBarOutlinePaint;
	private Paint mTextPaint;
	Paint textpaint = new Paint();
	float centerY;
	float centerX;
	GpsInfo pstGPSInfo = null;
	String date = "2011年11月22日";
	String time = "00:00:00";
	String num = "0";
	String direction = "0";
	String speed = "0 km/h";
	
	/*
	 * 接收GPS信息刷新星历图
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATEGPSINFO:
				pstGPSInfo = (GpsInfo) msg.obj;
				GPSInfoView.this.getGPSDesc();
				GPSInfoView.this.invalidate();
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	public void DoGpsInfo(GpsInfo gpsInfo) {
		Message msg=Message.obtain();
		msg.what=UPDATEGPSINFO;
		msg.obj=gpsInfo;
		handler.sendMessage(msg);
	}
	
	
	public GPSInfoView(Context context, AttributeSet att) {
		super(context, att);
		this.context = context;
		// TODO Auto-generated constructor stub
		earth = ((BitmapDrawable) getResources().getDrawable(R.drawable.gpsinfoview_earth))
				.getBitmap();
		satelliteImg = ((BitmapDrawable) getResources().getDrawable(
				R.drawable.gpsinfoview_zoom_cursor)).getBitmap();
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		SystemTime object = new SystemTime();
		RouteAPI.getInstance().getSystemTime(object);
		date = object.year+"年"+object.month+"月"+object.day+"日";
		init();

	}

	/*
	 * 初始化Paint
	 */
	private void init() {
		textpaint.setAntiAlias(true);// 抗锯齿
		textpaint.setStrokeCap(Paint.Cap.ROUND);// 圆头线
		textpaint.setStrokeJoin(Paint.Join.ROUND);// 去除直角
		mLinePaint = new Paint();
		mLinePaint.setColor(0xFFDDDDDD);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(1.0f);

		mThinLinePaint = new Paint(mLinePaint);
		mThinLinePaint.setStrokeWidth(0.5f);

		mBarPaintUsed = new Paint();
		mBarPaintUsed.setColor(0xFF00BB00);
		mBarPaintUsed.setAntiAlias(true);
		mBarPaintUsed.setStyle(Style.FILL);
		mBarPaintUsed.setStrokeWidth(1.0f);

		mBarPaintUnused = new Paint(mBarPaintUsed);
		mBarPaintUnused.setColor(0xFFFFCC33);

		mBarPaintNoFix = new Paint(mBarPaintUsed);
		mBarPaintNoFix.setStyle(Style.STROKE);

		mBarOutlinePaint = new Paint();
		mBarOutlinePaint.setColor(0xFFFFFFFF);
		mBarOutlinePaint.setAntiAlias(true);
		mBarOutlinePaint.setStyle(Style.STROKE);
		mBarOutlinePaint.setStrokeWidth(1.0f);

		mTextPaint = new Paint();
		mTextPaint.setColor(0xFFFFFFFF);
		mTextPaint.setTextSize(20.0f);
		mTextPaint.setTextAlign(Align.CENTER);
		DisPatchInfo.getInstance().addGpsInfoListener("GPSInfoView", this);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		try {
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			int screen = AndroidUtils.checkScreenResolution(context);
			if (screen == AndroidUtils.SCREEN_RESOLUTION_HVGA) {
				// 中分辨率
				this.drawForMdpi(canvas);
			} else if (screen == AndroidUtils.SCREEN_RESOLUTION_QVGA) {
				// 低分辨率
				this.drawForLdpi(canvas);
			} else {
				// 高分辨率
				this.drawForHdpi(canvas);
			}
		} catch (Exception ex) {
			Log.e(TAG, "ERR", ex);
		}
	}

	/*
	 * 高分辨率绘制
	 */
	private void drawForHdpi(Canvas canvas) {
		if (screenHeight > screenWidth) {// 竖向
			String date_str = context.getString(R.string.gpsinfo_date) + date;
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(20);
			float width = (screenWidth - textpaint.measureText(date_str)) / 2;
			float height = 150;
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(20);
			// ///////画日期/////////
			canvas.drawText(date_str, width, height, textpaint);
			// ///////画是否有效定位/////////
			if (NaviControl.mgpsStatus == 3) {
				textpaint.setColor(0xFF3BA500);
				canvas.drawText(context.getString(R.string.gpsinfo_goodstate),
						width, height - 25, textpaint);
			} else {
				textpaint.setColor(0xFFEE0022);
				canvas.drawText(context.getString(R.string.gpsinfo_badstate),
						width, height - 25, textpaint);
			}
			// ///////画时间/////////
			textpaint.setColor(0xFFfffff0);
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_time) + time,
					width, height, textpaint);
			// ////////画卫星颗数/////////////
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_count)
					+ this.num, width, height, textpaint);
			// ////////画方向//////////////////////
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_direct)
					+ this.direction, width, height, textpaint);
			// /////////画速度//////////////////////
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_speed)
					+ this.speed, width, height, textpaint);

			if (earth != null) {
				int left = (this.screenWidth - earth.getWidth()) / 2;
				int top = (screenHeight / 2) - 20;
				canvas.drawBitmap(earth, left, top, textpaint);
				centerY = top + this.earth.getHeight() / 2;
				centerX = left + this.earth.getHeight() / 2;
			}
			drawSatellite(canvas);
			drawSignal(canvas, 0);
		} else {// 横向
			String date_str = context.getString(R.string.gpsinfo_date) + date;
			float width = 50;
			float height = 150;
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(25);
			canvas.drawText(date_str, width, height, textpaint);
			if (NaviControl.mgpsStatus == 3) {
				textpaint.setColor(0xFF3BA500);
				canvas.drawText(context.getString(R.string.gpsinfo_goodstate),
						width, height - 30, textpaint);
			} else {
				textpaint.setColor(0xFFEE0022);
				canvas.drawText(context.getString(R.string.gpsinfo_badstate),
						width, height - 30, textpaint);
			}
			// ///////画时间/////////
			textpaint.setColor(0xFFfffff0);
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_time) + time,
					width, height, textpaint);
			// ////////画卫星颗数/////////////
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_count)
					+ this.num, width, height, textpaint);
			// ////////画方向//////////////////////
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_direct)
					+ this.direction, width, height, textpaint);
			// /////////画速度//////////////////////
			height = height + 30;
			canvas.drawText(context.getString(R.string.gpsinfo_speed)
					+ this.speed, width, height, textpaint);

			if (earth != null) {
				int top = (screenHeight - earth.getHeight()) / 2 - 20;
				int left = screenWidth / 2;
				canvas.drawBitmap(earth, left, top, textpaint);
				// Log.e(TAG, "EARTH____left:"+left+",top:"+top);
				centerY = top + this.earth.getHeight() / 2;
				centerX = left + this.earth.getHeight() / 2;
				// Log.e(TAG, "EARTH centerx:"+centerX);
				// Log.e(TAG, "EARTH centery:"+centerY);
			}
			drawSatellite(canvas);
			drawSignal(canvas, 1);
		}
	}

	/*
	 * 低分辨率绘制
	 */
	private void drawForLdpi(Canvas canvas) {
		if (screenHeight > screenWidth) {// 竖向
			String date_str = context.getString(R.string.gpsinfo_date);
			if (pstGPSInfo != null) {
				date_str += AndroidUtils.getDay(pstGPSInfo.time);
			} else {
				date_str += "2011年11月22日";
			}
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(15);
			float width = (screenWidth - textpaint
					.measureText("日期：2002年00月00日")) / 2;
			float height = 80;

			// ///////画日期/////////
			canvas.drawText(date_str, width, height, textpaint);
			// ///////画是否有效定位/////////
			if (NaviControl.mgpsStatus == 3) {
				textpaint.setColor(0xFF3BA500);
				canvas.drawText(context.getString(R.string.gpsinfo_goodstate),
						width, height - 20, textpaint);
			} else {
				textpaint.setColor(0xFFEE0022);
				canvas.drawText(context.getString(R.string.gpsinfo_badstate),
						width, height - 20, textpaint);
			}
			// ///////画时间/////////
			textpaint.setColor(0xFFfffff0);
			height = height + 20;
			String time_str = "";
			if (pstGPSInfo != null) {
				time_str += AndroidUtils.getTime(pstGPSInfo.time);
			} else {
				time_str += "00:00:00";
			}
			canvas.drawText(
					context.getString(R.string.gpsinfo_time) + time_str, width,
					height, textpaint);
			// ////////画卫星颗数/////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_count)
					+ this.num, width, height, textpaint);
			// ////////画方向//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_direct)
					+ this.direction, width, height, textpaint);
			// /////////画速度//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_speed)
					+ this.speed, width, height, textpaint);

			if (earth != null) {
				int left = (this.screenWidth - earth.getWidth()) / 2;
				int top = (screenHeight / 2) + 20;
				canvas.drawBitmap(earth, left, top, textpaint);
				centerY = top + this.earth.getHeight() / 2;
				centerX = left + this.earth.getHeight() / 2;
			}
			drawSatellite(canvas);
			drawSignal(canvas, 0);
		} else {// 横向
			String date_str = context.getString(R.string.gpsinfo_date);
			if (pstGPSInfo != null) {
				date_str += AndroidUtils.getDay(pstGPSInfo.time);
			} else {
				date_str += "2011年11月22日";
			}
			float width = 50;
			float height = 100;
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(15);
			canvas.drawText(date_str, width, height, textpaint);
			if (NaviControl.mgpsStatus == 3) {
				textpaint.setColor(0xFF3BA500);
				canvas.drawText(context.getString(R.string.gpsinfo_goodstate),
						width, height - 20, textpaint);
			} else {
				textpaint.setColor(0xFFEE0022);
				canvas.drawText(context.getString(R.string.gpsinfo_badstate),
						width, height - 20, textpaint);
			}
			// ///////画时间/////////
			textpaint.setColor(0xFFfffff0);
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_time) + time,
					width, height, textpaint);
			// ////////画卫星颗数/////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_count)
					+ this.num, width, height, textpaint);
			// ////////画方向//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_direct)
					+ this.direction, width, height, textpaint);
			// /////////画速度//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_speed)
					+ this.speed, width, height, textpaint);

			if (earth != null) {
				int top = (screenHeight - earth.getHeight()) / 2 + 20;
				int left = screenWidth / 2 + 40;
				canvas.drawBitmap(earth, left, top, textpaint);
				// Log.e(TAG, "EARTH____left:"+left+",top:"+top);
				centerY = top + this.earth.getHeight() / 2;
				centerX = left + this.earth.getHeight() / 2;
				// Log.e(TAG, "EARTH centerx:"+centerX);
				// Log.e(TAG, "EARTH centery:"+centerY);
			}
			drawSatellite(canvas);
			drawSignal(canvas, 1);
		}
	}

	/*
	 * 中分辨率绘制
	 */
	private void drawForMdpi(Canvas canvas) {
		if (screenHeight > screenWidth) {// 竖向
			String date_str = context.getString(R.string.gpsinfo_date);
			if (pstGPSInfo != null) {
				date_str += AndroidUtils.getDay(pstGPSInfo.time);
			} else {
				date_str += "2011年11月22日";
			}
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(15);
			float width = (screenWidth - textpaint
					.measureText("日期：2002年00月00日")) / 2;
			float height = 100;
			// ///////画日期/////////
			canvas.drawText(date_str, width, height, textpaint);
			// ///////画是否有效定位/////////
			if (NaviControl.mgpsStatus == 3) {
				textpaint.setColor(0xFF3BA500);
				// canvas.drawText("定位精度："+Accuracy, width, height-20,
				// textpaint);
				canvas.drawText(context.getString(R.string.gpsinfo_goodstate),
						width, height - 20, textpaint);
			} else {
				textpaint.setColor(0xFFEE0022);
				canvas.drawText(context.getString(R.string.gpsinfo_badstate),
						width, height - 20, textpaint);
			}
			// ///////画时间/////////
			textpaint.setColor(0xFFfffff0);
			height = height + 20;
			String time_str = "";
			if (pstGPSInfo != null) {
				time_str += AndroidUtils.getTime(pstGPSInfo.time);
			} else {
				time_str += "00:00:00";
			}
			canvas.drawText(
					context.getString(R.string.gpsinfo_time) + time_str, width,
					height, textpaint);
			// ////////画卫星颗数/////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_count)
					+ this.num, width, height, textpaint);
			// ////////画方向//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_direct)
					+ this.direction, width, height, textpaint);
			// /////////画速度//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_speed)
					+ this.speed, width, height, textpaint);
			if (earth != null) {
				int left = (this.screenWidth - earth.getWidth()) / 2;
				int top = (screenHeight / 2) - 20;
				canvas.drawBitmap(earth, left, top, textpaint);
				centerY = top + this.earth.getHeight() / 2;
				centerX = left + this.earth.getHeight() / 2;
				// Log.e(TAG, "EARTH____left:"+left+",top:"+top);
				// Log.e(TAG, "EARTH centerx:"+centerX);
				// Log.e(TAG, "EARTH centery:"+centerY);
			}
			drawSatellite(canvas);
			drawSignal(canvas, 0);
		} else {// 横向
			String date_str = context.getString(R.string.gpsinfo_date) + date;
			float width = 50;
			float height = 100;
			textpaint.setColor(0xFFfffff0);
			textpaint.setTextSize(15);
			canvas.drawText(date_str, width, height, textpaint);
			if (NaviControl.mgpsStatus == 3) {
				textpaint.setColor(0xFF3BA500);
				canvas.drawText(context.getString(R.string.gpsinfo_goodstate),
						width, height - 20, textpaint);
			} else {
				textpaint.setColor(0xFFEE0022);
				canvas.drawText(context.getString(R.string.gpsinfo_badstate),
						width, height - 20, textpaint);
			}
			// ///////画时间/////////
			textpaint.setColor(0xFFfffff0);
			height = height + 20;
			String time_str = "";
			if (pstGPSInfo != null) {
				time_str += AndroidUtils.getTime(pstGPSInfo.time);
			} else {
				time_str += "00:00:00";
			}
			canvas.drawText(
					context.getString(R.string.gpsinfo_time) + time_str, width,
					height, textpaint);
			// ////////画卫星颗数/////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_count)
					+ this.num, width, height, textpaint);
			// ////////画方向//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_direct)
					+ this.direction, width, height, textpaint);
			// /////////画速度//////////////////////
			height = height + 20;
			canvas.drawText(context.getString(R.string.gpsinfo_speed)
					+ this.speed, width, height, textpaint);
			if (earth != null) {
				int top = (screenHeight - earth.getHeight()) / 2 - 20;
				int left = screenWidth / 2;
				canvas.drawBitmap(earth, left, top, textpaint);
				// Log.e(TAG, "EARTH____left:"+left+",top:"+top);
				centerY = top + this.earth.getHeight() / 2;
				centerX = left + this.earth.getHeight() / 2;
				// Log.e(TAG, "EARTH centerx:"+centerX);
				// Log.e(TAG, "EARTH centery:"+centerY);
			}
			drawSatellite(canvas);
			drawSignal(canvas, 1);
		}
	}

	/*
	 * 解析部分GPS信息
	 */
	public void getGPSDesc() {
		if (pstGPSInfo != null) {
			time = AndroidUtils.getTime(pstGPSInfo.time);
			this.date = AndroidUtils.getDay(pstGPSInfo.time);
			
			if (NaviControl.mgpsStatus == 3) {
				// CGPSRouteInfo gpsInfo = new CGPSRouteInfo();
				// RouteAPI.getInstance().ns_ROUTE_GetRouteNaviInfo(gpsInfo);
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("#.0");
				this.direction = (int)pstGPSInfo.angle+"";
				if (pstGPSInfo.speed == 0) {
					this.speed = "0 km/h";
				} else {
					this.speed = df.format((double) pstGPSInfo.speed * 3.6)
							+ " km/h";
				}
			}
			this.num = pstGPSInfo.useableSatNum + "";
		}
	}

	/*
	 * 绘制卫星信号强度
	 */
	protected void drawSignal(Canvas canvas, int screen) {
		final int fill = 6;
		final int baseline = getHeight() - 20;
		final int maxHeight;
		if (screen == 0) {
			maxHeight = (screenHeight / 2) - 40 - this.earth.getHeight();
		} else {
			maxHeight = (this.screenWidth / 2) - 40 - this.earth.getHeight();
		}
		final float scale = maxHeight / 100.0F;

		float slotWidth = (float) Math.floor(getWidth() / 14);
		float barWidth = slotWidth - fill;
		float margin = (getWidth() - (slotWidth * 14)) / 2;

		int drawn = 0;
		if (pstGPSInfo != null && pstGPSInfo.satInfo != null
				&& pstGPSInfo.satInfo.length > 0)
			for (int i = 0; i < pstGPSInfo.useableSatNum; i++) {
				SatInfo csatinfo = pstGPSInfo.satInfo[i];
				if (csatinfo == null || csatinfo.satNo <= 0) {
					continue;
				}
				float left = margin + (drawn * slotWidth) + fill / 2;
				if (0 == pstGPSInfo.useableSatNum) {
					canvas.drawRect(left, baseline - (csatinfo.SNR * scale),
							left + barWidth, baseline, mBarPaintNoFix);
				} else if (csatinfo.SNR > 0) {
					canvas.drawRect(left, baseline - (csatinfo.SNR * scale),
							left + barWidth, baseline, mBarPaintUsed);
				} else {
					canvas.drawRect(left, baseline - (csatinfo.SNR * scale),
							left + barWidth, baseline, mBarPaintUnused);
				}
				canvas.drawRect(left, baseline - (csatinfo.SNR * scale), left
						+ barWidth, baseline, mBarOutlinePaint);
				String tmp = csatinfo.SNR + "";
				canvas.drawText(tmp, left + barWidth / 2, baseline
						- (csatinfo.SNR * scale) - 10, mTextPaint);
				canvas.drawText(csatinfo.satNo + "", left + barWidth / 2,
						baseline + 15, mTextPaint);
				drawn += 1;
			}
	}

	/*
	 * 绘制卫星
	 */
	protected void drawSatellite(Canvas canvas) {
		int radius = this.earth.getHeight() / 2;
		final Paint gridPaint = textpaint;
		final Paint textPaint = mTextPaint;
		double scale = radius / 90.0;
		computeXY();
		if (pstGPSInfo != null && pstGPSInfo.satInfo != null
				&& pstGPSInfo.satInfo.length > 0)
			for (int i = 0; i < pstGPSInfo.useableSatNum; i++) {
				SatInfo csatinfo = pstGPSInfo.satInfo[i];

				if (csatinfo == null || csatinfo.angle <= 0 || csatinfo.satNo <= 0|| mElevation_90[i] >= 90) {
					continue;
				}
				double a = mElevation_90[i] * scale;
				int x = (int) Math.round(centerX + (mX[i] * a)
						);
				int y = (int) Math.round(centerY + (mY[i] * a)
						);
				x = x - satelliteImg.getWidth() / 2;
				y = y - satelliteImg.getHeight() / 2;
				if (0 == (pstGPSInfo.useableSatNum) || csatinfo.SNR <= 0) { // red
					canvas.drawBitmap(satelliteImg, x, y, gridPaint);
					// } else if (0 != (mUsedInFixMask[0] &
					// (1<<(32-mPnrs[i])))){
					// //green
				} else if (0 != (pstGPSInfo.useableSatNum & (1 << (csatinfo.satNo - 1)))) { // green
					canvas.drawBitmap(satelliteImg, x, y, gridPaint);
				} else { // yellow
					canvas.drawBitmap(satelliteImg, x, y, gridPaint);
				}
				canvas.drawText(csatinfo.satNo + "", x, y, textPaint);
				// Log.e(TAG, "Pnrs:" + mPnrs[i] + "X:" + x + ",Y:" + y);
			}

	}

	private void computeXY() {
		if (pstGPSInfo != null && pstGPSInfo.satInfo != null
				&& pstGPSInfo.satInfo.length > 0)
			for (int i = 0; i < pstGPSInfo.useableSatNum; i++) {
				SatInfo csatinfo = pstGPSInfo.satInfo[i];
				if (csatinfo == null || csatinfo.angle <= 0 || csatinfo.satNo <= 0|| mElevation_90[i] >= 90
						) {
					continue;
				}
				double theta = -(csatinfo.angle - 90);
				double rad = theta * Math.PI / 180.0;
				mX[i] = (float) Math.cos(rad);
				mY[i] = -(float) Math.sin(rad);

				mElevation_90[i] = 90 - csatinfo.upAngle;
			}
	}


	@Override
	public void DoRouteInfo(GPSRouteInfo routeInfo, boolean gpsNavi) {
		// TODO Auto-generated method stub
		
	}




}
