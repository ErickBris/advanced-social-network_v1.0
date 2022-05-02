package com.atouchlab.socialnetwork.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.activities.FullScreenImageViewActivity;
import com.atouchlab.socialnetwork.activities.MapPreveiw;
import com.atouchlab.socialnetwork.activities.PostComments;
import com.atouchlab.socialnetwork.activities.ProfilePreview;
import com.atouchlab.socialnetwork.activities.YoutubeVideoPlayer;
import com.atouchlab.socialnetwork.animation.AnimUtils;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.PostsAPI;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.PostsItem;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.HashTag;
import com.atouchlab.socialnetwork.helpers.M;
import com.atouchlab.socialnetwork.helpers.Mention;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<PostsItem> mPostsItems;
    private Intent mIntent;
    private int mPreviousPosition = 0;

    public HomeListAdapter(Activity mActivity, List<PostsItem> mPostsItems) {
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
        this.mPostsItems = mPostsItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public List<PostsItem> getPosts() {
        return this.mPostsItems;
    }

    public void setPosts(List<PostsItem> postsItems) {
        this.mPostsItems = postsItems;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View view = mInflater.inflate(R.layout.home_header, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            return holder;
        } else {
            View view = mInflater.inflate(R.layout.posts_item, parent, false);
            ViewHolderPosts holder = new ViewHolderPosts(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderPosts) {
            PostsItem item = mPostsItems.get(position - 1);
            final ViewHolderPosts postsHolder = (ViewHolderPosts) holder;
            if (item.getOwnerName() != null) {
                postsHolder.name.setText(item.getOwnerName());
            } else {
                postsHolder.name.setText(item.getOwnerUsername());
            }
            postsHolder.likesNum.setText(item.getLikes() + "");
            //postsHolder.viewsNum.setText(item.getViews() + "");
            postsHolder.timestamp.setText(item.getDate());
            if (item.getImage() != null && item.getLink() != null) {
                if (item.getStatus() != null) {
                    String newStatus;
                    if (item.getLink().getType().equals("youtube")) {
                        newStatus = item.getStatus() + " " +
                                "<a href=\"https://www.youtube.com/watch?v=" + item.getLink().getLink() + "\">" + item.getLink().getTitle() + "</a>";
                    } else {
                        newStatus = item.getStatus() + " " +
                                "<a href=\"" + item.getLink().getLink() + "\">" + item.getLink().getTitle() + "</a>";
                    }
                    item.setStatus(newStatus);
                } else {
                    item.setStatus(item.getLink().getTitle() + " " + item.getLink().getLink());
                }
                item.setLink(null);
            }
            if (item.getStatus() != null) {
                SpannableString statusContent =
                        new SpannableString(item.getStatus());
                ArrayList<int[]> hashTagSpans = getSpans(item.getStatus(), '#');
                ArrayList<int[]> mentionSpans = getSpans(item.getStatus(), '@');
                for (int i = 0; i < hashTagSpans.size(); i++) {
                    int[] span = hashTagSpans.get(i);
                    int hashTagStart = span[0];
                    int hashTagEnd = span[1];
                    statusContent.setSpan(new HashTag(mActivity),
                            hashTagStart,
                            hashTagEnd, 0);

                }
                for (int i = 0; i < mentionSpans.size(); i++) {
                    int[] span = mentionSpans.get(i);
                    int mentionStart = span[0];
                    int mentionEnd = span[1];

                    statusContent.setSpan(new Mention(mActivity),
                            mentionStart,
                            mentionEnd, 0);

                }
                postsHolder.statusMsg.setText(statusContent);
                postsHolder.statusMsg.setMovementMethod(LinkMovementMethod.getInstance());
                postsHolder.statusMsg.setVisibility(View.VISIBLE);
            } else {
                postsHolder.statusMsg.setVisibility(View.GONE);
            }

            if (item.isLiked()) {
                postsHolder.likeBtn.setBackground(mActivity.getResources().getDrawable(
                        R.drawable.bg_unlike_button));
            } else {
                postsHolder.likeBtn.setBackground(mActivity.getResources().getDrawable(
                        R.drawable.bg_like_button));
            }
            Picasso.with(mActivity.getApplicationContext())
                    .load(AppConst.IMAGE_PROFILE_URL + item.getOwnerPicture())
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.image_holder)
                    .into(postsHolder.profilePic);
            if (item.getImage() != null) {
                postsHolder.feedImageView.setVisibility(View.VISIBLE);
                Picasso.with(mActivity.getApplicationContext())
                        .load(AppConst.IMAGE_URL + item.getImage())
                        .placeholder(R.drawable.image_holder)
                        .error(R.drawable.image_holder)
                        .into(postsHolder.feedImageView);
            } else {
                postsHolder.feedImageView.setVisibility(View.GONE);
            }
            if (item.getPlace() != null) {
                postsHolder.placePreviewLayout.setVisibility(View.VISIBLE);
                postsHolder.placeValuePreview.setText(item.getPlace());
            } else {
                postsHolder.placePreviewLayout.setVisibility(View.GONE);
            }
            if (item.getLink() != null) {
                if (item.getLink().getType().equals("youtube")) {
                    postsHolder.linkPreview.setVisibility(View.GONE);
                    postsHolder.youtubePreview.setVisibility(View.VISIBLE);
                    postsHolder.youtubeTitlePreview.setText(item.getLink().getTitle());
                    postsHolder.youtubeUrlPreview.setText("Author: " + item.getLink().getDesc());
                    Picasso.with(mActivity)
                            .load(item.getLink().getImage())
                            .placeholder(R.drawable.image_holder)
                            .error(R.drawable.image_holder)
                            .into(postsHolder.youtubeImagePreview);
                } else {
                    postsHolder.youtubePreview.setVisibility(View.GONE);
                    postsHolder.linkPreview.setVisibility(View.VISIBLE);
                    postsHolder.linkTitlePreview.setText(item.getLink().getTitle());
                    postsHolder.linkUrlPreview.setText(item.getLink().getLink());
                    if (item.getLink().getImage() != null) {
                        Picasso.with(mActivity)
                                .load(item.getLink().getImage())
                                .placeholder(R.drawable.image_holder)
                                .error(R.drawable.image_holder)
                                .into(postsHolder.linkImagePreview);
                    } else {
                        Picasso.with(mActivity)
                                .load(R.drawable.image_holder)
                                .into(postsHolder.linkImagePreview);
                    }
                }
            } else {
                postsHolder.linkPreview.setVisibility(View.GONE);
                postsHolder.youtubePreview.setVisibility(View.GONE);
            }
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

        if (position > mPreviousPosition) {
            AnimUtils.animate(holder, true);
        } else {
            AnimUtils.animate(holder, false);
        }
        mPreviousPosition = position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mPostsItems != null)
            return mPostsItems.size() + 1;
        else
            return 1;
    }

    public ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return spans;
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name,
                timestamp,
                statusMsg,
                likesNum,
                viewsNum,
                postNewStatus,
                linkTitlePreview,
                youtubeTitlePreview,
                linkUrlPreview,
                youtubeUrlPreview,
                placeValuePreview;
        ImageView profilePic,
                feedImageView,
                linkImagePreview,
                youtubeImagePreview;
        ImageButton shareBtn, likeBtn, options, postSaveBtn;
        LinearLayout editPostSection;
        FrameLayout linkPreview, youtubePreview;
        LinearLayout placePreviewLayout;

        public ViewHolderPosts(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.postOwnerName);
            timestamp = (TextView) itemView.findViewById(R.id.postPublishDate);
            statusMsg = (TextView) itemView.findViewById(R.id.postStatus);
            likesNum = (TextView) itemView.findViewById(R.id.likesNum);
            viewsNum = (TextView) itemView.findViewById(R.id.viewsNum);
            postNewStatus = (TextView) itemView.findViewById(R.id.postNewStatus);
            profilePic = (ImageView) itemView.findViewById(R.id.postOwnerPicture);
            feedImageView = (ImageView) itemView.findViewById(R.id.postImage);
            shareBtn = (ImageButton) itemView.findViewById(R.id.shareBtn);
            likeBtn = (ImageButton) itemView.findViewById(R.id.likeBtn);
            postSaveBtn = (ImageButton) itemView.findViewById(R.id.postSaveBtn);
            linkTitlePreview = (TextView) itemView.findViewById(R.id.linkTitlePreview);
            linkUrlPreview = (TextView) itemView.findViewById(R.id.linkUrlPreview);
            linkPreview = (FrameLayout) itemView.findViewById(R.id.linkPreview);
            linkImagePreview = (ImageView) itemView.findViewById(R.id.linkImagePreview);

            youtubeTitlePreview = (TextView) itemView.findViewById(R.id.youtubeTitlePreview);
            youtubeUrlPreview = (TextView) itemView.findViewById(R.id.youtubeUrlPreview);
            youtubePreview = (FrameLayout) itemView.findViewById(R.id.youtubePreview);
            youtubeImagePreview = (ImageView) itemView.findViewById(R.id.youtubeImagePreview);

            editPostSection = (LinearLayout) itemView.findViewById(R.id.editPostSection);
            options = (ImageButton) itemView.findViewById(R.id.postOptions);

            profilePic.setOnClickListener(this);
            name.setOnClickListener(this);
            shareBtn.setOnClickListener(this);
            likeBtn.setOnClickListener(this);
            options.setOnClickListener(this);
            postSaveBtn.setOnClickListener(this);
            linkPreview.setOnClickListener(this);
            youtubePreview.setOnClickListener(this);

            placePreviewLayout = (LinearLayout) itemView.findViewById(R.id.placePreviewLayout);
            placeValuePreview = (TextView) itemView.findViewById(R.id.placeValuePreview);
            feedImageView.setOnClickListener(this);
            placePreviewLayout.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            final int postPosition = this.getPosition() - 1;
            final PostsItem post = mPostsItems.get(postPosition);
            final PostsAPI mPostsAPI = APIService.createService(PostsAPI.class, M.getToken(mActivity));

            switch (v.getId()) {
                case R.id.postImage:
                    if (post.getImage() != null) {
                        mIntent = new Intent(mActivity, FullScreenImageViewActivity.class);
                        mIntent.putExtra("image", post.getImage());
                        mActivity.startActivity(mIntent);
                    }
                    break;
                case R.id.placePreviewLayout:
                    mIntent = new Intent(mActivity, MapPreveiw.class);
                    mIntent.putExtra("place", post.getPlace());
                    mActivity.startActivity(mIntent);
                    break;
                case R.id.shareBtn:
                    if (post.getImage() != null) {
                        Uri bmpUri = M.getLocalBitmapUri(feedImageView);
                        if (bmpUri != null) {

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            String subject = null;
                            if (post.getStatus() != null) {
                                subject = post.getStatus();
                            }
                            if (post.getLink() != null) {
                                if (subject != null) {
                                    subject = subject + " " + post.getLink().getLink();
                                } else {
                                    subject = post.getLink().getLink();
                                }
                            }
                            if (post.getPlace() != null) {
                                if (subject != null) {
                                    subject = subject + " at:" + post.getPlace();
                                } else {
                                    subject = post.getPlace();
                                }
                            }
                            if (subject != null) {
                                shareIntent.putExtra(Intent.EXTRA_TEXT, subject);
                            }
                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/*");
                            mActivity.startActivity(Intent.createChooser(shareIntent, mActivity.getString(R.string.sharePost)));
                        } else {
                            M.T(mActivity, mActivity.getString(R.string.SomethingWentWrong));
                        }
                    } else {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/*");
                        String text = null;
                        if (post.getStatus() != null) {
                            text = post.getStatus();
                        }
                        if (post.getLink() != null) {
                            if (!post.getLink().getType().equals("youtube")) {
                                if (text != null) {
                                    text = text + " " + post.getLink().getLink();
                                } else {
                                    text = post.getLink().getLink();
                                }
                            } else {
                                if (text != null) {
                                    text = text + " https://youtu.be/" + post.getLink().getLink();
                                } else {
                                    text = "https://youtu.be/" + post.getLink().getLink();
                                }
                            }

                        }
                        if (post.getPlace() != null) {
                            if (text != null) {
                                text = text + " at:" + post.getPlace();
                            } else {
                                text = post.getPlace();
                            }
                        }

                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                        mActivity.startActivity(Intent.createChooser(shareIntent, mActivity.getString(R.string.sharePost)));
                    }
                    break;
                case R.id.postOwnerPicture:
                    mIntent = new Intent(mActivity, ProfilePreview.class);
                    mIntent.putExtra("userID", post.getOwnerID());
                    mActivity.startActivity(mIntent);
                    break;

                case R.id.postOwnerName:
                    mIntent = new Intent(mActivity, ProfilePreview.class);
                    mIntent.putExtra("userID", post.getOwnerID());
                    mActivity.startActivity(mIntent);
                    break;
                case R.id.likeBtn:
                    if (post.isLiked()) {
                        mPostsAPI.unlikePost(post.getId(), new Callback<ResponseModel>() {
                            @Override
                            public void success(ResponseModel responseModel, Response response) {
                                if (responseModel.isDone()) {
                                    post.setLiked(false);
                                    int newLikes;

                                    if (post.getLikes() != 0) {
                                        newLikes = post.getLikes() - 1;
                                    } else {
                                        newLikes = 0;
                                    }

                                    post.setLikes(newLikes);
                                    notifyItemChanged(postPosition + 1);
                                }
                                M.T(mActivity, responseModel.getMessage());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                M.T(mActivity, error.getMessage());
                            }
                        });
                    } else {
                        mPostsAPI.likePost(post.getId(), new Callback<ResponseModel>() {
                            @Override
                            public void success(ResponseModel responseModel, Response response) {
                                if (responseModel.isDone()) {
                                    post.setLiked(true);
                                    int newLikes = post.getLikes() + 1;
                                    post.setLikes(newLikes);
                                    notifyItemChanged(postPosition + 1);
                                }
                                M.T(mActivity, responseModel.getMessage());
                                M.L(response.getUrl());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                M.T(mActivity, error.getMessage());
                            }
                        });
                    }
                    break;
                case R.id.postSaveBtn:
                    final String newStatus = postNewStatus.getText().toString().trim();
                    if (!newStatus.equals("")) {
                        mPostsAPI.updatePost(post.getId(), newStatus, new Callback<ResponseModel>() {
                            @Override
                            public void success(ResponseModel responseModel, Response response) {
                                if (responseModel.isDone()) {
                                    postNewStatus.setText("");
                                    editPostSection.setVisibility(View.GONE);
                                }
                                post.setStatus(newStatus);
                                statusMsg.setVisibility(View.VISIBLE);
                                notifyItemChanged(postPosition + 1);
                                M.T(mActivity, responseModel.getMessage());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                M.T(mActivity, error.getMessage());
                            }
                        });
                    } else {
                        editPostSection.setVisibility(View.GONE);
                        statusMsg.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.postOptions:
                    PopupMenu popup = new PopupMenu(mActivity, v);
                    if (post.getOwnerID() == M.getID(mActivity)) {
                        popup.getMenuInflater()
                                .inflate(R.menu.own_post_options, popup.getMenu());
                    } else {
                        popup.getMenuInflater()
                                .inflate(R.menu.post_options, popup.getMenu());
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    mPostsAPI.reportPost(post.getId(), new Callback<ResponseModel>() {
                                        @Override
                                        public void success(ResponseModel responseModel, Response response) {
                                            M.T(mActivity, responseModel.getMessage());
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            M.T(mActivity, error.getMessage());
                                        }
                                    });
                                    break;
                                case R.id.delete:
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    mPostsAPI.deletePost(post.getId(), new Callback<ResponseModel>() {
                                                        @Override
                                                        public void success(ResponseModel responseModel, Response response) {
                                                            if (responseModel.isDone()) {
                                                                M.T(mActivity, responseModel.getMessage());
                                                                mPostsItems.remove(postPosition);
                                                                notifyItemRemoved(postPosition + 1);
                                                            }
                                                            M.T(mActivity, responseModel.getMessage());
                                                        }

                                                        @Override
                                                        public void failure(RetrofitError error) {

                                                        }
                                                    });
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    builder.setMessage(mActivity.getString(R.string.Make_sure)).setPositiveButton(mActivity.getString(R.string.Yes), dialogClickListener)
                                            .setNegativeButton(mActivity.getString(R.string.No), dialogClickListener).show();
                                    break;
                                case R.id.edit:
                                    editPostSection.setVisibility(View.VISIBLE);
                                    postNewStatus.setText(post.getStatus());
                                    statusMsg.setVisibility(View.GONE);
                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();
                    break;
                case R.id.linkPreview:
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(post.getLink().getLink())));
                    break;
                case R.id.youtubePreview:
                    Intent youtubeVideoIntent = new Intent(mActivity, YoutubeVideoPlayer.class);
                    youtubeVideoIntent.putExtra("videoID", post.getLink().getLink());
                    mActivity.startActivity(youtubeVideoIntent);
                    break;
                default:
                    Intent postCommentIntent = new Intent(mActivity, PostComments.class);
                    postCommentIntent.putExtra("postID", post.getId());
                    mActivity.startActivity(postCommentIntent);
                    break;
            }
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
