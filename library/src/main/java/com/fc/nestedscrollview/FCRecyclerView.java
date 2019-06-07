package com.fc.nestedscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fangcan on 2018/3/28.
 */

public class FCRecyclerView extends RecyclerView {
    public static final int MODEL_ALL = 0; //自己优先滚动
    public static final int MODEL_UP = 1; //自己优先向上滚动
    public static final int MODEL_DOWN = 2; //自己优先向下滚动
    public static final int MODEL_NONE = 3; //自己最后滚动
    private static final String TAG = "FCRecycleView";

    private int nestedScrollModel = MODEL_ALL;
    /** 当滚到顶或底部的时候 否联动parent滚动 */
    private boolean isLinkedParent;
    private boolean isDraggingItem = false;
    private NestedScrollView nestedScrollView;
    private Handler handler = new Handler();
    private boolean isNestedScrollBy = false;

    private boolean isStartScroll = false;
    private OnScrollStateListener onScrollStateListener;

    public void setOnScrollStateListener(OnScrollStateListener onScrollStateListener) {
        this.onScrollStateListener = onScrollStateListener;
    }

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
        if (onScrollStateListener != null) {
            if (state == RecyclerView.SCROLL_STATE_DRAGGING || state == RecyclerView.SCROLL_STATE_SETTLING) {
                if (!isStartScroll) {
                    isStartScroll = true;
                    onScrollStateListener.onScrollStart();
                }
            } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
                if (isStartScroll) {
                    isStartScroll = false;
                    onScrollStateListener.onScrollEnd();
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
    public boolean startNestedScroll(int axes) {
        return super.startNestedScroll(axes);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
//        Log.i(TAG, "dispatchNestedPreScroll: " + type);
        if (isCanScrollVertically(dy)) {
            return false;
        } else {
//            Log.i(TAG, "=============" + type);
            if (type == ViewCompat.TYPE_TOUCH || isLinkedParentFling(dy)) {
                return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        if (type == ViewCompat.TYPE_TOUCH || isLinkedParentFling(dyUnconsumed)) {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
            return false;
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
//        return super.dispatchNestedFling(velocityX, velocityY, consumed && isCanScrollVertically((int) velocityY));
        return super.dispatchNestedFling(velocityX, velocityY, consumed);

    }
}
