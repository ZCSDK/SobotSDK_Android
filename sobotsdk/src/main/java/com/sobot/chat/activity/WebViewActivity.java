package com.sobot.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ToastUtil;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends SobotBaseActivity implements View.OnClickListener {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private RelativeLayout sobot_rl_net_error;
    private Button sobot_btn_reconnect;
    private TextView sobot_txt_loading;
    private String mUrl = "";
    private LinearLayout sobot_webview_toolsbar;
    private ImageView sobot_webview_goback;
    private ImageView sobot_webview_forward;
    private ImageView sobot_webview_reload;
    private ImageView sobot_webview_copy;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_webview");
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra("url"))) {
                mUrl = getIntent().getStringExtra("url");
            }
        } else {
            mUrl = savedInstanceState.getString("url");
        }
    }

    @Override
    protected void initView() {
        setTitle("");
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
        mWebView = (WebView) findViewById(getResId("sobot_mWebView"));
        mProgressBar = (ProgressBar) findViewById(getResId("sobot_loadProgress"));
        sobot_rl_net_error = (RelativeLayout) findViewById(getResId("sobot_rl_net_error"));
        sobot_webview_toolsbar = (LinearLayout) findViewById(getResId("sobot_webview_toolsbar"));
        sobot_btn_reconnect = (Button) findViewById(getResId("sobot_btn_reconnect"));
        sobot_btn_reconnect.setOnClickListener(this);
        sobot_txt_loading = (TextView) findViewById(getResId("sobot_txt_loading"));
        sobot_webview_goback = (ImageView) findViewById(getResId("sobot_webview_goback"));
        sobot_webview_forward = (ImageView) findViewById(getResId("sobot_webview_forward"));
        sobot_webview_reload = (ImageView) findViewById(getResId("sobot_webview_reload"));
        sobot_webview_copy = (ImageView) findViewById(getResId("sobot_webview_copy"));
        sobot_webview_goback.setOnClickListener(this);
        sobot_webview_forward.setOnClickListener(this);
        sobot_webview_reload.setOnClickListener(this);
        sobot_webview_copy.setOnClickListener(this);
        sobot_webview_goback.setEnabled(false);
        sobot_webview_forward.setEnabled(false);

        resetViewDisplay();
        initWebView();
        mWebView.loadUrl(mUrl);
        LogUtils.i("webViewActivity---" + mUrl);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onLeftMenuClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_btn_reconnect) {
            if (!TextUtils.isEmpty(mUrl)) {
                resetViewDisplay();
            }
        } else if (view == sobot_webview_forward) {
            mWebView.goForward();
        } else if (view == sobot_webview_goback) {
            mWebView.goBack();
        } else if (view == sobot_webview_reload) {
            mWebView.reload();
        } else if (view == sobot_webview_copy) {
            copyUrl(mUrl);
        }
    }

    private void copyUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 11) {
            LogUtils.i("API是大于11");
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(url);
            cmb.getText();
        } else {
            LogUtils.i("API是小于11");
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(url);
            cmb.getText();
        }

        ToastUtil.showToast(getApplicationContext(), CommonUtils.getResString(WebViewActivity.this, "sobot_ctrl_v_success"));
    }

    /**
     * 根据有无网络显示不同的View
     */
    private void resetViewDisplay() {
        if (CommonUtils.isNetWorkConnected(getApplicationContext())) {
            mWebView.setVisibility(View.VISIBLE);
            sobot_webview_toolsbar.setVisibility(View.VISIBLE);
            sobot_rl_net_error.setVisibility(View.GONE);
        } else {
            mWebView.setVisibility(View.GONE);
            sobot_webview_toolsbar.setVisibility(View.GONE);
            sobot_rl_net_error.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            } catch (Exception e) {
                //ignor
            }
        }
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.getSettings().setDefaultFontSize(16);
        mWebView.getSettings().setTextZoom(100);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以使用localStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " sobot");

        // 应用可以有数据库
        mWebView.getSettings().setDatabaseEnabled(true);

        // 应用可以有缓存
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //注释的地方是打开其它应用，比如qq
                /*if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                    return true;
                }*/
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                sobot_webview_goback.setEnabled(mWebView.canGoBack());
                sobot_webview_forward.setEnabled(mWebView.canGoForward());
                if (!mUrl.replace("http://", "").replace("https://", "").equals(view.getTitle())) {
                    setTitle(view.getTitle());
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.i("网页--title---：" + title);
                if (!mUrl.replace("http://", "").replace("https://", "").equals(title)) {
                    setTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                } else if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            final ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("url", mUrl);
        super.onSaveInstanceState(outState);
    }
}