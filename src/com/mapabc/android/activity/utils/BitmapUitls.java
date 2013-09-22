package com.mapabc.android.activity.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


public class BitmapUitls {
	
	private static final String TAG = "BitmapUitls";
	public static final int RESULT_TAKEIMAGE = 10001;
	public static final int REQUEST_CROP_IMAGE = 10002;
	public static String EXTRA_IMAGE_DATA = "data";
	
	private static final String MIME_TYPE_IMAGE = "image/*";
	private static final String ACTION_IMAGE_CORP = "com.android.camera.action.CROP";

	/**
	 * bitmap转byte
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream(); // 将Bitmap压缩成PNG编码，质量为100%存储\
		// bitmap.compress(Bitmap.CompressFormat.PNG, 100,
		// os);//除了PNG还有很多常见格式，如jpeg等。
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);// 除了PNG还有很多常见格式，如jpeg等。
		return os.toByteArray();
	}
	

	
	/**
	 * 初始化系统CropIntent
	 * @param mTempFile
	 * @param imagePath
	 * @param rawData
	 * @param context
	 * @return
	 */
	public static Intent openActivityForCorpImage(File mTempFile, Uri imagePath, Bitmap rawData, Context context) {
		
		int mOutputWidth = 1280;
		int mOutputHeight = 1280;
		boolean mFixSize = true;
		String EXTRA_IMAGE_DATA = "data";
		boolean mDoFaceDetection = true;
		
		Intent intent = new Intent(ACTION_IMAGE_CORP);
		intent.setDataAndType(imagePath, MIME_TYPE_IMAGE);
		if (rawData != null) {
			intent.putExtra(EXTRA_IMAGE_DATA, rawData);
		}

		if (mOutputWidth > 0 && mOutputHeight > 0) {
			if (mFixSize) {
				intent.putExtra("aspectX", mOutputWidth);
				intent.putExtra("aspectY", mOutputHeight);
			}

			if (mOutputWidth * mOutputHeight <= 90000) {
				intent.putExtra("outputX", mOutputWidth);
				intent.putExtra("outputY", mOutputHeight);
			}
		}

		if (imagePath != null && ContentResolver.SCHEME_FILE.equals(imagePath.getScheme())) {
			if (mTempFile == null) {
				mTempFile = initTempFile(context);
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
		}

		// intent.putExtra("circleCrop", "true");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", !mDoFaceDetection);

		intent.putExtra("setWallpaper", false);
		return intent;
	}
	
	private static final String TEMP_FILENAME = "temp_pic.jpg";

	private static File initTempFile(Context context) {
		File result = context.getFileStreamPath(TEMP_FILENAME);
		try {
			result.delete();
			OutputStream stream = context.openFileOutput(TEMP_FILENAME,
					Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
			stream.close();
		} catch (Exception e) {
			// do unthing
		}
		return result;
	}
	
	/**
	 * 缩放图片,当参数小于原图比例的话,直接返回原图
	 * @param bitmap 要缩放的图像
	 * @param px 缩放后的边长（px）
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int px){
		if(px == 0 || px >= bitmap.getWidth()){
			return bitmap;
		}else{
			int primaryWidth = bitmap.getWidth();
			int primaryHeight = bitmap.getHeight();
			Log.e(TAG, "get primaryWidth ---> " + primaryWidth + ", get primaryHeight ---> " + primaryHeight);
			double scale = (double)(px*100)/(primaryWidth*100);
			Log.e(TAG, "scale value ----> " + scale);
			double scaleWidth = primaryWidth*scale;
			double scaleHeight = primaryHeight*scale;
			Log.e(TAG, "get scaleWidth ---> " + scaleWidth + ", get scaleHeight ---> " + scaleHeight);
			Matrix matrix = new Matrix();
	        matrix.postScale((float)scaleWidth, (float)scaleHeight);    //设置高宽比例（三维矩阵）
			return Bitmap.createBitmap(bitmap, 0, 0, primaryWidth, primaryHeight, matrix, true);
		}
	}
	
	/**
	 * 加载网络图片
	 * 
	 * @param url
	 *            网络图片地址
	 * @return Bitmap
	 */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		InputStream is=null;
		HttpURLConnection conn=null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
			if(is!=null){
				is.close();
			}
			if(conn!=null){
				conn.disconnect();
			}
			}catch(Exception ex){
				
			}
		}
		return bitmap;
	}
}
