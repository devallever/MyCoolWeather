package com.allever.mycoolweather.modules.weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.HistoryWeather;
import com.allever.mycoolweather.bean.Image;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.bean.hweather.Daily_forecast;
import com.allever.mycoolweather.bean.hweather.HeWeather5;
import com.allever.mycoolweather.bean.hweather.Root;
import com.allever.mycoolweather.modules.weather.ui.MainActivity;
import com.allever.mycoolweather.utils.CommonUtil;
import com.allever.mycoolweather.utils.Constant;
import com.allever.mycoolweather.utils.OkHttpUtil;
import com.allever.mycoolweather.utils.SharePreferenceUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by allever on 17-4-27.
 */

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.d(TAG, "onStartCommand: ()");
        updateWeather();
        updateImage();
        int frequency = SharePreferenceUtil.getUpdateFrequency(this);
        Log.d(TAG, "onStartCommand: frequency = " + frequency);
        int anHour = frequency * 60 * 60 * 1000;   //１小时
        //int anHour = 10* 1000;   //10秒
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        //Intent receiverIntent = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,serviceIntent,0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }


    private void updateWeather(){
        Log.d(TAG, "updateWeather: () Started");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Weather> weatherList = new ArrayList<>();
                weatherList = DataSupport.findAll(Weather.class);
                OkHttpClient client = new OkHttpClient();

                for (Weather weather: weatherList){
                    try {
                        String weatherId = weather.getWeatherId();
                        String url = Constant.H_WEATHER_BASE_URL + "city=" + weatherId + "&key=" + Constant.H_WEATHER_KEY;
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        Response response = client.newCall(request).execute();
                        Log.d(TAG, "run: response.code() = " + response.code());
                        String result = response.body().string();


                        //天气提醒功能
                        //
                        if (SharePreferenceUtil.getIsNotify(AutoUpdateService.this)){
                            if ("1".equals(weather.getIsNotify())){
                                //比较
                                if (weather!=null && weather.getWeatherInfo()!=null)
                                sendNotification(weather.getWeatherInfo(), result, weather.getId());
                            }
                        }
                        Log.d(TAG, "run: result = " + result);
                        weather.setWeatherInfo(result);
                        weather.updateAll("weatherId=?",weatherId);

                        saveHistoryWeather(result);
                    }catch (IOException ioe){
                        //ioe.printStackTrace();
                    }

                }
            }
        }).start();

    }

    private void saveHistoryWeather(String result){
        Gson gson = new Gson();
        HeWeather5 heWeather5 = gson.fromJson(result,Root.class).getHeWeather5().get(0);

        List<Daily_forecast> daily_forecastList = heWeather5.getDaily_forecast();
        //for (Daily_forecast daily_forecast :daily_forecastList){
        Daily_forecast today = daily_forecastList.get(0);
            String date = today.getDate();
            String weather = today.getCond().getTxt_d();
            String weatherId = heWeather5.getBasic().getId();
            String countyName = heWeather5.getBasic().getCity();
            String min = today.getTmp().getMin();
            String max = today.getTmp().getMax();
            HistoryWeather historyWeather = new HistoryWeather();
            historyWeather.setDate(date);
            historyWeather.setWeatherId(weatherId);
            historyWeather.setCountyName(countyName);
            historyWeather.setWeather(weather);
            historyWeather.setMin(min);
            historyWeather.setMax(max);
            historyWeather.saveOrUpdate("weatherId=? and date=?", weatherId, date);
        //}

    }

    private void sendNotification(String oldWeatherInfo, String newWeatherInfo, int id){
        Gson gson = new Gson();
        HeWeather5 oldWeather = gson.fromJson(oldWeatherInfo,Root.class).getHeWeather5().get(0);
        HeWeather5 newWeather = gson.fromJson(newWeatherInfo,Root.class).getHeWeather5().get(0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String county = newWeather.getBasic().getCity();
        builder.setContentTitle("极简天气温馨提示:");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.logo));
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,0);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLights(Color.GREEN, 1000, 1000);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setTicker("极简天气");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        //1. 判断今日温度差,最高温和最低温
        int max = Integer.valueOf(newWeather.getDaily_forecast().get(0).getTmp().getMax());
        int min = Integer.valueOf(newWeather.getDaily_forecast().get(0).getTmp().getMin());
        if ((max-min) >=10 ){
            //显示通知
            builder.setContentText( county + ": 昼夜温差较大,请预防感冒!");
            notificationManager.notify(id,builder.build());
        }

        //2.比较今天明天气温相差5度提示
        Daily_forecast today = newWeather.getDaily_forecast().get(0);
        Daily_forecast tomorrow = newWeather.getDaily_forecast().get(1);
        int todayTmp = Integer.valueOf(today.getTmp().getMin());
        int tomorrowTmp = Integer.valueOf(tomorrow.getTmp().getMin());
        if ( Math.abs(todayTmp-tomorrowTmp) >= 5){
            if (tomorrowTmp > todayTmp){
                builder.setContentText( county + ": 明天将大幅度升温!");
            }else {
                builder.setContentText( county + ": 明天将大幅度降温温!");
            }
            notificationManager.notify(id+1000,builder.build());
        }

        //3. 有雨的要带伞
        if (tomorrow.getCond().getTxt_d().contains("雨")){
            builder.setContentText( county + ": 明天将有" + tomorrow.getCond().getTxt_d() + ", 出门记得带伞.");
            notificationManager.notify(id+1001,builder.build());
        }

        //4. 比较实时天气
        String old = oldWeather.getNow().getCond().getTxt();
        String newInfo = newWeather.getNow().getCond().getTxt();
        if (!old.equals(newInfo)){
            builder.setContentText( county + ": " + newWeather.getNow().getCond().getTxt());
            notificationManager.notify(id+1002,builder.build());
        }

    }

    private void updateImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String date = CommonUtil.getTodayFormatDate();
                List<Image> imageList = new ArrayList<>();
                imageList = DataSupport.where("date=?", date).find(Image.class);
                if (imageList.size()==0){
                    //获取当天图片地址
                    OkHttpUtil.sendImageRequest(Constant.BING_IMAGE_URL, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String imgUrl = response.body().string();
                            String name = (imgUrl.substring(imgUrl.lastIndexOf("/"))).split("/")[1];
                            Image image = new Image();
                            image.setDate(date);
                            image.setName(name);
                            image.setUrl(imgUrl);
                            image.save();
                        }
                    });
                }
            }
        }).start();
    }

}
