package com.example.myapplication.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Post implements Serializable {
    private String reviewDate, content, postImageUrl, userEmail, id, roomName, branchName;

    public Post() {
    }

    public Post(String reviewDate, String postDesc, String postImageUrl, String userEmail, String id, String roomName, String branchName) {
        this.reviewDate = reviewDate;
        this.content = postDesc;
        this.postImageUrl = postImageUrl;
        this.userEmail = userEmail;
        this.id = id;
        this.roomName = roomName;
        this.branchName = branchName;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String postDesc) {
        this.content = postDesc;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Post{" +
                "date='" + reviewDate + '\'' +
                ", postDesc='" + content + '\'' +
                ", postImageUrl='" + postImageUrl + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", postID='" + id + '\'' +
                '}';
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}


