package com.example.flaminx.anonapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.MainActivity;
import com.example.flaminx.anonapp.Pojo.Post;
import com.example.flaminx.anonapp.R;
import com.example.flaminx.anonapp.postsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class PostsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private String jsonResponse;
    private ArrayList<Post> postList = new ArrayList<Post>();
    private RecyclerView posts;
    private RecyclerView.LayoutManager postsLayout;
    private postsAdapter postAdapter;
    private SwipeRefreshLayout postRefresher;
    private Toolbar postFilter;

    public static PostsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PostsFragment fragment = new PostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_posts, container, false);




        postAdapter = new postsAdapter(postList);
        posts = (RecyclerView) view.findViewById(R.id.postList);
        posts.setHasFixedSize(false);
        postsLayout = new LinearLayoutManager(getActivity());
        posts.setLayoutManager(postsLayout);
        posts.setAdapter(postAdapter);
        postFilter = (Toolbar) view.findViewById(R.id.postFilter);
        postFilter.setTitle(R.string.Filter);
        postRefresher = (SwipeRefreshLayout) view.findViewById(R.id.refreshPosts);

        postRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getPosts(postAdapter);
                postRefresher.setRefreshing(false);
            }
        });
        setRecyclerViewItemTouchListener();

        getPosts(postAdapter);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


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
                    postAdapter.addVote(position);
                } else {
                    postAdapter.removeVote(position);
                }

                postAdapter.notifyItemChanged(position);

            }
        };

        //4
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(posts);
    }

    private void getPosts(final postsAdapter adapter) {
        String url = "http://192.168.10.27:80/posts";

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray Jpost = null;
                    Jpost = response.getJSONArray("data");
                    boolean exists = false;
                    int loc = 0;

                    for (int i = 0; i < Jpost.length(); i++) {

                        JSONObject cPost = Jpost.getJSONObject(i);
                        for (int j = 0; j < postList.size(); j++) {
                            if (cPost.getInt("id") == postList.get(j).getPostId()) {
                                exists = true;
                                loc = j;
                                break;
                            }
                        }
                        if (!exists) {
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
                        } else if (exists) {
                            postList.get(loc).setPostScore(cPost.getInt("votes"));
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
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                if (error instanceof ServerError) {
                    Toast.makeText(getContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                }
            }


        });

        AnonApp.getInstance().addToReqQ(postRequest);

    }

}








