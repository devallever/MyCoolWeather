package com.allever.mycoolweather.modules.city.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.City;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.modules.city.bean.CityItem;
import com.allever.mycoolweather.modules.city.event.DeleteCityEvent;
import com.allever.mycoolweather.modules.city.event.FixCityEvent;
import com.allever.mycoolweather.modules.city.listener.OnMoveAndSwipedListener;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by allever on 17-4-25.
 */

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityViewHolder> implements OnMoveAndSwipedListener{

    private static final String TAG = "CityRecyclerAdapter";
    
    private List<CityItem> cityItemList;
    private Context context;
    private boolean isFix;

    public CityRecyclerAdapter(Context context, List<CityItem> cityItems){
        this.cityItemList = cityItems;
        this.context = context;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_item,parent,false);
        CityViewHolder viewHolder = new CityViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        CityItem cityItem = cityItemList.get(position);

        Weather weather = DataSupport.where("weatherId=?",cityItem.getWeatherId()).find(Weather.class).get(0);

        holder.tv_tmp.setText(cityItem.getTmp() + "°C");
        holder.tv_county.setText(cityItem.getCounty());
        holder.tv_weather.setText(cityItem.getWeather());

        if (cityItem.getWeather().contains("雨")) Glide.with(context).load(R.mipmap.city_beijing_rainy).into(holder.iv_bg);
        else if (cityItem.getWeather().contains("晴")) Glide.with(context).load(R.mipmap.city_beijing_sunny).into(holder.iv_bg);
        else if (cityItem.getWeather().contains("云")) Glide.with(context).load(R.mipmap.city_beijing_cloudy).into(holder.iv_bg);
        else Glide.with(context).load(R.mipmap.city_other_rainy).into(holder.iv_bg);
        if (weather.getIsShow().equals("1")){
            holder.iv_fix.setImageResource(R.mipmap.fix_blue);
        }else {
            holder.iv_fix.setImageResource(R.mipmap.fix_gray);
        }

        if (weather.getIsNotify().equals("1")){
            holder.iv_notify.setImageResource(R.mipmap.notification_blue_13227a);
        }else {
            holder.iv_notify.setImageResource(R.mipmap.notification_gray_515151);
        }

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //noti
                DataSupport.deleteAll(Weather.class,"weatherId=?",weather.getWeatherId());
                cityItemList.remove(position);
                //notifyItemRemoved(position);
                notifyDataSetChanged();
                DeleteCityEvent deleteCityEvent = new DeleteCityEvent();
                EventBus.getDefault().post(deleteCityEvent);
            }
        });

        holder.iv_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (weather.getIsNotify().equals("1")){
                    weather.setIsNotify("0");
                    weather.updateAll("weatherId=?", weather.getWeatherId());
                    holder.iv_notify.setImageResource(R.mipmap.notification_gray_515151);
                }else {
                    weather.setIsNotify("1");
                    weather.updateAll("weatherId=?", weather.getWeatherId());
                    holder.iv_notify.setImageResource(R.mipmap.notification_blue_13227a);
                }
            }
        });


        holder.iv_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weather.getIsShow().equals("1")){
                    holder.iv_fix.setImageResource(R.mipmap.fix_gray);
                    weather.setIsShow("0");
                    weather.updateAll("weatherId=?", weather.getWeatherId());
                }else {
                    weather.setIsShow("1");
                    weather.updateAll("weatherId=?", weather.getWeatherId());
                    holder.iv_fix.setImageResource(R.mipmap.fix_blue);
                }

                FixCityEvent fixCityEvent = new FixCityEvent();
                EventBus.getDefault().post(fixCityEvent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return cityItemList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.d(TAG, "fromPosition = " + fromPosition + "\ntoPosition = " + toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        DataSupport.deleteAll(Weather.class,"weatherId=?",cityItemList.get(position).getWeatherId());
        cityItemList.remove(position);
        notifyItemRemoved(position);
        DeleteCityEvent deleteCityEvent = new DeleteCityEvent();
        EventBus.getDefault().post(deleteCityEvent);
    }

    class CityViewHolder extends RecyclerView.ViewHolder{
        TextView tv_tmp;
        TextView tv_county;
        TextView tv_weather;
        ImageView iv_notify;
        ImageView iv_delete;
        ImageView iv_fix;
        ImageView iv_bg;
        CardView cardView;
        public CityViewHolder(View itemView){
            super(itemView);

            tv_tmp = (TextView)itemView.findViewById(R.id.id_city_item_tv_tmp);
            tv_county = (TextView)itemView.findViewById(R.id.id_city_item_tv_county);
            tv_weather = (TextView)itemView.findViewById(R.id.id_city_item_tv_weather);

            iv_delete = (ImageView)itemView.findViewById(R.id.id_city_item_iv_delete);
            iv_fix = (ImageView)itemView.findViewById(R.id.id_city_item_iv_fix);
            iv_notify = (ImageView)itemView.findViewById(R.id.id_city_item_iv_notify);

            cardView = (CardView)itemView.findViewById(R.id.id_city_item_card_view);
            iv_bg = (ImageView)itemView.findViewById(R.id.id_city_item_iv_bg);

        }
    }
}
