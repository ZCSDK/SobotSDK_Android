package com.sobot.chat.widget.kpswitch.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.kpswitch.view.emoticon.EmoticonsFuncView;
import com.sobot.chat.widget.kpswitch.view.emoticon.EmoticonsIndicatorView;
import com.sobot.chat.widget.kpswitch.view.plus.SobotPlusPageView;
import com.sobot.chat.widget.kpswitch.widget.adpater.PageSetAdapter;
import com.sobot.chat.widget.kpswitch.widget.adpater.PlusAdapter;
import com.sobot.chat.widget.kpswitch.widget.data.PageSetEntity;
import com.sobot.chat.widget.kpswitch.widget.data.PlusPageEntity;
import com.sobot.chat.widget.kpswitch.widget.data.PlusPageSetEntity;
import com.sobot.chat.widget.kpswitch.widget.interfaces.PageViewInstantiateListener;
import com.sobot.chat.widget.kpswitch.widget.interfaces.PlusDisplayListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天面板   更多菜单
 *
 * @author Created by jinxl on 2018/7/31.
 */
public class ChattingPanelUploadView extends BaseChattingPanelView implements View.OnClickListener, EmoticonsFuncView.OnEmoticonsPageViewListener {

    private static final String ACTION_SATISFACTION = "sobot_action_satisfaction";
    private static final String ACTION_LEAVEMSG = "sobot_action_leavemsg";
    private static final String ACTION_PIC = "sobot_action_pic";
    private static final String ACTION_CAMERA = "sobot_action_camera";
    private static final String ACTION_CHOOSE_FILE = "sobot_action_choose_file";


    private List<SobotPlusEntity> robotList = new ArrayList<>();
    private List<SobotPlusEntity> operatorList = new ArrayList<>();

    //当前接待模式
    private int mCurrentClientMode = -1;
    private EmoticonsFuncView mEmoticonsFuncView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private PageSetAdapter pageSetAdapter;
    private SobotPlusClickListener mListener;

    public ChattingPanelUploadView(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        return View.inflate(context, getResLayoutId("sobot_upload_layout"), null);
    }

    @Override
    public void initData() {

        int leaveMsg = SharedPreferencesUtil.getIntData(context, ZhiChiConstant.sobot_msg_flag, ZhiChiConstant.sobot_msg_flag_open);
        mEmoticonsFuncView = (EmoticonsFuncView) getRootView().findViewById(getResId("view_epv"));
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) getRootView().findViewById(getResId("view_eiv")));
        mEmoticonsFuncView.setOnIndicatorListener(this);

        //图片
        SobotPlusEntity picEntity = new SobotPlusEntity(getResDrawableId("sobot_tack_picture_button_selector"), getResString("sobot_upload"), ACTION_PIC);
        //拍照
        SobotPlusEntity cameraEntity = new SobotPlusEntity(getResDrawableId("sobot_camera_picture_button_selector"), getResString("sobot_attach_take_pic"), ACTION_CAMERA);
        //文件
        SobotPlusEntity fileEntity = new SobotPlusEntity(getResDrawableId("sobot_choose_file_btn_selector"), getResString("sobot_choose_file"), ACTION_CHOOSE_FILE);
        //留言
        SobotPlusEntity leavemsgEntity = new SobotPlusEntity(getResDrawableId("sobot_leavemsg_selector"), getResString("sobot_str_bottom_message"), ACTION_LEAVEMSG);
        //评价
        SobotPlusEntity satisfactionEntity = new SobotPlusEntity(getResDrawableId("sobot_picture_satisfaction_selector"), getResString("sobot_str_bottom_satisfaction"), ACTION_SATISFACTION);

        robotList.clear();
        if (leaveMsg == ZhiChiConstant.sobot_msg_flag_open) {
            robotList.add(leavemsgEntity);
        }
        robotList.add(satisfactionEntity);

        operatorList.clear();
        operatorList.add(picEntity);
        operatorList.add(cameraEntity);
        operatorList.add(fileEntity);
        if (leaveMsg == ZhiChiConstant.sobot_msg_flag_open) {
            operatorList.add(leavemsgEntity);
        }
        operatorList.add(satisfactionEntity);

    }

    public static class SobotPlusEntity {
        public int iconResId;
        public String name;
        public String action;

        /**
         * 自定义菜单实体类
         *
         * @param iconResId 菜单图标
         * @param name      菜单名称
         * @param action    菜单动作 当点击按钮时会将对应action返回给callback
         *                  以此作为依据，判断用户点击了哪个按钮
         */
        public SobotPlusEntity(int iconResId, String name, String action) {
            this.iconResId = iconResId;
            this.name = name;
            this.action = action;
        }
    }

    private void setAdapter(List<SobotPlusEntity> datas) {
        if (pageSetAdapter == null) {
            pageSetAdapter = new PageSetAdapter();
        } else {
            pageSetAdapter.getPageSetEntityList().clear();
        }

        PlusPageSetEntity pageSetEntity
                = new PlusPageSetEntity.Builder()
                .setLine(getResInteger("sobot_plus_menu_line"))
                .setRow(getResInteger("sobot_plus_menu_row"))
                .setDataList(datas)
                .setIPageViewInstantiateItem(new PageViewInstantiateListener<PlusPageEntity>() {
                    @Override
                    public View instantiateItem(ViewGroup container, int position, PlusPageEntity pageEntity) {
                        if (pageEntity.getRootView() == null) {
                            //下面这个view  就是一个gridview
                            SobotPlusPageView pageView = new SobotPlusPageView(container.getContext());
                            pageView.setNumColumns(pageEntity.getRow());
                            pageEntity.setRootView(pageView);
                            try {
                                PlusAdapter adapter = new PlusAdapter(container.getContext(), pageEntity, mListener);
//                                adapter.setItemHeightMaxRatio(1.8);
                                adapter.setOnDisPlayListener(getPlusItemDisplayListener(mListener));
                                pageView.getGridView().setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return pageEntity.getRootView();
                    }
                })
                .build();
        pageSetAdapter.add(pageSetEntity);
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    /**
     * 这个是adapter里面的bindview回调
     * 作用就是绑定数据用的
     *
     * @param plusClickListener 点击表情的回调
     * @return
     */
    public PlusDisplayListener<Object> getPlusItemDisplayListener(final ChattingPanelUploadView.SobotPlusClickListener plusClickListener) {
        return new PlusDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, PlusAdapter.ViewHolder viewHolder, Object object) {
                final SobotPlusEntity plusEntity = (SobotPlusEntity) object;
                if (plusEntity == null) {
                    return;
                }
                // 显示菜单
                //viewHolder.ly_root.setBackgroundResource(getResDrawableId("sobot_bg_emoticon"));
                viewHolder.mMenu.setText(plusEntity.name);
                Drawable drawable = context.getResources().getDrawable(plusEntity.iconResId);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                viewHolder.mMenu.setCompoundDrawables(null, drawable, null, null);
                viewHolder.mMenu.setTag(plusEntity.action);


                viewHolder.rootView.setOnClickListener(ChattingPanelUploadView.this);
            }
        };
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {

    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    public interface SobotPlusClickListener extends SobotBasePanelListener {
        void btnPicture();

        void btnCameraPicture();

        void btnSatisfaction();

        void startToPostMsgActivty(boolean flag);

        void chooseFile();
    }

    @Override
    public void setListener(SobotBasePanelListener listener) {
        if (listener != null && listener instanceof ChattingPanelUploadView.SobotPlusClickListener) {
            mListener = (ChattingPanelUploadView.SobotPlusClickListener) listener;
        }
    }

    @Override
    public String getRootViewTag() {
        return "ChattingPanelUploadView";
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            View sobot_plus_menu = v.findViewById(getResId("sobot_plus_menu"));
            String action = (String) sobot_plus_menu.getTag();
            if (ACTION_SATISFACTION.equals(action)) {
                //评价客服或机器人
                mListener.btnSatisfaction();
            } else if (ACTION_LEAVEMSG.equals(action)) {
                //留言
                mListener.startToPostMsgActivty(false);
            } else if (ACTION_PIC.equals(action)) {
                //图库
                mListener.btnPicture();
            } else if (ACTION_CAMERA.equals(action)) {
                //拍照
                mListener.btnCameraPicture();
            } else if (ACTION_CHOOSE_FILE.equals(action)) {
                //选择文件
                mListener.chooseFile();
            } else {
                if (SobotUIConfig.pulsMenu.sSobotPlusMenuListener != null) {
                    SobotUIConfig.pulsMenu.sSobotPlusMenuListener.onClick(v, action);
                }
            }
        }
    }

    @Override
    public void onViewStart(Bundle bundle) {
        int tmpClientMode = bundle.getInt("current_client_model");
        if (mCurrentClientMode == -1 || mCurrentClientMode != tmpClientMode) {
            //在初次调用或者接待模式改变时修改view
            List<SobotPlusEntity> tmpList = new ArrayList<>();
            if (bundle.getInt("current_client_model") == ZhiChiConstant.client_model_robot) {
                tmpList.addAll(robotList);
            } else {
                tmpList.addAll(operatorList);
                if (SobotUIConfig.pulsMenu.operatorMenus != null) {
                    tmpList.addAll(SobotUIConfig.pulsMenu.operatorMenus);
                }
            }
            if (SobotUIConfig.pulsMenu.menus != null) {
                tmpList.addAll(SobotUIConfig.pulsMenu.menus);
            }
            setAdapter(tmpList);
        }

        mCurrentClientMode = tmpClientMode;
    }
}