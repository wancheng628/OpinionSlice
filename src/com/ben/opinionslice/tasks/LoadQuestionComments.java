package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Comment;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class LoadQuestionComments extends AsyncTask<Void, Void, List<Comment>> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    private int questionId;
    //private String userId;
    
    public LoadQuestionComments(Activity activity, int questionId, LoadingListener listener) {
        this.activity = activity;
        this.questionId = questionId;
        this.mListener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
    }

    @Override
    protected void onPostExecute(List<Comment> commentList) {
        //ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(commentList);
        }
        super.onPostExecute(commentList);
    }

    @Override
    protected List<Comment> doInBackground(Void... params) {
    	List<Comment> commentList = null;

    	try {
            
            String result = getQuestionComments(questionId);
            Log.d(TAG, "Result: " + result);
            
            commentList = JSONParser.getCommentListfromJson(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting Comments: " + ex.getMessage());

        }
        return commentList;
    }

    private String getQuestionComments(int questionId) {
        String result = null;
        String url = String.format(OSConfig.serviceUrl+"?action=getComments&question_id=%s&language=%s",
        		questionId,
        		((OSApplication) activity.getApplication()).getLanguage());
        
        Log.d(TAG,"Current Language::  "+((OSApplication) activity.getApplication()).getLanguage());
        Log.d(TAG,"Comment URL::  "+url);

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
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
}
