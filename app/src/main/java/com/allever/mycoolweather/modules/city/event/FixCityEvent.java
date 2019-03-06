package com.allever.mycoolweather.modules.city.event;

/**
 * Created by allever on 17-5-6.
 */

public class FixCityEvent {
    private int position;
    private String countyName;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
