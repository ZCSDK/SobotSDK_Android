package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SobotCustomTitleActivity extends AppCompatActivity implements View.OnClickListener{

    private String sobot_title_value;//默认头部文案
    private RadioGroup sobot_rg_title_value_show;//设置SDK头部显示文案
    private RadioButton sobot_show_custom_nike;//客服昵称
    private RadioButton sobot_show_fixed_text;//固定文案，自定义文案
    private RadioButton sobot_show_company_name;//公司名称
    private EditText sobot_tv_chat_index_show_text;//如果聊天页头部显示固定文案，则要获取text
    private String sobot_title_value_show_type = "0";//头部显示文案类型默认值
    private ImageView sobot_tv_left;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_custom_title_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initViews();
    }

    private void initViews(){
        sobot_tv_left = (ImageView) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("自定义标题设置");
        sobot_show_custom_nike = (RadioButton) findViewById(R.id.sobot_show_custom_nike);
        sobot_show_fixed_text = (RadioButton) findViewById(R.id.sobot_show_fixed_text);
        sobot_show_company_name = (RadioButton) findViewById(R.id.sobot_show_company_name);
        sobot_rg_title_value_show = (RadioGroup) findViewById(R.id.sobot_rg_title_value_show);
        sobot_tv_chat_index_show_text = (EditText) findViewById(R.id.sobot_tv_chat_index_show_text);

        getSobotStartSet();
    }

    private void getSobotStartSet(){
        sobot_title_value_show_type = SobotSPUtil.getStringData(this,"sobot_title_value_show_type",sobot_title_value_show_type);
        sobot_title_value = SobotSPUtil.getStringData(this,"sobot_title_value","");
        setRadioBtnCheckedTitleVale(sobot_title_value_show_type);
    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }

    private void saveSobotStartSet(){
        SobotSPUtil.saveStringData(this,"sobot_title_value",sobot_tv_chat_index_show_text.getText().toString());
        SobotSPUtil.saveStringData(this,"sobot_title_value_show_type",getTitleValue().getValue() + "");
    }

    private void setRadioBtnCheckedTitleVale(String type){
        switch (type){
            case "0":
                sobot_tv_chat_index_show_text.setText("");
                sobot_show_custom_nike.setChecked(true);
                break;
            case "1":
                if (!TextUtils.isEmpty(sobot_title_value)){
                    sobot_tv_chat_index_show_text.setText(sobot_title_value);
                }
                sobot_show_fixed_text.setChecked(true);
                break;
            case "2":
                sobot_tv_chat_index_show_text.setText("");
                sobot_show_company_name.setChecked(true);
                break;
            default:
                sobot_tv_chat_index_show_text.setText("");
                break;
        }
    }

    private SobotChatTitleDisplayMode getTitleValue(){
        SobotChatTitleDisplayMode result = SobotChatTitleDisplayMode.Default;
        int id = sobot_rg_title_value_show.getCheckedRadioButtonId();
        switch (id){
            case R.id.sobot_show_custom_nike://显示客服昵称
                result = SobotChatTitleDisplayMode.Default;
                break;
            case R.id.sobot_show_fixed_text://显示固定文案
                result = SobotChatTitleDisplayMode.ShowFixedText;
                break;
            case R.id.sobot_show_company_name://显示公司名称
                result = SobotChatTitleDisplayMode.ShowCompanyName;
                break;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left){
            saveSobotStartSet();
            finish();
        }
    }
}