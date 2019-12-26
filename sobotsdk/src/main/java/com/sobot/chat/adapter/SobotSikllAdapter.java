package com.sobot.chat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.List;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

@SuppressWarnings({"rawtypes"})
public class SobotSikllAdapter extends SobotBaseAdapter {
    private LayoutInflater mInflater;
    private TextView sobot_tv_content;//技能组名称
    private TextView sobot_tv_desc;//无客服时描述
    private LinearLayout sobot_ll_content;
    private int msgFlag;//留言开关
    private Context mContext;

    @SuppressWarnings("unchecked")
    public SobotSikllAdapter(Context context, List<ZhiChiGroupBase> list, int msgFlag) {
        super(context, list);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.msgFlag = msgFlag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(ResourceUtils
                    .getIdByName(context, "layout", "sobot_list_item_skill"), null);
        }
        sobot_ll_content = (LinearLayout) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_ll_content"));
        sobot_tv_content = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_tv_content"));
        sobot_tv_desc = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_tv_desc"));
        sobot_ll_content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(mContext, 36)));
        ZhiChiGroupBase zhiChiSkillIModel = (ZhiChiGroupBase) list.get(position);
        if (zhiChiSkillIModel != null) {
            sobot_ll_content.setVisibility(View.VISIBLE);
//                sobot_ll_content.setSelected(true);
            sobot_tv_content.setText(zhiChiSkillIModel.getGroupName());
            if ("true".equals(zhiChiSkillIModel.isOnline())) {
                sobot_tv_desc.setVisibility(View.GONE);
                sobot_tv_content.setTextSize(14);
                sobot_tv_content.setTextColor(Color.parseColor("#0DAEAF"));
            } else {
                String content;
                sobot_tv_content.setTextSize(12);
                sobot_tv_content.setTextColor(Color.parseColor("#ACB5C4"));
                if (msgFlag == ZhiChiConstant.sobot_msg_flag_open) {
                    content = ResourceUtils.getResString(context, "sobot_no_access") + ResourceUtils.getResString(context, "sobot_douhao") + ResourceUtils.getResString(context, "sobot_can") + "<font color='#0DAEAF'>" + ResourceUtils.getResString(context, "sobot_leavemsg") + "</a>";
                } else {
                    content = ResourceUtils.getResString(context, "sobot_no_access");
                }
                sobot_tv_desc.setText(Html.fromHtml(content));
                sobot_tv_desc.setVisibility(View.VISIBLE);
            }
        } else {
            sobot_ll_content.setVisibility(View.INVISIBLE);
            sobot_tv_desc.setVisibility(View.GONE);
            //   sobot_ll_content.setSelected(false);
            sobot_tv_content.setText("");
        }
        return convertView;
    }
}