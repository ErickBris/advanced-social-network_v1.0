package com.atouchlab.socialnetwork.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfilePreview extends Activity implements View.OnClickListener {
    public TextView userProfileName,
            userTotalFollowers,
            userTotalPosts,
            userProfileAddress,
            userProfileJob,
            followProfileBtn,
            contactProfileBtn,
            userPostsBtn;
    public userItem user;
    public ImageView userProfilePicture, userProfileCover;
    public LinearLayout actionProfileArea;
    public int userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (M.getToken(this) == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();

        }else {
            setContentView(R.layout.activity_profile_preview);
            initializeView();
            if (getIntent().hasExtra("userID")) {
                userID = getIntent().getExtras().getInt("userID");
            }
            getUser();
        }

    }

    private void initializeView() {
        userProfileName = (TextView) findViewById(R.id.userProfileName);
        userProfilePicture = (ImageView) findViewById(R.id.userProfilePicture);
        userTotalFollowers = (TextView) findViewById(R.id.userTotalFollowers);
        userTotalPosts = (TextView) findViewById(R.id.userTotalPosts);
        followProfileBtn = (TextView) findViewById(R.id.followProfileBtn);
        contactProfileBtn = (TextView) findViewById(R.id.contactProfileBtn);
        actionProfileArea = (LinearLayout) findViewById(R.id.actionProfileArea);
        userProfileAddress = (TextView) findViewById(R.id.userProfileAddress);
        userProfileJob = (TextView) findViewById(R.id.userProfileJob);
        userProfileCover = (ImageView) findViewById(R.id.userProfileCover);
        userPostsBtn = (TextView) findViewById(R.id.userPostsBtn);
        userPostsBtn.setOnClickListener(this);
        followProfileBtn.setOnClickListener(this);
        contactProfileBtn.setOnClickListener(this);
    }

    private void getUser() {
        M.showLoadingDialog(this);
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(this));
        mUsersAPI.getUser(userID, new Callback<userItem>() {
            @Override
            public void success(userItem userItem, retrofit.client.Response response) {
                user = userItem;
                updateView();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.T(ProfilePreview.this, getString(R.string.ServerError));
            }
        });
    }

    private void updateView() {

        if (user.isMine()) {
            actionProfileArea.setVisibility(View.GONE);
        } else {
            actionProfileArea.setVisibility(View.VISIBLE);
        }
        if (user.isFollowed()) {
            followProfileBtn.setText(getString(R.string.UnFollow));
            contactProfileBtn.setVisibility(View.VISIBLE);
        } else {
            followProfileBtn.setText(getString(R.string.Follow));
            contactProfileBtn.setVisibility(View.GONE);
        }

        if (user.getName() != null) {
            userProfileName.setText(user.getName());
        } else {
            userProfileName.setText(user.getUsername());
        }
        if (user.getAddress() != null) {
            userProfileAddress.setVisibility(View.VISIBLE);
            userProfileAddress.setText(user.getAddress());
        } else {
            userProfileAddress.setVisibility(View.GONE);
        }
        if (user.getJob() != null){
            userProfileJob.setVisibility(View.VISIBLE);
            userProfileJob.setText(user.getJob());
        }else{
            userProfileJob.setVisibility(View.GONE);
        }
        Picasso.with(this)
                .load(AppConst.IMAGE_PROFILE_URL + user.getPicture())
                .transform(new CropSquareTransformation())
                .placeholder(R.drawable.image_holder)
                .error(R.drawable.image_holder)
                .into(userProfilePicture);
        if (user.getCover() != null) {
            Picasso.with(this)
                    .load(AppConst.IMAGE_COVER_URL + user.getCover())
                    .placeholder(R.drawable.header)
                    .error(R.drawable.header)
                    .into(userProfileCover);
        } else {
            Picasso.with(this)
                    .load(R.drawable.header)
                    .into(userProfileCover);
        }
        userTotalFollowers.setText(user.getTotalFollowers() + "");
        userTotalPosts.setText(user.getTotalPosts() + "");
        M.hideLoadingDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.followProfileBtn:
                UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(this));
                mUsersAPI.followToggle(userID, new Callback<ResponseModel>() {
                    @Override
                    public void success(ResponseModel responseModel, Response response) {
                        if (responseModel.isDone()) {
                            if (user.isFollowed()) {
                                user.setFollowed(false);
                                followProfileBtn.setText(getString(R.string.Follow));
                                contactProfileBtn.setVisibility(View.GONE);
                            } else {
                                user.setFollowed(true);
                                followProfileBtn.setText(getString(R.string.UnFollow));
                                contactProfileBtn.setVisibility(View.VISIBLE);
                            }
                        } else {
                            M.T(ProfilePreview.this, responseModel.getMessage());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        M.T(ProfilePreview.this, getString(R.string.ServerError));
                    }
                });
                break;
            case R.id.contactProfileBtn:
                Intent messagingIntent = new Intent(this, MessagingActivity.class);
                messagingIntent.putExtra("conversationID", 0);
                messagingIntent.putExtra("recipientID", userID);
                startActivity(messagingIntent);
                break;
            case R.id.userPostsBtn:
                Intent userPostsIntent = new Intent(this, UserPosts.class);
                userPostsIntent.putExtra("userID", userID);
                startActivity(userPostsIntent);
                finish();
                break;
        }
    }
}
