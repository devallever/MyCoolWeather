package com.allever.mycoolweather.modules.setting.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.allever.mycoolweather.BaseActivity;
import com.allever.mycoolweather.R;

/**
 * Created by allever on 17-5-15.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.id_setting_activity_toolbar);
        toolbar.setTitle(getResources().getString(R.string.setting));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_android_48);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.id_setting_activity_fg_container,new SettingFragment());
        fragmentTransaction.commit();


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
