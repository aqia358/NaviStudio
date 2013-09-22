package com.mapabc.android.activity.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.naviapi.FavoriteAPI;
import com.mapabc.naviapi.favorite.FavoriteInfo;

/**
 * 编辑收藏夹
 * @author menglin.cao 2012-09-04
 *
 */
public class EditPOIInfoActivity  extends Activity implements OnClickListener {
	private static final String TAG = "EditPOIInfoActivity";
	FavoriteInfo poi;
	private EditText telephoneEditText,addressEditText;
	private TextView nameTextView;
	private Button saveButton;
	private String numbers="0123456789|";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editpoiinfo_main);
		initTop();
		Bundle b = this.getIntent().getExtras();
		poi = (FavoriteInfo)b.getSerializable("poi");
		telephoneEditText = (EditText)this.findViewById(R.id.et_editpoiinfo_poi_tel_info);
		addressEditText = (EditText)this.findViewById(R.id.et_editpoiinfo_poi_address_info);
		nameTextView = (TextView)this.findViewById(R.id.tv_editpoiinfo_poi_name);
		telephoneEditText.addTextChangedListener(mTextWatcher);
		try {
			nameTextView.setText(poi.name);
			addressEditText.setText(poi.address);
			telephoneEditText.setText(poi.telephone);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		saveButton = (Button)this.findViewById(R.id.bt_editpoiinfo_poi_btn_save);
		saveButton.setOnClickListener(this);
	}
	
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(this.getResources().getString(R.string.editpoiinfo_topic));
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this, false, false));
	}

	@Override
	public void onClick(View v) {
		if (v.equals(saveButton)) {
			FavoriteInfo poi = EditPOIInfoActivity.this.poi;
			try {
				poi.address = addressEditText.getText().toString();
				poi.telephone = telephoneEditText.getText().toString();
			} catch (Exception e) {
			}
			FavoriteAPI.getInstance().updateFavorite(poi);
			EditPOIInfoActivity.this.finish();
		}
	}
	
	private final TextWatcher mTextWatcher = new TextWatcher() {
		int start;
		int count;
		boolean isRight=true;
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
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
					if (numbers.indexOf(c) < 0 && numbers.indexOf(c) < 0) {
						isRight = false;
						break;
					}
				}
			}
		}

		public void afterTextChanged(Editable s) {
			if(!isRight){
				s.delete(start, start+count);
			}
			
			
		}
	};
}

