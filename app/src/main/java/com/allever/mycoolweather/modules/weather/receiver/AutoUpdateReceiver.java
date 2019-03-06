package com.allever.mycoolweather.modules.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.allever.mycoolweather.bean.Image;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.modules.weather.service.AutoUpdateService;
import com.allever.mycoolweather.utils.CommonUtil;
import com.allever.mycoolweather.utils.Constant;
import com.allever.mycoolweather.utils.OkHttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by allever on 17-5-2.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {


    private static final String TAG = "AutoUpdateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        updateWeather();
        updateImage();
        Intent autoUpdateService = new Intent(context, AutoUpdateService.class);
        context.startService(autoUpdateService);
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
                        Log.d(TAG, "run: result = " + result);
                        weather.setWeatherInfo(result);
                        weather.updateAll("weatherId=?",weatherId);
                    }catch (IOException ioe){
                        ioe.printStackTrace();
                    }

                }
            }
        }).start();

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
