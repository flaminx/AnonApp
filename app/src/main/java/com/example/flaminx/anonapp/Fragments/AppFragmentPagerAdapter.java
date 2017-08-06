package com.example.flaminx.anonapp.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;

import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.R;

import java.util.Locale;

/**
 * Created by Flaminx on 05/03/2017.
 */

public class AppFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context context;

    public AppFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        Resources resources;
        Configuration configuration;
        DisplayMetrics displayMetrics;
        resources = context.getResources();
        configuration = resources.getConfiguration();
        displayMetrics = resources.getDisplayMetrics();
        String lan = AnonApp.getInstance().getLanguage();
        if (lan.equals(" ")) {
            configuration.locale = Locale.getDefault();
        } else {
            configuration.locale = new Locale(lan);
        }
        resources.updateConfiguration(configuration, displayMetrics);
        tabTitles = context.getResources().getStringArray(R.array.tab_array);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                return UserFragment.newInstance(0);
            case 1:
                return PostsFragment.newInstance(1);
            case 2:
                return OptionsFragment.newInstance(2);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}