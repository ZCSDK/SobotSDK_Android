package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.activity.SobotPhotoListActivity;
import com.sobot.chat.adapter.SobotPicListAdapter;
import com.sobot.chat.adapter.SobotPostMsgTmpListAdapter;
import com.sobot.chat.api.ZhiChiApiImpl;
import com.sobot.chat.api.apiUtils.ZhiChiUrlApi;
import com.sobot.chat.api.model.SobotPostMsgTemplate;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.listener.PermissionListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.base.SobotActionSheet;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.ArrayList;

/**
 * 选择留言模板
 * Created by jinxl on 2018/3/5.
 */
public class SobotReplyDialog extends SobotActionSheet implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Activity activity;
    private final String CANCEL_TAG = SobotReplyDialog.class.getSimpleName();
    private LinearLayout coustom_pop_layout;

    private TextView sobotTvTitle;
    private LinearLayout sobotNegativeButton;
    private EditText sobotReplyEdit;
    private GridView sobotReplyMsgPic;
    private Button sobotBtnSubmit;

    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();
    private SobotPicListAdapter adapter;
    private SobotSelectPicDialog menuWindow;
    /**
     * 删除图片弹窗
     */
    protected SobotDeleteWorkOrderDialog seleteMenuWindow;

    PicCameraListener picCameraListener;


    public SobotReplyDialog(Activity context) {
        super(context);
        this.activity=context;
    }


    @Override
    protected String getLayoutStrName() {
        return "sobot_layout_dialog_reply";
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


        sobotTvTitle = (TextView) findViewById(getResId("sobot_tv_title"));
        sobotTvTitle.setText(getResString("sobot_reply"));
        sobotNegativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobotReplyEdit = (EditText) findViewById(getResId("sobot_reply_edit"));
        sobotReplyMsgPic = (GridView) findViewById(getResId("sobot_reply_msg_pic"));
        sobotBtnSubmit = (Button) findViewById(getResId("sobot_btn_submit"));

        sobotNegativeButton.setOnClickListener(this);
        sobotBtnSubmit.setOnClickListener(this);
        adapter = new SobotPicListAdapter(getContext(), pic_list);
        sobotReplyMsgPic.setAdapter(adapter);
        initPicListView();

    }

    @Override
    protected void initData() {

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

    }

    public void setPicCameraListener(PicCameraListener picCameraListener) {
        this.picCameraListener = picCameraListener;
    }

    @Override
    public void onClick(View v) {
        if (v == sobotNegativeButton) {
            dismiss();
        }
        if (v==sobotBtnSubmit){//提交
            if (picCameraListener!=null){
                picCameraListener.submitPost(sobotReplyEdit.getText().toString(),getFileStr());
            }
        }
    }


    /**
     * 初始化图片选择的控件
     */
    private void initPicListView() {
        adapter.setOnClickItemViewListener(new SobotPicListAdapter.ViewClickListener() {
            @Override
            public void clickView(View view, int position, int type) {
                KeyboardUtil.hideKeyboard(view);
                switch (type) {
                    case SobotPicListAdapter.ADD:
                        menuWindow = new SobotSelectPicDialog(activity, itemsOnClick);
                        menuWindow.show();
                        break;
                    case SobotPicListAdapter.PIC:
                        if (picCameraListener!=null){
                            picCameraListener.startPerviewPic(adapter,position);
                        }

                        break;
                    case SobotPicListAdapter.DEL:
                        if (seleteMenuWindow == null) {
                            seleteMenuWindow = new SobotDeleteWorkOrderDialog(activity, ResourceUtils.getResString(getContext(),"sobot_do_you_delete_picture"), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    seleteMenuWindow.dismiss();
                                    if(v.getId() == getResId("btn_pick_photo")) {
                                        Log.e("onClick: ", seleteMenuWindow.getPosition() + "\tfsfdsffsd");
                                        pic_list.remove(seleteMenuWindow.getPosition());
                                        adapter.restDataView();
                                    }
                                }
                            });
                        }
                        seleteMenuWindow.setPosition(position);
                        seleteMenuWindow.show();
                        break;
                }

            }
        });
        adapter.restDataView();
    }

    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if (picCameraListener==null){
                return;
            }
            if (v.getId() == getResId("btn_take_photo")) {
                LogUtils.i("拍照");
                picCameraListener.selectPicFromCameraBySys();
            }
            if (v.getId() == getResId("btn_pick_photo")) {
                LogUtils.i("选择照片");
                picCameraListener.selectPicFromLocal();
            }
            if (v.getId() == getResId("btn_pick_vedio")) {
                LogUtils.i("选择视频");
                picCameraListener.selectVedioFromLocal();
            }

        }
    };

    public void addPicView(ZhiChiUploadAppFileModelResult item) {
        adapter.addData(item);
    }


    public String getFileStr() {
        String tmpStr = "";
        ArrayList<ZhiChiUploadAppFileModelResult> tmpList = adapter.getPicList();
        for (int i = 0; i < tmpList.size(); i++) {
            tmpStr += tmpList.get(i).getFileUrl() + ";";
        }
        return tmpStr;
    }

    public interface PicCameraListener{
        void selectPicFromCameraBySys();
        void selectPicFromLocal();
        void selectVedioFromLocal();
        void startPerviewPic(SobotPicListAdapter adapter,int position);
        void submitPost(String content,String fileStr);


    }



}