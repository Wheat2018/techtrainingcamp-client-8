package com.example.todayBread.wheat.Utils;


import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * 重写了左滑退出事件监听的GestureListener实现类。
 */
public class FlipQuitListener extends GestureDetector.SimpleOnGestureListener{
    Activity activity;
    public FlipQuitListener(@NotNull Activity activity){
        this.activity = activity;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x = e2.getX() - e1.getX();
        float y = Math.abs(e2.getY() - e1.getY());
        if (x > Utils.screenWidth(activity.getResources()) / 4.0 && Math.abs(y / x) < 0.577){
            activity.finish();
            return true;
        }
        return false;
    }
}
