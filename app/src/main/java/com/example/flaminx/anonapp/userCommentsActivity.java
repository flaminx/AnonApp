package com.example.flaminx.anonapp;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.Middleware.commentAdapter;
import com.example.flaminx.anonapp.Middleware.postsAdapter;
import com.example.flaminx.anonapp.Pojo.Comment;
import com.example.flaminx.anonapp.Pojo.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class userCommentsActivity extends AppCompatActivity {
    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private RecyclerView comments;
    private RecyclerView.LayoutManager postsLayout;
    private commentAdapter cAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);


        cAdapter = new commentAdapter(commentList);
        comments = (RecyclerView) findViewById(R.id.userpostList);
        comments.setHasFixedSize(false);
        postsLayout = new LinearLayoutManager(this);
        comments.setLayoutManager(postsLayout);
        comments.setAdapter(cAdapter);
        getPosts(cAdapter, AnonApp.getInstance().getUserId());
    }


    private void getPosts(final commentAdapter adapter, String userId) {



        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String id = AnonApp.getInstance().getUserId();
        final String password = android_id;
        final String POST_URL = AnonApp.getInstance().getWebAddress()+"/user/comments";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray obj = new JSONArray(response);
                            for (int i = 0; i < obj.length(); i++) {

                                JSONObject cPost = obj.getJSONObject(i);
                                Comment tempPost = new Comment();
                                tempPost.setCommentText(cPost.getString("text"));
                                tempPost.setCommentScore(cPost.getInt("votes"));
                                tempPost.setCommentDate(cPost.getString("created_at"));
                                tempPost.setCommentPostId(cPost.getInt("post_id"));
                                commentList.add(0, tempPost);
                            }


                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                        if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "whoops", Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("user_pass", password);
                return params;
            }

        };

        AnonApp.getInstance().addToReqQ(stringRequest);
    }
}
