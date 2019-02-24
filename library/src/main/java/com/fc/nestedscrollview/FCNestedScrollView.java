package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by fangcan on 2018/3/28.
 */

public class FCNestedScrollView extends NestedScrollView implements NestedScrollingParent2 {
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

    public FCNestedScrollView(Context context) {
        this(context, null);
    }

    public FCNestedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FCNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FCNestedScrollView);
        nestedScrollModel = typedArray.getInt(R.styleable.FCNestedScrollView_fc_scroll_mode, MODEL_ALL);
        isLinkedParent = typedArray.getBoolean(R.styleable.FCNestedScrollView_fc_is_linked_parent, true);
        isLinkedChild = typedArray.getBoolean(R.styleable.FCNestedScrollView_fc_is_linked_child, true);
        typedArray.recycle();
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
        return isLinkedChild && flingView != null && (flingView.canScrollVertically(direction) || flingView instanceof FCSwipeRefreshLayout);
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
        } else if (flingView instanceof FCSwipeRefreshLayout) {
            ((FCSwipeRefreshLayout) flingView).fling(velocityY);
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
                    if (velocityY != 0) {
                        linkedChildFling(view, velocityY);
                    }
                }
                isFling = false;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            targetFlingView = null;
            isFling = false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isCanScrollVertically((int) velocityY)) {
            return false;
        } else {
            return super.dispatchNestedPreFling(velocityX, velocityY);
        }
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        isFling = velocityY != 0;
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

//    @Override
//    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
//        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
//    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (isCanScrollVertically(dy)) {
            return false;
        } else {
            if (type == ViewCompat.TYPE_TOUCH) {
                return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
            } else {
                if (isLinkedParentFling(dy)) {
//                    Log.i(TAG, "dispatchNestedPreScroll: type: " + type + ", dy: " + dy);
                    return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
                } else {
                    return false;
                }
            }
        }
    }

//    @Override
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
//        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
//    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
           if (isLinkedParentFling(dyUnconsumed)) {
//                Log.i(TAG, "dispatchNestedScroll: type: " + type + ", dyUnconsumed: " + dyUnconsumed);
                return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
            } else {
                return false;
            }
        }
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        isFling = true;
//        Log.i(TAG, toString() + "fling--velocityY: " + velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        targetFlingView = target;
        isFling = true;
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    /**
     * @return
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //先让parent处理
        boolean flag = super.onNestedPreFling(target, velocityX, velocityY);
        //如果parent没有处理，我再处理
        if (!flag) {
            flag = canScrollVertically((int) velocityY);
            if (flag) {
                onNestedFling(target, velocityX, velocityY, false);
            }
        }
//        Log.i(TAG, "onNestedPreFling " + flag);
        return flag;
    }

//    @Override
//    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
//        if (type == ViewCompat.TYPE_TOUCH) {
//            return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
//        } else {
//            return isNestedScrolling2Enabled() ? (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 : false;
//        }
//    }
//
//    @Override
//    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
//        if (type == ViewCompat.TYPE_TOUCH) {
//            onNestedScrollAccepted(child, target, axes);
//        } else {
//            if (isNestedScrolling2Enabled()) {
//                onNestedScrollAccepted(child, target, axes);
//            }
//        }
//    }

//    @Override
//    public void onStopNestedScroll(@NonNull View target, int type) {
//        onStopNestedScroll(target);
//    }

//    @Override
//    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
//    }

//    @Override
//    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH);
//    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (type == ViewCompat.TYPE_TOUCH || isNestedScrolling2Enabled()) {
            final int oldScrollY = getScrollY();
            scrollBy(0, dyUnconsumed);
            final int myConsumed = getScrollY() - oldScrollY;
            final int myUnconsumed = dyUnconsumed - myConsumed;
            dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        if (type == ViewCompat.TYPE_TOUCH || isNestedScrolling2Enabled()) {
            dispatchNestedPreScroll(dx, dy, consumed, null, type);
            if (dy != consumed[1] && canScrollVertically(dy)) {
                scrollBy(0, dy - consumed[1]);//减去parent消费的距离
                consumed[1] = dy;
            }
        }
    }
}
