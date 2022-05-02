package com.atouchlab.socialnetwork.data;

public class ConversationItem {
    public String date;
    public String lastMessage;
    public String recpientName;
    public String recpientUsername;
    public String recpientPicture;
    public int recpientID;
    public int conversationID,unseenCount;

    public ConversationItem() {

    }

    public int getUnseenCount() {
        return unseenCount;
    }

    public void setUnseenCount(int unseenCount) {
        this.unseenCount = unseenCount;
    }

    public String getRecpientName() {
        return recpientName;
    }

    public void setRecpientName(String recpientName) {
        this.recpientName = recpientName;
    }

    public String getRecpientUsername() {
        return recpientUsername;
    }

    public void setRecpientUsername(String recpientUsername) {
        this.recpientUsername = recpientUsername;
    }

    public int getRecpientID() {
        return recpientID;
    }

    public void setRecpientID(int recpientID) {
        this.recpientID = recpientID;
    }

    public String getRecpientPicture() {
        return recpientPicture;
    }

    public void setRecpientPicture(String recpientPicture) {
        this.recpientPicture = recpientPicture;
    }

    public int getConversationID() {
        return conversationID;
    }

    public void setConversationID(int conversationID) {
        this.conversationID = conversationID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
