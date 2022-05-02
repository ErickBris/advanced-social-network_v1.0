package com.atouchlab.socialnetwork.api;

import com.atouchlab.socialnetwork.data.LoginModel;
import com.atouchlab.socialnetwork.data.ResponseModel;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by SmartCoder on 17/04/2015.
 */
public interface AuthenticationAPI {
    @FormUrlEncoded
    @POST("/login")
    void login(@Field("username") String username,
               @Field("password") String password,
               Callback<LoginModel> response);

    @Multipart
    @POST("/register")
    void register(@Part("image") TypedFile image,
                  @Part("username") String username,
                  @Part("password") String Password,
                  @Part("email") String email,
                  Callback<ResponseModel> response);
}
