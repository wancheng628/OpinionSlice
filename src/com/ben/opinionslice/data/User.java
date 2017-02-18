package com.ben.opinionslice.data;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private int id;
	private String userName;
	private String fullName;
	private String email;
	private String password;
	private String avatar;
	private String gender;
	private String location;
	private String nationality;
	private Date birthday;
	private Date createdDate;
	private Date modifiedDate;
	private int notification;
	private int notification_fr;
	private int vibration;
	
	public User(){
		id = 0;
		userName = "";
		fullName = "";
		email = "";
		password = "";
		avatar = "";
		gender = "";
		location = "";
		nationality = "";
		birthday = null;
		notification=1;
		notification_fr=1;
		vibration=1;
		createdDate = null;
		modifiedDate = null;
	}
	public String toString(){
		JSONObject obj = new JSONObject();
		SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");  
		try {
			obj.put("id", id);
			obj.put("name", userName);
			//obj.put("fullName", fullName);
			obj.put("email", email);
			obj.put("password", password);
			obj.put("avatar", avatar);
			obj.put("gender", gender);
			obj.put("location", location);
			obj.put("nationality", nationality);
			if(birthday != null)
				obj.put("birthday", format.format(birthday));
			obj.put("notification", notification);
			obj.put("notification_fr", notification_fr);
			obj.put("vibration", vibration);
			//obj.put("createdDate", createdDate);
			//obj.put("modifiedDate", modifiedDate);
			
			return obj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	public static User parseUser(String str){
		if(str.isEmpty())
			return null;
		try{
			JSONObject jUser = new JSONObject(str);
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
			if(jUser.has("birthday") && !jUser.getString("birthday").isEmpty())
				user.setBirthday(format.parse(jUser.getString("birthday")));
			
			return user;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public User(int id, String userName, String fullName, String email, String password, String avatar, String gender, String location, String nationality, Date birthday, int notification, int notification_fr, int vibration, Date createdDate, Date modifiedDate){
		this.id = id;
		this.userName = userName;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.avatar = avatar;
		this.gender = gender;
		this.location = location;
		this.nationality = nationality;
		this.birthday = birthday;
		this.notification = notification;
		this.notification_fr = notification_fr;
		this.vibration = vibration;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date string) {
		this.birthday = string;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date created_date) {
		this.createdDate = created_date;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modified_date) {
		this.modifiedDate = modified_date;
	}
	@SuppressLint("SimpleDateFormat")
	public String getBirthdayMonth(){
		if(this.birthday == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		return sdf.format(this.birthday);
	}
	@SuppressLint("SimpleDateFormat")
	public String getBirthdayDate(){
		if(this.birthday == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return sdf.format(this.birthday);
	}
	@SuppressLint("SimpleDateFormat")
	public String getBirthdayYear(){
		if(this.birthday == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(this.birthday);
	}
	public int getNotification() {
		return notification;
	}
	public void setNotification(int notification) {
		this.notification = notification;
	}
	public int getNotificationFr() {
		return notification_fr;
	}
	public void setNotificationFr(int notification_fr) {
		this.notification_fr = notification_fr;
	}
	public int getNotificationByLanguage(String language){
		if(language.equalsIgnoreCase("en"))
			return getNotification();
		else
			return getNotificationFr();
	}
	public int getVibration() {
		return vibration;
	}
	public void setVibration(int vibration) {
		this.vibration = vibration;
	}
}
