package com.allever.mycoolweather.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by allever on 17-4-27.
 */

public class Image extends DataSupport {
    private int id;
    private String date;
    private String url;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
