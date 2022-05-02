package com.atouchlab.socialnetwork.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.FollowingListAdapter;
import com.atouchlab.socialnetwork.animation.HidingScrollListener;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.data.FollowingItem;
import com.atouchlab.socialnetwork.helpers.M;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

public class FollowingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public LinearLayoutManager layoutManager;
    private View mView;
    private RecyclerView followsList;
    private List<FollowingItem> mFollowingItems;
    private FollowingListAdapter mFollowingListAdapter;
    public Intent mIntent;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mContext = getActivity().getApplicationContext();
        mView = inflater.inflate(R.layout.fragment_following, container, false);
        initializeView();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_following);


        getFollowing();
        return mView;
    }

    private void initializeView() {
        followsList = (RecyclerView) mView.findViewById(R.id.followingList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeFollowing);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFollowingItems = new ArrayList<FollowingItem>();
        mFollowingListAdapter = new FollowingListAdapter(getActivity(),
                mFollowingItems);
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        followsList.setLayoutManager(layoutManager);
        followsList.setAdapter(mFollowingListAdapter);
        followsList.setOnScrollListener(new HidingScrollListener(layoutManager) {
            @Override
            public void onHide() {

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onLoadMore(int current_page) {

            }
        });
    }

    public void getFollowing() {
        M.showLoadingDialog(getActivity());
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(getActivity()));
        mUsersAPI.getFollowing(new Callback<List<FollowingItem>>() {
            @Override
            public void success(List<FollowingItem> followingItems, retrofit.client.Response response) {

                mFollowingListAdapter.setFollowing(followingItems);
                M.hideLoadingDialog();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
            }
        });
    }

    @Override
    public void onRefresh() {

        getFollowing();
    }
}
