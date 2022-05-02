package com.atouchlab.socialnetwork.fragments;

import android.app.Fragment;
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
import com.atouchlab.socialnetwork.adapters.ConversationsAdapter;
import com.atouchlab.socialnetwork.animation.HidingScrollListener;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.ChatAPI;
import com.atouchlab.socialnetwork.data.ConversationItem;
import com.atouchlab.socialnetwork.helpers.M;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public RecyclerView conversationList;
    public ConversationsAdapter mConversationsAdapter;
    public List<ConversationItem> mConversations = new ArrayList<ConversationItem>();
    public Intent mIntent = null;
    public int currentPage = 1;
    public LinearLayoutManager layoutManager;
    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_messages, container, false);
        conversationList = (RecyclerView) mView
                .findViewById(R.id.conversationsList);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_messages);
        initializeView();
        conversationList.setOnScrollListener(new HidingScrollListener(layoutManager) {

            @Override
            public void onHide() {

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onLoadMore(int currentPage) {
                setCurrentPage(currentPage);
                getConversations();
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeMessages);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getConversations();
        return mView;
    }

    public void initializeView() {
        mConversationsAdapter = new ConversationsAdapter(getActivity(),
                mConversations);
        conversationList.setAdapter(mConversationsAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        conversationList.setLayoutManager(layoutManager);
    }

    public void getConversations() {
        M.showLoadingDialog(getActivity());
        ChatAPI mChatAPI = APIService.createService(ChatAPI.class, M.getToken(getActivity()));
        mChatAPI.getConversations(getCurrentPage(), new Callback<List<ConversationItem>>() {
            @Override
            public void success(List<ConversationItem> conversationItems, retrofit.client.Response response) {
                M.L(response.getBody().mimeType());
                mConversationsAdapter.setConversations(conversationItems);
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
        setCurrentPage(1);
        getConversations();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
