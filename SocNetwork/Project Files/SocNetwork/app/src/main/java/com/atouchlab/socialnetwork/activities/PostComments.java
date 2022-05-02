package com.atouchlab.socialnetwork.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.CommentsListAdapter;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.CommentsAPI;
import com.atouchlab.socialnetwork.data.CommentsItem;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.helpers.M;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PostComments extends Activity implements View.OnClickListener {
    private int postID;
    private RecyclerView commentsList;
    public LinearLayoutManager layoutManager;
    private List<CommentsItem> mCommentsItem;
    private CommentsListAdapter mCommentsListAdapter;
    private ImageButton addCommentBtn;
    private EditText commentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        initializeView();
        postID = getIntent().getExtras().getInt("postID");
        getComments();
    }

    private void initializeView() {
        commentsList = (RecyclerView) findViewById(R.id.commentsList);
        mCommentsItem = new ArrayList<CommentsItem>();
        mCommentsListAdapter = new CommentsListAdapter(this, mCommentsItem);
        commentsList.setAdapter(mCommentsListAdapter);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentsList.setLayoutManager(layoutManager);
        addCommentBtn = (ImageButton) findViewById(R.id.addCommentBtn);
        commentField = (EditText) findViewById(R.id.commentField);
        addCommentBtn.setOnClickListener(this);
    }

    private void getComments() {
        M.showLoadingDialog(this);
        CommentsAPI mCommentsAPI = APIService.createService(CommentsAPI.class, M.getToken(this));
        mCommentsAPI.getComments(postID, new Callback<List<CommentsItem>>() {
            @Override
            public void success(List<CommentsItem> commentsItems, Response response) {
                mCommentsListAdapter.setComments(commentsItems);
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.L(getString(R.string.ServerError));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCommentBtn:
                String commentValue = commentField.getText().toString().trim();
                if (!TextUtils.isEmpty(commentValue)) {
                    addComment(commentValue);
                }
                break;
        }
    }

    private void addComment(String commentValue) {
        CommentsAPI mCommentsAPI = APIService.createService(CommentsAPI.class, M.getToken(this));
        mCommentsAPI.addComment(postID, commentValue, new Callback<ResponseModel>() {
            @Override
            public void success(ResponseModel responseModel, Response response) {
                if (responseModel.isDone()) {
                    commentField.setText("");
                    getComments();
                } else {
                    M.T(PostComments.this, responseModel.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                M.T(PostComments.this, getString(R.string.ServerError));
            }
        });
    }
}
