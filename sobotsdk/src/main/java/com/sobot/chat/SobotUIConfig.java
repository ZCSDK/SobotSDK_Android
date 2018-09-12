package com.sobot.chat;

import com.sobot.chat.listener.SobotPlusMenuListener;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;

import java.util.List;

public class SobotUIConfig {
    public static final int DEFAULT = -1;

    public static int sobot_serviceImgId = DEFAULT;//修改转人工按钮的图片
    public static int sobot_titleTextColor = DEFAULT;//修改顶部文字字体颜色
    public static boolean sobot_title_right_menu2_display = false;//toolbar右边第二个按钮是否显示（评价）
    public static int sobot_title_right_menu2_bg = DEFAULT;//修改toolbar右边第二个按钮的图片
    public static int sobot_title_right_menu2_text = DEFAULT;//修改toolbar右边第二个按钮的文字
    public static int sobot_title_right_menu2_textColor = DEFAULT;//修改toolbar右边第二个按钮的文字颜色
    public static int sobot_moreBtnImgId = DEFAULT;//修改更多按钮的图片
    public static int sobot_titleBgColor = DEFAULT;//修改顶部背景颜色
    public static int sobot_chat_left_textColor = DEFAULT;//聊天界面左边文字字体颜色
    public static int sobot_chat_left_link_textColor = DEFAULT;//聊天界面左边链接文字字体颜色
    public static int sobot_chat_left_bgColor = DEFAULT;//聊天界面左边气泡背景颜色
    public static int sobot_chat_right_bgColor = DEFAULT;//聊天界面右边气泡背景颜色
    public static int sobot_chat_right_link_textColor = DEFAULT;//聊天界面右边链接文字字体颜色
    public static int sobot_chat_right_textColor = DEFAULT;//聊天界面右边文字字体颜色
    public static int sobot_chat_bottom_bgColor = DEFAULT;//聊天界面底部布局背景颜色


    /**
     * 更多面板中的菜单配置
     */
    public static final class pulsMenu {
        public static List<ChattingPanelUploadView.SobotPlusEntity> menus;

        public static SobotPlusMenuListener sSobotPlusMenuListener;
    }
}