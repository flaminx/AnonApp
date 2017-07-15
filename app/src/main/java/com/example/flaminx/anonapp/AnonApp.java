package com.example.flaminx.anonapp;

import android.app.Application;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.leakcanary.LeakCanary;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Flaminx on 08/07/2017.
 */

public class AnonApp extends Application {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private static AnonApp instance;
    private static RequestQueue reqQ;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        RequestQueue requestQueue = Volley.newRequestQueue(this);
    }

    public static synchronized AnonApp getInstance()
    {
        return instance;
    }

    public RequestQueue getReqQ()
    {
        if(reqQ == null)
        {
            reqQ = Volley.newRequestQueue(getApplicationContext());
        }
        return reqQ;
    }
    public <T> void  addToReqQ(Request<T> request, String tag)
    {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getReqQ().add(request);
    }
    public <T> void  addToReqQ(Request<T> request)
    {
        request.setTag(TAG);
        getReqQ().add(request);
    }

    public void cancelReq(Object tag)
    {
        if(reqQ != null)
        {
            reqQ.cancelAll(tag);
        }
    }




    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}