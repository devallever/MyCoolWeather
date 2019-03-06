package com.allever.mycoolweather.modules.city.event;

/**
 * Created by allever on 17-4-30.
 */

public class AddCityEvent {
    private String weatherId;
    private String countyName;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
