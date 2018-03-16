package com.sobot.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sobot.chat.activity.SobotConsultationListActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.conversation.SobotChatActivity;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.HyperlinkListener;
import com.sobot.chat.listener.SobotLeaveMsgListener;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCache;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SobotChatApi接口输出类
 */
public class SobotApi {

	private static String Tag = SobotApi.class.getSimpleName();

	/**
	 * 初始化sdk
	 * @param context 上下文  必填
	 * @param appkey  用户的appkey  必填 如果是平台用户需要传总公司的appkey
	 * @param uid     用户的唯一标识不能传一样的值
	 */
	public static void initSobotSDK(Context context,String appkey,String uid){
		if (context == null || TextUtils.isEmpty(appkey)) {
			Log.e(Tag,"initSobotSDK  参数为空 context:" + context + "  appkey:" + appkey);
			return;
		}
		SobotMsgManager.getInstance(context).initSobotSDK(context,appkey,uid);
	}

	/**
	 * 初始化平台
	 *
	 * @param context           Context 对象
	 * @param platformUnionCode 平台标识
	 */
	public static void initPlatformUnion(Context context, String platformUnionCode) {
		if (context == null) {
			return;
		}
		SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, platformUnionCode);
	}

	/**
	 * 打开客服界面
	 * @param context 上下文对象
	 * @param information 接入参数
     */
	public static void startSobotChat(Context context, Information information) {
		if (information == null || context == null){
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
		bundle.putSerializable("info", information);
		intent.putExtra("informationBundle", bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 打开资讯列表界面
	 *
	 * @param context 上下文对象
	 * @param uid 用户唯一标识 与information中传的uid一致
	 */
	public static void startMsgCenter(Context context,String uid) {
		Intent intent = new Intent(context, SobotConsultationListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID,uid);
		context.startActivity(intent);
	}

	/**
	 * 初始化消息链接
	 * @param uid 用户唯一标识 与information中传的uid一致
	 * @param context 上下文对象
	 */
	public static void initSobotChannel(Context context,String uid) {
		if (context == null) {
			return;
		}
		context = context.getApplicationContext();
		SharedPreferencesUtil.removeKey(context, Const.SOBOT_WAYHTTP);
		SobotMsgManager.getInstance(context).getZhiChiApi().reconnectChannel();
		Intent intent = new Intent(context, SobotSessionServer.class);
		intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, uid);
		context.startService(intent);
	}

	/**
	 * 获取当前未读消息数
	 * @param context
	 * @param uid 用户唯一标识 与information中传的uid一致
	 * @return
     */
	public static int getUnreadMsg(Context context,String uid){
		if (context == null){
			return  0;
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
	 * @param context 上下文对象
	 */
	public static void disSobotChannel(Context context){
		if (context == null){
			return;
		}
		SobotMsgManager.getInstance(context).getZhiChiApi().disconnChannel();
		SobotMsgManager.getInstance(context).clearAllConfig();
	}

	/**
	 * 退出客服，用于用户退出登录时调用
	 * @param context 上下文对象
     */
	public static void exitSobotChat(final Context context){
		if (context == null){
			return;
		}
		try {
			disSobotChannel(context);
			context.stopService(new Intent(context, SobotSessionServer.class));

			String cid = SharedPreferencesUtil.getStringData(context,Const.SOBOT_CID,"");
			String uid = SharedPreferencesUtil.getStringData(context,Const.SOBOT_UID,"");
			SharedPreferencesUtil.removeKey(context,Const.SOBOT_WSLINKBAK);
			SharedPreferencesUtil.removeKey(context,Const.SOBOT_WSLINKDEFAULT);
			SharedPreferencesUtil.removeKey(context,Const.SOBOT_UID);
			SharedPreferencesUtil.removeKey(context,Const.SOBOT_CID);
			SharedPreferencesUtil.removeKey(context,Const.SOBOT_PUID);
			SharedPreferencesUtil.removeKey(context,Const.SOBOT_APPKEY);

			if (!TextUtils.isEmpty(cid) && !TextUtils.isEmpty(uid)){
                ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
                zhiChiApi.out(cid, uid,	new StringResultCallBack<CommonModel>() {
                    @Override
                    public void onSuccess(CommonModel result) {
                        LogUtils.i("下线成功");
                    }

                    @Override
                    public void onFailure(Exception e, String des) {}
                });
            }
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	/**
	 * 设置是否开启消息提醒   默认不提醒
	 * @param context
	 * @param flag
	 * @param smallIcon 小图标的id 设置通知栏中的小图片，尺寸一般建议在24×24
	 * @param largeIcon 大图标的id
     */
	public static void setNotificationFlag(Context context,boolean flag,int smallIcon,int largeIcon){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveBooleanData(context,Const.SOBOT_NOTIFICATION_FLAG,flag);
		SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_SMALL_ICON, smallIcon);
		SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_LARGE_ICON, largeIcon);
	}

	/**
	 * 清除所有通知
	 * @param context
     */
	public static void cancleAllNotification(Context context){
		if (context == null){
			return;
		}
		NotificationUtils.cancleAllNotification(context);
	}

	/**
	 * 设置超链接的点击事件监听
	 * @param hyperlinkListener
     */
	public static void setHyperlinkListener(HyperlinkListener hyperlinkListener){
		SobotOption.hyperlinkListener = hyperlinkListener;
	}

	/**
	 *  设置跳转到留言页的监听
	 * @param sobotLeaveMsgListener
	 */
	public static void setSobotLeaveMsgListener(SobotLeaveMsgListener sobotLeaveMsgListener){
		SobotOption.sobotLeaveMsgListener = sobotLeaveMsgListener;
	}

	/**
	 * 设置聊天界面标题显示模式
	 * @param context 上下文对象
	 * @param mode titile的显示模式
	 *              SobotChatTitleDisplayMode.Default:显示客服昵称(默认)
	 *              SobotChatTitleDisplayMode.ShowFixedText:显示固定文本
	 *              SobotChatTitleDisplayMode.ShowCompanyName:显示console设置的企业名称
	 * @param content 如果需要显示固定文本，需要传入此参数，其他模式可以不传
     */
	public static void setChatTitleDisplayMode(Context context, SobotChatTitleDisplayMode mode, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveIntData(context,ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_MODE,
				mode.getValue());
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_CONTENT,
				content);
	}

	/**
	 * 控制显示历史聊天记录的时间范围
	 * @param time  查询时间(例:100-表示从现在起前100分钟的会话)
     */
	public static void hideHistoryMsg(Context context,long time){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveLongData(context,ZhiChiConstant.SOBOT_CHAT_HIDE_HISTORYMSG_TIME,
				time);
	}

	/**
	 * 配置用户提交人工满意度评价后释放会话
	 * @param context
	 * @param flag
	 */
	public static void setEvaluationCompletedExit(Context context,boolean flag){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveBooleanData(context,ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT, flag);
	}

	/**
	 *
	 * @param context		Context 对象
	 * @param content	自定义客服欢迎语
	 */
	public static void setCustomAdminHelloWord(Context context, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CUSTOMADMINHELLOWORD, content);
	}

	/**
	 *
	 * @param context		Context 对象
	 * @param content	自定义机器人欢迎语
	 */
	public static void setCustomRobotHelloWord(Context context, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CUSTOMROBOTHELLOWORD, content);
	}

	/**
	 *
	 * @param context		Context 对象
	 * @param content	自定义用户超时提示语
	 */
	public static void setCustomUserTipWord(Context context, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CUSTOMUSERTIPWORD, content);
	}

	/**
	 *
	 * @param context		Context 对象
	 * @param content	自定义客服超时提示语
	 */
	public static void setCustomAdminTipWord(Context context, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CUSTOMADMINTIPWORD, content);
	}

	/**
	 *
	 * @param context		Context 对象
	 * @param content	自定义客服不在线的说辞
	 */
	public static void setCustomAdminNonelineTitle(Context context, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CUSTOMADMINNONELINETITLE, content);
	}

	/**
	 *
	 * @param context		Context 对象
	 * @param content	自定义用户超时下线提示语
	 */
	public static void setCustomUserOutWord(Context context, String content){
		if (context == null){
			return;
		}
		SharedPreferencesUtil.saveStringData(context,ZhiChiConstant.SOBOT_CUSTOMUSEROUTWORD, content);
	}

	/**
	 * 获取消息中心数据
	 *
	 * @param context
	 * @param uid 用户唯一标识 与information中传的uid一致
	 * @return
	 */
	public static List<SobotMsgCenterModel> getMsgCenterList(Context context,String uid) {
		if (context == null) {
			return null;
		}
		uid = uid == null?"":uid;
		SobotCache sobotCache = SobotCache.get(context);
		HashMap<String, SobotMsgCenterModel> msg_center_list = (HashMap<String, SobotMsgCenterModel>) sobotCache.getAsObject(uid+"sobot_msg_center_list");
		List<SobotMsgCenterModel> datas = new ArrayList<SobotMsgCenterModel>();
		if (msg_center_list != null && msg_center_list.size() > 0) {
			datas.clear();
			Iterator iter = msg_center_list.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry next = (Map.Entry) iter.next();
				datas.add((SobotMsgCenterModel) next.getValue());
			}
		}
		return datas;
	}

	/**
	 * 清除所有消息中心消息
	 * @param context
	 * @param uid 用户唯一标识 与information中传的uid一致
	 */
	public static void clearMsgCenterList(Context context,String uid) {
		if (context == null) {
			return;
		}
		uid = uid == null?"":uid;
		SobotCache sobotCache = SobotCache.get(context);
		sobotCache.remove(uid+"sobot_msg_center_list");
	}

	/**
	 * 清除消息中心单一消息
	 * @param context
	 * @param appId appkey
	 * @param uid 用户唯一标识 与information中传的uid一致
	 */
	public static void clearMsgCenter(Context context, String appId,String uid) {
		if (context == null || TextUtils.isEmpty(appId)) {
			return;
		}
		uid = uid == null?"":uid;
		SobotCache sobotCache = SobotCache.get(context);
		HashMap<String, SobotMsgCenterModel> msg_center_list = (HashMap<String, SobotMsgCenterModel>) sobotCache.getAsObject(uid+"sobot_msg_center_list");
		if (msg_center_list != null && msg_center_list.size() > 0) {
			msg_center_list.remove(appId);
			sobotCache.put(uid+"sobot_msg_center_list", msg_center_list);
		}
	}
}