package com.ben.opinionslice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Feed;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.FeedPagerAdapter;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.tasks.LoadFeedQuestions;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.SearchManager;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
    public FeedPagerAdapter mAdapter;
    private ActionBar actionBar;
    private OSApplication mApplication;
    protected FragmentManager fm;
    private Tracker tracker;
    private SearchView searchView;
    private final int MENU_ITEM_LOGOUT = 103;
    private Menu menu;
    @SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
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
		
		tracker = GoogleAnalytics.getInstance(this).getTracker(OSConfig.TRACK_ID);
		
		User user = mApplication.getUser();
		if(user != null)
			Log.d("User", user.toString());
		initParse();
		fm = getSupportFragmentManager();
		initialize();
		// Add code to print out the key hash
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo("com.ben.opinionslice", PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
		//mApplication.setLogin(true);
	}
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
    	
    	MenuItem settingItem = menu.findItem(R.id.action_settings);
    	MenuItem languageItem = menu.findItem(R.id.action_language);
    	
    	if(mApplication.isLogin()){
    		settingItem.setTitle(Utils._("settings"));
    		if(menu.findItem(MENU_ITEM_LOGOUT) == null){
    			menu.findItem(R.id.a_More).getSubMenu().add(Menu.NONE, MENU_ITEM_LOGOUT, 103, Utils._("signout"));
    		}
    	}else{
    		if(menu.findItem(MENU_ITEM_LOGOUT) != null){
    			menu.removeItem(103);
    		}
    		settingItem.setTitle(Utils._("signin"));
    	}
    	if(mApplication.getLanguage().equalsIgnoreCase("en"))
    		languageItem.setTitle("French");
    	else
    		languageItem.setTitle("English");
    	return super.onPrepareOptionsMenu(menu);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
 
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setQueryHint(Utils._("search_by_question_number"));
        
        /*
        AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setTextColor(Color.WHITE);
        search_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_small));
        */
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            	searchView.setQuery("", false);
            	searchView.clearFocus();
            	searchView.onActionViewCollapsed();
            	return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // ...
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	    	if(mApplication.isLogin()){
	    		item.setTitle(Utils._("settings"));
	    		showSettingPage();
	    	}else{
	    		item.setTitle(Utils._("signin"));
	    		showLoginPage();
	    	}
	        return true;
	    case R.id.action_language:
	        changeLanguage(item);
	    	return true;
	    case MENU_ITEM_LOGOUT:
	    	logout();
	    	return true;
	    case R.id.action_search:
            // search action
            return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	private void logout(){
		LoginActivity.callFacebookLogout(this);
		mApplication.setLogin(false);
		mApplication.setUser(null);
		reload();
	}
	private void showSettingPage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivityForResult(intent, 705);
	}
	private void showLoginPage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivityForResult(intent, 701);
	}
	private void changeLanguage(MenuItem item) {
		// TODO Auto-generated method stub
		String current_language = mApplication.getLanguage();
		if(current_language.equalsIgnoreCase("en")){
			mApplication.setLanguage("fr");
			item.setTitle("English");
		}else{
			mApplication.setLanguage("en");
			item.setTitle("French");
		}
		Utils.language = mApplication.getLanguage();
		reload();
		//initialize();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
		tracker.set(Fields.SCREEN_NAME, OSConfig.FEED_TITLES_EN[tab.getPosition()] + " Feed Page");
	     // Send a screen view for each feed
	     tracker.send(MapBuilder
	         .createAppView()
	         .build()
	     );
	    
	     viewPager.setCurrentItem(tab.getPosition());
			
	     mApplication.setCurrentFeedIndex(tab.getPosition());
	     if(mApplication.getCurrentFeed().load_status == 0){
	    	 loadFeedQuestions(tab.getPosition());
	     }
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	private void initialize(){
		viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new FeedPagerAdapter(fm);
        
        viewPager.setAdapter(mAdapter);
        actionBar.setLogo(R.drawable.logo1);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        for (int i=0; i<mApplication.feedList.size(); i++) {
        	Feed feed = mApplication.feedList.get(i);
        	feed.load_status = 0;
        	feed.setLanguage(mApplication.getLanguage());
        	feed.removeQustionList();
            
            mAdapter.addItem(new FeedFragment(i), feed.getTitle());
            actionBar.addTab(actionBar.newTab().setText(feed.getTitle()).setTabListener(this));
        }
 
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
	}
	
	private void reload(){
		mApplication.setPushEnabled(false);
		finish();
		startActivity(getIntent());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 701) {
	        if(resultCode == RESULT_OK){
	        	reload();
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }else if (requestCode == 704 || requestCode == 705) {
	        if(resultCode == RESULT_OK){
	        	Log.d("Current Status", mApplication.getCurrentStatus() + "");
	        	if(mApplication.getCurrentStatus() == OSConfig.OP_STATUS.OP_STATUS_UPDATE){
	        		updateMenuTitle();
	        		mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_NORMAL);
	        		loadFeedQuestions(mApplication.getCurrentFeedIndex());
	        	}
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	}
	private void updateMenuTitle() {
        MenuItem settingItem = menu.findItem(R.id.action_settings);
        if(mApplication.isLogin()){
    		settingItem.setTitle(Utils._("settings"));
    		if(menu.findItem(MENU_ITEM_LOGOUT) == null){
    			menu.findItem(R.id.a_More).getSubMenu().add(Menu.NONE, MENU_ITEM_LOGOUT, 103, Utils._("signout"));
    		}
    	}else{
    		if(menu.findItem(MENU_ITEM_LOGOUT) != null){
    			menu.removeItem(103);
    		}
    		settingItem.setTitle(Utils._("signin"));
    	}
    }
	public void loadFeedQuestions(final int feedId) {
		mApplication.getFeed(feedId).setMoreQuestions(true);
		//ShowDialog.showLoadingDialog(HomeActivity.this, OSConfig.MESSAGE_UPDATING);
		ShowDialog.showLoadingDialog(HomeActivity.this, Utils._("updating"));
		final LoadFeedQuestions asynTask = new LoadFeedQuestions(HomeActivity.this, feedId, new LoadingListener() {

            @Override
            public void onError(Object error) {
            	ShowDialog.removeLoadingDialog();
            }

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void onLoadingComplete(List obj) {
				//if(obj != null){
					//Log.d("Load Complete::", obj.toString());
					ShowDialog.removeLoadingDialog();
				
					Feed currentFeed = mApplication.getFeed(feedId);
					currentFeed.setQuestionList(obj);
					currentFeed.load_status = 1;
					Log.d("Fragments::", ""+fm.getFragments().size());
					FeedFragment cf = (FeedFragment) mAdapter.getItem(feedId);
					if(obj != null){
						cf.updateQuestionList(obj);
					}else{
						cf.clearQuestionListView();
					}
					if(obj == null || obj.size() < OSConfig.DEFAULT_LOAD_COUNT)
						mApplication.getFeed(feedId).setMoreQuestions(false);
				//}
					
			}
 
			@Override
			public void onLoadingComplete(Object obj) {
				// TODO Auto-generated method stub
				
			}
        });
		asynTask.execute();
    }
	private void initParse(){
		
		if(mApplication.isPushEnabled())
			return;
		
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		
		User user = mApplication.getUser();
		if(user == null || user.getNotificationByLanguage(mApplication.getLanguage())==1){
			installation.put("language",mApplication.getLanguage());
		}else
			installation.put("language","");
		installation.saveInBackground();
		
		PushService.setDefaultPushCallback(this, CommentActivity.class, R.drawable.notification1);
		ParseAnalytics.trackAppOpened(getIntent());
		mApplication.setPushEnabled(true);
	}
}
