package com.atouchlab.socialnetwork.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.atouchlab.socialnetwork.activities.PublishActivity;
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

public class FavoritesFragment extends Fragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public View mView;
    public FloatingActionButton mFabButton;
    public Intent mIntent;
    public LinearLayoutManager layoutManager;
    int currentPage = 1;
    private RecyclerView postsList;
    private List<PostsItem> mPostsItems;
    private HomeListAdapter mHomeListAdapter;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mContext = getActivity().getApplicationContext();
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);

        initializeView();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_favorites);
        getPosts();
        return mView;
    }

    private void initializeView() {
        postsList = (RecyclerView) mView.findViewById(R.id.postsList);
        mFabButton = (FloatingActionButton) mView.findViewById(R.id.fabButton);
        mFabButton.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeHome);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //layout manager
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        postsList.setLayoutManager(layoutManager);
        mPostsItems = new ArrayList<PostsItem>();
        mHomeListAdapter = new HomeListAdapter(getActivity(), mPostsItems);

        postsList.setAdapter(mHomeListAdapter);

        postsList.setOnScrollListener(new HidingScrollListener(layoutManager) {

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
    }

    public void getPosts() {
        M.showLoadingDialog(getActivity());
        PostsAPI mPostsAPI = APIService.createService(PostsAPI.class, M.getToken(getActivity()));

        mPostsAPI.getFavourites(getCurrentPage(), new Callback<List<PostsItem>>() {
            @Override
            public void success(List<PostsItem> postsItems, retrofit.client.Response response) {
                updateView(postsItems);
            }

            @Override
            public void failure(RetrofitError error) {
                M.T(getActivity(), getString(R.string.ServerError));
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
        M.hideLoadingDialog();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabButton) {
            mIntent = new Intent(getActivity(), PublishActivity.class);
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
