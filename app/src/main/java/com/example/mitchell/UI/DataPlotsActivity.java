package com.example.mitchell.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.mitchell.UI.ScreenSlidePlots.EfficiencyVTimePlot;


public class DataPlotsActivity extends AppCompatActivity {

    private Integer carID;
    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficiency_vtime_plot);

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(pagerAdapter);

        //get the carID and build the graph for it
        carID = getIntent().getIntExtra("carID", 0);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        Context context;
        public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            EfficiencyVTimePlot plot;
            switch (position) {
                case 1:
                    plot = new EfficiencyVTimePlot();
                    break;
                default:
                    plot = new EfficiencyVTimePlot();
                    break;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("carID", carID);
            plot.setArguments(bundle);
//                    plot.carID = carID;
            return plot;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
