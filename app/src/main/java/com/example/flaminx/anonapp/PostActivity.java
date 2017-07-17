package com.example.flaminx.anonapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.flaminx.anonapp.Pojo.Comment;
import com.example.flaminx.anonapp.Pojo.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class PostActivity extends AppCompatActivity {

    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private commentAdapter cAdapter;
    private RecyclerView comments;
    private RecyclerView.LayoutManager commentLayout;
    private int postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        TextView title = (TextView) findViewById(R.id.activityPostTitle);
        TextView text = (TextView) findViewById(R.id.activityPostText);

        postId = getIntent().getIntExtra("id",-1);
        title.setText(getIntent().getStringExtra("title"));
        text.setText(getIntent().getStringExtra("text"));
        commentLayout = new LinearLayoutManager(this);
        cAdapter = new commentAdapter(commentList);
        comments = (RecyclerView) findViewById(R.id.commentList);
        comments.setHasFixedSize(false);
        comments.setAdapter(cAdapter);
        comments.setLayoutManager(commentLayout);
        Comment c = new Comment();
        commentList.add(c);
        cAdapter.notifyDataSetChanged();
        getComments(cAdapter,postId);
    }


    private void getComments(final commentAdapter adapter,int id) {
        String url = "http://192.168.10.27:80/posts/"+id+"/comments";

        JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {

                    boolean exists = false;
                    int loc = 0;

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject cPost = response.getJSONObject(i);
                        for (int j = 0; j < commentList.size(); j++) {
                            if (cPost.getInt("id") == commentList.get(j).getCommentId()) {
                                exists = true;
                                loc = j;
                                break;
                            }
                        }
                        if (!exists) {
                            Comment tempComment = new Comment();


                            tempComment.setCommentText(cPost.getString("text"));
                            tempComment.setCommentScore(cPost.getInt("votes"));
                            tempComment.setCommentDate(cPost.getString("created_at"));
                            tempComment.setCommentId(cPost.getInt("id"));
                            commentList.add(0,tempComment);
                        } else if (exists) {
                            commentList.get(loc).setCommentScore(cPost.getInt("votes"));
                        }
                        exists = false;
                        loc = 0;
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

        AnonApp.getInstance().addToReqQ(postRequest);

    }
}
