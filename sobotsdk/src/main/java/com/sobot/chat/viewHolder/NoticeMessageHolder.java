package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.StExpandableTextView;

/**
 * 非置顶公告消息
 * Created by jinxl on 2017/3/17.
 */
public class NoticeMessageHolder extends MessageHolderBase {
    private StExpandableTextView expand_text_view;

    private ZhiChiMessageBase mMessage;


    public NoticeMessageHolder(Context context, View convertView) {
        super(context, convertView);
        expand_text_view = (StExpandableTextView) convertView.findViewById(ResourceUtils.getResId(context, "expand_text_view"));
        expand_text_view.setLinkBottomLine(true);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        mMessage = message;
        if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
            expand_text_view.setText(message.getAnswer().getMsg());
        }
    }
}
