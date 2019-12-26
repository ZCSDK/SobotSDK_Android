package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.SobotKeyWordTransfer;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;

public class RobotKeyWordMessageHolder extends MessageHolderBase {

    private TextView tv_title;
    private LinearLayout sobot_keyword_grouplist;

    public RobotKeyWordMessageHolder(Context context, View convertView) {
        super(context, convertView);
        tv_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_keyword_tips_msg"));
        sobot_keyword_grouplist = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_keyword_grouplist"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {
        if (message != null) {
            SobotKeyWordTransfer sobotKeyWordTransfer = message.getSobotKeyWordTransfer();
            if (sobotKeyWordTransfer != null) {
                if (sobotKeyWordTransfer.getTipsMessage() != null) {
                    applyTextViewUIConfig(tv_title);
                    tv_title.setText(sobotKeyWordTransfer.getTipsMessage());
                }

                List<ZhiChiGroupBase> groupList = sobotKeyWordTransfer.getGroupList();
                if (groupList != null && groupList.size() > 0) {
                    sobot_keyword_grouplist.removeAllViews();
                    sobot_keyword_grouplist.setVisibility(View.VISIBLE);
                    for (int i = 0; i < groupList.size(); i++) {
                        KeyWorkTempModel model = new KeyWorkTempModel();
                        model.setTempGroupId(groupList.get(i).getGroupId());
                        model.setKeyword(sobotKeyWordTransfer.getKeyword());
                        model.setKeywordId(sobotKeyWordTransfer.getKeywordId());
                        TextView tv = ChatUtils.initAnswerItemTextView(context, false);
                        tv.setText(groupList.get(i).getGroupName());
                        tv.setTag(model);
                        tv.setOnClickListener(mKeyWorkCheckGroupClickListener);
                        sobot_keyword_grouplist.addView(tv);
                    }
                } else {
                    sobot_keyword_grouplist.setVisibility(View.GONE);
                }
            }
        }
    }

    private View.OnClickListener mKeyWorkCheckGroupClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK);
            KeyWorkTempModel sobotKeyWordTransfer = (KeyWorkTempModel) v.getTag();
            intent.putExtra("tempGroupId", sobotKeyWordTransfer.getTempGroupId());
            intent.putExtra("keyword", sobotKeyWordTransfer.getKeyword());
            intent.putExtra("keywordId", sobotKeyWordTransfer.getKeywordId());
            mContext.sendBroadcast(intent);
        }
    };

    class KeyWorkTempModel {
        private String tempGroupId;
        private String keyword;
        private String keywordId;

        public String getTempGroupId() {
            return tempGroupId;
        }

        public void setTempGroupId(String tempGroupId) {
            this.tempGroupId = tempGroupId;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeywordId() {
            return keywordId;
        }

        public void setKeywordId(String keywordId) {
            this.keywordId = keywordId;
        }
    }
}