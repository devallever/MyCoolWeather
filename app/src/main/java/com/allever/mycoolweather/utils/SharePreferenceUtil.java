package com.allever.mycoolweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.allever.mycoolweather.BaseActivity;

/**
 * Created by allever on 17-4-24.
 */

public class SharePreferenceUtil {

    public static void saveBackgroundImagePath(Context context, String path){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("pref_bg_path", path);
        editor.commit();
    }

    public static String getBackgoundImagePath(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String path = sharedPreferences.getString("pref_bg_path","");
        return path;
    }

    public static boolean getIsNotify(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_notification",false);
    }

    public static int getUpdateFrequency(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("pref_update_time",1);
    }

    public static void setUpdateFrequency(Context context, int intFrequency){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt("pref_update_time", intFrequency);
        editor.commit();
    }


}
