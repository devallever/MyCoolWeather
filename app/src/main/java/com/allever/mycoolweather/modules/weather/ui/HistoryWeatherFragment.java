package com.allever.mycoolweather.modules.weather.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.HistoryWeather;
import com.allever.mycoolweather.modules.weather.adapter.HistoryWeatherItemRecyclerAdapter;
import com.allever.mycoolweather.modules.weather.bean.HistoryWeatherItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allever on 17-5-8.
 */

public class HistoryWeatherFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryWeatherItemRecyclerAdapter historyWeatherItemRecyclerAdapter;
    private List<HistoryWeatherItem> historyWeatherItemList = new ArrayList<>();
    private String weatherId;

    public HistoryWeatherFragment(){

    }
    @SuppressLint("ValidFragment")
    public HistoryWeatherFragment(String weatherId){
        this.weatherId = weatherId;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_weather_fragment_layout,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.id_history_weather_fragment_recycler_view);

        setHistoryWeatherItemData();

        return view;
    }

    private void setHistoryWeatherItemData(){
        historyWeatherItemList.clear();
        List<HistoryWeather> historyWeatherList;
        historyWeatherList = DataSupport.where("weatherId=? order by date desc", weatherId).find(HistoryWeather.class);
        for (HistoryWeather historyWeather: historyWeatherList){
            HistoryWeatherItem historyWeatherItem = new HistoryWeatherItem();
            historyWeatherItem.setWeather(historyWeather.getWeather());
            historyWeatherItem.setDate(historyWeather.getDate());
            historyWeatherItem.setMax(historyWeather.getMax());
            historyWeatherItem.setMin(historyWeather.getMin());
            historyWeatherItemList.add(historyWeatherItem);
        }

        historyWeatherItemRecyclerAdapter = new HistoryWeatherItemRecyclerAdapter(getActivity(),historyWeatherItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(historyWeatherItemRecyclerAdapter);

    }
}
