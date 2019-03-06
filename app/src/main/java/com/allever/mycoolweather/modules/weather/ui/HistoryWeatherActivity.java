package com.allever.mycoolweather.modules.weather.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.allever.mycoolweather.BaseActivity;
import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.Image;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.modules.weather.adapter.HistoryWeatherPageAdapter;
import com.allever.mycoolweather.utils.CommonUtil;
import com.allever.mycoolweather.utils.Constant;
import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allever on 17-5-8.
 */

public class HistoryWeatherActivity extends BaseActivity {

    private static final String TAG = "HistoryWeatherActivity";

    private Toolbar toolbar;
    private ImageView iv_bg;

    private ViewPager viewPager;
    private HistoryWeatherPageAdapter historyWeatherPageAdapter;
    private List<HistoryWeatherFragment> historyWeatherFragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    private int page_position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_weather_activity_layout);

        page_position = getIntent().getIntExtra("position",0);

        toolbar = (Toolbar)findViewById(R.id.id_history_weather_activity_toolbar);
        toolbar.setTitle(R.string.history_weather);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_android_48);
        }

        viewPager = (ViewPager)findViewById(R.id.id_history_weather_activity_view_pager);



        /*
        historyWeatherFragmentList.add(new HistoryWeatherFragment());
        historyWeatherFragmentList.add(new HistoryWeatherFragment());
        historyWeatherFragmentList.add(new HistoryWeatherFragment());

        titleList.add("北京");
        titleList.add("番禺");
        titleList.add("四会");
        */

        TabLayout tabLayout = (TabLayout) findViewById(R.id.id_history_weather_activity_table_layout);
        tabLayout.setupWithViewPager(viewPager);

        iv_bg = (ImageView) findViewById(R.id.id_history_weather_activity_iv_bg);

        showBackground();

        showHistoryWeather();


    }

    private void showHistoryWeather(){
        List<Weather> weatherList = DataSupport.findAll(Weather.class);
        for (Weather weather: weatherList){
            weather.getWeatherId();
            titleList.add(weather.getCountyName());
            historyWeatherFragmentList.add(new HistoryWeatherFragment(weather.getWeatherId()));
        }
        historyWeatherPageAdapter = new HistoryWeatherPageAdapter(getSupportFragmentManager(),historyWeatherFragmentList,titleList);
        viewPager.setAdapter(historyWeatherPageAdapter);
        viewPager.setCurrentItem(page_position);
    }


    private void showBackground(){

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_bg",false)){
            String bgPath = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_bg_path","");
            if (!TextUtils.isEmpty(bgPath)){
                Glide.with(this).load(bgPath).into(iv_bg);
            }
        }else {
            String date = CommonUtil.getTodayFormatDate();
            String name;
            String path = Constant.IMAGE_PATH;
            List<Image> imageList = new ArrayList<>();
            imageList = DataSupport.where("date=?", date).find(Image.class);
            if (imageList.size()>0) {
                Image image = imageList.get(0);
                name = image.getName();
                Glide.with(this).load(path + name).into(iv_bg);
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                //setResult(RESULT_OK);
                finish();
                break;
        }
        return true;
    }
}
