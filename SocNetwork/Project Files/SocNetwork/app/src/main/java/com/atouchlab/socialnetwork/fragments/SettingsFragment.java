package com.atouchlab.socialnetwork.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.activities.MainActivity;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.UsersAPI;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.data.userItem;
import com.atouchlab.socialnetwork.helpers.CropSquareTransformation;
import com.atouchlab.socialnetwork.helpers.FilePath;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class SettingsFragment extends Fragment implements OnClickListener {
    public View mView;
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;
    private String name;
    private String job;
    private String address;
    private EditText usernameField;
    private EditText emailField;
    private EditText fullNameField;
    private EditText jobField;
    private EditText addressField;
    private EditText passwordField;
    private EditText passwordConfirmField;
    private ImageView avatarImage, coverImage;
    private ImageButton chooseAvatar, chooseCover;
    private Button saveBtn;
    private String selectedAvatarImagePath = null;
    private String selectedCoverImagePath = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_settings, container,
                false);
        initializeView();
        getUser();
        return mView;
    }

    private void getUser() {
        M.showLoadingDialog(getActivity());
        UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(getActivity()));
        mUsersAPI.getUser(0, new Callback<userItem>() {
            @Override
            public void success(userItem user, retrofit.client.Response response) {
                usernameField.setText(user.getUsername());
                if (user.getName() != null) {
                    fullNameField.setText(user.getName());
                }
                if (user.getJob() != null) {
                    jobField.setText(user.getJob());
                }
                if (user.getAddress() != null) {
                    addressField.setText(user.getAddress());
                }
                emailField.setText(user.getEmail());
                Picasso.with(getActivity())
                        .load(AppConst.IMAGE_PROFILE_URL + user.getPicture())
                        .transform(new CropSquareTransformation())
                        .into(avatarImage);
                if (user.getCover() != null) {
                    Picasso.with(getActivity())
                            .load(AppConst.IMAGE_COVER_URL + user.getCover())
                            .into(coverImage);
                } else {
                    Picasso.with(getActivity())
                            .load(R.drawable.header)
                            .into(coverImage);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.T(getActivity(), getString(R.string.ServerError));
            }
        });
    }

    public void initializeView() {
        saveBtn = (Button) mView.findViewById(R.id.saveBtn);
        chooseAvatar = (ImageButton) mView.findViewById(R.id.changeAvatar);
        chooseCover = (ImageButton) mView.findViewById(R.id.changeCover);
        avatarImage = (ImageView) mView.findViewById(R.id.avatarPicture);
        coverImage = (ImageView) mView.findViewById(R.id.coverPicture);
        usernameField = (EditText) mView.findViewById(R.id.usernameField);
        emailField = (EditText) mView.findViewById(R.id.emailField);
        jobField = (EditText) mView.findViewById(R.id.jobField);
        addressField = (EditText) mView.findViewById(R.id.addressField);
        fullNameField = (EditText) mView.findViewById(R.id.fullNameField);
        passwordField = (EditText) mView.findViewById(R.id.passwordField);
        passwordConfirmField = (EditText) mView.findViewById(R.id.passwordConfirmField);

        saveBtn.setOnClickListener(this);
        chooseAvatar.setOnClickListener(this);
        chooseCover.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveBtn) {
            name = fullNameField.getText().toString().trim();
            job = jobField.getText().toString().trim();
            address = addressField.getText().toString().trim();
            username = usernameField.getText().toString().trim();
            email = emailField.getText().toString().trim();
            password = passwordField.getText().toString().trim();
            passwordConfirm = passwordConfirmField.getText().toString().trim();
            if (password.length() != 0 && ! password.equals(passwordConfirm)) {
                passwordConfirmField.setError(getActivity().getString(R.string.Passwords_does_not_much));
            } else if (name.length() <= 4) {
                fullNameField.setError(getActivity().getString(R.string.name_is_to_short));
            } else if (job.length() <= 4) {
                jobField.setError(getActivity().getString(R.string.job_too_short));
            } else if (name.length() <= 4) {
                addressField.setError(getActivity().getString(R.string.Adresse_too_short));
            } else if (username.length() <= 4) {
                usernameField.setError(getActivity().getString(R.string.username_is_short));
            } else if (email.length() <= 10) {
                emailField.setError(getActivity().getString(R.string.invalid_email));
            } else {
                UsersAPI mUsersAPI = APIService.createService(UsersAPI.class, M.getToken(getActivity()));
                TypedFile coverFile = null;
                TypedFile avatarFile = null;
                if (selectedCoverImagePath != null) {
                    coverFile = new TypedFile("image/jpg", new File(selectedCoverImagePath));
                }
                if (selectedAvatarImagePath != null) {
                    avatarFile = new TypedFile("image/jpg", new File(selectedAvatarImagePath));
                }
                M.showLoadingDialog(getActivity());
                mUsersAPI.updateProfile(username,
                        name,
                        job,
                        address,
                        email,
                        passwordConfirm,
                        avatarFile,
                        coverFile,
                        new Callback<ResponseModel>() {
                            @Override
                            public void success(ResponseModel responseModel, Response response) {
                                M.hideLoadingDialog();
                                M.T(getActivity(), responseModel.getMessage());
                                ((MainActivity) getActivity()).updateDrawerHeaderInfo();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                M.hideLoadingDialog();
                                M.T(getActivity(), getString(R.string.ServerError));
                            }
                        });
            }
        } else if (v.getId() == R.id.changeAvatar) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, getActivity().getString(R.string.select_an_image)),
                    AppConst.SELECT_PICTURE);
        } else if (v.getId() == R.id.changeCover) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, getActivity().getString(R.string.select_an_image)),
                    AppConst.SELECT_COVER);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == AppConst.SELECT_PICTURE) {
                selectedAvatarImagePath = FilePath.getPath(getActivity(), data.getData());
                Picasso.with(getActivity())
                        .load(new File(selectedAvatarImagePath))
                        .placeholder(R.drawable.image_holder)
                        .error(R.drawable.image_holder)
                        .transform(new CropSquareTransformation())
                        .into(avatarImage);
            } else if (requestCode == AppConst.SELECT_COVER) {
                selectedCoverImagePath = FilePath.getPath(getActivity(), data.getData());
                Picasso.with(getActivity())
                        .load(new File(selectedCoverImagePath))
                        .placeholder(R.drawable.image_holder)
                        .error(R.drawable.image_holder)
                        .into(coverImage);
            }
        }
    }
}
