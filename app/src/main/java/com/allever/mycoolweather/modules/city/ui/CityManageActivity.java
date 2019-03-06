package com.allever.mycoolweather.modules.city.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allever.mycoolweather.BaseActivity;
import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.City;
import com.allever.mycoolweather.bean.Weather;
import com.allever.mycoolweather.bean.hweather.Root;
import com.allever.mycoolweather.modules.city.adapter.CityRecyclerAdapter;
import com.allever.mycoolweather.modules.city.bean.CityItem;
import com.allever.mycoolweather.modules.city.event.AddCityEvent;
import com.allever.mycoolweather.modules.city.event.UpdateManageCityEvent;
import com.allever.mycoolweather.modules.city.listener.ItemTouchHelperCallback;
import com.allever.mycoolweather.modules.weather.ui.MainActivity;
import com.allever.mycoolweather.modules.weather.ui.WeatherFragment;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allever on 17-4-23.
 */

public class CityManageActivity extends BaseActivity {

    private static final String TAG = "CityManageActivity";

    private static final int REQUEST_CODE_CHOOSE_CITY = 0;

    private RecyclerView recyclerView;
    private List<CityItem> cityItemList = new ArrayList<>();
    private CityRecyclerAdapter cityRecyclerAdapter;



    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manage_activity_layout);
        EventBus.getDefault().register(this);


        Toolbar toolbar = (Toolbar)findViewById(R.id.id_city_manage_activity_toolbar);
        toolbar.setTitle("城市管理");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_android_48);
        }

        recyclerView = (RecyclerView)findViewById(R.id.id_city_manage_activity_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cityRecyclerAdapter = new CityRecyclerAdapter(this, cityItemList);
        recyclerView.setAdapter(cityRecyclerAdapter);

        //关联ItemTouchHelper和RecyclerView
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(cityRecyclerAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        //
        showCityList();


        fab = (FloatingActionButton)findViewById(R.id.id_city_manage_activity_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityManageActivity.this, ChooseCityActivity.class);
                startActivityForResult(intent,REQUEST_CODE_CHOOSE_CITY);
            }
        });

        /*
        Snackbar.make(fab,"滑动删除",Snackbar.LENGTH_LONG)
        .setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
        */

    }

    private void showCityList(){

        List<Weather> weatherList = DataSupport.findAll(Weather.class);
        Gson gson = new Gson();
        Root root;
        cityItemList.clear();
        for (Weather weather: weatherList){
            CityItem cityItem = new CityItem();
            cityItem.setIsShow(weather.getIsShow());
            cityItem.setIsNotify(weather.getIsNotify());
            root = gson.fromJson(weather.getWeatherInfo(),Root.class);
            if (root!=null){
                cityItem.setTmp(root.getHeWeather5().get(0).getNow().getTmp());
                cityItem.setWeather(root.getHeWeather5().get(0).getNow().getCond().getTxt());
                cityItem.setCounty(weather.getCountyName());
                cityItem.setWeatherId(weather.getWeatherId());
                cityItemList.add(cityItem);
            }else{
                Log.d(TAG, "showCityList: root is null");
            }

        }
        //cityRecyclerAdapter.no
        cityRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                //setResult(RESULT_OK);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //setResult(RESULT_OK);
        super.onBackPressed();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAddCityEvent(AddCityEvent addCityEvent){
        CityItem cityItem = new CityItem();
        cityItem.setWeatherId(addCityEvent.getWeatherId());
        cityItem.setIsShow("1");
        cityItem.setIsNotify("1");
        cityItem.setTmp("未知");
        cityItem.setCounty(addCityEvent.getCountyName());
        cityItem.setWeather("未知");
        cityItemList.add(cityItem);
        cityRecyclerAdapter.notifyDataSetChanged();
        //recyclerView.smoothScrollToPosition(cityItemList.size()-1);
        //recyclerView.position
    }

}
