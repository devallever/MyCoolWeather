# MyCoolWeather-极简天气
Weather APP

My First Project after reading The First Code Android.
To improve my Android skill.

---

> 我的博客: [https://devallever.github.io/](https://devallever.github.io/)


# 简介
极简天气, 天气应用,是我看完Android第一行代码后, 参考并修改的练习项目, 以巩固所学知识

感谢开源

 - OkHttp
 - Retrofit
 - Glide
 - RxJava/RxAndroid
 - LitePal
 - EventBus

 感谢郭神提供天气数据接口
 
 
# 功能
## 第一版
 - 显示实时天气, 三天预报, 空气质量, 温馨提示
 - 获取Bing每日图片
 - 滑动切换城市
 - 城市管理: 增加,删除
 - 下拉刷新
 - 缓存省市县, 天气信息  
 
 
 
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_01.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_02.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_03.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_04.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_05.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_06.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_07.png?raw=true)  
![](https://github.com/devallever/MyCoolWeather/blob/master/pic_08.png?raw=true)  



 
## 第二版
 - 后台自动刷新
 - 自动下载壁纸
 - 刷新天气随机更换壁纸
 - 天气提醒
 - 查看历史天气
 - 关于
 
## 已知bug
在主界面旋转屏幕时候, 报错
找不到数据
```
weatherList = DataSupport.where("weatherId = ? ", weather_id).find(Weather.class);
```

我想应该是旋转时候Activity和Fragment重新运行导致,weatherId空了, 导致查不到数据
然而我在Activity的onSaveInstanceState方法中保存了临时数据
并在onCreate中进行数据恢复,还是不行
 
# 功能实现细节

## 第二版功能细节

### 后台自动更新天气信息
使用服务, 启动服务后通过AlarmManager设置一个定时任务,每隔一小时更新天气信息, 在退出程序时候启动该服务,在服务中主要执行了两个方法分别更新天气信息和壁纸, 请求网络后把数据保存到Weather表中
```
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.d(TAG, "onStartCommand: ()");
        updateWeather();
        updateImage();
        int anHour = 1 * 60 * 60 * 1000;   //１小时
        //int anHour = 10* 1000;   //10秒
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        //Intent receiverIntent = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,serviceIntent,0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
```

启动服务
```
    @Override
    protected void onDestroy() {
        //启动后台更新服务
        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        startService(serviceIntent);
        super.onDestroy();
    }
```
### 天气提醒功能
每次后台获取更新后,判断该城市是否显示通知, 是的话就对天气信息进一步分析:  

昼夜温度大于10度时提醒, 
```
int max = Integer.valueOf(newWeather.getDaily_forecast().get(0).getTmp().getMax());
int min = Integer.valueOf(newWeather.getDaily_forecast().get(0).getTmp().getMin());
if ((max-min) >=10 ){
//显示通知
builder.setContentText( county + ": 昼夜温差较大,请预防感冒!");
notificationManager.notify(id,builder.build());
}
```

今明天气相差5度时提醒
```
//2.比较今天明天气温相差5度提示
Daily_forecast today = newWeather.getDaily_forecast().get(0);
Daily_forecast tomorrow = newWeather.getDaily_forecast().get(1);
int todayTmp = Integer.valueOf(today.getTmp().getMin());
int tomorrowTmp = Integer.valueOf(tomorrow.getTmp().getMin());
if ( Math.abs(todayTmp-tomorrowTmp) >= 5){
	if (tomorrowTmp > todayTmp){
		builder.setContentText( county + ": 明天将大幅度升温!");
	}else {
		builder.setContentText( county + ": 明天将大幅度降温温!");
	}
notificationManager.notify(id+1000,builder.build());
}
```

有降水提示带伞  
```
//3. 有雨的要带伞
if (tomorrow.getCond().getTxt_d().contains("雨")){
	builder.setContentText( county + ": 明天将有" + tomorrow.getCond().getTxt_d() + ", 出门记得带伞.");
	notificationManager.notify(id+1001,builder.build());
}
```
当实时天气与上一次的天气有不同时候,提醒  
```
//4. 比较实时天气
String old = oldWeather.getNow().getCond().getTxt();
String newInfo = newWeather.getNow().getCond().getTxt();
if (!old.equals(newInfo)){
	builder.setContentText( county + ": " + newWeather.getNow().getCond().getTxt());
	notificationManager.notify(id+1002,builder.build());
}
```
### 查看历史天气

创建一个数据库表HistoryWeather用来保存历史天气信息  
id, weatherId, countyName, date,  weatehr, min, max  
每次后台自动更新天气数据时候，解析json数据，获取所需数据封装成HIstoryWeather，然后保存，根据date和weatherId字段获取要保存的记录，如果存在的就更新，不存在的就天机记录  
```
    private void saveHistoryWeather(String result){
        Gson gson = new Gson();
        HeWeather5 heWeather5 = gson.fromJson(result,Root.class).getHeWeather5().get(0);

        List<Daily_forecast> daily_forecastList = heWeather5.getDaily_forecast();
        //for (Daily_forecast daily_forecast :daily_forecastList){
        Daily_forecast today = daily_forecastList.get(0);
            String date = today.getDate();
            String weather = today.getCond().getTxt_d();
            String weatherId = heWeather5.getBasic().getId();
            String countyName = heWeather5.getBasic().getCity();
            String min = today.getTmp().getMin();
            String max = today.getTmp().getMax();
            HistoryWeather historyWeather = new HistoryWeather();
            historyWeather.setDate(date);
            historyWeather.setWeatherId(weatherId);
            historyWeather.setCountyName(countyName);
            historyWeather.setWeather(weather);
            historyWeather.setMin(min);
            historyWeather.setMax(max);
            historyWeather.saveOrUpdate("weatherId=? and date=?", weatherId, date);
        //}

    }
```
查找历史天气信息时候，默认是查找前30条记录，并且按date降序排列  
```
        List<HistoryWeather> historyWeatherList;
        historyWeatherList = DataSupport.where("weatherId=? order by date desc", weatherId).find(HistoryWeather.class);
        for (HistoryWeather historyWeather: historyWeatherList){
            HistoryWeatherItem historyWeatherItem = new HistoryWeatherItem();
            historyWeatherItem.setWeather(historyWeather.getWeather());
            historyWeatherItem.setDate(historyWeather.getDate());
            historyWeatherItem.setMax(historyWeather.getMax());
            historyWeatherItem.setMin(historyWeather.getMin());
            historyWeatherItemList.add(historyWeatherItem);
        }
```




## 第一版功能细节

### 选择城市 ChooseCItyActivity

首先访问数据库中是否有所有省份数据,有则获取,没有则请求服务器,然后得到全国省份的json数据,保存到数据库中
```
    private void queryProvince(){
        toolbar.setTitle("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china/";
            queryFromServer(address, "province");
        }
    }
```

```
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
```
然后解析json数据,设置到Listview中,点击时记录省份id, 然后根据这个id访问数据库中是否有该省份的城市信息,有则获取,没有则请求服务器,然后得到该省份所有城市的json数据,保存到数据库中

```
    private void queryCity(){
        toolbar.setTitle(selectedProvince.getProvinceName());
        cityList = DataSupport.where("provinceCode = ? ", String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city: cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }
```

```
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
```

然后解析json数据,清空listview数据源, 然后加载该省的所有城市数据,设置到ListView中,点击时记录该市的id, 然后根据这个id访问数据库中是否存在该市的所有县数据,有则获取, 没有则请求服务器,然后得到该市所有县的json数据,保存到数据库中

```
    private void queryCounty(){
        toolbar.setTitle(selectedCity.getCityName());
        Toast.makeText(this,"cityCode = "+ selectedCity.getCityCode() + "\n" +
                "id = " + selectedCity.getId(),Toast.LENGTH_LONG ).show();
        countyList = DataSupport.where("cityCode = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0 ){
            dataList.clear();
            for (County county: countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/"  + cityCode;
            queryFromServer(address,"county");
        }
    }
```

```
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
```

选择其中某一项, 获取weatherId和县名称countyName, 通过setResult返回父Activity中,
在父Activity中onActivityResult方法中, 把weatherId和countyName保存到Weather数据表中, 然后重新获取weather表中数据.

```
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    Toast.makeText(ChooseCityActivity.this,"id = "  + selectedCity.getId() + "\n"+
                            "cityName = " + selectedCity.getCityName() + "\n" +
                            "cityCode = " + selectedCity.getCityCode() + "\n" +
                            "provinceCode = " + selectedCity.getProvinceCode(),Toast.LENGTH_SHORT).show();
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY){
                    selectedCounty = countyList.get(position);
                    Toast.makeText(ChooseCityActivity.this,"id = "  + selectedCounty.getId() + "\n"+
                            "countyName = " + selectedCounty.getCountyName() + "\n" +
                            "weather_id = " + selectedCounty.getWeatherId(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("weather_id", selectedCounty.getWeatherId());
                    intent.putExtra("county_name",selectedCounty.getCountyName());
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
```

回到父Activity进行操作
```
String weather_id = data.getStringExtra("weather_id");
List<Weather> weatherList = DataSupport.where("weatherId = ? ", weather_id).find(Weather.class);
if (weatherList != null && weatherList.size()==0){
	Weather weather = new Weather();
	weather.setCountyName(data.getStringExtra("county_name"));
	weather.setWeatherId(data.getStringExtra("weather_id"));
	weather.setIsShow("1");
	weather.save();
	titleList.add(weather.getCountyName());
	WeatherFragment weatherFragment = new WeatherFragment(weather.getWeatherId());
	weatherFragmentList.add(weatherFragment);
	weatherPageAdapter.notifyDataSetChanged();
}
```


### 构造ViewPager天气页面数据源
在MainActivity中,访问数据库weather表, (条件:isShow=1,表示显示是否显示在主界面), 把查询结果存到List中,如果list大小为0, 则打开选择城市界面,如果存在数据,则遍历每个weather对象,创建WeatherFragment,并把weather对象中的weatherId传到Fragment的构造方法中,并添加到fragmengList中, WeatherFragment会根据这个weatherId获取天气信息,同时把weather的countyName添加到titleLists中用于设置每个pager的标题,,,然后通知adapter数据更新了,
pagerAdapter是通过fragmengList和titleList绑定数据的

```
    private void showWeather(){
        weatherList.clear();
        weatherFragmentList.clear();
        titleList.clear();
        weatherList = DataSupport.where("isShow=?", "1").find(Weather.class);
        if (weatherList.size() ==0){
            //转到选择城市Activity
            Intent intent = new Intent(this, ChooseCityActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CITY);
        }else {
            for (Weather weather: weatherList) {
                titleList.add(weather.getCountyName());
                WeatherFragment weatherFragment = new WeatherFragment(weather.getWeatherId());
                weatherFragmentList.add(weatherFragment);
            }
            weatherPageAdapter.notifyDataSetChanged();
        }
    }
```

选择城市后返回的操作
```
String weather_id = data.getStringExtra("weather_id");
List<Weather> weatherList = DataSupport.where("weatherId = ? ", weather_id).find(Weather.class);
if (weatherList != null && weatherList.size()==0){
	Weather weather = new Weather();
	weather.setCountyName(data.getStringExtra("county_name"));
	weather.setWeatherId(data.getStringExtra("weather_id"));
	weather.setIsShow("1");
	weather.save();
	titleList.add(weather.getCountyName());
	WeatherFragment weatherFragment = new WeatherFragment(weather.getWeatherId());
	weatherFragmentList.add(weatherFragment);
	weatherPageAdapter.notifyDataSetChanged();
}
```


### 城市管理-CityManageActivity
该页面有一个RecyclerView,和一个FloatingActionButton, 其中recyclerView的item是一个CardView, 显示了该城市粗略的天气信息,如温度和天气,fab用于添加城市,可以通过左右滑动每一个卡片来删除数据,
请求数据库, 访问weather表的所有数据, 抽取其中所需的信息封装到CityItem中,然后添加到cityItemList中,作为RecyclerView的数据源,当成功选择一个城市并返回后,根据返回的weatherId访问数据库是否存在该数据,有则不操作,没有则添加到weather表中.以免产生冗余数据.当滑动删除城市后,会从数据表中删除掉这条记录,(改进的做法是滑动删除后显示一个Snackbar来确认操作)
```
weatherList = DataSupport.findAll(Weather.class);
Gson gson = new Gson();
Root root;
for (Weather weather: weatherList){
	CityItem cityItem = new CityItem();
	cityItem.setIsShow(weather.getIsShow());
	root = gson.fromJson(weather.getWeatherInfo(),Root.class);
	if (root!=null){
		cityItem.setTmp(root.getHeWeather5().get(0).getNow().getTmp());
		cityItem.setWeather(root.getHeWeather5().get(0).getNow().getCond().getTxt());
		cityItem.setCounty(weather.getCountyName());
		cityItem.setWeatherId(weather.getWeatherId());
		cityItemList.add(cityItem);
	}
}
cityRecyclerAdapter.notifyDataSetChanged();
```

滑动删除后的操作-我还在进一步研究,不是很懂,因为是复制别人的代码
关联
```
//关联ItemTouchHelper和RecyclerView
ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(cityRecyclerAdapter);
ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
mItemTouchHelper.attachToRecyclerView(recyclerView);
```

ItemTouchHelperCallback:
```
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private OnMoveAndSwipedListener moveAndSwipedListener;

    public ItemTouchHelperCallback(OnMoveAndSwipedListener listener) {
        this.moveAndSwipedListener = listener;
    }

    //设置拖动方向以及侧滑方向
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            //单列的RecyclerView支持上下拖动和左右侧滑
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            //多列的RecyclerView支持上下左右拖动和不支持左右侧滑
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    //拖动item时会调用此方法
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //如果两个item不是同一个类型的，不让他拖拽
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        moveAndSwipedListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    //侧滑item时会调用此方法
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        moveAndSwipedListener.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
```

OnMoveAndSwipedListener:
```
public interface OnMoveAndSwipedListener {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
```

监听到滑动删除执行以下方法
```
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        DataSupport.deleteAll(Weather.class,"weatherId=?",cityItemList.get(position).getWeatherId());
        cityItemList.remove(position);
        notifyItemRemoved(position);
    }
```



### 显示天气-WeatherFragmeng
显示天气信息是在WeatherFragment中完成的, 也就是每个页面. 从构造方法中获取到该所显示城市的天气数据, 根据这个weatherId访问数据库中该weatherId所在记录是否有weatherInfo信息,如果有则获取该天气信息的json数据, 没有则向服务器请求数据,获取天气信息,成功获取信息之后保存到数据库中.

```java
weatherList = DataSupport.where("weatherId = ? ", weather_id).find(Weather.class);
if (weatherList != null && weatherList.size()>0){
	if (TextUtils.isEmpty(weatherList.get(0).getWeatherInfo())) sendWeatherInfoRequest();
	else handleWeatherInfoResponse(weatherList.get(0).getWeatherInfo());
}else {
	//get dada from internet
	sendWeatherInfoRequest();
}
```
获取后保存到数据库
```java
String responseText = response.body().string();
Weather weather = new Weather();
weather.setWeatherInfo(responseText);
weather.updateAll("weatherId=?", weather_id);
getActivity().runOnUiThread(new Runnable() {
	@Override
	public void run() {
		handleWeatherInfoResponse(responseText);
	}
});
```


---
持续更新........
欢迎大家共同学习共同成长.....






