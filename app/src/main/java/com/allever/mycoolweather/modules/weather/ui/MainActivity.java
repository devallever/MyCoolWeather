package com.allever.mycoolweather.modules.weather.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.Image;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.modules.city.event.AddCityEvent;
import com.allever.mycoolweather.modules.city.event.DeleteCityEvent;
import com.allever.mycoolweather.modules.city.event.FixCityEvent;
import com.allever.mycoolweather.modules.city.event.UpdateManageCityEvent;
import com.allever.mycoolweather.modules.city.ui.ChooseCityActivity;
import com.allever.mycoolweather.modules.city.ui.CityManageActivity;
import com.allever.mycoolweather.modules.setting.event.BGEvent;
import com.allever.mycoolweather.modules.setting.ui.AboutActivity;
import com.allever.mycoolweather.modules.setting.ui.SettingActivity;
import com.allever.mycoolweather.modules.weather.adapter.WeatherPageAdapter;
import com.allever.mycoolweather.modules.weather.event.MessageEvent;
import com.allever.mycoolweather.modules.weather.event.RefreshBackGroundEvent;
import com.allever.mycoolweather.modules.weather.rx.RxDrawer;
import com.allever.mycoolweather.modules.weather.rx.RxUtils;
import com.allever.mycoolweather.modules.weather.rx.SimpleSubscriber;
import com.allever.mycoolweather.modules.weather.service.AutoUpdateService;
import com.allever.mycoolweather.utils.CommonUtil;
import com.allever.mycoolweather.utils.Constant;
import com.allever.mycoolweather.utils.OkHttpUtil;
import com.allever.mycoolweather.utils.SharePreferenceUtil;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_CHOOSE_CITY = 0;
    private static final int REQUEST_CODE_MANAGE_CITY = 1;


    private List<Weather> weatherList = new ArrayList<>();
    private ViewPager viewPager;
    private WeatherPageAdapter weatherPageAdapter;
    private List<WeatherFragment> weatherFragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    private ImageView iv_bg;
    private int page_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //initView();

        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.id_main_activity_toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.id_main_activity_drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        NavigationView navigationView = (NavigationView)findViewById(R.id.id_main_activity_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                RxDrawer.close(drawerLayout).compose(RxUtils.rxSchedulerHelper(AndroidSchedulers.mainThread())).subscribe(
                        new SimpleSubscriber<Void>() {
                            @Override
                            public void onNext(Void aVoid) {
                                Intent intent;
                                switch (item.getItemId()){
                                    case R.id.id_nav_menu_location:
                                        Toast.makeText(MainActivity.this,"定位...待完善",Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.id_nav_menu_city_manager:
                                        drawerLayout.closeDrawers();
                                        intent = new Intent(MainActivity.this, CityManageActivity.class);
                                        startActivityForResult(intent, REQUEST_CODE_MANAGE_CITY);
                                        break;
                                    case R.id.id_nav_menu_setting:
                                        //Toast.makeText(MainActivity.this,"设置",Toast.LENGTH_SHORT).show();
                                        intent = new Intent(MainActivity.this, SettingActivity.class);
                                        startActivity(intent);
                                        break;
                                    case R.id.id_nav_menu_about:
                                        intent = new Intent(MainActivity.this, AboutActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(MainActivity.this,"关于",Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                return true;
            }
        });

        iv_bg = (ImageView) findViewById(R.id.id_main_activity_iv_bg);

        //初始化数据库
        Connector.getDatabase();

        viewPager = (ViewPager)findViewById(R.id.id_main_activity_view_pager);
        weatherPageAdapter = new WeatherPageAdapter(getSupportFragmentManager(),weatherFragmentList,titleList);
        viewPager.setAdapter(weatherPageAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.id_main_activity_table_layout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOnPageChangeListener(this);

        //刷新ViewPager
        showWeather();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.id_main_activity_fab_add_city);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseCityActivity.class);
                startActivityForResult(intent,REQUEST_CODE_CHOOSE_CITY);
            }
        });

        //显示背景
        showBackground();


        //6.0权限管理
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //getImageDaily();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        page_position = position;
    }

    @Override
    protected void onDestroy() {
        //启动后台更新服务
        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        if (SharePreferenceUtil.getIsNotify(this)){
            startService(serviceIntent);
        }else {
            stopService(serviceIntent);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //Intent receiverIntent = new Intent(this, AutoUpdateReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getService(this,0,serviceIntent,0);
            alarmManager.cancel(pendingIntent);
        }

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    getImageDaily();
                    //Toast.makeText(this, "拒绝授权无法使用程序", Toast.LENGTH_SHORT).show();
                    //finish();
                }else {
                    showBackground();
                }
                break;
        }
    }

    private void showBackground(){
        Log.d(TAG, "showBackground: bgvalue = " + PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_bg",false));
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_bg",false)){
            String bgPath = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_bg_path","");
            if (!TextUtils.isEmpty(bgPath)){
                Glide.with(this).load(bgPath).into(iv_bg);
            }
        }else {
            String date = CommonUtil.getTodayFormatDate();
            Log.d(TAG, "showBackground: () date = " + date);
            String name;
            String path = Constant.IMAGE_PATH;
            Log.d(TAG, "showBackground: path = " + path);
            List<Image> imageList = new ArrayList<>();
            imageList = DataSupport.where("date=?", date).find(Image.class);
            if (imageList.size()>0) {
                Image image = imageList.get(0);
                name = image.getName();
                File fileDir = new File(path);
                if (!fileDir.exists()){
                    fileDir.mkdir();
                }
                File file = new File(path + name);
                if (file.exists()){
                    Glide.with(this).load(file).into(iv_bg);
                }else {
                    //下载图片
                    String url = image.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        //下载图片
                        downloadImage(url);
                    }
                }
            }
        }


    }

    private void downloadImage(String url){
        OkHttpUtil.downloadImage(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ()!!!!!!!1");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: url = " + url);
                String fileName = (url.substring(url.lastIndexOf("/"))).split("/")[1];
                Log.d(TAG, "onResponse: fileName = " + fileName);
                InputStream inputStream = response.body().byteStream();
                File file = new File(Constant.IMAGE_PATH + fileName);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int len;
                while ((len = bufferedInputStream.read(b)) > 0 ){
                    baos.write(b,0,len);
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.close();
                bufferedInputStream.close();
                inputStream.close();
                response.body().close();
                Log.d(TAG, "onResponse: DownloadFinish()");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("pref_bg",false)){
                            String bgPath = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("pref_bg_path","");
                            if (!TextUtils.isEmpty(bgPath)){
                                Glide.with(MainActivity.this).load(bgPath).into(iv_bg);
                            }
                        }else {
                            Glide.with(MainActivity.this).load(file).into(iv_bg);
                        }
                    }
                });
            }
        });
    }


    private void showWeather(){

        weatherList.clear();
        weatherFragmentList.clear();
        titleList.clear();
        weatherList = DataSupport.where("isShow=?", "1").find(Weather.class);
        if (weatherList.size() ==0){
            //转到选择城市Activity
            Intent intent = new Intent(this, ChooseCityActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CITY);
            //启动后台更新服务
            Intent serviceIntent = new Intent(this, AutoUpdateService.class);
            startService(serviceIntent);
        }else {
            for (Weather weather: weatherList) {
                titleList.add(weather.getCountyName());
                WeatherFragment weatherFragment = new WeatherFragment(weather.getWeatherId());
                weatherFragmentList.add(weatherFragment);
            }
            //weatherPageAdapter.notifyDataSetChanged();
            weatherPageAdapter = new WeatherPageAdapter(getSupportFragmentManager(),weatherFragmentList,titleList);
            viewPager.setAdapter(weatherPageAdapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.id_main_menu_history:
                intent = new Intent(this, HistoryWeatherActivity.class);
                intent.putExtra("position", page_position);
                startActivity(intent);
                break;
            case R.id.id_main_menu_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.id_main_menu_wrapper:
                setWrapper();
                break;
            default:
                break;

        }
        return true;
    }

    private void setWrapper(){
        String date = CommonUtil.getTodayFormatDate();
        String path = Constant.IMAGE_PATH;
        List<Image> imageList = new ArrayList<>();
        imageList = DataSupport.where("date=?", date).find(Image.class);
        if (imageList.size()>0) {
            Image image = imageList.get(0);
            String name = image.getName();
            File file = new File(Constant.IMAGE_PATH + name);
            if (file.exists()){
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("mimeType", "image/*");
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeFile(Constant.IMAGE_PATH + name),null,null));
                intent.setData(uri);
                startActivityForResult(intent,1);
            }
        }else {

        }
    }

    private void getImageDaily(){
        String url = "http://guolin.tech/api/bing_pic";
        OkHttpUtil.sendImageRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imgUrl = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("pref_bg",false)){
                            String bgPath = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("pref_bg_path","");
                            if (!TextUtils.isEmpty(bgPath)){
                                Glide.with(MainActivity.this).load(bgPath).into(iv_bg);
                            }
                        }else {
                            Glide.with(MainActivity.this).load(imgUrl).into(iv_bg);
                        }
                    }
                });

            }
        });
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBackground(RefreshBackGroundEvent refreshBackGroundEvent){
        //Toast.makeText(this,"changed background Image.", )
        Log.d(TAG, "onRefreshBackground: changed background Image.");
        List<Image> imageList = new ArrayList<>();
        imageList = DataSupport.findAll(Image.class);
        Random random = new Random();
        int id = random.nextInt(imageList.size());
        File fileDir = new File(Constant.IMAGE_PATH);
        if (!fileDir.exists()){
            fileDir.mkdir();
        }
        File file = new File(Constant.IMAGE_PATH + imageList.get(id).getName());
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_bg",false)){
            String bgPath = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_bg_path","");
            if (!TextUtils.isEmpty(bgPath)){
                Glide.with(this).load(bgPath).into(iv_bg);
            }
        }else {
            Glide.with(this).load(file).into(iv_bg);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAddCityEvent(AddCityEvent addCityEvent){
        List<Weather> weatherList = DataSupport.where("weatherId = ? ", addCityEvent.getWeatherId()).find(Weather.class);
        if (weatherList != null && weatherList.size()==0){
            Weather weather = new Weather();
            weather.setCountyName(addCityEvent.getCountyName());
            weather.setWeatherId(addCityEvent.getWeatherId());
            weather.setIsShow("1");
            weather.setIsNotify("1");
            weather.save();
            titleList.add(weather.getCountyName());
            WeatherFragment weatherFragment = new WeatherFragment(weather.getWeatherId());
            weatherFragmentList.add(weatherFragment);
            weatherPageAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeleteCity(DeleteCityEvent deleteCityEvent){
        showWeather();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleFixCityEvent(FixCityEvent fixCityEvent){
        //Toast.makeText(this,fixCityEvent.getCountyName(),Toast.LENGTH_SHORT).show();
        showWeather();
    }

    //设置背景
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBGEvent(BGEvent bgEvent){
        showBackground();
        Log.d(TAG, "handleBGEvent: ");
    }


}
