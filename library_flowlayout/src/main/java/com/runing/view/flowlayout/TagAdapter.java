package com.runing.view.flowlayout;

import android.view.View;

/**
 * 标签适配器
 * Created by runing on 2016/9/21.
 */
public abstract class TagAdapter {
    private DataChangedCallBack mCallBack;

    public interface DataChangedCallBack {
        void onDataChange();
    }

    public void setDataChangeCallBack(DataChangedCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    /**
     * 刷新数据并显示
     */
    public void notifyDataSetChanged() {
        if (mCallBack != null) {
            mCallBack.onDataChange();
        }
    }

    public abstract int getCount();

    public abstract Object getItem(int position);

    public abstract View getView(FlowLayout parent, int position, Object item);
}
