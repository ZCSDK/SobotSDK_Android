package com.sobot.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.StDocModel;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

public class SobotCategoryAdapter extends SobotBaseAdapter<StDocModel> {
    private LayoutInflater mInflater;

    public SobotCategoryAdapter(Context context, List<StDocModel> list) {
        super(context, list);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(ResourceUtils
                    .getResLayoutId(context, "sobot_list_item_help_category"), null);
            viewHolder = new ViewHolder(context, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindData(position, list.get(position));
        return convertView;
    }

    private static class ViewHolder {
        private TextView sobot_tv_title;

        public ViewHolder(Context context, View view) {
            sobot_tv_title = (TextView) view.findViewById(ResourceUtils
                    .getResId(context, "sobot_tv_title"));
        }

        public void bindData(int position, StDocModel data) {
            sobot_tv_title.setText(data.getQuestionTitle());
        }
    }
}