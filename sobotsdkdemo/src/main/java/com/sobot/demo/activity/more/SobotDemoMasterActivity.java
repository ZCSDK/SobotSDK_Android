package com.sobot.demo.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 * Created by Administrator on 2017/11/20.
 */

public class SobotDemoMasterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText sobot_appkey;//appkey
    private EditText sobot_partnerId;//partnerId唯一标识用户
    private EditText ed_hot_question;;//
    private RelativeLayout rl_;
    private ImageView sobot_tv_left;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_demo_master_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        inifViews();
    }

    private void inifViews() {
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("基本设置");
        sobot_appkey = (EditText) findViewById(R.id.sobot_demo_appkey);
        sobot_partnerId = (EditText) findViewById(R.id.sobot_partnerId);
        ed_hot_question = (EditText) findViewById(R.id.ed_hot_question);
        rl_ = (RelativeLayout) findViewById(R.id.rl_);
        rl_.setOnClickListener(this);
        getSobotStartSet();
    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveStringData(this, "sobot_appkey", sobot_appkey.getText().toString());
        SobotSPUtil.saveStringData(this, "sobot_partnerId", sobot_partnerId.getText().toString());
        SobotSPUtil.saveStringData(this, "ed_hot_question_value", ed_hot_question.getText().toString());
    }

    private void getSobotStartSet() {
        String sobot_value_appkey = SobotSPUtil.getStringData(this, "sobot_appkey", "");
        if (!TextUtils.isEmpty(sobot_value_appkey)) {
            sobot_appkey.setText(sobot_value_appkey);
        }
        String sobot_value_partnerId = SobotSPUtil.getStringData(this, "sobot_partnerId", "");
        if (!TextUtils.isEmpty(sobot_value_partnerId)) {
            sobot_partnerId.setText(sobot_value_partnerId);
        }
        String ed_hot_question_value = SobotSPUtil.getStringData(this, "ed_hot_question_value", "");
        if (!TextUtils.isEmpty(ed_hot_question_value)) {
            ed_hot_question.setText(ed_hot_question_value);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == rl_) {//用户资料设置
            Intent intent = new Intent(this, SobotPersonSetActivity.class);
            startActivity(intent);
        } else if (v == sobot_tv_left) {
            saveSobotStartSet();
            finish();
        }
    }
}