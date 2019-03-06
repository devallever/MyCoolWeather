package com.allever.mycoolweather.modules.city.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.mycoolweather.BaseActivity;
import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.City;
import com.allever.mycoolweather.bean.County;
import com.allever.mycoolweather.bean.Province;
import com.allever.mycoolweather.modules.city.event.AddCityEvent;
import com.allever.mycoolweather.utils.OkHttpUtil;
import com.allever.mycoolweather.utils.ResponseUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by allever on 17-4-24.
 */

public class ChooseCityActivity extends BaseActivity {

    private static final String TAG = "ChooseCityActivity";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private int currentLevel;

    private ProgressDialog progressDialog;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city_activity_layout);

        toolbar = (Toolbar)findViewById(R.id.id_choose_city_activity_toolbar);
        toolbar.setTitle("选择城市");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView = (ListView)findViewById(R.id.id_choose_city_activity_list_view);
        listView.setAdapter(adapter);

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
                    AddCityEvent addCityEvent = new AddCityEvent();
                    addCityEvent.setCountyName(selectedCounty.getCountyName());
                    addCityEvent.setWeatherId(selectedCounty.getWeatherId());
                    EventBus.getDefault().post(addCityEvent);
                    //Toast.makeText(ChooseCityActivity.this,"id = "  + selectedCounty.getId() + "\n"+
                    //        "countyName = " + selectedCounty.getCountyName() + "\n" +
                    //        "weather_id = " + selectedCounty.getWeatherId(),Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent();
                    intent.putExtra("weather_id", selectedCounty.getWeatherId());
                    intent.putExtra("county_name",selectedCounty.getCountyName());
                    setResult(RESULT_OK,intent);
                    */
                    finish();
                }
            }
        });

        queryProvince();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (currentLevel == LEVEL_PROVINCE){
                    finish();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }else if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_PROVINCE){
            finish();
        }else if (currentLevel == LEVEL_CITY){
            queryProvince();
        }else if (currentLevel == LEVEL_COUNTY){
            queryCity();
        }
        //super.onBackPressed();
    }

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

    private void queryFromServer(String address, final String type){
        showProgressDialog();
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "onResponse: " + responseText);
                boolean result = false;
                if ("province".equals(type)){
                    result = ResponseUtil.handleProvinceResponse(responseText);
                }else if (type.equals("city")){
                    result = ResponseUtil.handleCityResponse(responseText, selectedProvince.getId());
                }else if (type.equals("county")){
                    result = ResponseUtil.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            if (type.equals("province")){
                                queryProvince();
                            }else if (type.equals("city")){
                                queryCity();
                            }else if (type.equals("county")){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void hideProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

}
