package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;
import com.sobot.demo.SettingItemView;

public class SobotPersonSetActivity extends AppCompatActivity implements View.OnClickListener {

    private SettingItemView sobot_person_tel,
            sobot_person_uName,
            sobot_person_email,
            sobot_person_realName,
            sobot_person_qq,
            sobot_person_face,
            sobot_person_reMark,
            sobot_person_visitTitle,
            sobot_person_visitUrl,
            sobot_person_key1,
            sobot_person_key2;
    private ImageView sobot_tv_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_person_set_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initSobotView();
    }

    private void initSobotView() {
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("用户信息设置");
        sobot_person_tel = (SettingItemView) findViewById(R.id.sobot_person_tel);
        sobot_person_uName = (SettingItemView) findViewById(R.id.sobot_person_uName);
        sobot_person_email = (SettingItemView) findViewById(R.id.sobot_person_email);
        sobot_person_realName = (SettingItemView) findViewById(R.id.sobot_person_realName);
        sobot_person_qq = (SettingItemView) findViewById(R.id.sobot_person_qq);
        sobot_person_face = (SettingItemView) findViewById(R.id.sobot_person_face);
        sobot_person_reMark = (SettingItemView) findViewById(R.id.sobot_person_reMark);
        sobot_person_visitTitle = (SettingItemView) findViewById(R.id.sobot_person_visitTitle);
        sobot_person_visitUrl = (SettingItemView) findViewById(R.id.sobot_person_visitUrl);
        sobot_person_key1 = (SettingItemView) findViewById(R.id.sobot_person_key1);
        sobot_person_key2 = (SettingItemView) findViewById(R.id.sobot_person_key2);

        getPersonInfo();
    }

    @Override
    public void onBackPressed() {
        savePersonInfo();
        finish();
    }

    private void savePersonInfo() {
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_tel", sobot_person_tel.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "person_uName", sobot_person_uName.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_email", sobot_person_email.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_realname", sobot_person_realName.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_qq", sobot_person_qq.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_face", sobot_person_face.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_reMark", sobot_person_reMark.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_visitTitle", sobot_person_visitTitle.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_visitUrl", sobot_person_visitUrl.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_key1", sobot_person_key1.getTextByTrim());
        SobotSPUtil.saveStringData(SobotPersonSetActivity.this, "sobot_key2", sobot_person_key2.getTextByTrim());
    }

    private void getPersonInfo() {
        String person_tel = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_tel", "");
        if (!TextUtils.isEmpty(person_tel)) {
            sobot_person_tel.setText(person_tel);
        }
        String person_uName = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "person_uName", "");
        if (!TextUtils.isEmpty(person_uName)) {
            sobot_person_uName.setText(person_uName);
        }
        String person_email = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_email", "");
        if (!TextUtils.isEmpty(person_email)) {
            sobot_person_email.setText(person_email);
        }
        String person_realName = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_realname", "");
        if (!TextUtils.isEmpty(person_realName)) {
            sobot_person_realName.setText(person_realName);
        }
        String person_qq = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_qq", "");
        if (!TextUtils.isEmpty(person_qq)) {
            sobot_person_qq.setText(person_qq);
        }

        String person_face = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_face", "");
        if (!TextUtils.isEmpty(person_face)) {
            sobot_person_face.setText(person_face);
        }
        String person_reMark = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_reMark", "");
        if (!TextUtils.isEmpty(person_reMark)) {
            sobot_person_reMark.setText(person_reMark);
        }
        String person_visitTitle = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_visitTitle", "");
        if (!TextUtils.isEmpty(person_visitTitle)) {
            sobot_person_visitTitle.setText(person_visitTitle);
        }
        String person_visitUrl = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_visitUrl", "");
        if (!TextUtils.isEmpty(person_visitUrl)) {
            sobot_person_visitUrl.setText(person_visitUrl);
        }
        String person_key1 = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_key1", "");
        if (!TextUtils.isEmpty(person_key1)) {
            sobot_person_key1.setText(person_key1);
        }
        String person_key2 = SobotSPUtil.getStringData(SobotPersonSetActivity.this, "sobot_key2", "");
        if (!TextUtils.isEmpty(person_key2)) {
            sobot_person_key2.setText(person_key2);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            savePersonInfo();
            finish();
        }
    }
}