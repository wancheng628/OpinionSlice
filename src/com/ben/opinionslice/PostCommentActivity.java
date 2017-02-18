package com.ben.opinionslice;

import java.util.List;

import com.ben.opinionslice.application.OSApplication;
import com.ben.opinionslice.application.OSConfig;
import com.ben.opinionslice.application.OSConfig.OP_STATUS;
import com.ben.opinionslice.data.Question;
import com.ben.opinionslice.data.User;
import com.ben.opinionslice.listener.LoadingListener;
import com.ben.opinionslice.tasks.SaveQuestion;
import com.ben.opinionslice.tasks.SaveComment;
import com.ben.opinionslice.utils.ImageLoader;
import com.ben.opinionslice.utils.ShowDialog;
import com.ben.opinionslice.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PostCommentActivity extends Activity implements OnClickListener{

	LinearLayout keyboardLayout1;
	EditText txtComment;
	ImageView avatarView;
	TextView nameView;
	TextView counterView;
	InputMethodManager imm;
	
	User user;
	ImageLoader loader;
	String content;
	
	OSApplication mApplication;
	Question question;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_comment);
		
		mApplication = (OSApplication)getApplication();
		loader = new ImageLoader(this);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		avatarView = (ImageView) findViewById(R.id.img_user_profile_picture1);
		nameView = (TextView)findViewById(R.id.txt_user_profile_name1);
		txtComment = (EditText)findViewById(R.id.edit_write_comment);
		counterView = (TextView)findViewById(R.id.txt_comment_character_left);
		
		final TextWatcher mTextEditorWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	if (s.length() >= 170)
	            {
	                new AlertDialog
	                	.Builder(PostCommentActivity.this)
	                	.setTitle("Character limit exceeded")
	                	.setMessage("Input cannot exceed more than " + 170 + " characters.").
	                	setPositiveButton(android.R.string.ok, null).
	                	show();
	                
	            }
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	           //This sets a textview to the current length
	        	counterView.setText(String.valueOf(600-s.length()));
	        }

	        public void afterTextChanged(Editable s) {
	        }
		};
		txtComment.addTextChangedListener(mTextEditorWatcher);
		
		Button button;
		button = (Button)findViewById(R.id.btn_cancel);
		button.setOnClickListener(this);
		
		button = (Button)findViewById(R.id.btn_comment_post);
		button.setOnClickListener(this);
		
		
		refreshData();
	}
	private void refreshData(){
		
		user = mApplication.getUser();
		question = mApplication.getCurrentQuestion();
		
		String image_url = "";
		if(user == null || user.getAvatar() == "")
			image_url = "http://www.opinionslice.com/img/icons/images.jpg";
		else
			image_url = user.getAvatar();
		
		loader.DisplayImage(image_url, avatarView, false);
		
		String user_name = "";
		if(user == null || user.getAvatar() == "")
			user_name = Utils._("anonymous1");
		else
			user_name = user.getFullName();
		nameView.setText(user_name);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_comment, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_comment_post:
			content = txtComment.getText().toString();
			
			if(content.isEmpty()){
				ShowDialog.showAlertDialog(this, Utils._("login_require_title"), OSConfig.ALERT_MESSAGE_EMPTY_COMMENT);
			}else{
				String vote = question.getUserVote();
				if(vote == null || vote == "null" || vote.isEmpty())
					vote = mApplication.getVoteStatus(question.getId());
				
				SaveComment(content, question.getId(), vote);
			}
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}
	}
	
	private void SaveComment(String content, int question_id, String vote_type) {
        SaveComment asynTask = new SaveComment(PostCommentActivity.this, content, question_id, vote_type, new LoadingListener() {

                    @Override
                    public void onError(Object error) {

                    }

					@Override
					public void onLoadingComplete(List obj) {
					}

					@Override
					public void onLoadingComplete(Object obj) {
						// TODO Auto-generated method stub
						if(obj != null){
							Log.d("Comment Save Complete::", obj.toString());
							mApplication.setCurrentStatus(OP_STATUS.OP_STATUS_POST_COMMENT);
							Intent returnIntent = new Intent();
							setResult(RESULT_OK, returnIntent);
							finish();
						}
					}
                });

        asynTask.execute();
    }
}
