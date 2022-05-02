package com.atouchlab.socialnetwork.data;

public class CommentsItem {

    String ownerUsername,ownerName,
            ownerPicture,
            comment,
            date;
    int id,ownerID;

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPicture() {
        return ownerPicture;
    }

    public void setOwnerPicture(String ownerPicture) {
        this.ownerPicture = ownerPicture;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String text) {
        this.comment = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
}
