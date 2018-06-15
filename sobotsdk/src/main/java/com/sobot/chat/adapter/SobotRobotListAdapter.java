package com.sobot.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

public class SobotRobotListAdapter extends SobotBaseAdapter {
    private LayoutInflater mInflater;


    public SobotRobotListAdapter(Context context, List<SobotRobot> list) {
        super(context, list);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(ResourceUtils
                    .getIdByName(context, "layout", "sobot_list_item_robot"), null);
            holder = new ViewHolder(context, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bindData((SobotRobot) list.get(position), position);
        return convertView;
    }

    private static class ViewHolder {
        private TextView sobot_tv_content;
        private LinearLayout sobot_ll_content;
        private View sobot_divider_top;

        private ViewHolder(Context context, View view) {
            sobot_ll_content = (LinearLayout) view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_ll_content"));
            sobot_tv_content = (TextView) view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_tv_content"));
            sobot_divider_top = view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_divider_top"));
        }

        private void bindData(SobotRobot sobotRobot, int position) {
            sobot_divider_top.setVisibility(position < 2 ? View.VISIBLE : View.GONE);
            if (sobotRobot != null && !TextUtils.isEmpty(sobotRobot.getOperationRemark())) {
                sobot_ll_content.setVisibility(View.VISIBLE);
                sobot_ll_content.setSelected(sobotRobot.isSelected());
                sobot_tv_content.setText(sobotRobot.getOperationRemark());
            } else {
                sobot_ll_content.setVisibility(View.INVISIBLE);
                sobot_ll_content.setSelected(false);
                sobot_tv_content.setText("");
            }
        }
    }
}