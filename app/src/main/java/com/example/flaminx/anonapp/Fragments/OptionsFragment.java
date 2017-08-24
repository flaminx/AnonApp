package com.example.flaminx.anonapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.MainActivity;
import com.example.flaminx.anonapp.R;
import com.example.flaminx.anonapp.Middleware.Tutorial;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

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
        final Button deleteButton = (Button) view.findViewById(R.id.deleteAcc);
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
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete(getContext());
            }
        });

        final Spinner lSelect = (Spinner) view.findViewById(R.id.languageSelect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.language_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lSelect.setAdapter(adapter);
        switch (AnonApp.getInstance().getLanguage())
        {
            case "en":
                lSelect.setSelection(0);
                break;
            case "pl":
                lSelect.setSelection(1);
                break;
        }

lSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Resources res = getContext().getResources();
        Configuration config = new Configuration(res.getConfiguration());
        String cLanguage = AnonApp.getInstance().getLanguage();

        switch (position) {

            case 0:
            {
                if(!cLanguage.equals("en"))
                {
                    AnonApp.getInstance().setLanguage("en");
                    Intent refresh = new Intent(getContext(), MainActivity.class);
                    getActivity().finish();
                    startActivity(refresh);
                    break;
                }
                else break;
            }
            case 1:
            {
                if (!cLanguage.equals("pl"))
                {
                    AnonApp.getInstance().setLanguage("pl");
                    Intent refresh = new Intent(getContext(), MainActivity.class);
                    getActivity().finish();
                    startActivity(refresh);
                    break;
                }
                else break;
            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


});
        return view;
    }
    public void Delete(final Context context) {


        final String password = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String id = AnonApp.getInstance().getUserId();
        final String DELETE_URL = "http://192.168.10.27:80/users/delete";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, R.string.deleted, Toast.LENGTH_LONG).show();
                        SharedPreferences sPrefs = context.getSharedPreferences("com.example.flaminx.anonapp", MODE_PRIVATE);
                        sPrefs.edit().putString("anon_login", "-1").apply();
                        getActivity().finishAffinity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(SplashActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        if (error instanceof AuthFailureError) {
                            Toast.makeText(context, R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(context, R.string.notYours, Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(context, R.string.timeout, Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.deletePost);
        alertDialogBuilder.setCancelable(false);


        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AnonApp.getInstance().addToReqQ(stringRequest);
                dialog.dismiss();

            }
        });

        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}
