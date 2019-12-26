package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.sobot.chat.adapter.SobotPostMsgTmpListAdapter;
import com.sobot.chat.api.model.SobotPostMsgTemplate;
import com.sobot.chat.widget.dialog.base.SobotActionSheet;

import java.util.ArrayList;

/**
 * 选择留言模板
 * Created by jinxl on 2018/3/5.
 */
public class SobotPostMsgTmpListDialog extends SobotActionSheet implements AdapterView.OnItemClickListener, View.OnClickListener {
    private final String CANCEL_TAG = SobotPostMsgTmpListDialog.class.getSimpleName();
    private LinearLayout coustom_pop_layout;
    private LinearLayout sobot_negativeButton;
    private GridView sobot_gv;

    private ArrayList<SobotPostMsgTemplate> mDatas;

    private SobotDialogListener mListener;

    private SobotPostMsgTmpListAdapter mListAdapter;

    private SobotPostMsgTmpListDialog(Activity context) {
        super(context);
    }

    public SobotPostMsgTmpListDialog(Activity context, ArrayList<SobotPostMsgTemplate> datas, SobotDialogListener listListener) {
        super(context);
        mDatas = datas;
        mListener = listListener;
    }

    @Override
    protected String getLayoutStrName() {
        return "sobot_layout_post_msg_tmps";
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = (LinearLayout) findViewById(getResId("sobot_container"));
        }
        return coustom_pop_layout;
    }

    @Override
    protected void initView() {
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_gv = (GridView) findViewById(getResId("sobot_gv"));
        sobot_gv.setOnItemClickListener(this);
        sobot_negativeButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (mListAdapter == null) {
            mListAdapter = new SobotPostMsgTmpListAdapter(getContext(), mDatas);
            sobot_gv.setAdapter(mListAdapter);
        }
    }


    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            SobotPostMsgTemplate item = (SobotPostMsgTemplate) mListAdapter.getItem(position);
            mListener.onListItemClick(item);
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_negativeButton) {
            dismiss();
        }
    }

    public interface SobotDialogListener {
        void onListItemClick(SobotPostMsgTemplate data);
    }
}