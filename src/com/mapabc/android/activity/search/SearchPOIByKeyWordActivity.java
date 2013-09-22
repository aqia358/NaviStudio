package com.mapabc.android.activity.search;

import java.util.ArrayList;
import java.util.Observable;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.SearchOption;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.type.IntValue;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 关键字查询UI
 * @author: changbao.wang 2011-10-17
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchPOIByKeyWordActivity extends SearchActivity{
	private static final String TAG = "SearchPOIByKeyWordActivity";

	private String res = "key word";
	
//	RecognizerDialog mRecognizerDlg;
	char punctuations[] = {'，',',','.','。','？','?','!','！'};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayout();
		initTop();
//		mRecognizerDlg = new RecognizerDialog(this, "appid=" + getString(R.string.searchpoibykeyword_app_id));
//		mRecognizerDlg.setListener(this);
//		mRecognizerDlg.setEngine("sms", null, null);
	}

	@Override
	protected void returnSearchActivity() {
		startActivity(new Intent(Constants.ACTIVITY_SEARCHPOIBYKEYWORD));
	}

	@Override
	protected void searchPOI() {
		lstPOI.clear();
		String input = keywordAutoCompleteTextView.getText().toString();
		String[] info = {res,input};
//		databaseHelper.insert(info);
		System.out.println("input" + input);
		String[] typeCodes = new String[0];
		bRest = SearchAPI.getInstance()
				.resetNameSearchEngine(typeCodes, adCode,2);
		if (bRest) {
			IntValue count = new IntValue();
			SearchAPI.getInstance().inputName(input, count);
			int poiCount = SearchAPI.getInstance().startNameSearch();
			// 获取结果
			for (int i = 0; i < poiCount; i++) {
				SearchResultInfo poiInfo = new SearchResultInfo();
				SearchAPI.getInstance().getNameRecordByIndex(i, poiInfo);
				lstPOI.add(poiInfo);
			}
		}
	}

	@Override
	protected void forwardIntent(Bundle extras) {
		Intent intent = new Intent(Constants.ACTIVITY_SEARCHRESULTOFKEYWORD);
		intent.putExtras(extras);
		startActivity(intent);
		
	}

	@Override
	protected boolean validateInput() {
		String kw = Utils.getEditTextValue(keywordAutoCompleteTextView);
		if(kw == null || kw.trim().length() == 0){
			showInfo(R.string.search_enter_keyword);
			return false;
		}
		return true;
	}
	@Override
	protected void setLayout() {
		setContentView(R.layout.common_search);
		Log.e("SearchPOIByKeyWordActivity", "=====TaskId====="+getTaskId());
		target = Constants.ACTIVITY_SEARCHPOIBYKEYWORD;
		keywordAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actv_search_keyword);
		keywordAutoCompleteTextView.setHint(R.string.search_enter_keyword);
		keywordAutoCompleteTextView.setWidth((int) (Utils.getCurScreenWidth(this)*0.6));
		keywordAutoCompleteTextView.addTextChangedListener(mTextWatcher);
//		registerDropDown(res);
		updateCity();
		registerSearchBar(Constants.ACTIVITY_SEARCHRESULTOFKEYWORD);
		//////////////////////////屏蔽语音按钮/////////////////////////////////
//		voiceImageButton.setVisibility(View.VISIBLE);
//		voiceImageButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mRecognizerDlg.show();
//			}
//		});
	}

	@Override
	public void update(Observable observable, Object data) {
		
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		updateCity();
	}

	
	private String rejectPunctuations(String text)
	{
		if(text == null)
			return null;
		int len = text.length();
		StringBuilder sBuilder = new StringBuilder();
		char c;
		boolean bFlag;
		for(int i = 0;i < len;i++)
		{
			c = text.charAt(i);
			bFlag = false;
			for(int j = 0;j < punctuations.length;j++)
			{
				if(c == punctuations[j])
				{
					bFlag = true;
					break;
				}
			}
			if(bFlag)continue;
			sBuilder.append(c);
		}
		return sBuilder.toString();
	}
	/**
	 * 初使化顶部控件
	 */
	private void initTop() {
		TextView topicTextView = (TextView) findViewById(R.id.tv_topic);
		topicTextView.setText(this.getResources().getStringArray(
				R.array.searchlocitems)[1]);
		ImageButton backImageButton = (ImageButton) findViewById(R.id.ib_menu_back);
		registerBackAction(backImageButton);
	}
	
	private final TextWatcher mTextWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		private String alphaBet_a = "abcdefghijklmnopqrstuvwxyz";
		private String alphaBet_A = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		public void afterTextChanged(Editable s) {
			if (s != null)
				if (s.length() > 0) {
					int pos = s.length() - 1;
					String c = s.charAt(pos) + "";
					if (alphaBet_a.indexOf(c) >=0  || alphaBet_A.indexOf(c) >= 0) {
						s.delete(pos, pos + 1);
					}
				}
		}
	};
}
