package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class SaveFeedLocation extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    String tag;
    String location;
    
    public SaveFeedLocation(Activity activity, String tag, String location, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        this.tag = tag;
        this.location = location;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_SAVING_LOCATION);
    }

    @Override
    protected void onPostExecute(String result) {
        //ShowDialog.removeLoadingDialog();
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
            
            result = storeCurrentLocation();
            Log.d("Result", result);
            // = JSONParser.getUserInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in storing current location: " + ex.getMessage());

        }
        return result;
    }

    @SuppressWarnings("deprecation")
	private String storeCurrentLocation() {
        String result = null;
        
        String language = ((OSApplication) activity.getApplication()).getLanguage();
        User user = ((OSApplication) activity.getApplication()).getUser();
        
        String userId = "";
        if(user != null){
        	userId = user.getId() + "";
        }
         
        String url = String.format(OSConfig.serviceUrl+"?action=saveLocation&tag=%s&location=%s&language=%s&user_id=%s",
        		URLEncoder.encode(tag),
        		URLEncoder.encode(location),
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
