package com.ben.opinionslice.tasks;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class LoadSingleQuestion extends AsyncTask<Void, Void, Question> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    private int questionId;
    
    public LoadSingleQuestion(Activity activity, int questionId, LoadingListener listener) {
    	
    	this.activity = activity;
        this.questionId = questionId;
        this.mListener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
    }

    @Override
    protected void onPostExecute(Question questionList) {
        if (mListener != null) {
            mListener.onLoadingComplete(questionList);
        }
        super.onPostExecute(questionList);
    }

    @Override
    protected Question doInBackground(Void... params) {
    	Question question = null;
    	
        try {
            
            String result = getSingleQuestion(questionId);
            Log.d(TAG, "Result: " + result);
            if(result != null)
            	question = JSONParser.getQuestionfromJson(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting Question: " + ex.getMessage());

        }
        return question;
    }
	private String getSingleQuestion(int questionId) {
        String result = null;
        String userId = "";
        User user = ((OSApplication) activity.getApplication()).getUser();
        if(user != null)
        	userId = user.getId() + "";
        String url = String.format(OSConfig.serviceUrl+"?action=getQuestion&questionId=%s&user_id=%s&language=%s",
        		questionId,
        		userId,
        		((OSApplication) activity.getApplication()).getLanguage());
        Log.d("Question URL", url);
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
