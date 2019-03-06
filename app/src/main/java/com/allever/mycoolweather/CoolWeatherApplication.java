package com.allever.mycoolweather;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by allever on 17-4-24.
 */

public class CoolWeatherApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = this;
    }

    public static Context getContext(){
        return context;
    }

}
