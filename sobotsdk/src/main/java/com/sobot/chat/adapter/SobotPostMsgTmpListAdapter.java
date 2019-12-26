package com.sobot.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseGvAdapter;
import com.sobot.chat.api.model.SobotPostMsgTemplate;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

/**
 * 留言模版适配器
 */
public class SobotPostMsgTmpListAdapter extends SobotBaseGvAdapter<SobotPostMsgTemplate> {

    public SobotPostMsgTmpListAdapter(Context context, List<SobotPostMsgTemplate> list) {
        super(context, list);
    }

    @Override
    protected String getContentLayoutName() {
        return "sobot_list_item_robot";
    }

    @Override
    protected SobotBaseGvAdapter.BaseViewHolder getViewHolder(Context context, View view) {
        return new SobotPostMsgTmpListAdapter.ViewHolder(context,view);
    }

    private static class ViewHolder extends SobotBaseGvAdapter.BaseViewHolder<SobotPostMsgTemplate> {
        private TextView sobot_tv_content;
        private LinearLayout sobot_ll_content;

        private ViewHolder(Context context, View view) {
            super(context,view);
            sobot_ll_content = (LinearLayout) view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_ll_content"));
            sobot_tv_content = (TextView) view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_tv_content"));
        }

        public void bindData(SobotPostMsgTemplate data, int position) {
            if (data != null && !TextUtils.isEmpty(data.getTemplateName())) {
                sobot_ll_content.setVisibility(View.VISIBLE);
//                sobot_ll_content.setSelected(true);
                sobot_tv_content.setText(data.getTemplateName());
            } else {
                sobot_ll_content.setVisibility(View.INVISIBLE);
                sobot_ll_content.setSelected(false);
                sobot_tv_content.setText("");
            }
        }
    }
}