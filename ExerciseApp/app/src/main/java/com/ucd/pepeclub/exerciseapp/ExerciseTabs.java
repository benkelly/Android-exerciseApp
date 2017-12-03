package com.ucd.pepeclub.exerciseapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ExerciseTabs extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String tabTitles[] = new String[]{"Walk", "Run", "Cycle"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_tabs);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exercise Monitor");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        for (int i = 0; i < tabTitles.length; i++) {
            Log.wtf("topkek", tabTitles[i]);
            tabLayout.addTab(tabLayout.newTab().setText(tabTitles[i]));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mViewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
                    Intent i = new Intent("TAG_REFRESH");
                    lbm.sendBroadcast(i);
                }
            }
        });


    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    ExerciseTracker et1 = new ExerciseTracker();
                    et1.EXERCISE_NAME = "walk";
                    et1.MAX_AVERAGE_SPEED = 4;
                    return et1;
                case 1:
                    ExerciseTracker et2 = new ExerciseTracker();
                    et2.EXERCISE_NAME = "run";
                    et2.MAX_AVERAGE_SPEED = 10;
                    return et2;
                case 2:
                    ExerciseTracker et3 = new ExerciseTracker();
                    et3.EXERCISE_NAME = "cycle";
                    et3.MAX_AVERAGE_SPEED = 15;
                    return et3;
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
