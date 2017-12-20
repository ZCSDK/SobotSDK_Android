package com.sobot.demo.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.R;

public class SobotAccessSetActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_access_set_activity);
        findvViews();
    }

    private void findvViews() {
        RelativeLayout rl_1 = (RelativeLayout) findViewById(R.id.rl_1);
        RelativeLayout rl_2 = (RelativeLayout) findViewById(R.id.rl_2);
        RelativeLayout rl_3 = (RelativeLayout) findViewById(R.id.rl_3);
        RelativeLayout rl_4 = (RelativeLayout) findViewById(R.id.rl_4);
        RelativeLayout rl_5 = (RelativeLayout) findViewById(R.id.rl_5);
        RelativeLayout rl_6 = (RelativeLayout) findViewById(R.id.rl_6);
        RelativeLayout rl_7 = (RelativeLayout) findViewById(R.id.rl_7);
        ImageView sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("接入信息设置");
        sobot_tv_left.setOnClickListener(this);

        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        rl_3.setOnClickListener(this);
        rl_4.setOnClickListener(this);
        rl_5.setOnClickListener(this);
        rl_6.setOnClickListener(this);
        rl_7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_1://接入模式
                intent = new Intent(this, SobotDemoInitModeTypeActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_2://商品信息设置
                intent = new Intent(this, SobotConsultationSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_3://自动应答语设置
                intent = new Intent(this, SobotCustomReplyActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_4://转人工设置
                intent = new Intent(this, SobotTransferCustomActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_5://历史记录的时间范围设置
                intent = new Intent(this, SobotHistoryRulerActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_6://是否开启语音功能设置
                intent = new Intent(this, SobotIsUseVoiceSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_7://自定义标题设置
                intent = new Intent(this, SobotCustomTitleActivity.class);
                startActivity(intent);
                break;
            case R.id.sobot_demo_tv_left:
                finish();
                break;
        }
    }
}