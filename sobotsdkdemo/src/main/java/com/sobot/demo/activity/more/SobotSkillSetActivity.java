package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SobotSkillSetActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText sobot_demo_skillname;//技能组名称
    private EditText sobot_demo_skillid;//技能组id
    private ImageView sobot_tv_left;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_skill_set_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews(){
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("对接技能组设置");
        sobot_demo_skillname = (EditText) findViewById(R.id.sobot_demo_skillname);
        sobot_demo_skillid = (EditText) findViewById(R.id.sobot_demo_skillid);

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        String sobot_demo_skillname_value = SobotSPUtil.getStringData(this, "sobot_demo_skillname", "");
        if (!TextUtils.isEmpty(sobot_demo_skillname_value)) {
            sobot_demo_skillname.setText(sobot_demo_skillname_value);
        }

        String sobot_demo_skillid_value = SobotSPUtil.getStringData(this, "sobot_demo_skillid", "");
        if (!TextUtils.isEmpty(sobot_demo_skillid_value)) {
            sobot_demo_skillid.setText(sobot_demo_skillid_value);
        }
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveStringData(this,"sobot_demo_skillname",sobot_demo_skillname.getText().toString());
        SobotSPUtil.saveStringData(this,"sobot_demo_skillid",sobot_demo_skillid.getText().toString());
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
        }
    }
}