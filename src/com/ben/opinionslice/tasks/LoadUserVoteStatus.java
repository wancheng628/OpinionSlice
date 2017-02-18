package com.ben.opinionslice.tasks;
import java.io.IOException;
import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class LoadUserVoteStatus extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    int user_id;
    int question_id;
    
    public LoadUserVoteStatus(Activity activity, int user_id, int question_id, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.user_id = user_id;
        this.question_id = question_id;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_LOADING_USERVOTEINFO);
    }

    @Override
    protected void onPostExecute(String vote_status) {
        //ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(vote_status);
        }
        super.onPostExecute(vote_status);
    }

    @Override
    protected String doInBackground(Void... params) {
    	String vote_status = "";
    	
        try {
            
            String result = getUserVoteInfo();
            Log.d("Result", result);
            vote_status = JSONParser.getUserVoteInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting User Vote Status: " + ex.getMessage());

        }
        return vote_status;
    }

	private String getUserVoteInfo() {
        String result = null;
        
        String url = String.format(OSConfig.serviceUrl+"?action=getUserVote&user_id=%s&question_id=%s&language=%s",
        		user_id,
        		question_id,
        		((OSApplication) activity.getApplication()).getLanguage());
        
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
