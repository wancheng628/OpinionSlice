package com.ben.opinionslice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.tasks.LoadFeedLocation;
import com.ben.opinionslice.tasks.LoadUserInfo;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private ImageButton loginButton;
	private static final String TAG = "FB Login";
	ArrayList<String> permissions;
	private OSApplication mApplication;
	
	TextView txtPreventSapm;
	TextView txtLimitData;
	TextView txtDontAskFriend;
	TextView txtDontPost;
	TextView txtDontShare;
	TextView txtUnless;
	TextView linkPrivacy;
	TextView linkTerm;
	
    private Session.StatusCallback callback = new Session.StatusCallback() {
    	@Override
	    public void call(Session session, SessionState state, Exception exception) {
	    	if (session.isOpened()) {
	    		Request.newMeRequest(session, new Request.GraphUserCallback() {

    			  // callback after Graph API response with user object
    			  @Override
    			  public void onCompleted(GraphUser user, Response response) {
    				  Log.d(TAG, response.toString());
    				  if (user != null) {
    					  Log.d(TAG, "Facebook login success");
    					  Log.d(TAG, user.toString());
    					  
    					  String facebook_id = user.getId();//user id
                          String name= user.getName();
                          String birthday = user.getBirthday();
                          String profile_picture = "https://graph.facebook.com/" + user.getId() + "/picture";
                          String email = user.asMap().get("email").toString();
                          String gender = user.asMap().get("gender").toString();
                          String location = (user.getLocation() == null) ? "" : user.getLocation().getName();
                          
                          Log.d("picture", profile_picture);
                          Log.d("email", email);
                          Log.d("location", "location:"+location);
                          Log.d("gender", gender);
                          
                          loadUserInfo(facebook_id, name, email, location, birthday, gender, profile_picture);
                          
    				  }else{
    					  Log.d(TAG, "No Facebook User Info");
    				  }
    			  }
    			}).executeAsync();
	    	}else{
	    		
	    	}
	    }
    };
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ben.opinionslice", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:====================",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Toast.makeText(this, Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
		
		mApplication = (OSApplication)getApplication();
        
		loginButton = (ImageButton) findViewById(R.id.login_button);
		if(mApplication.getLanguage().equalsIgnoreCase("fr")){
			loginButton.setBackgroundResource(R.drawable.fb_login_fr);
		}else{
			loginButton.setBackgroundResource(R.drawable.fb_login_en);
		}
		
        permissions = new ArrayList<String>();
        permissions.add("email");
		permissions.add("user_location");
		permissions.add("user_birthday");
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, callback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            /*
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	Session.OpenRequest openRequest = new Session.OpenRequest(LoginActivity.this).setPermissions(permissions).setCallback(callback);
                session.openForRead(openRequest);
            }
            */
        }
       // uiHelper = new UiLifecycleHelper(this, callback);
        //uiHelper.onCreate(savedInstanceState);

        
		//loginButton.setReadPermissions(permissions);
        
        loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//openActiveSession(LoginActivity.this, true, callback, permissions);
				Session session = Session.getActiveSession();
		        if (!session.isOpened() && !session.isClosed()) {
		        	Session.OpenRequest openRequest = new Session.OpenRequest(LoginActivity.this).setPermissions(permissions).setCallback(callback);
		            session.openForRead(openRequest);
		        } else {
		            //Session.openActiveSession(LoginActivity.this, true, callback);
		        	openActiveSession(LoginActivity.this, true, callback, permissions);
		        }
			}
		});
        
        txtPreventSapm = (TextView) findViewById(R.id.txt_prevent_spam);
        txtLimitData = (TextView) findViewById(R.id.txt_limit_data);
        txtDontAskFriend = (TextView) findViewById(R.id.txt_dont_ask_friend);
        txtDontPost = (TextView) findViewById(R.id.txt_dont_post);
        txtDontShare = (TextView) findViewById(R.id.txt_dont_share);
        txtUnless = (TextView) findViewById(R.id.txt_unless);
        
        linkPrivacy = (TextView) findViewById(R.id.link_privacy);
        linkTerm = (TextView) findViewById(R.id.link_term);
        String link = String.format("<a href=\"%s\">%s</a>",
        		Utils._("privacy_link"),
        		"Privacy");
        Log.d("Link", link);
        linkPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        linkPrivacy.setText(Html.fromHtml(link));
        
        link = String.format("<a href=\"%s\">%s</a>",
        		Utils._("term_link"),
        		"Term of use");
        linkTerm.setMovementMethod(LinkMovementMethod.getInstance());
        linkTerm.setText(Html.fromHtml(link));
        
        txtPreventSapm.setText(Utils._("prevent_spam"));
        txtLimitData.setText(Utils._("limit_data"));
        txtDontAskFriend.setText(Utils._("dont_ask_friend_list"));
        txtDontPost.setText(Utils._("dont_post"));
        txtDontShare.setText(Utils._("dont_share_identity"));
        txtUnless.setText(Utils._("unless_required"));
        
	}
	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(callback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(callback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

	private static Session openActiveSession(Activity activity, boolean allowLoginUI, Session.StatusCallback callback, List<String> permissions) {
	    Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
		//Session.OpenRequest openRequest = new Session.OpenRequest(activity).setCallback(callback);
	    Session session = new Session.Builder(activity).build();
	    if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
	        Session.setActiveSession(session);
	        session.openForRead(openRequest);
	        return session;
	    }
	    return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    //uiHelper.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	/**
	 * Logout From Facebook 
	 */
	public static void callFacebookLogout(Context context) {
	    Session session = Session.getActiveSession();
	    if (session != null) {

	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	        }
	    } else {

	        session = new Session(context);
	        Session.setActiveSession(session);

	        session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	    }
	}
	private void loadUserInfo(String fbId, String name, String email, String location, String birthday, String gender, String profile_picture) {
		ShowDialog.showLoadingDialog(LoginActivity.this, OSConfig.MESSAGE_LOADING_USERINFO);
		LoadUserInfo asynTask = new LoadUserInfo(LoginActivity.this, fbId, name, email, location, birthday, gender, profile_picture, new LoadingListener() {

                    @Override
                    public void onError(Object error) {
                    	ShowDialog.removeLoadingDialog();
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
							mApplication.setLogin(true);
							
							User user = (User)obj;
							
							int notification = mApplication.isPushEnabled() ? 1:0;
							
							if(notification != user.getNotificationByLanguage(mApplication.getLanguage())){
								//Parse.initialize(LoginActivity.this, OSConfig.PARSE_APP_ID, OSConfig.PARSE_CLIENT_KEY);
								ParseInstallation installation = ParseInstallation.getCurrentInstallation();
								
								if(user.getNotification() == 0){
									//PushService.unsubscribe(LoginActivity.this, "fr");
									//PushService.unsubscribe(LoginActivity.this, "en");
									installation.put("language", "");
									mApplication.setPushEnabled(false);
								}else{
									/*
									if(mApplication.getLanguage().equalsIgnoreCase("en")){
										PushService.unsubscribe(LoginActivity.this, "fr");
										PushService.subscribe(LoginActivity.this, "en", CommentActivity.class, R.drawable.notification1);
										
									}else{
										PushService.unsubscribe(LoginActivity.this, "en");
										PushService.subscribe(LoginActivity.this, "fr", CommentActivity.class, R.drawable.notification1);
									}
									ParseAnalytics.trackAppOpened(getIntent());
									*/
									installation.put("language", mApplication.getLanguage());
									mApplication.setPushEnabled(true);
								}
								installation.saveInBackground();
							}
						}
						loadFeedLocations();
					}
                });

        asynTask.execute();
    }
	private void loadFeedLocations(){
		LoadFeedLocation asynTask = new LoadFeedLocation(LoginActivity.this, new LoadingListener() {

            @Override
            public void onError(Object error) {
            	ShowDialog.removeLoadingDialog();
            }

			@Override
			public void onLoadingComplete(List obj) {
				ShowDialog.removeLoadingDialog();
				if(obj != null){
					Log.d("Location Load Complete::", obj.toString());
					for(int i=0; i<9; i++){
						if(obj.get(i) != null && !obj.get(i).toString().isEmpty()){
							((OSApplication)getApplication()).getFeed(i).setLocation(obj.get(i).toString());
						}
					}
				}else{
					Log.d("Location Load Failed::", "Failed");
				}
				
				Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
			}

			@Override
			public void onLoadingComplete(Object obj) {
				
			}
        });
		asynTask.execute();
	}
}
