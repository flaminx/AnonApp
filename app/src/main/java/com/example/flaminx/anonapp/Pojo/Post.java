package com.example.flaminx.anonapp.Pojo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class Post {

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBlurb() {
        return postBlurb;
    }

    public void setPostBlurb(String postBlurb) {
        this.postBlurb = postBlurb;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    private String postTitle;
    private String postBlurb;
    private String postText;
    private String postDate;
    private int postScore;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    private int postId;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }



    public int getPostScore() {
        return postScore;
    }

    public void setPostScore(int postScore) {
        this.postScore = postScore;
    }

    public Post()
    {
        postTitle = "";
        postBlurb = "";
        postText = "";
        postDate = "";
        postScore = 0;
        postId = -1;
    }
}
