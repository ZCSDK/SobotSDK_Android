package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

public class SobotConsultationSetActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_goods_is_show_info;//是否显示咨询信息设置
    private EditText sobot_goods_title;//标题
    private EditText sobot_goods_describe;//摘要
    private EditText sobot_goods_lable;//标签
    private EditText sobot_goods_imgUrl;//图片Url 必须为有效连接才可以显示这张图片
    private EditText sobot_goods_fromUrl;//发送内容
    private boolean isShow = false;//是否显示咨询信息，默认不显示
    private ImageView sobot_tv_left, imgOpenVoice;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_consultation_set_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initSobotView();
    }

    private void initSobotView() {
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left.setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("商品信息设置");
        sobot_goods_is_show_info = (RelativeLayout) findViewById(R.id.sobot_goods_is_show_info);
        sobot_goods_title = (EditText) findViewById(R.id.sobot_goods_title);
        sobot_goods_describe = (EditText) findViewById(R.id.sobot_goods_describe);
        sobot_goods_lable = (EditText) findViewById(R.id.sobot_goods_lable);
        sobot_goods_imgUrl = (EditText) findViewById(R.id.sobot_goods_imgUrl);
        sobot_goods_fromUrl = (EditText) findViewById(R.id.sobot_goods_fromUrl);
        imgOpenVoice = (ImageView) findViewById(R.id.img_open_notify);
        sobot_goods_is_show_info.setOnClickListener(this);

        getSobotConsultation();
    }

    private void setSobotView(boolean checked) {
        if (checked) {
            sobot_goods_title.setVisibility(View.VISIBLE);
            sobot_goods_describe.setVisibility(View.VISIBLE);
            sobot_goods_lable.setVisibility(View.VISIBLE);
            sobot_goods_imgUrl.setVisibility(View.VISIBLE);
            sobot_goods_fromUrl.setVisibility(View.VISIBLE);
        } else {
            sobot_goods_title.setVisibility(View.INVISIBLE);
            sobot_goods_describe.setVisibility(View.INVISIBLE);
            sobot_goods_lable.setVisibility(View.INVISIBLE);
            sobot_goods_imgUrl.setVisibility(View.INVISIBLE);
            sobot_goods_fromUrl.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        saveSobotConsultation();
        finish();
    }

    private void saveSobotConsultation() {
        SobotSPUtil.saveStringData(SobotConsultationSetActivity.this, "sobot_goods_title_value", sobot_goods_title.getText().toString());
        SobotSPUtil.saveStringData(SobotConsultationSetActivity.this, "sobot_goods_describe_value", sobot_goods_describe.getText().toString());
        SobotSPUtil.saveStringData(SobotConsultationSetActivity.this, "sobot_goods_lable_value", sobot_goods_lable.getText().toString());
        SobotSPUtil.saveStringData(SobotConsultationSetActivity.this, "sobot_goods_imgUrl_value", sobot_goods_imgUrl.getText().toString());
        SobotSPUtil.saveStringData(SobotConsultationSetActivity.this, "sobot_goods_fromUrl_value", sobot_goods_fromUrl.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            saveSobotConsultation();
            finish();
        } else if (v == sobot_goods_is_show_info) {
            isShow = !isShow;
            setVoice(isShow);
        }
    }

    //是否显示商品信息
    private void setVoice(boolean open){
        if(open){
            isShow = true;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_open);
            SobotSPUtil.saveBooleanData(this, "sobot_goods_is_show_info", isShow);
            setSobotView(isShow);
        }else{
            isShow = false;
            setSobotView(isShow);
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_close);
            SobotSPUtil.saveBooleanData(this, "sobot_goods_is_show_info", isShow);
        }
    }

    private void getSobotConsultation() {
        isShow = SobotSPUtil.getBooleanData(SobotConsultationSetActivity.this, "sobot_goods_is_show_info", false);
        setVoice(isShow);
        if (isShow) {
            sobot_goods_title.setVisibility(View.VISIBLE);
            sobot_goods_describe.setVisibility(View.VISIBLE);
            sobot_goods_lable.setVisibility(View.VISIBLE);
            sobot_goods_imgUrl.setVisibility(View.VISIBLE);
            sobot_goods_fromUrl.setVisibility(View.VISIBLE);
        } else {
            sobot_goods_title.setVisibility(View.INVISIBLE);
            sobot_goods_describe.setVisibility(View.INVISIBLE);
            sobot_goods_lable.setVisibility(View.INVISIBLE);
            sobot_goods_imgUrl.setVisibility(View.INVISIBLE);
            sobot_goods_fromUrl.setVisibility(View.INVISIBLE);
        }

        String sobot_goods_title_value = SobotSPUtil.getStringData(SobotConsultationSetActivity.this, "sobot_goods_title_value", "");
        if (!TextUtils.isEmpty(sobot_goods_title_value)) {
            sobot_goods_title.setText(sobot_goods_title_value);
        }
        String sobot_goods_describe_value = SobotSPUtil.getStringData(SobotConsultationSetActivity.this, "sobot_goods_describe_value", "");
        if (!TextUtils.isEmpty(sobot_goods_describe_value)) {
            sobot_goods_describe.setText(sobot_goods_describe_value);
        }
        String sobot_goods_lable_value = SobotSPUtil.getStringData(SobotConsultationSetActivity.this, "sobot_goods_lable_value", "");
        if (!TextUtils.isEmpty(sobot_goods_lable_value)) {
            sobot_goods_lable.setText(sobot_goods_lable_value);
        }
        String sobot_goods_imgUrl_value = SobotSPUtil.getStringData(SobotConsultationSetActivity.this, "sobot_goods_imgUrl_value", "");
        if (!TextUtils.isEmpty(sobot_goods_imgUrl_value)) {
            sobot_goods_imgUrl.setText(sobot_goods_imgUrl_value);
        }
        String sobot_goods_fromUrl_value = SobotSPUtil.getStringData(SobotConsultationSetActivity.this, "sobot_goods_fromUrl_value", "");
        if (!TextUtils.isEmpty(sobot_goods_fromUrl_value)) {
            sobot_goods_fromUrl.setText(sobot_goods_fromUrl_value);
        }
    }
}