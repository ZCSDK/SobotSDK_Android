package com.sobot.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sobot.chat.activity.SobotConsultationListActivity;
import com.sobot.chat.activity.SobotHelpCenterActivity;
import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.enumtype.SobotChatAvatarDisplayMode;
import com.sobot.chat.api.enumtype.SobotChatStatusMode;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotLeaveMsgConfig;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.api.model.SobotTransferOperatorParam;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.conversation.SobotChatActivity;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.HyperlinkListener;
import com.sobot.chat.listener.NewHyperlinkListener;
import com.sobot.chat.listener.SobotChatStatusListener;
import com.sobot.chat.listener.SobotLeaveMsgListener;
import com.sobot.chat.listener.SobotOrderCardListener;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCache;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.StServiceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sobot.chat.presenter.StPostMsgPresenter.INTENT_KEY_CONFIG;
import static com.sobot.chat.presenter.StPostMsgPresenter.INTENT_KEY_UID;

/**
 * SobotChatApi接口输出类
 */
public class SobotApi {

    private static String Tag = SobotApi.class.getSimpleName();

    /**
     * 初始化sdk
     *
     * @param context 上下文  必填
     * @param appkey  用户的appkey  必填 如果是平台用户需要传总公司的appkey
     * @param uid     用户的唯一标识不能传一样的值
     */
    public static void initSobotSDK(final Context context, final String appkey, final String uid) {
        if (context == null || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "initSobotSDK  参数为空 context:" + context + "  appkey:" + appkey);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SobotMsgManager.getInstance(context).initSobotSDK(context, appkey, uid);
            }
        }).start();

    }

    /**
     * 初始化平台
     *
     * @param context           Context 对象
     * @param platformUnionCode 平台标识
     * @param platformSecretkey 平台标识 秘钥
     */
    public static void initPlatformUnion(Context context, String platformUnionCode, String platformSecretkey) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, platformUnionCode);
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_PLATFORM_KEY, platformSecretkey);
    }

    /**
     * 打开客服界面
     *
     * @param context     上下文对象
     * @param information 接入参数
     */
    public static void startSobotChat(Context context, Information information) {
        if (information == null || context == null) {
            Log.e(Tag, "Information is Null!");
            return;
        }
        boolean initSdk = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
        if (!initSdk) {
            Log.e(Tag, "请在Application中调用【SobotApi.initSobotSDK()】来初始化SDK!");
            return;
        }
        Intent intent = new Intent(context, SobotChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开帮助中心
     *
     * @param context     上下文对象
     * @param information 接入参数
     */
    public static void openSobotHelpCenter(Context context, Information information) {
        if (information == null || context == null) {
            Log.e(Tag, "Information is Null!");
            return;
        }
        boolean initSdk = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
        if (!initSdk) {
            Log.e(Tag, "请在Application中调用【SobotApi.initSobotSDK()】来初始化SDK!");
            return;
        }
        Intent intent = new Intent(context, SobotHelpCenterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开资讯列表界面
     *
     * @param context 上下文对象
     * @param uid     用户唯一标识 与information中传的uid一致
     */
    public static void startMsgCenter(Context context, String uid) {
        Intent intent = new Intent(context, SobotConsultationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, uid);
        context.startActivity(intent);
    }

    /**
     * 初始化消息链接
     *
     * @param uid     用户唯一标识 与information中传的uid一致
     * @param context 上下文对象
     */
    public static void initSobotChannel(Context context, String uid) {
        LogUtils.i("initSobotChannel uid=" + uid);
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        SharedPreferencesUtil.removeKey(context, Const.SOBOT_WAYHTTP);
        SobotMsgManager.getInstance(context).getZhiChiApi().reconnectChannel();
        Intent intent = new Intent(context, SobotSessionServer.class);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, uid);
        StServiceUtils.safeStartService(context, intent);
    }

    /**
     * 获取当前未读消息数
     *
     * @param context
     * @param uid     用户唯一标识 与information中传的uid一致
     * @return
     */
    public static int getUnreadMsg(Context context, String uid) {
        if (context == null) {
            return 0;
        } else {
            int count = 0;
            List<SobotMsgCenterModel> msgCenterList = getMsgCenterList(context, uid);
            if (msgCenterList != null) {
                for (int i = 0; i < msgCenterList.size(); i++) {
                    count += msgCenterList.get(i).getUnreadCount();
                }
            }
            return count;
        }
    }

    /**
     * 断开与智齿服务器的链接
     *
     * @param context 上下文对象
     */
    public static void disSobotChannel(Context context) {
        if (context == null) {
            return;
        }
        if (SobotOption.sobotChatStatusListener != null) {
            //修改聊天状态为离线
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectOffline);
        }
        SobotMsgManager.getInstance(context).getZhiChiApi().disconnChannel();
        SobotMsgManager.getInstance(context).clearAllConfig();
    }

    /**
     * 退出客服，用于用户退出登录时调用
     *
     * @param context 上下文对象
     */
    public static void exitSobotChat(final Context context) {
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_IS_EXIT, true);
        if (context == null) {
            return;
        }
        try {
            disSobotChannel(context);
            context.stopService(new Intent(context, SobotSessionServer.class));

            String cid = SharedPreferencesUtil.getStringData(context, Const.SOBOT_CID, "");
            String uid = SharedPreferencesUtil.getStringData(context, Const.SOBOT_UID, "");
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_WSLINKBAK);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_WSLINKDEFAULT);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_UID);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_CID);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_PUID);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_APPKEY);

            if (!TextUtils.isEmpty(cid) && !TextUtils.isEmpty(uid)) {
                ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
                zhiChiApi.out(cid, uid, new StringResultCallBack<CommonModel>() {
                    @Override
                    public void onSuccess(CommonModel result) {
                        LogUtils.i("下线成功");
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                    }
                });
            }
        } catch (Exception e) {
//			e.printStackTrace();
        }
    }

    /**
     * 设置是否开启消息提醒   默认不提醒
     *
     * @param context
     * @param flag
     * @param smallIcon 小图标的id 设置通知栏中的小图片，尺寸一般建议在24×24
     * @param largeIcon 大图标的id
     */
    public static void setNotificationFlag(Context context, boolean flag, int smallIcon, int largeIcon) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveBooleanData(context, Const.SOBOT_NOTIFICATION_FLAG, flag);
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_SMALL_ICON, smallIcon);
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_LARGE_ICON, largeIcon);
    }

    /**
     * 清除所有通知
     *
     * @param context
     */
    public static void cancleAllNotification(Context context) {
        if (context == null) {
            return;
        }
        NotificationUtils.cancleAllNotification(context);
    }

    /**
     * 设置超链接的点击事件监听
     *
     * @param hyperlinkListener
     */
    public static void setHyperlinkListener(HyperlinkListener hyperlinkListener) {
        SobotOption.hyperlinkListener = hyperlinkListener;
    }

    /**
     * 设置超链接的点击事件监听
     * 根据返回值用户可分开动态设置是否拦截，举例 监听到有订单编号，返回true 拦截；商品
     *
     * @param newHyperlinkListener
     */
    public static void setNewHyperlinkListener(NewHyperlinkListener newHyperlinkListener) {
        SobotOption.newHyperlinkListener = newHyperlinkListener;
    }


    /**
     * 设置当前聊天状态的监听
     *
     * @param chatStatusListener
     */
    public static void setChatStatusListener(SobotChatStatusListener chatStatusListener) {
        SobotOption.sobotChatStatusListener = chatStatusListener;
    }

    /**
     * 设置订单卡片的点击事件监听
     *
     * @deprecated Use {@link #setHyperlinkListener(HyperlinkListener)} instead.
     */
    @Deprecated
    public static void setOrderCardListener(SobotOrderCardListener orderCardListener) {
        SobotOption.orderCardListener = orderCardListener;
    }

    /**
     * 设置跳转到留言页的监听
     *
     * @param sobotLeaveMsgListener
     */
    public static void setSobotLeaveMsgListener(SobotLeaveMsgListener sobotLeaveMsgListener) {
        SobotOption.sobotLeaveMsgListener = sobotLeaveMsgListener;
    }


    /**
     * 设置聊天界面标题显示模式
     *
     * @param context      上下文对象
     * @param title_type   titile的显示模式
     *                     SobotChatTitleDisplayMode.Default:显示客服昵称(默认)
     *                     SobotChatTitleDisplayMode.ShowFixedText:显示固定文本
     *                     SobotChatTitleDisplayMode.ShowCompanyName:显示console设置的企业名称
     * @param custom_title 如果需要显示固定文本，需要传入此参数，其他模式可以不传
     * @param isShowTitle  是否显示标题
     */
    public static void setChatTitleDisplayMode(Context context, SobotChatTitleDisplayMode title_type, String custom_title, boolean isShowTitle) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_MODE,
                title_type.getValue());
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_CONTENT,
                custom_title);
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CHAT_TITLE_IS_SHOW,
                isShowTitle);
    }


    /**
     * 设置聊天界面头像显示模式
     *
     * @param context           上下文对象
     * @param avatar_type       titile的显示模式
     *                          SobotChatAvatarDisplayMode.Default:显示客服头像(默认)
     *                          SobotChatAvatarDisplayMode.ShowFixedAvatar:显示固定头像
     *                          SobotChatAvatarDisplayMode.ShowCompanyAvatar:显示console设置的企业名称
     * @param custom_avatar_url 如果需要显示固定头像，需要传入此参数，其他模式可以不传
     * @param isShowAvatar      是否显示头像
     */
    public static void setChatAvatarDisplayMode(Context context, SobotChatAvatarDisplayMode avatar_type, String custom_avatar_url, boolean isShowAvatar) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_CHAT_AVATAR_DISPLAY_MODE,
                avatar_type.getValue());
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_CHAT_AVATAR_DISPLAY_CONTENT,
                custom_avatar_url);
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CHAT_AVATAR_IS_SHOW,
                isShowAvatar);
    }

    /**
     * 控制显示历史聊天记录的时间范围
     *
     * @param time 查询时间(例:100-表示从现在起前100分钟的会话)
     * @deprecated Use {@link #setScope_time(Context, long)} instead.
     */
    @Deprecated
    public static void hideHistoryMsg(Context context, long time) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveLongData(context, ZhiChiConstant.SOBOT_SCOPE_TIME,
                time);
    }

    /**
     * 控制显示历史聊天记录的时间范围
     *
     * @param time 查询时间(例:100-表示从现在起前100分钟的会话)
     */
    public static void setScope_time(Context context, long time) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveLongData(context, ZhiChiConstant.SOBOT_SCOPE_TIME,
                time);
    }

    /**
     * 配置用户提交人工满意度评价后释放会话
     *
     * @param context
     * @param flag
     */
    public static void setEvaluationCompletedExit(Context context, boolean flag) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT, flag);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服欢迎语
     * @deprecated Use {@link #setAdmin_Hello_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomAdminHelloWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_HELLO_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服欢迎语
     */
    public static void setAdmin_Hello_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_HELLO_WORD, content);
    }


    /**
     * @param context Context 对象
     * @param content 自定义机器人欢迎语
     * @deprecated Use {@link #setRobot_Hello_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomRobotHelloWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义机器人欢迎语
     */
    public static void setRobot_Hello_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时提示语
     * @deprecated Use {@link #setUser_Tip_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomUserTipWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时提示语
     */
    public static void setUser_Tip_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服超时提示语
     * @deprecated Use {@link #setAdmin_Tip_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomAdminTipWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服超时提示语
     */
    public static void setAdmin_Tip_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服不在线的说辞
     * @deprecated Use {@link #setAdmin_Offline_Title(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomAdminNonelineTitle(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_OFFLINE_TITLE, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服不在线的说辞
     */
    public static void setAdmin_Offline_Title(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_OFFLINE_TITLE, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时下线提示语
     * @deprecated Use {@link #setUser_Out_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomUserOutWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_OUT_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时下线提示语
     */
    public static void setUser_Out_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_OUT_WORD, content);
    }

    /**
     * 获取消息中心数据
     *
     * @param context
     * @param uid     用户唯一标识 与information中传的uid一致
     * @return
     */
    public static List<SobotMsgCenterModel> getMsgCenterList(Context context, String uid) {
        if (context == null) {
            return null;
        }
        uid = uid == null ? "" : uid;
        SobotCache sobotCache = SobotCache.get(context);
        ArrayList<String> msgDatas = (ArrayList<String>) sobotCache.getAsObject(SobotMsgManager.getMsgCenterListKey(uid));
        List<SobotMsgCenterModel> datas = new ArrayList<SobotMsgCenterModel>();
        if (msgDatas != null && msgDatas.size() > 0) {
            datas.clear();
            for (String appkey : msgDatas) {
                SobotMsgCenterModel data = (SobotMsgCenterModel) sobotCache.getAsObject(SobotMsgManager.getMsgCenterDataKey(appkey, uid));
                if (data != null) {
                    datas.add(data);
                }
            }
        }
        return datas;
    }

    /**
     * 清除所有消息中心消息
     *
     * @param context
     * @param uid     用户唯一标识 与information中传的uid一致
     */
    public static void clearMsgCenterList(Context context, String uid) {
        if (context == null) {
            return;
        }
        uid = uid == null ? "" : uid;
        SobotCache sobotCache = SobotCache.get(context);
        sobotCache.remove(SobotMsgManager.getMsgCenterListKey(uid));
    }

    /**
     * 清除所有未读消息计数
     *
     * @param context
     * @param uid     用户唯一标识 与information中传的uid一致
     */
    public static void clearAllUnreadCount(Context context, String uid) {
        if (context == null) {
            return;
        }
        SobotMsgManager.getInstance(context).clearAllUnreadCount(context, uid);
    }

    /**
     * @param context        Context 对象
     * @param flow_companyid 设置溢出公司id
     * @deprecated Use {@link #setFlow_Company_Id(Context, String)} instead.
     */
    @Deprecated
    public static void setFlowCompanyId(Context context, String flow_companyid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_COMPANYID, flow_companyid);
    }

    /**
     * @param context        Context 对象
     * @param flow_companyid 设置溢出公司id
     */
    public static void setFlow_Company_Id(Context context, String flow_companyid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_COMPANYID, flow_companyid);
    }

    /**
     * @param context   Context 对象
     * @param flow_type flowType-是否溢出到主商户 0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,默认不溢出
     * @deprecated Use {@link #setFlow_Type(Context, String)} instead.
     */
    @Deprecated
    public static void setFlowType(Context context, String flow_type) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_TYPE, flow_type);
    }

    /**
     * @param context   Context 对象
     * @param flow_type @param content flowType -是否溢出到主商户 0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,默认不溢出
     */
    public static void setFlow_Type(Context context, String flow_type) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_TYPE, flow_type);
    }

    /**
     * @param context      Context 对象
     * @param flow_groupid 转人工溢出公司技能组id
     * @deprecated Use {@link #setFlow_GroupId(Context, String)} instead.
     */
    @Deprecated
    public static void setFlowGroupId(Context context, String flow_groupid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_GROUPID, flow_groupid);
    }

    /**
     * @param context      Context 对象
     * @param flow_groupid 转人工溢出公司技能组id
     */
    public static void setFlow_GroupId(Context context, String flow_groupid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_GROUPID, flow_groupid);
    }

    /**
     * 发送地址信息
     *
     * @param context
     * @param locationData 地址对象的值
     */
    public static void sendLocation(Context context, SobotLocationModel locationData) {
        if (context == null || locationData == null) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_LOCATION);
        intent.putExtra(ZhiChiConstant.SOBOT_LOCATION_DATA, locationData);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送文本类信息
     *
     * @param context
     * @param content 文本内容
     */
    public static void sendTextMsg(Context context, String content) {
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送商品卡片消息
     *
     * @param context
     * @param content 卡片信息
     */
    public static void sendCardMsg(Context context, ConsultingContent content) {
        if (context == null || content == null) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_CARD);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送订单卡片消息
     *
     * @param context
     * @param content 卡片信息
     */
    public static void sendOrderCardMsg(Context context, OrderCardContentModel content) {
        if (context == null || content == null) {
            return;
        }
        if (TextUtils.isEmpty(content.getOrderCode())) {
            ToastUtil.showCustomToast(context, ResourceUtils.getResString(context, "sobot_order_not_empty"), Toast.LENGTH_LONG);
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_ORDER_CARD);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 外部主动调用转人工
     *
     * @param context
     * @param param   转人工参数
     *                String groupId       技能组id
     *                String groupName     技能组名称
     *                String keyword       关键词转人工的关键词
     *                String keywordId     关键词转人工的关键词ID
     *                boolean isShowTips   是否显示转人工提示
     *                ConsultingContent consultingContent
     *                商品信息
     */
    public static void transfer2Operator(Context context, SobotTransferOperatorParam param) {
        if (context == null || param == null) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_TRASNFER_TO_OPERATOR);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, param);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 判断当前用户是否正在与当前商户客服聊天
     *
     * @param context 上下文对象
     * @param appkey  当前商户的appkey
     * @return true 表示正在与当前商户客服聊天
     * false 表示当前没有与所选商户客服聊天
     */
    public static boolean isActiveOperator(Context context, String appkey) {
        return SobotMsgManager.getInstance(context.getApplicationContext()).isActiveOperator(appkey);
    }


    /**
     * 跳转到留言界面
     *
     * @param context          上下文  必填
     * @param info             用户的appkey  必填 如果是平台用户需要传总公司的appkey
     * @param isOnlyShowTicket true只显示留言记录界面，false 请您留言和留言记录界面都显示
     */
    public static void startToPostMsgActivty(final Context context, final Information info, final boolean isOnlyShowTicket) {
        if (context == null || TextUtils.isEmpty(info.getAppkey())) {
            Log.e(Tag, "initSobotSDK  参数为空 context:" + context + "  appkey:" + info.getAppkey() + "  uid:" + info.getUid());
            return;
        }
        SobotMsgManager.getInstance(context).getZhiChiApi()
                .sobotInit(context, info, new StringResultCallBack<ZhiChiInitModeBase>() {
                    @Override
                    public void onSuccess(ZhiChiInitModeBase initModel) {
                        List<SobotFieldModel> sobotFieldModels = new ArrayList<>();
                        if (info.getLeaveCusFieldMap() != null&&info.getLeaveCusFieldMap().size()>0) {
                            for (String key :
                                    info.getLeaveCusFieldMap().keySet()) {
                                SobotFieldModel sobotFieldModel = new SobotFieldModel();
                                SobotCusFieldConfig sobotCusFieldConfig = new SobotCusFieldConfig();
                                sobotCusFieldConfig.setFieldId(key);
                                sobotCusFieldConfig.setValue(info.getLeaveCusFieldMap().get(key));
                                sobotFieldModel.setCusFieldConfig(sobotCusFieldConfig);
                                sobotFieldModels.add(sobotFieldModel);
                            }
                        }
                        SobotLeaveMsgConfig config = new SobotLeaveMsgConfig();
                        config.setEmailFlag(initModel.isEmailFlag());
                        config.setEmailShowFlag(initModel.isEmailShowFlag());
                        config.setEnclosureFlag(initModel.isEnclosureFlag());
                        config.setEnclosureShowFlag(initModel.isEnclosureShowFlag());
                        config.setTelFlag(initModel.isTelFlag());
                        config.setTelShowFlag(initModel.isTelShowFlag());
                        config.setTicketStartWay(initModel.isTicketStartWay());
                        config.setTicketShowFlag(initModel.isTicketShowFlag());
                        config.setCompanyId(initModel.getCompanyId());
                        if (!TextUtils.isEmpty(info.getLeaveMsgTemplateContent())) {
                            config.setMsgTmp(info.getLeaveMsgTemplateContent());
                        } else {
                            config.setMsgTmp(initModel.getMsgTmp());
                        }
                        if (!TextUtils.isEmpty(info.getLeaveMsgGuideContent())) {
                            config.setMsgTxt(info.getLeaveMsgGuideContent());
                        } else {
                            config.setMsgTxt(initModel.getMsgTxt());
                        }
                        Intent intent = new Intent(context, SobotPostMsgActivity.class);
                        intent.putExtra(INTENT_KEY_UID, initModel.getPartnerid());
                        intent.putExtra(INTENT_KEY_CONFIG, config);
                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                        intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getLeaveMsgGroupId());
                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUS_FIELDS, (Serializable) sobotFieldModels);
                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, isOnlyShowTicket);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e, String des) {

                    }
                });

    }

    /**
     * 获取开关状态
     *
     * @param markConfig 开关名
     * @return
     * @see MarkConfig 取值
     */
    public static boolean getSwitchMarkStatus(int markConfig) {
        if ((markConfig & (markConfig - 1)) == 0)
            return MarkConfig.getON_OFF(markConfig);
        else {
            throw new Resources.NotFoundException("markConfig 必须为2的指数次幂");
        }
    }

    /**
     * 设置开关状态
     *
     * @param markConfig 开关名 必须为 2 的非负数整数次幂
     * @param isOn
     * @see MarkConfig 取值
     */
    public static void setSwitchMarkStatus(int markConfig, boolean isOn) {
        if ((markConfig & (markConfig - 1)) == 0)
            MarkConfig.setON_OFF(markConfig, isOn);
        else {
            throw new Resources.NotFoundException("markConfig 必须为2的指数次幂");
        }
    }

}
