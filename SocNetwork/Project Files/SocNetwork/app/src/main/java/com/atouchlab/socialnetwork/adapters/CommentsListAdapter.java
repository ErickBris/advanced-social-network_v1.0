package com.atouchlab.socialnetwork.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.CommentsAPI;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.CommentsItem;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<CommentsItem> mCommentsItem;

    public CommentsListAdapter(Activity mActivity,
                               List<CommentsItem> mCommentsItem) {
        this.mActivity = mActivity;
        this.mCommentsItem = mCommentsItem;
        this.mInflater = LayoutInflater.from(mActivity);
    }

    public void setComments(List<CommentsItem> mCommentsItem) {
        this.mCommentsItem = mCommentsItem;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentsViewHolder(mInflater.inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentsItem comment = mCommentsItem.get(position);
        CommentsViewHolder mCommentsViewHolder = (CommentsViewHolder) holder;
        if(comment.getOwnerName() != null){
            mCommentsViewHolder.name.setText(comment.getOwnerName());
        }else{
            mCommentsViewHolder.name.setText(comment.getOwnerUsername());
        }
        mCommentsViewHolder.text.setText(comment.getComment());
        mCommentsViewHolder.date.setText(comment.getDate());
        Picasso.with(mActivity)
                .load(AppConst.IMAGE_PROFILE_URL + comment.getOwnerPicture())
                .transform(new CropSquareTransformation())
                .placeholder(R.drawable.image_holder)
                .error(R.drawable.image_holder)
                .into(mCommentsViewHolder.picture);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mCommentsItem.size();
    }

    private class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, date, text;
        ImageView picture;
        ImageButton commentOptions;

        CommentsViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            date = (TextView) v.findViewById(R.id.date);
            text = (TextView) v.findViewById(R.id.text);
            picture = (ImageView) v.findViewById(R.id.picture);
            commentOptions = (ImageButton) v.findViewById(R.id.commentOptions);
            commentOptions.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int commentPosition = this.getPosition();
            final CommentsItem comment = mCommentsItem.get(commentPosition);
            final CommentsAPI mCommentsAPI = APIService.createService(CommentsAPI.class, M.getToken(mActivity));
            switch (v.getId()) {
                case R.id.commentOptions:
                    PopupMenu popup = new PopupMenu(mActivity, v);
                    if ((comment.getOwnerID() + "").equals(M.getID(mActivity) + "")) {
                        popup.getMenuInflater()
                                .inflate(R.menu.own_comment_options, popup.getMenu());
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    mCommentsAPI.deleteComment(comment.getId(), new Callback<ResponseModel>() {
                                        @Override
                                        public void success(ResponseModel responseModel, Response response) {
                                            if (responseModel.isDone()) {
                                                mCommentsItem.remove(commentPosition);
                                                notifyItemRemoved(commentPosition);
                                            } else {
                                                M.T(mActivity, responseModel.getMessage());
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            M.T(mActivity, mActivity.getString(R.string.ServerError));
                                        }
                                    });
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
            }
        }
    }
}
