package com.atouchlab.socialnetwork.api;

import com.atouchlab.socialnetwork.data.ConversationItem;
import com.atouchlab.socialnetwork.data.MessagesItem;

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
public interface ChatAPI {
    @GET("/chat/all/{page}")
    void getConversations(@Path("page") int page,
                          Callback<List<ConversationItem>> conversations);

    @GET("/chat/get/{conversationID}/{recipientID}/{page}")
    void getMessages(@Path("conversationID") int conversationID,
                     @Path("recipientID") int recipientID,
                     @Path("page") int page,
                     Callback<List<MessagesItem>> messages);

    @FormUrlEncoded()
    @POST("/chat/add")
    void addMessage(@Field("message") String message,
                    @Field("conversationID") int conversationID,
                    @Field("recipientID") int recipientID,
                    Callback<MessagesItem> messageResponse);
}
