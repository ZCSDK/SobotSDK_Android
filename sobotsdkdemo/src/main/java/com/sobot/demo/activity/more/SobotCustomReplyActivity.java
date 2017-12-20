package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 *  自定义应答设置
 * Created by Administrator on 2017/5/24.
 */

public class SobotCustomReplyActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText sobot_customadminhelloword;
    private EditText sobot_customrobothelloword;
    private EditText sobot_customusertipword;
    private EditText sobot_customadmintipword;
    private EditText sobot_customadminnonelinetitle;
    private EditText sobot_customuseroutword;
    private ImageView sobot_tv_left;
    private TextView sobot_text_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_custom_reply_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initView();
    }

    private void initView(){
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("自定义应答语设置");
        sobot_customadminhelloword = (EditText) findViewById(R.id.sobot_customadminhelloword);
        sobot_customrobothelloword = (EditText) findViewById(R.id.sobot_customrobothelloword);
        sobot_customusertipword = (EditText) findViewById(R.id.sobot_customusertipword);
        sobot_customadmintipword = (EditText) findViewById(R.id.sobot_customadmintipword);
        sobot_customadminnonelinetitle = (EditText) findViewById(R.id.sobot_customadminnonelinetitle);
        sobot_customuseroutword = (EditText) findViewById(R.id.sobot_customuseroutword);

        getCustomReply();
    }

    @Override
    public void onBackPressed() {
        saveCustomReply();
        finish();
    }

    private void saveCustomReply(){
        SobotSPUtil.saveStringData(SobotCustomReplyActivity.this,"sobot_customAdminHelloWord",sobot_customadminhelloword.getText().toString());
        SobotSPUtil.saveStringData(SobotCustomReplyActivity.this,"sobot_customRobotHelloWord",sobot_customrobothelloword.getText().toString());
        SobotSPUtil.saveStringData(SobotCustomReplyActivity.this,"sobot_customUserTipWord",sobot_customusertipword.getText().toString());
        SobotSPUtil.saveStringData(SobotCustomReplyActivity.this,"sobot_customAdminTipWord",sobot_customadmintipword.getText().toString());
        SobotSPUtil.saveStringData(SobotCustomReplyActivity.this,"sobot_customAdminNonelineTitle",sobot_customadminnonelinetitle.getText().toString());
        SobotSPUtil.saveStringData(SobotCustomReplyActivity.this,"sobot_customUserOutWord",sobot_customuseroutword.getText().toString());
    }

    private void getCustomReply(){
        String sobot_customadmin_helloword = SobotSPUtil.getStringData(SobotCustomReplyActivity.this,"sobot_customAdminHelloWord","");
        if (!TextUtils.isEmpty(sobot_customadmin_helloword)){
            sobot_customadminhelloword.setText(sobot_customadmin_helloword);
        }

        String sobot_customrobot_helloword = SobotSPUtil.getStringData(SobotCustomReplyActivity.this,"sobot_customRobotHelloWord","");
        if (!TextUtils.isEmpty(sobot_customrobot_helloword)){
            sobot_customrobothelloword.setText(sobot_customrobot_helloword);
        }

        String sobot_customuser_tipword = SobotSPUtil.getStringData(SobotCustomReplyActivity.this,"sobot_customUserTipWord","");
        if (!TextUtils.isEmpty(sobot_customuser_tipword)){
            sobot_customusertipword.setText(sobot_customuser_tipword);
        }

        String sobot_customadmin_tipword = SobotSPUtil.getStringData(SobotCustomReplyActivity.this,"sobot_customAdminTipWord","");
        if (!TextUtils.isEmpty(sobot_customadmin_tipword)){
            sobot_customadmintipword.setText(sobot_customadmin_tipword);
        }

        String sobot_customadmin_nonelinetitle = SobotSPUtil.getStringData(SobotCustomReplyActivity.this,"sobot_customAdminNonelineTitle","");
        if (!TextUtils.isEmpty(sobot_customadmin_nonelinetitle)){
            sobot_customadminnonelinetitle.setText(sobot_customadmin_nonelinetitle);
        }

        String sobot_customuser_outword = SobotSPUtil.getStringData(SobotCustomReplyActivity.this,"sobot_customUserOutWord","");
        if (!TextUtils.isEmpty(sobot_customuser_outword)){
            sobot_customuseroutword.setText(sobot_customuser_outword);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left){
            saveCustomReply();
            finish();
        }
    }
}