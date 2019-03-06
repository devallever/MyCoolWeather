package com.allever.mycoolweather.modules.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.modules.weather.bean.HistoryWeatherItem;

import java.util.List;

/**
 * Created by allever on 17-5-8.
 */

public class HistoryWeatherItemRecyclerAdapter extends RecyclerView.Adapter<HistoryWeatherItemRecyclerAdapter.HistoryWeatherViewHolder> {

    private Context context;
    private List<HistoryWeatherItem> historyWeatherItemList;
    public HistoryWeatherItemRecyclerAdapter(Context context, List<HistoryWeatherItem> historyWeatherItemList){
        this.context = context;
        this.historyWeatherItemList = historyWeatherItemList;
    }

    @Override
    public HistoryWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.history_weather_item,parent,false);
        HistoryWeatherViewHolder historyWeatherViewHolder = new HistoryWeatherViewHolder(v);
        return historyWeatherViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryWeatherViewHolder holder, int position) {
        HistoryWeatherItem historyWeatherItem = historyWeatherItemList.get(position);
        holder.tv_date.setText(historyWeatherItem.getDate());
        holder.tv_weather.setText(historyWeatherItem.getWeather());
        holder.tv_min.setText(historyWeatherItem.getMin());
        holder.tv_max.setText(historyWeatherItem.getMax());
    }

    @Override
    public int getItemCount() {
        return historyWeatherItemList.size();
    }

    class HistoryWeatherViewHolder extends RecyclerView.ViewHolder{
        TextView tv_date;
        TextView tv_weather;
        TextView tv_min;
        TextView tv_max;
        public HistoryWeatherViewHolder(View itemView){
            super(itemView);
            tv_date = (TextView)itemView.findViewById(R.id.id_history_weather_item_tv_date);
            tv_weather = (TextView)itemView.findViewById(R.id.id_history_weather_item_tv_weather);
            tv_min= (TextView)itemView.findViewById(R.id.id_history_weather_item_tv_min_tmp);
            tv_max = (TextView)itemView.findViewById(R.id.id_history_weather_item_tv_max_tmp);
        }
    }
}
