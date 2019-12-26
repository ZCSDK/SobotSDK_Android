package com.sobot.chat.adapter.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

public abstract class SobotBaseGvAdapter<T> extends SobotBaseAdapter {
    protected LayoutInflater mInflater;


    public SobotBaseGvAdapter(Context context, List<T> list) {
        super(context, list);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(ResourceUtils
                    .getResLayoutId(context, getContentLayoutName()), null);
            holder = getViewHolder(context,convertView);
            convertView.setTag(holder);
        } else {
            holder = (BaseViewHolder) convertView.getTag();
        }
        holder.bindData(list.get(position), position);
        return convertView;
    }

    public abstract static class BaseViewHolder<T> {
        protected Context mContext;
        protected View mItemView;
        public BaseViewHolder(Context context, View view) {
            this.mContext=context;
            this.mItemView=view;
        }

        public abstract void bindData(T data, int position);
    }

    /**
     * 获取item的布局
     *
     * @return
     */
    protected abstract String getContentLayoutName();

    /**
     * 获取Viewholder
     *
     * @return
     */
    protected abstract BaseViewHolder getViewHolder(Context context, View view);

    /** 对TextView设置不同状态时其文字颜色。 */
    protected ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[] { pressed, focused, normal, focused, unable, normal };
        int[][] states = new int[6][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };
        states[2] = new int[] { android.R.attr.state_enabled };
        states[3] = new int[] { android.R.attr.state_focused };
        states[4] = new int[] { android.R.attr.state_window_focused };
        states[5] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
}