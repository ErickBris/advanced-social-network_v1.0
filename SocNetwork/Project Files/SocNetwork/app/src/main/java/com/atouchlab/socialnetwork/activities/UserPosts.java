package com.atouchlab.socialnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.HomeListAdapter;
import com.atouchlab.socialnetwork.animation.HidingScrollListener;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.PostsAPI;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.data.PostsItem;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.M;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

public class UserPosts extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public RecyclerView postsList;
    public Intent mIntent;
    public LinearLayoutManager layoutManager;
    int currentPage = 1;
    private List<PostsItem> mPostsItems;
    private HomeListAdapter mHomeListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);
        if (getIntent().hasExtra("userID")) {
            userID = getIntent().getExtras().getInt("userID");
        }

        initializeView();

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        postsList.setLayoutManager(layoutManager);

        mPostsItems = new ArrayList<PostsItem>();
        mHomeListAdapter = new HomeListAdapter(this, mPostsItems);
        postsList.setAdapter(mHomeListAdapter);
        getUser();
        getPosts();
    }
    private void getUser() {
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(this));
        mUsersAPI.getUser(userID, new Callback<userItem>() {
            @Override
            public void success(userItem user, retrofit.client.Response response) {
                if(user.getName() != null) {
                    getSupportActionBar().setTitle(user.getName() + "'s Posts");
                }else{
                    getSupportActionBar().setTitle(user.getUsername() + "'s Posts");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void initializeView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        postsList = (RecyclerView) findViewById(R.id.postsList);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeHome);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //setting up our OnScrollListener
        postsList.setOnScrollListener(new HidingScrollListener(layoutManager) {
            @Override
            public void onHide() {
            }

            @Override
            public void onShow() {
            }

            @Override
            public void onLoadMore(int currentPage) {
                setCurrentPage(currentPage);
                getPosts();
            }
        });
    }

    public void getPosts() {
        if (! mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        PostsAPI mPostsAPI = APIService.createService(PostsAPI.class, M.getToken(this));

        mPostsAPI.getUserPosts(userID, getCurrentPage(), new Callback<List<PostsItem>>() {
            @Override
            public void success(List<PostsItem> postsItems, retrofit.client.Response response) {
                updateView(postsItems);
            }

            @Override
            public void failure(RetrofitError error) {
                M.L(error.getUrl()+" "+ error.getMessage()+" ");
            }
        });
    }

    private void updateView(List<PostsItem> postsItems) {

        if (getCurrentPage() != 1) {
            List<PostsItem> oldItems = mHomeListAdapter.getPosts();
            oldItems.addAll(postsItems);
            mHomeListAdapter.setPosts(oldItems);
        } else {
            mHomeListAdapter.setPosts(postsItems);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        setCurrentPage(1);
        getPosts();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
