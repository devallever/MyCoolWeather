package com.allever.mycoolweather.bean.hweather;

/**
 * Created by allever on 17-4-25.
 */

public class Daily_forecast {
    private Astro astro;

    private Condd cond;

    private String date;

    private String hum;

    private String pcpn;

    private String pop;

    private String pres;

    private Tmp tmp;

    private String uv;

    private String vis;

    private Wind wind;

    public void setAstro(Astro astro){
        this.astro = astro;
    }
    public Astro getAstro(){
        return this.astro;
    }
    public void setCond(Condd cond){
        this.cond = cond;
    }
    public Condd getCond(){
        return this.cond;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public void setHum(String hum){
        this.hum = hum;
    }
    public String getHum(){
        return this.hum;
    }
    public void setPcpn(String pcpn){
        this.pcpn = pcpn;
    }
    public String getPcpn(){
        return this.pcpn;
    }
    public void setPop(String pop){
        this.pop = pop;
    }
    public String getPop(){
        return this.pop;
    }
    public void setPres(String pres){
        this.pres = pres;
    }
    public String getPres(){
        return this.pres;
    }
    public void setTmp(Tmp tmp){
        this.tmp = tmp;
    }
    public Tmp getTmp(){
        return this.tmp;
    }
    public void setUv(String uv){
        this.uv = uv;
    }
    public String getUv(){
        return this.uv;
    }
    public void setVis(String vis){
        this.vis = vis;
    }
    public String getVis(){
        return this.vis;
    }
    public void setWind(Wind wind){
        this.wind = wind;
    }
    public Wind getWind(){
        return this.wind;
    }

}