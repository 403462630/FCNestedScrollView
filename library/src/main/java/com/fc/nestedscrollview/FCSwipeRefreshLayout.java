package com.fc.nestedscrollview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;

/**
 * Created by fangcan on 2018/3/31.
 */

public class FCSwipeRefreshLayout extends SwipeRefreshLayout {
    private Field draggingField;
    public FCSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public FCSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            draggingField = getClass().getSuperclass().getDeclaredField("mTotalUnconsumed");
            draggingField.setAccessible(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (dy < 0) {
            return false;
        } else {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        if (dyUnconsumed < 0) {
            return false;
        } else {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
        }
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        boolean isDragging = false;
        try {
            if (!isRefreshing()) {
                //是否处于下拉状态
                float value = draggingField.getFloat(this);
                isDragging = value > 0;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return !isDragging && super.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        boolean isDragging = false;
        try {
            if (!isRefreshing()) {
                //是否处于下拉状态
                float value = draggingField.getFloat(this);
                isDragging = value > 0;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return !isDragging && super.dispatchNestedFling(velocityX, velocityY, consumed);
    }
}
