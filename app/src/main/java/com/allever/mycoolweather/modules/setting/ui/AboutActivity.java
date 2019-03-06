package com.allever.mycoolweather.modules.setting.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.allever.mycoolweather.BaseActivity;
import com.allever.mycoolweather.R;
import com.allever.mycoolweather.bean.Image;
import com.allever.mycoolweather.modules.weather.ui.MainActivity;
import com.allever.mycoolweather.utils.CommonUtil;
import com.allever.mycoolweather.utils.Constant;
import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allever on 17-5-9.
 */

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.id_about_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_android_48);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.id_about_activity_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
        ImageView iv_head = (ImageView)findViewById(R.id.id_about_activity_iv_head);


        if (PreferenceManager.getDefaultSharedPreferences(AboutActivity.this).getBoolean("pref_bg",false)){
            String bgPath = PreferenceManager.getDefaultSharedPreferences(AboutActivity.this).getString("pref_bg_path","");
            if (!TextUtils.isEmpty(bgPath)){
                Glide.with(AboutActivity.this).load(bgPath).into(iv_head);
            }
        }else {
            String date = CommonUtil.getTodayFormatDate();
            String path = Constant.IMAGE_PATH;
            List<Image> imageList = new ArrayList<>();
            imageList = DataSupport.where("date=?", date).find(Image.class);
            if (imageList.size()>0) {
                Image image = imageList.get(0);
                String name = image.getName();
                Glide.with(this).load(path + name).into(iv_head);
            }
        }


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
}
