package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Feed;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class SaveQuestion extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    String location;
    String link;
    int feedId;
    String content;
    boolean is_anonymous;
    
    public SaveQuestion(Activity activity, int feedId, String content, String link, String location, boolean is_anonymous, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.location = location;
        this.content = content;
        this.link = link;
        this.feedId = feedId;
        this.is_anonymous = is_anonymous;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ShowDialog.showLoadingDialog(activity, Utils._("posting"));
    }

    @Override
    protected void onPostExecute(String result) {
        ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(result);
        }
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(Void... params) {
    	String result = "";
    	//String status;
        try {
            
            result = saveQuestion();
            Log.d("Result", result);
            // = JSONParser.getUserInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting Question: " + ex.getMessage());

        }
        return result;
    }

    @SuppressWarnings("deprecation")
	private String saveQuestion() {
        String result = null;
        
        Feed currentFeed = ((OSApplication) activity.getApplication()).getFeed(feedId);
        User user = ((OSApplication) activity.getApplication()).getUser();
        String language = ((OSApplication) activity.getApplication()).getLanguage();
        String userId = "0";
        String userName = Utils._("anonymous1");
        if(!is_anonymous && user != null){
        	userId = user.getId() + "";
        	userName = user.getUserName();
        }
         
        String url = String.format(OSConfig.serviceUrl+"?action=postQuestion&question=%s&tag=%s&location=%s&link=%s&language=%s&user_name=%s&user_id=%s",
        		URLEncoder.encode(content),
        		URLEncoder.encode(currentFeed.getKey()), 
        		URLEncoder.encode(location),
        		URLEncoder.encode(link),
        		language,
        		URLEncoder.encode(userName),
        		userId);
        
        Log.d(TAG, url);
        
	    try {
	        result = (String) HttpManager.getResponse(url, false);
	    } catch (NullPointerException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	
	    return result;
	}
}
