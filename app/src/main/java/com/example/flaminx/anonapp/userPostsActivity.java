package com.example.flaminx.anonapp;

import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.Middleware.postsAdapter;
import com.example.flaminx.anonapp.Pojo.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class userPostsActivity extends AppCompatActivity {
    private ArrayList<Post> postList = new ArrayList<Post>();
    private RecyclerView posts;
    private RecyclerView.LayoutManager postsLayout;
    private postsAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);


        postAdapter = new postsAdapter(postList);
        posts = (RecyclerView) findViewById(R.id.userpostList);
        posts.setHasFixedSize(false);
        postsLayout = new LinearLayoutManager(this);
        posts.setLayoutManager(postsLayout);
        posts.setAdapter(postAdapter);
        getPosts(postAdapter,AnonApp.getInstance().getUserId());
    }


    private void getPosts(final postsAdapter adapter, String userId) {
        /*
        String url = "http://192.168.10.27:80/user/"+userId+"/posts";
        postList.clear();
        JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {


                    for (int i = 0; i < response.length(); i++) {

                        JSONObject cPost = response.getJSONObject(i);
                        Post tempPost = new Post();
                        tempPost.setPostTitle(cPost.getString("title"));
                        if (cPost.getString("text").length() > 20) {
                            tempPost.setPostBlurb(cPost.getString("text").substring(0, 20) + "...");
                        } else tempPost.setPostBlurb(cPost.getString("text"));
                        tempPost.setPostText(cPost.getString("text"));
                        tempPost.setPostScore(cPost.getInt("votes"));
                        tempPost.setPostDate(cPost.getString("created_at"));
                        tempPost.setPostId(cPost.getInt("id"));
                        postList.add(0,tempPost);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();

            }


        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                }
            }


        });

        AnonApp.getInstance().addToReqQ(postRequest);*/

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String id = AnonApp.getInstance().getUserId();
        final String password = android_id;
        final String POST_URL = "http://192.168.10.27:80/user/posts";




        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray obj = new JSONArray(response);

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject cPost = obj.getJSONObject(i);
                                Post tempPost = new Post();
                                tempPost.setPostTitle(cPost.getString("title"));
                                if (cPost.getString("text").length() > 20) {
                                    tempPost.setPostBlurb(cPost.getString("text").substring(0, 20) + "...");
                                } else tempPost.setPostBlurb(cPost.getString("text"));
                                tempPost.setPostText(cPost.getString("text"));
                                tempPost.setPostScore(cPost.getInt("votes"));
                                tempPost.setPostDate(cPost.getString("created_at"));
                                tempPost.setPostId(cPost.getInt("id"));
                                postList.add(0,tempPost);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();




                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                        if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(getApplicationContext(), R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                        }
                        else if(error instanceof ServerError)
                        {
                            if(error.networkResponse.statusCode == 409) {

                                Toast.makeText(getApplicationContext(),R.string.Oops, Toast.LENGTH_LONG).show();
                            }
                        }
                        else if(error instanceof TimeoutError)
                        {
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
