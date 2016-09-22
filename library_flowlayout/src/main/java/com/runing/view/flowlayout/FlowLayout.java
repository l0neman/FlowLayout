package com.runing.view.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局
 * Created by runing on 2016/9/14.
 */

public class FlowLayout extends ViewGroup {

//    private static final String TAG = FlowLayout.class.getSimpleName();

    public static final int GRAVITY_LEFT = -1;
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_RIGHT = 1;

    private int mGravity = GRAVITY_LEFT;
    /**
     * 记录子View分布
     * <pre>
     *  | key-换行View位置 | value-本行高度和宽度 |
     *  |----------------|--------------------|
     *  | ...            | ...                |
     *  </pre>
     */
    private SparseArray<Point> childLocate = new SparseArray<>();

    /**
     * 依次记录不为gone的View位置
     */
    private List<Integer> viewPositions = new ArrayList<>();

    private TagAdapter mViewAdapter;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mGravity = array.getInt(R.styleable.FlowLayout_gravity, GRAVITY_LEFT);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        final int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        final int modeW = MeasureSpec.getMode(widthMeasureSpec);
        final int modeH = MeasureSpec.getMode(heightMeasureSpec);
        //限制宽度
        final int boundW = sizeW - getPaddingLeft() - getPaddingRight();

        childLocate.clear();
        viewPositions.clear();

//        Log.i(TAG, "sw--------->" + sizeW);
//        Log.i(TAG, "sh--------->" + sizeH);

        int lastWidth = 0;
        int lastHeight = 0;

        // 记录每行宽度，高度
        int lineWidth = 0;
        int lineHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            viewPositions.add(i);
            if (child.getVisibility() == View.GONE) {
                viewPositions.remove(i);
                continue;
            }

            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

            //子View所占的宽度高度
            final int childWSpace = child.getMeasuredWidth() +
                    params.leftMargin + params.rightMargin;
            final int childHSpace = child.getMeasuredHeight() +
                    params.topMargin + params.bottomMargin;

            // 是否换行
            if (lineWidth + childWSpace <= boundW) {
                lineWidth += childWSpace;
                lineHeight = Math.max(lineHeight, childHSpace);
            } else {
                lastWidth = Math.max(lineWidth, childWSpace);
                lastHeight += lineHeight;
                // 当前位置的上一个
                childLocate.put(viewPositions.get(viewPositions.size() - 2),
                        getNewPoint(lineWidth, lineHeight));
                // 记录新行
                lineWidth = childWSpace;
                lineHeight = childHSpace;
            }
            // 最后一个
            if (i == count - 1) {
                lastWidth = Math.max(lastWidth, lineHeight);
                Integer key;
                if (viewPositions.size() == 1) {
                    key = viewPositions.get(0);
                } else {
                    key = viewPositions.get(viewPositions.size() - 1);
                }
                childLocate.put(key,
                        getNewPoint(lineWidth, lineHeight));
                lastHeight += lineHeight;
            }
        }

        lastWidth += (getPaddingLeft() + getPaddingRight());
        lastHeight += (getPaddingTop() + getPaddingBottom());

//        Log.i(TAG, "lw--------->" + lastWidth);
//        Log.i(TAG, "lh--------->" + lastHeight);

        setMeasuredDimension(modeW == MeasureSpec.EXACTLY ? sizeW : lastWidth,
                modeH == MeasureSpec.EXACTLY ? sizeH : lastHeight);
    }

    private Point getNewPoint(int width, int height) {
        return new Point(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        // 每行左边和上边距基准
        int baseL = getPaddingLeft();
        int baseT = getPaddingTop();

        int start = -1;
        childLocate.append(-1, null);

        final int size = childLocate.size();
        for (int i = 1; i < size; i++) {
            int end = childLocate.keyAt(i);

            Point linePoint = childLocate.get(end);
            baseL += getLeftOffset(linePoint.x, getWidth());

            for (int j = start + 1; j <= end; j++) {
                View child = getChildAt(j);
                if (child.getVisibility() == View.GONE) {
                    return;
                }
                MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();

                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final int wSpace = childWidth + params.leftMargin + params.rightMargin;

                // 实际左边和上边距
                final int realT = baseT + params.topMargin;
                final int realL = baseL + params.leftMargin;

                child.layout(realL, realT, realL + childWidth, realT + childHeight);
                baseL += wSpace;
            }
            baseL = getPaddingLeft();
            baseT += linePoint.y;

            start = childLocate.keyAt(i);
        }
    }

    /**
     * 设置重力
     * @param gravity gravity
     */
    public void setGravity(int gravity) {
        this.mGravity = gravity;
        if (getChildCount() != 0) {
            invalidate();
        }
    }

    /**
     * 获取偏移值
     *
     * @param lineW  当前行宽
     * @param totalW 总宽
     * @return offset
     */
    private int getLeftOffset(int lineW, int totalW) {
        if (mGravity == GRAVITY_LEFT) {
            return 0;
        } else if (mGravity == GRAVITY_CENTER) {
            return (totalW - lineW) / 2;
        } else if (mGravity == GRAVITY_RIGHT) {
            return totalW - lineW;
        }
        return 0;
    }

    private TagAdapter.DataChangedCallBack mDataCallBack =
            new TagAdapter.DataChangedCallBack() {
                @Override
                public void onDataChange() {
                    refreshAllViews();
                }
            };

    /**
     * 设置适配器
     *
     * @param adapter adapter
     */
    public void setAdapter(TagAdapter adapter) {
        mViewAdapter = adapter;
        refreshAllViews();
        adapter.setDataChangeCallBack(mDataCallBack);
    }

    /**
     * 刷新View
     */
    private void refreshAllViews() {
        removeAllViews();

        final int itemCount = mViewAdapter.getCount();
        for (int i = 0; i < itemCount; i++) {
            View child = mViewAdapter.getView(this, i, mViewAdapter.getItem(i));
            if (child == null) {
                throw new NullPointerException("view is null!");
            }
            addView(child);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

}
