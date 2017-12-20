package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

public class SobotIsUseVoiceSetActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout sobot_isUseVoice;
    private ImageView sobot_tv_left;
    private boolean isUseVoice = false;//是否显示咨询信息，默认不显示
    private ImageView imgOpenVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_isuse_voice_set_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initSobotView();
    }

    private void initSobotView() {
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("是否开启语音功能设置");

        sobot_isUseVoice = (RelativeLayout) findViewById(R.id.sobot_isUseVoice);
        imgOpenVoice = (ImageView) findViewById(R.id.img_open_notify);
        sobot_isUseVoice.setOnClickListener(this);

        getSobotConsultation();
    }

    @Override
    public void onBackPressed() {
        saveSobotConsultation();
        finish();
    }

    private void saveSobotConsultation() {
        SobotSPUtil.saveBooleanData(this, "sobot_isUseVoice", isUseVoice);
    }

    private void getSobotConsultation() {
        boolean sobot_bool_isUseVoice = SobotSPUtil.getBooleanData(this, "sobot_isUseVoice", false);
        setVoice(sobot_bool_isUseVoice);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left){
            saveSobotConsultation();
            finish();
        } else if (v == sobot_isUseVoice) {
            isUseVoice = !isUseVoice;
            setVoice(isUseVoice);
        }
    }

    private void setVoice(boolean open){
        if(open){
            isUseVoice = true;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_open);
            SobotSPUtil.saveBooleanData(this, "sobot_isUseVoice", isUseVoice);
        }else{
            isUseVoice = false;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_close);
            SobotSPUtil.saveBooleanData(this, "sobot_isUseVoice", isUseVoice);
        }
    }
}