package com.allever.mycoolweather.utils;

import android.text.TextUtils;
import android.util.Log;

import com.allever.mycoolweather.bean.City;
import com.allever.mycoolweather.bean.County;
import com.allever.mycoolweather.bean.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by allever on 17-4-24.
 */

public class ResponseUtil {
    private static final String TAG = "ResponseUtil";

    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i<allProvince.length(); i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();
                }
                return  true;
            }catch (JSONException je){
                je.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceCode){
        if (!TextUtils.isEmpty(response)){
            try {
                Log.d(TAG, "handleCityResponse: \n response = " + response);
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i< allCity.length(); i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceCode(provinceCode);
                    city.save();
                }
                return true;
            }catch (JSONException je){
                je.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityCode){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityCode(cityCode);
                    boolean successed = county.save();
                    if (successed) Log.d(TAG, "handleCountyResponse: save success");
                    else Log.d(TAG, "handleCountyResponse: save fail");
                }
                return true;
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }
        return false;
    }

}
