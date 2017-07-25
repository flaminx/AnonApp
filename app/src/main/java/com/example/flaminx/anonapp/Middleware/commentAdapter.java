package com.example.flaminx.anonapp.Middleware;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.Pojo.Comment;
import com.example.flaminx.anonapp.Pojo.Post;
import com.example.flaminx.anonapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        updateScore(rowComment,"1",pos);
        commentObject.remove(pos);
        commentObject.add(pos,rowComment);
    }
    public void removeVote(int pos)
    {
        Comment rowComment = commentObject.get(pos);
        updateScore(rowComment,"0",pos);
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

    private void updateScore(final Comment comment, String aOrM, int position)
    {
        final int pos = position;
        final String addOrRemove= aOrM;
        final String id = AnonApp.getInstance().getUserId();
        final String pid = Integer.toString(comment.getCommentId());
        final String POST_URL = "http://192.168.10.27:80/comments/score";




        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        comment.setCommentScore(Integer.parseInt(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(inflatedView.getContext(), error.toString(), Toast.LENGTH_LONG).show();


                        if(error instanceof ServerError)
                        {
                            commentObject.remove(pos);
                            commentObject.add(pos,comment);
                            if(error.networkResponse.statusCode == 409) {

                                Toast.makeText(inflatedView.getContext(), R.string.post_cooldown, Toast.LENGTH_LONG).show();
                            }
                            else if(error.networkResponse.statusCode == 400)
                            {
                                Toast.makeText(inflatedView.getContext(), R.string.post_requirements, Toast.LENGTH_LONG).show();
                            }
                        }
                        else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(inflatedView.getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", id);
                params.put("comment_id", pid);
                params.put("AorS", addOrRemove);

                return params;
            }

        };

        AnonApp.getInstance().addToReqQ(stringRequest);
    }



}
