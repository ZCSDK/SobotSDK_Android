package com.sobot.chat.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

/**
 * 留言记录适配器
 *
 * @author Created by jinxl on 2019/3/7.
 */
public class SobotTicketInfoAdapter extends SobotBaseAdapter<SobotUserTicketInfo> {

    private Context mContext;


    private static final String[] layoutRes = {
            "sobot_ticket_info_item",//留言记录布局
    };

    //文件类型
    public static final int MSG_TYPE_FILE = 0;

    public SobotTicketInfoAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SobotUserTicketInfo data = list.get(position);
        if (data != null) {
            int itemType = getItemViewType(position);
            convertView = initView(convertView, itemType, position, data);
            BaseViewHolder holder = (BaseViewHolder) convertView.getTag();
            holder.bindData(data);
        }
        return convertView;
    }

    private View initView(View convertView, int itemType, int position, final SobotUserTicketInfo data) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
            BaseViewHolder holder;
            switch (itemType) {
                case MSG_TYPE_FILE: {
                    holder = new TicketInfoViewHolder(context, convertView);
                    break;
                }
                default:
                    holder = new TicketInfoViewHolder(context, convertView);
                    break;
            }
            convertView.setTag(holder);
        }
        return convertView;
    }

    /**
     * @return 返回有多少种UI布局样式
     */
    @Override
    public int getViewTypeCount() {
        if (layoutRes.length > 0) {
            return layoutRes.length;
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return MSG_TYPE_FILE;
    }

    static abstract class BaseViewHolder {
        Context mContext;

        BaseViewHolder(Context context, View view) {
            mContext = context;
        }

        abstract void bindData(SobotUserTicketInfo data);
    }

    class TicketInfoViewHolder extends BaseViewHolder {
        private TextView tv_title;
        private TextView tv_ticket_status;
        private TextView tv_content;
        private TextView tv_code;
        private TextView tv_time;
        private ImageView sobot_tv_new;

        private int bg1_resId;
        private int bg2_resId;
        private int bg3_resId;
        private int str1_resId;
        private int str2_resId;
        private int str3_resId;
        private Context mContext;

        TicketInfoViewHolder(Context context, View view) {
            super(context, view);
            mContext = context;
            tv_title = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_title"));
            tv_ticket_status = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_ticket_status"));
            tv_content = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content"));
            tv_code = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_code"));
            tv_time = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_time"));
            sobot_tv_new = (ImageView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_new"));
            bg1_resId = ResourceUtils.getDrawableId(context, "sobot_ticket_status_bg3");
            bg2_resId = ResourceUtils.getDrawableId(context, "sobot_ticket_status_bg2");
            bg3_resId = ResourceUtils.getDrawableId(context, "sobot_ticket_status_bg1");
            str1_resId = ResourceUtils.getResStrId(context, "sobot_created_1");
            str2_resId = ResourceUtils.getResStrId(context, "sobot_processing");
            str3_resId = ResourceUtils.getResStrId(context, "sobot_completed");
        }

        void bindData(SobotUserTicketInfo data) {
            tv_content.setText(TextUtils.isEmpty(data.getContent())?"": Html.fromHtml(data.getContent()));
            if (2 == data.getFlag()) {
                tv_ticket_status.setText(str2_resId);
                tv_ticket_status.setBackgroundResource(bg2_resId);
            } else if (3 == data.getFlag()) {
                tv_ticket_status.setText(str3_resId);
                tv_ticket_status.setBackgroundResource(bg3_resId);
            } else {
                tv_ticket_status.setText(str1_resId);
                tv_ticket_status.setBackgroundResource(bg1_resId);
            }
            sobot_tv_new.setVisibility(data.isNewFlag() ? View.VISIBLE : View.GONE);
            tv_time.setText(data.getTimeStr());
            tv_code.setText(String.format(ResourceUtils.getResString(mContext, "sobot_ticket_code"), data.getTicketCode()));
        }
    }

}