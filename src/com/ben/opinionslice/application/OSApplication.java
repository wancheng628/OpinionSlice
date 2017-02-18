package com.ben.opinionslice.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.ben.opinionslice.application.OSConfig.OP_STATUS;
import com.ben.opinionslice.data.Countries;
import com.ben.opinionslice.data.Feed;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.utils.Utils;
import com.parse.Parse;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

public class OSApplication extends Application{

	public ArrayList<Feed> feedList = new ArrayList<Feed>();
	public ArrayList<Countries> countryList = new ArrayList<Countries>();
	private String language;
	private int currentFeedIndex;
	private Question currentQuestion;
	private User user;
	private boolean isLogin;
	private static Context mContext;
	private OP_STATUS current_status;
	private SharedPreferences session;
	private boolean pushEnabled;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	@Override
    public void onCreate() {
        super.onCreate();
        Log.d("OSApp", "Launching...");
        mContext = getApplicationContext();
        
        session = getSharedPreferences(PREFS_NAME, 0);
        //this.language = OSConfig.DEFAULT_LANGUAGE;
        String phone_lang = Locale.getDefault().getLanguage();
		
		if(retrieveLanguage() == null){
			if(phone_lang.equalsIgnoreCase("fr"))
				this.language = "fr";
			else
				this.language = "en";
			storeLanguage(this.language);
		}else{
			this.language = retrieveLanguage();
		}
		Log.d("Current Language",language);
        this.currentFeedIndex = 0;
        this.user = loadUser();
        this.isLogin = false;
        this.currentQuestion = null;
        this.current_status = OP_STATUS.OP_STATUS_NORMAL;
        this.pushEnabled = false;
        Utils.language = this.language;
        initFeeds();
        Log.d("OSApp", "Initializing...");
        
        Parse.initialize(this, OSConfig.PARSE_APP_ID, OSConfig.PARSE_CLIENT_KEY);
    }
	public static Resources getMyResources() {
        return mContext.getResources();
    }
	
	private void initFeeds(){
		for(int i = 0; i < 9; i++){
			String location_en = getFeedLocation(i, "en");
			String location_fr = getFeedLocation(i, "fr");
			if(location_en.isEmpty())
				location_en = OSConfig.FEED_LOCATIONS_EN[i];
			if(location_fr.isEmpty())
				location_fr = OSConfig.FEED_LOCATIONS_FR[i];
			Feed feed = new Feed(i, OSConfig.FEED_TITLES_EN[i], OSConfig.FEED_TITLES_FR[i], location_en, location_fr, OSConfig.FEED_KEYS[i], language);
			feedList.add(feed);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
		storeLanguage(language);
	}

	public int getCurrentFeedIndex() {
		return currentFeedIndex;
	}

	public void setCurrentFeedIndex(int currentFeedIndex) {
		this.currentFeedIndex = currentFeedIndex;
	}
	
	public Feed getCurrentFeed(){
		Feed feed = feedList.get(currentFeedIndex);
		feed.setLanguage(language);
		return feed;
	}
	public User getUser(){
		return this.user;
	}
	public void setUser(User user){
		this.user = user;
		storeUser(user);
	}
	public User loadUser() {
		//return session.get("login", false);
		Log.d("User", session.getString("user", ""));
		return User.parseUser(session.getString("user", ""));
		//return user;
	}

	public void storeUser(User user) {
		//this.user = user;
		SharedPreferences.Editor editor = session.edit();
		if(user == null){
			if(session.getString("user",null) != null){
				editor.remove("user");
				editor.commit();
			}
		}else{
			editor.putString("user", user.toString());
			editor.commit();
		}
	}
	public Feed getFeed(int index){
		Feed feed = feedList.get(index);
		feed.setLanguage(language);
		return feed;
	}

	public int getCurrentQuestionId() {
		return currentQuestion.getId();
	}

	public Question getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public boolean isLogin() {
		//return isLogin;
		//SharedPreferences.Editor editor = session.edit();
	    //editor.putBoolean("login", isLogin);
	    //editor.commit();
	    return session.getBoolean("login", false);
	}

	public void setLogin(boolean isLogin) {
		//this.isLogin = isLogin;
		SharedPreferences.Editor editor = session.edit();
	    editor.putBoolean("login", isLogin);
	    editor.commit();
	}

	public OP_STATUS getCurrentStatus() {
		return current_status;
	}

	public void setCurrentStatus(OP_STATUS current_status) {
		this.current_status = current_status;
	}
	public void setVoteStatus(int question_id, String vote){
		SharedPreferences.Editor editor = session.edit();
	    editor.putString("vote_"+question_id, vote);
	    editor.commit();
	}
	public String getVoteStatus(int question_id){
		return session.getString("vote_"+question_id, null);
	}
	public void storeFeedLocation(int feedId, String location){
		SharedPreferences.Editor editor = session.edit();
	    editor.putString("location_"+feedId+"_"+language, location);
	    editor.commit();
	}
	public String getFeedLocation(int feedId, String language){
		return session.getString("location_"+feedId+"_"+language, "");
	}
	public void storeFollowStatus(int comment_id, String vote){
		SharedPreferences.Editor editor = session.edit();
	    editor.putString("follow_"+comment_id, vote);
	    editor.commit();
	}
	public String getFollowStatus(int comment_id){
		return session.getString("follow_"+comment_id, null);
	}
	public void storeQuestionPosition(int feedId, int position){
		SharedPreferences.Editor editor = session.edit();
	    editor.putInt("feed_"+feedId+"_qposition", position);
	    editor.commit();
	}
	public int getQuestionPosition(int feedId){
		return session.getInt("feed_"+feedId+"_qposition", 0);
	}
	public boolean isPushEnabled() {
		return pushEnabled;
	}
	public void setPushEnabled(boolean pushEnabled) {
		this.pushEnabled = pushEnabled;
	}
	
	public void storeLanguage(String language) {
		//this.isLogin = isLogin;
		SharedPreferences.Editor editor = session.edit();
	    editor.putString("language", language);
	    editor.commit();
	}
	public String retrieveLanguage() {
		return session.getString("language", null);
	}
}
