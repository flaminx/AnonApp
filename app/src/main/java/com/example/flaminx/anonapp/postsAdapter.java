package com.example.flaminx.anonapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.flaminx.anonapp.Pojo.Post;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class postsAdapter extends RecyclerView.Adapter<postsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Post> postObject;
    private int lastObject = -1;
    private int animCutoff;
    private View inflatedView;
    private int pattern;
    public postsAdapter(ArrayList<Post> input)
    {
        animCutoff = input.size();
        postObject = input;
        pattern = 0;
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
        setColour(holder.itemView);
        setAnimation(holder.itemView,position);


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

    private void setColour(View v)
    {
        switch (pattern)
        {
            case 0:
                v.setBackgroundColor(Color.parseColor("#3a3a3a"));
                break;
            case 1:
                v.setBackgroundColor(Color.parseColor("#474747"));
                break;
            case 2:
                v.setBackgroundColor(Color.parseColor("#5e5e5e"));
                break;
            case 3:
                v.setBackgroundColor(Color.parseColor("#7a7a7a"));
                break;
        }
        if(pattern < 3)
        {
            pattern++;
        }
        else pattern = 0;
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

        public ViewHolder(View v, int count)
        {
            super(v);
            postTitle = (TextView) v.findViewById(R.id.post_title);
            postBlurb = (TextView) v.findViewById(R.id.post_blurb);
            postDate = (TextView) v.findViewById(R.id.date);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
        public void bind(Post post)
        {
            postTitle.setText(post.getPostTitle());
            postBlurb.setText(post.getPostBlurb());
            postDate.setText(post.getPostDate());
        }
    }





}
