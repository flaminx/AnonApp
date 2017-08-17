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
    private LinearLayoutManager postsLayout;
    private postsAdapter postAdapter;
    private boolean loading = false;
    private String userId;
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
        userId = AnonApp.getInstance().getUserId();
        AnonApp.getInstance().setThisPage(1);
        getPosts(postAdapter,userId,true);
        posts.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    int visibleItemCount = postsLayout.getChildCount();
                    int totalItemCount = postsLayout.getItemCount();
                    int pastVisiblesItems = postsLayout.findFirstVisibleItemPosition();
                    int thisPage = AnonApp.getInstance().getThisPage();
                    int lastPage = AnonApp.getInstance().getLastpage();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            if(thisPage < lastPage) {
                                AnonApp.getInstance().setThisPage(thisPage + 1);
                                getPosts(postAdapter,userId, false);
                            }
                        }
                    }
                }

            }
        });
    }

    private void getPosts(final postsAdapter adapter,final String uId, final boolean frontOrEnd) {

        final String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(frontOrEnd)
        {
            String url = "http://192.168.10.27/user/posts?page=1";
            postList.clear();
            adapter.notifyDataSetChanged();
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        AnonApp.getInstance().setLastpage(obj.getInt("last_page"));
                        AnonApp.getInstance().setThisPage(obj.getInt("current_page"));
                        JSONArray Jpost = null;
                        Jpost = obj.getJSONArray("data");
                        boolean exists = false;
                        int loc = 0;

                        for (int i = 0; i < Jpost.length(); i++) {

                            JSONObject cPost = Jpost.getJSONObject(i);
                            Post tempPost = new Post();
                            tempPost.setPostTitle(cPost.getString("title"));
                            if (cPost.getString("text").length() > 20) {
                                tempPost.setPostBlurb(cPost.getString("text").substring(0, 20) + "...");
                            } else tempPost.setPostBlurb(cPost.getString("text"));
                            tempPost.setPostText(cPost.getString("text"));
                            tempPost.setPostScore(cPost.getInt("votes"));
                            tempPost.setPostDate(cPost.getString("created_at"));
                            tempPost.setPostId(cPost.getInt("id"));
                            postList.add(tempPost);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loading = true;
                    adapter.notifyDataSetChanged();

                }


            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(getApplicationContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                    }
                }


            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", uId);
                    params.put("user_pass", android_id);
                    return params;
                }

            };
            AnonApp.getInstance().addToReqQ(postRequest);
        }
        else
        {
            String url = "http://192.168.10.27/user/posts?page=" + AnonApp.getInstance().getThisPage();
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray Jpost = null;
                        Jpost = obj.getJSONArray("data");
                        boolean exists = false;
                        int loc = 0;

                        for (int i = 0; i < Jpost.length(); i++) {

                            JSONObject cPost = Jpost.getJSONObject(i);
                            Post tempPost = new Post();
                            tempPost.setPostTitle(cPost.getString("title"));
                            if (cPost.getString("text").length() > 20) {
                                tempPost.setPostBlurb(cPost.getString("text").substring(0, 20) + "...");
                            } else tempPost.setPostBlurb(cPost.getString("text"));
                            tempPost.setPostText(cPost.getString("text"));
                            tempPost.setPostScore(cPost.getInt("votes"));
                            tempPost.setPostDate(cPost.getString("created_at"));
                            tempPost.setPostId(cPost.getInt("id"));
                            postList.add(tempPost);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loading = true;
                    adapter.notifyDataSetChanged();

                }


            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(getApplicationContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                    }
                }


            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", uId);
                    params.put("user_pass", android_id);
                    return params;
                }

            };
            AnonApp.getInstance().addToReqQ(postRequest);
        }


    }
}
