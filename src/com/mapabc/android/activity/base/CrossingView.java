package com.mapabc.android.activity.base;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;

import com.mapabc.android.activity.NaviStudioActivity;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.listener.CrossingZoomListener;
import com.mapabc.android.activity.utils.BitmapUitls;
import com.mapabc.android.activity.utils.DateTimeUtil;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.route.GPSRouteInfo;
import com.mapabc.naviapi.type.NSSize;
import com.mapabc.naviapi.utils.AndroidUtils;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 路口放大图容器
 * @author: changbao.wang 2011-11-14
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class CrossingView extends View {

	private static final String TAG = "CrossingView";
	private Context m_context;
	protected CrossingZoomListener m_CrossingZoomListener;
	public boolean drawCross = false;
	public android.graphics.Bitmap bitmapCross = null;
	public static java.nio.ByteBuffer pixelsBufferCross = null;
	boolean isdrewcross = false;
	private Paint paint = null;
	Resources resource = null;
	int nProgressLeft, nProgressTop;// 填充颜色
	int nLeft, nTop;// 进度条的位置
	Bitmap  fillImage, cursorImage, fillcursorImage,
			bg_land_Image;
	GPSRouteInfo m_gpsInfo;
	double factor = 1.0,heightfactor=1.0;
	int nWidth;
	private CarLinesListener carLinesListener;
	
	public CarLinesListener getCarLinesListener() {
		return carLinesListener;
	}

	public void setCarLinesListener(CarLinesListener carLinesListener) {
		this.carLinesListener = carLinesListener;
	}
	public CrossingView(Context context) {
		super(context);
		this.m_context = context;
		paint = new Paint();
		paint.setColor(Color.GRAY);
		resource = m_context.getResources();
		bg_land_Image = BitmapFactory.decodeResource(resource,
				R.drawable.crossingview_zoom_border_land);
			fillImage = BitmapFactory.decodeResource(resource,
					R.drawable.crossingview_zoom_process_land);
		cursorImage = BitmapFactory.decodeResource(resource,
				R.drawable.crossingview_zoom_cursor);
		nWidth = fillImage.getWidth();
	}

	public void refresh(GPSRouteInfo gpsRouteInfo) {
		try{
		if(gpsRouteInfo.segRemainDis>300){
			m_gpsInfo=null;
			isdrewcross = false;
			sendMessageToListener(CrossingZoomListener.CROSSING_ZOOM_DISAVISIBLE_STATUS);
		} else {
			m_gpsInfo = gpsRouteInfo;
			isdrewcross = true;
			NSSize size = new NSSize();
			getCrossImage(size);
			if(isdrewcross){
			  //解决黑屏问题
				this.invalidate();
			  sendMessageToListener(CrossingZoomListener.CROSSING_ZOOM_VISIBLE_STATUS);
			}else{
			  //解决黑屏问题
				  sendMessageToListener(CrossingZoomListener.CROSSING_ZOOM_DISAVISIBLE_STATUS);
			}
		}
		}catch(Exception ex){
			Log.e(TAG, ex.getMessage());
		}

	}
    private void sendMessageToListener(int type){
    	if (m_CrossingZoomListener != null) {
    		m_CrossingZoomListener.onStatusedChange(type);
    	}
    	if(type == CrossingZoomListener.CROSSING_ZOOM_VISIBLE_STATUS){
    		super.setVisibility(View.VISIBLE);
    	}else{
    		super.setVisibility(View.GONE);
    	}
    }
	protected void onDraw(Canvas canvas) {
		if (isdrewcross) {
			int screen = AndroidUtils.checkScreenResolution(m_context);
			if(!NaviStudioActivity.displayWarningOrNot){
			newDrawCrossImage(screen, canvas);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
			fillImage = BitmapFactory.decodeResource(resource,
					R.drawable.crossingview_zoom_process_land);
		nWidth = fillImage.getWidth();
	}
    private void newDrawCrossImage(int screen, Canvas canvas){
    	switch (screen) {
		case AndroidUtils.SCREEN_RESOLUTION_SVGA:// 超高
			nProgressLeft = 14;
			nProgressTop = 10;
			if(bg_land_Image.getHeight()==35){
				nProgressLeft = 13;
				nProgressTop = 6;
				heightfactor=1.3;
			}
			break;
		case AndroidUtils.SCREEN_RESOLUTION_WFVGA:// 高分
			nProgressLeft = 13;
			nProgressTop = 9;
			break;
		case AndroidUtils.SCREEN_RESOLUTION_WVGA:// 高分
			nProgressLeft = 13;
			nProgressTop = 10;
			break;
		case AndroidUtils.SCREEN_RESOLUTION_HVGA:// 中分
			nProgressLeft = 13;
			nProgressTop = 6;
			break;
		case AndroidUtils.SCREEN_RESOLUTION_QVGA:// 低分
			nProgressLeft = 13;
			nProgressTop = 10;
			break;
		default:
			nProgressLeft = 13;
		    nProgressTop = 10;
		break;
		}
    	if (Utils.isLand(m_context)) {//横屏
    		nLeft =this.getWidth()>>1;
    		factor = (double)nLeft / (double)bg_land_Image.getWidth();
    		nTop = this.getHeight() -(int)(heightfactor* bg_land_Image.getHeight());
    		fillcursorImage = Bitmap.createScaledBitmap(bitmapCross,nLeft,nTop, true);
    		canvas.drawBitmap(fillcursorImage, nLeft,0,null);    		
    	}else{
    		nLeft = 0;
    		factor = (double)this.getWidth() / (double)bg_land_Image.getWidth();
    		nTop = (this.getHeight()>>1) -(int)(heightfactor* bg_land_Image.getHeight());
    		fillcursorImage = Bitmap.createScaledBitmap(bitmapCross,
    				this.getWidth(), nTop, true);
    		carLinesListener.draw(canvas,0,fillcursorImage.getHeight()+bg_land_Image.getHeight());
    		canvas.drawBitmap(fillcursorImage, 0,0,null);
    	}
    	Rect src = new Rect();
		src.left = 0;
		src.right = src.left + bg_land_Image.getWidth();
		src.top = 0;
		src.bottom = src.top + bg_land_Image.getHeight();
		Rect dest = new Rect();
		dest.left = nLeft;
		dest.right = dest.left + (int)(factor * bg_land_Image.getWidth());
		dest.top = nTop;
		dest.bottom = dest.top +(int)(heightfactor*bg_land_Image.getHeight());
		canvas.drawBitmap(bg_land_Image, src, dest, paint);
		
		int nMaxDis = 200;
		if (m_gpsInfo == null) {
			return;
		}
		if (m_gpsInfo.segRemainDis >= nMaxDis)
			return;
		// 计算进度条的宽度
		int nProgWidth = (int) ((double) (nMaxDis - m_gpsInfo.segRemainDis)
				/ (double) nMaxDis*nWidth);
		// //绘制进度条
		
		src = new Rect();
		src.left = 0;
		src.right = src.left + nProgWidth;
		src.top = 0;
		src.bottom = src.top + fillImage.getHeight();

		Rect dst = new Rect();
		dst.left = nLeft + (int)(factor*nProgressLeft);
		dst.right = dst.left + (int)(factor*nProgWidth);
		dst.top = nTop + (int)(heightfactor*nProgressTop);
		dst.bottom = dst.top + (int)(heightfactor*fillImage.getHeight());

		canvas.drawBitmap(fillImage, src, dst, paint);

		// 绘制游标
		int x = nLeft +(int)(factor* (nProgressLeft + nProgWidth));
		int y = nTop + (int)(heightfactor*nProgressTop + (fillImage.getHeight() / 2));
		x -= (cursorImage.getWidth() / 2);
		y -= (cursorImage.getHeight() / 2);
		canvas.drawBitmap(cursorImage, x, y, paint);
    }


	private void getCrossImage(NSSize size) {
		if(m_gpsInfo==null){
			return ;
		}
		if(bitmapCross==null){			
			bitmapCross = Bitmap.createBitmap(400, 400,Config.RGB_565);
		}
		if(pixelsBufferCross==null){			
			pixelsBufferCross = java.nio.ByteBuffer.allocate(400* 400 * 2);
		}
		isdrewcross = RouteAPI.getInstance().getCrossImage(m_gpsInfo.crossBackID,m_gpsInfo.crossArrowID,pixelsBufferCross.array(),size);
		bitmapCross.copyPixelsFromBuffer(pixelsBufferCross);
	}

	public synchronized void setCrossingZoomListener(
			CrossingZoomListener listener) {
		this.m_CrossingZoomListener = listener;
	}
//    private void saveCrossingView(Bitmap m){
//    	String path = SysParameterManager.getBasePath()+"/MapABC/log";
//    	byte[] b = BitmapUitls.bitmapToBytes(m);
//		try{
//			String filepath = path+"/"+DateTimeUtil.getSystemDateTime1()+".bmp";
//			File ff = new File(filepath);
//			if(!ff.exists()){
//				ff.createNewFile();
//			}
//			FileOutputStream bitmapfile = new FileOutputStream(ff);
//			bitmapfile.write(b);
//			bitmapfile.flush();
//			bitmapfile.close();
//		}catch(Exception ex){
//			Log.e("saveLog", "ERROR",ex);
//		}
//    }
}
