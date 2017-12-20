package com.sobot.demo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.sobot.chat.utils.ResourceUtils;

/**
 * 自定义退出对话框
 */
public class SobotDemoExitDialog extends Dialog {

	private Context content;
	public Button button;
	public Button button2;
	public OnItemClick mOnItemClick = null;
	public SobotDemoExitDialog(Context context) {
		super(context);
		this.content = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sobot_demo_exit_dialog);
		button = (Button) findViewById(R.id.sobot_demo_negativeButton);
		button2 = (Button) findViewById(R.id.sobot_demo_positiveButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(0);
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(1);
			}
		});
	}

	public void setOnClickListener(OnItemClick onItemClick) {
		mOnItemClick = onItemClick;
	}

	public interface OnItemClick{
		void OnClick(int type);
	}
}