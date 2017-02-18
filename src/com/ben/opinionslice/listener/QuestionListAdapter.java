package com.ben.opinionslice.listener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ben.opinionslice.FeedFragment;
import com.ben.opinionslice.HomeActivity;
import com.ben.opinionslice.R;
import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.interfaces.ContentRefreshInterface;
import com.ben.opinionslice.tasks.UpdateQuestionVote;
//import com.ben.opinionslice.utils.ImageLoader;
import com.ben.opinionslice.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionListAdapter extends ArrayAdapter<Question> implements OnClickListener{

	private List<Question> questionList;
	Question question;
	private int layoutResourceId;
	private Context context;
	
	static class QuestionViewHolder{
		TextView qCreatorView;
		TextView qContentView;
		TextView qLinkView;
		TextView qShareCountView;
		ImageView qProfileView;
		TextView qDurationView;
		TextView txtMyVoice;
		TextView txtVoteYes;
		TextView txtVoteNo;
		TextView txtOpinionShared;
		
		LinearLayout layer_vote_yes;
		LinearLayout layer_vote_no;
		TextView qVoteUpView;
		TextView qVoteDownView;
		
		Button btnAddComment;
		Button btnShareQuestion;
    }
	
	OSApplication mApplication;
	FeedFragment fg;
	Activity activity;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	LayoutInflater mInflater;
	Typeface tf;
	
	public QuestionListAdapter(Context context, int questionItemResourceId, List<Question> questionList) {
		super(context, questionItemResourceId, questionList);
		// TODO Auto-generated constructor stub
		this.layoutResourceId = questionItemResourceId;
		this.context = context;
		this.activity = (Activity)context;
		this.questionList = questionList;
		this.mApplication = ((OSApplication)((Activity) context).getApplication());
		//this.fg = (FeedFragment)((HomeActivity)context).getSupportFragmentManager().getFragments().get(mApplication.getCurrentFeedIndex());
		this.fg = (FeedFragment) ((HomeActivity) context).mAdapter.getItem(mApplication.getCurrentFeedIndex());
		this.mInflater = ((Activity) context).getLayoutInflater();
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_stub)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(10))
		.build();
		if(!imageLoader.isInited())
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		QuestionViewHolder viewHolder;
		if (row == null) {
			row = mInflater.inflate(layoutResourceId, parent, false);
			viewHolder = new QuestionViewHolder();
			
			viewHolder.qCreatorView = (TextView)row.findViewById(R.id.txt_question_creator);
	        viewHolder.qContentView = (TextView)row.findViewById(R.id.txt_question_content);
	        viewHolder.qLinkView = (TextView)row.findViewById(R.id.txt_question_link);
	        viewHolder.qShareCountView = (TextView)row.findViewById(R.id.txt_share_count);
	        viewHolder.qProfileView = (ImageView)row.findViewById(R.id.img_profile_image);
	        viewHolder.qDurationView = (TextView)row.findViewById(R.id.txt_question_duration);
	        viewHolder.txtMyVoice = (TextView)row.findViewById(R.id.txt_myvoice1);
	        viewHolder.txtVoteYes = (TextView)row.findViewById(R.id.txt_vote_yes_text);
	        viewHolder.txtVoteNo = (TextView)row.findViewById(R.id.txt_vote_no_text);
	        viewHolder.txtOpinionShared = (TextView)row.findViewById(R.id.txt_opinion_shared);
	        		
	        viewHolder.layer_vote_yes = (LinearLayout)row.findViewById(R.id.layer_vote_yes);
			viewHolder.layer_vote_no = (LinearLayout)row.findViewById(R.id.layer_vote_no);
			viewHolder.btnShareQuestion = (Button)row.findViewById(R.id.btn_share_question);
			viewHolder.btnAddComment = (Button)row.findViewById(R.id.btn_add_comment);
			
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
			viewHolder.qContentView.setTypeface(tf);
			viewHolder.qLinkView.setTypeface(tf);
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
			viewHolder.qCreatorView.setTypeface(tf);
			
			viewHolder.txtVoteYes.setText(Utils._("yes"));
			viewHolder.txtVoteNo.setText(Utils._("no"));
			viewHolder.txtMyVoice.setText(Utils._("my_voice"));
			viewHolder.txtOpinionShared.setText(Utils._("opinion_shared"));
			
	        row.setTag(viewHolder);
        }else{
        	viewHolder = (QuestionViewHolder)row.getTag();
        }
		
		question = questionList.get(position);
		
		if(question.getCreatorName().isEmpty())
			viewHolder.qCreatorView.setText(Utils._("anonymous1"));
		else
			viewHolder.qCreatorView.setText(question.getCreatorName());
		
		
		viewHolder.qContentView.setText(question.getContent());
		
		String href = question.getLink();
		if(!href.isEmpty() && !href.startsWith("http://") && !href.startsWith("https://"))
			href = "http://" + href;
		String link = String.format("<a href=\"%s\">%s</a>",
				href,
        		question.getLink());
		viewHolder.qLinkView.setMovementMethod(LinkMovementMethod.getInstance());
		viewHolder.qLinkView.setText(Html.fromHtml(link));
		viewHolder.qShareCountView.setText("" + (question.getVoteUp() + question.getVoteDown()));
		
		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		if(question.getCreatorId() == 0 || question.getCreatorImage() == null || question.getCreatorImage().isEmpty() || question.getCreatorImage().equalsIgnoreCase("null"))
			//loader.DisplayImage("http://www.opinionslice.com/img/icons/images.jpg", qProfileView, false);
			imageLoader.displayImage("http://www.opinionslice.com/img/icons/images.jpg", viewHolder.qProfileView, options, animateFirstListener);
		else
			//loader.DisplayImage(question.getCreatorImage(), qProfileView, false);
			imageLoader.displayImage(question.getCreatorImage(), viewHolder.qProfileView, options, animateFirstListener);
		
		
		int up_percent = Utils.calculatePercentage(question.getVoteUp(), question.getVoteDown());
		
		viewHolder.qVoteUpView = (TextView)row.findViewById(R.id.txt_vote_yes_percent);
		viewHolder.qVoteUpView.setText(up_percent + "%");
		
		viewHolder.qVoteDownView = (TextView)row.findViewById(R.id.txt_vote_no_percent);
		viewHolder.qVoteDownView.setText((100-up_percent) + "%");
		
		viewHolder.qDurationView.setText(Utils.formatDuration(question.getDuration(), mApplication.getLanguage()));
		
		
		viewHolder.btnAddComment.setTag(position);
		viewHolder.btnAddComment.setOnClickListener(this);
		
		
		viewHolder.btnShareQuestion.setTag(position);
		viewHolder.btnShareQuestion.setOnClickListener(this);
		
		
		viewHolder.layer_vote_yes.setTag(position);
		viewHolder.layer_vote_no.setTag(position);
		
		String vote = question.getUserVote();
		if(vote == null || vote == "null" || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		Log.d("Question ID", question.getId()+"");
		if(vote == null || vote == "null" || vote.isEmpty()){
			viewHolder.layer_vote_yes.setBackgroundColor(Color.parseColor("#CDCDCD"));
			viewHolder.layer_vote_no.setBackgroundColor(Color.parseColor("#CDCDCD"));
			
			viewHolder.qVoteUpView.setVisibility(View.INVISIBLE);
			viewHolder.qVoteDownView.setVisibility(View.INVISIBLE);
			
			viewHolder.btnAddComment.setVisibility(View.INVISIBLE);
			viewHolder.txtMyVoice.setTextColor(Color.parseColor("#CDCDCD"));
		}else {
			if(vote.equalsIgnoreCase("up")){
				viewHolder.layer_vote_yes.setBackgroundColor(Color.parseColor("#56ACEE"));
				viewHolder.layer_vote_no.setBackgroundColor(Color.parseColor("#CDCDCD"));
			}else if(vote.equalsIgnoreCase("down")){
				viewHolder.layer_vote_yes.setBackgroundColor(Color.parseColor("#CDCDCD"));
				viewHolder.layer_vote_no.setBackgroundColor(Color.parseColor("#56ACEE"));
			}
			
			viewHolder.qVoteUpView.setVisibility(View.VISIBLE);
			viewHolder.qVoteDownView.setVisibility(View.VISIBLE);
			
			viewHolder.btnAddComment.setVisibility(View.VISIBLE);
			viewHolder.txtMyVoice.setTextColor(Color.parseColor("#56ACEE"));
		}
		viewHolder.layer_vote_yes.setOnClickListener(this);
		viewHolder.layer_vote_no.setOnClickListener(this);
		
		return row;
	}
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	public void updateQuestionList(List<Question> qList){
		questionList.clear();
		if(qList != null){
			questionList.addAll(qList);
		}
		this.notifyDataSetChanged();
	}
	@Override
	public void onClick(View v) {
		question = questionList.get((Integer) v.getTag());
		mApplication.setCurrentQuestion(question);
		//mApplication.storeQuestionPosition(mApplication.getCurrentFeedIndex(), (Integer)v.getTag());
		String vote = question.getUserVote();
		switch (v.getId()) {
			case R.id.btn_add_comment:
				fg.onClickAddBtn();
				break;
			case R.id.btn_share_question:
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				User user = mApplication.getUser();
				String subject = "";
				String body = "";
				if(user != null)
					subject = user.getUserName() + " shared a question on OpinionSlice with you";
				else
					subject = "A question is shared on OpinionSlice with you";
				String link = "http://www.opinionslice.com/";
				if(mApplication.getLanguage().equalsIgnoreCase("fr"))
						link += "fr/"; 
				link += question.getId();
				body = question.getContent() + " " + link;
				
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
				
				activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
				break;
			case R.id.layer_vote_yes:
				if(mApplication.getVoteStatus(question.getId()) == null){
					if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
						mApplication.setVoteStatus(question.getId(), "up");
						onClickVoteBtn("up", ((View)v.getParent()));
					}
				}
				break;
			case R.id.layer_vote_no:
				if(mApplication.getVoteStatus(question.getId()) == null){
					if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
						mApplication.setVoteStatus(question.getId(), "down");
						onClickVoteBtn("down", ((View)v.getParent()));
					}
				}
				break;	
			default:
				break;
		}
	}
	public void onClickVoteBtn(String vote, View parent){
		Question question = mApplication.getCurrentQuestion();
		User user = mApplication.getUser();
		if(user == null)
			updateQuestionVote(-1, question, vote, parent);
		else
			updateQuestionVote(user.getId(), question, vote, parent);
	}
	
	private void updateQuestionVote(int user_id, final Question question, final String vote, final View parent) {
		UpdateQuestionVote asynTask = new UpdateQuestionVote(activity, user_id, question.getId(), vote, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {

					}

					@Override
					public void onLoadingComplete(Object obj) {
						Log.d("Update Complete::", obj.toString());
						question.setUserVote(vote);
						if(vote.equalsIgnoreCase("up")){
							question.setVoteUp(question.getVoteUp()+1);
						}else if(vote.equalsIgnoreCase("down")){
							question.setVoteDown(question.getVoteDown()+1);
						}
						    	
				    	TextView txt_myvoice = (TextView) parent.findViewById(R.id.txt_myvoice1);
						TextView qVoteUpView = (TextView) parent.findViewById(R.id.txt_vote_yes_percent);
						TextView qVoteDownView = (TextView) parent.findViewById(R.id.txt_vote_no_percent);
						Button btnAddComment = (Button)parent.findViewById(R.id.btn_add_comment);
						TextView qShareCountView = (TextView)parent.findViewById(R.id.txt_share_count);
						LinearLayout layer_vote_yes = (LinearLayout)parent.findViewById(R.id.layer_vote_yes);
						LinearLayout layer_vote_no = (LinearLayout)parent.findViewById(R.id.layer_vote_no);
						
						int up_percent = Utils.calculatePercentage(question.getVoteUp(), question.getVoteDown());
						qVoteUpView.setText(up_percent + "%");
						qVoteDownView.setText((100-up_percent) + "%");
						
						qVoteUpView.setVisibility(View.VISIBLE);
						qVoteDownView.setVisibility(View.VISIBLE);
						btnAddComment.setVisibility(View.VISIBLE);
						txt_myvoice.setTextColor(Color.parseColor("#56ACEE"));
						qShareCountView.setText("" + (question.getVoteUp() + question.getVoteDown()));
						
						if(vote.equalsIgnoreCase("up")){
							layer_vote_yes.setBackgroundColor(Color.parseColor("#56ACEE"));
							layer_vote_no.setBackgroundColor(Color.parseColor("#CDCDCD"));
						}else if(vote.equalsIgnoreCase("down")){
							layer_vote_yes.setBackgroundColor(Color.parseColor("#CDCDCD"));
							layer_vote_no.setBackgroundColor(Color.parseColor("#56ACEE"));
						}
					}
                });

        asynTask.execute();
    }
}