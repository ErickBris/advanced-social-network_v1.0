package com.atouchlab.socialnetwork.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.GlobalAPI;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.fragments.DisclaimerFragment;
import com.atouchlab.socialnetwork.fragments.FavoritesFragment;
import com.atouchlab.socialnetwork.fragments.FindFriendsFragment;
import com.atouchlab.socialnetwork.fragments.FollowersFragment;
import com.atouchlab.socialnetwork.fragments.FollowingFragment;
import com.atouchlab.socialnetwork.fragments.HomeFragment;
import com.atouchlab.socialnetwork.fragments.MessagesFragment;
import com.atouchlab.socialnetwork.fragments.SettingsFragment;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.M;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavigationView;
    private Toolbar toolbar;
    private FragmentManager mFragmentManager;
    private android.app.Fragment mFragment = null;
    private Intent mIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (M.getToken(this) == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();
        } else {
            setContentView(R.layout.activity_main);
            initializeView();
            displayView(R.id.homeItem);
        }
    }

    public void initializeView() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (! M.getSharedPref(this).getBoolean("GCMregistered", false)) {
            registerInBackground();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                displayView(menuItem.getItemId());
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        updateDrawerHeaderInfo();
    }

    public void updateDrawerHeaderInfo() {
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(this));
        mUsersAPI.getUser(0, new Callback<userItem>() {
            @Override
            public void success(userItem user, retrofit.client.Response response) {
                updateHeaderView(user);
            }

            @Override
            public void failure(RetrofitError error) {
                M.T(MainActivity.this, getString(R.string.ServerError));
            }
        });
    }

    public void updateHeaderView(userItem user) {
        ImageView profilePicture = (ImageView) findViewById(R.id.profilePic);
        TextView userName = (TextView) findViewById(R.id.userName);
        TextView userJob = (TextView) findViewById(R.id.userJob);
        ImageView profileCover = (ImageView) findViewById(R.id.profileCover);
        userJob.setText(user.getJob());
        userName.setText(user.getUsername());
        Picasso.with(this)
                .load(AppConst.IMAGE_PROFILE_URL + user.getPicture())
                .transform(new CropSquareTransformation())
                .into(profilePicture);
        if (user.getCover() != null) {

            Picasso.with(this)
                    .load(AppConst.IMAGE_COVER_URL + user.getCover())
                    .placeholder(R.drawable.header)
                    .error(R.drawable.header)
                    .into(profileCover);
        } else {
            Picasso.with(this)
                    .load(R.drawable.header)
                    .into(profileCover);
        }
    }

    private void displayView(int id) {
        switch (id) {
            case R.id.homeItem:
                mFragment = new HomeFragment();
                break;
            case R.id.followingItem:
                mFragment = new FollowingFragment();
                break;
            case R.id.followersItem:
                mFragment = new FollowersFragment();
                break;
            case R.id.findPeopleItem:
                mFragment = new FindFriendsFragment();
                break;
            case R.id.favoritesItem:
                mFragment = new FavoritesFragment();
                break;
            case R.id.messagesItem:
                mFragment = new MessagesFragment();
                break;
            case R.id.settingsItem:
                mFragment = new SettingsFragment();
                break;
            case R.id.logoutItem:
                M.logOut(this);
                mIntent = new Intent(this, LoginActivity.class);
                finish();
                break;
            case R.id.disclaimerItem:
                mFragment = new DisclaimerFragment();
                break;

            default:
                break;
        }

        if (mFragment != null && mIntent == null) {
            mFragmentManager = getFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, mFragment).commit();
        } else {
            startActivity(mIntent);
            mIntent = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token;

                try {
                    InstanceID mInstanceID = InstanceID.getInstance(MainActivity.this);
                    token = mInstanceID.getToken(AppConst.GOOGLE_PROJ_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException ex) {
                    M.L(ex.getMessage());
                    token = null;
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                updateRegID(token);
            }
        }.execute(null, null, null);
    }

    public void updateRegID(String token) {
        if (token != null) {
            GlobalAPI mGlobalAPI = APIService.createService(GlobalAPI.class, M.getToken(this));
            mGlobalAPI.updateRegID(token, new Callback<ResponseModel>() {
                @Override
                public void success(ResponseModel response, Response response2) {
                    M.editSharedPref(MainActivity.this).putBoolean("GCMregistered", true).commit();
                }

                @Override
                public void failure(RetrofitError error) {
                    M.T(MainActivity.this, getString(R.string.ServerError));
                }
            });
        }
    }
}
