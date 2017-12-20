package com.sobot.demo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sobot.demo.R;
import com.sobot.demo.SobotDemoNewActivity;

/**
 * 闪屏界面
 *
 * @author Eric
 */
public class SplashActivity extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler();
    private int splashMin = 2000;//在闪屏界面等待的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sobot_splash_activity);
        goActivity(SobotDemoNewActivity.class, splashMin, true);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void goActivity(final Class clz, long delMin, final boolean isSuccess) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, clz);
                intent.putExtra("isSuccess", isSuccess);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, delMin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
