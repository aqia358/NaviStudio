package com.mapabc.android.activity.setting;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.BaseActivity;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.naviapi.UtilAPI;
import com.mapabc.naviapi.utils.VersionInfo;

/**
 * 更多模块中的关于功能
 * @author menglin.cao 2012-08-22
 *
 */
public class AboutAutonaviActivity extends BaseActivity {
	private static final String TAG = "AboutAutonaviActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutautonavi_main);
		initTop();
		Log.e(TAG, "AboutAutonaviActivity");
		VersionInfo versionInfo = new VersionInfo();
		UtilAPI.getInstance().getVersionInfo(versionInfo);
		
		String m_strShenTu = this.getResources().getString(
				R.string.aboutautonavi_shenTu)
				+ versionInfo.verifyNumber;
		TextView tv_shentu = (TextView) findViewById(R.id.tv_about_autonavi_shenTu);
		tv_shentu.setText(m_strShenTu);
		TextView tv_drive = (TextView) findViewById(R.id.tv_about_autonavi_drivenum);
		TextView tv_soft = (TextView) findViewById(R.id.tv_about_autonavi_softnum);
		tv_soft.setText(R.string.aboutautonavi_softnum);
		String m_strdrive = this.getResources().getString(
				R.string.aboutautonavi_drivenum);
		m_strdrive += versionInfo.engineVerson;
		tv_drive.setText(m_strdrive);
		TextView tv_data = (TextView) findViewById(R.id.tv_about_autonavi_datanum);
		String m_strdata = this.getResources().getString(
				R.string.aboutautonavi_datanum);
		m_strdata += versionInfo.mapVersion;
		tv_data.setText(m_strdata);
	}
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(this.getResources().getStringArray(R.array.otherfunctionitems)[4]);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this, false, false));
	}
}
