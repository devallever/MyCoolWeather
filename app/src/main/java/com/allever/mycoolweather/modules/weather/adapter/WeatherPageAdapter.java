package com.allever.mycoolweather.modules.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.allever.mycoolweather.modules.weather.ui.WeatherFragment;

import java.util.List;

/**
 * Created by allever on 17-4-24.
 */

public class WeatherPageAdapter extends FragmentPagerAdapter {

    private List<WeatherFragment> weatherFragmentList;
    private List<String> titleList;


    public WeatherPageAdapter(FragmentManager fragmentManager, List<WeatherFragment> fragmentList, List<String> titleList){
        super(fragmentManager);
        this.weatherFragmentList = fragmentList;
        this.titleList = titleList;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return weatherFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return weatherFragmentList.size();
    }
}
