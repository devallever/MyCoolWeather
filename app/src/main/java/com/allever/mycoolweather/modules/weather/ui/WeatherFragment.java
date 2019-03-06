package com.allever.mycoolweather.modules.weather.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.bean.hweather.Daily_forecast;
import com.allever.mycoolweather.bean.hweather.HeWeather5;
import com.allever.mycoolweather.bean.hweather.Root;
import com.allever.mycoolweather.bean.hweather.Suggestion;
import com.allever.mycoolweather.modules.weather.event.MessageEvent;
import com.allever.mycoolweather.modules.weather.event.RefreshBackGroundEvent;
import com.allever.mycoolweather.utils.Constant;
import com.allever.mycoolweather.utils.OkHttpUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by allever on 17-4-23.
 */

public class WeatherFragment extends Fragment {

    private static final String TAG = "WeatherFragment";

    SwipeRefreshLayout swipeRefreshLayout;
    private String weather_id = "";

    private List<Weather> weatherList;
    private String weatherInfo;

    private TextView tv_real_time;
    private TextView tv_real_tmp;
    private TextView tv_real_weather;

    private TextView tv_forecast_today_date;
    private TextView tv_forecast_today_weather;
    private TextView tv_forecast_today_min_tmp;
    private TextView tv_forecast_today_max_tmp;

    private TextView tv_forecast_tomorrow_date;
    private TextView tv_forecast_tomorrow_weather;
    private TextView tv_forecast_tomorrow_min_tmp;
    private TextView tv_forecast_tomorrow_max_tmp;

    private TextView tv_forecast_after_tomorrow_date;
    private TextView tv_forecast_after_tomorrow_weather;
    private TextView tv_forecast_after_tomorrow_min_tmp;
    private TextView tv_forecast_after_tomorrow_max_tmp;


    private TextView tv_air_quality;
    private TextView tv_aqi;
    private TextView tv_pm25;

    private TextView tv_comf;
    private TextView tv_drsg;//穿着
    private TextView tv_sport;

    public WeatherFragment(){

    }
    @SuppressLint("ValidFragment")
    public WeatherFragment(String weather_id){
        this.weather_id = weather_id;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment_layout, container,false);

        tv_real_time = (TextView)view.findViewById(R.id.id_weather_fragment_tv_real_time);
        tv_real_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_real_temp);
        tv_real_weather = (TextView)view.findViewById(R.id.id_weather_fragment_tv_real_weather);

        tv_forecast_today_date = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_today_date);
        tv_forecast_today_weather = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_today_weather);
        tv_forecast_today_min_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_today_min_tmp);
        tv_forecast_today_max_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_today_max_tmp);

        tv_forecast_tomorrow_date = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_tomorrow_date);
        tv_forecast_tomorrow_weather = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_tomorrow_weather);
        tv_forecast_tomorrow_min_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_tomorrow_min_tmp);
        tv_forecast_tomorrow_max_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_tomorrow_max_tmp);

        tv_forecast_after_tomorrow_date = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_after_tomorrow_date);
        tv_forecast_after_tomorrow_weather = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_after_tomorrow_weather);
        tv_forecast_after_tomorrow_min_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_after_tomorrow_min_tmp);
        tv_forecast_after_tomorrow_max_tmp = (TextView)view.findViewById(R.id.id_weather_fragment_tv_forecast_after_tomorrow_max_tmp);

        tv_air_quality = (TextView) view.findViewById(R.id.id_weather_fragment_tv_air_quality);
        tv_aqi = (TextView)view.findViewById(R.id.id_weather_fragment_tv_aqi);
        tv_pm25 = (TextView)view.findViewById(R.id.id_weather_fragment_tv_pm25);

        tv_comf = (TextView)view.findViewById(R.id.id_weather_fragment_tv_suggestion_comfortable);
        tv_drsg = (TextView)view.findViewById(R.id.id_weather_fragment_tv_suggestion_dress);
        tv_sport = (TextView)view.findViewById(R.id.id_weather_fragment_tv_suggestion_sport);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_weather_fragment_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                String url = Constant.H_WEATHER_BASE_URL + "city=" + weather_id + "&key=" + Constant.H_WEATHER_KEY;
                OkHttpUtil.sendWeatherInfoRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (swipeRefreshLayout.isRefreshing()){
                                    swipeRefreshLayout.setRefreshing(false);
                                    //下拉刷新同时, 刷新背景图片,发送事件
                                    //EventBus.getDefault().post(new RefreshBackGroundEvent());
                                }
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        Weather weather = new Weather();
                        weather.setWeatherInfo(responseText);
                        weather.updateAll("weatherId=?", weather_id);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleWeatherInfoResponse(responseText);
                            }
                        });
                    }
                });
            }
        });



        weatherList = DataSupport.where("weatherId = ? ", weather_id).find(Weather.class);
        if (weatherList != null && weatherList.size()>0){
            if (TextUtils.isEmpty(weatherList.get(0).getWeatherInfo())) sendWeatherInfoRequest();
            else handleWeatherInfoResponse(weatherList.get(0).getWeatherInfo());

        }else {
            //get dada from internet
            sendWeatherInfoRequest();
        }

        //initView();


        return view;
    }

    private void initView(){

    }

    private void sendWeatherInfoRequest(){
        String url = Constant.H_WEATHER_BASE_URL + "city=" + weather_id + "&key=" + Constant.H_WEATHER_KEY;
        Log.d(TAG, "onCreateView: url = " + url);
        //
        OkHttpUtil.sendWeatherInfoRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Weather weather = new Weather();
                weather.setWeatherInfo(responseText);
                weather.updateAll("weatherId=?", weather_id);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleWeatherInfoResponse(responseText);
                    }
                });
            }
        });
    }

    private void handleWeatherInfoResponse(String result){
        //下拉刷新同时, 刷新背景图片,发送事件
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        //EventBus.getDefault().post(new RefreshBackGroundEvent());
        Gson gson = new Gson();
        Root root = gson.fromJson(result, Root.class);
        Log.d(TAG, "handleWeatherInfoResponse: weather result = " + result);
        Log.d(TAG, "real_temp + " + root.getHeWeather5().get(0).getNow().getTmp());
        HeWeather5 hweather = root.getHeWeather5().get(0);
        if (hweather.getStatus().equals("ok")){
            tv_real_tmp.setText(hweather.getNow().getTmp() + "°C");
            tv_real_weather.setText(hweather.getNow().getCond().getTxt());
            tv_real_time.setText(hweather.getBasic().getUpdate().getLoc().split(" ")[1]);

            List<Daily_forecast> list = hweather.getDaily_forecast();
            Daily_forecast today = list.get(0);
            tv_forecast_today_date.setText(today.getDate());
            tv_forecast_today_weather.setText(today.getCond().getTxt_d());
            tv_forecast_today_min_tmp.setText(today.getTmp().getMin());
            tv_forecast_today_max_tmp.setText(today.getTmp().getMax());

            Daily_forecast tomorrow = list.get(1);
            tv_forecast_tomorrow_date.setText(tomorrow.getDate());
            tv_forecast_tomorrow_weather.setText(tomorrow.getCond().getTxt_d());
            tv_forecast_tomorrow_min_tmp.setText(tomorrow.getTmp().getMin());
            tv_forecast_tomorrow_max_tmp.setText(tomorrow.getTmp().getMax());

            Daily_forecast after_tomorrow = list.get(2);
            tv_forecast_after_tomorrow_date.setText(after_tomorrow.getDate());
            tv_forecast_after_tomorrow_weather.setText(after_tomorrow.getCond().getTxt_d());
            tv_forecast_after_tomorrow_min_tmp.setText(after_tomorrow.getTmp().getMin());
            tv_forecast_after_tomorrow_max_tmp.setText(after_tomorrow.getTmp().getMax());


            if (hweather.getAqi()!=null){
                tv_air_quality.setText( "空气质量: " +hweather.getAqi().getCity().getQlty());
                tv_aqi.setText(hweather.getAqi().getCity().getAqi());
                tv_pm25.setText(hweather.getAqi().getCity().getPm25());
            }

            Suggestion suggestion = hweather.getSuggestion();
            tv_comf.setText("舒适度: " + suggestion.getComf().getTxt());
            tv_drsg.setText("穿着: " + suggestion.getDrsg().getTxt());
            tv_sport.setText("运动: " + suggestion.getSport().getTxt());
        }

    }
}
