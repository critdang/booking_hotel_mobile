package com.example.myapplication.Model;

public class Comment {
    private String comment, userEmail;

    public Comment(String comment, String userEmail) {
        this.comment = comment;
        this.userEmail = userEmail;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
