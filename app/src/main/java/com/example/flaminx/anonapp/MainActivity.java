package com.example.flaminx.anonapp;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.flaminx.anonapp.Fragments.AppFragmentPagerAdapter;
import com.example.flaminx.anonapp.Middleware.Tutorial;
import com.example.flaminx.anonapp.Middleware.customViewPager;
import com.example.flaminx.anonapp.Writers.postWriter;

import java.util.Locale;

public class MainActivity extends AppCompatActivity  {

    private customViewPager viewPager;
    private TabLayout tabLayout;
    private final int PAGE_LIMIT = 3;
    private int runstate;
    private View parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
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


        setContentView(R.layout.sliding_tabs);
        parent = findViewById(R.id.Main_layout);
        runstate = getIntent().getIntExtra("runstate",-1);
        FloatingActionButton createPost = (FloatingActionButton) findViewById(R.id.createPost);
        viewPager = (customViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new AppFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
            // Give the TabLayout the ViewPager
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(PAGE_LIMIT);
        viewPager.setCurrentItem(1);

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postWriter p = new postWriter(getApplicationContext(),parent);
                p.writePost();
            }
        });

        findViewById(R.id.Main_layout).post(new Runnable() {
            public void run() {
                launchOptions(runstate);
            }
        });

    }




    //This function is for special launch options E.G info about an update or first time running tutorial, can also add stuff to normal runs if need be
    private void launchOptions(int o)
    {
        switch (o)
        {
            case 0:
                Tutorial m = new Tutorial(this,parent);
                m.beginTutorial();
                break;
            case 1:
                Tutorial t = new Tutorial(this,parent);
                t.beginTutorial();
                break;
            case 2:
                break;
            default:
                break;
        }
    }







}

