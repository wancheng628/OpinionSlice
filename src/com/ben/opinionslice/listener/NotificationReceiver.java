package com.ben.opinionslice.listener;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.utils.JSONParser;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver{
	private static final String TAG = "NotificationReceiver";
	@SuppressWarnings("rawtypes")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		String channel = intent.getExtras().getString("com.parse.Channel");
		JSONObject json;
		try {
			json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
			Log.d(TAG, "got action" + action + " on channel " + channel + " with:" );
			Iterator itr = json.keys();
			while(itr.hasNext()){
				String key = (String)itr.next();
				Log.d(TAG, "..." + key + " => " + json.getString(key));
			}
			//Question question = JSONParser.getQuestionfromJson(json.getString("question"));
			
			//((OSApplication)((Activity)context).getApplication()).setCurrentQuestion(question);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
