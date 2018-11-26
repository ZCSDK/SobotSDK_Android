package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotFilesAdapter;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SobotChooseFileActivity extends SobotBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView sobot_lv_files;
    private TextView sobot_tv_send;
    private TextView sobot_tv_total;

    private File mRootDir = Environment.getExternalStorageDirectory();
    private File mCurrentDir;
    private SobotFilesAdapter mAdapter;
    private List<File> mDatas = new ArrayList<>();

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_choose_file");
    }

    @Override
    protected void initView() {
        setTitle(getResString("sobot_internal_memory"));
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), getResString("sobot_back"), true);
        sobot_lv_files = (ListView) findViewById(getResId("sobot_lv_files"));
        sobot_tv_send = (TextView) findViewById(getResId("sobot_tv_send"));
        sobot_tv_total = (TextView) findViewById(getResId("sobot_tv_total"));
        sobot_tv_send.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (!checkStoragePermission() || !CommonUtils.isExitsSdcard()) {
            return;
        }
        mCurrentDir = mRootDir;

        showCurrentFiles(mCurrentDir);

        sobot_lv_files.setOnItemClickListener(this);
    }

    private void showCurrentFiles(File dir) {
        if (dir.isDirectory()) {
            File[] childFiles = getChildFiles(dir);
            showData(childFiles);
        }
    }

    private void showData(File[] files) {
        mDatas.clear();
        if (files != null) {
            mDatas.addAll(Arrays.asList(files));
        }
        Collections.sort(mDatas, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o2.getName().compareTo(o1.getName());
            }
        });
        if (mAdapter == null) {
            mAdapter = new SobotFilesAdapter(SobotChooseFileActivity.this, mDatas);
            sobot_lv_files.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private File[] getChildFiles(File dir) {
        if (dir.isDirectory()) {
            return dir.listFiles();
        }
        return null;
    }

    @Override
    protected void onLeftMenuClick(View view) {
        goback();
    }

    @Override
    public void onBackPressed() {
        goback();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            File file = mDatas.get(position);
            if (file != null) {
                if (file.isDirectory()) {
                    mCurrentDir = file;
                    showCurrentFiles(file);
                } else {
                    if (mAdapter != null) {
                        String totalSize;
                        if (mAdapter.isCheckedFile(file)) {
                            mAdapter.setCheckedFile(null);
                            totalSize = "0B";
                            sobot_tv_send.setEnabled(false);
                        } else {
                            mAdapter.setCheckedFile(file);
                            totalSize = Formatter.formatFileSize(this, file.length());
                            sobot_tv_send.setEnabled(true);
                        }
                        mAdapter.notifyDataSetChanged();
                        sobot_tv_total.setText(String.format(getResString("sobot_files_selected"), totalSize));
                    }
                }
            }
        } catch (Exception e) {
            //ignor
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_send) {
            File checkedFile = mAdapter.getCheckedFile();
            if (checkedFile != null) {
                Intent data = new Intent();
                data.putExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE, checkedFile);
                setResult(ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE, data);
                finish();
            }
        }
    }

    private void goback() {
        if (!mRootDir.equals(mCurrentDir)) {
            mCurrentDir = mCurrentDir.getParentFile();
            showCurrentFiles(mCurrentDir);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}