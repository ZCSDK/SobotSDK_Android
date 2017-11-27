package com.sobot.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.adapter.base.SobotMsgCenterAdapter;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCache;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.RichTextMessageHolder;
import com.sobot.chat.viewHolder.VoiceMessageHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 咨询列表
 * Created by jinxl on 2017/9/6.
 */
public class SobotConsultationListActivity extends SobotBaseActivity {

    private ListView sobot_ll_msg_center;
    private SobotMsgCenterAdapter adapter;
    private SobotCache mCache;
    private List<SobotMsgCenterModel> datas = new ArrayList<SobotMsgCenterModel>();
    private LocalBroadcastManager localBroadcastManager;
    private SobotMessageReceiver receiver;

    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null ){
            currentUid = getIntent().getStringExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID);
        }else{
            currentUid = savedInstanceState.getString(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID);
        }
        setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_activity_consultation_list"));
        mCache = SobotCache.get(getApplicationContext());
        initView();
        initBrocastReceiver();
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
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        List<SobotMsgCenterModel> msgCenterList = SobotApi.getMsgCenterList(getApplicationContext(),currentUid);
        if (msgCenterList != null && msgCenterList.size() > 0) {
            datas.clear();
            datas.addAll(msgCenterList);

            if (adapter == null) {
                adapter = new SobotMsgCenterAdapter(SobotConsultationListActivity.this, datas);
                sobot_ll_msg_center.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            //empty
        }

    }

    private void initView() {
        Drawable drawable = getResources().getDrawable(getResDrawableId("sobot_btn_back_selector"));
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        sobot_tv_left.setCompoundDrawables(drawable, null, null, null);
        sobot_tv_left.setText(getResString("sobot_back"));
        sobot_tv_left.setOnClickListener(this);

        setTitle(getResString("sobot_consultation_list"));
        setShowNetRemind(false);
        String bg_color = SharedPreferencesUtil.getStringData(this,
                "robot_current_themeColor", "");
        if (bg_color != null && bg_color.trim().length() != 0) {
            relative.setBackgroundColor(Color.parseColor(bg_color));
        }

        int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
        if (robot_current_themeImg != 0) {
            relative.setBackgroundResource(robot_current_themeImg);
        }

        sobot_ll_msg_center = (ListView) findViewById(getResId("sobot_ll_msg_center"));
        sobot_ll_msg_center.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SobotMsgCenterModel sobotMsgCenterModel = datas.get(position);
                Information info = sobotMsgCenterModel.getInfo();
                if (info != null) {
                    SobotApi.startSobotChat(getApplicationContext(), info);
                }
            }
        });

    }

    public class SobotMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
                // 接受下推的消息
                ZhiChiPushMessage pushMessage = (ZhiChiPushMessage) intent.getExtras().getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                if (pushMessage == null || TextUtils.isEmpty(pushMessage.getAppId())) {
                    return;
                }
                if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
                    // 接收到新的消息
                    for (int i = 0; i < datas.size(); i++) {
                        SobotMsgCenterModel sobotMsgCenterModel = datas.get(i);
                        if (sobotMsgCenterModel.getInfo() != null && pushMessage.getAppId().equals(sobotMsgCenterModel.getInfo().getAppkey())) {
                            ZhiChiReplyAnswer reply = null;
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
                            sobotMsgCenterModel.setLastDate(DateUtil.toDate(Calendar.getInstance().getTime().getTime(),DateUtil.DATE_FORMAT5));
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
    public void refershItemData(final SobotMsgCenterModel model){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0, count = sobot_ll_msg_center.getChildCount(); i < count; i++) {
                    View child = sobot_ll_msg_center.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof SobotMsgCenterAdapter.SobotMsgCenterViewHolder)) {
                        continue;
                    }
                    SobotMsgCenterAdapter.SobotMsgCenterViewHolder holder = (SobotMsgCenterAdapter.SobotMsgCenterViewHolder) child.getTag();
                    if (model != null && model.getInfo() != null && holder.data!=null
                            && holder.data.getInfo() != null && model.getInfo().getAppkey() != null
                            && model.getInfo().getAppkey().equals(holder.data.getInfo().getAppkey())) {
                        holder.bindData(model);
                    }
                }
            }
        });
    }

    @Override
    public void forwordMethod() {

    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消广播接受者
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        //销毁前缓存数据
        outState.putString(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID,currentUid);
        super.onSaveInstanceState(outState);
    }
}
