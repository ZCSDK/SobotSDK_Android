package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SobotDemoInitModeTypeActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioGroup sobot_rg_initModeType;//设置SDK客服模式。
    private RadioButton rg_initModeType_noSet;//不设置(-1) 后台设置优先
    private RadioButton rg_initModeType_only_robot;//1  仅机器人
    private RadioButton rg_initModeType_only_customer;//2  仅人工
    private RadioButton rg_initModeType_robot_first;//3  机器人优先
    private RadioButton rg_initModeType_custom_first;//4  人工优先
    private String sobot_bool_rg_initModeType = "-1";//客服模式默认值
    private ImageView sobot_tv_left;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_demo_init_mode_type_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initViews();
    }

    private void initViews(){
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("接入模式设置");
        sobot_rg_initModeType = (RadioGroup) findViewById(R.id.sobot_rg_initModeType);
        rg_initModeType_noSet = (RadioButton) findViewById(R.id.rg_initModeType_noSet);
        rg_initModeType_only_robot = (RadioButton) findViewById(R.id.rg_initModeType_only_robot);
        rg_initModeType_only_customer = (RadioButton) findViewById(R.id.rg_initModeType_only_customer);
        rg_initModeType_robot_first = (RadioButton) findViewById(R.id.rg_initModeType_robot_first);
        rg_initModeType_custom_first = (RadioButton) findViewById(R.id.rg_initModeType_custom_first);

        getSobotStartSet();
    }

    private void getSobotStartSet(){
        sobot_bool_rg_initModeType = SobotSPUtil.getStringData(this, "sobot_rg_initModeType", sobot_bool_rg_initModeType + "");
        setRadioBtnCheckedType(sobot_bool_rg_initModeType);
    }

    private void setRadioBtnCheckedType(String type){
        switch (type){
            case "1":
                rg_initModeType_only_robot.setChecked(true);
                break;
            case "2":
                rg_initModeType_only_customer.setChecked(true);
                break;
            case "3":
                rg_initModeType_robot_first.setChecked(true);
                break;
            case "4":
                rg_initModeType_custom_first.setChecked(true);
                break;
            default:
                rg_initModeType_noSet.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }

    private void saveSobotStartSet(){
        SobotSPUtil.saveStringData(this, "sobot_rg_initModeType", getInitModeType() + "");
    }

    private int getInitModeType(){
        int resutlt = -1;
        int id = sobot_rg_initModeType.getCheckedRadioButtonId();
        switch (id){
            case R.id.rg_initModeType_noSet:
                resutlt = -1;
                break;
            case R.id.rg_initModeType_only_robot:
                resutlt = ZhiChiConstant.type_robot_only;
                break;
            case R.id.rg_initModeType_only_customer:
                resutlt = ZhiChiConstant.type_custom_only;
                break;
            case R.id.rg_initModeType_robot_first:
                resutlt = ZhiChiConstant.type_robot_first;
                break;
            case R.id.rg_initModeType_custom_first:
                resutlt = ZhiChiConstant.type_custom_first;
                break;
        }
        return resutlt;
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left){
            saveSobotStartSet();
            finish();
        }
    }
}