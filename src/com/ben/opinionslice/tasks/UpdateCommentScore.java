package com.ben.opinionslice.tasks;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.application.OSConfig.OP_STATUS;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateCommentScore extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    int comment_id;
    int follow;
    
    public UpdateCommentScore(Activity activity, int comment_id, int follow, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.comment_id = comment_id;
        this.follow = follow;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_UPDATING_COMMENT_SCORE);
        ((OSApplication)activity.getApplication()).setCurrentStatus(OP_STATUS.OP_STATUS_UPDATE_FOLLOW_COUNT);
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
            
            result = updateCommentScore();
            Log.d("Result", result);
            // = JSONParser.getUserInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in updating comment score: " + ex.getMessage());

        }
        return result;
    }
	private String updateCommentScore() {
        String result = null;
        
        String url = String.format(OSConfig.serviceUrl+"?action=updateCommentScore&comment_id=%s&follow=%s&language=%s",
        		comment_id,
        		follow, 
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
