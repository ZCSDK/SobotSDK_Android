package com.sobot.demo;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.chat.listener.SobotPlusMenuListener;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String appkey = SobotSPUtil.getStringData(this, "sobot_appkey", "");
        appkey="dd0e991266a94723af14cc23cda5e1f1";
        SobotBaseUrl.setHost("https://test.sobot.com");
        if (TextUtils.isEmpty(appkey)) {
            appkey = "991bcba6975246448640724385796b81";
        }
        SobotApi.initSobotSDK(this, appkey, SobotSPUtil.getStringData(this, "sobot_partnerId", ""));
        initUi();
//        customMenu();
        initLocationModule();
    }

    //自定义UI
    private void initUi() {
//        SobotUIConfig.sobot_serviceImgId = R.drawable.sobot_failed_normal;
//        SobotUIConfig.sobot_titleTextColor = R.color.sobot_color_evaluate_text_btn;
//        SobotUIConfig.sobot_moreBtnImgId  = R.drawable.sobot_btn_back_selector;
//        SobotUIConfig.sobot_titleBgColor = R.color.sobot_title_category_unselect_color;
//        SobotUIConfig.sobot_chat_bottom_bgColor = R.color.sobot_text_delete_hismsg_color;
//
//        SobotUIConfig.sobot_chat_left_textColor = R.color.sobot_text_delete_hismsg_color;
//        SobotUIConfig.sobot_chat_left_link_textColor = R.color.sobot_title_category_unselect_color;
//        SobotUIConfig.sobot_chat_left_bgColor = R.color.sobot_color_suggestion_history;
//
//        SobotUIConfig.sobot_chat_right_bgColor = R.color.sobot_title_category_unselect_color;
//        SobotUIConfig.sobot_chat_right_link_textColor = R.color.sobot_viewpagerbackground;
//        SobotUIConfig.sobot_chat_right_textColor = R.color.sobot_text_delete_hismsg_color;
//        SobotUIConfig.sobot_title_right_menu2_display = true;
//        SobotUIConfig.sobot_title_right_menu2_bg = R.drawable.sobot_failed_normal;
    }

    private void customMenu() {
        ArrayList<ChattingPanelUploadView.SobotPlusEntity> objects = new ArrayList<>();
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "位置", "action_location"));
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "签到", "action_sing_in"));
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "收藏", "action_ollection"));

        SobotUIConfig.pulsMenu.menus = objects;
        SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
            @Override
            public void onClick(View view, String action) {
                ToastUtil.showToast(getApplicationContext(), "action:" + action);
            }
        };
    }

    //设置位置功能
    private void initLocationModule() {
        final String ACTION_LOCATION = "sobot_action_location";
        //拷贝assets中地图图片 到sdcard  demo的临时操作，实际使用时  需动态传入真实的图片地址
        IOUtils.copyAssetAndWrite(getApplicationContext(), "tmp_pic.jpg");
        //位置
        ChattingPanelUploadView.SobotPlusEntity locationEntity = new ChattingPanelUploadView.SobotPlusEntity(ResourceUtils.getDrawableId(getApplicationContext(), "sobot_location_btn_selector"), ResourceUtils.getResString(getApplicationContext(), "sobot_location"), ACTION_LOCATION);
        List<ChattingPanelUploadView.SobotPlusEntity> tmpList = new ArrayList<>();
        tmpList.add(locationEntity);
        SobotUIConfig.pulsMenu.operatorMenus = tmpList;
        SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
            @Override
            public void onClick(View view, String action) {
                if (ACTION_LOCATION.equals(action)) {
                    Context context = view.getContext();
                    SobotLocationModel locationData = new SobotLocationModel();
                    //必须传入本地图片地址
                    locationData.setSnapshot(context.getCacheDir() + File.separator + "tmp_pic.jpg");
                    locationData.setLat("40.001630");
                    locationData.setLng("116.353313");
                    locationData.setLocalName("云景四季餐厅");
                    locationData.setLocalLabel("学清路38号金码大厦A座23层");

                    SobotApi.sendLocation(context, locationData);

//					view.getContext().startActivity(new Intent(view.getContext(),));
                }
            }
        };
    }


}