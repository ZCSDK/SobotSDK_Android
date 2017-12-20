package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SobotSatisfactionSetActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout sobot_isShowSatisfaction;//是否弹出满意度评价
    private RelativeLayout sobot_evaluationCompletedExit;//评价完是否结束会话
    private boolean isShowSatisfaction = false, isEvaluationCompletedExit = false;
    private ImageView sobot_tv_left;
    private ImageView imageView3, imageView4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_satisfaction_set_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews(){
        sobot_isShowSatisfaction = (RelativeLayout) findViewById(R.id.sobot_isShowSatisfaction);
        sobot_evaluationCompletedExit = (RelativeLayout) findViewById(R.id.sobot_evaluationCompletedExit);
        imageView3 = (ImageView) findViewById(R.id.img_open_notify3);
        imageView4 = (ImageView) findViewById(R.id.img_open_notify4);
        sobot_isShowSatisfaction.setOnClickListener(this);
        sobot_evaluationCompletedExit.setOnClickListener(this);
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("评价设置");

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        boolean sobot_bool_isShowSatisfaction = SobotSPUtil.getBooleanData(this, "sobot_isShowSatisfaction", false);
        setShowSatisfaction(sobot_bool_isShowSatisfaction);

        boolean sobot_evaluationCompletedExit_value = SobotSPUtil.getBooleanData(this, "sobot_evaluationCompletedExit_value", false);
        setEvaluationCompletedExit(sobot_evaluationCompletedExit_value);
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveBooleanData(this, "sobot_isShowSatisfaction", isShowSatisfaction);
        SobotSPUtil.saveBooleanData(this, "sobot_evaluationCompletedExit_value", isEvaluationCompletedExit);
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
        } else if (v == sobot_isShowSatisfaction){
            isShowSatisfaction = !isShowSatisfaction;
            setShowSatisfaction(isShowSatisfaction);
        } else if (v == sobot_evaluationCompletedExit){
            isEvaluationCompletedExit = !isEvaluationCompletedExit;
            setEvaluationCompletedExit(isEvaluationCompletedExit);
        }
    }

    private void setShowSatisfaction(boolean open){
        if (open){
            isShowSatisfaction = true;
            imageView3.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isShowSatisfaction = false;
            imageView3.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setEvaluationCompletedExit(boolean open){
        if (open){
            isEvaluationCompletedExit = true;
            imageView4.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isEvaluationCompletedExit = false;
            imageView4.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }
}