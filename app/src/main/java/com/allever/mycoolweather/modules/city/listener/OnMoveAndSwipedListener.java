package com.allever.mycoolweather.modules.city.listener;

/**
 * Created by allever on 17-4-25.
 */

public interface OnMoveAndSwipedListener {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
