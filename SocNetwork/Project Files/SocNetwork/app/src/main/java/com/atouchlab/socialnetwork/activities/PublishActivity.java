package com.atouchlab.socialnetwork.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.adapters.FollowingListAdapter;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.GlobalAPI;
import com.atouchlab.socialnetwork.api.PostsAPI;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.FollowingItem;
import com.atouchlab.socialnetwork.data.LocationModel;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.FilePath;
import com.atouchlab.socialnetwork.helpers.GPSHelper;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class PublishActivity extends AppCompatActivity implements OnClickListener, DialogInterface.OnClickListener {
    EditText insertedLink;
    private ImageView mImagePreview;
    private ImageButton addPhoto, addPlace, addLink, sendStatus, changePrivacy;
    private EditText statusInput;
    private TextView profileName, postPrivacy;
    private ImageView profilePicture;
    private String privacy = "public";
    private String statusValue = null;
    private String linkValue = null;
    private Uri imageUriValue = null;
    private LinearLayout urlPreviewLayout;
    private TextView urlValuePreview, placeValuePreview, removePlace, removeLink;
    private LinearLayout placePreviewLayout;
    private String placeValue = null;
    private ListPopupWindow popupWindow;
    private RecyclerView followsList;
    private List<FollowingItem> mFollowingItems;
    private FollowingListAdapter mFollowingListAdapter;
    public Intent mIntent;
    public LinearLayoutManager layoutManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (M.getToken(this) == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();

        }else {
            setContentView(R.layout.activity_publish);
            initializeView();
            if (getIntent().hasExtra(Intent.EXTRA_SUBJECT)) {
                setStatusValue(getIntent().getExtras().get(Intent.EXTRA_SUBJECT).toString());
            }
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                String text = getIntent().getExtras().get(Intent.EXTRA_TEXT).toString();
                if (M.isValidUrl(text)) {
                    setLinkValue(text);
                } else {
                    setStatusValue(text);
                }
            }
            if (getIntent().hasExtra(Intent.EXTRA_STREAM)) {
                setImageUriValue((Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM));
                M.L(getIntent().getType());
            }
            getUser();
        }

    }

    public void initializeView() {
        addPhoto = (ImageButton) findViewById(R.id.addPhoto);
        sendStatus = (ImageButton) findViewById(R.id.sendStatus);
        changePrivacy = (ImageButton) findViewById(R.id.changePrivacy);
        mImagePreview = (ImageView) findViewById(R.id.imagePreview);
        statusInput = (EditText) findViewById(R.id.statusEdittext);
        profilePicture = (ImageView) findViewById(R.id.postOwnerImage);
        profileName = (TextView) findViewById(R.id.postOwnerName);
        postPrivacy = (TextView) findViewById(R.id.postPrivacy);
        removeLink = (TextView) findViewById(R.id.removeLink);
        removePlace = (TextView) findViewById(R.id.removePlace);
        addPlace = (ImageButton) findViewById(R.id.addPlace);
        addLink = (ImageButton) findViewById(R.id.addLink);

        placePreviewLayout = (LinearLayout) findViewById(R.id.placePreviewLayout);
        placeValuePreview = (TextView) findViewById(R.id.placeValuePreview);

        urlPreviewLayout = (LinearLayout) findViewById(R.id.urlPreviewLayout);
        urlValuePreview = (TextView) findViewById(R.id.urlValuePreview);

        sendStatus.setOnClickListener(this);
        addPhoto.setOnClickListener(this);
        changePrivacy.setOnClickListener(this);
        addPlace.setOnClickListener(this);
        addLink.setOnClickListener(this);
        removePlace.setOnClickListener(this);
        removeLink.setOnClickListener(this);
        //including toolbar  and enabling the home button
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_publish));
    }

    private void getUser() {
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(this));
        mUsersAPI.getUser(0, new Callback<userItem>() {
            @Override
            public void success(userItem user, retrofit.client.Response response) {
                if (user.getName() != null) {
                    profileName.setText(user.getName());
                } else {
                    profileName.setText(user.getUsername());
                }
                Picasso.with(getApplicationContext())
                        .load(AppConst.IMAGE_PROFILE_URL + user.getPicture())
                        .transform(new CropSquareTransformation())
                        .placeholder(R.drawable.image_holder)
                        .error(R.drawable.image_holder)
                        .into(profilePicture);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConst.SELECT_PICTURE) {
                setImageUriValue(data.getData());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.removePlace) {
            setPlaceValue(null);
        } else if (v.getId() == R.id.removeLink) {
            setLinkValue(null);
        } else if (v.getId() == R.id.addPlace) {
            final GPSHelper mGpsHelper = new GPSHelper(this);
            if (mGpsHelper.canGetLocation()) {
                GlobalAPI mGlobalAPI = APIService.createService(GlobalAPI.class, M.getToken(this));
                mGlobalAPI.getCurrentPlace(mGpsHelper.getLatitude(), mGpsHelper.getLongitude(), new Callback<LocationModel>() {
                    @Override
                    public void success(LocationModel location, retrofit.client.Response response) {
                        if (location.isStatus()) {
                            setPlaceValue(location.getAddress());
                        } else {
                            mGpsHelper.showSettingsAlert();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mGpsHelper.showSettingsAlert();
                    }
                });
            } else {
                mGpsHelper.showSettingsAlert();
            }
        } else if (v.getId() == R.id.addLink) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(getString(R.string.Insert_a_link));

            insertedLink = new EditText(this);
            insertedLink.setText("http://");
            alert.setView(insertedLink);
            alert.setPositiveButton(getString(R.string.ok), this);
            alert.setNegativeButton(getString(R.string.cancel), this);
            alert.show();
        } else if (v.getId() == addPhoto.getId()) {
            launchImageChooser();
        } else if (v.getId() == sendStatus.getId()) {

            String statusText = statusInput.getText().toString().trim();
            if (!statusText.isEmpty()) {
                setStatusValue(statusText);
            }
            if (getStatusValue() == null && getImageUriValue() == null && getLinkValue() == null) {
                M.T(PublishActivity.this,
                        getString(R.string.Error_empty_post));
            } else {
                PostsAPI mPostsAPI = APIService.createService(PostsAPI.class, M.getToken(this));

                TypedFile image = null;
                if (getImageUriValue() != null) {
                    image = new TypedFile("image/jpg", new File(FilePath.getPath(this, getImageUriValue())));
                }
                M.showLoadingDialog(this);
                mPostsAPI.publishPost(image, getStatusValue(), getLinkValue(), getPlaceValue(), privacy, new Callback<ResponseModel>() {
                    @Override
                    public void success(ResponseModel responseModel, Response response) {
                        M.hideLoadingDialog();
                        M.T(PublishActivity.this, responseModel.getMessage());
                        if (responseModel.isDone()) {
                            startActivity(new Intent(PublishActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        M.hideLoadingDialog();
                        M.T(PublishActivity.this, getString(R.string.ServerError));
                    }
                });
            }
        } else if (v.getId() == changePrivacy.getId()) {
            if (privacy.equals("public")) {
                postPrivacy.setText(R.string.privatePrivacy);
                privacy = "private";
                M.T(this, getString(R.string.changed_to_private));
            } else {
                postPrivacy.setText(R.string.publicPrivacy);
                privacy = "public";
                M.T(this, getString(R.string.changed_to_public));
            }
        }
    }

    private void launchImageChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Choose An Image"),
                AppConst.SELECT_PICTURE);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String Link = insertedLink.getText().toString();
        if (!Link.equals("http://") && !Link.equals("")) {
            setLinkValue(Link);
        }
    }

    public String getPlaceValue() {
        return placeValue;
    }

    public void setPlaceValue(String placeValue) {
        if (placeValue != null) {

            if (placePreviewLayout.getVisibility() != View.VISIBLE) {
                placePreviewLayout.setVisibility(View.VISIBLE);
                placeValuePreview.setText(placeValue);
            }
        } else {
            if (placePreviewLayout.getVisibility() != View.GONE) {
                placePreviewLayout.setVisibility(View.GONE);
                placeValuePreview.setText("");
            }
        }
        this.placeValue = placeValue;
    }

    public Uri getImageUriValue() {
        return imageUriValue;
    }

    public void setImageUriValue(Uri imageUriValue) {
        this.imageUriValue = imageUriValue;
        mImagePreview.setImageURI(imageUriValue);
        if (mImagePreview.getVisibility() != View.VISIBLE) {
            mImagePreview.setVisibility(View.VISIBLE);
        }
    }

    public String getLinkValue() {
        return linkValue;
    }

    public void setLinkValue(String linkValue) {
        if (linkValue != null) {

            if (urlPreviewLayout.getVisibility() != View.VISIBLE) {
                urlPreviewLayout.setVisibility(View.VISIBLE);
                urlValuePreview.setText(linkValue);
            }
        } else {
            if (urlPreviewLayout.getVisibility() != View.GONE) {
                urlPreviewLayout.setVisibility(View.GONE);
                urlValuePreview.setText("");
            }
        }
        this.linkValue = linkValue;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        String statusInputValue = statusInput.getText().toString().trim();
        if (!statusValue.equals(statusInputValue)) {
            String finalStatus;
            if (TextUtils.isEmpty(statusInputValue)) {
                finalStatus = statusValue;
            } else {
                finalStatus = statusInputValue + " " + statusValue;
            }
            statusInput.setText(finalStatus);
            this.statusValue = finalStatus;
        } else {
            this.statusValue = statusValue;
        }
    }
}
