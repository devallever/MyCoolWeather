package com.allever.mycoolweather.modules.city;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allever.mycoolweather.R;

/**
 * Created by allever on 17-4-24.
 */

public class ChooseCityFragment extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_city_fragment_layout,container,false);
        return view;
    }
}
