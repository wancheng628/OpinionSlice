package com.ben.opinionslice.listener;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ben.opinionslice.CommentActivity;
import com.ben.opinionslice.R;
import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.data.Comment;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.interfaces.ContentRefreshInterface;
import com.ben.opinionslice.listener.QuestionListAdapter.QuestionViewHolder;
import com.ben.opinionslice.utils.Utils;

public class CommentListAdapter extends ArrayAdapter<Comment> implements OnClickListener{

	private List<Comment> commentList;
	private int layoutResourceId;
	private Context context;
	private CommentActivity activity;
	private OSApplication mApplication;
	private LayoutInflater mInflater;
	Question question;
	
	Button btnAddComment;
	LinearLayout vote_no;
	LinearLayout vote_yes;
	TextView qVoteUpView;
	TextView qVoteDownView;
	TextView qCreatorView;
	TextView qContentView;
	TextView qLinkView;
	TextView qShareCountView;
	TextView qDurationView;
	TextView txtMyVoice;
	TextView txtVoteYes;
	TextView txtVoteNo;
	TextView txtOpinionShared;
	TextView txtAsked;
	EditText txtComment;
	LinearLayout layout_write_comment;
	
	Button btnFollowComment;
	Button btnUnfollowComment;
	
	
	TextView cCreatorView;
	TextView cOpinionView;
	TextView cWithyouView;
	TextView cContentView;
	TextView cFollowCountView;
	ContentRefreshInterface refreshInterface;
	Resources resource;
	Typeface tf_bold;
	Typeface tf_light;
	Typeface tf_regular;
	public CommentListAdapter(Context context, int questionItemResourceId, List<Comment> commentList) {
		super(context, questionItemResourceId, commentList);
		// TODO Auto-generated constructor stub
		this.layoutResourceId = questionItemResourceId;
		this.context = context;
		this.commentList = commentList;
		resource = context.getResources();
		activity = (CommentActivity)context;
		mApplication = (OSApplication)activity.getApplication();
		//this.refreshInterface = refreshInterface;
		mInflater = ((Activity) context).getLayoutInflater();
		tf_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
		tf_light = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		tf_regular = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
		
		question = mApplication.getCurrentQuestion();
			
	}
	@Override
	public int getCount(){
		if(commentList == null)
			return 1;
		else
			return commentList.size() + 1;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		View row1 = convertView;
		Log.d("Position", position + "");
		
		if(position == 0){
			row1 = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_comment_header, parent, false);
		
			qCreatorView = (TextView)row1.findViewById(R.id.txt_question_detail_creator);
			qContentView = (TextView)row1.findViewById(R.id.txt_question_detail_content);
			qLinkView = (TextView)row1.findViewById(R.id.txt_question_detail_link);
			qShareCountView = (TextView)row1.findViewById(R.id.txt_question_detail_vote_count);
			qVoteUpView = (TextView)row1.findViewById(R.id.txt_question_detail_vote_yes_percent);
			qVoteDownView = (TextView)row1.findViewById(R.id.txt_question_detail_vote_no_percent);
			qDurationView = (TextView)row1.findViewById(R.id.txt_question_detail_duration);
			vote_no = (LinearLayout)row1.findViewById(R.id.layer_question_detail_vote_no);
			vote_yes = (LinearLayout)row1.findViewById(R.id.layer_question_detail_vote_yes);
			txtComment = (EditText)row1.findViewById(R.id.edit_write_comment1);
			txtMyVoice = (TextView)row1.findViewById(R.id.txt_myvoice);
			txtVoteYes = (TextView)row1.findViewById(R.id.txt_vote_yes_text);
	        txtVoteNo = (TextView)row1.findViewById(R.id.txt_vote_no_text);
	        txtAsked = (TextView)row1.findViewById(R.id.txt_asked);
	        txtOpinionShared = (TextView)row1.findViewById(R.id.txt_opinion_shared1);
			layout_write_comment = (LinearLayout)row1.findViewById(R.id.layout_write_comment);
			
			btnAddComment = (Button)row1.findViewById(R.id.btn_add_comment);
			btnAddComment.setOnClickListener(this);
			
			qContentView.setTypeface(tf_light);
			qLinkView.setTypeface(tf_light);
			qCreatorView.setTypeface(tf_bold);
			
			txtVoteYes.setText(Utils._("yes"));
			txtVoteNo.setText(Utils._("no"));
			txtMyVoice.setText(Utils._("my_voice"));
			txtOpinionShared.setText(Utils._("opinion_shared"));
			txtAsked.setText(Utils._("asked"));
			refreshData();
			
			return row1;
		}else{
			
			row = ((Activity) context).getLayoutInflater().inflate(layoutResourceId, parent, false);

			Comment comment = commentList.get(position-1);
			
			cCreatorView = (TextView)row.findViewById(R.id.txt_comment_creator);
			if(comment.getCreatorName().isEmpty() || comment.getCreatorId() <= 0)
				cCreatorView.setText(Utils._("anonymous1"));
			else
				cCreatorView.setText(comment.getCreatorName());
			
			cOpinionView = (TextView)row.findViewById(R.id.txt_creator_opinion);
			
			String vote = question.getUserVote();
			if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty())
				vote = mApplication.getVoteStatus(question.getId());
			
			if(comment.getType().equalsIgnoreCase(vote)){
				cOpinionView.setText(Utils._("agree"));
				cOpinionView.setTextColor(Color.parseColor("#56ACEE"));
			}else{
				cOpinionView.setText(Utils._("disagree"));
				cOpinionView.setTextColor(Color.parseColor("#CDCDCD"));
			}
				
			cWithyouView = (TextView)row.findViewById(R.id.txt_with_you);
			cWithyouView.setText(Utils._("withyou"));
			
			cCreatorView.setTypeface(tf_bold);
			cOpinionView.setTypeface(tf_bold);
			cWithyouView.setTypeface(tf_bold);
			
			cContentView = (TextView)row.findViewById(R.id.txt_comment_content);
			cContentView.setText(comment.getContent());
			cContentView.setTypeface(tf_regular);
			
			cFollowCountView = (TextView)row.findViewById(R.id.txt_comment_follow_count);
			btnFollowComment = (Button)row.findViewById(R.id.btn_comment_follow);
			btnUnfollowComment = (Button)row.findViewById(R.id.btn_comment_unfollow);
			
			btnFollowComment.setTag(position-1);
			btnFollowComment.setOnClickListener(this);
			btnUnfollowComment.setTag(position-1);
			btnUnfollowComment.setOnClickListener(this);
			cFollowCountView.setText(comment.getScore()+"");
			
			if(mApplication.getFollowStatus(comment.getId()) != null){
				if(mApplication.getFollowStatus(comment.getId()).equalsIgnoreCase("follow")){
					btnFollowComment.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_action_follow_blue));
					btnUnfollowComment.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_action_unfollow_gray));
				}else{
					btnFollowComment.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_action_follow_gray));
					btnUnfollowComment.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_action_unfollow_blue));
				}
				cFollowCountView.setTextColor(Color.parseColor("#56ACEE"));
			}
			return row;
		}
	}
	
	@Override
	public void onClick(View v) {
		Comment comment;
		switch (v.getId()) {
		
		case R.id.btn_add_comment:
			activity.onClickAddBtn();
			break;
		case R.id.layer_question_detail_vote_yes:
			activity.onClickVoteBtn("up");
			break;
		case R.id.layer_question_detail_vote_no:
			activity.onClickVoteBtn("down");
			break;
		case R.id.btn_comment_follow:
			comment = commentList.get((Integer) v.getTag());
			if(mApplication.getFollowStatus(comment.getId()) == null){
				mApplication.storeFollowStatus(comment.getId(), "follow");
				View parent = (View)v.getParent();
				TextView txtCount = (TextView)parent.findViewById(R.id.txt_comment_follow_count);
				txtCount.setText(comment.getScore()+1+"");
				txtCount.setTextColor(Color.parseColor("#56ACEE"));
				v.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_action_follow_blue));
				
				((CommentActivity)context).updateCommentScore(comment.getId(), 1);
			}
			break;
		case R.id.btn_comment_unfollow:
			comment = commentList.get((Integer) v.getTag());
			if(mApplication.getFollowStatus(comment.getId()) == null){
				mApplication.storeFollowStatus(comment.getId(), "unfollow");
				
				View parent = (View)v.getParent();
				TextView txtCount = (TextView)parent.findViewById(R.id.txt_comment_follow_count);
				txtCount.setText(comment.getScore()-1+"");
				txtCount.setTextColor(Color.parseColor("#56ACEE"));
				v.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_action_unfollow_blue));
				
				((CommentActivity)context).updateCommentScore(comment.getId(), 0);
			}
			break;
		default:
			break;
		}
	}
	
	private void refreshData(){
		question = mApplication.getCurrentQuestion();
		if(question == null)
			return;
		if(question.getCreatorName().isEmpty())
			qCreatorView.setText(Utils._("anonymous1"));
		else
			qCreatorView.setText(question.getCreatorName());
		qContentView.setText(question.getContent());
		
		String link = String.format("<a href=\"%s\">%s</a>",
        		question.getLink(),
        		question.getLink());
		qLinkView.setMovementMethod(LinkMovementMethod.getInstance());
		qLinkView.setText(Html.fromHtml(link));
		
		qShareCountView.setText("" + (question.getVoteUp() + question.getVoteDown()));
		
		int up_percent = Utils.calculatePercentage(question.getVoteUp(), question.getVoteDown());
		qVoteUpView.setText(up_percent + "%");
		qVoteDownView.setText((100-up_percent) + "%");
		qDurationView.setText(Utils.formatDuration(question.getDuration(), mApplication.getLanguage()));
		
		Log.d("User Vote", question.getUserVote());
		String vote = question.getUserVote();
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
			vote_yes.setBackgroundColor(Color.parseColor("#CDCDCD"));
			vote_no.setBackgroundColor(Color.parseColor("#CDCDCD"));
			
			qVoteUpView.setVisibility(View.INVISIBLE);
			qVoteDownView.setVisibility(View.INVISIBLE);
			
			vote_yes.setOnClickListener(this);
			vote_no.setOnClickListener(this);
			
			btnAddComment.setVisibility(View.INVISIBLE);
			txtMyVoice.setTextColor(Color.parseColor("#CDCDCD"));
			
		}else {
			if(vote.equalsIgnoreCase("up")){
				vote_yes.setBackgroundColor(Color.parseColor("#56ACEE"));
				vote_no.setBackgroundColor(Color.parseColor("#CDCDCD"));
			}else if(vote.equalsIgnoreCase("down")){
				vote_yes.setBackgroundColor(Color.parseColor("#CDCDCD"));
				vote_no.setBackgroundColor(Color.parseColor("#56ACEE"));
			}
			
			qVoteUpView.setVisibility(View.VISIBLE);
			qVoteDownView.setVisibility(View.VISIBLE);
			btnAddComment.setVisibility(View.VISIBLE);
			txtMyVoice.setTextColor(Color.parseColor("#56ACEE"));
		}
	}
	public void updateCommentList(List<Comment> cList) {
		// TODO Auto-generated method stub
		commentList.clear();
		if(cList != null)
			commentList.addAll(cList);
	    this.notifyDataSetChanged();
	}
	public void clearCommentList() {
		// TODO Auto-generated method stub
		commentList.clear();
	    this.notifyDataSetChanged();
	}
	public void updateUIAfterVote(String vote){
		
		qVoteUpView.setVisibility(View.VISIBLE);
		qVoteDownView.setVisibility(View.VISIBLE);
		
		if(vote.equalsIgnoreCase("up")){
			vote_yes.setBackgroundColor(Color.parseColor("#56ACEE"));
		}else if(vote.equalsIgnoreCase("down")){
			vote_no.setBackgroundColor(Color.parseColor("#56ACEE"));
		}
		refreshData();
	}
}