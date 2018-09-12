package com.sobot.chat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.sobot.chat.adapter.base.SobotMsgCenterAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCompareNewMsgTime;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ZhiChiConstant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 咨询列表
 * Created by jinxl on 2017/9/6.
 */
public class SobotConsultationListActivity extends SobotBaseActivity {
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
                        if (msgCenterList != null && msgCenterList.size() > 0) {
                            datas.clear();
                            datas.addAll(msgCenterList);
                            if (adapter == null) {
                                adapter = new SobotMsgCenterAdapter(activity, datas);
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
                if (SobotOption.sobotConversationListCallback != null && !TextUtils.isEmpty(sobotMsgCenterModel.getAppkey())) {
                    SobotOption.sobotConversationListCallback.onConversationInit(sobotMsgCenterModel.getAppkey());
                    return;
                }
                Information info = sobotMsgCenterModel.getInfo();
                if (info != null) {
                    SobotApi.startSobotChat(getApplicationContext(), info);
                }
            }
        });
    }

    @Override
    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SobotMsgCenterModel> msgCenterList = SobotApi.getMsgCenterList(getApplicationContext(), currentUid);
                if (msgCenterList == null) {
                    msgCenterList = new ArrayList<>();
                }
                SobotCompareNewMsgTime compare = new SobotCompareNewMsgTime();
                Collections.sort(msgCenterList,compare);
                sendDatasOnUi(msgCenterList);

                List<SobotMsgCenterModel> dataFromServer = getDataFromServer();
                if (dataFromServer != null && dataFromServer.size() > 0) {
                    dataFromServer.removeAll(msgCenterList);
                    msgCenterList.addAll(dataFromServer);
                    Collections.sort(msgCenterList,compare);
                    sendDatasOnUi(msgCenterList);
                }
            }
        }).start();
    }

    private void sendDatasOnUi(final List<SobotMsgCenterModel> msgCenterList) {
        Message message = mHandler.obtainMessage();
        message.what = REFRESH_DATA;
        List<SobotMsgCenterModel> tmpList = new ArrayList<>();
        tmpList.addAll(msgCenterList);
        message.obj = tmpList;
        mHandler.sendMessage(message);
    }

    public class SobotMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
                // 接受下推的消息
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    return;
                }
                ZhiChiPushMessage pushMessage = (ZhiChiPushMessage) extras.getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                if (pushMessage == null || TextUtils.isEmpty(pushMessage.getAppId())) {
                    return;
                }
                if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
                    // 接收到新的消息
                    for (int i = 0; i < datas.size(); i++) {
                        SobotMsgCenterModel sobotMsgCenterModel = datas.get(i);
                        if (sobotMsgCenterModel.getInfo() != null && pushMessage.getAppId().equals(sobotMsgCenterModel.getInfo().getAppkey())) {
                            ZhiChiReplyAnswer reply;
                            if (TextUtils.isEmpty(pushMessage.getMsgType())) {
                                return;
                            }
                            if ("7".equals(pushMessage.getMsgType())) {
                                reply = GsonUtil.jsonToZhiChiReplyAnswer(pushMessage.getContent());
                            } else {
                                reply = new ZhiChiReplyAnswer();
                                reply.setMsgType(pushMessage.getMsgType() + "");
                                reply.setMsg(pushMessage.getContent());
                            }
                            sobotMsgCenterModel.setLastDateTime(Calendar.getInstance().getTime().getTime() + "");
                            sobotMsgCenterModel.setLastMsg(reply.getMsg());
                            int unreadCount = sobotMsgCenterModel.getUnreadCount() + 1;
                            sobotMsgCenterModel.setUnreadCount(unreadCount);
                            refershItemData(sobotMsgCenterModel);
                            break;
                        }
                    }
                }
            } else if (ZhiChiConstant.SOBOT_ACTION_UPDATE_LAST_MSG.equals(intent.getAction())) {
                SobotMsgCenterModel lastMsg = (SobotMsgCenterModel) intent.getSerializableExtra("lastMsg");
                if (lastMsg == null || lastMsg.getInfo() == null || TextUtils.isEmpty(lastMsg.getInfo().getAppkey())) {
                    return;
                }
                refershItemData(lastMsg);
            }
        }
    }

    /**
     * 刷新条目
     */
    public void refershItemData(final SobotMsgCenterModel model) {
        if (model != null && model.getInfo() != null && !TextUtils.isEmpty(model.getLastMsg()) && datas != null) {
            datas.remove(model);
            datas.add(model);
            Collections.sort(datas,mCompareNewMsgTime);
            sendDatasOnUi(datas);
        }
    }

    /**
     * IO Thread
     * 从服务器获取会话列表
     */
    private List<SobotMsgCenterModel> getDataFromServer() {
        String platformID = SharedPreferencesUtil.getStringData(getApplicationContext(), ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        List<SobotMsgCenterModel> platformList = null;
        if (!TextUtils.isEmpty(platformID) && !TextUtils.isEmpty(currentUid)) {
            ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
            try {
                platformList = zhiChiApi.getPlatformList(ZhiChiConstant.SOBOT_GLOBAL_REQUEST_CANCEL_TAG, platformID, currentUid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return platformList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消广播接受者
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

}
