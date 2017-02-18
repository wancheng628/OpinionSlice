package com.ben.opinionslice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Comment;
import com.ben.opinionslice.data.Countries;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;

public class JSONParser {
	
	public static List<Question> getQuestionListfromJson(String result){
		List<Question> questionList = new ArrayList<Question>();
		try {
			JSONObject jsonObj = new JSONObject(result);
			
			if(jsonObj.get("result") == null || !(jsonObj.get("result") instanceof JSONArray))
				return null;
			
			JSONArray jQuestions = jsonObj.getJSONArray("result");
			if(jQuestions != null && jQuestions.length() > 0){
				for(int i=0; i<jQuestions.length(); i++){
					JSONObject jQuestion = jQuestions.getJSONObject(i);
					Question q = new Question();
					q.setId(jQuestion.getInt("id"));
					q.setCreatorId(Integer.parseInt(jQuestion.getString("user_id")));
					//Log.d("UserName", jQuestion.getString("user_name"));
					if(jQuestion.get("user_name") != null && !jQuestion.getString("user_name").equalsIgnoreCase("null"))
						q.setCreatorName(jQuestion.getString("user_name"));
					if(jQuestion.get("user_avatar") != null && !jQuestion.getString("user_avatar").equalsIgnoreCase("null"))
						q.setCreatorImage(jQuestion.getString("user_avatar"));
					q.setLocation(jQuestion.getString("location"));
					q.setContent(jQuestion.getString("question"));
					//q.setFeedId(jQuestion.getInt("tags"));
					q.setLink(jQuestion.getString("link"));
					q.setVoteUp(jQuestion.getInt("up"));
					q.setVoteDown(jQuestion.getInt("down"));
					q.setUserVote((jQuestion.getString("vote")==null) ? "":jQuestion.getString("vote"));
					q.setDuration(jQuestion.getLong("duration"));
					
					//q.setCreatedDate(new Date(jQuestion.getString("created")));
					//q.set
					
					questionList.add(q);
				}
				return questionList;
			}
			return null;
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Question getQuestionfromJson(String result){
		
		try {
			JSONObject jsonObj = new JSONObject(result);
			JSONObject jQuestion = jsonObj.getJSONObject("result");
			if(jQuestion != null){
				Question q = new Question();
				q.setId(jQuestion.getInt("id"));
				q.setCreatorId(Integer.parseInt(jQuestion.getString("user_id")));
				Log.d("UserName", jQuestion.getString("user_name"));
				if(jQuestion.get("user_name") != null && !jQuestion.getString("user_name").equalsIgnoreCase("null"))
					q.setCreatorName(jQuestion.getString("user_name"));
				if(jQuestion.get("user_avatar") != null && !jQuestion.getString("user_avatar").equalsIgnoreCase("null"))
					q.setCreatorImage(jQuestion.getString("user_avatar"));
				q.setLocation(jQuestion.getString("location"));
				q.setContent(jQuestion.getString("question"));
				//q.setFeedId(jQuestion.getInt("tags"));
				q.setLink(jQuestion.getString("link"));
				q.setVoteUp(jQuestion.getInt("up"));
				q.setVoteDown(jQuestion.getInt("down"));
				q.setUserVote((jQuestion.getString("vote")==null) ? "":jQuestion.getString("vote"));
				q.setDuration(jQuestion.getLong("duration"));
				
				return q;
			}
			return null;
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<Comment> getCommentListfromJson(String result){
		List<Comment> commentList = new ArrayList<Comment>();
		try {
			JSONObject jsonObj = new JSONObject(result);
			if(jsonObj.get("result") == null || !(jsonObj.get("result") instanceof JSONArray))
				return null;
			JSONArray jComments = jsonObj.getJSONArray("result");
			
			if(jComments != null && jComments.length() > 0){
				for(int i=0; i<jComments.length(); i++){
					JSONObject jComment = jComments.getJSONObject(i);
					Comment c = new Comment();
					
					c.setId(jComment.getInt("id"));
					c.setCreatorId(Integer.parseInt(jComment.getString("userid")));
					c.setCreatorName(jComment.getString("user_name"));
					c.setQuestionId(Integer.parseInt(jComment.getString("ques_id")));
					c.setContent(jComment.getString("comment"));
					c.setType(jComment.getString("type"));
					c.setScore(jComment.getInt("score"));
					//c.setCreatedDate(jComment.getString("created"));
					//c.setModifiedDate(jComment.getString("modified"));
					commentList.add(c);
				}
				return commentList;
			}
			return null;
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<String> getCountryListfromJson(String result){
		try {
			JSONObject jsonObj = new JSONObject(result);
			if(jsonObj.get("result") == null || !(jsonObj.get("result") instanceof JSONArray))
				return null;
			JSONArray jCountries = jsonObj.getJSONArray("result");
			
			if(jCountries != null && jCountries.length() > 0){
				for(int i=0; i<jCountries.length(); i++){
					JSONObject jCountry = jCountries.getJSONObject(i);
					Countries country = new Countries();
					
					String code = jCountry.getString("country_code");
					String name = jCountry.getString("Country");
					Countries.addCountry(code, name);
				}
				return Countries.getAllCountryNames();
			}
			return null;
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@SuppressLint("SimpleDateFormat")
	public static User getUserInfoFromJSON(String result){
		try {
			JSONObject jsonObj = new JSONObject(result);
			if(jsonObj.get("result") == null)
				return null;
			JSONObject jUser = jsonObj.getJSONObject("result");
			
			User user = new User();
			user.setId(Integer.parseInt(jUser.getString("id")));
			if(jUser.get("name") != null){
				user.setUserName(jUser.getString("name"));
				user.setFullName(jUser.getString("name"));
			}
			user.setEmail(jUser.getString("email"));
			user.setPassword(jUser.getString("password"));
			if(jUser.get("avatar") != null)
				user.setAvatar(jUser.getString("avatar"));
			user.setGender(jUser.getString("gender"));
			user.setLocation(jUser.getString("location"));
			user.setNationality(jUser.getString("nationality"));
			user.setNotification(jUser.getInt("notification"));
			user.setNotificationFr(jUser.getInt("notification_fr"));
			SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");  
			if(!jUser.getString("birthday").isEmpty())
				user.setBirthday(format.parse(jUser.getString("birthday")));
			
			return user;

	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String getLocationFromJSON(String result){
		
		JSONObject jsonObj;
		String address = "";
		String status = "";
		
		try {
			jsonObj = new JSONObject(result);
			status = jsonObj.getString("status");
	        if (status.equalsIgnoreCase("OK")) {
	            JSONArray Results = jsonObj.getJSONArray("results");
	            String type = "";
	            for(int i=0; i<Results.length(); i++){
	            	type = Results.getJSONObject(i).getJSONArray("types").getString(0);
	            	if(!type.isEmpty() && type.equalsIgnoreCase("locality")){
	            		address = Results.getJSONObject(i).getString("formatted_address");
	            		return address;
	            	}
	            }
	            return address;
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    }
	public static List<String> getFeedLocationsFromJSON(String result){
		List<String> locationList = new ArrayList<String>(9);
		for(int i=0; i<OSConfig.FEED_COUNT; i++)
			locationList.add("");
		try {
			JSONObject jsonObj = new JSONObject(result);
			if(jsonObj.get("result") == null)
				return locationList;
			JSONArray jLocations = jsonObj.getJSONObject("result").getJSONArray("location");
			for(int i = 0; i < jLocations.length(); i++){
				JSONObject jLocation = jLocations.getJSONObject(i);
				String tag = jLocation.getString("tag");
				String location = jLocation.getString("location");
				for(int j=0; j<OSConfig.FEED_KEYS.length; j++){
					if(tag.equalsIgnoreCase(OSConfig.FEED_KEYS[j])){
						locationList.set(j, location);
						break;
					}
				}
			}
			return locationList;

	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return locationList;
    }
	public static String getUserVoteInfoFromJSON(String result){
		try {
			JSONObject jsonObj = new JSONObject(result);
			if(jsonObj.get("result") == null)
				return "";
			JSONObject jVote = jsonObj.getJSONObject("result");
			return jVote.getString("user_vote");

	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
