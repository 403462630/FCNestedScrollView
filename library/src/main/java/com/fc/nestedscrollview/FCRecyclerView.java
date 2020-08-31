package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by fangcan on 2018/3/28.
 */

public class FCRecyclerView extends RecyclerView {
    public static final int MODEL_ALL = 0; //自己优先滚动
    public static final int MODEL_UP = 1; //自己优先向上滚动
    public static final int MODEL_DOWN = 2; //自己优先向下滚动
    public static final int MODEL_NONE = 3; //自己最后滚动
    private static final String TAG = "FCRecycleView";

    private int nestedScrollModel = MODEL_NONE;
    /** 当滚到顶或底部的时候 否联动parent滚动 */
    private boolean isLinkedParent;
    private boolean isDraggingItem = false;
    private NestedScrollView nestedScrollView;
    private Handler handler = new Handler();
    private boolean isNestedScrollBy = false;
    // 测试看日志专用
    public boolean enableLog;
    private String uuid;

    public int lastDy;

    private boolean isStartScroll = false;
    private CopyOnWriteArraySet<OnScrollStateListener> onScrollStateListeners = new CopyOnWriteArraySet<>();

    public void addOnScrollStateListener(OnScrollStateListener onScrollStateListener) {
        onScrollStateListeners.add(onScrollStateListener);
    }

    public void removeOnScrollStateListener(OnScrollStateListener onScrollStateListener) {
        onScrollStateListeners.remove(onScrollStateListener);
    }

    public FCRecyclerView(Context context) {
        this(context, null);
    }

    public FCRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FCRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        uuid = UUID.randomUUID().toString();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FCRecyclerView);
        nestedScrollModel = typedArray.getInt(R.styleable.FCRecyclerView_fc_scroll_mode, MODEL_NONE);
        isLinkedParent = typedArray.getBoolean(R.styleable.FCRecyclerView_fc_is_linked_parent, true);
        typedArray.recycle();
    }

    private void notifyScrollStart() {
        for (OnScrollStateListener onScrollStateListener : onScrollStateListeners) {
            onScrollStateListener.onScrollStart();
        }
    }

    private void notifyScrollEnd() {
        for (OnScrollStateListener onScrollStateListener : onScrollStateListeners) {
            onScrollStateListener.onScrollEnd();
        }
    }

    public int getNestedScrollModel() {
        return nestedScrollModel;
    }

    public void setNestedScrollModel(int nestedScrollModel) {
        this.nestedScrollModel = nestedScrollModel;
    }

    public boolean isDraggingItem() {
        return isDraggingItem;
    }

    public void setDraggingItem(boolean draggingItem) {
        isDraggingItem = draggingItem;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        Log.i("onScrollStateChanged", "" + state);
        if (isNestedScrollBy == true && state == RecyclerView.SCROLL_STATE_IDLE) {
            isNestedScrollBy = false;
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
        }
        if (!onScrollStateListeners.isEmpty()) {
            if (state == RecyclerView.SCROLL_STATE_DRAGGING || state == RecyclerView.SCROLL_STATE_SETTLING) {
                if (!isStartScroll) {
                    isStartScroll = true;
                    notifyScrollStart();
                }
            } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
                if (isStartScroll) {
                    isStartScroll = false;
                    notifyScrollEnd();
                }
            }
        }
    }

    public void nestedScrollBy(int dy) {
        isNestedScrollBy = true;
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        smoothScrollBy(0, dy);
    }

    public void nestedScrollToPosition(int position) {
        isNestedScrollBy = true;
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        scrollToPosition(position);
    }

    private NestedScrollView initNestedScrollView() {
        ViewParent view = getParent();
        while (view != null) {
            if (view instanceof NestedScrollView) {
                nestedScrollView = (NestedScrollView) view;
            }
            view = view.getParent();
        }
        return null;
    }

    @Override
    public void scrollBy(int x, int y) {
        if (isDraggingItem()) {
            if (nestedScrollView == null) {
                initNestedScrollView();
            }
            if (nestedScrollView != null) {
                nestedScrollView.scrollBy(x, y);
            } else {
                super.scrollBy(x, y);
            }
        } else {
            super.scrollBy(x, y);
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        log("fling: velocityY " + velocityY);
        return super.fling(velocityX, velocityY);
    }

    /**
     * 当滚到顶或底部的时候 否联动parent滚动
     * @param direction < 0 向下滚，> 0 向上滚
     * @return
     */
    protected boolean isLinkedParentFling(int direction) {
        return isLinkedParent;
    }

    public void setLinkedParent(boolean linkedParent) {
        isLinkedParent = linkedParent;
    }

    protected boolean isCanScrollVertically(int direction) {
        boolean flag = false;
        switch (getNestedScrollModel()) {
            case MODEL_ALL:
                flag = canScrollVertically(direction);
                break;
            case MODEL_DOWN:
                flag = direction < 0 && canScrollVertically(direction);
                break;
            case MODEL_UP:
                flag = direction > 0 && canScrollVertically(direction);
                break;
            case MODEL_NONE:
                flag = false;
                break;
        }
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastDy = 0;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastDy = 0;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        log("startNestedScroll: axes " + axes + ", type " + type);
        lastDy = 0;
        return super.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        log("stopNestedScroll: type " + type);
        super.stopNestedScroll(type);
        lastDy = 0;
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        boolean flag = super.hasNestedScrollingParent(type);
        log("hasNestedScrollingParent: " + flag);
        return flag;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (!isNestedScrollingEnabled()) return false;
//        Log.i(TAG, "dispatchNestedPreScroll: " + type);
        lastDy = dy;
        if (isCanScrollVertically(dy)) {
            if (enableLog) {
                log("dispatchNestedPreScroll: false-自己消费1, dy " + dy + ", consumed(" + consumed[0] + ", " + consumed[1] + "), type " + type);
            }
            return false;
        } else {
            if (type == ViewCompat.TYPE_TOUCH || isLinkedParentFling(dy)) {
                if (enableLog) {
                    log("dispatchNestedPreScroll: dy " + dy + ", consumed(" + consumed[0] + ", " + consumed[1] + "), type " + type);
                }
                return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
            } else {
                if (enableLog) {
                    log("dispatchNestedPreScroll: false-自己消费2, dy " + dy + ", consumed(" + consumed[0] + ", " + consumed[1] + "), type " + type);
                }
                return false;
            }
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        if (type == ViewCompat.TYPE_TOUCH || isLinkedParentFling(dyUnconsumed)) {
            if (enableLog) {
                log("dispatchNestedScroll: dyConsumed " + dyConsumed + ", dyUnconsumed " + dyUnconsumed + ", " + "type " + type);
            }
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
            if (enableLog) {
                log("dispatchNestedScroll: false-自己消费, dyConsumed " + dyConsumed + ", dyUnconsumed " + dyUnconsumed + ", " + "type " + type);
            }
            return false;
        }
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isCanScrollVertically((int) velocityY)) {
            log("dispatchNestedPreFling: false-自己消费 velocityY " + velocityY);
            return false;
        } else {
            log("dispatchNestedPreFling: velocityY " + velocityY);
            if (isNestedScrollingEnabled() && lastDy * velocityY < 0) {
                log("onNestedPreFling: lastDy " + lastDy + ", velocityY " + velocityY + ", velocityY 异常", true);
                return true;
            }
            return super.dispatchNestedPreFling(velocityX, velocityY);
        }
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return super.dispatchNestedFling(velocityX, velocityY, consumed && isCanScrollVertically((int) velocityY));
        log("dispatchNestedFling: velocityY " + velocityY + ", consumed " + consumed);
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    private void log(String message) {
        log(message, false);
    }

    private void log(String message, boolean isForce) {
        if (enableLog || isForce) {
            Log.i("FCNested", "[" + uuid + "-FCRecyclerView] " + message);
        }
    }
}
