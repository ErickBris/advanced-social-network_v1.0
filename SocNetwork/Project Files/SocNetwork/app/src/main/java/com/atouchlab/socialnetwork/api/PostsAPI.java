package com.atouchlab.socialnetwork.api;

import com.atouchlab.socialnetwork.data.PostsItem;
import com.atouchlab.socialnetwork.data.ResponseModel;

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
public interface PostsAPI {

    @Multipart
    @POST("/posts/publish")
    void publishPost(@Part("image") TypedFile file,
                     @Part("status") String status,
                     @Part("link") String link,
                     @Part("place") String place,
                     @Part("privacy") String privacy,
                     Callback<ResponseModel> responseModel);



    @GET("/posts/all/{page}")
    void getPosts(@Path("page") int page,
                  Callback<List<PostsItem>> posts);
    @GET("/posts/all/{hashtag}/{page}")
    void getPosts(@Path("hashtag") String hashtag,@Path("page") int page,
                  Callback<List<PostsItem>> posts);

    @GET("/posts/user/{userid}/{page}")
    void getUserPosts(@Path("userid") int userID,
                      @Path("page") int page,
                      Callback<List<PostsItem>> posts);

    @FormUrlEncoded
    @POST("/posts/update/{postid}")
    void updatePost(@Path("postid") int postID, @Field("status") String status, Callback<ResponseModel> response);

    @GET("/posts/liked/{page}")
    void getFavourites(@Path("page") int page, Callback<List<PostsItem>> favouritesPosts);

    @GET("/posts/report/{postid}")
    void reportPost(@Path("postid") int postID, Callback<ResponseModel> response);

    @GET("/posts/delete/{postid}")
    void deletePost(@Path("postid") int postID, Callback<ResponseModel> response);

    @GET("/posts/like/{postid}")
    void likePost(@Path("postid") int postID, Callback<ResponseModel> response);

    @GET("/posts/unlike/{postid}")
    void unlikePost(@Path("postid") int postID, Callback<ResponseModel> response);
}
