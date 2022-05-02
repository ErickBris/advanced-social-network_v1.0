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
import com.atouchlab.socialnetwork.data.FollowingItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity mActivity;
    private List<FollowingItem> mFollowingItem;
    private LayoutInflater mInflater;

    public FollowingListAdapter(Activity mActivity,
                                List<FollowingItem> mFollowingItems) {
        this.mActivity = mActivity;
        this.mFollowingItem = mFollowingItems;

        mInflater = LayoutInflater.from(mActivity);
    }

    public void setFollowing(List<FollowingItem> followingList) {
        this.mFollowingItem = followingList;
        notifyDataSetChanged();
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
            View view = mInflater.inflate(R.layout.following_item, parent, false);
            FollowingViewHolder holder = new FollowingViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FollowingViewHolder) {
            FollowingItem item = mFollowingItem.get(position - 1);
            FollowingViewHolder followingHolder = (FollowingViewHolder) holder;
            if (item.getName() != null) {
                followingHolder.name.setText(item.getName());
            } else {
                followingHolder.name.setText(item.getUsername());
            }
            followingHolder.job.setText(item.getJob());
            Picasso.with(mActivity.getApplicationContext())
                    .load(AppConst.IMAGE_PROFILE_URL + item.getPicture())
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.image_holder)
                    .into(followingHolder.picture);
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mFollowingItem != null){
            return mFollowingItem.size() + 1;
        }else{
            return 1;
        }
    }

    private class FollowingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, job;
        ImageView picture;

        FollowingViewHolder(View v) {
            super(v);
            this.name = (TextView) v.findViewById(R.id.name);
            this.job = (TextView) v.findViewById(R.id.job);
            this.picture = (ImageView) v.findViewById(R.id.picture);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(mActivity, ProfilePreview.class);
            FollowingItem item = mFollowingItem.get(this.getPosition() - 1);
            mIntent.putExtra("userID", item.getId());
            mActivity.startActivity(mIntent);
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
