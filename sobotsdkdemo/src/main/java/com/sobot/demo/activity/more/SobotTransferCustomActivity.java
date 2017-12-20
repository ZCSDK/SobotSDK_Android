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

public class SobotTransferCustomActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText sobot_isArtificialIntelligence_num;//如果是只能转人工，需要设置当应到问题出现几次以后显示转人工按钮
    private RelativeLayout sobot_isArtificialIntelligence;//是否智能转人工,如果是，需要设置setArtificialIntelligenceNum为大于等于1的数字，默认是1
    private ImageView sobot_tv_left;
    private boolean isUseVoice = false;//是否显示咨询信息，默认不显示
    private ImageView imgOpenVoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_transfer_custom_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews(){
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("转人工设置");
        sobot_isArtificialIntelligence_num = (EditText) findViewById(R.id.sobot_isArtificialIntelligence_num);
        sobot_isArtificialIntelligence = (RelativeLayout) findViewById(R.id.sobot_isArtificialIntelligence);
        imgOpenVoice = (ImageView) findViewById(R.id.img_open_notify);
        sobot_isArtificialIntelligence.setOnClickListener(this);

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        String sobot_isArtificialIntelligence_num_value = SobotSPUtil.getStringData(this, "sobot_isArtificialIntelligence_num_value", "");
        if (!TextUtils.isEmpty(sobot_isArtificialIntelligence_num_value)) {
            sobot_isArtificialIntelligence_num.setText(sobot_isArtificialIntelligence_num_value);
        }

        boolean sobot_bool_isArtificialIntelligence = SobotSPUtil.getBooleanData(this, "sobot_isArtificialIntelligence", false);
        setVoice(sobot_bool_isArtificialIntelligence);
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveBooleanData(this, "sobot_isArtificialIntelligence", isUseVoice);
        SobotSPUtil.saveStringData(this,"sobot_isArtificialIntelligence_num_value",sobot_isArtificialIntelligence_num.getText().toString());
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
        } else if (v == sobot_isArtificialIntelligence){
            isUseVoice = !isUseVoice;
            setVoice(isUseVoice);
        }
    }

    private void setVoice(boolean open){
        if(open){
            isUseVoice = true;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_open);
            SobotSPUtil.saveBooleanData(this, "sobot_isArtificialIntelligence", isUseVoice);
        }else{
            isUseVoice = false;
            imgOpenVoice.setBackgroundResource(R.drawable.sobot_demo_icon_close);
            SobotSPUtil.saveBooleanData(this, "sobot_isArtificialIntelligence", isUseVoice);
        }
    }
}