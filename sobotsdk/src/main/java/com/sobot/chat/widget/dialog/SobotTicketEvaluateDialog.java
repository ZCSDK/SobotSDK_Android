package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.api.model.SobotUserTicketEvaluate;
import com.sobot.chat.widget.SobotEditTextLayout;
import com.sobot.chat.widget.dialog.base.SobotActionSheet;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.List;

/**
 * 评价界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotTicketEvaluateDialog extends SobotActionSheet {
    private Activity mContext;
    private LinearLayout sobot_negativeButton;
    private LinearLayout coustom_pop_layout;
    private RatingBar sobot_ratingBar;//评价  打分
    private TextView sobot_ratingBar_title;//评价  对人工客服打分不同显示不同的内容
    private EditText sobot_add_content;

    private SobotEditTextLayout setl_submit_content;//评价框
    private Button sobot_close_now;//提交评价按钮
    private SobotUserTicketEvaluate mEvaluate;

    public SobotTicketEvaluateDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public SobotTicketEvaluateDialog(Activity context, SobotUserTicketEvaluate evaluate) {
        super(context);
        mEvaluate = evaluate;
        this.mContext = context;
    }

    @Override
    protected String getLayoutStrName() {
        return "sobot_layout_ticket_evaluate";
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = (LinearLayout) findViewById(getResId("sobot_evaluate_container"));
        }
        return coustom_pop_layout;
    }

    @Override
    protected void initView() {
        sobot_add_content = (EditText) findViewById(getResId("sobot_add_content"));
        sobot_close_now = (Button) findViewById(getResId("sobot_close_now"));
        sobot_ratingBar = (RatingBar) findViewById(getResId("sobot_ratingBar"));
        setl_submit_content = (SobotEditTextLayout) findViewById(getResId("setl_submit_content"));
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobotTicketEvaluateDialog.this.dismiss();
            }
        });
        sobot_ratingBar_title = (TextView) findViewById(getResId("sobot_ratingBar_title"));
        if (mEvaluate.isOpen()) {
            sobot_add_content.setVisibility(mEvaluate.isTxtFlag() ? View.VISIBLE : View.GONE);
        }
        setViewListener();
    }

    @Override
    protected void initData() {

    }

    private void setViewListener() {

        sobot_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int score = (int) Math.ceil(sobot_ratingBar.getRating());
                if (score > 0 && score <= 5) {
                    List<SobotUserTicketEvaluate.TicketScoreInfooListBean> scoreInfooList = mEvaluate.getTicketScoreInfooList();
                    if (scoreInfooList != null && scoreInfooList.size() >= score) {
                        SobotUserTicketEvaluate.TicketScoreInfooListBean data = scoreInfooList.get(5 - score);
                        sobot_ratingBar_title.setText(data.getScoreExplain());
                    }
                }
            }
        });
        sobot_ratingBar.setRating(5);
        sobot_close_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交评价
                if (mContext instanceof SobotTicketEvaluateCallback) {
                    SobotTicketEvaluateCallback callback = (SobotTicketEvaluateCallback) mContext;
                    int score = (int) Math.ceil(sobot_ratingBar.getRating());
                    KeyboardUtil.hideKeyboard(sobot_add_content);
                    callback.submitEvaluate(score, sobot_add_content.getText().toString());
                    dismiss();
                }
            }
        });


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

    public interface SobotTicketEvaluateCallback {
        void submitEvaluate(int score, String remark);
    }
}