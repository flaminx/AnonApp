package com.example.flaminx.anonapp.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.R;
import com.example.flaminx.anonapp.userCommentsActivity;
import com.example.flaminx.anonapp.userPostsActivity;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class UserFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private Handler dynUiHandler;
    private TextView uPointsUI;
    private int uPointsValue;
    private int usersPoints;
    private int delay = 1;

    public static UserFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.activity_user, container, false);
    uPointsUI = (TextView) view.findViewById(R.id.pointCount);
        uPointsValue = 0;
        usersPoints = AnonApp.getInstance().getUserScore();
        dynUiHandler = new Handler();
        Button getPosts = (Button) view.findViewById(R.id.userPosts);
        Button getComments = (Button) view.findViewById(R.id.userComments);

        getPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),userPostsActivity.class);
                startActivity(intent);
            }
        });

        getComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),userCommentsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            uPointsUI.setText(Integer.toString(uPointsValue));
            Toast toast = Toast.makeText(getActivity(),"Visible",Toast.LENGTH_SHORT);
            toast.show();
            dynUiHandler.post(pointsUpdate);
        }
        else {
           uPointsValue = 0;
        }
    }


    private Runnable pointsUpdate = new Runnable() {
        @Override
        public void run() {
            uPointsUI.setText(Integer.toString(uPointsValue));

            if(uPointsValue < usersPoints) {
                if((usersPoints - 100 <= 0)||((usersPoints-uPointsValue) * 0.05) < 1)
                {
                    delay++;
                    uPointsValue++;
                }
                else uPointsValue += (usersPoints-uPointsValue) * 0.05;


                dynUiHandler.postDelayed(this,delay);

            }
            else delay = 1;

        }
    };

}
