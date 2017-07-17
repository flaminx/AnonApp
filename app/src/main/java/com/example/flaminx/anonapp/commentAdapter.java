package com.example.flaminx.anonapp;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.flaminx.anonapp.Pojo.Comment;
import com.example.flaminx.anonapp.Pojo.Post;

import java.util.ArrayList;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder>{


    private ArrayList<Comment> commentObject;
    private int lastObject = -1;
    private int animCutoff;
    private View inflatedView;

    public commentAdapter(ArrayList<Comment> input)
    {
        animCutoff = input.size();
        commentObject = input;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_row,parent,false);
        return new ViewHolder(inflatedView,lastObject);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Comment rowPost = commentObject.get(position);
        holder.bind(rowPost);
        setAnimation(holder.itemView,position);


    }
    public void addVote(int pos)
    {
        Comment rowComment = commentObject.get(pos);
        rowComment.setCommentScore(rowComment.getCommentScore() + 1);
        commentObject.remove(pos);
        commentObject.add(pos,rowComment);
    }
    public void removeVote(int pos)
    {
        Comment rowComment = commentObject.get(pos);
        rowComment.setCommentScore(rowComment.getCommentScore() - 1);
        commentObject.remove(pos);
        commentObject.add(pos,rowComment);
    }

    public void addAll(ArrayList<Comment> update) {
        lastObject = -1;
        animCutoff = update.size();
        commentObject.addAll(0,update);
        notifyDataSetChanged();
    }

    private void setAnimation(View vta, int pos)
    {
        if(pos > lastObject && pos < animCutoff)
        {
            Animation anim = AnimationUtils.loadAnimation(inflatedView.getContext(),android.R.anim.fade_in);
            vta.startAnimation(anim);
            lastObject = pos;
        }
    }





    @Override
    public int getItemCount() {
        return commentObject.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView commentText;
        private TextView commentDate;
        private TextView commentScore;
        private Comment mComment;

        public ViewHolder(View v, int count)
        {
            super(v);

            commentText = (TextView) v.findViewById(R.id.comment_text);
            commentDate = (TextView) v.findViewById(R.id.comment_date);
            commentScore = (TextView) v.findViewById(R.id.comment_score);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                v.setElevation(10);
            }
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
        public void bind(Comment comment)
        {
            mComment = comment;
            commentText.setText(comment.getCommentText());
            commentDate.setText(comment.getCommentDate());
            commentScore.setText(Integer.toString(comment.getCommentScore()));
        }
    }





}
