package com.ben.opinionslice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Countries;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.listener.GooglePlacesAutoCompleteAdapter;
import com.ben.opinionslice.tasks.LoadCountries;
import com.ben.opinionslice.tasks.LoadUserInfo;
import com.ben.opinionslice.tasks.UpdateUserInfo;
import com.ben.opinionslice.utils.Utils;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnFocusChangeListener, OnClickListener, OnItemSelectedListener, OnItemClickListener, OnTouchListener{

	TextView nameLabel;
	TextView liveinLabel;
	TextView birthdayLabel;
	TextView sexLabel;
	TextView nationalityLabel;
	TextView notificationLabel;
	TextView tailoredLabel;
	
	Button maleButton;
	Button femaleButton;
	
	EditText nameText;
	EditText dayText;
	EditText monthText;
	EditText yearText;
	AutoCompleteTextView liveinText;
	Spinner nationalitySpin;
	Button button;
	Switch notificaitonSwitch;
	//Switch vibrationSwitch;
	
	String name;
	String location;
	String birthday;
	String birthday_date, birthday_month, birthday_year;
	String nationality;
	String gender;
	boolean isNotification;
	boolean isVibrate;
	private OSApplication mApplication;
	private User user;
	InputMethodManager imm;
	ArrayAdapter<String> mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mApplication = (OSApplication)getApplication();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		user = mApplication.getUser();
		
		nameLabel = (TextView)findViewById(R.id.lb_setting_name);
		liveinLabel = (TextView)findViewById(R.id.lb_setting_livein);
		birthdayLabel = (TextView)findViewById(R.id.lb_setting_birthday);
		nationalityLabel = (TextView) findViewById(R.id.lb_setting_nationality);
		sexLabel = (TextView) findViewById(R.id.lb_setting_sex);
		notificationLabel = (TextView) findViewById(R.id.lb_setting_notification);
		tailoredLabel = (TextView) findViewById(R.id.lb_setting_tailored);
		
		nameLabel.setText(Utils._("name"));
		liveinLabel.setText(Utils._("lives"));
		birthdayLabel.setText(Utils._("birthday"));
		nationalityLabel.setText(Utils._("nationality"));
		sexLabel.setText(Utils._("sex"));
		notificationLabel.setText(Utils._("notification"));
		tailoredLabel.setText(Utils._("tailored"));
		
		nameText = (EditText)findViewById(R.id.txt_setting_name);
		nameText.setOnFocusChangeListener(this);
		
		dayText = (EditText)findViewById(R.id.txt_setting_birthday_day);
		dayText.setOnFocusChangeListener(this);
		
		monthText = (EditText)findViewById(R.id.txt_setting_birthday_month);
		monthText.setOnFocusChangeListener(this);
		
		yearText = (EditText)findViewById(R.id.txt_setting_birthday_year);
		yearText.setOnFocusChangeListener(this);
		
		maleButton = (Button)findViewById(R.id.btn_setting_sex_male);
		maleButton.setText(Utils._("guy"));
		maleButton.setOnClickListener(this);
		
		femaleButton = (Button)findViewById(R.id.btn_setting_sex_female);
		femaleButton.setText(Utils._("girl"));
		femaleButton.setOnClickListener(this);
		
		liveinText = (AutoCompleteTextView)findViewById(R.id.txt_setting_livein);
		liveinText.setOnFocusChangeListener(this);
		liveinText.setAdapter(new GooglePlacesAutoCompleteAdapter(this, R.layout.layout_list_item));
		liveinText.setOnItemClickListener(this);
	    
	    LinearLayout layout_setting_other = (LinearLayout)findViewById(R.id.layout_setting_main);
	    layout_setting_other.setOnTouchListener(this);
	    
	    button = (Button)findViewById(R.id.btn_setting_save);
	    button.setText(Utils._("save"));
		button.setOnClickListener(this);
		
		button = (Button)findViewById(R.id.btn_cancel);
		button.setText(Utils._("cancel"));
		button.setOnClickListener(this);
		
		nationalitySpin = (Spinner) findViewById(R.id.spin_setting_nationality);
		nationalitySpin.setOnItemSelectedListener(this);
        
		notificaitonSwitch = (Switch) findViewById(R.id.switch_setting_notification);
		notificaitonSwitch.setTextOn(Utils._("on"));
		notificaitonSwitch.setTextOff(Utils._("off"));
		//vibrationSwitch = (Switch) findViewById(R.id.switch_setting_vibrate);
		notificaitonSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	isNotification = isChecked;
		    }
		});
		
		/*
		vibrationSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	isVibrate = isChecked;
		    }
		});
		*/
		loadUserInfo("", "", user.getEmail(), "", "", "", "");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean isFocused) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.txt_setting_name:
				if(isFocused){
					nameLabel.setTextColor(getResources().getColor(R.color.blue_main));
				}else{
					nameLabel.setTextColor(getResources().getColor(R.color.gray_main));
					//imm.hideSoftInputFromWindow(nameLabel.getApplicationWindowToken(), 0);
				}
				break;
			case R.id.txt_setting_livein:
				if(isFocused)
					liveinLabel.setTextColor(getResources().getColor(R.color.blue_main));
				else{
					liveinLabel.setTextColor(getResources().getColor(R.color.gray_main));
					//imm.hideSoftInputFromWindow(nameLabel.getApplicationWindowToken(), 0);
					
				}
				break;
			case R.id.txt_setting_birthday_day:
			case R.id.txt_setting_birthday_month:
			case R.id.txt_setting_birthday_year:
				if(isFocused)
					birthdayLabel.setTextColor(getResources().getColor(R.color.blue_main));
				else
					birthdayLabel.setTextColor(getResources().getColor(R.color.gray_main));
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_setting_save:
			name = nameText.getText().toString();
			birthday_date = (dayText.getText().toString().isEmpty()) ? "01" : dayText.getText().toString();
			birthday_month = (monthText.getText().toString().isEmpty()) ? "01" : monthText.getText().toString();
			birthday_year = (yearText.getText().toString().isEmpty()) ? "1970" : yearText.getText().toString();
			
			birthday = birthday_date + "/" + birthday_month + "/" + birthday_year;
			nationality = Countries.getCodeFromCountryName(nationalitySpin.getSelectedItem().toString());
			int notification_en = user.getNotification();
			int notification_fr = user.getNotificationFr();
			if(mApplication.getLanguage().equalsIgnoreCase("en"))
				notification_en = (isNotification) ? 1:0;
			else
				notification_fr = (isNotification) ? 1:0;
			
			saveUserInfo(name, location, birthday, nationality, gender, notification_en, notification_fr, (isVibrate) ? 1:0);
			break;
		case R.id.btn_cancel:
			Intent returnIntent = new Intent();
	        setResult(RESULT_OK,returnIntent);
	        finish();
			break;
		case R.id.btn_setting_sex_male:
			gender = "male";
			maleButton.setTextColor(getResources().getColor(R.color.white));
			maleButton.setBackgroundColor(getResources().getColor(R.color.blue_main));
			femaleButton.setTextColor(getResources().getColor(R.color.gray_main));
			femaleButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
			break;
		case R.id.btn_setting_sex_female:
			gender = "female";
			maleButton.setTextColor(getResources().getColor(R.color.gray_main));
			maleButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
			femaleButton.setTextColor(getResources().getColor(R.color.white));
			femaleButton.setBackgroundColor(getResources().getColor(R.color.blue_main));
			break;
		default:
			break;
		}
	}
	
	private void initVariables(){
		gender = "male";
		name = "";
		location = "";
		birthday_date = "";
		birthday_month = "";
		birthday_year = "";
		nationality = "";
		isVibrate = false;
		isNotification = true;
		
		user = mApplication.getUser();
		if(user != null){
			gender = user.getGender();
			name = user.getUserName();
			location = user.getLocation();
			birthday_date = user.getBirthdayDate();
			birthday_month = user.getBirthdayMonth();
			birthday_year = user.getBirthdayYear();
			nationality = user.getNationality();
			isVibrate = (user.getVibration()==1) ? true:false;
			isNotification = (user.getNotificationByLanguage(mApplication.getLanguage())==1) ? true:false;
			nameText.setText(name);
			liveinText.setText(location);
			if(gender.equalsIgnoreCase("male")){
				maleButton.performClick();
			}else{
				femaleButton.performClick();
			}
			
			dayText.setText(birthday_date);
			monthText.setText(birthday_month);
			yearText.setText(birthday_year);
			
			//vibrationSwitch.setChecked(isVibrate);
			notificaitonSwitch.setChecked(isNotification);
		}
		Log.d("Natinality", nationality);
	}
	private void loadCountries() {
		if(Countries.count() > 0){
			loadNations(Countries.getAllCountryNames());
			return;
		}
        LoadCountries asynTask = new LoadCountries(SettingActivity.this, new LoadingListener() {

            @Override
            public void onError(Object error) {

            }

			@Override
			public void onLoadingComplete(List obj) {
				if(obj != null){
					loadNations(obj);
				}
			}

			@Override
			public void onLoadingComplete(Object obj) {
				// TODO Auto-generated method stub
				
			}
        });

        asynTask.execute();
    }
	private void loadUserInfo(String fbId, String name, String email, String location, String birthday, String gender, String profile_picture) {
		LoadUserInfo asynTask = new LoadUserInfo(SettingActivity.this, fbId, name, email, location, birthday, gender, profile_picture, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {

					}

					@Override
					public void onLoadingComplete(Object obj) {
						if(obj != null){
							Log.d("Load Complete::", obj.toString());
							// TODO Auto-generated method stub
							mApplication.setUser((User)obj);
							initVariables();
							loadCountries();
						}
					}
                });

        asynTask.execute();
    }
	private void saveUserInfo(final String name, final String location, final String birthday, final String nationality, final String gender, final int notification, final int notification_fr, final int vibration){
		UpdateUserInfo asynTask = new UpdateUserInfo(SettingActivity.this, name, location, birthday, nationality, gender, notification, notification_fr, vibration, new LoadingListener() {

            @Override
            public void onError(Object error) {

            }

			@Override
			public void onLoadingComplete(List obj) {
				
			}

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onLoadingComplete(Object obj) {
				// TODO Auto-generated method stub
				if(obj == null){
					Log.d("Update UserInfo", "Failed");
				}else{
					Log.d("Update UserInfo", "Success");
					user.setUserName(name);
					user.setLocation(location);
					//user.setBirthday(birthday);
					SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");  
					if(!birthday.isEmpty()){
						try {
							user.setBirthday(format.parse(birthday));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					user.setNationality(nationality);
					user.setGender(gender);
					
					user.setVibration(vibration);
					
					mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_UPDATE);
					
					int current_notificaiton = (mApplication.getLanguage().equalsIgnoreCase("en")) ? notification : notification_fr;
					if(current_notificaiton != user.getNotificationByLanguage(mApplication.getLanguage())){
						//Parse.initialize(SettingActivity.this, OSConfig.PARSE_APP_ID, OSConfig.PARSE_CLIENT_KEY);
						ParseInstallation installation = ParseInstallation.getCurrentInstallation();
						
						if(current_notificaiton == 0){
							installation.put("language","");
							mApplication.setPushEnabled(false);
						}else{
							installation.put("language",mApplication.getLanguage());
							mApplication.setPushEnabled(true);
						}
						installation.saveInBackground();
						
						if(mApplication.getLanguage().equalsIgnoreCase("en"))
							user.setNotification(current_notificaiton);
						else
							user.setNotificationFr(current_notificaiton);
					}
					mApplication.setUser(user);
					//finish();
				}
					
			}
        });

        asynTask.execute();
	}
	private void loadNations(List<String> countries){
		
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nationalitySpin.setAdapter(mAdapter);

        nationalitySpin.setSelection(mAdapter.getPosition(Countries.getNameFromCountryCode(nationality)));
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		// TODO Auto-generated method stub
		location = (String) adapterView.getItemAtPosition(position);
        Log.d("Location", location);
	}
	private void hideKeyboard(){
		getCurrentFocus().clearFocus();
		imm.hideSoftInputFromWindow(nameLabel.getApplicationWindowToken(), 0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		hideKeyboard();
		return false;
	}
}
