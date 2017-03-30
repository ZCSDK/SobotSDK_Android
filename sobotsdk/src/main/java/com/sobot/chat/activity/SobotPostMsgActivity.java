package com.sobot.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.api.ZhiChiApiImpl;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.EmojiFilter;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.ThankDialog;

@SuppressLint("HandlerLeak")
public class SobotPostMsgActivity extends SobotBaseActivity implements
		OnClickListener,View.OnTouchListener {

	private EditText sobot_et_email,sobot_et_content,sobot_leavemsg_nikename,sobot_leavemsg_phone;
	private TextView sobot_tv_post_msg1;
	private ImageView sobot_img_clear_nikename,sobot_img_clear_email,sobot_img_clear_phone;
	private View sobot_frist_line,sobot_second_line;
//	private GestureDetector mGestureDetector;
	private ZhiChiApiImpl zhiChiApi;
	private RelativeLayout sobot_post_msg_layout;
	private String uid = "";
	private String companyId = "";
	private String msgTmp = "";
	private String msgTxt = "";
	private boolean flag_exit_sdk,isShowNikeNameTv,isShowPhoneTv,isShowNikeName,isShowPhone,
			isEmailFocus = false,isPhoneFocus = false,isNameFocus = false;
	private int flag_exit_type = -1;
	private ThankDialog d;

	public Handler handler = new Handler() {
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (flag_exit_type == 1) {
					finishPageOrSDK(true);
				} else if (flag_exit_type == 2) {
					setResult(200);
					finishPageOrSDK(false);
				} else {
					finishPageOrSDK(flag_exit_sdk);
				}
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout", "sobot_activity_post_msg"));

		isShowNikeNameTv = SharedPreferencesUtil.getBooleanData(this,"isShowNikeNameTv",false);//控制textView是否显示
		isShowPhoneTv = SharedPreferencesUtil.getBooleanData(this,"isShowPhoneTv",false);//控制textView是否显示

		isShowNikeName = SharedPreferencesUtil.getBooleanData(this,"isShowNikeName",false);//控制是否必填
		isShowPhone = SharedPreferencesUtil.getBooleanData(this,"isShowPhone",false);//控制是否必填

		sobot_leavemsg_nikename = (EditText) findViewById(getResId("sobot_leavemsg_nikename"));
		sobot_leavemsg_phone = (EditText) findViewById(getResId("sobot_leavemsg_phone"));
		sobot_et_email = (EditText) findViewById(getResId("sobot_et_email"));
		sobot_frist_line = findViewById(getResId("sobot_frist_line"));
		sobot_second_line = findViewById(getResId("sobot_second_line"));
		sobot_leavemsg_nikename.setText(SharedPreferencesUtil.getStringData(this,"sobot_user_nikename",""));
		sobot_leavemsg_phone.setText(SharedPreferencesUtil.getStringData(this,"sobot_user_phone",""));

		if (isShowNikeNameTv){
			sobot_leavemsg_nikename.setVisibility(View.VISIBLE);
			sobot_frist_line.setVisibility(View.VISIBLE);
			sobot_second_line.setVisibility(View.GONE);
		} else {
			sobot_leavemsg_nikename.setVisibility(View.GONE);
		}

		if (isShowPhoneTv){
			sobot_leavemsg_phone.setVisibility(View.VISIBLE);
			sobot_frist_line.setVisibility(View.GONE);
			sobot_second_line.setVisibility(View.VISIBLE);
		} else {
			sobot_leavemsg_phone.setVisibility(View.GONE);
		}

		if (isShowNikeNameTv && isShowPhoneTv){
			sobot_leavemsg_nikename.setVisibility(View.VISIBLE);
			sobot_leavemsg_phone.setVisibility(View.VISIBLE);
			sobot_frist_line.setVisibility(View.VISIBLE);
			sobot_second_line.setVisibility(View.VISIBLE);
		}

		sobot_et_content = (EditText) findViewById(getResId("sobot_et_content"));
		sobot_tv_post_msg1 = (TextView) findViewById(getResId("sobot_tv_post_msg1"));
		sobot_post_msg_layout =(RelativeLayout) findViewById(getResId("sobot_post_msg_layout"));
		sobot_img_clear_nikename = (ImageView) findViewById(getResId("sobot_img_clear_nikename"));
		sobot_img_clear_email = (ImageView) findViewById(getResId("sobot_img_clear_email"));
		sobot_img_clear_phone = (ImageView) findViewById(getResId("sobot_img_clear_phone"));
		sobot_img_clear_nikename.setOnClickListener(this);
		sobot_img_clear_email.setOnClickListener(this);
		sobot_img_clear_phone.setOnClickListener(this);

		sobot_post_msg_layout.setOnTouchListener(this);
		setTitle(getResString("sobot_str_bottom_message"));
		setShowNetRemind(false);
		String bg_color = SharedPreferencesUtil.getStringData(this,
				"robot_current_themeColor", "");
		if (bg_color != null && bg_color.trim().length() != 0) {
			relative.setBackgroundColor(Color.parseColor(bg_color));
		}

		int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
		if (robot_current_themeImg != 0){
			relative.setBackgroundResource(robot_current_themeImg);
		}
		showRightView(0,getResString("sobot_submit"), true);
		Drawable drawable = getResources().getDrawable(getResDrawableId("sobot_btn_back_selector"));
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		sobot_tv_left.setCompoundDrawables(drawable, null, null, null);
		sobot_tv_left.setText(getResString("sobot_back"));
		sobot_tv_left.setOnClickListener(this);
		if (Build.VERSION.SDK_INT >= 11)
			sobot_tv_right.setAlpha(0.5f);
		sobot_tv_right.setClickable(false);
		zhiChiApi = new ZhiChiApiImpl(getApplicationContext());
		setTextChangedListener();

		initBundleData(savedInstanceState);
		if (msgTmp.startsWith("<br/>")) {
			msgTmp = msgTmp.substring(5, msgTmp.length());
		}

		if (msgTmp.endsWith("<br/>")) {
			msgTmp = msgTmp.substring(0, msgTmp.length() - 5);
		}

		if (msgTxt.startsWith("<br/>")) {
			msgTxt = msgTxt.substring(5, msgTxt.length());
		}

		if (msgTxt.endsWith("<br/>")) {
			msgTxt = msgTxt.substring(0, msgTxt.length() - 5);
		}
		sobot_et_content.setHint(Html.fromHtml(msgTmp));
		HtmlTools.getInstance(getApplicationContext()).setRichText(sobot_tv_post_msg1, msgTxt,
				ResourceUtils.getIdByName(this, "color", "sobot_postMsg_url_color"));
		sobot_post_msg_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(sobot_post_msg_layout.getWindowToken(), 0); // 强制隐藏键盘
			}
		});

		if (isShowNikeName){
			sobot_leavemsg_nikename.setHint(getResString("sobot_post_msg_hint_nikename") + getResString("sobot_required"));
		} else {
			sobot_leavemsg_nikename.setHint(getResString("sobot_post_msg_hint_nikename") + getResString("sobot_optional"));
		}

		if (isShowPhone){
			sobot_leavemsg_phone.setHint(getResString("sobot_post_msg_hint_phone") + getResString("sobot_required"));
		} else {
			sobot_leavemsg_phone.setHint(getResString("sobot_post_msg_hint_phone") + getResString("sobot_optional"));
		}
	}

	private void initBundleData(Bundle savedInstanceState) {
		if(savedInstanceState == null){
			if (getIntent() != null) {
				uid = getIntent().getStringExtra("uid");
				companyId = getIntent().getStringExtra("companyId");
				flag_exit_type = getIntent().getIntExtra(
						ZhiChiConstant.FLAG_EXIT_TYPE, -1);
				flag_exit_sdk = getIntent().getBooleanExtra(
						ZhiChiConstant.FLAG_EXIT_SDK, false);
				msgTmp = getIntent().getStringExtra("msgTmp").replaceAll("\n",
						"<br/>");
				msgTxt = getIntent().getStringExtra("msgTxt").replaceAll("\n",
						"<br/>");
			}
		} else {
			uid = savedInstanceState.getString("uid");
			companyId = savedInstanceState.getString("companyId");
			flag_exit_type = savedInstanceState.getInt(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
			flag_exit_sdk = savedInstanceState.getBoolean(ZhiChiConstant.FLAG_EXIT_SDK, false);

			msgTmp = savedInstanceState.getString("msgTmp");
			msgTxt = savedInstanceState.getString("msgTxt");
			if(!TextUtils.isEmpty(msgTmp)){
				msgTmp = msgTmp.replaceAll("\n", "<br/>");
			}

			if(!TextUtils.isEmpty(msgTxt)){
				msgTxt = msgTxt.replaceAll("\n", "<br/>");
			}
		}
	}

	@Override
	public void forwordMethod() {
		if (ScreenUtils.isEmail(sobot_et_email.getText().toString().trim())) {
			postMsg();
		} else {
			showHint(getResString("sobot_email_dialog_hint"), false);
		}
	}

	@SuppressWarnings("deprecation")
	public void showHint(String content, final boolean flag) {
		if(!isFinishing()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(sobot_et_email.getWindowToken(), 0); // 强制隐藏键盘
			imm.hideSoftInputFromWindow(sobot_et_content.getWindowToken(), 0); // 强制隐藏键盘
			if(d != null){
				d.dismiss();
			}
			ThankDialog.Builder customBuilder = new ThankDialog.Builder(
					SobotPostMsgActivity.this);
			customBuilder.setMessage(content);
            d = customBuilder.create();
			d.show();

			WindowManager.LayoutParams lp = d.getWindow().getAttributes();
			float dpToPixel = ScreenUtils.dpToPixel(
					getApplicationContext(), 1);
			lp.width = (int) (dpToPixel * 200); // 设置宽度
			d.getWindow().setAttributes(lp);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(d != null){
						d.dismiss();
					}
					if (flag) {
						handler.sendEmptyMessage(1);
					}
				}
			},2000);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == sobot_tv_left) {
			if (flag_exit_type == 1 || flag_exit_type == 2) {
				finishPageOrSDK(false);
			} else {
				finishPageOrSDK(flag_exit_sdk);
			}
		}

		if (view == sobot_img_clear_nikename){
			sobot_leavemsg_nikename.setText("");
			sobot_img_clear_nikename.setVisibility(View.GONE);
		}

		if (view == sobot_img_clear_email){
			sobot_et_email.setText("");
			sobot_img_clear_email.setVisibility(View.GONE);
		}

		if (view == sobot_img_clear_phone){
			sobot_leavemsg_phone.setText("");
			sobot_img_clear_phone.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if (flag_exit_type == 1 || flag_exit_type == 2) {
			finishPageOrSDK(false);
		} else {
			finishPageOrSDK(flag_exit_sdk);
		}
	}

	private void postMsg() {
		String userName = "",userPhone = "";

		if (isShowNikeNameTv) {
			userName = EmojiFilter.removeNonBmpUnicode(sobot_leavemsg_nikename.getText().toString());
			if (TextUtils.isEmpty(userName)){
				if (isShowNikeName){
					sobot_leavemsg_nikename.setText("");
					return;
				} else {
					sobot_leavemsg_nikename.setText(userName);
				}
			}
		}

		if (isShowPhoneTv){
			userPhone = sobot_leavemsg_phone.getText().toString();
		}

		zhiChiApi.postMsg(uid, sobot_et_content.getText().toString(),
				sobot_et_email.getText().toString(), userPhone, companyId,userName,
				new StringResultCallBack<CommonModelBase>() {
					@Override
					public void onSuccess(CommonModelBase base) {
						if (Integer.parseInt(base.getStatus()) == 0){
							showHint(base.getMsg(),false);
						} else if (Integer.parseInt(base.getStatus()) == 1){
							showHint(getResString("sobot_leavemsg_success_hint"),true);
						}
					}

					@Override
					public void onFailure(Exception e, String des) {}
				});
	}

	private void setTextChangedListener() {

		sobot_leavemsg_nikename.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isNameFocus = hasFocus;
				if (hasFocus) {
					if (sobot_leavemsg_nikename.getText().toString().trim().length() != 0) {
						sobot_img_clear_nikename.setVisibility(View.VISIBLE);
					}
				} else {
					sobot_img_clear_nikename.setVisibility(View.GONE);
				}
			}
		});

		sobot_leavemsg_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isPhoneFocus = hasFocus;
				if (hasFocus) {
					if (sobot_leavemsg_phone.getText().toString().trim().length() != 0) {
						sobot_img_clear_phone.setVisibility(View.VISIBLE);
					}
				} else {
					sobot_img_clear_phone.setVisibility(View.GONE);
				}
			}
		});

		sobot_et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isEmailFocus = hasFocus;
				if (hasFocus) {
					if (sobot_et_email.getText().toString().trim().length() != 0) {
						sobot_img_clear_email.setVisibility(View.VISIBLE);
					}
				} else {
					sobot_img_clear_email.setVisibility(View.GONE);
				}
			}
		});

		sobot_leavemsg_nikename.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				check();
				if (!TextUtils.isEmpty(arg0.toString()) && EmojiFilter.hasEmojiStr(arg0.toString())){
					sobot_leavemsg_nikename.setText(EmojiFilter.removeNonBmpUnicode(arg0.toString()));
					sobot_leavemsg_nikename.setSelection(arg1);
				}

				if (isNameFocus && arg0.toString().length() != 0){
					sobot_img_clear_nikename.setVisibility(View.VISIBLE);
				} else {
					sobot_img_clear_nikename.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});

		sobot_leavemsg_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				check();
				if (isPhoneFocus && arg0.toString().length() != 0){
					sobot_img_clear_phone.setVisibility(View.VISIBLE);
				} else {
					sobot_img_clear_phone.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});

		sobot_et_email.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				check();
				if (isEmailFocus && arg0.toString().length() != 0){
					sobot_img_clear_email.setVisibility(View.VISIBLE);
				} else {
					sobot_img_clear_email.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});

		sobot_et_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				check();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				check();
			}
		});
	}

	private void check() {

		if (isShowPhoneTv && isShowNikeNameTv){
			if (isShowPhone && isShowNikeName){
				String userPhone = sobot_leavemsg_phone.getText().toString();
				String userName = sobot_leavemsg_nikename.getText().toString();
				if ((!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim())) && !TextUtils.isEmpty(userPhone) && !TextUtils.isEmpty(userName)){
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				} else {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(0.5f);
					sobot_tv_right.setClickable(false);
				}
			} else if (isShowPhone && !isShowNikeName){
				String userPhone = sobot_leavemsg_phone.getText().toString();
				if ((!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim())) && !TextUtils.isEmpty(userPhone)){
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				} else {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(0.5f);
					sobot_tv_right.setClickable(false);
				}
			} else if (!isShowPhone && isShowNikeName){
				String userName = sobot_leavemsg_nikename.getText().toString();
				if ((!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim())) && !TextUtils.isEmpty(userName)){
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				} else {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(0.5f);
					sobot_tv_right.setClickable(false);
				}
			} else {
				if ((!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim()))){
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				} else {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(0.5f);
					sobot_tv_right.setClickable(false);
				}
			}
		} else if (isShowPhoneTv && !isShowNikeNameTv){
			if (isShowPhone){
				String userPhone = sobot_leavemsg_phone.getText().toString();
				if (!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim()) && !TextUtils.isEmpty(userPhone)) {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				} else {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(0.5f);
					sobot_tv_right.setClickable(false);
				}
			} else {
				if (!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim())) {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				}
			}
		} else if (!isShowPhoneTv && isShowNikeNameTv){
			if (isShowNikeName){
				String userName = sobot_leavemsg_nikename.getText().toString();
				if (!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim()) && !TextUtils.isEmpty(userName)) {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				} else {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(0.5f);
					sobot_tv_right.setClickable(false);
				}
			} else {
				if (!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
						&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
						.trim())) {
					if (Build.VERSION.SDK_INT >= 11)
						sobot_tv_right.setAlpha(1f);
					sobot_tv_right.setClickable(true);
				}
			}
		} else {
			if (!TextUtils.isEmpty(sobot_et_email.getText().toString().trim())
					&& !TextUtils.isEmpty(sobot_et_content.getText().toString()
					.trim())) {
				if (Build.VERSION.SDK_INT >= 11)
					sobot_tv_right.setAlpha(1f);
				sobot_tv_right.setClickable(true);
			} else {
				if (Build.VERSION.SDK_INT >= 11)
					sobot_tv_right.setAlpha(0.5f);
				sobot_tv_right.setClickable(false);
			}
		}
	}

	private void finishPageOrSDK(boolean flag) {
		if (!flag) {
			finish();
			overridePendingTransition(ResourceUtils.getIdByName(
					getApplicationContext(), "anim", "push_right_in"),
					ResourceUtils.getIdByName(getApplicationContext(), "anim",
							"push_right_out"));
		} else {
			MyApplication.getInstance().exit();
		}
	}
	
	@Override
	protected void onDestroy() {
		if(d!=null){
			d.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		return mGestureDetector.onTouchEvent(event);
		return false;
	}

	protected void onSaveInstanceState(Bundle outState) {
		//被摧毁前缓存一些数据
		outState.putString("uid",uid);
		outState.putString("companyId",companyId);
		outState.putInt("flag_exit_type",flag_exit_type);
		outState.putBoolean("flag_exit_sdk",flag_exit_sdk);
		outState.putString("msgTmp",msgTmp);
		outState.putString("msgTxt",msgTxt);
		super.onSaveInstanceState(outState);
	}
}
