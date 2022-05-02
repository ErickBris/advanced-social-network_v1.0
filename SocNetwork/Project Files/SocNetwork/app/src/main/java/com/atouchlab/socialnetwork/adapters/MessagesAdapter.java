package com.atouchlab.socialnetwork.adapters;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.FileModel;
import com.atouchlab.socialnetwork.data.MessagesItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.M;
import com.atouchlab.socialnetwork.helpers.Utilities;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    private List<MessagesItem> mMessages;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private MediaPlayer mediaPlayer;
    private Utilities utils;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();


    public void stop() {
        mediaPlayer.release();
    }

    public MessagesAdapter(Activity mActivity, List<MessagesItem> mMessages) {
        this.mMessages = mMessages;
        this.mActivity = mActivity;
        this.mInflater = LayoutInflater.from(mActivity);
        mediaPlayer = new MediaPlayer();
        utils = new Utilities();

    }

    @Override
    public int getItemViewType(int position) {
        MessagesItem messageItem = mMessages.get(position);
        if (messageItem.getOwnerID() == M.getID(mActivity)) {
            return DIRECTION_OUTGOING;
        } else {
            return DIRECTION_INCOMING;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == DIRECTION_OUTGOING) {
            view = mInflater.inflate(R.layout.messages_bubble_right, parent, false);
        } else {
            view = mInflater.inflate(R.layout.messages_bubble_left, parent, false);
        }
        if (view != null) {
            MessagesViewHolder holder = new MessagesViewHolder(view);
            return holder;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MessagesItem messagesItem = mMessages.get(position);
        MessagesViewHolder mMessagesViewHolder = (MessagesViewHolder) holder;
        mMessagesViewHolder.message.setText(messagesItem.getMessage());
        Picasso.with(mActivity)
                .load(AppConst.IMAGE_PROFILE_URL + messagesItem.getOwnerPicture())
                .transform(new CropSquareTransformation())
                .into(mMessagesViewHolder.senderPicture);
        if (messagesItem.getFile() != null) {
            if (messagesItem.getFile().getType().equals("image")) {
                mMessagesViewHolder.imageFile.setVisibility(View.VISIBLE);
                Picasso.with(mActivity)
                        .load(AppConst.IMAGE_FILE_URL + messagesItem.getFile().getHash())
                        .into(mMessagesViewHolder.imageFile);
            } else if (messagesItem.getFile().getType().equals("audio")) {
                mMessagesViewHolder.audioLayout.setVisibility(View.VISIBLE);


            } else {
                mMessagesViewHolder.audioLayout.setVisibility(View.GONE);
                mMessagesViewHolder.imageFile.setVisibility(View.GONE);
            }
        } else {
            mMessagesViewHolder.audioLayout.setVisibility(View.GONE);
            mMessagesViewHolder.imageFile.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mMessages != null) {
            return mMessages.size();
        } else {
            return 0;
        }
    }

    public void setMessages(List<MessagesItem> messageItems) {
        this.mMessages = messageItems;
        notifyDataSetChanged();
    }

    private class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        TextView message, audioCurrentDurationLabel, audioTotalDurationLabel;
        ImageButton btnPlay;
        ImageView senderPicture, imageFile;
        SeekBar audioProgressBar;
        LinearLayout audioLayout;

        MessagesViewHolder(View v) {
            super(v);
            senderPicture = (ImageView) v.findViewById(R.id.senderPicture);
            imageFile = (ImageView) v.findViewById(R.id.imageFile);
            message = (TextView) v.findViewById(R.id.message_text);
            audioLayout = (LinearLayout) v.findViewById(R.id.audioLayout);
            btnPlay = (ImageButton) v.findViewById(R.id.btnPlay);
            audioProgressBar = (SeekBar) v.findViewById(R.id.songProgressBar);
            audioCurrentDurationLabel = (TextView) v.findViewById(R.id.songCurrentDurationLabel);
            audioTotalDurationLabel = (TextView) v.findViewById(R.id.songTotalDurationLabel);
            btnPlay.setOnClickListener(this);
            audioProgressBar.setOnSeekBarChangeListener(this);
        }

        @Override
        public void onClick(View v) {


            // check for already playing
            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                // Changing button image to play button
                btnPlay.setImageResource(R.drawable.btn_play);
                // set Progress bar values
                audioProgressBar.setProgress(0);
                audioProgressBar.setMax(100);
                M.L("is paused");
                // Updating progress bar
                updateProgressBar();

            } else {

                // Updating progress bar
                updateProgressBar();

                // Resume song
                if (mediaPlayer != null) {

                    // Set data source -
                    try {

                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        FileModel file = mMessages.get(this.getPosition()).getFile();
                        mediaPlayer.setDataSource(AppConst.AUDIO_FILE_URL + file.getHash());
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer player) {
                                player.start();

                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();

                    // Changing button image to pause button
                    btnPlay.setImageResource(R.drawable.btn_pause);
                }
            }
        }

        public void updateProgressBar() {
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }


        /**
         * Background Runnable thread
         */
        private Runnable mUpdateTimeTask = new Runnable() {
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    long totalDuration = mediaPlayer.getDuration();
                    long currentDuration = mediaPlayer.getCurrentPosition();

                    // Displaying Total Duration time
                    audioTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    audioCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                    //Log.d("Progress", ""+progress);
                    audioProgressBar.setProgress(progress);

                    // Running this thread after 100 milliseconds
                    mHandler.postDelayed(this, 100);
                }
            }
        };

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // remove message Handler from updating progress bar
            mHandler.removeCallbacks(mUpdateTimeTask);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            mediaPlayer.seekTo(currentPosition);

            // update timer progress again
            updateProgressBar();
        }
    }
}
