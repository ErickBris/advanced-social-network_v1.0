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
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.FilePath;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {
    private String username, email, password, passwordConfirm;
    private EditText usernameInput, emailInput, passwordInput,
            passwordConfirmInput;
    private ImageView avatar;
    private Button registerBtn, chooseAvatar;
    private String selectedImagePath = null;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeView();
    }

    public void initializeView() {
        registerBtn = (Button) findViewById(R.id.registerBtn);
        chooseAvatar = (Button) findViewById(R.id.chooseAvatar);
        avatar = (ImageView) findViewById(R.id.avatar);
        usernameInput = (EditText) findViewById(R.id.username);
        emailInput = (EditText) findViewById(R.id.email);
        passwordInput = (EditText) findViewById(R.id.password);
        passwordConfirmInput = (EditText) findViewById(R.id.passwordConfirm);
        Picasso.with(getApplicationContext())
                .load(R.drawable.logo)
                .transform(new CropSquareTransformation())
                .into(avatar);
        registerBtn.setOnClickListener(this);
        chooseAvatar.setOnClickListener(this);

        //including toolbar  and enabling the home button
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerBtn) {
            username = usernameInput.getText().toString().trim();
            email = emailInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            passwordConfirm = passwordConfirmInput.getText().toString().trim();
            if (username.length() <= 4) {
                usernameInput.setError(getString(R.string.username_is_short));
            } else if (email.length() <= 10) {
                emailInput.setError(getString(R.string.invalid_email));
            } else if (password.length() <= 5) {
                passwordInput.setError(getString(R.string.password_is_short));
            } else if (!password.equals(passwordConfirm)) {
                passwordConfirmInput.setError(getString(R.string.Passwords_does_not_much));
            } else if (selectedImagePath == null) {
                M.T(this, "Please Choose An Avatar");
            } else {
                AuthenticationAPI mAuthenticationAPI = APIService.createService(AuthenticationAPI.class);
                TypedFile image = new TypedFile("image/jpg", new File(selectedImagePath));
                M.showLoadingDialog(this);
                mAuthenticationAPI.register(image, username, password, email, new Callback<ResponseModel>() {
                    @Override
                    public void success(ResponseModel registerModel, Response response) {
                        if (registerModel.isDone()) {
                            M.T(RegisterActivity.this, registerModel.getMessage());
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            M.T(RegisterActivity.this, registerModel.getMessage());
                        }
                        M.hideLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        M.hideLoadingDialog();
                        M.T(RegisterActivity.this, getString(R.string.ServerError));
                    }
                });

            }
        } else if (v.getId() == R.id.chooseAvatar) {
            mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(mIntent, getString(R.string.select_picture)),
                    AppConst.SELECT_PICTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConst.SELECT_PICTURE) {
                selectedImagePath = FilePath.getPath(this, data.getData());
                Picasso.with(getApplicationContext())
                        .load(data.getData())
                        .transform(new CropSquareTransformation())
                        .placeholder(R.drawable.image_holder)
                        .error(R.drawable.image_holder)
                        .into(avatar);
                if (avatar.getVisibility() != View.VISIBLE) {
                    avatar.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
