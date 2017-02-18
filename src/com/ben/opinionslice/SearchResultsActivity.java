package com.ben.opinionslice;

import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.data.Feed;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.tasks.LoadFeedQuestions;
import com.ben.opinionslice.tasks.LoadQuestionComments;
import com.ben.opinionslice.tasks.LoadSingleQuestion;
import com.ben.opinionslice.tasks.LoadUserVoteStatus;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SearchResultsActivity extends Activity {

	private TextView txtNoResult;
	private OSApplication mApplication; 
	private Question question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
 
        txtNoResult = (TextView) findViewById(R.id.txtNoResult);
        txtNoResult.setText(Utils._("search_no_result"));
        txtNoResult.setVisibility(View.INVISIBLE);
        // get the action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        mApplication = (OSApplication)getApplication();
        question = null;
        
        handleIntent(getIntent());
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
            	loadQuestion(questionId);
            }
        }
 
    }
    public void loadQuestion(final int questionId) {
		ShowDialog.showLoadingDialog(SearchResultsActivity.this, Utils._("searching"));
		final LoadSingleQuestion asynTask = new LoadSingleQuestion(SearchResultsActivity.this, questionId, new LoadingListener() {

            @Override
            public void onError(Object error) {
            	ShowDialog.removeLoadingDialog();
            }

			@Override
			public void onLoadingComplete(List obj) {
				
			}

			@Override
			public void onLoadingComplete(Object obj) {
				// TODO Auto-generated method stub
				if(obj == null){
					ShowDialog.removeLoadingDialog();
					Log.d("Load Complete::", "No Question");
					txtNoResult.setVisibility(View.VISIBLE);
				}else{
					txtNoResult.setVisibility(View.INVISIBLE);
					question = (Question)obj;
					mApplication.setCurrentQuestion(question);
					
					if(mApplication.getUser() != null){
						LoadUserVoteStatus task1 = new LoadUserVoteStatus(SearchResultsActivity.this, mApplication.getUser().getId(), question.getId(), new LoadingListener() {

				            @Override
				            public void onError(Object error) {
				            	ShowDialog.removeLoadingDialog();
				            }

							@Override
							public void onLoadingComplete(List obj) {

							}

							@Override
							public void onLoadingComplete(Object obj) {
								ShowDialog.removeLoadingDialog();
								if(obj != null){
									Log.d("User Vote Load Complete::", (String)obj);
									// TODO Auto-generated method stub
									question.setUserVote((String)obj);
									Intent intent = new Intent(SearchResultsActivity.this, CommentActivity.class);
									startActivity(intent);
									finish();
								}
								
							}
				        });

						task1.execute();
					}else{
						ShowDialog.removeLoadingDialog();
						Intent intent = new Intent(SearchResultsActivity.this, CommentActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}
        });
		asynTask.execute();
    }
}
