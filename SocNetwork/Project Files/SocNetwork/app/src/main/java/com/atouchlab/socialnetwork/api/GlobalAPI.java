package com.atouchlab.socialnetwork.api;

import com.atouchlab.socialnetwork.data.LocationModel;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.data.LinkModel;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Streaming;

/**
 * Created by SmartCoder on 18/04/2015.
 */
public interface GlobalAPI {
    @GET("/place/{longitude}/{latitude}")
    void getCurrentPlace(@Path("longitude") double longitude,
                         @Path("latitude") double latitude,
                         Callback<LocationModel> response);

    @FormUrlEncoded()
    @POST("/map/get")
    void getMapImageUrl(@Field("place_name") String placeName, Callback<ResponseModel> MapInfo);

    @GET("/image/large/{imageid}")
    @Streaming
    void getImage(@Path("imageid") String imageID, Callback<Response> response);

    @FormUrlEncoded
    @POST("/users/regid/")
    void updateRegID(@Field("regID") String regID, Callback<ResponseModel> response);

    @GET("/link/{linkHash}")
    void getLink(@Path("linkHash") String linkHash, Callback<LinkModel> linkModel);

    @GET("/terms")
    void getTerms(Callback<ResponseModel> responseModel);

}
