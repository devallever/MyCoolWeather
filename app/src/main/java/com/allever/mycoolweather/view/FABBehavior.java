package com.allever.mycoolweather.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by allever on 17-6-9.
 */

public class FABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>   {
    public FABBehavior(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    /*@Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }*/
}
