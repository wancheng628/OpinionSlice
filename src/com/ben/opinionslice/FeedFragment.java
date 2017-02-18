package com.ben.opinionslice;

import java.util.ArrayList;
import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Comment;
import com.ben.opinionslice.data.Feed;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.interfaces.ContentRefreshInterface;
import com.ben.opinionslice.listener.CommentListAdapter;
import com.ben.opinionslice.listener.GooglePlacesAutoCompleteAdapter;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.listener.QuestionListAdapter;
import com.ben.opinionslice.tasks.LoadFeedLocation;
import com.ben.opinionslice.tasks.LoadMoreQuestions;
import com.ben.opinionslice.tasks.SaveComment;
import com.ben.opinionslice.tasks.SaveFeedLocation;
import com.ben.opinionslice.tasks.UpdateQuestionVote;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;
import com.ben.opinionslice.view.PullAndLoadListView;
import com.ben.opinionslice.view.PullAndLoadListView.OnLoadMoreListener;
import com.ben.opinionslice.view.PullToRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class FeedFragment extends Fragment implements OnClickListener, OnItemClickListener{
	
	private int currentFeedIndex;
	private QuestionListAdapter newAdapter = null;
	private OSApplication mApplication;
	List<Question> questionList;
	InputMethodManager imm;
	
	EditText txtComment;
	RelativeLayout layout_write_comment;
	ImageButton btnAddQuestion;
	PullAndLoadListView questionListView;
	Button btnPostComment;
	AutoCompleteTextView editLocation;
	Button btnLocationChange;
	RelativeLayout layoutLocation;
	TextView txtLocation;
	View rootView;
	
	private static final int CREATE = 1;
	private static final int UPDATE = 2;
	
	public FeedFragment(){
		
	}
	public FeedFragment(int index) {
		currentFeedIndex = index;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
		mApplication = (OSApplication) getActivity().getApplication();
		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		rootView = inflater.inflate(R.layout.layout_feed_page, container, false);
        
        editLocation = (AutoCompleteTextView)rootView.findViewById(R.id.edit_location_search);
        editLocation.setHint(Utils._("hint_select_another_location"));
        editLocation.setAdapter(new GooglePlacesAutoCompleteAdapter(getActivity(), R.layout.layout_list_item));
        editLocation.setOnItemClickListener(this);
        
		txtLocation = (TextView)rootView.findViewById(R.id.txt_location_search);
        layoutLocation = (RelativeLayout)rootView.findViewById(R.id.layout_searchbar);
        layoutLocation.setOnClickListener(this);
        
        //btnLocationChange = (Button)rootView.findViewById(R.id.btn_location_change);
        //layoutLocation.setOnClickListener(this);
        
        txtComment = (EditText)rootView.findViewById(R.id.edit_write_comment2);
        txtComment.setHint(Utils._("write_comment"));
        
		btnPostComment = (Button)rootView.findViewById(R.id.btn_post_comment);
		btnPostComment.setText(Utils._("add"));
		btnPostComment.setOnClickListener(this);
		
		layout_write_comment = (RelativeLayout)rootView.findViewById(R.id.layout_write_comment);
		txtLocation.setText(mApplication.getFeed(currentFeedIndex).getLocation());
		
		btnAddQuestion = (ImageButton)rootView.findViewById(R.id.btn_add_question);
		btnAddQuestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mApplication.isLogin()){
					Intent intent = new Intent(getActivity(), PostQuestionActivity.class);
					startActivity(intent);
				}else{
					ShowDialog.showAlertDialog(getActivity(), Utils._("login_require_title"), Utils._("login_require_msg"));
				}
			}
		});
		
		questionListView = (PullAndLoadListView)rootView.findViewById(R.id.view_questionList);
		questionListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				((HomeActivity)getActivity()).loadFeedQuestions(currentFeedIndex);
			}
		});
        
        questionListView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				// Do the work to load more items at the end of list
				// here
				//new LoadMoreDataTask().execute();
				//Log.d("Action", "Load More");
				loadMoreQuestions(currentFeedIndex);
			}
		});
        
        
        questionListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideCommentView();
				return false;
			}
		});
		questionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            	//Log.d("Position", position + "");
            	hideCommentView();
            	Log.d("Question List", mApplication.getFeed(currentFeedIndex).getQuestionList().size()+"");
            	mApplication.setCurrentQuestion(mApplication.getFeed(currentFeedIndex).getQuestionList().get(position-1));
            	mApplication.getFeed(currentFeedIndex).setCurrentQuestionIndex(position-1);
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                //Intent intent = new Intent(getActivity(), PostQuestionActivity.class);
                getActivity().startActivityForResult(intent, 704);
            }

        });
		questionList = mApplication.getFeed(currentFeedIndex).getQuestionList();
		
		if(questionList == null || questionList.isEmpty()){
			
		}else{
			newAdapter = new QuestionListAdapter(getActivity(), R.layout.layout_question_item, questionList);
			questionListView.setAdapter(newAdapter);
		}
        return rootView;
    }
	public void initQuestionListView(List<Question> items){
		if(items == null)
			items = new ArrayList<Question>();
		questionList = items;
		
		if(newAdapter == null){
		}
        newAdapter = new QuestionListAdapter(getActivity(), R.layout.layout_question_item, questionList);
        questionListView.setAdapter(newAdapter);
		
	}
	public void updateQuestionList(List<Question> questionList){
		Log.d("Action", "Update Question List View");
		this.questionList = questionList;
		if(newAdapter == null){
			newAdapter = new QuestionListAdapter(getActivity(), R.layout.layout_question_item, this.questionList);
	        questionListView.setAdapter(newAdapter);
	        questionListView.onRefreshComplete();
		}else{
        	newAdapter.updateQuestionList(this.questionList);
			questionListView.onRefreshComplete();
		}
		//questionListView.setSelection(mApplication.getQuestionPosition(currentFeedIndex));
	}
	public void clearQuestionListView(){
		Log.d("Action", "Clear Question List View");
		//txtLocation.setText("");
		//questionList.clear();
		if(newAdapter != null)
			newAdapter.updateQuestionList(null);
		
	}
	public void hideCommentView(){
		if(layout_write_comment.getVisibility() == View.VISIBLE)
			layout_write_comment.setVisibility(View.INVISIBLE);
		imm.hideSoftInputFromWindow(txtComment.getApplicationWindowToken(), 0);
	}
	public void onClickAddBtn(){
		Question question = mApplication.getCurrentQuestion();
		String vote = question.getUserVote();
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		if(!mApplication.isLogin()){
			ShowDialog.showAlertDialog(getActivity(), Utils._("login_require_title"), Utils._("login_require_msg"));
		}else if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty() || ((question.getVoteUp()+question.getVoteDown()) == 0)){
			ShowDialog.showAlertDialog(getActivity(), Utils._("login_require_title"), OSConfig.ALERT_MESSAGE_VOTE_REQUIRE);
		}else{
			//intent = new Intent(CommentActivity.this, PostCommentActivity.class);
            //startActivity(intent);
			layout_write_comment.setVisibility(View.VISIBLE);
			//if(question.getCommentCount() <= 0)
			//	txtComment.setHint(Utils._("Be the first to post a comment!"));
			txtComment.setVisibility(View.VISIBLE);
			txtComment.requestFocus();
			imm.showSoftInput(txtComment, 0);
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_post_comment){
			onClickPostBtn();
		}else if(v.getId() == R.id.layout_searchbar){
			
			hideCommentView();
			layoutLocation.setVisibility(View.GONE);
			editLocation.setText("");
			editLocation.setVisibility(View.VISIBLE);
			editLocation.requestFocus();
			imm.showSoftInput(editLocation, 0);
		}
	}
	public void onClickPostBtn(){
		Question question = mApplication.getCurrentQuestion();
		String content = txtComment.getText().toString();
		
		String vote = question.getUserVote();
		if(vote == null || vote == "null" || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		
		if(content.isEmpty()){
			ShowDialog.showAlertDialog(getActivity(), Utils._("login_require_title"), OSConfig.ALERT_MESSAGE_EMPTY_COMMENT);
		}else{
			if(vote != null)
				SaveComment(content, question.getId(), vote);
		}
	}
	
	private void SaveComment(String content, int question_id, String vote_type) {
        SaveComment asynTask = new SaveComment(getActivity(), content, question_id, vote_type, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {
					}

					@Override
					public void onLoadingComplete(Object obj) {
						if(obj != null){
							Log.d("Comment Save Complete::", obj.toString());
							layout_write_comment.setVisibility(View.INVISIBLE);
							imm.hideSoftInputFromWindow(txtComment.getApplicationWindowToken(), 0);
							//loadQuestionComments(question.getId());
						}else{
							Log.d("Comment Save Failed::", "Failed");
						}
					}
                });

        asynTask.execute();
    }
	private void SaveLocation(String tag, String location) {
		SaveFeedLocation asynTask = new SaveFeedLocation(getActivity(), tag, location, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {
					}

					@Override
					public void onLoadingComplete(Object obj) {
						if(obj != null){
							Log.d("Location Save Complete::", obj.toString());
							
						}else{
							Log.d("Location Save Failed::", "Failed");
						}
					}
                });

        asynTask.execute();
    }
	
	public void loadMoreQuestions(final int feedId) {
		if(!mApplication.getFeed(feedId).isMoreQuestions()){
			questionListView.onLoadMoreComplete();
			return;
		}
        LoadMoreQuestions asynTask = new LoadMoreQuestions(getActivity(), feedId, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {
						if(obj != null && !obj.isEmpty()){
							Log.d("Load Complete::", obj.toString());
							
							Feed currentFeed = mApplication.getFeed(feedId);
							questionList = currentFeed.getQuestionList();
							List<Question> newList = new ArrayList<Question>();
							newList.addAll(questionList);
							newList.addAll(obj);
							newAdapter.updateQuestionList(newList);
							currentFeed.setQuestionList(newList);
							questionListView.onLoadMoreComplete();
							
							if(obj.size() < OSConfig.LOAD_MORE_COUNT){
								Log.d("Load Complete::", "No More questions");
								mApplication.getFeed(feedId).setMoreQuestions(false);
							}
						}else{
							Log.d("Load Complete::", "No More questions");
							mApplication.getFeed(feedId).setMoreQuestions(false);
							questionListView.onLoadMoreComplete();
						}
					}

					@Override
					public void onLoadingComplete(Object obj) {
						
					}
                });

        asynTask.execute();
    }
	public void refreshLocation(){
		txtLocation.setText(mApplication.getFeed(currentFeedIndex).getLocation());
	}
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		
		String location = (String) adapterView.getItemAtPosition(position);
		txtLocation.setText(location);
		editLocation.setVisibility(View.GONE);
		layoutLocation.setVisibility(View.VISIBLE);
		imm.hideSoftInputFromWindow(editLocation.getApplicationWindowToken(), 0);
		
		User user = mApplication.getUser();
		
		if(mApplication.isLogin() && user != null){
			SaveLocation(OSConfig.FEED_KEYS[currentFeedIndex], location);
		}else{
			mApplication.storeFeedLocation(currentFeedIndex, location);
		}
		mApplication.getFeed(currentFeedIndex).setLocation(location);
		((HomeActivity)getActivity()).loadFeedQuestions(currentFeedIndex);
		
	}
}
