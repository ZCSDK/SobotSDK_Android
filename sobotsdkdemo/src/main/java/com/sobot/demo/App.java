package com.sobot.demo;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.listener.SobotPlusMenuListener;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;

import java.util.ArrayList;

/**
 *
 * Created by Administrator on 2017/12/29.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String appkey = SobotSPUtil.getStringData(this, "sobot_appkey", "");
        if (TextUtils.isEmpty(appkey)) {
            appkey = "07a5ff1050c047c4b9d3c57eeb7ced29";
        }
        SobotApi.initSobotSDK(this, appkey, SobotSPUtil.getStringData(this, "sobot_partnerId", ""));
//        initUi();
//        customMenu();
    }

    //自定义UI
    private void initUi() {
        SobotUIConfig.sobot_serviceImgId = R.drawable.sobot_failed_normal;
        SobotUIConfig.sobot_titleTextColor = R.color.sobot_color_evaluate_text_btn;
        SobotUIConfig.sobot_moreBtnImgId  = R.drawable.sobot_btn_back_selector;
        SobotUIConfig.sobot_titleBgColor = R.color.sobot_title_category_unselect_color;
        SobotUIConfig.sobot_chat_bottom_bgColor = R.color.sobot_text_delete_hismsg_color;

        SobotUIConfig.sobot_chat_left_textColor = R.color.sobot_text_delete_hismsg_color;
        SobotUIConfig.sobot_chat_left_link_textColor = R.color.sobot_title_category_unselect_color;
        SobotUIConfig.sobot_chat_left_bgColor = R.color.sobot_color_suggestion_history;

        SobotUIConfig.sobot_chat_right_bgColor = R.color.sobot_title_category_unselect_color;
        SobotUIConfig.sobot_chat_right_link_textColor = R.color.sobot_viewpagerbackground;
        SobotUIConfig.sobot_chat_right_textColor = R.color.sobot_text_delete_hismsg_color;
    }

    private void customMenu(){
        ArrayList<ChattingPanelUploadView.SobotPlusEntity> objects = new ArrayList<>();
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "位置", "action_location"));
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "签到", "action_sing_in"));
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "收藏", "action_ollection"));

        SobotUIConfig.pulsMenu.menus = objects;
        SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
            @Override
            public void onClick(View view, String action) {
                ToastUtil.showToast(getApplicationContext(), "action:"+action);
            }
        };
    }
}