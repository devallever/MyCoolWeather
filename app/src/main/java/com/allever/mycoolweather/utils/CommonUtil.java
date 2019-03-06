package com.allever.mycoolweather.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.allever.mycoolweather.CoolWeatherApplication;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by allever on 17-4-23.
 */

public class CommonUtil {

    public static String getTodayFormatDate(){
        Date mDate = new Date();
        Format format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(mDate);
        return date;
    }

    public static int getVersionCode(){
        PackageManager packageManager= CoolWeatherApplication.getContext().getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo=packageManager.getPackageInfo(CoolWeatherApplication.getContext().getPackageName(),0);
            versionCode=packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
