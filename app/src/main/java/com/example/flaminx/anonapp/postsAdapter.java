package com.example.flaminx.anonapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaminx.anonapp.Pojo.Post;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class postsAdapter extends RecyclerView.Adapter<postsAdapter.ViewHolder>{


    private ArrayList<Post> postObject;
    private int lastObject = -1;
    private int animCutoff;
    private View inflatedView;

    public postsAdapter(ArrayList<Post> input)
    {
        animCutoff = input.size();
        postObject = input;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posts_item_row,parent,false);
        return new ViewHolder(inflatedView,lastObject);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       Post rowPost = postObject.get(position);
        holder.bind(rowPost);
        setAnimation(holder.itemView,position);


    }
    public void addVote(int pos)
    {
        Post rowPost = postObject.get(pos);
        rowPost.setPostScore(rowPost.getPostScore() + 1);
        postObject.remove(pos);
        postObject.add(pos,rowPost);
    }
    public void removeVote(int pos)
    {
        Post rowPost = postObject.get(pos);
        rowPost.setPostScore(rowPost.getPostScore() - 1);
        postObject.remove(pos);
        postObject.add(pos,rowPost);
    }

    public void addAll(ArrayList<Post> update) {
        lastObject = -1;
        animCutoff = update.size();
        postObject.addAll(0,update);
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
        return postObject.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView postTitle;
        private TextView postBlurb;
        private TextView postDate;
        private TextView postScore;
        private Post mPost;

        public ViewHolder(View v, int count)
        {
            super(v);
            postTitle = (TextView) v.findViewById(R.id.post_title);
            postBlurb = (TextView) v.findViewById(R.id.post_blurb);
            postDate = (TextView) v.findViewById(R.id.date);
            postScore = (TextView) v.findViewById(R.id.post_score);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                v.setElevation(10);
            }
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent postIntent = new Intent(context,PostActivity.class);
            postIntent.putExtra("title",mPost.getPostTitle());
            postIntent.putExtra("text",mPost.getPostText());
            postIntent.putExtra("id",mPost.getPostId());
            context.startActivity(postIntent);




        }
        public void bind(Post post)
        {
            mPost = post;
            postTitle.setText(post.getPostTitle());
            postBlurb.setText(post.getPostBlurb());
            postDate.setText(post.getPostDate());
            postScore.setText(Integer.toString(post.getPostScore()));
        }
    }





}
