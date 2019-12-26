package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.HttpUtils.FileCallBack;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MD5Util;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.widget.SelectPicPopupWindow;
import com.sobot.chat.widget.gif.GifView;
import com.sobot.chat.widget.gif.GifView2;
import com.sobot.chat.widget.photoview.PhotoView;
import com.sobot.chat.widget.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SobotPhotoActivity extends Activity implements View.OnLongClickListener{

	private PhotoView big_photo;
	private PhotoViewAttacher mAttacher;
	private GifView2 sobot_image_view;
	private RelativeLayout sobot_rl_gif;
	private SelectPicPopupWindow menuWindow;
	String imageUrL;
	Bitmap bitmap;
	boolean isRight;
	String sdCardPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getIdByName(this, "layout",
				"sobot_photo_activity"));
		MyApplication.getInstance().addActivity(this);
		big_photo = (PhotoView) findViewById(ResourceUtils.getIdByName(this,
				"id", "sobot_big_photo"));
		sobot_image_view = (GifView2) findViewById(ResourceUtils.getIdByName(
				this, "id", "sobot_image_view"));
		sobot_image_view.setIsCanTouch(true);
		sobot_rl_gif = (RelativeLayout) findViewById(ResourceUtils.getIdByName(
				this, "id", "sobot_rl_gif"));
		sobot_rl_gif.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		sobot_image_view.setLoadFinishListener(new GifView2.LoadFinishListener() {
			@Override
			public void endCallBack(String pathAbsolute) {
				showView(pathAbsolute);
			}
		});
		initBundleData(savedInstanceState);

		LogUtils.i("SobotPhotoActivity-------" + imageUrL);

		if (TextUtils.isEmpty(imageUrL)){
			return;
		}

		if (imageUrL.startsWith("http")) {
			File dirPath = this.getImageDir(this);
			String encode = MD5Util.encode(imageUrL);
			File savePath = new File(dirPath, encode);
			sdCardPath = savePath.getAbsolutePath();
			if (!savePath.exists()) {
				displayImage(imageUrL, savePath, sobot_image_view);
			} else {
				showView(savePath.getAbsolutePath());
			}
		} else {
			File gifSavePath = new File(imageUrL);
			sdCardPath=imageUrL;
			if (gifSavePath.exists()) {
				showView(imageUrL);
			}
		}
		sobot_rl_gif.setVisibility(View.VISIBLE);
	}

	private void initBundleData(Bundle savedInstanceState) {
		if(savedInstanceState == null){
			imageUrL = getIntent().getStringExtra("imageUrL");
			isRight = getIntent().getBooleanExtra("isRight",false);
		} else {
			imageUrL = savedInstanceState.getString("imageUrL");
			isRight = savedInstanceState.getBoolean("isRight");
		}

	}

	void showView(String savePath) {
		if (!TextUtils.isEmpty(imageUrL)
				&& (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))
				&&isRight) {
			showGif(savePath);
		} else {
			if (!TextUtils.isEmpty(imageUrL)
					&& (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))) {
				showGif(savePath);
			} else {
				bitmap = SobotBitmapUtil.compress(savePath, getApplicationContext());
				big_photo.setImageBitmap(bitmap);
				mAttacher = new PhotoViewAttacher(big_photo);
				mAttacher
						.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
							@Override
							public void onPhotoTap(View view, float x, float y) {
								LogUtils.i("点击图片的时间：" + view + " x:" + x
										+ "  y:" + y);
								finish();
							}
						});
				mAttacher.update();
				big_photo.setVisibility(View.VISIBLE);
				mAttacher.setOnLongClickListener(this);
			}
		}
	}

	private void showGif(String savePath) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(savePath);
			bitmap = BitmapFactory.decodeFile(savePath);
//			sobot_image_view.setGifImageType(GifView.GifImageType.COVER);
			sobot_image_view.setGifImage(in,imageUrL);
			int screenWidth = ScreenUtils
					.getScreenWidth(SobotPhotoActivity.this);
			int screenHeight = ScreenUtils
					.getScreenHeight(SobotPhotoActivity.this);
			int w = ScreenUtils.formatDipToPx(SobotPhotoActivity.this,
					bitmap.getWidth());
			int h = ScreenUtils.formatDipToPx(SobotPhotoActivity.this,
					bitmap.getHeight());
			if (w == h) {
				if (w > screenWidth) {
					w = screenWidth;
					h = w;
				}
			} else {
				if (w > screenWidth) {

					h = (int) (h * (screenWidth*1.0f / w));
                    w = screenWidth;
				}
				if (h > screenHeight) {

					w = (int) (w * (screenHeight*1.0f / h));
                    h = screenHeight;

				}
			}
			LogUtils.i("bitmap" + w + "*" + h);
//			sobot_image_view.setShowDimension(w, h);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					w, h);
			sobot_image_view.setLayoutParams(layoutParams);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		sobot_rl_gif.setVisibility(View.VISIBLE);
		sobot_rl_gif.setOnLongClickListener(gifLongClickListener);
		sobot_image_view.setOnLongClickListener(gifLongClickListener);
	}

	private View.OnLongClickListener gifLongClickListener=new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if (!TextUtils.isEmpty(sdCardPath) && new File(sdCardPath).exists()){
				menuWindow = new SelectPicPopupWindow(SobotPhotoActivity.this,sdCardPath,"gif");
				try {
					menuWindow.showAtLocation(sobot_rl_gif, Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				} catch (Exception e) {
					menuWindow = null;
//						e.printStackTrace();
				}
			}
			return false;
		}
	};

	public void displayImage(String url, File saveFile, final GifView2 gifView) {
		// 下载图片
	    HttpUtils.getInstance().download(url, saveFile, null, new FileCallBack() {
            
            @Override
            public void onResponse(File file) {
                LogUtils.i("down load onSuccess gif"
                        + file.getAbsolutePath());
                // 把图片文件打开为文件流，然后解码为bitmap
                showView(file.getAbsolutePath());
            }
            
            @Override
            public void onError(Exception e, String msg, int responseCode) {
                LogUtils.w("图片下载失败:" + msg,e);
            }
            
            @Override
            public void inProgress(int progress) {
                LogUtils.i("gif图片下载进度:" + progress);
            }
        });
	}

	public File getFilesDir(Context context, String tag) {
		if (isSdCardExist() == true) {
			return context.getExternalFilesDir(tag);
		} else {
			return context.getFilesDir();
		}
	}

	public File getImageDir(Context context) {
		File file = getFilesDir(context, "images");
		return file;
	}

	public boolean isSdCardExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		sobot_image_view.pause();
		if (bitmap != null && bitmap.isRecycled() == false) {
			bitmap.recycle();
			System.gc();
		}
		if (menuWindow != null && menuWindow.isShowing()){
			try {
				menuWindow.dismiss();
			} catch (Exception e) {
//				e.printStackTrace();
			}
			menuWindow = null;
		}

		MyApplication.getInstance().deleteActivity(SobotPhotoActivity.this);
		super.onDestroy();
	}

	@Override
	public boolean onLongClick(View v) {
		if (!TextUtils.isEmpty(sdCardPath) && new File(sdCardPath).exists()){
			menuWindow = new SelectPicPopupWindow(SobotPhotoActivity.this,sdCardPath,"jpg/png",true);
			try {
				menuWindow.showAtLocation(sobot_rl_gif, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			} catch (Exception e) {
//				e.printStackTrace();
				menuWindow = null;
			}
		}
		return false;
	}

	protected void onSaveInstanceState(Bundle outState) {
		//被摧毁前缓存一些数据
		outState.putString("imageUrL",imageUrL);
		outState.putBoolean("isRight", isRight);
		super.onSaveInstanceState(outState);
	}
}