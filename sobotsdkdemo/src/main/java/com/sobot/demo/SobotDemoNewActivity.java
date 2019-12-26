package com.sobot.demo;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.lzy.widget.AlphaIndicator;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class SobotDemoNewActivity extends AppCompatActivity {

    private SobotNotificationClickReceiver nClickReceiver;//点击通知以后发出的广播接收者
    private SobotUnReadMsgReceiver unReadMsgReceiver;//获取未读消息数的广播接收者

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_demo_new_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        LogUtils.isDebug = true;
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        AlphaIndicator alphaIndicator = (AlphaIndicator) findViewById(R.id.alphaIndicator);
        alphaIndicator.setViewPager(viewPager);
        regReceiver();
    }

    private class MainAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public MainAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new SobotDemoWelcomeFragment());
            fragments.add(new SobotDemoSettingFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void regReceiver(){
        IntentFilter filter = new IntentFilter();
        if (nClickReceiver == null){
            nClickReceiver = new SobotNotificationClickReceiver();
        }
        filter.addAction(ZhiChiConstant.SOBOT_NOTIFICATION_CLICK);
        registerReceiver(nClickReceiver, filter);

        if (unReadMsgReceiver == null){
            unReadMsgReceiver = new SobotUnReadMsgReceiver();
        }
        filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
        registerReceiver(unReadMsgReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        try {
            if (nClickReceiver != null){
                unregisterReceiver(nClickReceiver);
            }

            if (unReadMsgReceiver != null){
                unregisterReceiver(unReadMsgReceiver);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }
}