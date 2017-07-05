package com.example.flaminx.anonapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.flaminx.anonapp.Pojo.Post;
import com.example.flaminx.anonapp.R;
import com.example.flaminx.anonapp.postsAdapter;

import java.util.ArrayList;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class PostsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    private ArrayList<Post> postList = new ArrayList<Post>();
    private RecyclerView posts;
    private RecyclerView.LayoutManager postsLayout;
    private postsAdapter postAdapter;
    private SwipeRefreshLayout postRefresher;
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
        Post charlie = new Post(1);
        Post sierra = new Post(2);
        postList.add(charlie);
        postList.add(sierra);
        postAdapter = new postsAdapter(postList);
        posts = (RecyclerView) view.findViewById(R.id.postList);
        posts.setHasFixedSize(false);
        postsLayout = new LinearLayoutManager(getActivity());
        posts.setLayoutManager(postsLayout);
        posts.setAdapter(postAdapter);

        postRefresher = (SwipeRefreshLayout) view.findViewById(R.id.refreshPosts);

        postRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refresh();
            }
        });


        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void refresh()
    {

            ArrayList<Post> pl = new ArrayList<Post>();
            Post delta = new Post(3);
            Post echo = new Post(4);
            pl.add(delta);
            pl.add(echo);
            postAdapter.addAll(pl);

        postRefresher.setRefreshing(false);
    }
}
