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
import com.example.mitchell.UI.ScreenSlidePlots.FuelEffectEfficiency;
import com.example.mitchell.UI.ScreenSlidePlots.TagEffectEfficiency;
import com.example.mitchell.UI.ScreenSlidePlots.cpkmOverTime;


public class DataPlotsActivity extends AppCompatActivity {

    private Integer carID;
    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficiency_vtime_plot);

        mPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(pagerAdapter);

        //get the carID and build the graph for it
        carID = getIntent().getIntExtra("carID", 0);
    }

    /**
     * Used to create the plot fragments to be displeayed on the screen
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        Context context;
        public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment plot;
            switch (position) {
                case 0:
                    plot = new EfficiencyVTimePlot();
                    break;
                case 1:
                    plot = new cpkmOverTime();
                    break;
                case 2:
                    plot = new FuelEffectEfficiency();
                    break;
                case 3:
                    plot = new TagEffectEfficiency();
                    break;
                default:
                    plot = new FuelEffectEfficiency();
                    break;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("carID", carID);
            plot.setArguments(bundle);
            return plot;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
