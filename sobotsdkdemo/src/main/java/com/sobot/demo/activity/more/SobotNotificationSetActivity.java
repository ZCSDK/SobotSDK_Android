package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

public class SobotNotificationSetActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_isOpenNotification;//是否开启消息提醒
    private RelativeLayout sobot_btn_open_leave_msg;//开启离线消息
    private ImageView sobot_tv_left;
    private ImageView imgOpenVoice, imgOpenVoice1;
    private boolean isOpenNotification = false, isOpenLeaveMsg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_notification_set_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initSobotView();
    }

    private void initSobotView() {
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("推送设置");
        sobot_isOpenNotification = (RelativeLayout) findViewById(R.id.sobot_isOpenNotification);
        sobot_btn_open_leave_msg = (RelativeLayout) findViewById(R.id.sobot_btn_open_leave_msg);
        sobot_isOpenNotification.setOnClickListener(this);
        sobot_btn_open_leave_msg.setOnClickListener(this);
        imgOpenVoice = (ImageView) findViewById(R.id.img_open_notify);
        imgOpenVoice1 = (ImageView) findViewById(R.id.img_open_notify1);

        getSobotConsultation();
    }

    @Override
    public void onBackPressed() {
        saveSobotConsultation();
        finish();
    }

    private void saveSobotConsultation() {
        SobotSPUtil.saveBooleanData(this, "sobot_isOpenNotification", isOpenNotification);
        SobotSPUtil.saveBooleanData(this, "sobot_isOpenLeaveMsg", isOpenLeaveMsg);
    }

    private void getSobotConsultation() {
        boolean sobot_isOpenNotification = SobotSPUtil.getBooleanData(this, "sobot_isOpenNotification", isOpenNotification);
        setOpenNotification(sobot_isOpenNotification);
        boolean sobot_isOpenLeaveMsg = SobotSPUtil.getBooleanData(this, "sobot_isOpenLeaveMsg", isOpenLeaveMsg);
        setOpenLeaveMsg(sobot_isOpenLeaveMsg);
    }

    @Override
    public void onClick(View v) {
        // 开启通道接受离线消息，开启后会将消息以广播的形式发送过来
        // 如果无需此功能那么可以不做调用
        if (v == sobot_btn_open_leave_msg) {
            isOpenNotification = !isOpenNotification;
            setOpenNotification(isOpenNotification);
        } else if (v == sobot_tv_left) {
            saveSobotConsultation();
            finish();
        } else if (v == sobot_isOpenNotification) {
            isOpenLeaveMsg = !isOpenLeaveMsg;
            setOpenLeaveMsg(isOpenLeaveMsg);
        }
    }

    private void setOpenLeaveMsg(boolean open) {
        // 开启通道接受离线消息，开启后会将消息以广播的形式发送过来
        // 如果无需此功能那么可以不做调用
        if (open) {
            isOpenLeaveMsg = true;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_open);
            SobotSPUtil.saveBooleanData(this, "sobot_isOpenLeaveMsg", isOpenLeaveMsg);
            // 开启通道接收离线消息，开启后会将消息以广播的形式发送过来
            // 如果无需此功能那么可以不做调用
            SobotApi.initSobotChannel(getApplicationContext(), SobotSPUtil.getStringData(this, "sobot_partnerId", ""));
        } else {
            isOpenLeaveMsg = false;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_close);
            SobotSPUtil.saveBooleanData(this, "sobot_isOpenLeaveMsg", isOpenLeaveMsg);
            // 关闭通道,清除当前会话缓存
            SobotApi.disSobotChannel(getApplicationContext());
        }
    }

    private void setOpenNotification(boolean open) {
        if (open) {
            isOpenNotification = true;
            imgOpenVoice1.setBackgroundResource(R.drawable.sobot_demo_icon_open);
            SobotSPUtil.saveBooleanData(this, "sobot_isOpenNotification", isOpenNotification);
        } else {
            isOpenNotification = false;
            imgOpenVoice1.setBackgroundResource(R.drawable.sobot_demo_icon_close);
            SobotSPUtil.saveBooleanData(this, "sobot_isOpenNotification", isOpenNotification);
        }
    }
}