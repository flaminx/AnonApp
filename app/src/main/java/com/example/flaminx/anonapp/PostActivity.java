package com.example.flaminx.anonapp;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.flaminx.anonapp.Middleware.commentAdapter;
import com.example.flaminx.anonapp.Pojo.Comment;
import com.example.flaminx.anonapp.Writers.commentWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class PostActivity extends AppCompatActivity {

    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private commentAdapter cAdapter;
    private RecyclerView comments;
    private RecyclerView.LayoutManager commentLayout;
    private int postId;
    volatile boolean success;
    Thread refreshThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        TextView title = (TextView) findViewById(R.id.activityPostTitle);
        TextView text = (TextView) findViewById(R.id.activityPostText);
        final View parent = findViewById(R.id.activity_comment);
        postId = getIntent().getIntExtra("id", -1);
        title.setText(getIntent().getStringExtra("title"));
        text.setText(getIntent().getStringExtra("text"));
        commentLayout = new LinearLayoutManager(this);
        cAdapter = new commentAdapter(commentList);
        comments = (RecyclerView) findViewById(R.id.commentList);
        comments.setHasFixedSize(false);
        comments.setAdapter(cAdapter);
        comments.setLayoutManager(commentLayout);
        cAdapter.notifyDataSetChanged();
        getComments(cAdapter, postId);
        setRecyclerViewItemTouchListener();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                success = false;
                while (!success) {
                    try {
                        sleep(1000);
                        if (AnonApp.getInstance().isRefresh()) {
                            getComments(cAdapter, postId);
                            AnonApp.getInstance().setRefresh(false);
                            success = true;

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        Button addComment = (Button) findViewById(R.id.addcomment);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentWriter c = new commentWriter(getApplicationContext(), parent);
                c.writePost(postId);
                refreshThread = new Thread(runnable);
                refreshThread.start();
            }
        });


        final SwipeRefreshLayout commentRefresher = (SwipeRefreshLayout) findViewById(R.id.refreshComments);

        commentRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getComments(cAdapter, postId);
                commentRefresher.setRefreshing(false);
            }
        });
    }


    private void getComments(final commentAdapter adapter, int id) {
        String url = "http://192.168.10.27:80/posts/" + id + "/comments";

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
                            commentList.add(0, tempComment);
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

    //update score when swiped left/right
    private void setRecyclerViewItemTouchListener() {
        //1
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(2, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                //2
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //3

                int position = viewHolder.getAdapterPosition();
                if (swipeDir == 8) {
                    cAdapter.addVote(position);
                } else {
                    cAdapter.removeVote(position);
                }

                cAdapter.notifyItemChanged(position);

            }
        };

        //4
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(comments);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(refreshThread != null) {
            refreshThread.interrupt();
        }
        success = true;
    }
}
