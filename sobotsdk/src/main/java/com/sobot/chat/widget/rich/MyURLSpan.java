package com.sobot.chat.widget.rich;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.SobotOption;

public class MyURLSpan extends URLSpan {

    private Context context;
    private int color;
    private boolean isShowLine;// 下划线

    public MyURLSpan(Context context, String url, int color) {
        this(context, url, color, false);
    }

    public MyURLSpan(Context context, String url, int color, boolean isShowLine) {
        super(url);
        this.context = context;
        this.color = context.getResources().getColor(color);
        this.isShowLine = isShowLine;
    }


    @Override
    public void onClick(View widget) {
        String url = getURL();
//        LogUtils.i("url:" + url);
        if (url.startsWith("sobot:")) {
            //不是超链接  而是自己内部的东西  例如留言
            if ("sobot:SobotPostMsgActivity".equals(url)) {
                Intent intent = new Intent();
                intent.setAction(ZhiChiConstants.chat_remind_post_msg);
                CommonUtils.sendLocalBroadcast(context, intent);
            } else if ("sobot:SobotTicketInfo".equals(url)) {
                Intent intent = new Intent();
                intent.putExtra("isShowTicket", true);
                intent.setAction(ZhiChiConstants.chat_remind_post_msg);
                CommonUtils.sendLocalBroadcast(context, intent);
            }
        } else {
            if (url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls")
                    || url.endsWith(".txt") || url.endsWith(".ppt") || url.endsWith(".pptx")
                    || url.endsWith(".xlsx") || url.endsWith(".pdf") || url.endsWith(".rar")
                    || url.endsWith(".zip")) {// 内部浏览器不支持，所以打开外部
                if (SobotOption.hyperlinkListener != null) {
                    SobotOption.hyperlinkListener.onUrlClick(url);
                    return;
                }
                if (SobotOption.newHyperlinkListener != null) {
                    //如果返回true,拦截;false 不拦截
                    boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(url);
                    if (isIntercept) {
                        return;
                    }

                }
                url = fixUrl(url);
                // 外部浏览器
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri content = Uri.parse(url);
                intent.setData(content);
                context.startActivity(intent);
            } else if (url.startsWith("tel:")) {
                if (SobotOption.hyperlinkListener != null) {
                    SobotOption.hyperlinkListener.onPhoneClick(url);
                    return;
                }
                if (SobotOption.newHyperlinkListener != null) {
                    boolean isIntercept = SobotOption.newHyperlinkListener.onPhoneClick(url);
                    if (isIntercept) {
                        return;
                    }
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));// mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                context.startActivity(intent);
            } else {
                // 内部浏览器
                if (SobotOption.hyperlinkListener != null) {
                    SobotOption.hyperlinkListener.onUrlClick(url);
                    return;
                }
                if (SobotOption.newHyperlinkListener != null) {
                    //如果返回true,拦截;false 不拦截
                    boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(url);
                    if (isIntercept) {
                        return;
                    }
                }
                url = fixUrl(url);
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    private String fixUrl(String url) {
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = "https://" + url;
            LogUtils.i("url:" + url);
        }
        return url;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(isShowLine);
    }
}