package com.example.iswakt.Data;

public class UpdatesModel {

    private String userId;
    private String userName;
    private String postId;
    private String post;
    private String location;
    private String time;
    private String photoUrl;
    private int likes;
    private int dislikes;

    public UpdatesModel() {
    }

    public UpdatesModel(String id, String userName, String post, String location, String time, int likes, int dislikes,String postId,String photoUrl) {
        this.userId = id;
        this.userName = userName;
        this.post = post;
        this.location = location;
        this.time = time;
        this.likes = likes;
        this.dislikes = dislikes;
        this.postId=postId;
        this.photoUrl=photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }



    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPost() {
        return post;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getPostId() {
        return postId;
    }
}
