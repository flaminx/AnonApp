package com.example.flaminx.anonapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.flaminx.anonapp.Fragments.AppFragmentPagerAdapter;
import com.example.flaminx.anonapp.Pojo.FragmentInterface;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements FragmentInterface {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private final int PAGE_LIMIT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new AppFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
            // Give the TabLayout the ViewPager
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(PAGE_LIMIT);
        viewPager.setCurrentItem(1);
    }

    public void showUpoints(int n)
    {
        TextView uPoints = (TextView)findViewById(R.id.pointCount);
        uPoints.setText(Integer.toString(n));
    }




}

