package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 商品咨询项目
 * Created by jinxl on 2017/3/17.
 */
public class ConsultMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private TextView tv_title;//   商品标题页title    商品描述  商品图片   发送按钮  商品标签
    private ImageView iv_pic;
    private Button btn_sendBtn;
    private View sobot_container;
    private TextView tv_lable;
    private TextView tv_des;
    private int defaultPicResId;
    private ZhiChiMessageBase mData;

    public ConsultMessageHolder(Context context, View convertView) {
        super(context, convertView);
        btn_sendBtn = (Button) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_sendBtn"));
        sobot_container = convertView.findViewById(ResourceUtils.getResId(context, "sobot_container"));
        iv_pic = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_pic"));
        tv_title = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_title"));
        tv_lable = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_label"));
        tv_des = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_des"));
        defaultPicResId = ResourceUtils.getDrawableId(context, "sobot_icon_consulting_default_pic");
        sobot_container.setOnClickListener(this);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        mData = message;
        String title = message.getContent();
        String picurl = message.getPicurl();
        final String url = message.getUrl();
        String lable = message.getAname();
        String describe = message.getReceiverFace();
        if (!TextUtils.isEmpty(picurl)) {
            iv_pic.setVisibility(View.VISIBLE);
            tv_des.setMaxLines(1);
            tv_des.setEllipsize(TextUtils.TruncateAt.END);
            SobotBitmapUtil.display(context, CommonUtils.encode(picurl), iv_pic, defaultPicResId, defaultPicResId);
        } else {
            iv_pic.setVisibility(View.GONE);
            iv_pic.setImageResource(defaultPicResId);
        }

        tv_des.setText(describe);
        tv_title.setText(title);

        if (!TextUtils.isEmpty(lable)) {
            tv_lable.setVisibility(View.VISIBLE);
            tv_lable.setText(lable);
        } else {
            if (!TextUtils.isEmpty(picurl)) {
                tv_lable.setVisibility(View.INVISIBLE);
            } else {
                tv_lable.setVisibility(View.GONE);
            }
        }

        btn_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("发送连接---->" + url);
                if (msgCallBack != null) {
                    msgCallBack.sendConsultingContent();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_container && mData != null && !TextUtils.isEmpty(mData.getUrl())) {
            if (SobotOption.hyperlinkListener != null) {
                SobotOption.hyperlinkListener.onUrlClick(mData.getUrl());
                return;
            }
            if (SobotOption.newHyperlinkListener != null) {
                //如果返回true,拦截;false 不拦截
                boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(mData.getUrl());
                if (isIntercept) {
                    return;
                }
            }
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", mData.getUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
}
