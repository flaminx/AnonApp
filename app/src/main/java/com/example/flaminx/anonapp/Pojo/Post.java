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
    private DateFormat df;
    private Date jvDate;
    private int postScore;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    private String postDate;

    public int getPostScore() {
        return postScore;
    }

    public void setPostScore(int postScore) {
        this.postScore = postScore;
    }

    public Post(int i)
    {
        postTitle = "John Sucks" + i;
        postBlurb = "Blah blah...";
        postText = "Blah blah Blah";
        df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        jvDate = new Date();
        postDate = df.format(jvDate);
        postScore = 0;
    }
}
