package com.sunday.imoocrecyclerview.common;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @项目名 ImoocRecyclerView
 * @文件名 CommonViewHolder.java
 * @作者 geely
 * @创建时间 2021年06月09日 13:59:00
 * @描述 CommonViewHolder
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {

    //子View的集合
    private SparseArray<View> mViews;
    private Context context;

    public CommonViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        mViews = new SparseArray<>();
    }

    public static CommonViewHolder getCommonViewHolder(Context context, View itemView) {
        return new CommonViewHolder(context, itemView);
    }

    /**
     * 提供给外部访问 View 的方法
     *
     * @param viewId id
     * @param <T>    泛型
     * @return 泛型
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置文本
     *
     * @param viewId id
     * @param text   文本
     * @return this
     */
    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置图片
     *
     * @param viewId id
     * @param resId  图片id
     * @return this
     */
    public CommonViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }
}
