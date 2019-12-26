package com.sobot.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.api.model.BaseCode;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

public class SobotPostLeaveMsgActivity extends SobotBaseActivity implements View.OnClickListener {

    private static final String EXTRA_MSG_UID = "EXTRA_MSG_UID";
    private static final String EXTRA_MSG_LEAVE_TXT = "EXTRA_MSG_LEAVE_TXT";
    private static final String EXTRA_MSG_LEAVE_CONTENT_TXT = "EXTRA_MSG_LEAVE_CONTENT_TXT";
    private static final String EXTRA_MSG_LEAVE_CONTENT = "EXTRA_MSG_LEAVE_CONTENT";
    public static final int EXTRA_MSG_LEAVE_REQUEST_CODE = 109;

    private String mMsgLeaveTxt;
    private String mMsgLeaveContentTxt;
    private String mUid;
    private TextView sobot_tv_post_msg;
    private EditText sobot_post_et_content;
    private Button sobot_btn_submit;

    public static Intent newIntent(Context context, String msgLeaveTxt, String msgLeaveContentTxt, String uid) {
        Intent intent = new Intent(context, SobotPostLeaveMsgActivity.class);
        intent.putExtra(EXTRA_MSG_LEAVE_TXT, msgLeaveTxt);
        intent.putExtra(EXTRA_MSG_LEAVE_CONTENT_TXT, msgLeaveContentTxt);
        intent.putExtra(EXTRA_MSG_UID, uid);
        return intent;
    }

    public static String getResultContent(Intent intent) {
        if (intent != null) {
            return intent.getStringExtra(EXTRA_MSG_LEAVE_CONTENT);
        }
        return null;
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_post_leave_msg");
    }

    protected void initBundleData(Bundle savedInstanceState) {
        if (getIntent() != null) {
            mMsgLeaveTxt = getIntent().getStringExtra(EXTRA_MSG_LEAVE_TXT);
            mMsgLeaveContentTxt = getIntent().getStringExtra(EXTRA_MSG_LEAVE_CONTENT_TXT);
            mUid = getIntent().getStringExtra(EXTRA_MSG_UID);
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
        setTitle(getResString("sobot_leavemsg_title"));
        sobot_tv_post_msg = (TextView) findViewById(getResId("sobot_tv_post_msg"));
        sobot_post_et_content = (EditText) findViewById(getResId("sobot_post_et_content"));
        sobot_btn_submit = (Button) findViewById(getResId("sobot_btn_submit"));
        sobot_btn_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        sobot_tv_post_msg.setText(mMsgLeaveTxt);
        sobot_post_et_content.setHint(mMsgLeaveContentTxt);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_btn_submit) {
            final String content = sobot_post_et_content.getText().toString();
            if (TextUtils.isEmpty(content) || TextUtils.isEmpty(mUid)) {
                CustomToast.makeText(SobotPostLeaveMsgActivity.this, ResourceUtils.getResString(SobotPostLeaveMsgActivity.this, "sobot_problem_description") + ResourceUtils.getResString(SobotPostLeaveMsgActivity.this, "sobot_no_empty"), 1000).show();
                return;
            }
            KeyboardUtil.hideKeyboard(sobot_post_et_content);
            zhiChiApi.leaveMsg(SobotPostLeaveMsgActivity.class, mUid, content, new StringResultCallBack<BaseCode>() {
                @Override
                public void onSuccess(BaseCode baseCode) {
                    CustomToast.makeText(getBaseContext(), ResourceUtils.getResString(getBaseContext(), "sobot_leavemsg_success_tip"), 1000,
                            ResourceUtils.getDrawableId(getBaseContext(), "sobot_iv_login_right")).show();
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_MSG_LEAVE_CONTENT, content);
                    setResult(EXTRA_MSG_LEAVE_REQUEST_CODE, intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e, String des) {
                    ToastUtil.showToast(getApplicationContext(), des);
                }
            });
        }
    }

}