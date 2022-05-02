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
import com.atouchlab.socialnetwork.activities.ProfilePreview;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity mActivity;
    private List<userItem> mFriends;
    private LayoutInflater mInflater;

    public FriendsAdapter(Activity mActivity, List<userItem> mFriends) {
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
        this.mFriends = mFriends;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View view = mInflater.inflate(R.layout.home_header, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            return holder;
        } else {
            View view = mInflater.inflate(R.layout.friends_list_item, parent, false);
            FindHolder holder = new FindHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FindHolder) {

            userItem item = mFriends.get(position - 1);
            FindHolder findHolder = (FindHolder) holder;
            if (item.getName() != null) {
                findHolder.username.setText(item.getName());
            } else {
                findHolder.username.setText(item.getUsername());
            }
            findHolder.status.setText(item.getJob());
            Picasso.with(mActivity.getApplicationContext())
                    .load(AppConst.IMAGE_PROFILE_URL + item.getPicture())
                    .transform(new CropSquareTransformation())
                    .error(R.drawable.image_holder)
                    .placeholder(R.drawable.image_holder)
                    .into(findHolder.picture);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            if (AppConst.SHOW_ADS) {
                headerHolder.mAdView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                headerHolder.mAdView.loadAd(adRequest);
            } else {
                headerHolder.mAdView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mFriends != null) {
            return mFriends.size() + 1;
        } else {
            return 0;
        }
    }

    public void setUsers(List<userItem> userItems) {
        this.mFriends = userItems;
        notifyDataSetChanged();
    }

    public class FindHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username, status;
        ImageView picture;

        public FindHolder(View v) {
            super(v);
            picture = (ImageView) v.findViewById(R.id.picture);
            username = (TextView) v.findViewById(R.id.username);
            status = (TextView) v.findViewById(R.id.status);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            userItem item = mFriends.get(this.getPosition() - 1);
            Intent FindIntent = new Intent(mActivity, ProfilePreview.class);
            FindIntent.putExtra("userID", item.getId());
            mActivity.startActivity(FindIntent);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public AdView mAdView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mAdView = (AdView) itemView.findViewById(R.id.adView);
        }
    }
}