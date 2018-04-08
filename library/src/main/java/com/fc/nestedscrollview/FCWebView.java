package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.webkit.WebView;

/**
 * Created by fangcan on 2018/3/31.
 */

public class FCWebView extends WebView {
    private static final String TAG = "FCWebView";
    public static final int MODEL_ALL = 0; //自己优先滚动
    public static final int MODEL_UP = 1; //自己优先向上滚动
    public static final int MODEL_DOWN = 2; //自己优先向下滚动
    public static final int MODEL_NONE = 3; //自己最后滚动

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private int mLastY;

    private NestedScrollingChildHelper mChildHelper;
    private VelocityTracker mVelocityTracker;
    private final int mTouchSlop;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;
    private float parentConsumed;

    private int nestedScrollModel = MODEL_ALL;
//    private float startFlingVelocity;
//    private long startFlingTime;
//    private float consumedDistance;
//    private boolean isFling;

    public FCWebView(Context context) {
        this(context, null);
    }

    public FCWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public FCWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FCWebView);
        nestedScrollModel = typedArray.getInt(R.styleable.FCWebView_fc_scroll_mode, MODEL_ALL);
        boolean isNestedScrollEnable = typedArray.getBoolean(R.styleable.FCWebView_fc_is_nested_scroll_enabled, true);
        typedArray.recycle();

        mChildHelper = new NestedScrollingChildHelper(this);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        setNestedScrollingEnabled(isNestedScrollEnable);
    }

    /**
     * 当滚到顶或底部的时候 否联动parent滚动
     * @param direction < 0 向下滚，> 0 向上滚
     * @return
     */
    protected boolean isLinkedParentFling(int direction) {
        return true;
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

    /**
     * 计算加速度
     */
    private float calculateVelocity(float startVelocity, float consumedDistance, long duration) {
        float velocity1 = consumedDistance / duration * 1000;
        float velocity2 = consumedDistance * 8;
        if (Math.abs(startVelocity) > mMaximumVelocity) {
            return Math.min(Math.abs(startVelocity - velocity1), mMaximumVelocity);
        } else {
            return Math.min((Math.abs(startVelocity - velocity1) + Math.abs(startVelocity - velocity2)) / 2, mMaximumVelocity);
        }
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);
//        int dy = top - oldTop;
//        if (dy != 0 && isNestedScrollEnable) {
//            consumedDistance += dy;
//            if (isFling && !canScrollVertically(dy)) {
//                long duration = System.currentTimeMillis() - this.startFlingTime;
//                int velocityY;
//                if (dy < 0) {
//                    velocityY = (int) -calculateVelocity(startFlingVelocity, consumedDistance, duration);
//                } else {
//                    velocityY = (int) calculateVelocity(startFlingVelocity, consumedDistance, duration);
//                }
//                Log.i(TAG, "consumedDistance: " + consumedDistance + ", velocityY: " + velocityY + ", startFlingVelocity: " + startFlingVelocity + ", startFlingTime: " + duration);
//                if (velocityY != 0) {
//                    if (isLinkedParentFling(dy)) {
//                        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
//                        dispatchNestedPreFling(0, velocityY);
//                        stopNestedScroll();
//                    }
//                }
//                isFling = false;
//            }
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isNestedScrollingEnabled()) {
            return super.onTouchEvent(ev);
        } else {
            boolean returnValue = false;

            MotionEvent event = MotionEvent.obtain(ev);
            final int action = MotionEventCompat.getActionMasked(event);
            if (action == MotionEvent.ACTION_DOWN) {
                mNestedOffsetY = 0;
                parentConsumed = 0;
//                consumedDistance = 0;
//                startFlingTime = System.currentTimeMillis();
//                isFling = false;
            }
            int eventY = (int) event.getY();
            event.offsetLocation(0, mNestedOffsetY);

            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            boolean eventAddedToVelocityTracker = false;
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int deltaY = mLastY - eventY;
                    if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                        deltaY -= mScrollConsumed[1];
                        parentConsumed += mScrollConsumed[1];
                        mLastY = eventY - mScrollOffset[1];
                        mNestedOffsetY += mScrollOffset[1];
                    }
                    if (deltaY != 0) {
//                    if (deltaY > 0) {
                        event.offsetLocation(0, -mNestedOffsetY);
//                    }
                        returnValue = super.onTouchEvent(event);
//                    if (deltaY < 0) {
                        if (isCanScrollVertically(deltaY)) {
                            mLastY = eventY;
                        } else {
                            mLastY = eventY - mScrollOffset[1];
                            if (dispatchNestedScroll(0, 0, 0, deltaY, mScrollOffset)) {
                                mLastY -= mScrollOffset[1];
                                event.offsetLocation(0, mScrollOffset[1]);
                                mNestedOffsetY += mScrollOffset[1];
                            }
                        }
//                    }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    returnValue = super.onTouchEvent(event);
                    mLastY = eventY;
                    // start NestedScroll
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                    break;
                case MotionEvent.ACTION_UP:
                    final int actionIndex = MotionEventCompat.getActionIndex(event);
                    mVelocityTracker.addMovement(event);
                    eventAddedToVelocityTracker = true;
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int mScrollPointerId = MotionEventCompat.getPointerId(event, actionIndex);
                    float vY = -mVelocityTracker.getYVelocity(mScrollPointerId);
                    Log.i(TAG, "mVelocityTracker vY: " + vY);
//                    startFlingVelocity = vY;
                    // 产生 fling 事件
                    if (Math.abs(vY) > mMinimumVelocity) {
                        if (!dispatchNestedPreFling(0, vY)) {
                            dispatchNestedFling(0, vY, true);
                        }
//                        startFlingTime = System.currentTimeMillis();
//                        consumedDistance = 0;
//                        isFling = true;
                    }
                    if (Math.abs(parentConsumed) < mTouchSlop) {//防止触发点击事件
                        returnValue = super.onTouchEvent(event);
                    }
                    resetTouch();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    eventAddedToVelocityTracker = true;
                    returnValue = super.onTouchEvent(event);
                    resetTouch();
                    break;
            }

            if (!eventAddedToVelocityTracker) {
                mVelocityTracker.addMovement(event);
            }
            return returnValue;
        }
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        stopNestedScroll();
    }

    // Nested Scroll implements
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (isCanScrollVertically(dy)) {
            return false;
        } else {
            return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isCanScrollVertically((int) velocityY)) {//如果我没有滚动到最顶上，则不传递给parent
            return false;
        } else {
            return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
        }
    }
}
