package com.ben.opinionslice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.GooglePlacesAutoCompleteAdapter;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.tasks.GetCurrentLocation;
import com.ben.opinionslice.tasks.SaveQuestion;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PostQuestionActivity extends Activity implements OnClickListener{

	List<Button> tagButtons;
	RelativeLayout optionLinkLayout;
	RelativeLayout optionLocationLayout;
	RelativeLayout optionFeedLayout;
	//LinearLayout keyboardLayout1;
	//LinearLayout keyboardLayout2;
	ImageView avatarView;
	TextView nameView;
	TextView counterView;
	TextView txtSelectTag;
	EditText edit_link;
	EditText edit_content;
	AutoCompleteTextView edit_location;
	CheckBox chk_anonymous;
	ImageButton gpsButton;
	Button postButton;
	ImageButton oLinkButton;
	ImageButton oTagButton;
	ImageButton oLocationButton;
	InputMethodManager imm;
	User user;
	int feedId;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	
	OSApplication mApplication;
	LocationManager mLocationManager;
	Location currentLocation;
	
	private final LocationListener mLocationListener = new LocationListener() {
	    @Override
	    public void onLocationChanged(final Location location) {
	        //your code here
	    	currentLocation = location;
	    }

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
	};
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_question);
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		
		avatarView = (ImageView) findViewById(R.id.img_user_profile_picture);
		nameView = (TextView)findViewById(R.id.txt_user_profile_name);
		txtSelectTag = (TextView)findViewById(R.id.txt_select_tag);
		
		optionLinkLayout = (RelativeLayout)findViewById(R.id.layout_question_option_link);
		optionLocationLayout = (RelativeLayout)findViewById(R.id.layout_question_option_location);
		optionFeedLayout = (RelativeLayout)findViewById(R.id.layout_question_option_feed);
		
		//keyboardLayout1 = (LinearLayout)findViewById(R.id.layout_keyboard1);
		edit_link = (EditText)findViewById(R.id.txt_question_option_link);
		edit_location = (AutoCompleteTextView)findViewById(R.id.txt_question_option_location);
		edit_content = (EditText)findViewById(R.id.edit_write_question);
		chk_anonymous = (CheckBox)findViewById(R.id.chk_question_stay_anonymous);
		counterView = (TextView)findViewById(R.id.txt_question_character_left);
		gpsButton = (ImageButton)findViewById(R.id.btn_question_option_gps);
		gpsButton.setOnClickListener(this);
		
		mApplication = (OSApplication)getApplication();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(PostQuestionActivity.this));

		feedId = -1;

		edit_link.setHint(Utils._("paste_link"));
		edit_location.setHint(Utils._("add_location"));
		edit_location.setText(Utils._("default_location"));
		edit_content.setHint(Utils._("write_question"));
		chk_anonymous.setText(Utils._("anonymous"));
		txtSelectTag.setText(Utils._("select_tag"));
		
		edit_location.setAdapter(new GooglePlacesAutoCompleteAdapter(this, R.layout.layout_list_item));
		edit_location.addTextChangedListener(new TextWatcher() {         
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	updatePostButtnStyle();
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {                
	        		
	        }

	        @Override
	        public void afterTextChanged(Editable s) {

	        }
	    });
		
		final TextWatcher mTextEditorWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	if (s.length() >= 170)
	            {
	                new AlertDialog
	                	.Builder(PostQuestionActivity.this)
	                	.setTitle("Character limit exceeded")
	                	.setMessage("Input cannot exceed more than " + 170 + " characters.").
	                	setPositiveButton(android.R.string.ok, null).
	                	show();
	                
	            }
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	           //This sets a textview to the current length
	        	counterView.setText(String.valueOf(170-s.length()));
	        	updatePostButtnStyle();
	        }

	        public void afterTextChanged(Editable s) {
	        }
		};
		edit_content.addTextChangedListener(mTextEditorWatcher);
		
		tagButtons = new ArrayList<Button>();
		
		Button button;
		button = (Button)findViewById(R.id.btn_question_tag_new);
		button.setText(Utils._("news"));
		button.setOnClickListener(this);
		//button.setSelected(true);
		//button.setTextColor(getResources().getColor(R.color.white));
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_politics);
		button.setText(Utils._("politics"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_business);
		button.setText(Utils._("business"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_tech);
		button.setText(Utils._("tech"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_sport);
		button.setText(Utils._("sport"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_entertainment);
		button.setText(Utils._("entertainment"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_planet);
		button.setText(Utils._("planet"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_lifestyle);
		button.setText(Utils._("lifestyle"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_question_tag_health);
		button.setText(Utils._("health"));
		button.setOnClickListener(this);
		tagButtons.add(button);
		
		button = (Button)findViewById(R.id.btn_cancel);
		button.setText(Utils._("cancel"));
		button.setOnClickListener(this);
		
		postButton = (Button)findViewById(R.id.btn_question_post);
		postButton.setText(Utils._("post"));
		postButton.setOnClickListener(this);
		
		oLinkButton = (ImageButton)findViewById(R.id.btn_question_option_link);
		oLinkButton.setOnClickListener(this);
		
		oTagButton = (ImageButton)findViewById(R.id.btn_question_option_feed);
		oTagButton.setOnClickListener(this);
		
		oLocationButton = (ImageButton)findViewById(R.id.btn_question_option_location);
		oLocationButton.setOnClickListener(this);
		oLocationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location_blue));
		
		imm.showSoftInput(edit_location, 0);
		
		refreshData();
	}

	private void refreshData(){
		user = mApplication.getUser();
		String image_url = "";
		if(user == null || user.getAvatar() == "")
			image_url = "http://www.opinionslice.com/img/icons/images.jpg";
		else
			image_url = user.getAvatar();
		
		//loader.DisplayImage(image_url, avatarView, false);
		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader.displayImage(image_url, avatarView, options, animateFirstListener);
		
		String user_name = "";
		if(user == null || user.getAvatar() == "")
			user_name = Utils._("anonymous1");
		else
			user_name = user.getUserName();
		nameView.setText(user_name);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_question, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button button;
		if(v.getId() == R.id.btn_question_option_link){
			oLinkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_link_blue));
			oLocationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location));
			oTagButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_tag));
			
			optionLinkLayout.setVisibility(View.VISIBLE);
			optionLocationLayout.setVisibility(View.GONE);
			optionFeedLayout.setVisibility(View.GONE);
			edit_link.requestFocus();
			imm.showSoftInput(edit_link, 0);
		}else if(v.getId() == R.id.btn_question_option_location){
			
			oLinkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_link));
			oLocationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location_blue));
			oTagButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_tag));
			
			optionLinkLayout.setVisibility(View.GONE);
			optionLocationLayout.setVisibility(View.VISIBLE);
			optionFeedLayout.setVisibility(View.GONE);
			edit_location.requestFocus();
			imm.showSoftInput(edit_location, 0);
		}else if(v.getId() == R.id.btn_question_option_feed){
			
			oLinkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_link));
			oLocationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location));
			oTagButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_tag_blue));
			
			optionLinkLayout.setVisibility(View.GONE);
			optionLocationLayout.setVisibility(View.GONE);
			optionFeedLayout.setVisibility(View.VISIBLE);
			imm.hideSoftInputFromWindow(optionFeedLayout.getApplicationWindowToken(), 0);
		}else if(v.getId() == R.id.btn_question_option_gps){
			
			Log.d("GPS", "Clicked");
			//getLocation(39.86481939999999,-75.4257325);
			Location location = this.getLocationNetwork();
			
		    if (location != null) {
		        System.out.println("Provider has been selected.");
		        double lat = location.getLatitude();
		        double lng = location.getLongitude();

		        Log.d("Lat", lat + "");
		        Log.d("Lng", lng + "");
		        getLocation(lat, lng);
		        
		    } else {
		        Log.e("OSAPP", "Location not detected");
		    }
		    
		}else if(v.getId() == R.id.btn_question_post){
			if(!canPost())
				return;
			Log.d("Question Post", "Location");
			
			String content = edit_content.getText().toString();
			String location = edit_location.getText().toString();
			String link = edit_link.getText().toString();
			if(!link.isEmpty() && !link.startsWith("http://") && !link.startsWith("https://"))
				link = "http://" + link;
				
			Log.d("Question Post", "Button Clicked");
			boolean is_anonymous =  chk_anonymous.isChecked();
			SaveQuestion(feedId, content, link, is_anonymous,location);
			
		}else if(v.getId() == R.id.btn_cancel){
			finish();
		}else{
			if(tagButtons.size() > 0){
				for(int i=0; i<tagButtons.size(); i++){
					tagButtons.get(i).setBackgroundColor(getResources().getColor(R.color.custom_gray));
					tagButtons.get(i).setTextColor(getResources().getColor(R.color.black));
				}
			}
			switch(v.getId()){
				case R.id.btn_question_tag_new:
					feedId = 0;
					break;
				case R.id.btn_question_tag_politics:
					feedId = 1;
					break;
				case R.id.btn_question_tag_business:
					feedId = 2;
					break;
				case R.id.btn_question_tag_tech:
					feedId = 3;
					break;
				case R.id.btn_question_tag_sport:
					feedId = 4;
					break;
				case R.id.btn_question_tag_entertainment:
					feedId = 5;
					break;
				case R.id.btn_question_tag_planet:
					feedId = 6;
					break;
				case R.id.btn_question_tag_lifestyle:
					feedId = 7;
					break;
				case R.id.btn_question_tag_health:
					feedId = 8;
					break;
				default:
					feedId = -1;
					break;
			}
			button = (Button) findViewById(v.getId());
			button.setBackgroundColor(getResources().getColor(R.color.blue_main));
			button.setTextColor(getResources().getColor(R.color.white));
			
			updatePostButtnStyle();
		}
	}
	private void SaveQuestion(int feedId, String content, String link, boolean is_anonymous, String location) {
        SaveQuestion asynTask = new SaveQuestion(PostQuestionActivity.this, feedId, content, link, location, is_anonymous, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {
					}

					@Override
					public void onLoadingComplete(Object obj) {
						// TODO Auto-generated method stub
						if(obj != null){
							Log.d("Question Save Complete::", obj.toString());
							Question question = JSONParser.getQuestionfromJson((String)obj);
							mApplication.setCurrentQuestion(question);
							Intent intent = new Intent(PostQuestionActivity.this, CommentActivity.class);
							startActivity(intent);
							finish();
						}
					}
                });

        asynTask.execute();
    }
	private void getLocation(double lat, double lng) {
        GetCurrentLocation asynTask = new GetCurrentLocation(PostQuestionActivity.this, lat, lng, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {
					}

					@Override
					public void onLoadingComplete(Object obj) {
						// TODO Auto-generated method stub
						if(obj != null){
							Log.d("Get Location::", obj.toString());
							edit_location.setText((String)obj);
						}
					}
                });

        asynTask.execute();
    }
	private boolean canPost(){
		if(edit_content.getText().toString().length() <= 0)
			return false;
		if(edit_location.getText().toString().length() <=0)
			return false;
		if(feedId == -1)
			return false;
		return true;
	}
	private void updatePostButtnStyle(){
		if(canPost()){
			postButton.setBackgroundColor(getResources().getColor(R.color.blue_main));
		}else{
			postButton.setBackgroundColor(getResources().getColor(R.color.gray_main));
		}
			
	}
	public Location getLocationNetwork() {
		Location location = null;
	    try {
 	        // getting GPS status
	        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        // getting network status
	        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	        if (!isGPSEnabled && !isNetworkEnabled) {
	            // no network provider is enabled
	        } else {
	            if (isNetworkEnabled) {
	            	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
	                Log.d("Network", "Network Enabled");
	                if (mLocationManager != null) {
	                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    
	                }
	            }
	            // if GPS Enabled get lat/long using GPS Services
	            if (isGPSEnabled) {
	                if (location == null) {
	                	mLocationManager.requestLocationUpdates(
	                            LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
	                    Log.d("GPS", "GPS Enabled");
	                    if (mLocationManager != null) {
	                        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                    }
	                }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return location;
	}
}
