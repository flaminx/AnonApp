package com.example.flaminx.anonapp;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.leakcanary.LeakCanary;

import java.util.Locale;

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

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    private int userScore = -1;
    private static AnonApp instance;
    private static RequestQueue reqQ;

    public String getWebAddress() {
        return webAddress;
    }

    private final String webAddress = "http://192.168.10.27:80";

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    private String Language = "en";

    private int thisPage = 0;

    public int getLastpage() {
        return lastpage;
    }

    public void setLastpage(int lastpage) {
        this.lastpage = lastpage;
    }

    public int getThisPage() {
        return thisPage;
    }

    public void setThisPage(int thisPage) {
        this.thisPage = thisPage;
    }

    private int lastpage = 0;

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    private boolean refresh = false;

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

    public static synchronized AnonApp getInstance() {
        return instance;
    }

    public RequestQueue getReqQ() {
        if (reqQ == null) {
            reqQ = Volley.newRequestQueue(getApplicationContext());
        }
        return reqQ;
    }

    public <T> void addToReqQ(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getReqQ().add(request);
    }

    public <T> void addToReqQ(Request<T> request) {
        request.setTag(TAG);
        getReqQ().add(request);
    }

    public void cancelReq(Object tag) {
        if (reqQ != null) {
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