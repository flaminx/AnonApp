package com.example.flaminx.anonapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaminx.anonapp.Pojo.FragmentInterface;
import com.example.flaminx.anonapp.R;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class UserFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

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

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FragmentInterface ufi = (FragmentInterface) getActivity();
                int n = 0;
                while (n < 500)
                {
                    ufi.showUpoints(n);
                    n++;
                }
            }

        };
        Thread mythread = new Thread(runnable);
        mythread.start();

    }

}
