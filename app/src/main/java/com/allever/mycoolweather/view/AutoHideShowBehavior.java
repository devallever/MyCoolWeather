package com.allever.mycoolweather.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by allever on 17-4-22.
 */

public class AutoHideShowBehavior extends CoordinatorLayout.Behavior<View> {

    public AutoHideShowBehavior() {

    }

    public AutoHideShowBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = parent.getMeasuredHeight() - child.getMeasuredHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        //因为Behavior只对CoordinatorLayout的直接子View生效，因此将依赖关系转移到AppBarLayout
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        //得到依赖View的滑动距离
        int top = ((AppBarLayout.Behavior)((CoordinatorLayout.LayoutParams)dependency.getLayoutParams()).getBehavior()).getTopAndBottomOffset();
        //因为BottomNavigation的滑动与ToolBar是反向的，所以取-top值
        ViewCompat.setTranslationY(child, -top);
        return false;
    }
}
