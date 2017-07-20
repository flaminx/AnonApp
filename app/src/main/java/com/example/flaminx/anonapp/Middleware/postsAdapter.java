package com.example.flaminx.anonapp.Middleware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.Pojo.Post;
import com.example.flaminx.anonapp.PostActivity;
import com.example.flaminx.anonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

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
        updateScore(rowPost,"1",pos);
        postObject.remove(pos);
        postObject.add(pos,rowPost);
    }
    public void removeVote(int pos)
    {
        Post rowPost = postObject.get(pos);
        updateScore(rowPost,"0",pos);
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

    private void updateScore(final Post post, String aOrM, int position)
    {
        final int pos = position;
        final String addOrRemove= aOrM;
        final String id = AnonApp.getInstance().getUserId();
        final String pid = Integer.toString(post.getPostId());
        final String POST_URL = "http://192.168.10.27:80/posts/score";




        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        post.setPostScore(Integer.parseInt(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(inflatedView.getContext(), error.toString(), Toast.LENGTH_LONG).show();


                        if(error instanceof ServerError)
                        {
                            postObject.remove(pos);
                            postObject.add(pos,post);
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
                params.put("post_id", pid);
                params.put("AorS", addOrRemove);

                return params;
            }

        };

        AnonApp.getInstance().addToReqQ(stringRequest);
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
