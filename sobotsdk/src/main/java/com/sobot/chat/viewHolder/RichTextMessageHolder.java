package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.model.Suggestions;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.VersionUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.ArrayList;

/**
 * 富文本消息
 * Created by jinxl on 2017/3/17.
 */
public class RichTextMessageHolder extends MessageHolderBase {
    public ZhiChiMessageBase message;
    private Context mContext;
    private TextView msg; // 聊天的消息内容
    private TextView sobot_msg_title; // 机会人回复的富文本标题
    private TextView sobot_msgStripe; // 多轮会话中配置的引导语
    private LinearLayout answersList;
    private TextView stripe;
    // 答案
    private ImageView bigPicImage; // 大的图片的展示
    private TextView rendAllText; // 阅读全文
    private View read_alltext_line;
    private TextView sobot_tv_transferBtn;
    private TextView sobot_tv_likeBtn;//机器人评价 顶 的按钮
    private TextView sobot_tv_dislikeBtn;//机器人评价 踩 的按钮

    public RichTextMessageHolder(Context context, View convertView){
        super(context,convertView);
        this.mContext = context;
        imgHead = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_imgHead"));
        name = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_name"));
        msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
        sobot_msg_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg_title"));
        sobot_msgStripe = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msgStripe"));
        read_alltext_line = convertView.findViewById(ResourceUtils.getIdByName(context, "id", "read_alltext_line"));

        // 富文本的大图片
        bigPicImage = (ImageView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_bigPicImage"));
        // 阅读全文
        rendAllText = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_rendAllText"));

        stripe = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_stripe"));
        answersList = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_answersList"));

        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_likeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_likeBtn"));
        sobot_tv_dislikeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_dislikeBtn"));
    }

    @Override
    public void bindData(final Context context,final ZhiChiMessageBase message) {
        this.message = message;
        // 更具消息类型进行对布局的优化
        if (message.getAnswer() != null) {

            setupMsgContent(context,message);
            if (!TextUtils.isEmpty(message.getAnswer().getRichpricurl())) {
                bigPicImage.setVisibility(View.VISIBLE);
                SobotBitmapUtil.display(context, CommonUtils.encode(message.getAnswer()
                        .getRichpricurl()), bigPicImage);
                // 点击大图 查看大图的内容
                bigPicImage.setOnClickListener(new ImageClickLisenter(context,message
                        .getAnswer().getRichpricurl()));
            } else {
                bigPicImage.setVisibility(View.GONE);
            }

            if (1 == message.getSugguestionsFontColor()){
                if (message.getSdkMsg() != null && !TextUtils.isEmpty(message.getSdkMsg().getQuestion())){
                    sobot_msg_title.setVisibility(View.VISIBLE);
                    sobot_msg_title.setText(message.getSdkMsg().getQuestion());
                } else {
                    sobot_msg_title.setVisibility(View.GONE);
                }
            } else if (!TextUtils.isEmpty(message.getQuestion())){
                sobot_msg_title.setVisibility(View.VISIBLE);
                sobot_msg_title.setText(message.getQuestion());
            } else {
                sobot_msg_title.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(message.getAnswer().getRichmoreurl())) {
                read_alltext_line.setVisibility(View.VISIBLE);
                rendAllText.setVisibility(View.VISIBLE);
                rendAllText.setOnClickListener(new ReadAllTextLisenter(context,message.getAnswer().getRichmoreurl()));
                msg.setMaxLines(3);
            } else {
                read_alltext_line.setVisibility(View.GONE);
                rendAllText.setVisibility(View.GONE);
                msg.setMaxLines(Integer.MAX_VALUE);
            }

            if (!TextUtils.isEmpty(message.getAnswer().getMsgStripe())) {
                sobot_msgStripe.setVisibility(View.VISIBLE);
                sobot_msgStripe.setText(message.getAnswer().getMsgStripe());
            } else {
                sobot_msgStripe.setVisibility(View.GONE);
            }
        }

        if ("1".equals(message.getRictype())) {
            bigPicImage.setVisibility(View.VISIBLE);
            rendAllText.setVisibility(View.VISIBLE);
            SobotBitmapUtil.display(context, CommonUtils.encode(message.getPicurl()), bigPicImage);
            rendAllText.setVisibility(View.VISIBLE);
            rendAllText.setOnClickListener(new ReadAllTextLisenter(context,message
                    .getAnswer().getRichmoreurl()));
        } else if ("0".equals(message.getRictype())) {// 代表无图片的格式
            bigPicImage.setVisibility(View.GONE);
            rendAllText.setVisibility(View.GONE);
        }

        // 回复语的答复
        String stripeContent = message.getStripe() != null?message.getStripe().trim():"";
        if (!TextUtils.isEmpty(stripeContent)) {
            // 设置提醒的内容
            stripe.setVisibility(View.VISIBLE);
            HtmlTools.getInstance(context).setRichText(stripe,stripeContent, ResourceUtils.getIdByName(context, "color","sobot_color_link"));
        } else {
            stripe.setText(null);
            stripe.setVisibility(View.GONE);
        }

        if (message.getSugguestions() != null && message.getSugguestions().length > 0) {
            if (message.getListSuggestions() != null && message.getListSuggestions().size() > 0) {
                ArrayList<Suggestions> listSuggestions = message.getListSuggestions();
                answersList.removeAllViews();
                answersList.setVisibility(View.VISIBLE);
                for (int i = 0; i < listSuggestions.size(); i++) {
                    TextView answer = ChatUtils.initAnswerItemTextView(context, false);
                    int currentItem = i + 1;
                    answer.setOnClickListener(new AnsWerClickLisenter(context, null,
                            processPrefix(message,currentItem) + listSuggestions.get(i).getQuestion(), null, listSuggestions.get(i).getDocId(), msgCallBack));
                    String tempStr = processPrefix(message,currentItem) + listSuggestions.get(i).getQuestion();
                    answer.setText(tempStr);
                    answersList.addView(answer);
                }
            } else {
                String[] answerStringList = message.getSugguestions();
                answersList.removeAllViews();
                answersList.setVisibility(View.VISIBLE);
                for (int i = 0; i < answerStringList.length; i++) {
                    TextView answer = ChatUtils.initAnswerItemTextView(context, true);
                    int currentItem = i + 1;
                    String tempStr = processPrefix(message,currentItem) + answerStringList[i];
                    answer.setText(tempStr);
                    answersList.addView(answer);
                }
            }
        } else {
            answersList.setVisibility(View.GONE);
        }

        checkShowTransferBtn();
        resetRevaluateBtn();

        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())){
                    ToastUtil.showCopyPopWindows(context,view, message.getAnswer().getMsg(), 30,0);
                }
                return false;
            }
        });
    }

    private void checkShowTransferBtn(){
        if(message.isShowTransferBtn()){
            showTransferBtn();
        }else {
            hideTransferBtn();
        }
    }

    /**
     * 隐藏转人工按钮
     */
    public void hideTransferBtn(){
        sobot_tv_transferBtn.setVisibility(View.GONE);
        if(message != null){
            message.setShowTransferBtn(false);
        }
    }

    /**
     * 显示转人工按钮
     */
    public void showTransferBtn(){
        sobot_tv_transferBtn.setVisibility(View.VISIBLE);
        if(message != null){
            message.setShowTransferBtn(true);
        }
        sobot_tv_transferBtn.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if(msgCallBack != null){
                    msgCallBack.doClickTransferBtn();
                }
            }
        });
    }

    public void resetRevaluateBtn(){
        //顶 踩的状态 0 不显示顶踩按钮  1显示顶踩 按钮  2 显示顶之后的view  3显示踩之后view
        switch (message.getRevaluateState()){
            case 1:
                showRevaluateBtn();
                break;
            case 2:
                showLikeWordView();
                break;
            case 3:
                showDislikeWordView();
                break;
            default:
                hideRevaluateBtn();
                break;
        }
    }

    /**
     * 显示 顶踩 按钮
     */
    public void showRevaluateBtn(){
        sobot_tv_likeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setText("");
        if(mContext != null){
            sobot_tv_dislikeBtn.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_cai_selector"));
        }
        sobot_tv_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(false);
            }
        });
    }

    /**
     * 顶踩 操作
     * @param revaluateFlag true 顶  false 踩
     */
    private void doRevaluate(boolean revaluateFlag){
        if(msgCallBack != null){
            msgCallBack.doRevaluate(revaluateFlag,message);
        }
    }

    /**
     * 隐藏 顶踩 按钮
     */
    public void hideRevaluateBtn(){
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
    }

    /**
     * 显示顶之后的view
     */
    public void showLikeWordView(){
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        VersionUtils.setBackground(null,sobot_tv_dislikeBtn);
        sobot_tv_dislikeBtn.setText(getResStringId("sobot_robot_like"));
    }

    /**
     * 显示踩之后的view
     */
    public void showDislikeWordView(){
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        VersionUtils.setBackground(null,sobot_tv_dislikeBtn);
        sobot_tv_dislikeBtn.setText(getResStringId("sobot_robot_dislike"));
    }

    public int getResStringId(String name) {
        if (mContext != null){
            return ResourceUtils.getIdByName(mContext, "string", name);
        } else {
            return 0;
        }
    }

    // 查看阅读全文的监听
    public static class ReadAllTextLisenter implements View.OnClickListener {
        private String mUrlContent;
        private Context context;

        public ReadAllTextLisenter(Context context,String urlContent) {
            super();
            this.mUrlContent = urlContent;
            this.context = context;
        }

        @Override
        public void onClick(View arg0) {

            if (!mUrlContent.startsWith("http://")
                    && !mUrlContent.startsWith("https://")) {
                mUrlContent = "http://" + mUrlContent;
            }
            // 内部浏览器
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", mUrlContent);
            context.startActivity(intent);
        }
    }

    // 问题的回答监听
    public static class AnsWerClickLisenter implements View.OnClickListener {

        private String msgContent;
        private String id;
        private ImageView img;
        private String docId;
        private Context context;
        private SobotMsgAdapter.SobotMsgCallBack mMsgCallBack;

        public AnsWerClickLisenter(Context context, String id, String msgContent, ImageView image,
                                   String docId, SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            super();
            this.context = context;
            this.msgContent = msgContent;
            this.id = id;
            this.img = image;
            this.docId = docId;
            mMsgCallBack = msgCallBack;
        }

        @Override
        public void onClick(View arg0) {
            if (img != null) {
                img.setVisibility(View.GONE);
            }

            if (mMsgCallBack != null){
                mMsgCallBack.hidePanelAndKeyboard();
                ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                msgObj.setContent(msgContent);
                msgObj.setId(id);
                mMsgCallBack.sendMessageToRobot(msgObj, 0, 1, docId);
            }
        }
    }

    private void setupMsgContent(Context  context,ZhiChiMessageBase message){
        if ( message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
            msg.setVisibility(View.VISIBLE);
            String robotAnswer = message.getAnswer().getMsg();
            HtmlTools.getInstance(context).setRichText(msg,robotAnswer.replaceAll("\n", "<br/>"), ResourceUtils.getIdByName(context, "color","sobot_color_link"));
        } else {
            msg.setVisibility(View.GONE);
        }
    }

    private String processPrefix(final ZhiChiMessageBase message,int num){
        if (message != null && message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null
                && message.getAnswer().getMultiDiaRespInfo().getIcLists() != null) {
            return "•";
        }
        return num + "、";
    }


}
