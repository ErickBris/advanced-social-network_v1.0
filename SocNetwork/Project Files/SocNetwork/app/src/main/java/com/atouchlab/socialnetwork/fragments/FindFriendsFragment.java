package com.atouchlab.socialnetwork.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.FriendsAdapter;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.M;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FindFriendsFragment extends Fragment implements
        OnClickListener {
    public RecyclerView friendsList;
    public LinearLayoutManager layoutManager;
    public List<userItem> mFriends = new ArrayList<userItem>();
    public FriendsAdapter mFriendsAdapter;
    public Intent mIntent = null;
    public EditText searchField;
    public ImageButton searchBtn;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_find_friends, container,
                false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_find_people);

        initializeView();
        search("suggestions");
        return mView;
    }

    public void initializeView() {
        friendsList = (RecyclerView) mView.findViewById(R.id.friendsList);
        searchField = (EditText) mView.findViewById(R.id.searchField);
        searchBtn = (ImageButton) mView.findViewById(R.id.searchBtn);
        //layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFriendsAdapter = new FriendsAdapter(getActivity(), mFriends);
        friendsList.setLayoutManager(layoutManager);
        friendsList.setAdapter(mFriendsAdapter);//set the adapter
        searchBtn.setOnClickListener(this);
    }

    public void search(String value) {
        M.showLoadingDialog(getActivity());
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(getActivity()));
        mUsersAPI.search(value, new Callback<List<userItem>>() {
            @Override
            public void success(List<userItem> userItems, Response response) {
                mFriendsAdapter.setUsers(userItems);
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
        if (v.getId() == R.id.searchBtn) {
            String searchValue = searchField.getText().toString().trim();
            if (searchValue.length() <= 2) {
                M.T(getActivity(), "Search Value is too short");
            } else {
                search(searchValue);
            }
        }
    }
}
