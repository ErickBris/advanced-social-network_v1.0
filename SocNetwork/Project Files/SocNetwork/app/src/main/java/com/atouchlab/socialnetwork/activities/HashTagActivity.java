package com.atouchlab.socialnetwork.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.HomeListAdapter;
import com.atouchlab.socialnetwork.animation.HidingScrollListener;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.PostsAPI;
import com.atouchlab.socialnetwork.data.PostsItem;
import com.atouchlab.socialnetwork.helpers.M;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Ben Cherif on 09/06/2015.
 */
public class HashTagActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public RecyclerView hashTagList;
    public FloatingActionButton mFabButton;
    public Intent mIntent;
    public LinearLayoutManager layoutManager;
    int currentPage = 1;
    private List<PostsItem> mPostsItems;
    private HomeListAdapter mHomeListAdapter;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private  String hashtag = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);
        initializeView();
        if (getIntent().hasExtra("hashtag")){
            hashtag = getIntent().getExtras().getString("hashtag");
        }
        getPosts();

    }

    private void initializeView() {
        hashTagList = (RecyclerView) findViewById(R.id.hashTagList);
        mFabButton = (FloatingActionButton) findViewById(R.id.fabButton);
        mFabButton.setOnClickListener(this);
        mFabButton.setRippleColor(this.getResources().getColor(R.color.accentColor));
        //layout manager
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hashTagList.setLayoutManager(layoutManager);
        mPostsItems = new ArrayList<PostsItem>();
        mHomeListAdapter = new HomeListAdapter(this, mPostsItems);
        hashTagList.setAdapter(mHomeListAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeHashTag);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //setting up our OnScrollListener
        hashTagList.setOnScrollListener(new HidingScrollListener(layoutManager) {
            @Override
            public void onHide() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                mFabButton.animate().translationY(mFabButton.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            public void onShow() {
                mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void onLoadMore(int currentPage) {
                setCurrentPage(currentPage);
                getPosts();
            }
        });
        //including toolbar  and enabling the home button
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_hashtag));
    }

    @Override
    public void onDestroy() {
        super.onResume();
        M.hideLoadingDialog();
    }

    public void getPosts() {
        M.showLoadingDialog(this);
        PostsAPI mPostsAPI = APIService.createService(PostsAPI.class, M.getToken(this));

        mPostsAPI.getPosts(hashtag,getCurrentPage(), new Callback<List<PostsItem>>() {
            @Override
            public void success(List<PostsItem> postsItems, retrofit.client.Response response) {
                updateView(postsItems);
            }

            @Override
            public void failure(RetrofitError error) {
                M.T(HashTagActivity.this, getString(R.string.ServerError));
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
        M.hideLoadingDialog();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabButton) {
            mIntent = new Intent(HashTagActivity.this, PublishActivity.class);
            startActivity(mIntent);
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

