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
import com.ben.opinionslice.utils.Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class SaveComment extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    String content;
    int question_id;
    String vote_type;
    
    public SaveComment(Activity activity, String content, int question_id, String vote_type, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.content = content;
        this.question_id = question_id;
        this.vote_type = vote_type;
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
            
            result = saveComment();
            Log.d("Result", result);
            // = JSONParser.getUserInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in posting Comment: " + ex.getMessage());

        }
        return result;
    }

    @SuppressWarnings("deprecation")
	private String saveComment() {
        String result = null;
        
        String language = ((OSApplication) activity.getApplication()).getLanguage();
        User user = ((OSApplication) activity.getApplication()).getUser();
        
        String userId = "";
        if(user != null){
        	userId = user.getId() + "";
        }
         
        String url = String.format(OSConfig.serviceUrl+"?action=postComments&comment=%s&question_id=%s&type=%s&language=%s&user_id=%s",
        		URLEncoder.encode(content),
        		question_id, 
        		URLEncoder.encode(vote_type),
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
