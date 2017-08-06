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
public class commentWriter {
    Context context;
    View parentView;
    private String android_id;


    public commentWriter(Context vContext, View pView) {
        context = vContext;
        parentView = pView;

    }

    public void writePost(final int postId) {
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
        View customView = inflater.inflate(R.layout.commentwriter_layout, null);

        final PopupWindow commentPopup = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            commentPopup.setElevation(5);
        }
        commentPopup.setOutsideTouchable(false);
        commentPopup.setFocusable(true);
        commentPopup.setAnimationStyle(R.style.AnimationPopup);
        commentPopup.showAtLocation(parentView, Gravity.CENTER, 0, 0);


        final EditText pText = (EditText) customView.findViewById(R.id.PopupEditComment);

        Button quit = (Button) customView.findViewById(R.id.popupBackComment);
        Button next = (Button) customView.findViewById(R.id.popupSubmitComment);

        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                commentPopup.dismiss();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                submitComment(pText.getText().toString(),postId);
                commentPopup.dismiss();
            }
        });




    }

    private void submitComment(final String text, final int pId) {




        android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String id = AnonApp.getInstance().getUserId();
        final String password = android_id;
        final String POST_URL = AnonApp.getInstance().getWebAddress()+"/comments";
        final String postId = Integer.toString(pId);



        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast toast = Toast.makeText(context, "Posted", Toast.LENGTH_SHORT);
                        toast.show();
                        AnonApp.getInstance().setRefresh(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                        if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(context, R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                        }
                        else if(error instanceof ServerError)
                        {
                            Toast.makeText(context, R.string.post_cooldown, Toast.LENGTH_LONG).show();
                        }
                        else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(context, R.string.timeout, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("post_id",postId);
                params.put("user_pass", password);
                params.put("text", text);
                return params;
            }

        };

        AnonApp.getInstance().addToReqQ(stringRequest);
    }


}
