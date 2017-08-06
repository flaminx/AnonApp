package com.example.flaminx.anonapp.Pojo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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
import com.example.flaminx.anonapp.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class Post {

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBlurb() {
        return postBlurb;
    }

    public void setPostBlurb(String postBlurb) {
        this.postBlurb = postBlurb;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    private String postTitle;
    private String postBlurb;
    private String postText;
    private String postDate;
    private int postScore;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    private int postId;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }


    public int getPostScore() {
        return postScore;
    }

    public void setPostScore(int postScore) {
        this.postScore = postScore;
    }

    public Post() {
        postTitle = "";
        postBlurb = "";
        postText = "";
        postDate = "";
        postScore = 0;
        postId = -1;
    }

    public void Delete(final Context context, int postId) {


        final String password = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String id = AnonApp.getInstance().getUserId();
        final String pId = Integer.toString(postId);
        final String DELETE_URL = AnonApp.getInstance().getWebAddress()+"/posts/delete";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, R.string.deleted, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                        if (error instanceof AuthFailureError) {
                            Toast.makeText(context, R.string.notYours, Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(context, R.string.Oops, Toast.LENGTH_LONG).show();
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
                params.put("post_id",pId);
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
