package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by fangcan on 2018/3/28.
 */

public class FCNestedScrollView extends NestedScrollView {
    private static final String TAG = "FCNestedScrollView";
    public static final int MODEL_ALL = 0; //自己优先滚动
    public static final int MODEL_UP = 1; //自己优先向上滚动
    public static final int MODEL_DOWN = 2; //自己优先向下滚动
    public static final int MODEL_NONE = 3; //自己最后滚动

    /** 当滚到顶或底部的时候 需要fling 的child view */
    private View linkChildView;
    private View targetFlingView;
    private boolean isFling;

    private int nestedScrollModel = MODEL_ALL;
    /** 当滚到顶或底部的时候 否联动parent滚动 */
    private boolean isLinkedParent;
    /** 当自己滚到顶或底部的时候 否联动child滚动 */
    private boolean isLinkedChild;
    private boolean isNestedScrolling2Enabled = true;
    // 测试看日志专用
    public String logId;
    public boolean enableLog = false;

    public int lastDy;

    private CopyOnWriteArraySet<OnScrollChangeListener> listeners = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<OnScrollStateListener> onScrollStateListeners = new CopyOnWriteArraySet<>();

    private long lastScrollUpdate = -1;
    private int scrollTaskInterval = 100;
    private boolean isTouched = false;

    // 用来修复 NestedScrollView 嵌套滚动 bug
    private boolean nonTouchFlag = false;
    private NestedScrollingChild2 nonTouchTargetView = null;

    private Runnable scrollingRunnable = new Runnable() {
        @Override
        public void run() {
            long time = System.currentTimeMillis() - lastScrollUpdate;
            if ((!isTouched && time > scrollTaskInterval) || time > scrollTaskInterval * 3) {
                // Scrolling has stopped.
                lastScrollUpdate = -1;
                notifyScrollEnd();
            } else {
                postDelayed(this, scrollTaskInterval);
            }
        }
    };

    public void addOnScrollStateListener(OnScrollStateListener onScrollStateListener) {
        onScrollStateListeners.add(onScrollStateListener);
    }

    public void removeOnScrollStateListener(OnScrollStateListener onScrollStateListener) {
        onScrollStateListeners.remove(onScrollStateListener);
    }

    private NestedScrollView.OnScrollChangeListener defaultListener = new OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
            if (listeners != null && !listeners.isEmpty()) {
                for (NestedScrollView.OnScrollChangeListener listener : listeners) {
                    if (listener != null) {
                        listener.onScrollChange(nestedScrollView, i, i1, i2, i3);
                    }
                }
            }
        }
    };

    public FCNestedScrollView(Context context) {
        this(context, null);
    }

    public FCNestedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FCNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        logId = UUID.randomUUID().toString();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FCNestedScrollView);
        nestedScrollModel = typedArray.getInt(R.styleable.FCNestedScrollView_fc_scroll_mode, MODEL_ALL);
        isLinkedParent = typedArray.getBoolean(R.styleable.FCNestedScrollView_fc_is_linked_parent, true);
        isLinkedChild = typedArray.getBoolean(R.styleable.FCNestedScrollView_fc_is_linked_child, true);
        typedArray.recycle();

        setOnScrollChangeListener(defaultListener);
    }

    public void addOnScrollChangeListener(NestedScrollView.OnScrollChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeOnScrollChangeListener(NestedScrollView.OnScrollChangeListener listener) {
        listeners.remove(listener);
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

    public boolean isNestedScrolling2Enabled() {
        return isNestedScrolling2Enabled;
    }

    public void setNestedScrolling2Enabled(boolean enabled) {
        this.isNestedScrolling2Enabled = enabled;
    }

    public void setLinkChildView(View linkChildView) {
        this.linkChildView = linkChildView;
    }

    protected View getLinkedFlingView() {
        if (targetFlingView != null) {
            return targetFlingView;
        } else {
            return linkChildView;
        }
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

    /**
     * 当滚到顶或底部的时候 否联动child滚动
     * @param direction < 0 向下滚，> 0 向上滚
     * @return
     */
    protected boolean isLinkedChildFling(int direction, View flingView) {
        if (!isLinkedChild || flingView == null) {
            return false;
        }
        if (flingView instanceof FCFlingView) {
            return ((FCFlingView) flingView).canFling(direction);
        } else {
            return flingView.canScrollVertically(direction);
        }
    }

    /**
     * 当滚到顶或底部的时候, 联动child滚动
     * @param flingView
     * @param velocityY
     */
    protected void linkedChildFling(View flingView, int velocityY) {
        if (flingView instanceof RecyclerView) {
            ((RecyclerView) flingView).fling(0, velocityY);
        } else if (flingView instanceof NestedScrollView) {
            ((NestedScrollView) flingView).fling(velocityY);
        } else if (flingView instanceof WebView) {
            ((WebView) flingView).flingScroll(0, velocityY);
        } else if (flingView instanceof FCFlingView) {
            ((FCFlingView) flingView).fling(velocityY);
        }
    }

    protected boolean isCanScrollVertically(int direction) {
        boolean flag = false;
        switch (nestedScrollModel) {
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
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);
        int dy = top - oldTop;
        if (dy != 0) {
            if (isFling && !canScrollVertically(dy)) {
                View view = getLinkedFlingView();
                if (isLinkedChildFling(dy, view)) {
//                        Log.i(TAG, "onScrollChanged   linkedChildFling ");
                    int velocityY = (int) FcNestedUtil.getCurrVelocityY(this);
                    if (lastDy * velocityY < 0) {
                        log("onScrollChanged: lastDy " + lastDy + ", velocityY " + velocityY + ", velocityY 异常", true);
                    } else {
                        if (velocityY != 0) {
                            linkedChildFling(view, velocityY);
                        }
                    }
                }
                isFling = false;
            }
        }

        if (!onScrollStateListeners.isEmpty()) {
            if (lastScrollUpdate == -1) {
                notifyScrollStart();
                postDelayed(scrollingRunnable, scrollTaskInterval);
            }
            lastScrollUpdate = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastDy = 0;
            isTouched = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            isTouched = false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastDy = 0;
            targetFlingView = null;
            isFling = false;
            isTouched = true;
            if (nonTouchFlag && nonTouchTargetView != null) {
                nonTouchTargetView.stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            isTouched = false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        log("startNestedScroll: axes " + axes + ", type " + type);
        boolean flag = super.startNestedScroll(axes, type);
        lastDy = 0;
        return flag;
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
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isCanScrollVertically((int) velocityY)) {
            log("dispatchNestedPreFling: false-自己消费, velocityY " + velocityY);
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
        isFling = velocityY != 0 && canScrollVertically((int) velocityY);
        if (!isFling && !consumed) {
            View view = getLinkedFlingView();
            if (isLinkedChildFling((int) velocityY, view)) {
                log("dispatchNestedFling: false-child消费, velocityY " + velocityY);
                linkedChildFling(view, (int) velocityY);
                return false;
            } else {
                log("dispatchNestedFling: velocityY " + velocityY + ", consumed " + consumed);
                return super.dispatchNestedFling(velocityX, velocityY, consumed);
            }
        } else {
            log("dispatchNestedFling: velocityY " + velocityY + ", consumed " + consumed);
            return super.dispatchNestedFling(velocityX, velocityY, consumed);
        }
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (!isNestedScrollingEnabled()) return false;
        lastDy = dy;
        if (isCanScrollVertically(dy)) {
            if (enableLog) {
                log("dispatchNestedPreScroll: false-自己消费1, dy " + dy + ", consumed(" + consumed[0] + ", " + consumed[1] + "), type " + type);
            }
            return false;
        } else {
            if (type == ViewCompat.TYPE_TOUCH) {
                if (enableLog) {
                    log("dispatchNestedPreScroll: dy " + dy + ", consumed(" + consumed[0] + ", " + consumed[1] + "), type " + type);
                }
                return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
            } else {
                if (isLinkedParentFling(dy)) {
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
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            if (enableLog) {
                log("dispatchNestedScroll: dyConsumed " + dyConsumed + ", dyUnconsumed " + dyUnconsumed + ", " + "type " + type);
            }
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
           if (isLinkedParentFling(dyUnconsumed)) {
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
    }

    @Override
    public void fling(int velocityY) {
        log("fling: velocityY " + velocityY);
        super.fling(velocityY);
        isFling = true;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        targetFlingView = target;
        isFling = true;
        if (!consumed && !canScrollVertically((int) velocityY)) {
            View view = getLinkedFlingView();
            if (isLinkedChildFling((int) velocityY, view)) {
                log("onNestedFling: child消费, velocityY " + velocityY);
                linkedChildFling(view, (int) velocityY);
                return true;
            }
        }
        log("onNestedFling: velocityY " + velocityY + ", consumed " + consumed);
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    /**
     * @return
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //先让parent处理
        log("onNestedPreFling: velocityY " + velocityY);
        boolean flag = super.onNestedPreFling(target, velocityX, velocityY);
        //如果parent没有处理，我再处理
        if (!flag) {
            flag = canScrollVertically((int) velocityY);
            if (flag) {
                log("onNestedPreFling: 手动调用onNestedFling, velocityY " + velocityY + ", consumed false");
                onNestedFling(target, velocityX, velocityY, false);
            }
        }
//        Log.i(TAG, "onNestedPreFling " + flag);
        return flag;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        log("onStartNestedScroll: axes  " + axes + ", type " + type);
        boolean flag = super.onStartNestedScroll(child, target, axes, type);
        if (flag) {
            if (type == ViewCompat.TYPE_NON_TOUCH && target instanceof NestedScrollingChild2) {
                nonTouchFlag = true;
                nonTouchTargetView = (NestedScrollingChild2) target;
            }
        }
        return flag;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        log("onNestedScrollAccepted: axes  " + axes + ", type " + type);
        super.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        log("onNestedScrollAccepted: type " + type);
        super.onStopNestedScroll(target, type);
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            nonTouchTargetView = null;
            nonTouchFlag = false;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (type == ViewCompat.TYPE_TOUCH || isNestedScrolling2Enabled()) {
            final int oldScrollY = getScrollY();
            scrollBy(0, dyUnconsumed);
            if (enableLog) {
                log("onNestedScroll: 先消费, 后传递 dyUnconsumed " + dyUnconsumed + ", type " + type);
            }
            final int myConsumed = getScrollY() - oldScrollY;
            final int myUnconsumed = dyUnconsumed - myConsumed;
            dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type);
        } else {
            log("onNestedScroll: 不消费, 不传递");
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        if (type == ViewCompat.TYPE_TOUCH || isNestedScrolling2Enabled()) {
            if (enableLog) {
                log("onNestedPreScroll: 先传递，后消费, dy " + dy + ", type " + type);
            }
            dispatchNestedPreScroll(dx, dy, consumed, null, type);
            if (dy != consumed[1] && canScrollVertically(dy)) {
                scrollBy(0, dy - consumed[1]);//减去parent消费的距离
                consumed[1] = dy;
            }
        } else {
            log("onNestedPreScroll: 不消费, 不传递");
        }
    }

    private void log(String message) {
        log(message, false);
    }

    private void log(String message, boolean isForce) {
        if (enableLog || isForce) {
            Log.i("FCNested", "[" + logId + "-FCNestedScrollView] " + message);
        }
    }
}
