package com.example.flaminx.anonapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.provider.Settings.Secure;


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
        Resources resources;
        Configuration configuration;
        DisplayMetrics displayMetrics;
        resources = this.getResources();
        configuration = resources.getConfiguration();
        displayMetrics = resources.getDisplayMetrics();
        String lan = AnonApp.getInstance().getLanguage();
        configuration.locale = new Locale(lan);
        resources.updateConfiguration(configuration, displayMetrics);
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

            runstate = 0;
        } else if (oVersion == -1) {
            runstate = 1;
        } else if (cVersion > oVersion) {
            runstate = 2;
        }
        sPrefs.edit().putInt("anon_version", cVersion).apply();
        if (oVersion == -1) {
            String[] languages = getResources().getStringArray(R.array.language_array);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
            alertDialogBuilder.setTitle("Language");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            AnonApp.getInstance().setLanguage("en");
                            break;
                        case 1:
                            AnonApp.getInstance().setLanguage("pl");
                            break;
                        default:
                            AnonApp.getInstance().setLanguage("en");
                    }
                    registerUser();
                }
            });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        } else registerUser();
    }

    private void registerUser() {
        SharedPreferences sPrefs = getSharedPreferences("com.example.flaminx.anonapp", MODE_PRIVATE);
        final String id = sPrefs.getString("anon_login", "-1");
        final String password = android_id;
        final String REGISTER_URL = AnonApp.getInstance().getWebAddress() + "/users";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject user = new JSONObject(response);
                            String uId = user.getString("id");
                            String uScore = user.getString("score");
                            AnonApp.getInstance().setUserScore(Integer.parseInt(uScore));
                            AnonApp.getInstance().setUserId(uId);
                            SharedPreferences sPrefs = getSharedPreferences("com.example.flaminx.anonapp", MODE_PRIVATE);
                            sPrefs.edit().putString("anon_login", uId).apply();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("runstate", runstate);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(SplashActivity.this, R.string.Oops, Toast.LENGTH_LONG).show();
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
                            alertDialogBuilder.setMessage(R.string.timeout);
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
                                    finishAffinity();
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
