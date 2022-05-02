package com.atouchlab.socialnetwork.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.atouchlab.socialnetwork.activities.MessagingActivity;
import com.atouchlab.socialnetwork.activities.PostComments;
import com.atouchlab.socialnetwork.activities.ProfilePreview;
import com.atouchlab.socialnetwork.helpers.M;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

/**
 * Created by SmartCoder on 23/03/2015.
 */
public class GCMNotificationIntentService extends IntentService {
    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (! extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                parseRequest(extras);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void parseRequest(Bundle extras) {
        if (extras.containsKey("for") && extras.containsKey("recipientID")) {
            if (Integer.parseInt(extras.getString("recipientID")) == M.getID(this)) {
                switch (extras.getString("for")) {
                    case "chat":
                        if (isRunning("MessagingActivity")) {
                            Intent intent = new Intent("update_messages_list");
                            intent.putExtra("data", extras);
                            sendBroadcast(intent);
                        } else {
                            Intent resultIntent = new Intent(this, MessagingActivity.class);
                            resultIntent.putExtra("conversationID", Integer.parseInt(extras.getString("conversationID")));
                            resultIntent.putExtra("recipientID", Integer.parseInt(extras.getString("ownerID")));
                            M.showNotification(getApplicationContext(), resultIntent,
                                    extras.getString("ownerUsername"),
                                    extras.getString("message"),
                                    Integer.parseInt(extras.getString("conversationID")));
                        }
                        break;
                    case "following":
                        Intent resultIntent = new Intent(this, ProfilePreview.class);
                        resultIntent.putExtra("userID", Integer.parseInt(extras.getString("userID")));
                        M.showNotification(this,
                                resultIntent,
                                "New Follower",
                                extras.getString("username") + " Started following you",
                                Integer.parseInt(extras.getString("userID")));
                        break;
                    case "comment":
                        Intent commentIntent = new Intent(this, PostComments.class);
                        commentIntent.putExtra("postID", Integer.parseInt(extras.getString("postID")));
                        M.showNotification(this,
                                commentIntent,
                                "New Comment",
                                extras.getString("username") + " Commented to your post",
                                Integer.parseInt(extras.getString("postID")));
                        break;
                }
            }
        }
    }

    public boolean isRunning(String activityName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(2);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if ((getPackageName() + ".activities." + activityName).equals(task.topActivity.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
