package com.example.flaminx.anonapp.Writers;

/**
 * Created by Flaminx on 15/07/2017.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Flaminx on 03/07/2017.
 */
//This class handles the app tutorial, should be called from fresh install or options menu
public class postWriter {
    Context context;
    View parentView;
    private String android_id;


    public postWriter(Context vContext, View pView) {
        context = vContext;
        parentView = pView;


    }

    public void writePost() {
        Resources resources;
        Configuration configuration;
        DisplayMetrics displayMetrics;

        resources = context.getResources();
        configuration = resources.getConfiguration();
        displayMetrics = resources.getDisplayMetrics();
        String lan = AnonApp.getInstance().getLanguage();
        configuration.locale = new Locale(lan);
        resources.updateConfiguration(configuration, displayMetrics);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.postwriter_layout, null);

        final PopupWindow postPopup = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postPopup.setElevation(5);
        }
        postPopup.setOutsideTouchable(false);
        postPopup.setFocusable(true);
        postPopup.setAnimationStyle(R.style.AnimationPopup);
        postPopup.showAtLocation(parentView, Gravity.CENTER, 0, 0);

        final EditText pTitle = (EditText) customView.findViewById(R.id.PopupEditTitle);
        final EditText pText = (EditText) customView.findViewById(R.id.PopupEditText);

        Button quit = (Button) customView.findViewById(R.id.popupBack);
        Button next = (Button) customView.findViewById(R.id.popupSubmit);

        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                postPopup.dismiss();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                submitPost(pText.getText().toString(), pTitle.getText().toString(), postPopup);

            }
        });


    }

    private void submitPost(final String text, final String title, PopupWindow p) {


        final PopupWindow Pwindow = p;
        android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String id = AnonApp.getInstance().getUserId();
        final String password = android_id;
        final String POST_URL = AnonApp.getInstance().getWebAddress() +"/posts";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Toast toast = Toast.makeText(context, "Posted", Toast.LENGTH_SHORT);
                        toast.show();
                        AnonApp.getInstance().setRefresh(true);
                        Pwindow.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                        if (error instanceof AuthFailureError) {
                            Toast.makeText(context, R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            if (error.networkResponse.statusCode == 409) {

                                Toast.makeText(context, R.string.post_cooldown, Toast.LENGTH_LONG).show();
                            } else if (error.networkResponse.statusCode == 400) {
                                Toast.makeText(context, R.string.post_requirements, Toast.LENGTH_LONG).show();
                            }
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
                params.put("title", title);
                params.put("text", text);
                return params;
            }

        };

        AnonApp.getInstance().addToReqQ(stringRequest);
    }


}
