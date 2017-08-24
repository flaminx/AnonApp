package com.example.flaminx.anonapp.Fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.Pojo.Post;
import com.example.flaminx.anonapp.R;
import com.example.flaminx.anonapp.Middleware.postsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class PostsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private boolean loading = true;
    private int mPage;
    private String jsonResponse;
    private int[] rbgArray = {211, 211, 214}; // colours for recycler items
    private ArrayList<Post> postList = new ArrayList<Post>();
    private RecyclerView posts;
    private LinearLayoutManager postsLayout;
    private postsAdapter postAdapter;
    private SwipeRefreshLayout postRefresher;
    private Toolbar postFilter;
    private Runnable runnable;
    private volatile boolean success;

    public static PostsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PostsFragment fragment = new PostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Set up refresh thread
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_posts, container, false);


        //set up the RecyclerView
        postAdapter = new postsAdapter(postList);
        posts = (RecyclerView) view.findViewById(R.id.postList);
        posts.setHasFixedSize(false);
        postsLayout = new LinearLayoutManager(getActivity());
        posts.setLayoutManager(postsLayout);
        posts.setAdapter(postAdapter);
        postFilter = (Toolbar) view.findViewById(R.id.postFilter);
        postFilter.setTitle(R.string.Filter);
        postRefresher = (SwipeRefreshLayout) view.findViewById(R.id.refreshPosts);

        //On pull up refresh the posts
        postRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getPosts(postAdapter, true);
                postRefresher.setRefreshing(false);
            }
        });
        setRecyclerViewItemTouchListener();

        //Load Posts
        getPosts(postAdapter, true);

        //Detect when last post loaded and get more

        posts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = postsLayout.getChildCount();
                    int totalItemCount = postsLayout.getItemCount();
                    int pastVisiblesItems = postsLayout.findFirstVisibleItemPosition();
                    int thisPage = AnonApp.getInstance().getThisPage();
                    int lastPage = AnonApp.getInstance().getLastpage();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            if (thisPage < lastPage) {
                                AnonApp.getInstance().setThisPage(thisPage + 1);
                                getPosts(postAdapter, false);
                            }
                        }
                    }
                }

            }
        });

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                    postAdapter.addVote(position);
                } else {
                    postAdapter.removeVote(position);
                }
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#d3d3d6"));
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

                    float limit = dX / 30;
                    if (isCurrentlyActive) {
                        if (dX > 0) {
                            //rbgArray[0] += limit;
                            viewHolder.itemView.setBackgroundColor(Color.rgb(rbgArray[0] - Math.round(limit), rbgArray[1] + Math.round(limit), rbgArray[2] - Math.round(limit)));
                        } else if (dX < 0) {
                            //rbgArray[1] += limit;
                            viewHolder.itemView.setBackgroundColor(Color.rgb(rbgArray[0] - Math.round(limit), rbgArray[1] + Math.round(limit), rbgArray[2] + Math.round(limit)));
                        }
                        //rgb(211,211,214)
                    }
                    else viewHolder.itemView.setBackgroundColor(Color.rgb(211, 211, 214));
                }
            }
        };
        //4
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(posts);
    }

    //Retrieve posts from server
    private void getPosts(final postsAdapter adapter, final boolean frontOrEnd) {

        if (frontOrEnd) {
            String url = "http://192.168.10.27:80/posts?page=1";
            postList.clear();
            adapter.notifyDataSetChanged();
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        AnonApp.getInstance().setLastpage(response.getInt("last_page"));
                        AnonApp.getInstance().setThisPage(response.getInt("current_page"));
                        JSONArray Jpost = null;
                        Jpost = response.getJSONArray("data");
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
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof ServerError) {
                        Toast.makeText(getContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                    }
                }


            });
            AnonApp.getInstance().addToReqQ(postRequest);
        } else {
            String url = "http://192.168.10.27:80/posts?page=" + AnonApp.getInstance().getThisPage();
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
                        Toast.makeText(getContext(), R.string.Oops, Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                    }
                }


            });
            AnonApp.getInstance().addToReqQ(postRequest);
        }


    }

}








