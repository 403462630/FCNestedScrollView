package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.OverScroller;

import java.lang.reflect.Field;

import static android.support.v4.view.ViewCompat.TYPE_TOUCH;

/**
 * Created by fangcan on 2018/3/28.
 */

public class FCRecyclerView extends RecyclerView {
    public static final int MODEL_ALL = 0; //自己优先滚动
    public static final int MODEL_UP = 1; //自己优先向上滚动
    public static final int MODEL_DOWN = 2; //自己优先向下滚动
    public static final int MODEL_NONE = 3; //自己最后滚动
    private static final String TAG = "FCRecycleView";

    private ScrollerCompat scrollerCompat;
    private OverScroller overScroller;
    private int nestedScrollModel = MODEL_ALL;
    /** 当滚到顶或底部的时候 否联动parent滚动 */
    private boolean isLinkedParent;

    public FCRecyclerView(Context context) {
        this(context, null);
    }

    public FCRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FCRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FCRecyclerView);
        nestedScrollModel = typedArray.getInt(R.styleable.FCRecyclerView_fc_scroll_mode, MODEL_ALL);
        isLinkedParent = typedArray.getBoolean(R.styleable.FCRecyclerView_fc_is_linked_parent, true);
        typedArray.recycle();
        createScroller();
    }

    public int getNestedScrollModel() {
        return nestedScrollModel;
    }

    public void setNestedScrollModel(int nestedScrollModel) {
        this.nestedScrollModel = nestedScrollModel;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (getScrollState() == SCROLL_STATE_SETTLING && dy != 0 && !canScrollVertically(dy) && isLinkedParentFling(dy)) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, TYPE_TOUCH);
            if (dy > 0) {
                if (overScroller != null) {
                    dispatchNestedPreFling( 0, overScroller.getCurrVelocity());
                }
                if (scrollerCompat != null) {
                    dispatchNestedPreFling(0, scrollerCompat.getCurrVelocity()); //手动添加方向
                }
            } else {
                if (overScroller != null) {
                    dispatchNestedPreFling( 0, -overScroller.getCurrVelocity());
                }
                if (scrollerCompat != null) {
                    dispatchNestedPreFling(0, -scrollerCompat.getCurrVelocity()); //手动添加方向
                }
            }
            stopNestedScroll();
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
    public void setLayoutManager(LayoutManager layoutManager) {
        super.setLayoutManager(layoutManager);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return super.startNestedScroll(axes);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (isCanScrollVertically(dy)) {
            return false;
        } else {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (isCanScrollVertically(dy)) {
            return false;
        } else {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        }
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
        return super.dispatchNestedFling(velocityX, velocityY, consumed && isCanScrollVertically((int) velocityY));
    }

    private void createScroller() {
        try {
            Field viewFlinger = this.getClass().getSuperclass().getDeclaredField("mViewFlinger");
            viewFlinger.setAccessible(true);
            Object viewFlingerObject = viewFlinger.get(this);
            Class<?> viewFlingerClazz = Class.forName("android.support.v7.widget.RecyclerView$ViewFlinger");
            if (viewFlingerClazz.isInstance(viewFlingerObject)) {
                Object scrollerCompatObject = viewFlingerClazz.cast(viewFlingerObject);
                Field mScrollerCompat = viewFlingerClazz.getDeclaredField("mScroller");
                mScrollerCompat.setAccessible(true);

                Object scroller = mScrollerCompat.get(scrollerCompatObject);
                if (scroller instanceof OverScroller) {
                    overScroller = (OverScroller) scroller;
                } else if (scroller instanceof ScrollerCompat) {
                    scrollerCompat = (ScrollerCompat) scroller;
                }

                Log.d(TAG, "release check ok");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.d(TAG, "release check failed->NoSuchFieldException:" + e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "release check failed->IllegalAccessException:" + e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "release check failed->ClassNotFoundException:" + e.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
