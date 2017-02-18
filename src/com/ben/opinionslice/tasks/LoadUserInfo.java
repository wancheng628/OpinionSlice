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

public class LoadUserInfo extends AsyncTask<Void, Void, User> {

	private final String TAG = "OSApp";
    private LoadingListener mListener;
    //private OSApplication mApplication;	
    private Activity activity;
    
    String fbId;
    String userName;
    String email;
    String location;
    String birthday;
    String gender;
    String avatar;
    
    public LoadUserInfo(Activity activity, String fbId, String name, String email, String location, String birthday, String gender, String profile_picture, LoadingListener listener) {
    	
    	this.activity = activity;
        this.mListener = listener;
        
        this.fbId = fbId;
        this.userName = name;
        this.email = email;
        this.birthday = birthday;
        this.location = location;
        this.avatar = profile_picture;
        this.gender = gender;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ShowDialog.showLoadingDialog(activity, OSConfig.MESSAGE_LOADING_USERINFO);
    }

    @Override
    protected void onPostExecute(User userInfo) {
        //ShowDialog.removeLoadingDialog();
        if (mListener != null) {
            mListener.onLoadingComplete(userInfo);
        }
        super.onPostExecute(userInfo);
    }

    @Override
    protected User doInBackground(Void... params) {
    	User userInfo = null;
    	
        try {
            
            String result = getUserInfo();
            Log.d("Result", result);
            userInfo = JSONParser.getUserInfoFromJSON(result);

        } catch (Exception ex) {
            Log.e(TAG, "Exception in getting UserInfo: " + ex.getMessage());

        }
        return userInfo;
    }

    @SuppressWarnings("deprecation")
	private String getUserInfo() {
        String result = null;
        
        String url = OSConfig.serviceUrl+"?action=getUserInfo";
        url += "&username=" + URLEncoder.encode(userName);
        url += "&name=" + URLEncoder.encode(userName);
        url += "&email=" + URLEncoder.encode(email);
        url += "&location=" + ((location == null) ? "" : URLEncoder.encode(location));
        url += "&birthday=" + ((birthday == null) ? "" : URLEncoder.encode(birthday));
        url += "&gender=" + gender;
        url += "&avatar=" + URLEncoder.encode(avatar);
        url += "&fbId=" + fbId;
        
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
