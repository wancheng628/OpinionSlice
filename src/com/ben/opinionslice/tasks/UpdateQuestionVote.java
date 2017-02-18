package com.ben.opinionslice.tasks;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateQuestionVote extends AsyncTask<Void, Void, Question> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    int user_id;
    int question_id;
    String vote;
    
    public UpdateQuestionVote(Activity activity, int user_id, int question_id, String vote, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.user_id = user_id;
        this.question_id = question_id;
        this.vote = vote;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_VOTNG_QUESTION);
    }

    @Override
    protected void onPostExecute(Question result) {
        //ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(result);
        }
        super.onPostExecute(result);
    }

    @Override
    protected Question doInBackground(Void... params) {
    	String result = "";
    	Question question = null;
    	//String status;
        try {
            
            result = updateQuestionVote();
            Log.d("Result", result);
            if(result != null)
            	question = JSONParser.getQuestionfromJson(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting Question: " + ex.getMessage());

        }
        return question;
    }
	private String updateQuestionVote() {
        String result = null;
        
        String url = String.format(OSConfig.serviceUrl+"?action=userVote&user_id=%s&question_id=%s&vote=%s&language=%s",
        		user_id,
        		question_id, 
        		vote,
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
