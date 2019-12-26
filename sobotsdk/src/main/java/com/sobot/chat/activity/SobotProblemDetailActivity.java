package com.sobot.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseHelpCenterActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.StDocModel;
import com.sobot.chat.api.model.StHelpDocModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

/**
 * 帮助中心问题详情
 */
public class SobotProblemDetailActivity extends SobotBaseHelpCenterActivity implements View.OnClickListener {
    public static final String EXTRA_KEY_DOC = "extra_key_doc";

    public static final String DEFAULT_STYLE = "<style>*,body,html,div,p,img{border:0;margin:0;padding:0;} </style>";

    private StDocModel mDoc;
    private WebView mWebView;
    private View mBottomBtn;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_problem_detail");
    }

    public static Intent newIntent(Context context, Information information, StDocModel data) {
        Intent intent = new Intent(context, SobotProblemDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.putExtra(EXTRA_KEY_DOC, data);
        return intent;
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        super.initBundleData(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mDoc = (StDocModel) intent.getSerializableExtra(EXTRA_KEY_DOC);
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_grey_selector"), getResString("sobot_back"), true);
        setTitle(getResString("sobot_problem_detail_title"));
        mBottomBtn = findViewById(getResId("ll_bottom"));
        mBottomBtn.setOnClickListener(this);
        mWebView = (WebView) findViewById(getResId("sobot_webView"));
        initWebView();
    }

    @Override
    protected void initData() {
        ZhiChiApi api = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        api.getHelpDocByDocId(SobotProblemDetailActivity.this, mInfo.getApp_key(), mDoc.getDocId(), new StringResultCallBack<StHelpDocModel>() {

            @Override
            public void onSuccess(StHelpDocModel data) {
                String answerDesc = data.getAnswerDesc();
                if (!TextUtils.isEmpty(answerDesc)) {
                    //修改图片高度为自适应宽度
                    answerDesc = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "    <head>\n" +
                            "        <meta charset=\"utf-8\">\n" +
                            "        <title></title>\n" +
                            "        <style>\n" +
                            "            img{\n" +
                            "                width: auto;\n" +
                            "                height:auto;\n" +
                            "                max-height: 100%;\n" +
                            "                max-width: 100%;\n" +
                            "            }\n" +
                            "        </style>\n" +
                            "    </head>\n" +
                            "    <body>" + answerDesc + "  </body>\n" +
                            "</html>";
                    //显示文本内容
                    mWebView.loadData(DEFAULT_STYLE + answerDesc, "text/html; charset=UTF-8", "UTF-8");
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            } catch (Exception e) {
                //ignor
            }
        }
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //检测到下载文件就打开系统浏览器
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri content = Uri.parse(url);
                intent.setData(content);
                startActivity(intent);
            }
        });
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.getSettings().setDefaultFontSize(14);
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
        //把html中的内容放大webview等宽的一列中
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgReset();
            }

            @Override
            // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (SobotOption.hyperlinkListener != null) {
                    SobotOption.hyperlinkListener.onUrlClick(url);
                    return true;
                }
                if (SobotOption.newHyperlinkListener != null) {
                    //如果返回true,拦截;false 不拦截
                    boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(url);
                    if (isIntercept) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     **/
    private void imgReset() {
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                "}" +
                "})()");
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
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
    public void onClick(View v) {
        if (v == mBottomBtn) {
            SobotApi.startSobotChat(getApplicationContext(), mInfo);
        }
    }
}