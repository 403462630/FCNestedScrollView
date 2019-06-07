package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.reflect.Field;

/**
 * Created by fangcan on 2018/3/31.
 */

public class FCSwipeRefreshLayout extends SwipeRefreshLayout {
    private Field draggingField;
    private View targetView;
    /** 当自己滚到顶或底部的时候 否联动child滚动 */
    private boolean isLinkedChild;
    /** 是否拦截下拉操作，优先处理下拉刷新 */
    private boolean isPullRefreshIntercept;
    public FCSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public FCSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FCSwipeRefreshLayout);
        isLinkedChild = typedArray.getBoolean(R.styleable.FCSwipeRefreshLayout_fc_is_linked_child, true);
        isPullRefreshIntercept = true; // typedArray.getBoolean(R.styleable.FCSwipeRefreshLayout_fc_is_pull_refresh_intercept, false);
        typedArray.recycle();
        try {
            draggingField = getClass().getSuperclass().getDeclaredField("mTotalUnconsumed");
            draggingField.setAccessible(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setPullRefreshIntercept(boolean pullRefreshIntercept) {
        isPullRefreshIntercept = pullRefreshIntercept;
    }

    public void fling(float velocityY) {
        if (isLinkedChild && this.targetView != null) {
            if (this.targetView.canScrollVertically((int) velocityY)) {
                if (this.targetView instanceof FCRecyclerView) {
                    ((FCRecyclerView)this.targetView).fling(0, (int) velocityY);
                } else if (this.targetView instanceof FCNestedScrollView) {
                    ((FCNestedScrollView)this.targetView).fling((int) velocityY);
                } else if (this.targetView instanceof FCWebView) {
                    ((FCWebView)this.targetView).flingScroll(0, (int) velocityY);
                }
            }
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        this.targetView = target;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (dy < 0 && isPullRefreshIntercept) {
            return false;
        } else {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return false;
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isPullRefreshIntercept && velocityY < 0) {
            return false;
        } else {
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
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (isPullRefreshIntercept && velocityY < 0) {
            return false;
        } else {
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
}
