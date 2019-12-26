package com.sobot.chat.conversation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.sobot.chat.activity.SobotQueryFromActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.SobotQueryFormModel;
import com.sobot.chat.api.model.SobotQuestionRecommend;
import com.sobot.chat.api.model.SobotUserTicketInfoFlag;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.fragment.SobotBaseFragment;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.FileOpenHelper;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Created by jinxl on 2018/2/9.
 */
public abstract class SobotChatBaseFragment extends SobotBaseFragment implements SensorEventListener {

    protected Context mAppContext;

    //消息发送状态
    protected static final int SEND_VOICE = 0;
    protected static final int UPDATE_VOICE = 1;
    protected static final int CANCEL_VOICE = 2;
    protected static final int SEND_TEXT = 0;
    protected static final int UPDATE_TEXT = 1;
    protected static final int UPDATE_TEXT_VOICE = 2;

    //当前客户端模式
    protected int current_client_model = ZhiChiConstant.client_model_robot;
    //客服在线状态
    protected CustomerState customerState = CustomerState.Offline;
    protected ZhiChiInitModeBase initModel;/*初始化成功服务器返回的实体对象*/
    //
    protected String currentUserName;
    private String adminFace = "";

    protected boolean isAboveZero = false;
    protected int remindRobotMessageTimes = 0;//机器人的提醒次数
    protected boolean isRemindTicketInfo;//是否已经进行过工单状态提醒

    //
    //防止询前表单接口重复执行
    private boolean isQueryFroming = false;
    //防止重复弹出询前表单
    protected boolean isHasRequestQueryFrom = false;

    //定时器
    protected boolean customTimeTask = false;
    protected boolean userInfoTimeTask = false;
    protected boolean is_startCustomTimerTask = false;
    protected int noReplyTimeUserInfo = 0; // 用户已经无应答的时间
    public int paseReplyTimeUserInfo = 0; // 会话返回山歌界面时  用户计时器暂停时间

    //会话是否锁定 0:默认未收到消息  1: true 锁定；2: false 解锁
    protected int isChatLock = 0;

    private Timer timerUserInfo;
    private TimerTask taskUserInfo;
    /**
     * 客服的定时任务
     */
    protected Timer timerCustom;
    protected TimerTask taskCustom;
    protected int noReplyTimeCustoms = 0;// 客服无应答的时间
    public int paseReplyTimeCustoms = 0;// 会话返回山歌界面时 客服计时器暂停时间
    protected int serviceOutTimeTipCount = 0; // 客服无应答超时提醒次数


    //正在输入监听
    private Timer inputtingListener = null;//用于监听正在输入的计时器
    private boolean isSendInput = false;//防止同时发送正在输入
    private String lastInputStr = "";
    private TimerTask inputTimerTask = null;

    //语音相关
    // 听筒模式转换
    private AudioManager audioManager = null; // 声音管理器
    private SensorManager _sensorManager = null; // 传感器管理器
    private Sensor mProximiny = null; // 传感器实例

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppContext = getContext().getApplicationContext();
        initAudioManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initModel != null && customerState == CustomerState.Online && current_client_model == ZhiChiConstant
                .client_model_customService) {
            restartInputListener();
        }
        NotificationUtils.cancleAllNotification(mAppContext);
        //重新恢复连接
        if (customerState == CustomerState.Online || customerState == CustomerState.Queuing) {
            zhiChiApi.reconnectChannel();
        }

        if (_sensorManager != null) {
            _sensorManager.registerListener(this, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        stopInputListener();
        // 取消注册传感器
        _sensorManager.unregisterListener(this);
        super.onPause();
    }

    protected void finish() {
        if (isActive() && getSobotActivity() != null) {
            getSobotActivity().finish();
        }
    }

    /**
     * fragment是否有效
     *
     * @return
     */
    protected boolean isActive() {
        return isAdded();
    }

    /**
     * 用户的定时任务的处理
     */
    public void startUserInfoTimeTask(final Handler handler) {
        LogUtils.i("--->  startUserInfoTimeTask=====" + isChatLock);
        if (isChatLock == 1) {
            return;
        }
        if (current_client_model == ZhiChiConstant.client_model_customService) {
            if (initModel.isCustomOutTimeFlag()) {
                stopUserInfoTimeTask();
                userInfoTimeTask = true;
                timerUserInfo = new Timer();
                taskUserInfo = new TimerTask() {
                    @Override
                    public void run() {
                        // 需要做的事:发送消息
                        sendHandlerUserInfoTimeTaskMessage(handler);
                    }
                };
                timerUserInfo.schedule(taskUserInfo, 1000, 1000);
            }
        }
    }

    public void stopUserInfoTimeTask() {
        userInfoTimeTask = false;
        if (timerUserInfo != null) {
            timerUserInfo.cancel();
            timerUserInfo = null;
        }
        if (taskUserInfo != null) {
            taskUserInfo.cancel();
            taskUserInfo = null;
        }
        noReplyTimeUserInfo = 0;

    }

    /**
     * 设置定时任务
     */
    public void setTimeTaskMethod(Handler handler) {
        if (customerState == CustomerState.Online) {
            //LogUtils.i(" 定时任务的计时的操作：" + current_client_model);
            // 断开我的计时任务
            if (current_client_model == ZhiChiConstant.client_model_customService) {
                if (!is_startCustomTimerTask) {
                    stopUserInfoTimeTask();
                    startCustomTimeTask(handler);
                }
            }
        } else {
            stopCustomTimeTask();
            stopUserInfoTimeTask();
        }
    }

    public void restartMyTimeTask(Handler handler) {
        if (customerState == CustomerState.Online) {
            // 断开我的计时任务
            if (current_client_model == ZhiChiConstant.client_model_customService) {
                if (!is_startCustomTimerTask) {
                    stopUserInfoTimeTask();
                    startCustomTimeTask(handler);
                }
            }
        }
    }

    /**
     * 客服的定时处理
     */
    public void startCustomTimeTask(final Handler handler) {
        if (isChatLock == 1) {
            return;
        }
        if (current_client_model == ZhiChiConstant.client_model_customService) {
            if (initModel.isServiceOutTimeFlag()) {
                if (initModel.isServiceOutCountRule() && serviceOutTimeTipCount >= 1) {
                    //开启 客服超时推送规则，只显示一次 时并且已经提醒过一次 屏蔽之后的操作
                    stopCustomTimeTask();
                    return;
                }
                if (!is_startCustomTimerTask) {
                    stopCustomTimeTask();
                    customTimeTask = true;
                    is_startCustomTimerTask = true;
                    timerCustom = new Timer();
                    taskCustom = new TimerTask() {
                        @Override
                        public void run() {
                            // 需要做的事:发送消息
                            sendHandlerCustomTimeTaskMessage(handler);
                        }
                    };
                    timerCustom.schedule(taskCustom, 1000, 1000);
                }
            }
        }
    }

    public void stopCustomTimeTask() {
        customTimeTask = false;
        is_startCustomTimerTask = false;
        if (timerCustom != null) {
            timerCustom.cancel();
            timerCustom = null;
        }
        if (taskCustom != null) {
            taskCustom.cancel();
            taskCustom = null;
        }
        noReplyTimeCustoms = 0;

    }

    /**
     * 客服的定时任务处理
     */
    public void sendHandlerCustomTimeTaskMessage(Handler handler) {
        noReplyTimeCustoms++;
        // 用户和人工进行聊天 超长时间没有发起对话
        //LogUtils.i("  客服 ---的定时任务--监控--->："+noReplyTimeCustoms );
        // 妹子忙翻了
        if (initModel != null) {
            if (noReplyTimeCustoms == Integer.parseInt(initModel.getAdminTipTime()) * 60) {
                serviceOutTimeTipCount++;
                ZhiChiMessageBase result = new ZhiChiMessageBase();
                result.setT(Calendar.getInstance().getTime().getTime() + "");
                ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
                customTimeTask = false;
                // 发送我的语音的消息
                result.setSenderName(currentUserName); // 当前的用户
                result.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                String adminTipWord = SharedPreferencesUtil.getStringData(mAppContext, ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, "");
                if (!TextUtils.isEmpty(adminTipWord)) {
                    reply.setMsg(adminTipWord);
                } else {
                    String msgHint = initModel.getAdminTipWord().replace("\n", "<br/>");
                    if (msgHint.startsWith("<br/>")) {
                        msgHint = msgHint.substring(5, msgHint.length());
                    }

                    if (msgHint.endsWith("<br/>")) {
                        msgHint = msgHint.substring(0, msgHint.length() - 5);
                    }
                    reply.setMsg(msgHint);
                }

                result.setSenderFace(adminFace);
                reply.setMsgType(ZhiChiConstant.message_type_text + "");
                result.setAnswer(reply);
                Message message = handler.obtainMessage();
                message.what = ZhiChiConstant.hander_timeTask_custom_isBusying;
                message.obj = result;
                // 当有通道连接的时候才提醒
                handler.sendMessage(message);
                LogUtils.i("sobot---sendHandlerCustomTimeTaskMessage" + noReplyTimeCustoms);
            }
        }
    }

    /**
     * 客户的定时任务处理
     *
     * @param handler
     */
    private void sendHandlerUserInfoTimeTaskMessage(Handler handler) {
        noReplyTimeUserInfo++;
        // LogUtils.i(" 客户的定时任务--监控--->："+noReplyTimeUserInfo );
        // 用户几分钟没有说话
        if (current_client_model == ZhiChiConstant.client_model_customService) {
            if (initModel != null) {
                if (noReplyTimeUserInfo == (Integer.parseInt(initModel.getUserOutTime()) * 60)) {
                    userInfoTimeTask = false;
                    // 进行消息的封装
                    ZhiChiMessageBase base = new ZhiChiMessageBase();
                    base.setT(Calendar.getInstance().getTime().getTime() + "");
                    // 设置
                    base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                    ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
                    reply.setMsgType(ZhiChiConstant.message_type_text + "");
                    // 根据当前的模式
                    base.setSenderName(currentUserName);

                    String userTipWord = SharedPreferencesUtil.getStringData(mAppContext, ZhiChiConstant.SOBOT_USER_TIP_WORD, "");
                    if (!TextUtils.isEmpty(userTipWord)) {
                        reply.setMsg(userTipWord);
                    } else {
                        String msgHint = initModel.getUserTipWord().replace("\n", "<br/>");
                        if (msgHint.startsWith("<br/>")) {
                            msgHint = msgHint.substring(5, msgHint.length());
                        }

                        if (msgHint.endsWith("<br/>")) {
                            msgHint = msgHint.substring(0, msgHint.length() - 5);
                        }
                        reply.setMsg(msgHint);
                    }

                    base.setAnswer(reply);
                    base.setSenderFace(adminFace);
                    Message message = handler.obtainMessage();
                    message.what = ZhiChiConstant.hander_timeTask_userInfo;
                    message.obj = base;
                    handler.sendMessage(message);
                }
            }
        }
    }

    // ##################### 更新界面的ui ###############################

    /**
     * handler 消息实体message 更新ui界面
     *
     * @param messageAdapter
     * @param msg
     */
    protected void updateUiMessage(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        updateUiMessage(messageAdapter, myMessage);
    }

    protected void updateMessageStatus(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        messageAdapter.updateDataStateById(myMessage.getId(), myMessage);
        messageAdapter.notifyDataSetChanged();
    }

    protected void updateVoiceStatusMessage(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        messageAdapter.updateVoiceStatusById(myMessage.getId(),
                myMessage.getSendSuccessState(), myMessage.getAnswer().getDuration());
        messageAdapter.notifyDataSetChanged();
    }

    protected void cancelUiVoiceMessage(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        messageAdapter.cancelVoiceUiById(myMessage.getId());
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * 通过消息实体 zhiChiMessage进行封装
     *
     * @param messageAdapter
     * @param zhichiMessage
     */
    protected void updateUiMessage(SobotMsgAdapter messageAdapter, ZhiChiMessageBase zhichiMessage) {
        messageAdapter.addData(zhichiMessage);
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * 通过消息实体 zhiChiMessage进行封装
     *
     * @param messageAdapter
     * @param zhichiMessage
     */
    protected void updateUiMessageBefore(SobotMsgAdapter messageAdapter, ZhiChiMessageBase zhichiMessage) {
        messageAdapter.addDataBefore(zhichiMessage);
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * @param messageAdapter
     * @param id
     * @param status
     * @param progressBar
     */
    protected void updateUiMessageStatus(SobotMsgAdapter messageAdapter,
                                         String id, int status, int progressBar) {
        messageAdapter.updateMsgInfoById(id, status, progressBar);
        messageAdapter.notifyDataSetChanged();
    }

    // ##################### 更新界面的ui ###############################

    protected String getAdminFace() {
        return this.adminFace;
    }

    protected void setAdminFace(String str) {
        LogUtils.i("头像地址是" + str);
        this.adminFace = str;
    }

    /**
     * @param context
     * @param initModel
     * @param handler
     * @param current_client_model
     */
    protected void sendMessageWithLogic(String msgId, String context,
                                        ZhiChiInitModeBase initModel, final Handler handler, int current_client_model, int questionFlag, String question) {
        if (ZhiChiConstant.client_model_robot == current_client_model) { // 客户和机械人进行聊天
            sendHttpRobotMessage(msgId, context, initModel.getPartnerid(),
                    initModel.getCid(), handler, questionFlag, question);
            LogUtils.i("机器人模式");
        } else if (ZhiChiConstant.client_model_customService == current_client_model) {
            sendHttpCustomServiceMessage(context, initModel.getPartnerid(),
                    initModel.getCid(), handler, msgId);
            LogUtils.i("客服模式");
        }
    }

    // 人与机械人进行聊天
    protected void sendHttpRobotMessage(final String msgId, String requestText,
                                        String uid, String cid, final Handler handler, int questionFlag, String question) {
        zhiChiApi.chatSendMsgToRoot(initModel.getRobotid(), requestText, questionFlag, question, uid, cid,
                new StringResultCallBack<ZhiChiMessageBase>() {
                    @Override
                    public void onSuccess(ZhiChiMessageBase simpleMessage) {
                        if (!isActive()) {
                            return;
                        }
                        // 机械人的回答语
                        sendTextMessageToHandler(msgId, null, handler, 1, UPDATE_TEXT);
                        String id = System.currentTimeMillis() + "";
                        if (simpleMessage.getUstatus() == ZhiChiConstant.result_fail_code) {
                            //机器人超时下线
                            customerServiceOffline(initModel, 4);
                        } else {
                            isAboveZero = true;
                            simpleMessage.setId(id);
                            simpleMessage.setSenderName(initModel.getRobotName());
                            simpleMessage.setSender(initModel.getRobotName());
                            simpleMessage.setSenderFace(initModel.getRobotLogo());
                            simpleMessage.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
                            Message message = handler.obtainMessage();
                            message.what = ZhiChiConstant.hander_robot_message;
                            message.obj = simpleMessage;
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        if (!isActive()) {
                            return;
                        }
                        //LogUtils.e("sendHttpRobotMessage:e= " +e.toString() + des);
                        // 显示信息发送失败
                        sendTextMessageToHandler(msgId, null, handler, 0, UPDATE_TEXT);
                    }
                });
    }

    protected void sendHttpCustomServiceMessage(final String content, final String uid,
                                                String cid, final Handler handler, final String mid) {
        zhiChiApi.sendMsgToCoutom(content, uid, cid, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                Boolean switchFlag = Boolean.valueOf(commonModelBase.getSwitchFlag()).booleanValue();
                //如果switchFlag 为true，就会断开通道走，轮训
                if (switchFlag) {
                    // 说明用户端有两条或者两条以上消息未接受到，要去切换轮询
                    CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_CHECK_SWITCHFLAG));
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
                    sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(mid)) {
                        CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_CHECK_CONNCHANNEL));
                        isAboveZero = true;
                        // 当发送成功的时候更新ui界面
                        sendTextMessageToHandler(mid, null, handler, 1, UPDATE_TEXT);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                //LogUtils.e("sendHttpCustomServiceMessage:e= " + e.toString());
                sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
            }
        });
    }

    /**
     * 发送卡片信息
     *
     * @param consultingContent
     * @param uid
     * @param cid
     * @param handler
     * @param mid
     */
    protected void sendHttpCardMsg(final String uid,
                                   String cid, final Handler handler, final String mid, final ConsultingContent consultingContent) {
        zhiChiApi.sendCardMsg(consultingContent, uid, cid, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
//                    sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(mid)) {
                        isAboveZero = true;
                        // 当发送成功的时候更新ui界面
                        ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
                        myMessage.setId(mid);
                        myMessage.setConsultingContent(consultingContent);
                        myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                        myMessage.setSendSuccessState(1);
                        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                        answer.setMsgType(ZhiChiConstant.message_type_card + "");
                        myMessage.setAnswer(answer);
                        myMessage.setT(Calendar.getInstance().getTime().getTime() + "");
                        Message handMyMessage = handler.obtainMessage();
                        handMyMessage.what = ZhiChiConstant.hander_send_msg;
                        handMyMessage.obj = myMessage;
                        handler.sendMessage(handMyMessage);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                final Map<String, String> map = new HashMap<>();
                String cotent = e.toString() + des;
                map.put("sendHttpCardMsg", cotent);
                LogUtils.i2Local(map, LogUtils.LOGTYPE_ERROE);
                LogUtils.i("sendHttpCardMsg error:" + e.toString());
//                sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
            }
        });
    }

    /**
     * 发送订单卡片信息
     *
     * @param orderCardContent
     * @param uid
     * @param cid
     * @param handler
     * @param mid
     */
    protected void sendHttpOrderCardMsg(final String uid,
                                        String cid, final Handler handler, final String mid, final OrderCardContentModel orderCardContent) {
        zhiChiApi.sendOrderCardMsg(orderCardContent, uid, cid, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
//                    sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(mid)) {
                        isAboveZero = true;
                        // 当发送成功的时候更新ui界面
                        ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
                        myMessage.setId(mid);
                        myMessage.setOrderCardContent(orderCardContent);
                        myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                        myMessage.setSendSuccessState(1);
                        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                        answer.setMsgType(ZhiChiConstant.message_type_ordercard + "");
                        myMessage.setAnswer(answer);
                        myMessage.setT(Calendar.getInstance().getTime().getTime() + "");
                        Message handMyMessage = handler.obtainMessage();
                        handMyMessage.what = ZhiChiConstant.hander_send_msg;
                        handMyMessage.obj = myMessage;
                        handler.sendMessage(handMyMessage);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                final Map<String, String> map = new HashMap<>();
                String cotent = e.toString() + des;
                map.put("sendHttpOrderCardMsg", cotent);
                LogUtils.i2Local(map, LogUtils.LOGTYPE_ERROE);
                LogUtils.i("sendHttpOrderCardMsg error:" + e.toString());
//                sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
            }
        });
    }


    protected void uploadFile(File selectedFile, Handler handler, final ListView lv_message,
                              final SobotMsgAdapter messageAdapter) {
        if (selectedFile != null && selectedFile.exists()) {
            // 发送文件
            LogUtils.i(selectedFile.toString());
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".gif") || fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                ChatUtils.sendPicLimitBySize(selectedFile.getAbsolutePath(), initModel.getCid(),
                        initModel.getPartnerid(), handler, mAppContext, lv_message, messageAdapter);
            } else {
                if (selectedFile.length() > 20 * 1024 * 1024) {
                    ToastUtil.showToast(getContext(), getResString("sobot_file_upload_failed"));
                    return;
                }
                if (!FileOpenHelper.checkEndsWithInStringArray(fileName, getContext(), "sobot_fileEndingAll")) {
                    ToastUtil.showToast(getContext(), getResString("sobot_file_upload_failed_unknown_format"));
                    return;
                }
                String tmpMsgId = String.valueOf(System.currentTimeMillis());
                LogUtils.i("tmpMsgId:" + tmpMsgId);
                zhiChiApi.addUploadFileTask(false, tmpMsgId, initModel.getPartnerid(), initModel.getCid(), selectedFile.getAbsolutePath(), null);
                updateUiMessage(messageAdapter, ChatUtils.getUploadFileModel(getContext(), tmpMsgId, selectedFile));
            }
        }
    }

    protected void sendLocation(String msgId, SobotLocationModel data, final Handler handler, boolean isNewMsg) {
        if (!isActive() || initModel != null
                && current_client_model != ZhiChiConstant.client_model_customService) {
            return;
        }
        if (isNewMsg) {
            msgId = System.currentTimeMillis() + "";
            sendNewMsgToHandler(ChatUtils.getLocationModel(msgId, data), handler, ZhiChiConstant.MSG_SEND_STATUS_LOADING);
        } else {
            if (TextUtils.isEmpty(msgId)) {
                return;
            }
            updateMsgToHandler(msgId, handler, ZhiChiConstant.MSG_SEND_STATUS_LOADING);
        }
        final String finalMsgId = msgId;
        zhiChiApi.sendLocation(SobotChatBaseFragment.this, data, initModel.getPartnerid(), initModel.getCid(), new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
                    updateMsgToHandler(finalMsgId, handler, ZhiChiConstant.MSG_SEND_STATUS_ERROR);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(finalMsgId)) {
                        isAboveZero = true;
                        updateMsgToHandler(finalMsgId, handler, ZhiChiConstant.MSG_SEND_STATUS_SUCCESS);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                updateMsgToHandler(finalMsgId, handler, ZhiChiConstant.MSG_SEND_STATUS_ERROR);
            }
        });

    }

    protected void uploadVideo(File videoFile, String snapshotPath, final SobotMsgAdapter messageAdapter) {
        String tmpMsgId = String.valueOf(System.currentTimeMillis());
        LogUtils.i("tmpMsgId:" + tmpMsgId);
        zhiChiApi.addUploadFileTask(true, tmpMsgId, initModel.getPartnerid(), initModel.getCid(), videoFile.getAbsolutePath(), snapshotPath);
        updateUiMessage(messageAdapter, ChatUtils.getUploadVideoModel(getContext(), tmpMsgId, videoFile, snapshotPath));
    }

    /**
     * 文本通知
     *
     * @param id
     * @param msgContent
     * @param handler
     * @param isSendStatus 0 失败  1成功  2 正在发送
     * @param updateStatus
     */
    protected void sendTextMessageToHandler(String id, String msgContent,
                                            Handler handler, int isSendStatus, int updateStatus) {
        ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
        myMessage.setId(id);
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        if (!TextUtils.isEmpty(msgContent)) {
            msgContent = msgContent.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace
                    ("\n", "<br/>").replace("&lt;br/&gt;", "<br/>");
            reply.setMsg(msgContent);
        } else {
            reply.setMsg(msgContent);
        }
        reply.setMsgType(ZhiChiConstant.message_type_text + "");
        myMessage.setAnswer(reply);
        myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
        myMessage.setSendSuccessState(isSendStatus);
        myMessage.setT(Calendar.getInstance().getTime().getTime() + "");
        Message handMyMessage = handler.obtainMessage();
        switch (updateStatus) {
            case SEND_TEXT:
                handMyMessage.what = ZhiChiConstant.hander_send_msg;
                break;
            case UPDATE_TEXT:
                handMyMessage.what = ZhiChiConstant.hander_update_msg_status;
                break;
            case UPDATE_TEXT_VOICE:
                handMyMessage.what = ZhiChiConstant.update_send_data;
                break;
        }

        handMyMessage.obj = myMessage;
        handler.sendMessage(handMyMessage);
    }

    /**
     * 新增消息
     *
     * @param messageData
     * @param handler
     * @param updateStatus ZhiChiConstant.MSG_SEND_STATUS_SUCCESS
     *                     ZhiChiConstant.MSG_SEND_STATUS_LOADING
     *                     ZhiChiConstant.MSG_SEND_STATUS_ERROR
     */
    protected void sendNewMsgToHandler(ZhiChiMessageBase messageData, Handler handler, int updateStatus) {
        if (messageData == null) {
            return;
        }

        Message message = handler.obtainMessage();
        messageData.setSendSuccessState(updateStatus);
        message.what = ZhiChiConstant.hander_send_msg;
        message.obj = messageData;
        handler.sendMessage(message);
    }

    /**
     * 更新消息状态
     *
     * @param id
     * @param handler
     * @param updateStatus ZhiChiConstant.MSG_SEND_STATUS_SUCCESS
     *                     ZhiChiConstant.MSG_SEND_STATUS_LOADING
     *                     ZhiChiConstant.MSG_SEND_STATUS_ERROR
     */
    protected void updateMsgToHandler(String id, Handler handler, int updateStatus) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        ZhiChiMessageBase messageData = new ZhiChiMessageBase();
        messageData.setId(id);
        messageData.setSendSuccessState(updateStatus);
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.hander_update_msg_status;
        message.obj = messageData;
        handler.sendMessage(message);
    }

    /**
     * 发送语音消息
     *
     * @param voiceMsgId
     * @param voiceTimeLongStr
     * @param cid
     * @param uid
     * @param filePath
     * @param handler
     */
    protected void sendVoice(final String voiceMsgId, final String voiceTimeLongStr,
                             String cid, String uid, final String filePath, final Handler handler) {
        if (current_client_model == ZhiChiConstant.client_model_robot) {
            zhiChiApi.sendVoiceToRobot(filePath, uid, cid, initModel.getRobotid(), voiceTimeLongStr, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {
                    if (!isActive()) {
                        return;
                    }
                    LogUtils.i("发送给机器人语音---sobot---" + zhiChiMessage.getMsg());
                    // 语音发送成功
                    String id = System.currentTimeMillis() + "";
                    isAboveZero = true;
                    restartMyTimeTask(handler);
                    if (!TextUtils.isEmpty(zhiChiMessage.getMsg())) {
                        sendTextMessageToHandler(voiceMsgId, zhiChiMessage.getMsg(), handler, 1, UPDATE_TEXT_VOICE);//语音通过服务器转为文字，发送给页面
                    } else {
                        sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 1, UPDATE_VOICE, handler);
                    }

                    ZhiChiMessageBase simpleMessage = zhiChiMessage.getData();
                    if (simpleMessage.getUstatus() == ZhiChiConstant.result_fail_code) {
                        //机器人超时下线
                        customerServiceOffline(initModel, 4);
                    } else {
                        isAboveZero = true;
                        simpleMessage.setId(id);
                        simpleMessage.setSenderName(initModel.getRobotName());
                        simpleMessage.setSender(initModel.getRobotName());
                        simpleMessage.setSenderFace(initModel.getRobotLogo());
                        simpleMessage.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
                        Message message = handler.obtainMessage();
                        message.what = ZhiChiConstant.hander_robot_message;
                        message.obj = simpleMessage;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    if (!isActive()) {
                        return;
                    }
                    LogUtils.i("发送语音error:" + des + "exception:" + e.toString());
                    sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 0, UPDATE_VOICE, handler);
                }

                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {

                }
            });
        } else if (current_client_model == ZhiChiConstant.client_model_customService) {
            LogUtils.i("发送给人工语音---sobot---" + filePath);
            zhiChiApi.sendFile(cid, uid, filePath, voiceTimeLongStr,
                    new ResultCallBack<ZhiChiMessage>() {
                        @Override
                        public void onSuccess(ZhiChiMessage zhiChiMessage) {
                            if (!isActive()) {
                                return;
                            }
                            // 语音发送成功
                            isAboveZero = true;
                            restartMyTimeTask(handler);
                            sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 1, UPDATE_VOICE, handler);
                        }

                        @Override
                        public void onFailure(Exception e, String des) {
                            if (!isActive()) {
                                return;
                            }
                            final Map<String, String> map = new HashMap<>();
                            String cotent = e.toString() + des;
                            map.put("sendHttpCustomServiceMessage", cotent);
                            LogUtils.i2Local(map, LogUtils.LOGTYPE_ERROE);
                            LogUtils.i("发送语音error:" + des + "exception:" + e.toString());
                            sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 0, UPDATE_VOICE, handler);
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {

                        }
                    });
        }

    }

    /**
     * @param voiceMsgId       语音暂时产生唯一标识符
     * @param voiceUrl         语音的地址
     * @param voiceTimeLongStr 语音的时长
     * @param isSendSuccess
     * @param state            发送状态
     * @param handler
     */
    protected void sendVoiceMessageToHandler(String voiceMsgId, String voiceUrl,
                                             String voiceTimeLongStr, int isSendSuccess, int state,
                                             final Handler handler) {

        ZhiChiMessageBase zhichiMessage = new ZhiChiMessageBase();
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        reply.setMsg(voiceUrl);
        reply.setDuration(voiceTimeLongStr);
        zhichiMessage.setT(Calendar.getInstance().getTime().getTime() + "");
        zhichiMessage.setAnswer(reply);
        zhichiMessage.setSenderType(ZhiChiConstant.message_sender_type_send_voice + "");
        zhichiMessage.setId(voiceMsgId);
        zhichiMessage.setSendSuccessState(isSendSuccess);
        // 设置语音的时长的操作

        Message message = handler.obtainMessage();
        if (state == UPDATE_VOICE) {// 更新界面布局
            message.what = ZhiChiConstant.message_type_update_voice;
        } else if (state == CANCEL_VOICE) {
            message.what = ZhiChiConstant.message_type_cancel_voice;
        } else if (state == SEND_VOICE) {
            message.what = ZhiChiConstant.hander_send_msg;
        }

        message.obj = zhichiMessage;
        handler.sendMessage(message);
    }

    /**
     * 重置输入预知
     */
    protected void restartInputListener() {
        stopInputListener();
        startInputListener();
    }

    //开启正在输入的监听
    protected void startInputListener() {
        inputtingListener = new Timer();
        inputTimerTask = new TimerTask() {
            @Override
            public void run() {
                //人工模式并且没有发送的时候
                if (customerState == CustomerState.Online && current_client_model == ZhiChiConstant.client_model_customService && !isSendInput) {
                    //获取对话
                    try {
                        String content = getSendMessageStr();
                        if (!TextUtils.isEmpty(content) && !content.equals(lastInputStr)) {
                            lastInputStr = content;
                            isSendInput = true;
                            //发送接口
                            zhiChiApi.input(initModel.getPartnerid(), content, new StringResultCallBack<CommonModel>() {
                                @Override
                                public void onSuccess(CommonModel result) {
                                    isSendInput = false;
                                }

                                @Override
                                public void onFailure(Exception e, String des) {
                                    isSendInput = false;
                                }
                            });
                        }
                    } catch (Exception e) {
//						e.printStackTrace();
                    }
                }
            }
        };
        // 500ms进行定时任务
        inputtingListener.schedule(inputTimerTask, 0, initModel.getInputTime() * 1000);
    }

    protected void stopInputListener() {
        if (inputtingListener != null) {
            inputtingListener.cancel();
            inputtingListener = null;
        }
    }

    // 设置听筒模式或者是正常模式的转换
    private void initAudioManager() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        _sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        if (_sensorManager != null) {
            mProximiny = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /* 获取当前手机品牌 过滤掉小米手机 */
        try {
            String phoneName = android.os.Build.MODEL.substring(0, 2);
            // LogUtils.i("当前手机品牌是" + phoneName + phoneName.length());
            // 模式的转化
            // 当前传感器距离
            float f_proximiny = event.values[0];
            // LogUtils.i("监听模式的转换：" + f_proximiny + " 听筒的模式："
            // + mProximiny.getMaximumRange());
            if (!phoneName.trim().equals("MI")) {
                if (f_proximiny != 0.0) {
                    audioManager.setSpeakerphoneOn(true);// 打开扬声器
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    // LogUtils.i("监听模式的转换：" + "正常模式");
                } else {
                    audioManager.setSpeakerphoneOn(false);// 关闭扬声器
                    if (getSobotActivity() != null) {
                        getSobotActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    }
                    // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    // LogUtils.i("监听模式的转换：" + "听筒模式");
                }
            }
        } catch (Exception e) {
//			e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 判断用户是否为黑名单
     *
     * @return
     */
    protected boolean isUserBlack() {
        if (initModel != null && "1".equals(initModel.getIsblack())) {
            return true;
        }
        return false;
    }

    /**
     * 重置内存中保存的数据
     */
    protected void clearCache() {
        SobotMsgManager.getInstance(mAppContext).clearAllConfig();
    }

    protected void requestQueryFrom(final String groupId, final String groupName) {
        requestQueryFrom(groupId, groupName);
    }

    /**
     * 检查是否有询前表单，这个方法在转人工时 会首先检查是否需要填写询前表单，
     * 如果有那么将会弹出询前表单填写界面，之后会调用转人工
     *
     * @param groupId
     * @param groupName
     */
    protected void requestQueryFrom(final String groupId, final String groupName, final int transferType, final boolean isCloseInquiryFrom) {
        if (customerState == CustomerState.Queuing || isHasRequestQueryFrom) {
            //如果在排队中就不需要填写询前表单 、或者之前弹过询前表单
            connectCustomerService(groupId, groupName);
            return;
        }
        if (isQueryFroming) {
            return;
        }
        isHasRequestQueryFrom = true;
        isQueryFroming = true;
        zhiChiApi.queryFormConfig(SobotChatBaseFragment.this, initModel.getPartnerid(), new StringResultCallBack<SobotQueryFormModel>() {
            @Override
            public void onSuccess(SobotQueryFormModel sobotQueryFormModel) {
                isQueryFroming = false;
                if (!isActive()) {
                    return;
                }
                if (sobotQueryFormModel.isOpenFlag() && !isCloseInquiryFrom && sobotQueryFormModel.getField() != null && sobotQueryFormModel.getField().size() > 0) {
                    // 打开询前表单
                    Intent intent = new Intent(mAppContext, SobotQueryFromActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPID, groupId);
                    bundle.putString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPNAME, groupName);
                    bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD, sobotQueryFormModel);
                    bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_UID, initModel.getPartnerid());
                    bundle.putInt(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_TRANSFER_TYPE, transferType);
                    intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA, bundle);
                    startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM);
                } else {
                    connectCustomerService(groupId, groupName, transferType);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                isQueryFroming = false;
                if (!isActive()) {
                    return;
                }
                ToastUtil.showToast(mAppContext, des);
            }

        });
    }

    public void remindRobotMessage(final Handler handler, final ZhiChiInitModeBase initModel, final Information info) {
        //true代表结束会话，重新显示机器人提示语
        boolean flag = SharedPreferencesUtil.getBooleanData(mAppContext, ZhiChiConstant.SOBOT_IS_EXIT, false);
        if (initModel == null) {
            return;
        }
        // 修改提醒的信息
        remindRobotMessageTimes = remindRobotMessageTimes + 1;
        if (remindRobotMessageTimes == 1) {
            if ((initModel.getUstatus() == ZhiChiConstant.ustatus_robot) && !flag) {
                processNewTicketMsg(handler);
                return;
            }
            /* 首次的欢迎语 */
            ZhiChiMessageBase robot = new ZhiChiMessageBase();
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();

            if (initModel.isRobotHelloWordFlag()) {
                String robotHolloWord = SharedPreferencesUtil.getStringData(mAppContext, ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, "");
                if (!TextUtils.isEmpty(robotHolloWord) || !TextUtils.isEmpty(initModel.getRobotHelloWord())) {
                    if (!TextUtils.isEmpty(robotHolloWord)) {
                        reply.setMsg(robotHolloWord);
                    } else {
                        String msgHint = initModel.getRobotHelloWord().replace("\n", "<br/>");
                        if (msgHint.startsWith("<br/>")) {
                            msgHint = msgHint.substring(5, msgHint.length());
                        }

                        if (msgHint.endsWith("<br/>")) {
                            msgHint = msgHint.substring(0, msgHint.length() - 5);
                        }
                        reply.setMsg(msgHint);
                    }
                    reply.setMsgType(ZhiChiConstant.message_type_text + "");
                    robot.setAnswer(reply);
                    robot.setSenderFace(initModel.getRobotLogo());
                    robot.setSender(initModel.getRobotName());
                    robot.setSenderType(ZhiChiConstant.message_sender_type_robot_welcome_msg + "");
                    robot.setSenderName(initModel.getRobotName());
                    Message message = handler.obtainMessage();
                    message.what = ZhiChiConstant.hander_robot_message;
                    message.obj = robot;
                    handler.sendMessage(message);
                }
            }
            //获取机器人带引导与的欢迎语
            if (1 == initModel.getGuideFlag()) {

                zhiChiApi.robotGuide(SobotChatBaseFragment.this, initModel.getPartnerid(), initModel.getRobotid(), new
                        StringResultCallBack<ZhiChiMessageBase>() {
                            @Override
                            public void onSuccess(ZhiChiMessageBase robot) {
                                if (!isActive()) {
                                    return;
                                }
                                if (current_client_model == ZhiChiConstant.client_model_robot) {
                                    robot.setSenderFace(initModel.getRobotLogo());
                                    robot.setSenderType(ZhiChiConstant.message_sender_type_robot_guide + "");
                                    Message message = handler.obtainMessage();
                                    message.what = ZhiChiConstant.hander_robot_message;
                                    message.obj = robot;
                                    handler.sendMessage(message);

                                    questionRecommend(handler, initModel, info);
                                    processAutoSendMsg(info);
                                    processNewTicketMsg(handler);
                                }
                            }

                            @Override
                            public void onFailure(Exception e, String des) {
                            }
                        });
            } else {
                questionRecommend(handler, initModel, info);
                processAutoSendMsg(info);
                processNewTicketMsg(handler);
            }
        }
    }

    /**
     * 获取用户是否有新留言回复
     */
    protected void processNewTicketMsg(final Handler handler) {
        if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_open
                && !TextUtils.isEmpty(initModel.getCustomerId())) {
            isRemindTicketInfo = true;
            //留言开关打开并且 customerId不为空时获取最新的工单信息
            zhiChiApi.checkUserTicketInfo(SobotChatBaseFragment.this, initModel.getPartnerid(), initModel.getCompanyId(), initModel.getCustomerId(), new StringResultCallBack<SobotUserTicketInfoFlag>() {
                @Override
                public void onSuccess(SobotUserTicketInfoFlag data) {
                    if (data.isExistFlag()) {
                        ZhiChiMessageBase base = new ZhiChiMessageBase();

                        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

                        ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
                        reply1.setRemindType(ZhiChiConstant.sobot_remind_type_simple_tip);
                        reply1.setMsg("<font color='#ffacb5c4'>" + getResString("sobot_new_ticket_info") + " </font>" + " <a href='sobot:SobotTicketInfo'  target='_blank' >" + getResString("sobot_new_ticket_info_update") + "</a> ");
                        base.setAnswer(reply1);
                        Message message = handler.obtainMessage();
                        message.what = ZhiChiConstant.hander_send_msg;
                        message.obj = base;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        }
    }

    protected void processAutoSendMsg(final Information info) {
        SobotAutoSendMsgMode autoSendMsgMode = info.getAutoSendMsgMode();
        if (TextUtils.isEmpty(autoSendMsgMode.getContent())) {
            return;
        }
        if (current_client_model == ZhiChiConstant.client_model_robot) {
            if (autoSendMsgMode == SobotAutoSendMsgMode.SendToRobot
                    || autoSendMsgMode == SobotAutoSendMsgMode.SendToAll) {
                sendMsg(autoSendMsgMode.getContent());
            }
        } else if (current_client_model == ZhiChiConstant.client_model_customService) {
            if ((autoSendMsgMode == SobotAutoSendMsgMode.SendToOperator
                    || autoSendMsgMode == SobotAutoSendMsgMode.SendToAll) && customerState == CustomerState.Online) {
                sendMsg(autoSendMsgMode.getContent());
            }
        }
    }

    private void questionRecommend(final Handler handler, final ZhiChiInitModeBase initModel, final Information info) {
        if (info.getMargs() == null || info.getMargs().size() == 0) {
            return;
        }
        zhiChiApi.questionRecommend(SobotChatBaseFragment.this, initModel.getPartnerid(), info.getMargs(), new StringResultCallBack<SobotQuestionRecommend>() {
            @Override
            public void onSuccess(SobotQuestionRecommend data) {
                if (!isActive()) {
                    return;
                }
                if (data != null && current_client_model == ZhiChiConstant.client_model_robot) {
                    ZhiChiMessageBase robot = ChatUtils.getQuestionRecommendData(initModel, data);
                    Message message = handler.obtainMessage();
                    message.what = ZhiChiConstant.hander_robot_message;
                    message.obj = robot;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
            }
        });
    }

    protected View.OnClickListener mLableClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SobotOption.hyperlinkListener != null) {
                SobotOption.hyperlinkListener.onUrlClick(v.getTag() + "");
                    return;
            }
            if (SobotOption.newHyperlinkListener != null) {
                //如果返回true,拦截;false 不拦截
                boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(v.getTag() + "");
                if (isIntercept) {
                    return;
                }
            }

            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", v.getTag() + "");
            getSobotActivity().startActivity(intent);
        }
    };

    //-------------以下由子类实现-----------------------
    protected abstract String getSendMessageStr();

    protected void connectCustomerService(String groupId, String groupName) {
        connectCustomerService(groupId, groupName, 0);
    }

    protected void connectCustomerService(String groupId, String groupName, boolean isShowTips) {
        connectCustomerService(groupId, groupName, null, null, isShowTips, 0);
    }

    protected void connectCustomerService(String groupId, String groupName, final String keyword, final String keywordId, final boolean isShowTips) {
        connectCustomerService(groupId, groupName, keyword, keywordId, isShowTips, 0);
    }

    protected void connectCustomerService(String groupId, String groupName, int transferType) {
        connectCustomerService(groupId, groupName, null, null, true, transferType);
    }

    protected void connectCustomerService(String groupId, String groupName, String keyword, String keywordId, boolean isShowTips, int transferType) {
    }

    protected void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {
    }

    protected void sendMsg(String content) {
    }

}
