package com.sobot.chat.conversation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sobot.chat.activity.base.SobotBaseActivity;

public class SobotChatActivity extends SobotBaseActivity {

    Bundle informationBundle;
    SobotChatFragment chatFragment;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_chat_act");
    }

    protected void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            informationBundle = getIntent().getBundleExtra("informationBundle");
        } else {
            informationBundle = savedInstanceState.getBundle("informationBundle");
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        //销毁前缓存数据
        outState.putBundle("informationBundle", informationBundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initView() {
        chatFragment = (SobotChatFragment) getSupportFragmentManager()
                .findFragmentById(getResId("sobot_contentFrame"));
        if (chatFragment == null) {
            chatFragment = SobotChatFragment.newInstance(informationBundle);

            addFragmentToActivity(getSupportFragmentManager(),
                    chatFragment, getResId("sobot_contentFrame"));
        }
    }

    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        if (chatFragment != null) {
            chatFragment.onBackPress();
        } else {
            super.onBackPressed();
        }
    }
}