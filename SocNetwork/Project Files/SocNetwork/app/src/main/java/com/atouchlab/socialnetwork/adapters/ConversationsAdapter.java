package com.atouchlab.socialnetwork.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.activities.MessagingActivity;
import com.atouchlab.socialnetwork.activities.ProfilePreview;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.ConversationItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity mActivity;
    private List<ConversationItem> mConversations;
    private LayoutInflater mInflater;

    public ConversationsAdapter(Activity mActivity,
                                List<ConversationItem> mConversations) {
        this.mActivity = mActivity;
        this.mConversations = mConversations;

        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setConversations(List<ConversationItem> mConversationsList) {
        this.mConversations = mConversationsList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View view = mInflater.inflate(R.layout.home_header, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            return holder;
        } else {
            View view = mInflater.inflate(R.layout.conversation_list_item, parent, false);
            ConversationsViewHolder holder = new ConversationsViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ConversationsViewHolder) {
            ConversationsViewHolder conversationsHolder = (ConversationsViewHolder) holder;
            ConversationItem item = mConversations.get(position - 1);
            if(item.getRecpientName() != null){
                conversationsHolder.username.setText(item.getRecpientName());
            }else{
                conversationsHolder.username.setText(item.getRecpientUsername());
            }
            conversationsHolder.lastMessage.setText(item.getLastMessage());
            conversationsHolder.date.setText(item.getDate());
            Picasso.with(mActivity.getApplicationContext())
                    .load(AppConst.IMAGE_PROFILE_URL + item.getRecpientPicture())
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.image_holder)
                    .into(conversationsHolder.picture);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            if(AppConst.SHOW_ADS){
                headerHolder.mAdView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                headerHolder.mAdView.loadAd(adRequest);
            }else{
                headerHolder.mAdView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mConversations.size() + 1;
    }

    private class ConversationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username, lastMessage, unseenCount, date;
        ImageView picture;

        ConversationsViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.username);
            date = (TextView) v.findViewById(R.id.date);
            lastMessage = (TextView) v.findViewById(R.id.lastMessage);
            picture = (ImageView) v.findViewById(R.id.picture);
            picture.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ConversationItem item = mConversations.get(this.getPosition() - 1);
            if (v.getId() == R.id.picture) {
                Intent mIntent = new Intent(mActivity, ProfilePreview.class);
                mIntent.putExtra("userID", item.getRecpientID());
                mActivity.startActivity(mIntent);
            } else {
                Intent messagingIntent = new Intent(mActivity, MessagingActivity.class);
                messagingIntent.putExtra("conversationID", item.getConversationID());
                messagingIntent.putExtra("recipientID", item.getRecpientID());
                mActivity.startActivity(messagingIntent);
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public AdView mAdView;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            mAdView= (AdView) itemView.findViewById(R.id.adView);
        }
    }
}
