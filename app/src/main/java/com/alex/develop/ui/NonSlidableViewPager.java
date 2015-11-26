package com.alex.develop.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by alex on 15-6-15.
 */
public class NonSlidableViewPager extends ViewPager {
    public NonSlidableViewPager(Context context) {
        super(context);
    }

    public NonSlidableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return slidable && super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return slidable && super.onTouchEvent(event);
    }

    public boolean isSlidable() {
        return slidable;
    }

    public void setSlidable(boolean slidable) {
        this.slidable = slidable;
    }

    private boolean slidable = true;
}
