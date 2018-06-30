package com.fc.nestedscrollview;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.OverScroller;

import java.lang.reflect.Field;

public class FcNestedUtil {

    public static float getCurrVelocityY(RecyclerView recyclerView) {
        try {
            OverScroller overScroller = getOverScroller(recyclerView);
            if (overScroller != null) {
                return getCurrVelocityY(overScroller);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float getCurrVelocityX(RecyclerView recyclerView) {
        try {
            OverScroller overScroller = getOverScroller(recyclerView);
            if (overScroller != null) {
                return getCurrVelocityX(overScroller);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float[] getCurrVelocityXY(RecyclerView recyclerView) {
        try {
            OverScroller overScroller = getOverScroller(recyclerView);
            if (overScroller != null) {
                return new float[]{getCurrVelocityX(overScroller), getCurrVelocityY(overScroller)};
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return new float[] {0, 0};
    }

    public static float getCurrVelocityY(NestedScrollView nestedScrollView) {
        try {
            OverScroller overScroller = getOverScroller(nestedScrollView);
            if (overScroller != null) {
                return getCurrVelocityY(overScroller);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static OverScroller getOverScroller(RecyclerView recyclerView) {
        OverScroller overScroller = null;
        try {
            Class cls = recyclerView.getClass();
            String name = RecyclerView.class.getName();
            while (cls != null) {
                if (cls.getName().equals(name)) {
                    Field viewFlinger = cls.getDeclaredField("mViewFlinger");
                    viewFlinger.setAccessible(true);
                    Object viewFlingerObject = viewFlinger.get(recyclerView);
                    Field field = viewFlingerObject.getClass().getDeclaredField("mScroller");
                    if (field != null) {
                        field.setAccessible(true);
                        Object object = field.get(viewFlingerObject);
                        if (object instanceof OverScroller) {
                            overScroller = (OverScroller) object;
                        }
                    }
                    break;
                } else {
                    cls = cls.getSuperclass();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overScroller;
    }

    public static OverScroller getOverScroller(NestedScrollView nestedScrollView) {
        Class cls = nestedScrollView.getClass();
        String name = NestedScrollView.class.getName();
        OverScroller overScroller = null;
        try {
            while (cls != null) {
                if (cls.getName().equals(name)) {
                    Field field = cls.getDeclaredField("mScroller");
                    if (field != null) {
                        field.setAccessible(true);
                        Object object = field.get(nestedScrollView);
                        if (object instanceof OverScroller) {
                            overScroller = (OverScroller) object;
                        }
                    }
                    break;
                } else {
                    cls = cls.getSuperclass();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overScroller;
    }

    public static float getCurrVelocityX(OverScroller overScroller) {
        try {
            Field mScrollerX = overScroller.getClass().getDeclaredField("mScrollerX");
            mScrollerX.setAccessible(true);
            Object splineOverScroller = mScrollerX.get(overScroller);
            Field field1 = splineOverScroller.getClass().getDeclaredField("mCurrVelocity");
            field1.setAccessible(true);
            return (float) field1.get(splineOverScroller);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float getCurrVelocityY(OverScroller overScroller) {
        try {
            Field mScrollerY = overScroller.getClass().getDeclaredField("mScrollerY");
            mScrollerY.setAccessible(true);
            Object splineOverScroller = mScrollerY.get(overScroller);
            Field field1 = splineOverScroller.getClass().getDeclaredField("mCurrVelocity");
            field1.setAccessible(true);
            return (float) field1.get(splineOverScroller);
        } catch (Exception e) {
            return 0;
        }
    }
}
