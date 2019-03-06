package com.allever.mycoolweather.modules.weather.bean;

/**
 * Created by allever on 17-5-8.
 */

public class HistoryWeatherItem {
    private String date;
    private String weather;
    private String min;
    private String max;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
}
