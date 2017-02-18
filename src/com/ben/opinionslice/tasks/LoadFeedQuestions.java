package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Feed;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class LoadFeedQuestions extends AsyncTask<Void, Void, List<Question>> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    private int feedIndex;
    
    public LoadFeedQuestions(Activity activity, int feedIndex, LoadingListener listener) {
    	
    	this.activity = activity;
        this.feedIndex = feedIndex;
        this.mListener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
    }

    @Override
    protected void onPostExecute(List<Question> questionList) {
        if (mListener != null) {
            mListener.onLoadingComplete(questionList);
        }
        super.onPostExecute(questionList);
    }

    @Override
    protected List<Question> doInBackground(Void... params) {
    	List<Question> questionList = null;
    	
        try {
            
            String result = getFeedQuestions(feedIndex);
            //Log.d(TAG, "Result: " + result);
            
            questionList = JSONParser.getQuestionListfromJson(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting QuesWithAns: " + ex.getMessage());

        }
        return questionList;
    }

    @SuppressWarnings("deprecation")
	private String getFeedQuestions(int feedIndex) {
        String result = null;
        
        Feed currentFeed = ((OSApplication) activity.getApplication()).getFeed(feedIndex);
        
        User user = ((OSApplication) activity.getApplication()).getUser();
        String userId = "";
        if(user != null)
        	userId = user.getId() + "";
        
        String url = String.format(OSConfig.serviceUrl+"?action=getQuestions&tag=%s&location=%s&language=%s&start=%s&limit=%s&user_id=%s",
        		URLEncoder.encode(currentFeed.getKey()), 
        		URLEncoder.encode(currentFeed.getLocation()), 
        		 ((OSApplication) activity.getApplication()).getLanguage(), 
        		 OSConfig.DEFAULT_START_COUNT,
        		OSConfig.DEFAULT_LOAD_COUNT,
        		userId);
        //Log.d(TAG,"Current Location::  "+currentFeed.getLocation());
        //Log.d(TAG,"Current Language::  "+currentFeed.getLanguage());
        Log.d(TAG,"FeedQuestions URL::  "+url);

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
