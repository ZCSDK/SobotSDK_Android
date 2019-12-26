package com.sobot.chat.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.SobotRobotGuess;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.emoji.InputHelper;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动补全的editText
 */
public class ContainsEmojiEditText extends EditText implements View.OnFocusChangeListener {
    private static final String LAYOUT_CONTENT_VIEW_LAYOUT_RES_NAME = "sobot_layout_auto_complete";
    private static final String LAYOUT_AUTOCOMPELTE_ITEM = "sobot_item_auto_complete_menu";
    private static final String SOBOT_AUTO_COMPLETE_REQUEST_CANCEL_TAG = "SOBOT_AUTO_COMPLETE_REQUEST_CANCEL_TAG";
    private static final int MAX_AUTO_COMPLETE_NUM = 3;
    Handler handler = new Handler();
    SobotCustomPopWindow mPopWindow;

    View mContentView;
    SobotAutoCompelteAdapter mAdapter;
    MyWatcher myWatcher;
    MyEmojiWatcher myEmojiWatcher;
    String mUid;
    String mRobotFlag;
    boolean mIsAutoComplete;
    SobotAutoCompleteListener autoCompleteListener;

    public ContainsEmojiEditText(Context context) {
        super(context);
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditText();
    }

    // 初始化edittext 控件
    private void initEditText() {
        myEmojiWatcher = new MyEmojiWatcher();
        addTextChangedListener(myEmojiWatcher);
        boolean supportFlag = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CONFIG_SUPPORT, false);
        if (!supportFlag) {
            return;
        }

        setOnFocusChangeListener(this);

        myWatcher = new MyWatcher();
        addTextChangedListener(myWatcher);
        if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {//横屏
            setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {//完成
                        KeyboardUtil.hideKeyboard(ContainsEmojiEditText.this);
                        doAfterTextChanged(v.getText().toString());
                        return true;
                    }
                    if (actionId==KeyEvent.ACTION_DOWN){
                        KeyboardUtil.hideKeyboard(ContainsEmojiEditText.this);
                        doAfterTextChanged(v.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void doAfterTextChanged(String s) {
        if (!mIsAutoComplete) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            dismissPop();
        } else {
            OkHttpUtils.getInstance().cancelTag(SOBOT_AUTO_COMPLETE_REQUEST_CANCEL_TAG);
            ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(getContext()).getZhiChiApi();
            zhiChiApi.robotGuess(SOBOT_AUTO_COMPLETE_REQUEST_CANCEL_TAG, mUid, mRobotFlag, s, new StringResultCallBack<SobotRobotGuess>() {
                @Override
                public void onSuccess(SobotRobotGuess result) {
                    try {
                        String originQuestion = result.getOriginQuestion();
                        String currntContent = getText().toString();
                        if (currntContent.equals(originQuestion)) {
                            //只处理当前查询到的返回值
                            List<SobotRobotGuess.RespInfoListBean> respInfoList = result.getRespInfoList();
                            showPop(ContainsEmojiEditText.this, respInfoList);
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        }
    }

    public void setRequestParams(String uid, String robotFlag) {
        this.mUid = uid;
        this.mRobotFlag = robotFlag;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            dismissPop();
        }
    }

    private class MyWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
            LogUtils.e( "beforeTextChanged: "+s.toString());
            if (!SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                doAfterTextChanged(s.toString());
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            doBeforeTextChanged();
            LogUtils.e( "beforeTextChanged: "+s.toString());
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.e( "onTextChanged: "+s.toString());
        }
    }

    /**
     * 表情监听
     */
    private class MyEmojiWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            s = InputHelper.displayEmoji(getContext(), s);
        }
    }


    public boolean isShowing() {
        if (mPopWindow != null) {
            PopupWindow popupWindow = mPopWindow.getPopupWindow();
            if (popupWindow != null) {
                return popupWindow.isShowing();
            }
        }
        return false;
    }

    private void showPop(final View anchorView, final List<SobotRobotGuess.RespInfoListBean> list) {
        if (getWindowVisibility() == View.GONE) {
            return;
        }

        if (list == null || list.size() == 0) {
            dismissPop();
            return;
        }

        View contentView = getContentView();
        //处理popWindow 显示内容
        final ListView listView = handleListView(contentView, list);
        if (mPopWindow == null) {
            mPopWindow = new SobotCustomPopWindow.PopupWindowBuilder(getContext())
                    .setView(contentView)
                    .setFocusable(false)
                    .setOutsideTouchable(false)
                    .setWidthMatchParent(true)
                    .create();
        }
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
        mPopWindow.showAsDropDown(anchorView, 0, -(anchorView.getHeight() + params.height));

        handler.post(new Runnable() {
            @Override
            public void run() {
                mPopWindow.getPopupWindow().update(anchorView, 0, -(anchorView.getHeight() + params.height), mPopWindow.getPopupWindow().getWidth(), params.height);
            }
        });
    }

    public int getResId(String name) {
        return ResourceUtils.getIdByName(getContext(), "id", name);
    }

    private ListView handleListView(View contentView, final List<SobotRobotGuess.RespInfoListBean> list) {
        final ListView listView = (ListView) contentView.findViewById(getResId("sobot_lv_menu"));
        notifyAdapter(listView, list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dismissPop();
                if (autoCompleteListener != null) {
                    SobotAutoCompelteAdapter adapter = (SobotAutoCompelteAdapter) listView.getAdapter();
                    List<SobotRobotGuess.RespInfoListBean> datas = adapter.getDatas();
                    if (datas != null && position < datas.size()) {
                        SobotRobotGuess.RespInfoListBean respInfoListBean = datas.get(position);
                        autoCompleteListener.onRobotGuessComplete(respInfoListBean.getQuestion());
                    }
                }
//                ToastUtil.showToast(getContext(), "" + position);
            }
        });
        return listView;

    }

    private void notifyAdapter(ListView listView, final List<SobotRobotGuess.RespInfoListBean> list) {
        if (mAdapter == null) {
            List<SobotRobotGuess.RespInfoListBean> tmpList = new ArrayList<>();
            tmpList.clear();
            tmpList.addAll(list);
            mAdapter = new SobotAutoCompelteAdapter(getContext(), tmpList);
            listView.setAdapter(mAdapter);
        } else {
            List<SobotRobotGuess.RespInfoListBean> datas = mAdapter.getDatas();
            if (datas != null) {
                datas.clear();
                datas.addAll(list);
            }
            mAdapter.notifyDataSetChanged();
        }
        listView.setSelection(0);

        measureListViewHeight(listView);
    }

    private void measureListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < Math.min(listAdapter.getCount(), MAX_AUTO_COMPLETE_NUM); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            int itemHeight = listItem.getMeasuredHeight();
            totalHeight += itemHeight;
        }
        // 底部分割线的高度
        int historyHeight = totalHeight
                + (listView.getDividerHeight() * listAdapter.getCount() - 1);
        if (listAdapter.getCount() > MAX_AUTO_COMPLETE_NUM) {
            historyHeight += ScreenUtils.dip2px(getContext(), 10);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
        params.height = historyHeight;
        listView.setLayoutParams(params);
    }

    public void dismissPop() {
        if (mPopWindow != null) {
            try {
                mPopWindow.dissmiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View getContentView() {
        if (mContentView == null) {
            synchronized (ContainsEmojiEditText.class) {
                if (mContentView == null) {
                    mContentView = LayoutInflater.from(getContext()).inflate(ResourceUtils.getIdByName(getContext(), "layout", LAYOUT_CONTENT_VIEW_LAYOUT_RES_NAME), null);
                }
            }
        }
        return mContentView;
    }

    private static class SobotAutoCompelteAdapter extends SobotBaseAdapter<SobotRobotGuess.RespInfoListBean> {

        private SobotAutoCompelteAdapter(Context context, List<SobotRobotGuess.RespInfoListBean> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, ResourceUtils.getIdByName(context, "layout", LAYOUT_AUTOCOMPELTE_ITEM), null);
                holder = new ViewHolder(context, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SobotRobotGuess.RespInfoListBean child = list.get(position);
            if (child != null && !TextUtils.isEmpty(child.getHighlight())) {
                holder.sobot_child_menu.setText(Html.fromHtml(child.getHighlight()));
            } else {
                holder.sobot_child_menu.setText("");
            }
            return convertView;
        }

        private static class ViewHolder {
            private TextView sobot_child_menu;

            private ViewHolder(Context context, View view) {
                sobot_child_menu = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_child_menu"));
            }
        }
    }

    public void setAutoCompleteEnable(boolean flag) {
        mIsAutoComplete = flag;
        if (!mIsAutoComplete) {
            OkHttpUtils.getInstance().cancelTag(SOBOT_AUTO_COMPLETE_REQUEST_CANCEL_TAG);
            dismissPop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeTextChangedListener(myWatcher);
        OkHttpUtils.getInstance().cancelTag(SOBOT_AUTO_COMPLETE_REQUEST_CANCEL_TAG);
        dismissPop();
        autoCompleteListener = null;
        mContentView = null;
        super.onDetachedFromWindow();
    }

    public void setSobotAutoCompleteListener(SobotAutoCompleteListener listener) {
        autoCompleteListener = listener;
    }

    public interface SobotAutoCompleteListener {
        void onRobotGuessComplete(String question);
    }

}