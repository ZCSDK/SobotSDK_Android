package com.sobot.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by jinxl on 2016/5/16.
 */
public class SettingItemView extends RelativeLayout implements View.OnClickListener {

    private String left_text;
    private int display_type;
    private TextView leftTextView;
    private TextView midTextView;
    private String mid_text;
    private ImageView rightImg;
    private EditText et_text;
    private String mid_hint;
    private int maxLength;
    private int mid_text_maxlines;
    private TextChangedListener textChangedListener;
    private String default_str;
    private int hintColor;
    private int leftTextColor;
    private boolean isShowArrow;
    private String value;
    private boolean enabled = true;
    private boolean isInputInt = false;
    private boolean mid_text_aliginright = false;
    private String old_str = "";//点击清除后出现的字

    private float left_text_with;
    private ContentOnFocusChangeListener contentOnFocusChangeListener;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //atts 包括
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
//        系统会在自定义属性前加上它所属的declare-styleable 的name_
        display_type = array.getInt(R.styleable.SettingItemView_display_type, 0);
        left_text = array.getString(R.styleable.SettingItemView_left_text);
        mid_text = array.getString(R.styleable.SettingItemView_mid_text);
        mid_hint = array.getString(R.styleable.SettingItemView_mid_hint);
        maxLength = array.getInteger(R.styleable.SettingItemView_maxLength, 30);
        mid_text_maxlines = array.getInteger(R.styleable.SettingItemView_mid_text_maxlines, 2);
        default_str = array.getString(R.styleable.SettingItemView_default_str);
        hintColor = array.getColor(R.styleable.SettingItemView_hintColor, Color.parseColor("#000000"));
        leftTextColor = array.getColor(R.styleable.SettingItemView_left_text_color, Color.parseColor("#000000"));
        isShowArrow = array.getBoolean(R.styleable.SettingItemView_isShowArrow, false);
        value = array.getString(R.styleable.SettingItemView_value);
        enabled = array.getBoolean(R.styleable.SettingItemView_enable, true);
        left_text_with = array.getDimension(R.styleable.SettingItemView_left_text_width, getResources().getDimensionPixelSize(R.dimen.settingItemView_left_text_width));
        isInputInt = array.getBoolean(R.styleable.SettingItemView_isInputInt, false);
        mid_text_aliginright = array.getBoolean(R.styleable.SettingItemView_mid_text_aliginright, false);
        old_str = array.getString(R.styleable.SettingItemView_old_str);
        if (TextUtils.isEmpty(value)) {
            value = "-1";
        }
        array.recycle();//回收
        initView();
    }

    private void initView() {
        View view = null;
        //1:带右边箭头的  项目
        //2:中间是edittext
        switch (display_type) {
            case 1:
                view = View.inflate(getContext(), R.layout.sobot_setting_item_view1, null);
                leftTextView = (TextView) view.findViewById(R.id.tv_setting_item_left_text);
                midTextView = (TextView) view.findViewById(R.id.tv_setting_item_mid_text);
                rightImg = (ImageView) view.findViewById(R.id.tv_setting_item_img);
                leftTextView.setText(left_text);
                leftTextView.setTextColor(leftTextColor);
                leftTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                midTextView.setMaxEms(maxLength);
                midTextView.setText(mid_text);
                if (mid_text_aliginright) {
                    midTextView.setGravity(Gravity.RIGHT);
                }
                midTextView.setEllipsize(TextUtils.TruncateAt.END);
                midTextView.setMaxLines(mid_text_maxlines);
                setArrowDisplay(isShowArrow);
                break;
            case 2:
                view = View.inflate(getContext(), R.layout.sobot_setting_item_view2, null);
                leftTextView = (TextView) view.findViewById(R.id.tv_setting_item_left_text);
                et_text = (EditText) view.findViewById(R.id.et_setting_item_mid_text);
                rightImg = (ImageView) view.findViewById(R.id.tv_setting_item_img);
                leftTextView.setText(left_text);
                et_text.setHint(mid_hint);
                et_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                et_text.addTextChangedListener(tw);
                et_text.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (contentOnFocusChangeListener != null) {
                            contentOnFocusChangeListener.onFocusChange(v, hasFocus);
                        }
                        if (hasFocus) {
                            if (et_text.getText().toString().trim().length() != 0) {
                                rightImg.setVisibility(View.VISIBLE);
                            } else {
                                rightImg.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            rightImg.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                if (isInputInt) {
                    et_text.setKeyListener(new DigitsKeyListener(false, true));
                }
                rightImg.setOnClickListener(this);
                setEnabled(enabled);
                break;
            default:
                break;
        }
        LayoutParams layoutParams = (LayoutParams) leftTextView.getLayoutParams();
        layoutParams.width = (int) left_text_with;
        leftTextView.setLayoutParams(layoutParams);
        if (view != null) {
            LayoutParams param = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(view, param);
            setText(getTextByTrim());
        }
    }

    public String getTextByTrim() {
        String result = "";
        switch (display_type) {
            case 1:
                result = midTextView.getText().toString().trim();
                break;
            case 2:
                result = et_text.getText().toString().trim();
                break;
            default:

                break;
        }
        return result;
    }

    public void setText(String str) {
        switch (display_type) {
            case 1:
                midTextView.setText(str);
                if (!TextUtils.isEmpty(str) && str.equals(default_str)) {
                    midTextView.setTextColor(hintColor);
                } else {
                    midTextView.setTextColor(Color.parseColor("#787878"));
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(str) && str.equals(default_str)) {
                    et_text.setTextColor(hintColor);
                } else {
                    et_text.setTextColor(Color.parseColor("#787878"));
                }
                et_text.setText(str);
                break;
            default:

                break;
        }
    }

    public void setContentOnLongClickLinstner(OnLongClickListener listener) {
        switch (display_type) {
            case 1:
                midTextView.setOnLongClickListener(listener);
                break;
            case 2:
                et_text.setOnLongClickListener(listener);
                break;
            default:

                break;
        }
    }

    public void setContentOnFocusChangeListener(ContentOnFocusChangeListener listener) {
        this.contentOnFocusChangeListener = listener;
    }

    private TextWatcher tw = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (et_text.isFocused()) {
                if (et_text.getText().toString().trim().length() != 0) {
                    rightImg.setVisibility(View.VISIBLE);
                } else {
                    rightImg.setVisibility(View.INVISIBLE);
                }
            } else {
                rightImg.setVisibility(View.INVISIBLE);
            }

            if (textChangedListener != null) {
                textChangedListener.afterTextChanged(arg0);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_item_img:
                et_text.setText(old_str);
                break;
            default:
                break;
        }
    }

    public interface TextChangedListener {
        void afterTextChanged(Editable arg0);
    }

    public void setTextChangedListener(TextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

    public boolean isEmpty() {
        boolean result = false;
        switch (display_type) {
            case 1:
                if (!TextUtils.isEmpty(getTextByTrim())) {
                    if (getTextByTrim().equals(default_str)) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else {
                    result = true;
                }
                break;
            case 2:
                result = TextUtils.isEmpty(getTextByTrim());
                break;
            default:

                break;
        }
        return result;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public void reset() {
        this.value = "-1";
        setText(default_str);
    }

    public void setEnabled(boolean enabled) {
        switch (display_type) {
            case 1:

                break;
            case 2:
                this.enabled = enabled;
                if (!enabled) {
                    et_text.removeTextChangedListener(tw);
                }
                et_text.setEnabled(enabled);
                rightImg.setVisibility(enabled && !TextUtils.isEmpty(et_text.getText().toString()) ? View.VISIBLE : View.GONE);
                invalidate();
                break;
            default:

                break;
        }
    }

    public void setDefaultStr(String default_str) {
        this.default_str = default_str;
    }

    public String getDefaultStr() {
        return this.default_str;
    }

    public void setOldStr(String str) {
        this.old_str = str;
    }

    public void setOnIconClickListner(final OnIconClickListner listner) {
        rightImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClick(v);
            }
        });
    }

    public interface OnIconClickListner {
        void onClick(View v);
    }

    public interface ContentOnFocusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }

    public void requestLeftTextViewFoucus() {
        switch (display_type) {
            case 1:
                leftTextView.setFocusable(true);
                leftTextView.setFocusableInTouchMode(true);
                leftTextView.requestFocus();
                break;
            case 2:
                break;
            default:

                break;
        }
    }

    public void setArrowDisplay(boolean isShow) {
        isShowArrow = isShow;
        rightImg.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }
}