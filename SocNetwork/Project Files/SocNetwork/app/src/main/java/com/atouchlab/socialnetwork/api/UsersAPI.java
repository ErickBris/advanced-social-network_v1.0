package com.atouchlab.socialnetwork.api;

import com.atouchlab.socialnetwork.data.FollowingItem;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.data.userItem;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by SmartCoder on 17/04/2015.
 */
public interface UsersAPI {
    @GET("/users/get/{userid}")
    void getUser(@Path("userid") int userID,
                 Callback<userItem> user);

    @GET("/users/following")
    void getFollowing(Callback<List<FollowingItem>> following);

    @FormUrlEncoded
    @POST("/users/search")
    void search(@Field("value") String value, Callback<List<userItem>> users);

    @GET("/users/follow/{userID}")
    void followToggle(@Path("userID") int userID, Callback<ResponseModel> followResponse);

    //@FormUrlEncoded
    @Multipart
    @POST("/users/update")
    void updateProfile(@Part("username") String username,
                       @Part("name") String name,
                       @Part("job") String job,
                       @Part("address") String address,
                       @Part("email") String email,
                       @Part("password") String password,
                       @Part("image") TypedFile avatar,
                       @Part("cover") TypedFile cover,
                       Callback<ResponseModel> updateResponse);
    @GET("/users/followers")
    void getFollowers(Callback<List<FollowingItem>> followers);
}
