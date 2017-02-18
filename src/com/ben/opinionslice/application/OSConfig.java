package com.ben.opinionslice.application;

public class OSConfig {
	
	public static final String PACKAGE_NAME = "com.ben.opinionslice";
	//public static final String domainUrl = "http://162.242.225.64";
	//public static final String baseUrl = domainUrl + "/backup123";
	//public static final String domainUrl = "http://192.168.0.46:88";
	//public static final String baseUrl = domainUrl + "/OSApp";
	/*
	public static final String domainUrl = "http://opinionslice.com";
	public static final String baseUrl = domainUrl + "/mobile";
	public static final String serviceUrl = baseUrl + "/services/mobile_service.php";
	*/
	public static final String domainUrl = "http://www.mb.opinionslice.com";
	public static final String baseUrl = domainUrl + "";
	public static final String serviceUrl = baseUrl + "/mobile_service.php";
	
	//public static final String FACEBOOK_ID = "602510403136233";
	public static final String FACEBOOK_ID = "1461186377469393";
	//public static final String FACEBOOK_ID = "935457883146934";
	
	public static final String TRACK_ID="UA-50894856-2"; 
	
	public static int FEED_COUNT = 9;
	public static final String[] FEED_TITLES_EN = {"News", "Politics", "Business", "Tech", "Sport", "Entertainment", "Planet", "Lifestyle", "Health"};
	public static final String[] FEED_TITLES_FR = {"Actualité", "Politique", "Société", "Tech", "Sport", "Culture", "Economie", "Planète", "Santé"};
	public static final String[] FEED_KEYS = {"Breaking News", "Politics", "Business", "Tech", "Sport", "Entertainment", "Green", "Lifestyle", "Health"};
	public static final String[] FEED_LOCATIONS_EN = {"World", "United States", "United States", "World", "United States", "United States", "World", "United States", "World"};
	public static final String[] FEED_LOCATIONS_FR = {"International", "France", "France", "International", "France", "France", "International", "France", "International"};
	
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String[] DEFAULT_LOCATIONS = FEED_LOCATIONS_EN;
	public static int DEFAULT_FEED_INDEX = 0;
	
	public static final int QUESTION_TAG = 7000;
	 
	public static final String MESSAGE_REFRESHING_QUESTIONS = "Refreshing Questions...";
	public static final String MESSAGE_LOADING_QUESTIONS = "Loading Questions...";
	public static final String MESSAGE_LOADING_MORE_QUESTIONS = "Loading More Questions...";
	public static final String MESSAGE_LOADING_MORE_COMMENTS = "Loading More Comments...";
	public static final String MESSAGE_LOADING_COMMENTS = "Loading Comments...";
	public static final String MESSAGE_UPDATING_COMMENT_SCORE = "Updating Comment Score...";
	public static final String MESSAGE_LOADING_COUNTRIES = "Loading Countries...";
	public static final String MESSAGE_LOADING_USERINFO = "Loading User Info...";
	public static final String MESSAGE_LOADING_USERVOTEINFO = "Loading User Vote status...";
	public static final String MESSAGE_SAVING_USERINFO = "Saving your info...";
	public static final String MESSAGE_POSITNG_QUESTION = "Posting...";
	public static final String MESSAGE_VOTNG_QUESTION = "Voting question...";
	public static final String MESSAGE_POSITNG_COMMENT = "Posting comment...";
	public static final String MESSAGE_GETTING_LOCATION = "Getting current location...";
	public static final String MESSAGE_SAVING_LOCATION = "Saving current location...";
	public static final String MESSAGE_LOADING_FEEDLOCATION = "Loading feed location...";
	public static final String MESSAGE_UPDATING_USERINFO = "Updating User Info...";
	public static final String MESSAGE_UPDATING = "Updating";
	public static final String MESSAGE_SEARCHING = "Searching";
	// ALERT MESSAGE
	public static final String ALERT_MESSAGE_LOCATION_REQUIRE = "Location field is mandatory. Please add a location and try again.";
	public static final String ALERT_MESSAGE_FEED_REQUIRE = "Tag field is mandatory. Please select a tag and try again.";
	public static final String ALERT_MESSAGE_EMPTY_QUESTION = "Please write your question.";
	public static final String ALERT_MESSAGE_EMPTY_COMMENT = "Please write your comment.";
	public static final String ALERT_MESSAGE_LOGIN_REQUIRE = "Please login first and try again.";
	public static final String ALERT_MESSAGE_VOTE_REQUIRE = "You must vote before writing your comment.";
	
	//public static final String PARSE_APP_ID = "39TYru61LZguT84bxpO4FijZVLar6g366l76NNjo";
	//public static final String PARSE_CLIENT_KEY = "Nvjf2wo5tT6HwXL5us25vZVM2WfuMWfZDjHuRuzt";
	
	public static final String PARSE_APP_ID = "CQmwWT8iKVzeI6fm6chqc4wWX7mx60JabdUTBPPU";
	public static final String PARSE_CLIENT_KEY = "RWf5s9eWPAaTe9fhlrCR5Rr7vNeqYkzCTqjwLpBh";
	
	public enum OP_STATUS
	{
		OP_STATUS_NORMAL,
		OS_STATUS_LOAD_MORE_QUESTION,
		OS_STATUS_LOAD_QUESTION,
		OP_STATUS_LOAD_COMMENT,
		OS_STATUS_LOAD_MORE_COMMENT,
		OP_STATUS_POST_COMMENT,
		OP_STATUS_UPDATE,
		OP_STATUS_UPDATE_FOLLOW_COUNT
	};
	
	public static final int LOAD_MORE_COUNT = 4;
	public static final int DEFAULT_START_COUNT = 0;
	public static final int DEFAULT_LOAD_COUNT = 6;
}
