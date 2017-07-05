package com.example.flaminx.anonapp;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.flaminx.anonapp.Fragments.AppFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity  {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private final int PAGE_LIMIT = 3;
    private int runstate;
    private View parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_tabs);
        parent = findViewById(R.id.Main_layout);
        runstate = getIntent().getIntExtra("runstate",-1);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new AppFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
            // Give the TabLayout the ViewPager
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(PAGE_LIMIT);
        viewPager.setCurrentItem(1);


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
                //call the "shit has gone wrong class"
        }
    }






}

