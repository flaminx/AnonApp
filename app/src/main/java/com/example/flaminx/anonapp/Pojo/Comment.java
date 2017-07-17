package com.example.flaminx.anonapp.Pojo;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class Comment {





    private String commentText;
    private String commentDate;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public int getCommentScore() {
        return commentScore;
    }

    public void setCommentScore(int commentScore) {
        this.commentScore = commentScore;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    private int commentScore;
    private int commentId;



    public Comment()
    {
        commentText = "";
        commentDate = "";
        commentScore = 0;
        commentId = -1;
    }
}
