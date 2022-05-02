package com.atouchlab.socialnetwork.api;

import com.atouchlab.socialnetwork.data.CommentsItem;
import com.atouchlab.socialnetwork.data.ResponseModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by SmartCoder on 20/04/2015.
 */
public interface CommentsAPI {
    @GET("/comments/post/{postid}")
    void getComments(@Path("postid") int postID, Callback<List<CommentsItem>> comments);

    @GET("/comments/get/{commentid}")
    void getComment(@Path("commentid") int commentID, Callback<CommentsItem> comment);

    @GET("/comments/delete/{commentid}")
    void deleteComment(@Path("commentid") int commentID, Callback<ResponseModel> response);

    @FormUrlEncoded()
    @POST("/comments/update/{commentid}")
    void updateComment(@Path("commentid") int commentID, @Field("comment") String comment, Callback<ResponseModel> response);

    @FormUrlEncoded()
    @POST("/comments/add/{postid}")
    void addComment(@Path("postid") int postID, @Field("comment") String comment, Callback<ResponseModel> response);
}
