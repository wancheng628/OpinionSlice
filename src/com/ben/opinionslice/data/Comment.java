package com.ben.opinionslice.data;

import java.util.Date;

public class Comment {
	private int id;
	private int creator_id;
	private String creator_name;
	private int question_id;
	private String content;
	private String type;
	private Date createdDate;
	private Date modifiedDate;
	private int score;
	public Comment(){
		id = 0;
		creator_id = 0;
		creator_name = "";
		question_id = 0;
		content = "";
		type = "";
		createdDate = null;
		modifiedDate = null;
		score = 0;
	}
	public Comment(int id, int creator_id, String creator_name, int question_id, String content, String type, Date createdDate, Date modifiedDate, int score){
		this.id = id;
		this.creator_id = creator_id;
		this.creator_name = creator_name;
		this.question_id = question_id;
		this.content = content;
		this.type = type;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.score = score;
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
	public int getQuestionId() {
		return question_id;
	}
	public void setQuestionId(int question_id) {
		this.question_id = question_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getCreatorName() {
		return creator_name;
	}
	public void setCreatorName(String creator_name) {
		this.creator_name = creator_name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public void increaseScore(){
		this.score++;
	}
	public void decreaseScore(){
		this.score--;
	}
}
