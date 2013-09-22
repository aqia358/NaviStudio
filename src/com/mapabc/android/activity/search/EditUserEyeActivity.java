package com.mapabc.android.activity.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapabc.android.activity.R;
import com.mapabc.android.activity.base.Constants;
import com.mapabc.android.activity.listener.BackListener;
import com.mapabc.android.activity.utils.Utils;
import com.mapabc.naviapi.RouteAPI;
import com.mapabc.naviapi.UserEyeAPI;
import com.mapabc.naviapi.type.UserEventPot;

/**
 * 编辑电子眼界面
 * @author menglin.cao 2012-09-17
 *
 */
public class EditUserEyeActivity extends Activity implements OnClickListener{

	private UserEventPot poi;
	private EditText userEyeNameEditText;
	private Button saveButton;
	private Spinner typeSpinner,speedSpinner;
	private static int speedIndex=0;
	private static int typeIndex = 0;
	private static String [] typeIdArr;
	private String [] typeArr;
	private EditText directionEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.editusereye_main);
		initTop();
		super.onCreate(savedInstanceState);
		Bundle b = this.getIntent().getExtras();
		poi = (UserEventPot)b.getSerializable("poi");
		init();
	}
	
	private void init(){
		userEyeNameEditText = (EditText)findViewById(R.id.et_editusereye_name);
		directionEditText = (EditText)findViewById(R.id.et_editusereye_direction);
		if(poi!=null){
			userEyeNameEditText.setText(poi.name);
			directionEditText.setText(poi.angle+"");
		}
		if(typeIdArr==null){
			typeIdArr = this.getResources().getStringArray(R.array.usereyetypeid);
		}
		saveButton = (Button)findViewById(R.id.bt_editusereye_save);
		saveButton.setOnClickListener(this);
		typeSpinner = (Spinner) findViewById(R.id.spinner_editusereye_type);
		speedSpinner = (Spinner) findViewById(R.id.spinner_editusereye_speed);
		ArrayAdapter<String> speedAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, this.getResources()
						.getStringArray(R.array.limitSpeed));

		speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		speedSpinner.setAdapter(speedAdapter);
		typeArr = this.getResources().getStringArray(R.array.usereyetype);
		speedSpinner.setOnItemSelectedListener(new SpeedSpinnerSelectedListener());
		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,typeArr);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(typeAdapter);
		typeSpinner.setOnItemSelectedListener(new TypeSpinnerSelectedListener());
		
		int value_index = poi.limitSpeed/10-1;
        if(value_index<0){
        	value_index=0;
        }
        speedSpinner.setSelection(value_index);
        
        for(int index =0;index<typeIdArr.length;index++){
			if(typeIdArr[index].equals(""+poi.type)){
				typeSpinner.setSelection(index);
				break;
			}
		}
	}
	/**
	 * 初使化顶部控件
	 */
	private void initTop(){
		TextView topicTextView = (TextView)findViewById(R.id.tv_topic);
		topicTextView.setText(R.string.editusereye_topic);
		ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_menu_back);
		backImageButton.setOnClickListener(new BackListener(this, false, false));
	}

	@Override
	public void onClick(View v) {
		if(v.equals(saveButton)){
			saveUserEye();
		}
	}
	
	/**
	 * 保存电子眼
	 */
	private void saveUserEye(){
		poi.name = userEyeNameEditText.getText().toString();
		String angle = directionEditText.getText().toString();
		if(angle==null||angle.length()==0){
			Utils.showTipInfo(this, R.string.editeusereye_angle_error_tip);
			return ;
		}
		
		poi.angle = Long.parseLong(directionEditText.getText().toString());
		if(poi.angle>360){
			Utils.showTipInfo(this, R.string.editeusereye_angle_error_tip);
			return ;
		}
		if(speedIndex==0){
			poi.limitSpeed = 0;
		}else{
			poi.limitSpeed =(short)((speedIndex+1)*10);
		}
		if(typeIndex==0){
			poi.type = 1;
		}else{
			poi.type = Short.parseShort(typeIdArr[typeIndex]);
		}
		/************解决电子眼类型播报不正确问题**************/
		RouteAPI.getInstance().delUserEyeData(poi.id, poi.longitude, poi.latitude);
		RouteAPI.getInstance().addUserEyeData(poi);
		/****************************************************/
		UserEyeAPI.getInstance().updateUserEye(poi);
//		Intent routeIntent = new Intent(Constants.ACTIVITY_USEREYE);
//		startActivity(routeIntent);
		EditUserEyeActivity.this.finish();
	}
	class SpeedSpinnerSelectedListener implements OnItemSelectedListener{   
		  
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,   
                long arg3) { 
        	speedIndex = arg2;
        }   
  
        public void onNothingSelected(AdapterView<?> arg0) {   
        }   
    }
	
	class TypeSpinnerSelectedListener implements OnItemSelectedListener{   
		  
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,   
                long arg3) { 
        	typeIndex = arg2;
        }   
  
        public void onNothingSelected(AdapterView<?> arg0) {   
        }   
    }
}
