package com.mapabc.android.activity.search;

import java.util.Observable;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import com.mapabc.naviapi.type.Const;
import com.mapabc.naviapi.type.IntValue;
import com.mapabc.naviapi.utils.SysParameterManager;

/**
 * @description: 首字母查询UI
 * @author menglin.cao 2012-08-24
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchPOIByFirstAlphaBetActivity extends SearchActivity {

	private static final String TAG = "SearchPOIByFirstAlphaBetActivity";
	private String res = "first alphabeta";
	private static int searchType = 0;// 0名称首字母，2地址首字母
	private String alphaBet_a = "abcdefghijklmnopqrstuvwxyz";
	private String alphaBet_A = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		searchType = extras.getInt(Constants.SEARCHTYPE_KEYWORD);
		this.setLayout();
		initTop();
	}

	/**
	 * 初使化顶部控件
	 */
	private void initTop() {
		TextView topicTextView = (TextView) findViewById(R.id.tv_topic);
		if(searchType==0){
			topicTextView.setText(this.getResources().getStringArray(
					R.array.searchlocitems)[0]);
		}else if(searchType==2){
			topicTextView.setText(this.getResources().getStringArray(
					R.array.searchlocitems)[2]);
		}
		ImageButton backImageButton = (ImageButton) findViewById(R.id.ib_menu_back);
		registerBackAction(backImageButton);
	}

	@Override
	protected void returnSearchActivity() {
		startActivity(new Intent(Constants.ACTIVITY_SEARCH_FIRSTALPHABET));
	}

	@Override
	protected void searchPOI() {
		lstPOI.clear();
		Log.e(TAG, "first alphabet search");
		String input = keywordAutoCompleteTextView.getText().toString();
		String[] info = { res, input };
//		databaseHelper.insert(info);
		String output = "";
		Log.e(TAG, "First Letter=" + input);
		int len = input.length();
		// 名称首拼检索
		String[] typeCodes = new String[0];
		// typeCodes[0] = "0601";
		int searchTypeConst = -1;
		if (searchType == 0) {
			searchTypeConst = Const.SEARCH_TYPE_POIFIRSTLETTER;
		} else if (searchType == 2) {
			searchTypeConst = Const.SEARCH_TYPE_ADDFIRSTLETTER;
		}
		bRest = SearchAPI.getInstance().resetFirstLetterEngine(typeCodes,
				adCode,2, searchTypeConst);
		if (bRest) {
			// 输入'H'单个字母
			IntValue count = new IntValue();
			// 小写字母转化为大写字母
			for (int i = 0; i < len; i++) {
				char a = input.charAt(i);
				int b = (int) a;
				if ((b < 122 || b == 122) && (b > 97 || b == 97)) {
					b -= 32;
				}
				SearchAPI.getInstance().inputFirstLetter((char) b, count);
				output += (char) b;
				Log.e(TAG, "Char:" + (char) b);
			}
			// 开始查询, 注意，即使之前查询到的POI数量大于100，但引擎只能返回前100个poi.
			int poiCount = SearchAPI.getInstance().startFirstLetterSearch();
			// 获取结果
			for (int i = 0; i < poiCount; i++) {
				SearchResultInfo poiInfo = new SearchResultInfo();
				boolean getRet = SearchAPI.getInstance()
						.getFirstLetterRecordByIndex(i, poiInfo);
				lstPOI.add(poiInfo);
			}
		}

	}

	@Override
	protected void forwardIntent(Bundle extras) {
		Intent intent = new Intent(Constants.ACTIVITY_RES_FIRSTALPHABET);
		intent.putExtras(extras);
		startActivity(intent);
	}

	@Override
	protected boolean validateInput() {
		String kw = Utils.getEditTextValue(keywordAutoCompleteTextView);
		if (kw == null || kw.trim().length() == 0) {
			showInfo(R.string.search_enter_firstalpha);
			return false;
		}
		return true;
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.common_search);
		target = Constants.ACTIVITY_SEARCH_FIRSTALPHABET;
		keywordAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actv_search_keyword);
		keywordAutoCompleteTextView.setHint(R.string.search_enter_keyword);
		keywordAutoCompleteTextView.setInputType(InputType.TYPE_CLASS_TEXT);
		 keywordAutoCompleteTextView.setWidth((int)
		 (Utils.getCurScreenWidth(this)*0.6));
		keywordAutoCompleteTextView.addTextChangedListener(mTextWatcher);
		
//		registerDropDown(res);
		updateCity();
		registerSearchBar(Constants.ACTIVITY_RES_FIRSTALPHABET);
	}

	private final TextWatcher mTextWatcher = new TextWatcher() {
		int start;
		int count;
		boolean isRight=true;
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			Log.e(TAG, "TEXT:"+s.toString());
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String s_text = s.toString();
			isRight=true;
			if(s_text!=null&&s_text.length()>0){
				this.start = start;
				this.count = count;
				for(int i=start;i<start+count;i++){
					String c = s_text.charAt(start) + "";
					if (alphaBet_a.indexOf(c) < 0 && alphaBet_A.indexOf(c) < 0) {
						isRight = false;
						break;
					}
				}
			}
			Log.e(TAG, "TEXT:"+s.toString());
		}

		public void afterTextChanged(Editable s) {
			if(!isRight){
				s.delete(start, start+count);
			}
			
			
		}
	};

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e(TAG, "=======onRestart========");
		updateCity();
	}
	
	@Override
	protected void onDestroy() {
//		SearchAPI.getInstance().exit();
		super.onDestroy();
	}
	

}
