package com.example.flaminx.anonapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flaminx.anonapp.R;
import com.example.flaminx.anonapp.Middleware.Tutorial;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class OptionsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static OptionsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        OptionsFragment fragment = new OptionsFragment();
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
        final View view = inflater.inflate(R.layout.activity_options, container, false);
        final View parent = view.findViewById(R.id.activity_options);
        final Button tutButton = (Button) view.findViewById(R.id.tutorial_button);



        view.findViewById(R.id.activity_options).post(new Runnable() {
            public void run() {
                tutButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Tutorial t = new Tutorial(getActivity(),parent);
                        t.beginTutorial();

                    }
                });
            }
        });
        return view;
    }
}
