package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class LoadFeedLocation extends AsyncTask<Void, Void, List<String>> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;

    public LoadFeedLocation(Activity activity, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_LOADING_FEEDLOCATION);
    }

    @Override
    protected void onPostExecute(List<String> location) {
        //ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(location);
        }
        super.onPostExecute(location);
    }

    @Override
    protected List<String> doInBackground(Void... params) {
    	List<String> location = new ArrayList<String>();
    	
        try {
            String result = getLocation();
            Log.d("Result", result);
            location = JSONParser.getFeedLocationsFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting feed location: " + ex.getMessage());

        }
        return location;
    }

    @SuppressWarnings("deprecation")
	private String getLocation() {
        String result = null;
        
        String language = ((OSApplication) activity.getApplication()).getLanguage();
        User user = ((OSApplication) activity.getApplication()).getUser();
        
        String userId = "";
        if(user != null){
        	userId = user.getId() + "";
        }
        
        String url = String.format(OSConfig.serviceUrl+"?action=getLocation&language=%s&user_id=%s",
        		language,
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
