package com.ben.opinionslice.tasks;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.application.OSConfig.OP_STATUS;
import com.ben.opinionslice.data.Comment;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.utils.HttpManager;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class LoadMoreComments extends AsyncTask<Void, Void, List<Comment>> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    private Question question;
    
    public LoadMoreComments(Activity activity, Question question, LoadingListener listener) {
    	
    	this.activity = activity;
        this.question = question;
        this.mListener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_LOADING_MORE_COMMENTS);
        ((OSApplication)activity.getApplication()).setCurrentStatus(OP_STATUS.OS_STATUS_LOAD_MORE_COMMENT);
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
            
            String result = getMoreComments();
            Log.d(TAG, "Result: " + result);
            
            commentList = JSONParser.getCommentListfromJson(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting more comments: " + ex.getMessage());

        }
        return commentList;
    }

    private String getMoreComments() {
        String result = null;
        String url = String.format(OSConfig.serviceUrl+"?action=getComments&question_id=%s&language=%s&start=%s&limit=%s",
        		question.getId(),
        		((OSApplication) activity.getApplication()).getLanguage(),
        		question.getCommentCount(),
        		OSConfig.LOAD_MORE_COUNT);
        
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
}
