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

public class SobotHistoryRulerActivity extends AppCompatActivity implements View.OnClickListener{

    private String sobot_show_history = "0";//显示历史记录默认时间段
    private EditText sobot_show_history_ruler;//历史记录的显示规则
    private ImageView sobot_tv_left;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_history_ruler_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews(){
        sobot_show_history_ruler = (EditText) findViewById(R.id.sobot_show_history_ruler);
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("历史记录的时间范围设置");

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        sobot_show_history = SobotSPUtil.getStringData(this,"sobot_show_history_ruler",sobot_show_history);
        if (!TextUtils.isEmpty(sobot_show_history) && !"0".equals(sobot_show_history)){
            sobot_show_history_ruler.setText(sobot_show_history);
        } else {
            sobot_show_history_ruler.setHint("历史记录显示时间");
        }
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveStringData(this,"sobot_show_history_ruler",sobot_show_history_ruler.getText().toString());
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