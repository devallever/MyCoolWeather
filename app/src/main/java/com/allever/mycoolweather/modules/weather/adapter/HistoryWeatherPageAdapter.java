package com.allever.mycoolweather.modules.weather.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.allever.mycoolweather.modules.weather.ui.HistoryWeatherFragment;

import java.util.List;

/**
 * Created by allever on 17-5-8.
 */

public class HistoryWeatherPageAdapter extends FragmentPagerAdapter {
    private List<HistoryWeatherFragment> historyWeatherFragmentList;
    private List<String> titleList;

    public HistoryWeatherPageAdapter(FragmentManager fragmentManager,
                                     List<HistoryWeatherFragment> historyWeatherFragmentList,
                                     List<String> titleList){
        super(fragmentManager);
        this.historyWeatherFragmentList = historyWeatherFragmentList;
        this.titleList = titleList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return historyWeatherFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return historyWeatherFragmentList.size();
    }
}
