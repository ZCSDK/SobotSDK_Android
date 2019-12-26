package com.sobot.chat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotSikllAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

public class SobotSkillGroupActivity extends SobotDialogBaseActivity {

    private LinearLayout sobot_btn_cancle;
    private GridView sobot_gv_skill;
    private SobotSikllAdapter sobotSikllAdapter;
    private List<ZhiChiGroupBase> list_skill = new ArrayList<ZhiChiGroupBase>();
    private boolean flag_exit_sdk;
    private String uid = null;
    private String companyId = null;
    private String customerId = null;
    private String appkey = null;
    private String msgTmp = null;
    private String msgTxt = null;
    private int transferType;
    private ZhiChiApi zhiChiApi;
    private int mType = -1;
    private int msgFlag = 0;

    private StPostMsgPresenter mPressenter;

    @Override
    protected int getRootViewLayoutId() {
        return ResourceUtils.getResLayoutId(this, "sobot_activity_skill_group");
    }

    @Override
    protected void initView() {
        mPressenter = StPostMsgPresenter.newInstance(SobotSkillGroupActivity.this, SobotSkillGroupActivity.this);
        sobot_btn_cancle = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_btn_cancle"));
        sobot_gv_skill = (GridView) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_gv_skill"));

        sobotSikllAdapter = new SobotSikllAdapter(this, list_skill, msgFlag);
        sobot_gv_skill.setAdapter(sobotSikllAdapter);
        sobot_gv_skill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list_skill != null && list_skill.size() > 0) {
                    if ("true".equals(list_skill.get(position).isOnline())) {
                        if (!TextUtils.isEmpty(list_skill.get(position).getGroupName())) {
                            Intent intent = new Intent();
                            intent.putExtra("groupIndex", position);
                            intent.putExtra("transferType", transferType);
                            setResult(ZhiChiConstant.REQUEST_COCE_TO_GRROUP, intent);
                            finish();
                        }
                    } else {
                        if (msgFlag == ZhiChiConstant.sobot_msg_flag_open) {
                            Intent intent = new Intent();
                            intent.putExtra("toLeaveMsg", true);
                            setResult(ZhiChiConstant.REQUEST_COCE_TO_GRROUP, intent);
                            finish();
                        }
                    }
                }
            }
        });

        sobot_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishPageOrSDK();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() <= 0) {
                finishPageOrSDK();
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        mPressenter.destory();
        OkHttpUtils.getInstance().cancelTag(SobotSkillGroupActivity.this);
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }

    protected void initData() {
        if (getIntent() != null) {
            uid = getIntent().getStringExtra("uid");
            companyId = getIntent().getStringExtra("companyId");
            customerId = getIntent().getStringExtra("customerId");
            appkey = getIntent().getStringExtra("appkey");
            flag_exit_sdk = getIntent().getBooleanExtra(
                    ZhiChiConstant.FLAG_EXIT_SDK, false);
            mType = getIntent().getIntExtra("type", -1);
            msgTmp = getIntent().getStringExtra("msgTmp");
            msgTxt = getIntent().getStringExtra("msgTxt");
            msgFlag = getIntent().getIntExtra("msgFlag", 0);
            transferType = getIntent().getIntExtra("transferType", 0);
        }
        zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        zhiChiApi.getGroupList(SobotSkillGroupActivity.this, appkey, uid, new StringResultCallBack<ZhiChiGroup>() {
            @Override
            public void onSuccess(ZhiChiGroup zhiChiGroup) {

                list_skill = zhiChiGroup.getData();
                if (list_skill != null && list_skill.size() > 0) {
//                    if (list_skill.size() % 2 != 0) {
//                        list_skill.add(new ZhiChiGroupBase("", ""));// 奇数时，加一个空布局，仅为展示
//                    }
                    sobotSikllAdapter = new SobotSikllAdapter(getApplicationContext(), list_skill, msgFlag);
                    sobot_gv_skill.setAdapter(sobotSikllAdapter);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
            }
        });
    }

    private void finishPageOrSDK() {
        int initType = SharedPreferencesUtil.getIntData(
                getApplicationContext(), appkey + "_" + ZhiChiConstant.initType, -1);
        if (initType == ZhiChiConstant.type_custom_only) {
            finish();
            sendCloseIntent(1);
        } else {
            if (!flag_exit_sdk) {
                finish();
                sendCloseIntent(2);
            } else {
                MyApplication.getInstance().exit();
            }
        }
    }

    private void sendCloseIntent(int type) {
        Intent intent = new Intent();
        if (type == 1) {
            intent.setAction(ZhiChiConstants.sobot_close_now_clear_cache);
        } else {
            intent.setAction(ZhiChiConstants.sobot_click_cancle);
        }
        CommonUtils.sendLocalBroadcast(getApplicationContext(), intent);
    }

    @Override
    public void onBackPressed() {
        finishPageOrSDK();
    }

    private void startToPostMsgActivty() {
        mPressenter.obtainTemplateList(uid, new StPostMsgPresenter.ObtainTemplateListDelegate() {
            @Override
            public void onSuccess(Intent intent) {
                intent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, companyId);
                intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, customerId);

                if (mType == 2) {
                    intent.putExtra(ZhiChiConstant.FLAG_EXIT_TYPE, 1);
                    startActivity(intent);
                } else if (mType == 3 || mType == 1) {
                    intent.putExtra(ZhiChiConstant.FLAG_EXIT_TYPE, 2);
                    startActivityForResult(intent, 200);
                } else if (mType == 4) {
                    intent.putExtra(ZhiChiConstant.FLAG_EXIT_TYPE, 2);
                    startActivityForResult(intent, 200);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            finish();
        }
    }
}