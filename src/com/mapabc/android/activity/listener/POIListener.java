package com.mapabc.android.activity.listener;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import com.mapabc.android.activity.base.Constants;
/**
 * @description: POI显示页面按钮监听器
 * @author menglin.cao 2012-08-28
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public abstract class POIListener implements OnClickListener {

	protected void forward(Context context,Bundle extras){
		Intent intent = new Intent(Constants.ACTIVITY_NAVISTUDIOACTIVITY);
		intent.putExtras(extras);
		context.startActivity(intent);
		
	}

}
