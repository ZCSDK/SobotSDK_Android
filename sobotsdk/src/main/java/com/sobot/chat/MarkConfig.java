package com.sobot.chat;

/**
 * 开关常量标识
 */
public class MarkConfig {

    private static int markValue = 0b00000000010;


    /**
     * 右起第一位 横屏
     */
    public static final int LANDSCAPE_SCREEN = 0b1;
    /**
     * 留言关闭状态下，可回复
     */
    public static final int LEAVE_COMPLETE_CAN_REPLY =0b10;

    /**
     * 获取开关位
     * @param mark 开关名
     * @return 1 true,0 false
     */
    public static boolean getON_OFF(int mark) {
        if ((markValue & mark) == mark) {
            return true;
        }
        return false;
    }


    /**
     * 设置开关
     * @param mark 开关位名
     * @param isON true 1,false 0
     */
    public static void setON_OFF(int mark, boolean isON) {
        if (isON) {
            markValue = markValue | mark;
        } else {
            markValue=markValue&(~mark);

        }
    }

    public static void main(String[] args){

//        setON_OFF(LANDSCAPE_SCREEN,true);
//        System.out.println(Integer.toBinaryString(markValue));

        System.out.println(getON_OFF(LANDSCAPE_SCREEN));

    }

}
