package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.net.URLEncoder;
import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class GetCurrentLocation extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    double lat;
    double lng;
    
    public GetCurrentLocation(Activity activity, double lat, double lng, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.lat = lat;
        this.lng = lng;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_GETTING_LOCATION);
    }

    @Override
    protected void onPostExecute(String location) {
        ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(location);
        }
        super.onPostExecute(location);
    }

    @Override
    protected String doInBackground(Void... params) {
    	String location = "";
    	
        try {
            
            String result = getLocation();
            Log.d("Result", result);
            location = JSONParser.getLocationFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting location: " + ex.getMessage());

        }
        return location;
    }

	private String getLocation() {
        String result = null;
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false";
        
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
