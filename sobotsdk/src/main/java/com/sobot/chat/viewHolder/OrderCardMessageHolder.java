package com.sobot.chat.viewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.image.SobotRCImageView;

import java.util.Locale;

/**
 * 订单卡片
 * Created by znw on 2019/9/28.
 */
public class OrderCardMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private View mContainer;
    private SobotRCImageView mPic;
    private TextView mTitle;
    private TextView mGoodsCount;
    private TextView mGoodsTotalMoney;
    private TextView mOrderStatus;
    private TextView mOrderNumber;
    private TextView mOrderCreatetime;
    private View mGoodsOrderSplit;
    private int defaultPicResId;
    private OrderCardContentModel orderCardContent;

    public OrderCardMessageHolder(Context context, View convertView) {
        super(context, convertView);
        mContainer = convertView.findViewById(ResourceUtils.getResId(context, "sobot_rl_hollow_container"));
        mPic = (SobotRCImageView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_pic"));
        mTitle = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_title"));
        mGoodsCount = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_count"));
        mGoodsTotalMoney = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_total_money"));
        mGoodsOrderSplit = (View) convertView.findViewById(ResourceUtils.getResId(context, "sobot_goods_order_split"));
        mOrderStatus = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_order_status"));
        mOrderNumber = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_order_number"));
        mOrderCreatetime = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_order_createtime"));
        defaultPicResId = ResourceUtils.getDrawableId(context, "sobot_icon_consulting_default_pic");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        orderCardContent = message.getOrderCardContent();
        if (orderCardContent != null) {
            if (orderCardContent.getGoods() != null && orderCardContent.getGoods().size() > 0) {
                mPic.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.VISIBLE);
                OrderCardContentModel.Goods firstGoods = orderCardContent.getGoods().get(0);
                SobotBitmapUtil.display(context, CommonUtils.encode(firstGoods.getPictureUrl())
                        , mPic, defaultPicResId, defaultPicResId);
                mTitle.setText(firstGoods.getName());
            } else {
                mPic.setVisibility(View.GONE);
                mTitle.setVisibility(View.GONE);
            }

            if ((orderCardContent.getGoods() != null && orderCardContent.getGoods().size() > 0) || !TextUtils.isEmpty(orderCardContent.getGoodsCount()) || orderCardContent.getTotalFee() > 0) {
                mGoodsOrderSplit.setVisibility(View.VISIBLE);
            } else {
                mGoodsOrderSplit.setVisibility(View.GONE);
            }

            if (orderCardContent.getOrderStatus() > 0) {
                mOrderStatus.setVisibility(View.VISIBLE);
                //待付款: 1   待发货: 2   运输中: 3   派送中: 4   已完成: 5   待评价: 6   已取消: 7
                String statusStr = "";
                switch (orderCardContent.getOrderStatus()) {
                    case 1:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_1");
                        break;
                    case 2:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_2");
                        break;
                    case 3:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_3");
                        break;
                    case 4:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_4");
                        break;
                    case 5:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_5");
                        break;
                    case 6:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_6");
                        break;
                    case 7:
                        statusStr = ResourceUtils.getResString(context, "sobot_order_status_7");
                        break;
                }
                mOrderStatus.setText(Html.fromHtml(ResourceUtils.getResString(context, "sobot_order_status_lable") + statusStr + "</font></b>"));
            } else {
                mOrderStatus.setVisibility(View.GONE);
            }

            if (orderCardContent.getTotalFee() > 0) {
                mGoodsTotalMoney.setVisibility(View.VISIBLE);
                mGoodsTotalMoney.setText((!TextUtils.isEmpty(orderCardContent.getGoodsCount()) ? " ," : "") + ResourceUtils.getResString(context, "sobot_order_total_money") + getMoney(orderCardContent.getTotalFee()));
            } else {
                mGoodsTotalMoney.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(orderCardContent.getGoodsCount())) {
                mGoodsCount.setVisibility(View.VISIBLE);
                mGoodsCount.setText(orderCardContent.getGoodsCount() + ResourceUtils.getResString(context, "sobot_how_goods"));
            } else {
                mGoodsCount.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(orderCardContent.getOrderCode())) {
                mOrderNumber.setText(ResourceUtils.getResString(context, "sobot_order_code_lable") + orderCardContent.getOrderCode());
                mOrderNumber.setVisibility(View.VISIBLE);
            } else {
                mOrderNumber.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(orderCardContent.getCreateTime())) {
                mOrderCreatetime.setText(ResourceUtils.getResString(context, "sobot_order_time_lable") + DateUtil.longToDateStr(Long.parseLong(orderCardContent.getCreateTime()), "yyyy-MM-dd HH:mm"));
                mOrderCreatetime.setVisibility(View.VISIBLE);
            } else {
                mOrderCreatetime.setVisibility(View.GONE);
            }


            if (isRight) {
                try {
                    msgStatus.setClickable(true);
                    if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {// 成功的状态
                        msgStatus.setVisibility(View.GONE);
                        msgProgressBar.setVisibility(View.GONE);
                    } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ERROR) {
                        msgStatus.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.GONE);
//                        msgStatus.setOnClickListener(new TextMessageHolder.ReSendTextLisenter(context, message
//                                .getId(), content, msgStatus, msgCallBack));
                    } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_LOADING) {
                        msgProgressBar.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mContainer && orderCardContent != null) {
            if (SobotOption.orderCardListener != null) {
                SobotOption.orderCardListener.onClickOrderCradMsg(orderCardContent);
                return;
            }
            if (SobotOption.hyperlinkListener != null) {
                SobotOption.hyperlinkListener.onUrlClick(orderCardContent.getOrderUrl());
                return;
            }
            if (SobotOption.newHyperlinkListener != null) {
                //如果返回true,拦截;false 不拦截
                boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(orderCardContent.getOrderUrl());
                if (isIntercept) {
                    return;
                }
            }
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", orderCardContent.getOrderUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    /**
     * 获取钱的数量
     *
     * @param money
     * @return
     */
    private String getMoney(int money) {
        if (mContext == null) {
            return "";
        }
        return String.format(ResourceUtils.getResString(mContext, "sobot_money_format"), money / 100.0f);


    }
}
