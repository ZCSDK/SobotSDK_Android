package com.sobot.demo.activity.more;

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
 * Created by Administrator on 2017/11/21.
 */

public class SobotReceptionistIdSetActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText sobot_receptionistId;//指定转入的人工客服id
    private RelativeLayout sobot_receptionistId_must;//是否必须转入指定客服
    private ImageView sobot_tv_left;
    private boolean isReceptionistIdMust = false;
    private ImageView img_open_notify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_receptionistid_set_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews(){
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("对接客服设置");
        sobot_receptionistId = (EditText) findViewById(R.id.sobot_receptionistId);
        sobot_receptionistId_must = (RelativeLayout) findViewById(R.id.sobot_receptionistId_must);
        img_open_notify = (ImageView) findViewById(R.id.img_open_notify);
        sobot_receptionistId_must.setOnClickListener(this);

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        String sobot_value_receptionistId = SobotSPUtil.getStringData(this, "sobot_receptionistId", "");
        if (!TextUtils.isEmpty(sobot_value_receptionistId)) {
            sobot_receptionistId.setText(sobot_value_receptionistId);
        }

        boolean sobot_bool_receptionistId_must = SobotSPUtil.getBooleanData(this, "sobot_receptionistId_must", false);
        setReceptionistIdMust(sobot_bool_receptionistId_must);
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveStringData(this, "sobot_receptionistId", sobot_receptionistId.getText().toString());
        SobotSPUtil.saveBooleanData(this, "sobot_receptionistId_must", isReceptionistIdMust);
    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left){
            saveSobotStartSet();
            finish();
        } else if (v == sobot_receptionistId_must){
            isReceptionistIdMust = !isReceptionistIdMust;
            setReceptionistIdMust(isReceptionistIdMust);
        }
    }

    private void setReceptionistIdMust(boolean isReceptionistMust){
        if (isReceptionistMust){
            isReceptionistIdMust = true;
            img_open_notify.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isReceptionistIdMust = false;
            img_open_notify.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }
}