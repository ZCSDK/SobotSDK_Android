package com.sobot.chat.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.SobotVerControl;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotEvaluateModel;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.SobotQuestionRecommend;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.viewHolder.ImageMessageHolder;
import com.sobot.chat.widget.dialog.SobotEvaluateDialog;
import com.sobot.chat.widget.dialog.SobotRobotListDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatUtils {

	public static void showThankDialog(final Activity act,Handler handler, final boolean isFinish) {

		ToastUtil.showToast(act.getApplicationContext(),act.getResources().getString(
				ResourceUtils.getIdByName(act, "string", "sobot_thank_dialog_hint")));
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!act.isFinishing()){
					if(isFinish){
						act.finish();
					}
				}
			}
		},2000);
	}

	/**
	 * activity打开选择图片界面
	 * @param act
	 */
	public static void openSelectPic(Activity act) {
		openSelectPic(act,null);
	}

	/**
	 * Fragment打开选择图片界面
	 * @param act
	 */
	public static void openSelectPic(Activity act,Fragment childFragment) {
		if(act == null){
			return;
		}
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		try {
			if (childFragment != null) {
				childFragment.startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_picture);
			} else {
				act.startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_picture);
			}
		}catch (Exception e){
			ToastUtil.showToast(act.getApplicationContext(),"无法打开相册，请检查相册是否开启");
		}
	}

	/**
	 * activity打开相机
	 * @param act
	 * @return
	 */
	public static File openCamera(Activity act) {
		return openCamera(act, null);
	}

	/**
	 * Fragment打开相机
	 * @param act
	 * @param childFragment 打开相机的fragment
	 * @return
     */
	public static File openCamera(Activity act, Fragment childFragment) {
		String path = CommonUtils.getSDCardRootPath() + "/" +
				CommonUtils.getApplicationName(act.getApplicationContext()) + "/" + System.currentTimeMillis() + ".jpg";
		// 创建图片文件存放的位置
		File cameraFile = new File(path);
		boolean mkdirs = cameraFile.getParentFile().mkdirs();
		LogUtils.i("cameraPath:" + path);
		Uri uri;
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
			ContentValues contentValues = new ContentValues(1);
			contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
			uri = act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					contentValues);
		} else {
			uri = Uri.fromFile(cameraFile);
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore
				.EXTRA_OUTPUT, uri);
		if (childFragment != null) {
			childFragment.startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_makePictureFromCamera);
		} else {
			act.startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_makePictureFromCamera);
		}
		return cameraFile;
	}

	public static int getResId(Context context,String name) {
		return ResourceUtils.getIdByName(context, "id", name);
	}

	public static int getResDrawableId(Context context,String name) {
		return ResourceUtils.getIdByName(context, "drawable", name);
	}

	public static int getResLayoutId(Context context,String name) {
		return ResourceUtils.getIdByName(context, "layout", name);
	}

	public static int getResStringId(Context context,String name) {
		return ResourceUtils.getIdByName(context, "string", name);
	}

	public static String getResString(Context context,String name){
		return context.getResources().getString(ChatUtils.getResStringId(context,name));
	}

	public static void sendPicByUri(Context context, Handler handler,
			Uri selectedImage, ZhiChiInitModeBase initModel,final ListView lv_message,
									final SobotMsgAdapter messageAdapter) {
		if(initModel == null){
			return;
		}
		String picturePath = ImageUtils.getPath(context, selectedImage);
		LogUtils.i("picturePath:" + picturePath);
		if (!TextUtils.isEmpty(picturePath)) {
			sendPicLimitBySize(picturePath, initModel.getCid(),
					initModel.getUid(), handler, context, lv_message,messageAdapter);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				ToastUtil.showToast(context,"找不到图片");
				return;
			}
			sendPicLimitBySize(file.getAbsolutePath(),
					initModel.getCid(), initModel.getUid(), handler, context, lv_message,messageAdapter);
		}
	}

	@SuppressWarnings("deprecation")
	public static void sendPicLimitBySize(String filePath, String cid, String uid,
										  Handler handler, Context context, final ListView lv_message,
										  final SobotMsgAdapter messageAdapter) {

		Bitmap bitmap = SobotBitmapUtil.compress(filePath,context);
		if(bitmap!=null){
			String realFilePath = filePath;
			int degree = ImageUtils.readPictureDegree(filePath);
			bitmap = ImageUtils.rotateBitmap(bitmap, degree);
			if (!(filePath.endsWith(".gif") || filePath.endsWith(".GIF"))) {
				String fName = MD5Util.encode(filePath);
				filePath = CommonUtils.getSDCardRootPath() + "/" +
						CommonUtils.getApplicationName(context.getApplicationContext()) + "/" + fName + "_tmp.jpg";
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
				} catch (Exception e) {
					e.printStackTrace();
					filePath = realFilePath;
				}
			}

			long size = CommonUtils.getFileSize(filePath);

			if (size < 8388608) {
				String id = System.currentTimeMillis() + "";
				sendImageMessageToHandler(filePath, handler, id);
				sendPicture(context,cid, uid, filePath, handler, id,lv_message,
						messageAdapter);
			} else {
				ToastUtil.showToast(context,"图片大小需小于8M");
			}
		}else{
			ToastUtil.showToast(context,"图片格式错误");
		}
	}

	// 图片通知
	public static void sendImageMessageToHandler(String imageUrl,
			final Handler handler, String id) {
		ZhiChiMessageBase zhichiMessage = new ZhiChiMessageBase();
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsg(imageUrl);
		zhichiMessage.setAnswer(reply);
		zhichiMessage.setId(id);

		zhichiMessage.setSendSuccessState(ZhiChiConstant.MSG_SEND_STATUS_LOADING);
		zhichiMessage.setSenderType(ZhiChiConstant.message_sender_type_customer_sendImage + "");
		Message message = new Message();
		message.what = ZhiChiConstant.message_type_wo_sendImage;
		message.obj = zhichiMessage;
		handler.sendMessage(message);
	}

	public static void sendPicture(Context context,String cid, String uid,
								   final String filePath, final Handler handler, final String id,
								   final ListView lv_message, final SobotMsgAdapter messageAdapter) {
		SobotMsgManager.getInstance(context).getZhiChiApi().sendFile(cid, uid, filePath, "", new ResultCallBack<ZhiChiMessage>() {
					@Override
					public void onSuccess(ZhiChiMessage zhiChiMessage) {
						if (ZhiChiConstant.result_success_code == Integer
								.parseInt(zhiChiMessage.getCode())) {
							if (id != null) {
								Message message = handler.obtainMessage();
								message.what = ZhiChiConstant.hander_sendPicStatus_success;
								message.obj = id;
								handler.sendMessage(message);
							}
						}
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						LogUtils.i("发送图片 进度:" + current + "/" + total);
						if (id != null) {
							int position=messageAdapter.getMsgInfoPosition(id);
							LogUtils.i("发送图片 position:" +position);
							updateProgressPartly((int)current,position,lv_message);
						}
					}

					@Override
					public void onFailure(Exception e, String des) {
						LogUtils.i("发送图片error:" + des + "exception:" + e);
						if (id != null) {
							Message message = handler.obtainMessage();
							message.what = ZhiChiConstant.hander_sendPicStatus_fail;
							message.obj = id;
							handler.sendMessage(message);
						}
					}
				});
	}

	/**
	 * 单个更新某个条目   只有可见的时候更新progress，
	 * @param progress 当前进度
	 * @param position 位置
	 * @param lv_message Listview
     */
	public static void updateProgressPartly(int progress,int position,ListView lv_message){
		int firstVisiblePosition = lv_message.getFirstVisiblePosition();
		int lastVisiblePosition = lv_message.getLastVisiblePosition();
		if(position>=firstVisiblePosition && position<=lastVisiblePosition){
			View view = lv_message.getChildAt(position - firstVisiblePosition);
			if(view.getTag() instanceof ImageMessageHolder){
				ImageMessageHolder vh = (ImageMessageHolder)view.getTag();
				vh.sobot_pic_progress_round.setProgress(progress);
			}
		}
	}

	public static String getMessageContentByOutLineType(Context context,ZhiChiInitModeBase
			initModel, int type) {
		Resources resources = context.getResources();
		if (1 == type) {// 管理员下线
			return resources.getString(ResourceUtils.getIdByName(context, "string", "sobot_outline_leverByManager"));
		} else if (2 == type) { // 被管理员移除结束会话
			return resources.getString(ResourceUtils.getIdByName(context, "string", "sobot_outline_leverByManager"));
		} else if (3 == type) { // 被加入黑名单
			return resources.getString(ResourceUtils.getIdByName(context, "string", "sobot_outline_leverByManager"));
		} else if (4 == type) { // 超时下线
			String userOutWord = SharedPreferencesUtil.getStringData(context,ZhiChiConstant.SOBOT_CUSTOMUSEROUTWORD,"");
			if (!TextUtils.isEmpty(userOutWord)){
				return userOutWord;
			} else {
				return initModel != null?initModel.getUserOutWord():resources.getString(ResourceUtils
						.getIdByName(context, "string", "sobot_outline_leverByManager"));
			}

		} else if (6 == type) {
			return resources.getString(ResourceUtils.getIdByName(context, "string", "sobot_outline_openNewWindows"));
		}
		return null;
	}

	public static ZhiChiMessageBase getUnreadMode(Context context){
		ZhiChiMessageBase msgBase = new ZhiChiMessageBase();
		msgBase.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
		answer.setMsg(context.getResources().getString(ResourceUtils.getIdByName(context, "string", "sobot_no_read")));
		answer.setRemindType(ZhiChiConstant.sobot_remind_type_below_unread);
		msgBase.setAnswer(answer);
		return msgBase;
	}

	/**
	 * 获取客服邀请的mode
	 * @param pushMessage 推送的信息
	 * @return
	 */
	public static ZhiChiMessageBase getCustomEvaluateMode(ZhiChiPushMessage pushMessage){
		ZhiChiMessageBase base = new ZhiChiMessageBase();
		base.setSenderName(TextUtils.isEmpty(pushMessage.getAname())?"客服":pushMessage.getAname());
		SobotEvaluateModel sobotEvaluateModel = new SobotEvaluateModel();
		sobotEvaluateModel.setIsQuestionFlag(pushMessage.getIsQuestionFlag());
        sobotEvaluateModel.setIsResolved(-1);
		base.setSobotEvaluateModel(sobotEvaluateModel);
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		base.setSenderType(ZhiChiConstant.message_sender_type_custom_evaluate + "");
		base.setAction(ZhiChiConstant.action_custom_evaluate);
		base.setAnswer(reply);
		return base;
	}

	/**
	 * 保存一些配置项
	 * @param context
	 * @param info
     */
	public static void saveOptionSet(Context context, Information info){
		SharedPreferencesUtil.saveIntData(context, "robot_current_themeImg", info.getTitleImgId());
		SharedPreferencesUtil.saveStringData(context, "sobot_current_sender_face", TextUtils.isEmpty
				(info.getFace())?"":info.getFace());
		SharedPreferencesUtil.saveStringData(context, "sobot_current_sender_name", TextUtils.isEmpty
				(info.getUname())?"":info.getUname());
		SharedPreferencesUtil.saveStringData(context, "sobot_user_phone", TextUtils.isEmpty
				(info.getTel())?"":info.getTel());
		SharedPreferencesUtil.saveStringData(context, "sobot_user_email", TextUtils.isEmpty
				(info.getEmail())?"":info.getEmail());

		if (!TextUtils.isEmpty(info.getColor())) {
			SharedPreferencesUtil.saveStringData(context, "robot_current_themeColor", info.getColor());
		} else {
			SharedPreferencesUtil.removeKey(context,"robot_current_themeColor");
		}

		if (TextUtils.isEmpty(info.getUid())) {
			info.setEquipmentId(CommonUtils.getPartnerId(context));
		}
	}

	/**
	 * 初始化时检查一下传入参数是否发生变化，是否需要重新进行初始化
	 * @param context
	 * @param info
     * @return
     */
	public static boolean checkConfigChange(Context context,String appkey,final Information info) {
		if (!SobotVerControl.isPlatformVer) {
			String last_current_appkey = SharedPreferencesUtil.getStringData(context, ZhiChiConstant.sobot_last_current_appkey,"");
			if(!last_current_appkey.equals(info.getAppkey())){
				SharedPreferencesUtil.removeKey(context,ZhiChiConstant.sobot_last_login_group_id);
				SobotApi.exitSobotChat(context);
				return true;
			}
		}
		String last_current_partnerId = SharedPreferencesUtil.getStringData
				(context, appkey+"_"+ZhiChiConstant.sobot_last_current_partnerId,"");
		String last_current_dreceptionistId = SharedPreferencesUtil.getStringData(
				context,appkey+"_"+ZhiChiConstant.SOBOT_RECEPTIONISTID,"");
		String last_current_robot_code = SharedPreferencesUtil.getStringData(
				context,appkey+"_"+ZhiChiConstant.SOBOT_ROBOT_CODE,"");
		//判断上次uid是否跟此次传入的一样
		if(!last_current_partnerId.equals(info.getUid())){
			return true;
		} else if (!last_current_dreceptionistId.equals(info.getReceptionistId())){
			LogUtils.i("转入的指定客服发生了变化，重新初始化..............");
			return true;
		} else if (!last_current_robot_code.equals(info.getRobotCode())){
			LogUtils.i("指定机器人发生变化，重新初始化..............");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 打开评价对话框
	 * @param context
	 * @param isFinish 评价完是否关闭
	 * @param initModel 初始化信息
	 * @param current_model 评价对象
	 * @param commentType commentType 评价类型 主动评价1 邀请评价0
	 */
	public static SobotEvaluateDialog showEvaluateDialog(Activity context , boolean isFinish, ZhiChiInitModeBase
			initModel, int current_model, int commentType, String customName,int scroe){
		if(initModel == null){
			return null;
		}

		SobotEvaluateDialog dialog = new SobotEvaluateDialog(context, isFinish,initModel,current_model,commentType, customName, scroe);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		return dialog;
	}

	/**
	 * 打开机器人切换列表
	 * @param context
	 * @param initMode 初始化对象
	 */
	public static SobotRobotListDialog showRobotListDialog(Activity context , ZhiChiInitModeBase initMode,SobotRobotListDialog.SobotRobotListListener listener){
		if(context == null || initMode == null || listener == null){
			return null;
		}

		SobotRobotListDialog dialog = new SobotRobotListDialog(context, initMode.getUid(),initMode.getCurrentRobotFlag(),listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		return dialog;
	}

	/**
	 * 根据当前cid的位置获取cid
	 * @return
	 */
	public static String getCurrentCid(ZhiChiInitModeBase initModel, List<String> cids, int currentCidPosition) {
		if(initModel != null){
			String currentCid = initModel.getCid();
			if(currentCidPosition > 0){
				if(currentCidPosition > cids.size() - 1){
					currentCid = "-1";
				} else {
					currentCid = cids.get(currentCidPosition);
				}
			}
			return currentCid;
		} else {
			return "-1";
		}
	}

	/**
	 * 根据逻辑获取应该显示的标题
	 * @param context
	 * @param ignoreLogic
	 * @param title
	 * @param companyName
     * @return
     */
	public static String getLogicTitle(Context context,boolean ignoreLogic,String title,String
			companyName){
		if(ignoreLogic){
			return title;
		}else{
			int titleDisplayMode = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
					.SOBOT_CHAT_TITLE_DISPLAY_MODE, SobotChatTitleDisplayMode.Default.getValue());
			if(SobotChatTitleDisplayMode.Default.getValue() == titleDisplayMode){
				//显示昵称
				return title;
			} else if (SobotChatTitleDisplayMode.ShowFixedText.getValue() == titleDisplayMode){
				//显示固定文本
				String titleContent = SharedPreferencesUtil.getStringData(context, ZhiChiConstant
						.SOBOT_CHAT_TITLE_DISPLAY_CONTENT, "");
				if(!TextUtils.isEmpty(titleContent)){
					return titleContent;
				}else{
					//显示昵称
					return title;
				}
			} else if (SobotChatTitleDisplayMode.ShowCompanyName.getValue() == titleDisplayMode){
				//显示公司名称
				String titleContent = companyName;
				if(!TextUtils.isEmpty(titleContent)){
					return titleContent;
				}else{
					//显示昵称
					return title;
				}
			}
		}
		return title;
	}

	/**
	 * 获取被xx客服接入的提醒对象
	 * @param context
	 * @param aname
     * @return
     */
	public static ZhiChiMessageBase getServiceAcceptTip(Context context,String aname){
		ZhiChiMessageBase base = new ZhiChiMessageBase();
		base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		base.setAction(ZhiChiConstant.action_remind_connt_success);
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsgType(null);

		String nameColor = "<font color='"+ChatUtils.getResString(context,"sobot_color_custom_name")
				+"'>" + aname + "</font>";
		reply.setMsg(String.format(ChatUtils.getResString(context,"sobot_service_accept"), nameColor));
		reply.setRemindType(ZhiChiConstant.sobot_remind_type_accept_request);
		base.setAnswer(reply);
		return base;
	}

	/**
	 * 获取人工提示语的对象
	 * @param aname 客服名称
	 * @param aface 客服头像
	 * @param content 欢迎语内容
	 * @return
	 */
	public static ZhiChiMessageBase getServiceHelloTip(String aname, String aface,String content) {
		ZhiChiMessageBase base = new ZhiChiMessageBase();
		base.setSenderName(TextUtils.isEmpty(aname)?"":aname);
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsgType(ZhiChiConstant.message_type_text + "");
		base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
		reply.setMsg(content);
		base.setSenderFace(aface);
		base.setAnswer(reply);
		return base;
	}

	/**
	 *
	 * @return
     */
	public static ZhiChiMessageBase getInLineHint(Context context,int num){
		ZhiChiMessageBase paiduizhichiMessageBase = new ZhiChiMessageBase();
		paiduizhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
		paiduizhichiMessageBase.setAction(ZhiChiConstant.action_remind_info_paidui);

		ZhiChiReplyAnswer reply_paidui = new ZhiChiReplyAnswer();
		reply_paidui.setMsg(String.format(ChatUtils.getResString(context,"sobot_in_line_position"), num + ""));
		reply_paidui.setRemindType(ZhiChiConstant.sobot_remind_type_paidui_status);
		paiduizhichiMessageBase.setAnswer(reply_paidui);
		return paiduizhichiMessageBase;
	}

	/**
	 * 判断是否评价完毕就释放会话
	 * @param context
	 * @param isComment
	 * @param current_client_model
	 * @return
	 */
	public static boolean isEvaluationCompletedExit(Context context,boolean isComment,int current_client_model){
		boolean evaluationCompletedExit = SharedPreferencesUtil.getBooleanData
				(context,ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT,false);
		if(evaluationCompletedExit && isComment && current_client_model == ZhiChiConstant.client_model_customService){
			return true;
		}
		return false;
	}

	/**
	 * 退出登录
	 * @param context
	 */
	public static void userLogout(final Context context){


		String cid = SharedPreferencesUtil.getStringData(context, Const.SOBOT_CID,"");
		String uid = SharedPreferencesUtil.getStringData(context,Const.SOBOT_UID,"");

		if (!TextUtils.isEmpty(cid) && !TextUtils.isEmpty(uid)){
			ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
			zhiChiApi.out(cid, uid,	new StringResultCallBack<CommonModel>() {
				@Override
				public void onSuccess(CommonModel result) { }

				@Override
				public void onFailure(Exception e, String des) {}
			});
		}
	}

    /**
     * 判断机器人引导转人工是否勾选
     * @param manualType 机器人引导转人工 勾选为1，默认为0 固定位置，比如1,1,1,1=直接回答勾选，理解回答勾选，引导回答勾选，未知回答勾选
     * @param answerType
     * @return true表示勾选上了
     */
	public static boolean checkManualType(String manualType,String answerType){
        if(TextUtils.isEmpty(manualType) || TextUtils.isEmpty(answerType)){
            return false;
        }
        try {
            Integer type = Integer.valueOf(answerType);
            String[] mulArr = manualType.split(",");
            if((type == 1 && "1".equals(mulArr[0])) || (type == 2 && "1".equals(mulArr[1]))
                    || (type == 4 && "1".equals(mulArr[2])) || (type == 3 && "1".equals(mulArr[3]))) {
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

	public static void sendPicByFilePath(Context context,String filePath,SobotSendFileListener listener) {

		Bitmap bitmap = SobotBitmapUtil.compress(filePath,context);
		if(bitmap!=null){
			int degree = ImageUtils.readPictureDegree(filePath);
			bitmap = ImageUtils.rotateBitmap(bitmap, degree);
			if (!(filePath.endsWith(".gif") || filePath.endsWith(".GIF"))) {
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			listener.onSuccess(filePath);
		}else{
			ToastUtil.showToast(context,"图片格式错误");
			listener.onError();
		}
	}

	public static void sendPicByUriPost(Context context,Uri selectedImage,SobotSendFileListener listener){
		String picturePath = ImageUtils.getPath(context, selectedImage);
		if (!TextUtils.isEmpty(picturePath)) {
			sendPicByFilePath(context,picturePath, listener);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				ToastUtil.showToast(context,"找不到图片");
				return;
			}
			sendPicByFilePath(context,picturePath, listener);
		}
	}

	public interface SobotSendFileListener{
		void onSuccess(String filePath);
		void onError();
	}

	/**
	 * 检查是否开启   是否已解决配置
	 * @return
	 */
	public static boolean isQuestionFlag(final SobotEvaluateModel evaluateModel){
		if(evaluateModel != null){
			return evaluateModel.getIsQuestionFlag();
		}
		return false;
	}

	/**
	 * 保存消息列表
	 * @param context
	 * @param info
	 * @param appkey
	 * @param initModel
	 * @param messageList
	 */
	public static void saveLastMsgInfo(Context context,Information info,String appkey,ZhiChiInitModeBase initModel,List<ZhiChiMessageBase> messageList) {
		SobotCache sobotCache = SobotCache.get(context);
		HashMap<String,SobotMsgCenterModel> msg_center_list = (HashMap<String, SobotMsgCenterModel>) sobotCache.getAsObject(info.getUid()+"sobot_msg_center_list");
		if (msg_center_list == null) {
			msg_center_list = new HashMap<>();
		}
		SobotMsgCenterModel sobotMsgCenterModel = msg_center_list.get(appkey);
		if (sobotMsgCenterModel==null) {
			sobotMsgCenterModel = new SobotMsgCenterModel();
		}
		sobotMsgCenterModel.setInfo(info);
		sobotMsgCenterModel.setFace(initModel.getCompanyLogo());
		sobotMsgCenterModel.setName(initModel.getCompanyName());
		sobotMsgCenterModel.setLastDate(DateUtil.toDate(Calendar.getInstance().getTime().getTime(),DateUtil.DATE_FORMAT5));
		sobotMsgCenterModel.setUnreadCount(0);

		if (messageList != null) {
			String lastMsg = "";
			for (int i = messageList.size() -1; i >= 0; i--) {
				ZhiChiMessageBase tempMsg = messageList.get(i);
				if (tempMsg.getAnswer() != null){
					lastMsg = tempMsg.getAnswer().getMsg();
				}
				break;
			}
			sobotMsgCenterModel.setLastMsg(lastMsg);
		}
		msg_center_list.put(appkey, sobotMsgCenterModel);
		sobotCache.put(info.getUid()+"sobot_msg_center_list",msg_center_list);
		SharedPreferencesUtil.removeKey(context, ZhiChiConstant.SOBOT_CURRENT_IM_APPID);
		Intent lastMsgIntent = new Intent(ZhiChiConstant.SOBOT_ACTION_UPDATE_LAST_MSG);
		lastMsgIntent.putExtra("lastMsg", sobotMsgCenterModel);
		LocalBroadcastManager.getInstance(context).sendBroadcast(lastMsgIntent);
	}

	public static void sendMultiRoundQuestions(Context context, SobotMultiDiaRespInfo multiDiaRespInfo, Map<String, String> interfaceRet, SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
		if (context != null && multiDiaRespInfo != null && interfaceRet != null) {
			ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
			String content = "{\"interfaceRetList\":[" + GsonUtil.map2Json(interfaceRet) + "]," + "\"template\":" + multiDiaRespInfo.getTemplate() + "}";

			msgObj.setContent(formatQuestionStr(multiDiaRespInfo.getOutPutParamList(), interfaceRet, multiDiaRespInfo));
			msgObj.setId(System.currentTimeMillis() + "");
			if (msgCallBack != null) {
				msgCallBack.sendMessageToRobot(msgObj, 4, 2, content, interfaceRet.get("title"));
			}
		}
	}

	private static String formatQuestionStr(String[] outPutParam, Map<String, String> interfaceRet, SobotMultiDiaRespInfo multiDiaRespInfo) {
		if (multiDiaRespInfo != null && interfaceRet != null && interfaceRet.size() > 0) {
			Map<String, String> map = new HashMap<>();
			map.put("level", multiDiaRespInfo.getLevel());
			map.put("conversationId", multiDiaRespInfo.getConversationId());
			if (outPutParam != null && outPutParam.length > 0) {
				for (int i = 0; i < outPutParam.length; i++) {
					map.put(outPutParam[i], interfaceRet.get(outPutParam[i]));
				}
			}
			return GsonUtil.map2Str(map);
		}
		return "";
	}

	public static String getMultiMsgTitle(SobotMultiDiaRespInfo multiDiaRespInfo){
		if (multiDiaRespInfo == null) {
			return "";
		}
		if ("000000".equals(multiDiaRespInfo.getRetCode())) {
			if (multiDiaRespInfo.getEndFlag()) {
				return !TextUtils.isEmpty(multiDiaRespInfo.getAnswerStrip()) ? multiDiaRespInfo.getAnswerStrip() : multiDiaRespInfo.getAnswer();
			} else {
				return multiDiaRespInfo.getRemindQuestion();
			}
		}
		return multiDiaRespInfo.getRetErrorMsg();
	}

	/**
	 * 获取热点问题的类型
	 * @param initModel 初始化参数
	 * @return
	 */
	public static ZhiChiMessageBase getQuestionRecommendData(final ZhiChiInitModeBase initModel, final SobotQuestionRecommend data){
		ZhiChiMessageBase robot = new ZhiChiMessageBase();
		robot.setSenderType(ZhiChiConstant.message_sender_type_questionRecommend + "");
		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setQuestionRecommend(data);
		robot.setAnswer(reply);
		robot.setSenderFace(initModel.getRobotLogo());
		robot.setSender(initModel.getRobotName());
		robot.setSenderName(initModel.getRobotName());
		return robot;
	}

	public static ZhiChiMessageBase getTipByText(String content){
		ZhiChiMessageBase data = new ZhiChiMessageBase();
		data.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

		ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
		reply.setMsg(content);
		reply.setRemindType(ZhiChiConstant.sobot_remind_type_tip);
		data.setAnswer(reply);
		return data;
	}

	public static void msgLogicalProcess(ZhiChiInitModeBase initModel,SobotMsgAdapter messageAdapter,ZhiChiPushMessage pushMessage){
		if (initModel != null && ChatUtils.isNeedWarning(pushMessage.getContent(),initModel.getAccountStatus())) {
			messageAdapter.justAddData(ChatUtils.getTipByText("温馨提示：从正规网站按照正常操作进行交易，不要轻易提供验证码、账号等隐私信息，谨防被骗！"));
		}
	}

	private static boolean isNeedWarning(String content,int accountStatus){
		return !TextUtils.isEmpty(content) && (accountStatus == ZhiChiConstant.SOBOT_ACCOUNTSTATUS_FREE_EDITION
				|| accountStatus == ZhiChiConstant.SOBOT_ACCOUNTSTATUS_TRIAL_EDITION)
				&& content.contains("验证码");
	}

	/**
	 * 获取引导问题中的问题选项view
	 * @param context
	 */
	public static TextView initAnswerItemTextView(Context context,boolean isHistoryMsg) {
		TextView answer = new TextView(context);
		answer.setTextSize(16);
		answer.setLineSpacing(2f, 1f);
		// 设置字体的颜色的样式
		answer.setTextColor(context.getResources().getColor(ResourceUtils.getIdByName(context,
				"color",isHistoryMsg?"sobot_color_suggestion_history":"sobot_color_link")));
		return answer;
	}
}