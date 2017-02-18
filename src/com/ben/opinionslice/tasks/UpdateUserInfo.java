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

public class UpdateUserInfo extends AsyncTask<Void, Void, String> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    String name;
    String location;
    String birthday;
    String nationality;
    String gender;
    int notification;
    int notification_fr;
    int vibration;
    
    public UpdateUserInfo(Activity activity, String name, String location, String birthday, String nationality, String gender, int notification, int notification_fr, int vibration, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.location = location;
        this.name = name;
        this.birthday = birthday;
        this.nationality = nationality;
        this.gender = gender;
        this.notification = notification;
        this.notification_fr = notification_fr;
        this.vibration = vibration;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_UPDATING_USERINFO);
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
            
            result = updateUserInfo();
            Log.d("Result", result);
            // = JSONParser.getUserInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in updating user info: " + ex.getMessage());

        }
        return result;
    }

    @SuppressWarnings("deprecation")
	private String updateUserInfo() {
        String result = null;
        
        User user = ((OSApplication) activity.getApplication()).getUser();
        if(user == null || user.getId() < 1){
        	Log.e(TAG, "User doesn't login");
        	return null;
        }
         
        String url = String.format(OSConfig.serviceUrl+"?action=updateUserInfo&user_id=%s&name=%s&location=%s&birthday=%s&nationality=%s&gender=%s&notification=%s&notification_fr=%s&vibration=%s",
        		user.getId(),
        		URLEncoder.encode(name),
        		URLEncoder.encode(location), 
        		URLEncoder.encode(birthday),
        		URLEncoder.encode(nationality),
        		gender,
        		notification,
        		notification_fr,
        		vibration);
        
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
