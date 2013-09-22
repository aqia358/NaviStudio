package com.mapabc.android.activity.setting;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.base.ExitDialog;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.setting.adapter.OtherFunctionListAdapter;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 更多功能
 * @author menglin.cao 2012-08-21
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class OtherFunctionActivity extends BaseActivity implements OnItemClickListener{

	private static final String TAG = "OtherFunctionActivity";
	private ListView functionListView;
	OtherFunctionListAdapter itemadapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otherfunction_main);
		initTop();
		functionListView = (ListView) findViewById(R.id.lv_otherfunctionitem);
		itemadapter = new OtherFunctionListAdapter(this);
		functionListView.setAdapter(itemadapter);
		functionListView.setOnItemClickListener(this);
	}

	/**
	 * 初使化顶部控件
	 */
	private void initTop() {
		TextView topicTextView = (TextView) findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.otherfunction_title);
		ImageButton backImageButton = (ImageButton) findViewById(R.id.ib_menu_back);
		backImageButton
				.setOnClickListener(new BackListener(this, false, false));
	}

	/**
	 * 相应listview内的点击事件
	 * 
	 *@param position
	 *            索引
	 *@return
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.e(TAG, "position:" + position);
		Intent target=null;
		switch (position) {
		case 0:// 地图设置
			target = new Intent(Constants.ACTIVITY_SETTINGFORMAP);
			startActivity(target);
			break;
		case 1:// 导航设置
			target = new Intent(Constants.ACTIVITY_SETTINGFORROUTE);
			startActivity(target);
			break;
		case 2:// 交通雷达
			target = new Intent(Constants.ACTIVITY_SETTINGFORTRAFFICRADIO);
			startActivity(target);
			break;
		case 3:// 帮助
			try{
			String file = this.getString(R.string.otherfunction_helpfile);
			String filePath = SysParameterManager.getBasePath() + file;
			File f = new File(filePath);
			if (!f.exists()) {
				return;
			}
			Log.e(TAG, filePath);
			Intent it = this.getPdfFileIntent(filePath);
			startActivity(it);
			}catch(Exception ex){
				Utils.showTipInfo(this, R.string.otherfunctionactivity_openpdffailed);
			}
			break;
		case 4:// 关于
			target = new Intent(Constants.ACTIVITY_ABOUTAUTONAVI);
			startActivity(target);
			break;
		case 5:// 退出导航
			ExitDialog exitDailog = new ExitDialog(this);
			exitDailog.show();
			break;
		default:
			break;
		}
		
//		if (position == 3) {
//			ExitDialog exitDailog = new ExitDialog(this);
//			exitDailog.show();
//		} else if (position == 1) {
//			String file = this.getString(R.string.otherfunction_helpfile);
//			String filePath = SysParameterManager.getBasePath() + file;
//			File f = new File(filePath);
//			if (!f.exists()) {
//				return;
//			}
//			Log.e(TAG, filePath);
//			Intent it = this.getPdfFileIntent(filePath);
//			startActivity(it);
//		} else {
//			String intent = MenuActivityFactory
//					.getOtherFunctionActivityIntent(position);
//			if (intent != null) {
//				Log.e(TAG, "Intent is not null");
//				Intent target = new Intent(intent);
//				startActivity(target);
//			}
//		}
	}

	/**
	 * android获取一个用于打开PDF文件的intent
	 * 
	 * @param param
	 * @return
	 */
	public static Intent getPdfFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}
}
