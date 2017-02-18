package com.ben.opinionslice.data;

import java.util.ArrayList;
import java.util.List;

import com.ben.opinionslice.application.OSConfig;

public class Feed {
	
	private int index;
	private String key;
	private String title_en;
	private String title_fr;
	private String location_en;
	private String location_fr;
	private String language;
	private List<Question> questionList;
	public int load_status;
	private boolean isMoreQuestions;
	private int currentQuestionIndex;
	
	public Feed(){
		index = OSConfig.DEFAULT_FEED_INDEX; 
		key = OSConfig.FEED_KEYS[index];
		title_en = OSConfig.FEED_TITLES_EN[index];
		title_fr = OSConfig.FEED_TITLES_FR[index];
		location_en = OSConfig.FEED_LOCATIONS_EN[index];
		location_fr = OSConfig.FEED_LOCATIONS_FR[index];
		language = OSConfig.DEFAULT_LANGUAGE;
		questionList = new ArrayList<Question>();
		isMoreQuestions = true;
		load_status = 0;
		currentQuestionIndex = -1;
	}
	public Feed(int index, String title_en, String title_fr, String location_en, String location_fr, String key, String language){
		setIndex(index);
		setKey(key);
		setLanguage("en");
		
		setEnglishTitle(title_en);
		setFrenchTitle(title_fr);
		setLocationEn(location_en);
		setLocationFr(location_fr);
		setLanguage(language);
		questionList = new ArrayList<Question>();
		isMoreQuestions = true;
		load_status = 0;
	}
	public String getTitle() {
		if(language.equalsIgnoreCase("fr"))
			return getFrenchTitle();
		else
			return getEnglishTitle();
	}
	public void setTitle(String title) {
		if(language.equalsIgnoreCase("fr"))
			setFrenchTitle(title);
		else
			setEnglishTitle(title);
	}
	public String getLocation() {
		if(language.equalsIgnoreCase("fr"))
			return getLocationFr();
		else
			return getLocationEn();
	}
	public void setLocation(String location) {
		if(language.equalsIgnoreCase("fr"))
			setLocationFr(location);
		else
			setLocationEn(location);
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
	// Language specific functions
	private String getEnglishTitle() {
		return title_en;
	}
	private void setEnglishTitle(String title_en) {
		this.title_en = title_en;
	}
	private String getFrenchTitle() {
		return title_fr;
	}
	private void setFrenchTitle(String title_fr) {
		this.title_fr = title_fr;
	}
	
	private String getLocationEn() {
		return location_en;
	}
	private void setLocationEn(String location_en) {
		this.location_en = location_en;
	}
	private String getLocationFr() {
		return location_fr;
	}
	private void setLocationFr(String location_fr) {
		this.location_fr = location_fr;
	}
	public List<Question> getQuestionList() {
		if(questionList != null)
			return questionList;
		return new ArrayList<Question>();
	}
	public void setQuestionList(List<Question> questionList) {
		this.questionList = questionList;
	}
	public void removeQustionList(){
		if(this.questionList != null)
			this.questionList.clear();
	}
	public void appendQuestions(List<Question> questions){
		this.questionList.addAll(questions);
	}
	public int getQuestionCount() {
		return this.questionList.size();
	}
	public boolean isMoreQuestions() {
		return isMoreQuestions;
	}
	public void setMoreQuestions(boolean isMoreQuestions) {
		this.isMoreQuestions = isMoreQuestions;
	}
	public int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}
	public void setCurrentQuestionIndex(int currentQuestionIndex) {
		this.currentQuestionIndex = currentQuestionIndex;
	}
}
