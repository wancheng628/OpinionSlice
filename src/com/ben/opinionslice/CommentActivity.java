package com.ben.opinionslice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Comment;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.CommentListAdapter;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.tasks.LoadMoreComments;
import com.ben.opinionslice.tasks.LoadQuestionComments;
import com.ben.opinionslice.tasks.LoadSingleQuestion;
import com.ben.opinionslice.tasks.LoadUserVoteStatus;
import com.ben.opinionslice.tasks.SaveComment;
import com.ben.opinionslice.tasks.UpdateCommentScore;
import com.ben.opinionslice.tasks.UpdateQuestionVote;
import com.ben.opinionslice.utils.JSONParser;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;
import com.ben.opinionslice.view.PullAndLoadListView;
import com.ben.opinionslice.view.PullAndLoadListView.OnLoadMoreListener;
import com.ben.opinionslice.view.PullToRefreshListView.OnRefreshListener;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class CommentActivity extends Activity implements OnClickListener, OnRefreshListener{

	Button btnAddComment;
	Button btnPostComment;
	LinearLayout vote_no;
	LinearLayout vote_yes;
	TextView qVoteUpView;
	TextView qVoteDownView;
	TextView qCreatorView;
	TextView qContentView;
	TextView qShareCountView;
	TextView qDurationView;
	TextView txtBeFirst;
	EditText txtComment;
	RelativeLayout layout_write_comment;
	
	OSApplication mApplication;
	CommentListAdapter newAdapter;
	Question question;
	User user;
	boolean isVoted = false;
	InputMethodManager imm;
	PullAndLoadListView commentListView;
	Menu menu;
	private final int MENU_ITEM_LOGOUT = 103;
	private static final int CREATE = 1;
	private static final int UPDATE = 2;
	
	int OPEN_STATUS = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
				
		ActionBar actionBar = getActionBar();    
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Questions");
		actionBar.setDisplayUseLogoEnabled(false);
		
		mApplication = (OSApplication)getApplication();
		user = mApplication.getUser();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		txtComment = (EditText)findViewById(R.id.edit_write_comment1);
		txtComment.setHint(Utils._("write_comment"));
		
		txtBeFirst = (TextView)findViewById(R.id.txt_beFirst);
		txtBeFirst.setText(Utils._("be_the_first"));
		
		
		btnPostComment = (Button)findViewById(R.id.btn_post_comment);
		btnPostComment.setText(Utils._("add"));
		btnPostComment.setOnClickListener(this);
		
		layout_write_comment = (RelativeLayout)findViewById(R.id.layout_write_comment);
		commentListView =(PullAndLoadListView)findViewById(R.id.view_commentList);
		
		commentListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				hideCommentView();
				return false;
			}
		});
		commentListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				
				question = mApplication.getCurrentQuestion();
				ShowDialog.showLoadingDialog(CommentActivity.this, Utils._("updating"));
				LoadSingleQuestion task1 = new LoadSingleQuestion(CommentActivity.this, question.getId(), new LoadingListener() {

		            @Override
		            public void onError(Object error) {
		            	ShowDialog.removeLoadingDialog();
		            }

					@SuppressWarnings("rawtypes")
					@Override
					public void onLoadingComplete(List obj) {
						
					}

					@Override
					public void onLoadingComplete(Object obj) {
						mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_UPDATE);
						if(obj != null){
							question = (Question)obj;
							mApplication.setCurrentQuestion(question);
							
							loadQuestionComments(question.getId(), CommentActivity.UPDATE);
						}
					}
				});
				task1.execute();
				
			}
		});
		commentListView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				loadMoreComments();
			}
		});
		/*
        List<Comment> items = new ArrayList<Comment>();
        newAdapter = new CommentListAdapter(this, R.layout.layout_comment_item, items);
        commentListView.setAdapter(newAdapter);
        */
        Bundle extras = getIntent().getExtras();
        
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {			// For search
        	OPEN_STATUS = 1;
        	question = mApplication.getCurrentQuestion();
			String vote = question.getUserVote();
			if(vote == null || vote == "null" || vote.isEmpty())
				vote = mApplication.getVoteStatus(question.getId());
			
			if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
				initCommentListView(null);
			}else{
				loadQuestionComments(question.getId(), CommentActivity.CREATE);
			}
        }else if(extras != null) {											// For push notification
        	OPEN_STATUS = 2;
			receiveNotification(extras);
		}else{																// default
			OPEN_STATUS = 0;
			question = mApplication.getCurrentQuestion();
			String vote = question.getUserVote();
			if(vote == null || vote == "null" || vote.isEmpty())
				vote = mApplication.getVoteStatus(question.getId());
			
			if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
				initCommentListView(null);
			}else{
				//loadQuestionComments(question.getId());
				question.setMoreComments(true);
				loadQuestionComments(question.getId(), CommentActivity.CREATE);
			}
		}
        /*
        if(question.getCommentCount() > 0)
        	txtBeFirst.setVisibility(View.GONE);
        else
        	txtBeFirst.setVisibility(View.VISIBLE);
		*/
	}
	private void initCommentListView(List<Comment> items){
		if(items == null)
			items = new ArrayList<Comment>();
		
		newAdapter = new CommentListAdapter(CommentActivity.this, R.layout.layout_comment_item, items);
        commentListView.setAdapter(newAdapter);
	}
	public void hideCommentView(){
		if(layout_write_comment.getVisibility() == View.VISIBLE){
			layout_write_comment.setVisibility(View.INVISIBLE);
			imm.hideSoftInputFromWindow(txtComment.getApplicationWindowToken(), 0);
		}
	}
	private void receiveNotification(Bundle extras){
		try{
			String message = extras != null ? extras.getString("com.parse.Data") : "";
			if(message == null || message.isEmpty())
				return;
			JSONObject json = new JSONObject(message);
			Question question = JSONParser.getQuestionfromJson(json.getString("question"));
			if(question != null)
				mApplication.setCurrentQuestion(question);
			Log.d("Current Language", mApplication.getLanguage());
			if(mApplication.getUser() != null){
				loadUserVoteStatus(mApplication.getUser().getId(), question.getId(), CommentActivity.CREATE);
			}else{
				loadQuestionComments(question.getId(), CommentActivity.CREATE);
			}
			Log.d("Received Data", message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void goBackHome(){
		Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
	}
	@Override
    public boolean onPrepareOptionsMenu(Menu menu){
    	
    	MenuItem settingItem = menu.findItem(R.id.action_settings1);
    	
    	if(mApplication.isLogin()){
    		settingItem.setTitle(Utils._("settings"));
    		if(menu.findItem(MENU_ITEM_LOGOUT) == null){
    			menu.findItem(R.id.a_More1).getSubMenu().add(Menu.NONE, MENU_ITEM_LOGOUT, 103, Utils._("signout"));
    		}
    	}else{
    		if(menu.findItem(MENU_ITEM_LOGOUT) != null){
    			menu.removeItem(103);
    		}
    		settingItem.setTitle(Utils._("signin"));
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		this.menu = menu;
		
		getMenuInflater().inflate(R.menu.comment, menu);
		
		MenuItem item = menu.findItem(R.id.action_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
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
		question = mApplication.getCurrentQuestion();
		link += question.getId();
		body = question.getContent() + " " + link;
		
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
		myShareActionProvider.setShareIntent(sharingIntent);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	goBackHome();
	            return true;
	        case R.id.action_settings1:
		    	if(mApplication.isLogin()){
		    		item.setTitle(Utils._("settings"));
		    		showSettingPage();
		    	}else{
		    		item.setTitle(Utils._("signin"));
		    		showLoginPage();
		    		//showSettingPage();
		    	}
		        return true;
	        case MENU_ITEM_LOGOUT:
		    	logout();
		    	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private void logout(){
		LoginActivity.callFacebookLogout(this);
		mApplication.setLogin(false);
		mApplication.setUser(null);
		reload();
	}
	private void showLoginPage() {
		Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
        startActivityForResult(intent, 702);
	}
	private void showSettingPage() {
		Intent intent = new Intent(CommentActivity.this, SettingActivity.class);
        startActivityForResult(intent, 102);
	}
	
	public void onClickAddBtn(){
		question = mApplication.getCurrentQuestion();
		String vote = question.getUserVote();
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		if(!mApplication.isLogin()){
			ShowDialog.showAlertDialog(CommentActivity.this, Utils._("login_require_title"), Utils._("login_require_msg"));
		}else if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty() || ((question.getVoteUp()+question.getVoteDown()) == 0)){
			ShowDialog.showAlertDialog(CommentActivity.this, Utils._("login_require_title"), OSConfig.ALERT_MESSAGE_VOTE_REQUIRE);
		}else{
			layout_write_comment.setVisibility(View.VISIBLE);
			if(question.getCommentCount() <= 0)
				txtComment.setHint(Utils._("be_the_first_hint"));
			txtComment.requestFocus();
			txtComment.setVisibility(View.VISIBLE);
			imm.showSoftInput(txtComment, 0);
		}
	}
	public void onClickPostBtn(){
		String content = txtComment.getText().toString();
		
		if(content.isEmpty()){
			ShowDialog.showAlertDialog(this, Utils._("login_require_title"), OSConfig.ALERT_MESSAGE_EMPTY_COMMENT);
		}else{
			String vote = question.getUserVote();
			if(vote == null || vote == "null" || vote.isEmpty())
				vote = mApplication.getVoteStatus(question.getId());
			SaveComment(content, question.getId(), vote);
		}
	}
	public void onClickVoteBtn(String user_vote){
		String vote = question.getUserVote();
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
			mApplication.setVoteStatus(question.getId(), user_vote);
			if(user != null)
				updateQuestionVote(user.getId(), question.getId(), user_vote);
			else
				updateQuestionVote(-1, question.getId(), user_vote);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_post_comment:
			onClickPostBtn();
			break;
		default:
			break;
		}
	}
	
	private void reload(){
		finish();
		startActivity(getIntent());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		*/
		if (requestCode == 702) {
	        if(resultCode == RESULT_OK){
	        	mApplication.setLogin(true);
	        	mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_UPDATE);
	        	
	        	updateMenuTitle();
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	}
	private void updateMenuTitle() {
		MenuItem settingItem = menu.findItem(R.id.action_settings1);
        if(mApplication.isLogin()){
    		settingItem.setTitle(Utils._("settings"));
    		if(menu.findItem(MENU_ITEM_LOGOUT) == null){
    			menu.findItem(R.id.a_More1).getSubMenu().add(Menu.NONE, MENU_ITEM_LOGOUT, 103, Utils._("signout"));
    		}
    	}else{
    		if(menu.findItem(MENU_ITEM_LOGOUT) != null){
    			menu.removeItem(103);
    		}
    		settingItem.setTitle(Utils._("signin"));
    	}
    }
	@Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
 
    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            int questionId = Integer.parseInt(intent.getStringExtra(SearchManager.QUERY));
            if(questionId > 0){
            	loadSearchQuestion(questionId);
            }
        }
 
    }
    public void loadSearchQuestion(final int questionId) {
		ShowDialog.showLoadingDialog(CommentActivity.this, OSConfig.MESSAGE_SEARCHING);
		final LoadSingleQuestion asynTask = new LoadSingleQuestion(CommentActivity.this, questionId, new LoadingListener() {

            @Override
            public void onError(Object error) {
            	ShowDialog.removeLoadingDialog();
            }

			@SuppressWarnings("rawtypes")
			@Override
			public void onLoadingComplete(List obj) {
				
			}

			@Override
			public void onLoadingComplete(Object obj) {
				// 
				if(obj == null){
					ShowDialog.removeLoadingDialog();
					Log.d("Load Complete::", "No Question");
				}else{
					question = (Question)obj;
					mApplication.setCurrentQuestion(question);
					
					if(mApplication.getUser() != null){
						LoadUserVoteStatus task1 = new LoadUserVoteStatus(CommentActivity.this, mApplication.getUser().getId(), question.getId(), new LoadingListener() {

				            @Override
				            public void onError(Object error) {
				            	ShowDialog.removeLoadingDialog();
				            }

							@SuppressWarnings("rawtypes")
							@Override
							public void onLoadingComplete(List obj) {

							}

							@Override
							public void onLoadingComplete(Object obj) {
								ShowDialog.removeLoadingDialog();
								if(obj != null){
									Log.d("User Vote Load Complete::", (String)obj);
									// 
									question.setUserVote((String)obj);
									
									String vote = question.getUserVote();
									if(vote == null || vote == "null" || vote.isEmpty())
										vote = mApplication.getVoteStatus(question.getId());
									
									if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
										
									}else{
										loadQuestionComments(question.getId(), CommentActivity.CREATE);
									}
								}
								
							}
				        });

						task1.execute();
					}else{
						ShowDialog.removeLoadingDialog();
					}
				}
			}
        });
		asynTask.execute();
    }
    public void loadUserVoteStatus(int user_id, int question_id, final int action){
		
		ShowDialog.showLoadingDialog(CommentActivity.this, OSConfig.MESSAGE_LOADING_COMMENTS);
		LoadUserVoteStatus task1 = new LoadUserVoteStatus(CommentActivity.this, user_id, question_id, new LoadingListener() {

            @Override
            public void onError(Object error) {

            }

			@SuppressWarnings("rawtypes")
			@Override
			public void onLoadingComplete(List obj) {

			}

			@Override
			public void onLoadingComplete(Object obj) {
				if(obj != null){
					Log.d("Load Complete::", (String)obj);
					// 
					question.setUserVote((String)obj);
					
					/*
					String vote = question.getUserVote();
					if(vote == null || vote == "null" || vote.isEmpty())
						vote = mApplication.getVoteStatus(question.getId());
					
					if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
						ShowDialog.removeLoadingDialog();
						return;
					}
					*/
					//question.setMoreComments(true);
					loadQuestionComments(question.getId(), action);
				}
				
			}
        });

		task1.execute();
	}
	public void loadQuestionComments(int questionId, final int action) {
		ShowDialog.showLoadingDialog(CommentActivity.this, Utils._("updating"));
		
		question = mApplication.getCurrentQuestion();
		String vote = question.getUserVote();
		if(vote == null || vote == "null" || vote.isEmpty())
			vote = mApplication.getVoteStatus(question.getId());
		
		if(vote == null || vote.equalsIgnoreCase("null") || vote.isEmpty()){
			ShowDialog.removeLoadingDialog();
			initCommentListView(null);
			return;
		}
		
		question.setMoreComments(true);
        LoadQuestionComments asynTask = new LoadQuestionComments(CommentActivity.this, questionId, new LoadingListener() {

            @Override
            public void onError(Object error) {
            	ShowDialog.removeLoadingDialog();
            }

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void onLoadingComplete(List obj) {
				ShowDialog.removeLoadingDialog();
				question.setCommentList(obj);
				
				if(action == CommentActivity.CREATE){
					initCommentListView(obj);
				}else{
					newAdapter.updateCommentList(obj);
					commentListView.onRefreshComplete();
				}
				if(obj == null || obj.size() < OSConfig.DEFAULT_LOAD_COUNT)
					question.setMoreComments(false);
				
				if(question.getCommentCount() > 0)
		        	txtBeFirst.setVisibility(View.INVISIBLE);
		        else
		        	txtBeFirst.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingComplete(Object obj) {

				
			}
        });

        asynTask.execute();
    }
	public void loadMoreComments() {
		question = mApplication.getCurrentQuestion();
		Log.d("CommentActivity", "Load More");
		if(!question.isMoreComments()){
			commentListView.onLoadMoreComplete();
		}else{
			LoadMoreComments asynTask = new LoadMoreComments(this, question, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					public void onLoadingComplete(List obj) {
						if(obj != null && !obj.isEmpty()){
							Log.d("Load Complete::", obj.toString());
							//newAdapter.clearCommentList();
							List<Comment> newList = new ArrayList<Comment>();
							newList.addAll(question.getCommentList());
							newList.addAll(obj);
							newAdapter.updateCommentList(newList);
							question.setCommentList(newList);
							mApplication.setCurrentQuestion(question);
							commentListView.onLoadMoreComplete();
							
							if(obj.size() < OSConfig.LOAD_MORE_COUNT){
								Log.d("Load Complete::", "No More questions");
								question.setMoreComments(false);
							}
						}else{
							Log.d("Load Complete::", "No More comments");
							question.setMoreComments(false);
							commentListView.onLoadMoreComplete();
						}
					}

					@Override
					public void onLoadingComplete(Object obj) {
						
					}
                });

        asynTask.execute();
		}
    }
	private void updateQuestionVote(int user_id, int question_id, final String vote) {
		UpdateQuestionVote asynTask = new UpdateQuestionVote(CommentActivity.this, user_id, question_id, vote, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@SuppressWarnings("rawtypes")
					@Override
					public void onLoadingComplete(List obj) {

					}

					@Override
					public void onLoadingComplete(Object obj) {
						//Log.d("Load Complete::", obj.toString());
						if(obj != null){
							question = (Question)obj;
							mApplication.setCurrentQuestion(question);
						//question.setUserVote(vote);
						//if(vote.equalsIgnoreCase("up")){
						//	question.setVoteUp(question.getVoteUp()+1);
						//}else if(vote.equalsIgnoreCase("down")){
						//	question.setVoteDown(question.getVoteDown()+1);
						//}
							mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_UPDATE);
							newAdapter.updateUIAfterVote(vote);
							loadQuestionComments(question.getId(), CommentActivity.UPDATE);
						}
					}
                });

        asynTask.execute();
    }
	private void SaveComment(String content, int question_id, String vote_type) {
        SaveComment asynTask = new SaveComment(CommentActivity.this, content, question_id, vote_type, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@SuppressWarnings("rawtypes")
					@Override
					public void onLoadingComplete(List obj) {
					}

					@Override
					public void onLoadingComplete(Object obj) {

						if(obj != null){
							Log.d("Comment Save Complete::", obj.toString());
							layout_write_comment.setVisibility(View.INVISIBLE);
							imm.hideSoftInputFromWindow(txtComment.getApplicationWindowToken(), 0);
							mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_UPDATE);
							loadQuestionComments(question.getId(), CommentActivity.UPDATE);
						}else{
							Log.d("Comment Save Failed::", "Failed");
						}
					}
                });

        asynTask.execute();
    }
	public void updateCommentScore(int comment_id, final int follow) {
		UpdateCommentScore asynTask = new UpdateCommentScore(CommentActivity.this, comment_id, follow, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@SuppressWarnings("rawtypes")
					@Override
					public void onLoadingComplete(List obj) {

					}

					@Override
					public void onLoadingComplete(Object obj) {
						if(obj != null){
							mApplication.setCurrentStatus(OSConfig.OP_STATUS.OP_STATUS_UPDATE);
							Log.d("Score update Complete::", obj.toString());
							//loadQuestionComments(question.getId());
						}else{
							Log.d("Score update Failed::", "Failed");
						}
					}
                });

        asynTask.execute();
    }
	@Override
	public void onRefresh() {
	}
}
