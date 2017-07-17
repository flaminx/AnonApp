package com.example.flaminx.anonapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.flaminx.anonapp.Pojo.NetworkCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.provider.Settings.Secure;

import static java.security.AccessController.getContext;


/**
 * Created by Flaminx on 06/03/2017.
 */

public class SplashActivity extends AppCompatActivity {
    Context c = this;
    private int runstate;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);

        runstate = -1;
        setupApp();
    }

    private void setupApp() {
        int cVersion = BuildConfig.VERSION_CODE;
        SharedPreferences sPrefs = getSharedPreferences("com.example.flaminx.anonapp", MODE_PRIVATE);
        int oVersion = sPrefs.getInt("anon_version", -1);


        //Normal run
        if (cVersion == oVersion) {
            Toast toast = Toast.makeText(this, "Normal Run", Toast.LENGTH_SHORT);
            // toast.show();
            runstate = 0;
        } else if (oVersion == -1) {
            Toast toast = Toast.makeText(this, "First Run", Toast.LENGTH_SHORT);
            toast.show();
            runstate = 1;
        } else if (cVersion > oVersion) {
            runstate = 2;
        }
        sPrefs.edit().putInt("anon_version", cVersion).apply();
        registerUser();
    }

    private void registerUser() {
        SharedPreferences sPrefs = getSharedPreferences("com.example.flaminx.anonapp", MODE_PRIVATE);

        final String id = sPrefs.getString("anon_login", "-1");
        final String password = android_id;
        final String REGISTER_URL = "http://192.168.10.27:80/users";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject user = new JSONObject(response);
                            String uId = user.getString("id");
                            String uScore = user.getString("score");
                            AnonApp.getInstance().setUserScore(Integer.parseInt(uScore));
                            SharedPreferences sPrefs = getSharedPreferences("com.example.flaminx.anonapp", MODE_PRIVATE);
                            sPrefs.edit().putString("anon_login", uId).apply();
                            Toast toast = Toast.makeText(SplashActivity.this, uScore, Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("runstate", runstate);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SplashActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        if (error instanceof AuthFailureError) {
                            Toast.makeText(SplashActivity.this, R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(SplashActivity.this, R.string.Oops, Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                            alertDialogBuilder.setTitle(getString(R.string.friendly_error));
                            alertDialogBuilder.setMessage("The Servers are down my dude");
                            alertDialogBuilder.setCancelable(false);


                            alertDialogBuilder.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    registerUser();
                                }
                            });

                            alertDialogBuilder.setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                    System.exit(0);
                                }
                            });

                            AlertDialog dialog = alertDialogBuilder.create();
                            dialog.show();
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

        AnonApp.getInstance().addToReqQ(stringRequest);

    }

}
