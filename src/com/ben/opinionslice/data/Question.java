package com.ben.opinionslice.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class Question {
	private int id;
	private int creator_id;
	private String creator_image;
	private String creator_name;
	private String location;
	private String content;
	private int feed_id;
	private String link;
	private int vote_up;
	private int vote_down;
	private Date createdDate;
	private long duration;
	private String user_vote;
	private List<Comment> commentList;
	private boolean isMoreComments;
	
	public Question(){
		this.setId(0);
		this.setCreatorId(0);
		this.setLocation("");
		this.setContent("");
		this.setFeedId(0);
		this.setLink("");
		this.setVoteUp(0);
		this.setVoteDown(0);
		this.setCreatedDate(null);
		this.setCreatorName("");
		this.setCreatorImage("");
		this.setUserVote("");
		this.setCommentList(new ArrayList<Comment>());
		this.isMoreComments = true;
	}
	public Question(int id, int creator_id, String creator_image, String creator_name, String location, String content, int feed_id, String link, int vote_up, int vote_down, Date createDate, String userVote){
		this.setId(id);
		this.setCreatorId(creator_id);
		this.setCreatorName(creator_name);
		this.setLocation(location);
		this.setContent(content);
		this.setFeedId(feed_id);
		this.setLink(link);
		this.setVoteUp(vote_up);
		this.setVoteDown(vote_down);
		this.setCreatedDate(createDate);
		this.setCreatorImage(creator_image);
		this.setUserVote(userVote);
		this.setCommentList(new ArrayList<Comment>());
		this.isMoreComments = true;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCreatorId() {
		return creator_id;
	}
	public void setCreatorId(int creator_id) {
		this.creator_id = creator_id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getFeedId() {
		return feed_id;
	}
	public void setFeedId(int feed_id) {
		this.feed_id = feed_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getVoteUp() {
		return vote_up;
	}
	public void setVoteUp(int vote_up) {
		this.vote_up = vote_up;
	}
	public int getVoteDown() {
		return vote_down;
	}
	public void setVoteDown(int vote_down) {
		this.vote_down = vote_down;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date created_date) {
		this.createdDate = created_date;
	}
	public String getCreatorName() {
		return creator_name;
	}
	public void setCreatorName(String creator_name) {
		this.creator_name = creator_name;
	}
	public String getCreatorImage() {
		return creator_image;
	}
	public void setCreatorImage(String creator_image) {
		this.creator_image = creator_image;
	}
	public String getUserVote() {
		return user_vote;
	}
	public void setUserVote(String user_vote) {
		this.user_vote = user_vote;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public List<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	public int getCommentCount(){
		if(this.commentList == null)
			return 0;
		return this.commentList.size();
	}
	public void appendComments(List<Comment> comments){
		Log.d("Comment Count", this.commentList.size() + "");
		this.commentList.addAll(comments);
		Log.d("Comment Count1", this.commentList.size() + "");
	}
	public boolean isMoreComments() {
		return isMoreComments;
	}
	public void setMoreComments(boolean isMoreComments) {
		this.isMoreComments = isMoreComments;
	}
}
