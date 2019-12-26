package com.sobot.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.StViewPagerAdapter;
import com.sobot.chat.api.model.SobotLeaveMsgConfig;
import com.sobot.chat.fragment.SobotBaseFragment;
import com.sobot.chat.fragment.SobotPostMsgFragment;
import com.sobot.chat.fragment.SobotTicketInfoFragment;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.PagerSlidingTab;

import java.util.ArrayList;
import java.util.List;

public class SobotPostMsgActivity extends SobotBaseActivity implements View.OnClickListener {

    private SobotLeaveMsgConfig mConfig;
    private String mUid = "";
    private String mGroupId = "";
    private String mCustomerId = "";
    private String mCompanyId = "";
    private boolean flag_exit_sdk;
    private boolean mIsShowTicket;
    private boolean mIsCreateSuccess;
    private int flag_exit_type = -1;

    private LinearLayout mllContainer;
    private LinearLayout mLlCompleted;
    private ViewPager mViewPager;
    private TextView mTvTicket;
    private TextView mTvCompleted;
    private StViewPagerAdapter mAdapter;
    private PagerSlidingTab sobot_pst_indicator;
    private ImageView psgBackIv;


    private SobotPostMsgFragment mPostMsgFragment;
    private List<SobotBaseFragment> mFragments = new ArrayList<>();

    private MessageReceiver mReceiver;

    public static final String SOBOT_ACTION_SHOW_COMPLETED_VIEW = "sobot_action_show_completed_view";

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_post_msg");
    }

    protected void initBundleData(Bundle savedInstanceState) {
        if (getIntent() != null) {
            mUid = getIntent().getStringExtra(StPostMsgPresenter.INTENT_KEY_UID);
            mConfig = (SobotLeaveMsgConfig) getIntent().getSerializableExtra(StPostMsgPresenter.INTENT_KEY_CONFIG);
            mGroupId = getIntent().getStringExtra(StPostMsgPresenter.INTENT_KEY_GROUPID);
            mCustomerId = getIntent().getStringExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID);
            mCompanyId = getIntent().getStringExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID);
            flag_exit_type = getIntent().getIntExtra(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
            flag_exit_sdk = getIntent().getBooleanExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
            mIsShowTicket = getIntent().getBooleanExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, false);
        }
    }

    @Override
    protected void initView() {
//        showLeftMenu(getResDrawableId("sobot_icon_back_grey"), "", true);
//        setTitle(getResString("sobot_str_bottom_message"));
        mLlCompleted = (LinearLayout) findViewById(getResId("sobot_ll_completed"));
        mllContainer = (LinearLayout) findViewById(getResId("sobot_ll_container"));
        mTvTicket = (TextView) findViewById(getResId("sobot_tv_ticket"));
        mTvCompleted = (TextView) findViewById(getResId("sobot_tv_completed"));
        mViewPager = (ViewPager) findViewById(getResId("sobot_viewPager"));
        sobot_pst_indicator = (PagerSlidingTab) findViewById(getResId("sobot_pst_indicator"));
        psgBackIv = (ImageView) findViewById(getResId("sobot_pst_back_iv"));
        mTvTicket.setOnClickListener(this);
        mTvCompleted.setOnClickListener(this);
        psgBackIv.setOnClickListener(this);
        initReceiver();
    }

    @Override
    protected void initData() {

        mFragments.clear();
        if (!mIsShowTicket) {
            Bundle bundle = new Bundle();
            bundle.putString(StPostMsgPresenter.INTENT_KEY_UID, mUid);
            bundle.putString(StPostMsgPresenter.INTENT_KEY_GROUPID, mGroupId);
            bundle.putInt(ZhiChiConstant.FLAG_EXIT_TYPE, flag_exit_type);
            bundle.putBoolean(ZhiChiConstant.FLAG_EXIT_SDK, flag_exit_sdk);
            bundle.putSerializable(StPostMsgPresenter.INTENT_KEY_CONFIG, mConfig);
            bundle.putSerializable(StPostMsgPresenter.INTENT_KEY_CUS_FIELDS, getIntent().getSerializableExtra(StPostMsgPresenter.INTENT_KEY_CUS_FIELDS));
            mPostMsgFragment = SobotPostMsgFragment.newInstance(bundle);
            mFragments.add(mPostMsgFragment);
        }

        if (mIsShowTicket || (mConfig != null && mConfig.isTicketShowFlag())) {
            Bundle bundle2 = new Bundle();
            bundle2.putString(StPostMsgPresenter.INTENT_KEY_UID, mUid);
            bundle2.putString(StPostMsgPresenter.INTENT_KEY_COMPANYID, mCompanyId);
            bundle2.putString(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, mCustomerId);
            mFragments.add(SobotTicketInfoFragment.newInstance(bundle2));
        }

        if (mConfig != null) {
            mTvTicket.setVisibility(mConfig.isTicketShowFlag() ? View.VISIBLE : View.GONE);
        }

        mAdapter = new StViewPagerAdapter(this, getSupportFragmentManager(), new String[]{getResString("sobot_please_leave_a_message"), getResString("sobot_message_record")}, mFragments);
        mViewPager.setAdapter(mAdapter);

        if ((mConfig != null && mConfig.isTicketShowFlag()) && !mIsShowTicket) {
            if (!mIsCreateSuccess) {//不是创建成功
                mLlCompleted.setVisibility(View.VISIBLE);
                mllContainer.setVisibility(View.VISIBLE);
            }
            sobot_pst_indicator.setViewPager(mViewPager);
        }


        if (mIsShowTicket) {
            showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
            setTitle(getResString("sobot_message_record"));
            showTicketInfo();
            getToolBar().setVisibility(View.VISIBLE);
        } else {
            getToolBar().setVisibility(View.GONE);
        }


    }

    /**
     * 显示留言记录
     */
    private void showTicketInfo() {
        if (mFragments.size() > 0) {
            int lastIndex = mFragments.size() - 1;
            mViewPager.setCurrentItem(lastIndex);
            SobotBaseFragment lastFragment = mFragments.get(lastIndex);
            if (lastFragment instanceof SobotTicketInfoFragment) {
                SobotTicketInfoFragment fragment = (SobotTicketInfoFragment) lastFragment;
                fragment.initData();
            }
        }
    }

    private void initReceiver() {
        if (mReceiver == null) {
            mReceiver = new MessageReceiver();
        }
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(SOBOT_ACTION_SHOW_COMPLETED_VIEW);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        if (mPostMsgFragment != null) {
            mPostMsgFragment.onBackPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mTvTicket) {
            mLlCompleted.setVisibility(View.GONE);

            mViewPager.setVisibility(View.VISIBLE);
            if (mConfig != null && mConfig.isTicketShowFlag()) {
                mllContainer.setVisibility(View.VISIBLE);
            }
            showTicketInfo();
        }

        if (v == mTvCompleted) {
            onBackPressed();
        }

        if (v == psgBackIv) {
            onBackPressed();
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (SOBOT_ACTION_SHOW_COMPLETED_VIEW.equals(intent.getAction())) {
                //显示提交完成后的页面
                mllContainer.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);
                mLlCompleted.setVisibility(View.VISIBLE);
                mIsCreateSuccess = true;
                initData();

            }
        }
    }
}