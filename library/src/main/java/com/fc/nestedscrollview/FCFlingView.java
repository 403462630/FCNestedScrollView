package com.fc.nestedscrollview;

public interface FCFlingView {

    /**
     * 判断是否能滚动
     * @param direction < 0 向下滚，> 0 向上滚
     * @return
     */
    public boolean canFling(int direction);

    /**
     * 滚动实现
     * @param velocityY 加速度
     */
    public void fling(int velocityY);
}
