package com.sobot.demo.activity.product;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.R;
import com.sobot.demo.SobotUtils;

public class SobotDemoCloudCallActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView sobot_tv_left;
    private RelativeLayout sobot_demo_bottom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_cloud_call_activity);
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_demo_bottom_layout = (RelativeLayout) findViewById(R.id.sobot_demo_bottom_layout);
        sobot_demo_bottom_layout.setOnClickListener(this);
        sobot_text_title.setText("云呼叫中心");
        sobot_tv_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }

        if (v == sobot_demo_bottom_layout){
            SobotUtils.startSobot(this);
        }
    }
}