package com.sobot.chat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotMsgCenterAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.handler.SobotMsgCenterHandler;
import com.sobot.chat.receiver.SobotMsgCenterReceiver;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCompareNewMsgTime;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ZhiChiConstant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 咨询列表
 * Created by jinxl on 2017/9/6.
 */
public class SobotConsultationListActivity extends SobotBaseActivity implements SobotMsgCenterHandler.SobotMsgCenterCallBack{
    //刷新列表
    private static final int REFRESH_DATA = 1;

    private ListView sobot_ll_msg_center;
    private SobotMsgCenterAdapter adapter;
    private List<SobotMsgCenterModel> datas = new ArrayList<>();
    private LocalBroadcastManager localBroadcastManager;
    private SobotMessageReceiver receiver;
    private String currentUid;
    private SHandler mHandler = new SHandler(this);
    private SobotCompareNewMsgTime mCompareNewMsgTime;

    static class SHandler extends Handler {
        WeakReference<Activity> mActivityReference;

        SHandler(Activity activity) {
            mActivityReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SobotConsultationListActivity activity = (SobotConsultationListActivity) mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_DATA:
                        List<SobotMsgCenterModel> datas = activity.datas;
                        SobotMsgCenterAdapter adapter = activity.adapter;
                        ListView sobot_ll_msg_center = activity.sobot_ll_msg_center;

                        List<SobotMsgCenterModel> msgCenterList = (List<SobotMsgCenterModel>) msg.obj;
                        if (msgCenterList != null) {
                            datas.clear();
                            datas.addAll(msgCenterList);
                            if (adapter == null) {
                                adapter = new SobotMsgCenterAdapter(activity, datas);
                                activity.adapter = adapter;
                                sobot_ll_msg_center.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_consultation_list");
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            currentUid = getIntent().getStringExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID);
        } else {
            currentUid = savedInstanceState.getString(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //销毁前缓存数据
        outState.putString(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, currentUid);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBrocastReceiver();
        mCompareNewMsgTime = new SobotCompareNewMsgTime();
    }

    /* 初始化广播接受者 */
    private void initBrocastReceiver() {
        if (receiver == null) {
            receiver = new SobotMessageReceiver();
        }
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ZhiChiConstants.receiveMessageBrocast);
        filter.addAction(ZhiChiConstant.SOBOT_ACTION_UPDATE_LAST_MSG);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        // 注册广播接收器
        localBroadcastManager.registerReceiver(receiver, filter);
    }

    @Override
    public void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
        setTitle(getResString("sobot_consultation_list"));

        sobot_ll_msg_center = (ListView) findViewById(getResId("sobot_ll_msg_center"));
        sobot_ll_msg_center.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SobotMsgCenterModel sobotMsgCenterModel = datas.get(position);
                Information info = sobotMsgCenterModel.getInfo();
                if (info != null) {
                    info.setUid(currentUid);
                    if (SobotOption.sobotConversationListCallback != null && !TextUtils.isEmpty(sobotMsgCenterModel.getAppkey())) {
                        SobotOption.sobotConversationListCallback.onConversationInit(getApplicationContext(), info);
                        return;
                    }
                    SobotApi.startSobotChat(getApplicationContext(), info);
                }
            }
        });
        sobot_ll_msg_center.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                new AlertDialog.Builder(SobotConsultationListActivity.this)
                        .setPositiveButton("删除会话", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final SobotMsgCenterModel data = (SobotMsgCenterModel) adapter.getItem(position);
                                dialog.dismiss();
                                ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
                                String platformID = SharedPreferencesUtil.getStringData(getApplicationContext(), ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
                                zhiChiApi.removeMerchant(SobotConsultationListActivity.this, platformID
                                        , currentUid,data, new StringResultCallBack<SobotMsgCenterModel>() {
                                            @Override
                                            public void onSuccess(SobotMsgCenterModel deleteData) {
                                                if (deleteData != null && deleteData.getInfo() != null && datas != null) {
                                                    datas.remove(deleteData);
                                                    Collections.sort(datas, mCompareNewMsgTime);
                                                    sendDatasOnUi(datas);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Exception e, String des) {
                                            }
                                        });
                            }
                        })
                        .create().show();

                return true;
            }
        });
    }

    @Override
    public void initData() {
        SobotMsgCenterHandler.getMsgCenterAllData(SobotConsultationListActivity.this,SobotConsultationListActivity.this,currentUid,this);
    }

    @Override
    public void onLocalDataSuccess(List<SobotMsgCenterModel> msgCenterList) {
        sendDatasOnUi(msgCenterList);
    }

    @Override
    public void onAllDataSuccess(List<SobotMsgCenterModel> msgCenterList) {
        sendDatasOnUi(msgCenterList);
    }

    private void sendDatasOnUi(final List<SobotMsgCenterModel> msgCenterList) {
        Message message = mHandler.obtainMessage();
        message.what = REFRESH_DATA;
        List<SobotMsgCenterModel> tmpList = new ArrayList<>();
        tmpList.addAll(msgCenterList);
        message.obj = tmpList;
        mHandler.sendMessage(message);
    }

    public class SobotMessageReceiver extends SobotMsgCenterReceiver {

        @Override
        public void onDataChanged(SobotMsgCenterModel data) {
            refershItemData(data);
        }

        @Override
        public List<SobotMsgCenterModel> getMsgCenterDatas() {
            return datas;
        }
    }

    /**
     * 刷新条目
     */
    public void refershItemData(final SobotMsgCenterModel model) {
        if (model != null && model.getInfo() != null && !TextUtils.isEmpty(model.getLastMsg()) && datas != null) {
            datas.remove(model);
            datas.add(model);
            Collections.sort(datas, mCompareNewMsgTime);
            sendDatasOnUi(datas);
        }
    }

    @Override
    public void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(SobotConsultationListActivity.this);
        // 取消广播接受者
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

}
