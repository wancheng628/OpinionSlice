package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class LoadCountries extends AsyncTask<Void, Void, List<String>> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    public LoadCountries(Activity activity, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_LOADING_COUNTRIES);
    }

    @Override
    protected void onPostExecute(List<String> countryList) {
        ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(countryList);
        }
        super.onPostExecute(countryList);
    }

    @Override
    protected List<String> doInBackground(Void... params) {
    	List<String> countryList = null;
    	
        try {
            
            String result = getCountries();
            countryList = JSONParser.getCountryListfromJson(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting Country list: " + ex.getMessage());

        }
        return countryList;
    }

    private String getCountries() {
        String result = null;
        
        String url = OSConfig.serviceUrl+"?action=getCountries";
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
