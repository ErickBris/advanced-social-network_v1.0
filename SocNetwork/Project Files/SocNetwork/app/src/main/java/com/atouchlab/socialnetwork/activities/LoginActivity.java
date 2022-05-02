package com.atouchlab.socialnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.AuthenticationAPI;
import com.atouchlab.socialnetwork.data.LoginModel;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private Button loginBtn, registerBtn;
    private EditText usernameInput, passwordInput;
    private String username, password;
    private ImageView avatar;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (M.getToken(this) != null) {
            mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();
        }

            setContentView(R.layout.activity_login);
            initializeView();

    }

    public void initializeView() {
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        avatar = (ImageView) findViewById(R.id.avatar);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        Picasso.with(getApplicationContext())
                .load(R.drawable.logo)
                .transform(new CropSquareTransformation())
                .into(avatar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_login);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginBtn) {
            username = usernameInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            if (username.length() <= 4) {
                usernameInput.setError("Invalid Username");
            } else if (password.length() <= 5) {
                passwordInput.setError("Invalid Password");
            } else {
                M.showLoadingDialog(this);
                AuthenticationAPI mAuthenticationAPI = APIService.createService(AuthenticationAPI.class);
                mAuthenticationAPI.login(username, password, new Callback<LoginModel>() {
                    @Override
                    public void success(LoginModel loginModel, retrofit.client.Response response) {
                        M.hideLoadingDialog();
                        if (loginModel.isSuccess()) {
                            M.setToken(loginModel.getToken(), LoginActivity.this);
                            M.setID(loginModel.getUserID(), LoginActivity.this);
                            mIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mIntent);
                            finish();
                        } else {
                            M.T(LoginActivity.this, loginModel.getMessage());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        M.hideLoadingDialog();
                        M.T(LoginActivity.this, getString(R.string.ServerError));
                    }
                });
            }
        } else if (v.getId() == R.id.registerBtn) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }
}
