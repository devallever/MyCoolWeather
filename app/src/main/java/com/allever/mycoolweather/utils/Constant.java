package com.allever.mycoolweather.utils;

import android.os.Environment;

/**
 * Created by allever on 17-4-24.
 */

public class Constant {
    public static final String JSON_PROVINCE = "[{\"id\":1,\"name\":\"北京\"},{\"id\":2,\"name\":\"上海\"},{\"id\":3,\"name\":\"天津\"},{\"id\":4,\"name\":\"重庆\"},{\"id\":5,\"name\":\"香港\"},{\"id\":6,\"name\":\"澳门\"},{\"id\":7,\"name\":\"台湾\"},{\"id\":8,\"name\":\"黑龙江\"},{\"id\":9,\"name\":\"吉林\"},{\"id\":10,\"name\":\"辽宁\"},{\"id\":11,\"name\":\"内蒙古\"},{\"id\":12,\"name\":\"河北\"},{\"id\":13,\"name\":\"河南\"},{\"id\":14,\"name\":\"山西\"},{\"id\":15,\"name\":\"山东\"},{\"id\":16,\"name\":\"江苏\"},{\"id\":17,\"name\":\"浙江\"},{\"id\":18,\"name\":\"福建\"},{\"id\":19,\"name\":\"江西\"},{\"id\":20,\"name\":\"安徽\"},{\"id\":21,\"name\":\"湖北\"},{\"id\":22,\"name\":\"湖南\"},{\"id\":23,\"name\":\"广东\"},{\"id\":24,\"name\":\"广西\"},{\"id\":25,\"name\":\"海南\"},{\"id\":26,\"name\":\"贵州\"},{\"id\":27,\"name\":\"云南\"},{\"id\":28,\"name\":\"四川\"},{\"id\":29,\"name\":\"西藏\"},{\"id\":30,\"name\":\"陕西\"},{\"id\":31,\"name\":\"宁夏\"},{\"id\":32,\"name\":\"甘肃\"},{\"id\":33,\"name\":\"青海\"},{\"id\":34,\"name\":\"新疆\"}]";
    public static final String JSON_CITY_GUANGDONG = "[{\"id\":205,\"name\":\"广州\"},{\"id\":206,\"name\":\"韶关\"},{\"id\":207,\"name\":\"惠州\"},{\"id\":208,\"name\":\"梅州\"},{\"id\":209,\"name\":\"汕头\"},{\"id\":210,\"name\":\"深圳\"},{\"id\":211,\"name\":\"珠海\"},{\"id\":212,\"name\":\"顺德\"},{\"id\":213,\"name\":\"肇庆\"},{\"id\":214,\"name\":\"湛江\"},{\"id\":215,\"name\":\"江门\"},{\"id\":216,\"name\":\"河源\"},{\"id\":217,\"name\":\"清远\"},{\"id\":218,\"name\":\"云浮\"},{\"id\":219,\"name\":\"潮州\"},{\"id\":220,\"name\":\"东莞\"},{\"id\":221,\"name\":\"中山\"},{\"id\":222,\"name\":\"阳江\"},{\"id\":223,\"name\":\"揭阳\"},{\"id\":224,\"name\":\"茂名\"},{\"id\":225,\"name\":\"汕尾\"},{\"id\":350,\"name\":\"佛山\"}]";
    public static final String JSON_COUNTY_GUANGZHOU = "[{\"id\":1525,\"name\":\"广州\",\"weather_id\":\"CN101280101\"},{\"id\":1526,\"name\":\"番禺\",\"weather_id\":\"CN101280102\"},{\"id\":1527,\"name\":\"从化\",\"weather_id\":\"CN101280103\"},{\"id\":1528,\"name\":\"增城\",\"weather_id\":\"CN101280104\"},{\"id\":1529,\"name\":\"花都\",\"weather_id\":\"CN101280105\"}]";
    public static final String JSON_WEATHER_INFO_GUANGZHOU =
            "{\"HeWeather5\":" +
                    "[" +
                    "{  " +
                        "\"aqi\":{\"city\":{\"aqi\":\"72\",\"co\":\"1\",\"no2\":\"65\",\"o3\":\"78\",\"pm10\":\"94\",\"pm25\":\"44\",\"qlty\":\"良\",\"so2\":\"17\"}}," +
                        "\"basic\":{\"city\":\"广州\",\"cnty\":\"中国\",\"id\":\"CN101280101\",\"lat\":\"23.125178\",\"lon\":\"113.280637\",\"update\":{\"loc\":\"2017-04-24 16:53\",\"utc\":\"2017-04-24 08:53\"}}," +
                        "\"daily_forecast\":[{\"astro\":{\"mr\":\"04:18\",\"ms\":\"16:37\",\"sr\":\"05:58\",\"ss\":\"18:51\"},\"cond\":{\"code_d\":\"300\",\"code_n\":\"302\",\"txt_d\":\"阵雨\",\"txt_n\":\"雷阵雨\"},\"date\":\"2017-04-24\",\"hum\":\"84\",\"pcpn\":\"3.5\",\"pop\":\"100\",\"pres\":\"1012\",\"tmp\":{\"max\":\"24\",\"min\":\"20\"},\"uv\":\"4\",\"vis\":\"14\",\"wind\":{\"deg\":\"127\",\"dir\":\"无持续风向\",\"sc\":\"微风\",\"spd\":\"0\"}},{\"astro\":{\"mr\":\"05:02\",\"ms\":\"17:39\",\"sr\":\"05:57\",\"ss\":\"18:51\"},\"cond\":{\"code_d\":\"302\",\"code_n\":\"302\",\"txt_d\":\"雷阵雨\",\"txt_n\":\"雷阵雨\"},\"date\":\"2017-04-25\",\"hum\":\"87\",\"pcpn\":\"7.1\",\"pop\":\"100\",\"pres\":\"1011\",\"tmp\":{\"max\":\"25\",\"min\":\"22\"},\"uv\":\"6\",\"vis\":\"15\",\"wind\":{\"deg\":\"134\",\"dir\":\"无持续风向\",\"sc\":\"微风\",\"spd\":\"3\"}},{\"astro\":{\"mr\":\"05:48\",\"ms\":\"18:42\",\"sr\":\"05:57\",\"ss\":\"18:52\"},\"cond\":{\"code_d\":\"307\",\"code_n\":\"300\",\"txt_d\":\"大雨\",\"txt_n\":\"阵雨\"},\"date\":\"2017-04-26\",\"hum\":\"84\",\"pcpn\":\"9.2\",\"pop\":\"100\",\"pres\":\"1010\",\"tmp\":{\"max\":\"26\",\"min\":\"20\"},\"uv\":\"11\",\"vis\":\"14\",\"wind\":{\"deg\":\"155\",\"dir\":\"无持续风向\",\"sc\":\"微风\",\"spd\":\"0\"}}]," +
                        "\"hourly_forecast\":[{\"cond\":{\"code\":\"305\",\"txt\":\"小雨\"},\"date\":\"2017-04-24 19:00\",\"hum\":\"87\",\"pop\":\"33\",\"pres\":\"1010\",\"tmp\":\"20\",\"wind\":{\"deg\":\"167\",\"dir\":\"东南风\",\"sc\":\"微风\",\"spd\":\"14\"}},{\"cond\":{\"code\":\"103\",\"txt\":\"晴间多云\"},\"date\":\"2017-04-24 22:00\",\"hum\":\"89\",\"pop\":\"7\",\"pres\":\"1010\",\"tmp\":\"16\",\"wind\":{\"deg\":\"157\",\"dir\":\"东南风\",\"sc\":\"微风\",\"spd\":\"15\"}}]," +
                        "\"now\":{\"cond\":{\"code\":\"300\",\"txt\":\"阵雨\"},\"fl\":\"25\",\"hum\":\"76\",\"pcpn\":\"0\",\"pres\":\"1012\",\"tmp\":\"22\",\"vis\":\"8\",\"wind\":{\"deg\":\"100\",\"dir\":\"东南风\",\"sc\":\"微风\",\"spd\":\"7\"}}," +
                        "\"status\":\"ok\"," +
                        "\"suggestion\":{\"air\":{\"brf\":\"中\",\"txt\":\"气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。\"},\"comf\":{\"brf\":\"较舒适\",\"txt\":\"白天有降雨，但会使人们感觉有些热，不过大部分人仍会有比较舒适的感觉。\"},\"cw\":{\"brf\":\"不宜\",\"txt\":\"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。\"},\"drsg\":{\"brf\":\"较舒适\",\"txt\":\"建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。\"},\"flu\":{\"brf\":\"较易发\",\"txt\":\"天气转凉，空气湿度较大，较易发生感冒，体质较弱的朋友请注意适当防护。\"},\"sport\":{\"brf\":\"较不宜\",\"txt\":\"有降水，推荐您在室内进行健身休闲运动；若坚持户外运动，须注意携带雨具并注意避雨防滑。\"},\"trav\":{\"brf\":\"适宜\",\"txt\":\"有降水，温度适宜，在细雨中游玩别有一番情调，可不要错过机会呦！但记得出门要携带雨具。\"},\"uv\":{\"brf\":\"弱\",\"txt\":\"紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。\"}}}]}";
    //FULL URL = https://free-api.heweather.com/v5/weather?city=%E5%B9%BF%E5%B7%9E&key=f5730fe0847b499cbd41aa6d2f69a3e2 ;
    public static final String H_WEATHER_BASE_URL = "https://free-api.heweather.com/v5/weather?";
    //bc0418b57b2d4918819d3974ac1285d9
    public static final String H_WEATHER_KEY = "f5730fe0847b499cbd41aa6d2f69a3e2";
    //public static final String H_WEATHER_KEY = "bc0418b57b2d4918819d3974ac1285d9"; //郭霖
    public static final String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/coolWeather/";
    public static final String BING_IMAGE_URL = "http://guolin.tech/api/bing_pic";

    //public static final String SERVER_BASE_URL = "http://10.42.0.1:8080/MyCoolWeatherServer/";
    public static final String SERVER_BASE_URL = "http://39.108.9.138:8080/MyCoolWeatherServer/";

}
