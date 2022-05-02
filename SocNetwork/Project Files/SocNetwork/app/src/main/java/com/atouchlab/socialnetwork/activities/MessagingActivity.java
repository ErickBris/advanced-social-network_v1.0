package com.atouchlab.socialnetwork.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.MessagesAdapter;
import com.atouchlab.socialnetwork.animation.ViewAudioProxy;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.ChatAPI;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.data.MessagesItem;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.M;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessagingActivity extends AppCompatActivity implements OnClickListener {
    public Intent mIntent = null;
    private MediaRecorder recorder = null;
    private String outFile = null;
    private TextView recordTimeText;
    private ImageButton audioSendButton;
    private View recordPanel;
    private View slideText;
    private float startedDraggingX = -1;
    private float distCanMove = dp(80);
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Timer timer;
    public int RECIPIENT_ID = 0,
            CONVERSATION_ID = 0;
    public LinearLayoutManager layoutManager;
    private EditText messageField;
    private String messageBody, USERNAME;
    private MessagesAdapter messageAdapter;
    private List<MessagesItem> mMessages = new ArrayList<MessagesItem>();
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            Bundle data = intent.getBundleExtra("data");
            if (Integer.parseInt(data.getString("ownerID")) == RECIPIENT_ID) {
                MessagesItem newMsg = new MessagesItem();
                newMsg.setOwnerName(data.getString("ownerName"));
                newMsg.setId(Integer.parseInt(data.getString("id")));
                newMsg.setMessage(data.getString("message"));
                newMsg.setDate(data.getString("date"));
                newMsg.setOwnerUsername(data.getString("ownerUsername"));
                newMsg.setOwnerPicture(data.getString("ownerPicture"));
                newMsg.setOwnerID(Integer.parseInt(data.getString("ownerID")));
                newMsg.setConversationID(Integer.parseInt(data.getString("conversationID")));

                addMessage(newMsg);
            } else {
                Intent resultIntent = new Intent(MessagingActivity.this, MessagingActivity.class);
                resultIntent.putExtra("data", intent);
                M.showNotification(MessagingActivity.this, resultIntent,
                        data.getString("ownerUsername"),
                        data.getString("message"),
                        Integer.parseInt(data.getString("conversationID")));
            }
        }
    };
    private RecyclerView messagesList;
    private LinearLayout attachLayout;
    private RelativeLayout recordPannel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (M.getToken(this) == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();
        } else {
            setContentView(R.layout.messaging);

            initializer();
            if (getIntent().hasExtra("conversationID")) {
                CONVERSATION_ID = getIntent().getExtras().getInt("conversationID");
            }
            if (getIntent().hasExtra("recipientID")) {
                RECIPIENT_ID = getIntent().getExtras().getInt("recipientID");
            }
            getUser();
            getMessages();
        }

    }

    public void initializer() {

        messagesList = (RecyclerView) findViewById(R.id.listMessages);
        messageAdapter = new MessagesAdapter(this, mMessages);
        messagesList.setAdapter(messageAdapter);
        //
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);

        messagesList.setLayoutManager(layoutManager);
        messageField = (EditText) findViewById(R.id.messageField);
        attachLayout = (LinearLayout) findViewById(R.id.attachLayout);
        recordPannel = (RelativeLayout) findViewById(R.id.record_pannel);
        recordPanel = findViewById(R.id.record_panel);
        recordTimeText = (TextView) findViewById(R.id.recording_time_text);
        slideText = findViewById(R.id.slideText);
        audioSendButton = (ImageButton) findViewById(R.id.chat_audio_send_button);
        TextView textView = (TextView) findViewById(R.id.slideToCancelTextView);
        textView.setText("Slide To Cancel");
        //including toolbar  and enabling the home button
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.attachBtn).setOnClickListener(this);
        findViewById(R.id.sendBtn).setOnClickListener(this);
        findViewById(R.id.micBtn).setOnClickListener(this);
        audioSendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                            .getLayoutParams();
                    params.leftMargin = dp(30);
                    slideText.setLayoutParams(params);
                    ViewAudioProxy.setAlpha(slideText, 1);
                    startedDraggingX = -1;
                    // startRecording();
                    startRecording();
                    audioSendButton.getParent()
                            .requestDisallowInterceptTouchEvent(true);
                    recordPanel.setVisibility(View.VISIBLE);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    startedDraggingX = -1;
                    stopRecording();
                    recordPannel.setVisibility(View.GONE);
                    // stopRecording(true);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = motionEvent.getX();
                    if (x < -distCanMove) {
                        stopRecording();
                        // stopRecording(false);
                    }
                    x = x + ViewAudioProxy.getX(audioSendButton);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                            .getLayoutParams();
                    if (startedDraggingX != -1) {
                        float dist = (x - startedDraggingX);
                        params.leftMargin = dp(30) + (int) dist;
                        slideText.setLayoutParams(params);
                        float alpha = 1.0f + dist / distCanMove;
                        if (alpha > 1) {
                            alpha = 1;
                        } else if (alpha < 0) {
                            alpha = 0;
                        }
                        ViewAudioProxy.setAlpha(slideText, alpha);
                    }
                    if (x <= ViewAudioProxy.getX(slideText) + slideText.getWidth()
                            + dp(30)) {
                        if (startedDraggingX == -1) {
                            startedDraggingX = x;
                            distCanMove = (recordPanel.getMeasuredWidth()
                                    - slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                            if (distCanMove <= 0) {
                                distCanMove = dp(80);
                            } else if (distCanMove > dp(80)) {
                                distCanMove = dp(80);
                            }
                        }
                    }
                    if (params.leftMargin > dp(30)) {
                        params.leftMargin = dp(30);
                        slideText.setLayoutParams(params);
                        ViewAudioProxy.setAlpha(slideText, 1);
                        startedDraggingX = -1;
                    }
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void getUser() {
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(this));
        mUsersAPI.getUser(RECIPIENT_ID, new Callback<userItem>() {
            @Override
            public void success(userItem userItem, retrofit.client.Response response) {
                if (userItem.getName() != null) {
                    getSupportActionBar().setTitle(userItem.getName());
                } else {
                    getSupportActionBar().setTitle(userItem.getUsername());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageAdapter.stop();
    }

    public void getMessages() {
        M.showLoadingDialog(this);
        ChatAPI mChatAPI = APIService.createService(ChatAPI.class, M.getToken(this));
        mChatAPI.getMessages(CONVERSATION_ID, RECIPIENT_ID, 1, new Callback<List<MessagesItem>>() {
            @Override
            public void success(List<MessagesItem> messagesItems, Response response) {
                mMessages = messagesItems;
                messageAdapter.setMessages(messagesItems);
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.L(getString(R.string.ServerError));
            }
        });
    }

    private void sendMessage() {
        messageBody = messageField.getText().toString().trim();
        if (!messageBody.isEmpty()) {
            ChatAPI mChatAPI = APIService.createService(ChatAPI.class, M.getToken(this));
            mChatAPI.addMessage(messageBody, CONVERSATION_ID, RECIPIENT_ID, new Callback<MessagesItem>() {
                @Override
                public void success(MessagesItem messagesItem, Response response) {
                    if (messagesItem != null) {
                        mMessages.add(messagesItem);
                        messageAdapter.setMessages(mMessages);
                        messagesList.smoothScrollToPosition(mMessages.size());
                        messageField.setText("");
                    } else {
                        M.T(MessagingActivity.this, getString(R.string.SomethingWentWrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    M.T(MessagingActivity.this, getString(R.string.ServerError));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.attachBtn) {
            if (attachLayout.getVisibility() == View.GONE) {
                attachLayout.setVisibility(View.VISIBLE);
            } else {
                attachLayout.setVisibility(View.GONE);
            }
            if (recordPannel.getVisibility() == View.VISIBLE) {
                recordPannel.setVisibility(View.GONE);
            }
        } else if (v.getId() == R.id.sendBtn) {
            sendMessage();
        } else if (v.getId() == R.id.micBtn) {
            attachLayout.setVisibility(View.GONE);
            recordPannel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mMessageReceiver, new IntentFilter("update_messages_list"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    public void addMessage(MessagesItem newMsg) {
        mMessages.add(newMsg);
        messageAdapter.setMessages(mMessages);
        messagesList.smoothScrollToPosition(mMessages.size());
    }

    private void startRecording() {
        try {
            startRecordingAudio();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
        vibrate();

    }

    private void stopRecording() {

        if (timer != null) {
            timer.cancel();

        }
        if (recordTimeText.getText().toString().equals("00:00")) {
            return;
        }
        recordTimeText.setText("00:00");

        vibrate();
        Toast.makeText(getApplicationContext(), "Stop Recording", Toast.LENGTH_SHORT).show();
        stopRecordingAudio();


    }

    public void startRecordingAudio() throws IOException {
        Toast.makeText(getApplicationContext(), "Start Recording...", Toast.LENGTH_SHORT).show();
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy-MM-dd-HH.mm.ss");
        String fileName = "audio_" + timeStampFormat.format(new Date())
                + ".mp3";
        outFile = "/sdcard/";// Environment.getExternalStorageDirectory().getAbsolutePath();
        stopRecordingAudio();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(outFile + fileName);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
        recorder.prepare();
        recorder.start();

    }

    public void stopRecordingAudio() {
        if (recorder != null) {

            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;

        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(MessagingActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(MessagingActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private void vibrate() {
        // TODO Auto-generated method stub

        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null)
                            recordTimeText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
    }

}
