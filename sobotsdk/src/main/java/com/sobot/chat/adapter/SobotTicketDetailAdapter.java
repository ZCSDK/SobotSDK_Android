package com.sobot.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.activity.SobotFileDetailActivity;
import com.sobot.chat.activity.SobotPhotoActivity;
import com.sobot.chat.activity.SobotVideoActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.SobotFileModel;
import com.sobot.chat.api.model.SobotUserTicketEvaluate;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.api.model.StUserDealTicketInfo;
import com.sobot.chat.api.model.StUserDealTicketReply;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.StExpandableTextView;
import com.sobot.chat.widget.attachment.AttachmentView;
import com.sobot.chat.widget.attachment.FileAttachmentAdapter;
import com.sobot.chat.widget.attachment.FileTypeConfig;
import com.sobot.chat.widget.attachment.SpaceItemDecoration;

import java.util.List;

/**
 * 留言记录适配器
 *
 * @author Created by jinxl on 2019/3/7.
 */
public class SobotTicketDetailAdapter extends SobotBaseAdapter<Object> {

    private Context mContext;
    private int attachmentCount;


    private static final String[] layoutRes = {
            "sobot_ticket_detail_head_item",//详情头布局
            "sobot_ticket_detail_created_item",//已创建布局
            "sobot_ticket_detail_processing_item",//受理中布局
            "sobot_ticket_detail_completed_item",//已完成布局
            "sobot_ticket_detail_foot_item",//评价尾布局
    };

    //详情头
    public static final int MSG_TYPE_HEAD = 0;
    //已创建
    public static final int MSG_TYPE_TYPE1 = 1;
    //受理中
    public static final int MSG_TYPE_TYPE2 = 2;
    //已完成
    public static final int MSG_TYPE_TYPE3 = 3;
    //评价尾
    public static final int MSG_TYPE_TYPE4 = 4;

    AttachmentView.Listener listener = new AttachmentView.Listener() {
        @Override
        public void downFileLister(SobotFileModel fileModel, int position) {
            // 打开详情页面
            Intent intent = new Intent(mContext, SobotFileDetailActivity.class);
            SobotCacheFile cacheFile = new SobotCacheFile();
            cacheFile.setFileName(fileModel.getFileName());
            cacheFile.setUrl(fileModel.getFileUrl());
            cacheFile.setFileType(FileTypeConfig.getFileType(fileModel.getFileType()));
            cacheFile.setMsgId(fileModel.getFileId());
            intent.putExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE, cacheFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }

        @Override
        public void previewMp4(SobotFileModel fileModel, int position) {
            SobotCacheFile cacheFile = new SobotCacheFile();
            cacheFile.setFileName(fileModel.getFileName());
            cacheFile.setUrl(fileModel.getFileUrl());
            cacheFile.setFileType(FileTypeConfig.getFileType(fileModel.getFileType()));
            cacheFile.setMsgId(fileModel.getFileId());
            Intent intent = SobotVideoActivity.newIntent(mContext, cacheFile);
            mContext.startActivity(intent);

        }

        @Override
        public void previewPic(String fileUrl, String fileName, int position) {
            Intent intent = new Intent(context, SobotPhotoActivity.class);
            intent.putExtra("imageUrL", fileUrl);
            context.startActivity(intent);
        }


    };

    public SobotTicketDetailAdapter(Context context, List list) {
        this(context, list, 2);
    }

    public SobotTicketDetailAdapter(Context context, List list, int attachmentCount) {
        super(context, list);
        this.mContext = context;
        this.attachmentCount = attachmentCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object data = list.get(position);
        if (data != null) {
            int itemType = getItemViewType(position);
            convertView = initView(convertView, itemType, position, data);
            BaseViewHolder holder = (BaseViewHolder) convertView.getTag();
            holder.bindData(data, position);
        }
        return convertView;
    }

    private View initView(View convertView, int itemType, int position, final Object data) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
            BaseViewHolder holder;
            switch (itemType) {
                case MSG_TYPE_HEAD: {
                    holder = new HeadViewHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_TYPE1: {
                    holder = new Type1ViewHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_TYPE2: {
                    holder = new Type2ViewHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_TYPE3: {
                    holder = new Type3ViewHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_TYPE4: {
                    holder = new Type4ViewHolder(context, convertView);
                    break;
                }
                default:
                    holder = new HeadViewHolder(context, convertView);
                    break;
            }
            convertView.setTag(holder);
        }
        return convertView;
    }

    /**
     * @return 返回有多少种UI布局样式
     */
    @Override
    public int getViewTypeCount() {
        if (layoutRes.length > 0) {
            return layoutRes.length;
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        Object data = list.get(position);
        if (data instanceof SobotUserTicketInfo) {
            return MSG_TYPE_HEAD;
        } else if (data instanceof StUserDealTicketInfo) {
            StUserDealTicketInfo item = (StUserDealTicketInfo) data;
            if (item.getFlag() == 1) {
                return MSG_TYPE_TYPE1;
            } else if (item.getFlag() == 2) {
                return MSG_TYPE_TYPE2;
            } else if (item.getFlag() == 3) {
                return MSG_TYPE_TYPE3;
            }
        } else if (data instanceof SobotUserTicketEvaluate) {
            return MSG_TYPE_TYPE4;
        }
        return MSG_TYPE_HEAD;
    }

    static abstract class BaseViewHolder {
        Context mContext;

        BaseViewHolder(Context context, View view) {
            mContext = context;
        }

        abstract void bindData(Object data, int position);
    }

    class HeadViewHolder extends BaseViewHolder {
        private TextView tv_title;
        private StExpandableTextView tv_exp;
        private ImageView imageView;
        private TextView textView;

        private TextView tv_time;
        private TextView tv_ticket_status;
        private RecyclerView recyclerView;


        private Context mContext;
        private int bg1_resId;
        private int bg2_resId;
        private int bg3_resId;
        private int str1_resId;
        private int str2_resId;
        private int str3_resId;

        HeadViewHolder(Context context, View view) {
            super(context, view);
            mContext = context;
            tv_title = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_title"));
            tv_exp = (StExpandableTextView) view.findViewById(ResourceUtils.getResId(context, "sobot_content_fl"));
            imageView = tv_exp.getImageView();
            textView = tv_exp.getTextBtn();
            tv_exp.setOnExpandStateChangeListener(new StExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView text, boolean isExpanded) {
                    if (isExpanded) {//展开 sobot_icon_arrow_selector
                        textView.setText(ResourceUtils.getResString(mContext, "sobot_notice_collapse"));
//                        imageView.setImageResource(ResourceUtils.getDrawableId(mContext,"sobot_icon_arrow_up"));
                    } else {
                        textView.setText(ResourceUtils.getResString(mContext, "sobot_ticket_expand"));
//                        imageView.setImageResource(ResourceUtils.getDrawableId(mContext,"sobot_icon_arrow_dwon"));
                    }

                }
            });
            textView.setText(ResourceUtils.getResString(mContext, "sobot_ticket_expand"));
            imageView.setImageResource(ResourceUtils.getDrawableId(mContext,"sobot_icon_arrow_down"));
            tv_time = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_time"));
            ViewGroup otherGroup = tv_exp.getmOtherView();
            if (otherGroup != null) {
                recyclerView = (RecyclerView) otherGroup.findViewById(ResourceUtils.getResId(context, "sobot_attachment_file_layout"));
                GridLayoutManager gridlayoutmanager = new GridLayoutManager(context, 3);
                recyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(mContext, 10),ScreenUtils.dip2px(mContext, 10),0,SpaceItemDecoration.GRIDLAYOUT));
                recyclerView.setLayoutManager(gridlayoutmanager);
            }


            tv_ticket_status = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_ticket_status"));
            bg1_resId = ResourceUtils.getDrawableId(context, "sobot_ticket_status_bg3");
            bg2_resId = ResourceUtils.getDrawableId(context, "sobot_ticket_status_bg2");
            bg3_resId = ResourceUtils.getDrawableId(context, "sobot_ticket_status_bg1");
            str1_resId = ResourceUtils.getResStrId(context, "sobot_created_1");
            str2_resId = ResourceUtils.getResStrId(context, "sobot_processing");
            str3_resId = ResourceUtils.getResStrId(context, "sobot_completed");
        }

        void bindData(Object item, final int position) {
            final SobotUserTicketInfo data = (SobotUserTicketInfo) item;
            String tempStr=data.getContent().replaceAll("<br/>","").replace("<p></p>","")
                    .replaceAll("<p>", "").replaceAll("</p>", "<br/>").replaceAll("\n", "<br/>");
            tv_exp.setText(TextUtils.isEmpty(data.getContent()) ? "" : Html.fromHtml(tempStr));
            int color = ResourceUtils.getResColorValue(context, "sobot_color_title_bar_title");

            if (2 == data.getFlag()) {
                tv_ticket_status.setText(str2_resId);
                tv_ticket_status.setBackgroundResource(bg2_resId);
            } else if (3 == data.getFlag()) {
                tv_ticket_status.setText(str3_resId);
                tv_ticket_status.setBackgroundResource(bg3_resId);
            } else {
                tv_ticket_status.setText(str1_resId);
                tv_ticket_status.setBackgroundResource(bg1_resId);
            }
            tv_time.setText(data.getTimeStr());
            tv_exp.setHaveFile(data.getFileList()!=null&&data.getFileList().size()>0);
            recyclerView.setAdapter(new FileAttachmentAdapter(context, data.getFileList(), color, listener));
        }
    }

    class Type1ViewHolder extends BaseViewHolder {
        private TextView sobot_tv_time;
        private LinearLayout sobot_ll_root;
        private TextView sobot_tv_status;
        private TextView sobot_tv_secod;
        private View sobot_line_view;
        private View sobot_line_split;
        private TextView sobot_tv_icon2;

        Type1ViewHolder(Context context, View view) {
            super(context, view);
            sobot_ll_root = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_ll_root"));
            sobot_tv_icon2 = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_icon2"));
            sobot_tv_status = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_status"));
            sobot_tv_time = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_time"));
            sobot_tv_secod = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_secod"));
            sobot_line_view = view.findViewById(ResourceUtils.getResId(context, "sobot_line_view"));
            sobot_line_split = view.findViewById(ResourceUtils.getResId(context, "sobot_line_split"));
        }

        void bindData(Object item, int position) {
            LinearLayout.LayoutParams lp;
            if (position == 1) {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(context, 19), ScreenUtils.dip2px(context, 19));
                sobot_tv_time.setSelected(true);
                sobot_tv_status.setSelected(true);
                sobot_tv_secod.setSelected(true);
                sobot_tv_icon2.setSelected(true);
                sobot_line_split.setVisibility(View.VISIBLE);
                sobot_line_view.setBackgroundColor(Color.parseColor("#00000000"));
                sobot_ll_root.setPadding(ScreenUtils.dip2px(context, 20), ScreenUtils.dip2px(context, 30), ScreenUtils.dip2px(context, 20), ScreenUtils.dip2px(context, 30));
            } else {
                sobot_tv_time.setSelected(false);
                sobot_tv_status.setSelected(false);
                sobot_tv_secod.setSelected(false);
                sobot_tv_icon2.setSelected(false);
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(context, 14), ScreenUtils.dip2px(context, 14));
                sobot_line_split.setVisibility(View.GONE);
                sobot_line_view.setBackgroundColor(Color.parseColor("#EDEEF0"));
                sobot_ll_root.setPadding(ScreenUtils.dip2px(context, 20), 0, ScreenUtils.dip2px(context, 20), ScreenUtils.dip2px(context, 30));
            }
            sobot_tv_icon2.setLayoutParams(lp);
            StUserDealTicketInfo data = (StUserDealTicketInfo) item;
            sobot_tv_time.setText(DateUtil.stringToFormatString(data.getTimeStr(), "MM-dd"));
            sobot_tv_secod.setText(DateUtil.stringToFormatString(data.getTimeStr(), "HH:mm"));
        }
    }

    class Type2ViewHolder extends BaseViewHolder {
        private TextView sobot_tv_time;
        private TextView sobot_tv_secod;
        private TextView sobot_tv_icon2;
        private TextView sobot_tv_status;
        private TextView sobot_tv_content;
        private TextView sobot_tv_content_detail;
        private View sobot_tv_content_detail_split;
        private LinearLayout sobot_ll_container;
        private LinearLayout sobot_tv_content_ll;
        private View sobot_line_split;
        private View sobot_top_line_view;
        private LinearLayout sobot_ll_root;
        private RecyclerView recyclerView;

        Type2ViewHolder(Context context, View view) {
            super(context, view);
            sobot_ll_root = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_ll_root"));
            sobot_tv_icon2 = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_icon2"));
            sobot_tv_status = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_status"));
            sobot_tv_time = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_time"));
            sobot_tv_secod = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_secod"));
            sobot_tv_content_ll = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content_ll"));
            sobot_tv_content = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content"));
            sobot_tv_content_detail_split = view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content_detail_split"));
            sobot_tv_content_detail = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content_detail"));
            sobot_ll_container = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_ll_container"));
            sobot_top_line_view = view.findViewById(ResourceUtils.getResId(context, "sobot_top_line_view"));
            sobot_line_split = view.findViewById(ResourceUtils.getResId(context, "sobot_line_split"));
            recyclerView = (RecyclerView) view.findViewById(ResourceUtils.getResId(context, "sobot_attachment_file_layout"));
            GridLayoutManager gridlayoutmanager = new GridLayoutManager(context, 2);
            recyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(mContext, 10),ScreenUtils.dip2px(mContext, 10),0,SpaceItemDecoration.GRIDLAYOUT));
            recyclerView.setLayoutManager(gridlayoutmanager);
        }

        void bindData(Object item, final int position) {
            LinearLayout.LayoutParams lp;
            int color;
            if (position == 1) {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(context, 19), ScreenUtils.dip2px(context, 19));
                sobot_tv_time.setSelected(true);
                sobot_tv_secod.setSelected(true);
                sobot_tv_icon2.setSelected(true);
                sobot_tv_status.setSelected(true);
                sobot_ll_container.setSelected(true);
                color = ResourceUtils.getResColorValue(context, "sobot_color_title_bar_title");
                sobot_top_line_view.setBackgroundColor(Color.parseColor("#00000000"));
                sobot_line_split.setVisibility(View.VISIBLE);
                sobot_tv_icon2.setBackgroundResource(ResourceUtils.getDrawableId(context, "sobot_icon_processing_point_selector_2"));
                sobot_ll_root.setPadding(ScreenUtils.dip2px(context, 20), ScreenUtils.dip2px(context, 30), ScreenUtils.dip2px(context, 20), 0);
            } else {
                sobot_tv_time.setSelected(false);
                sobot_tv_secod.setSelected(false);
                sobot_tv_icon2.setSelected(false);
                sobot_tv_status.setSelected(false);
                sobot_ll_container.setSelected(false);
                color = ResourceUtils.getResColorValue(context, "sobot_common_gray");
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(context, 14), ScreenUtils.dip2px(context, 14));
                sobot_tv_icon2.setBackgroundResource(ResourceUtils.getDrawableId(context, "sobot_icon_processing_point_selector"));
                sobot_top_line_view.setBackgroundColor(Color.parseColor("#EDEEF0"));
                sobot_line_split.setVisibility(View.GONE);
                sobot_ll_root.setPadding(ScreenUtils.dip2px(context, 20), 0, ScreenUtils.dip2px(context, 20), 0);
            }
            sobot_tv_icon2.setLayoutParams(lp);
            final StUserDealTicketInfo data = (StUserDealTicketInfo) item;
            final StUserDealTicketReply reply = data.getReply();
            if (reply != null) {
                if (reply.getStartType() == 0) {
                    //客服
                    sobot_tv_status.setVisibility(View.VISIBLE);
                    sobot_tv_status.setText(ResourceUtils.getResString(context, "sobot_processing"));
                    if (TextUtils.isEmpty(reply.getReplyContent())) {
                        sobot_tv_content.setText(ResourceUtils.getResString(mContext, "sobot_please_wait"));
                        sobot_tv_content_ll.setBackgroundDrawable(null);
                        sobot_tv_content_detail.setVisibility(View.GONE);
                        sobot_tv_content_detail.setOnClickListener(null);
                        sobot_tv_content_detail_split.setVisibility(View.GONE);
                        sobot_tv_content.setPadding(0, 0, 0, 0);
                    } else {
                        //如果回复里包含图片（img标签），如果有，显示查看详情，并且跳转到WebViewActivity展示
                        if (StringUtils.getImgSrc(reply.getReplyContent()).size() > 0) {
                            sobot_tv_content_ll.setBackgroundDrawable(context.getResources().getDrawable(ResourceUtils.getDrawableId(context, "sobot_round_ticket")));
                            sobot_tv_content_detail.setVisibility(View.VISIBLE);
                            sobot_tv_content_detail_split.setVisibility(View.VISIBLE);
                            sobot_tv_content.setPadding(ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 10), ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 10));
                            sobot_tv_content_detail.setPadding(ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 11), ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 11));
                            sobot_tv_content_detail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("url", reply.getReplyContent());
                                    context.startActivity(intent);
                                }
                            });
                        } else {
                            sobot_tv_content_ll.setBackgroundDrawable(null);
                            sobot_tv_content_detail.setVisibility(View.GONE);
                            sobot_tv_content_detail.setOnClickListener(null);
                            sobot_tv_content_detail_split.setVisibility(View.GONE);
                            sobot_tv_content.setPadding(0, 0, 0, 0);
                        }
                        HtmlTools.getInstance(context).setRichText(sobot_tv_content, reply.getReplyContent().replaceAll("<br/>", "").replaceAll("\n", "<br/>").replaceAll("<img.*?/>", " " + ResourceUtils.getResString(context, "sobot_chat_type_pic") + " "), getLinkTextColor());
                    }


                } else {
                    //客户
                    sobot_tv_content_ll.setBackgroundDrawable(null);
                    sobot_tv_content_detail.setVisibility(View.GONE);
                    sobot_tv_content_detail.setOnClickListener(null);
                    sobot_tv_content_detail_split.setVisibility(View.GONE);
                    sobot_tv_content.setPadding(0, 0, 0, 0);
                    sobot_tv_status.setVisibility(View.VISIBLE);
                    sobot_tv_status.setText(ResourceUtils.getResString(context, "sobot_my_reply"));
                    sobot_tv_content.setText(TextUtils.isEmpty(reply.getReplyContent()) ? ResourceUtils.getResString(context, "sobot_nothing") : Html.fromHtml(reply.getReplyContent().replaceAll("<img.*?/>", " " + ResourceUtils.getResString(context, "sobot_chat_type_pic") + " ")));
                }
                sobot_tv_time.setText(DateUtil.toDate(reply.getReplyTime() * 1000, DateUtil.DATE_FORMAT6));
                sobot_tv_secod.setText(DateUtil.toDate(reply.getReplyTime() * 1000, DateUtil.DATE_FORMAT3));

                recyclerView.setAdapter(new FileAttachmentAdapter(context, data.getFileList(), color, listener));


            } else {
                sobot_tv_status.setVisibility(View.GONE);
                sobot_tv_content.setText(TextUtils.isEmpty(data.getContent()) ? "" : Html.fromHtml(data.getContent().replaceAll("<p>", "").replaceAll("</p>", "")));
                sobot_tv_time.setText(DateUtil.stringToFormatString(data.getTimeStr(), "MM-dd"));
                sobot_tv_secod.setText(DateUtil.stringToFormatString(data.getTimeStr(), "HH:mm"));
            }

//            if (position==1){
//                sobot_tv_content.setTextColor(ResourceUtils.getResColorValue(context,"sobot_ticket_deal_text_black"));
//            }else{
//                sobot_tv_content.setTextColor(ResourceUtils.getResColorValue(context,"sobot_common_gray"));
//            }
        }
    }

    class Type3ViewHolder extends BaseViewHolder {
        private TextView sobot_tv_time;
        private TextView sobot_tv_secod;
        private TextView sobot_tv_icon2;
        private TextView sobot_tv_status;
        private TextView sobot_tv_content;
        private View sobot_top_line_view;
        private LinearLayout sobot_ll_root;
        private RecyclerView recyclerView;
        private TextView sobot_tv_content_detail;
        private View sobot_line_split;
        private View sobot_tv_content_detail_split;
        private LinearLayout sobot_tv_content_ll;

        Type3ViewHolder(Context context, View view) {
            super(context, view);
            sobot_ll_root = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_ll_root"));
            sobot_tv_icon2 = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_icon2"));
            sobot_tv_status = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_status"));
            sobot_tv_time = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_time"));
            sobot_tv_secod = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_secod"));
            sobot_tv_content = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content"));
            sobot_tv_content_ll = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content_ll"));
            sobot_tv_content_detail_split = view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content_detail_split"));
            sobot_tv_content_detail = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_content_detail"));

            sobot_line_split=view.findViewById(ResourceUtils.getResId(context, "sobot_top_line_view_slip"));
            sobot_top_line_view = view.findViewById(ResourceUtils.getResId(context, "sobot_top_line_view"));
            recyclerView = (RecyclerView) view.findViewById(ResourceUtils.getResId(context, "sobot_attachment_file_layout"));
            GridLayoutManager gridlayoutmanager = new GridLayoutManager(context, 2);
            recyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(mContext, 10),ScreenUtils.dip2px(mContext, 10),0,SpaceItemDecoration.GRIDLAYOUT));
            recyclerView.setLayoutManager(gridlayoutmanager);
        }

        void bindData(Object item, int position) {
            LinearLayout.LayoutParams lp;
            int color;
            if (position == 1) {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(context, 19), ScreenUtils.dip2px(context, 19));
                sobot_tv_time.setSelected(true);
                sobot_tv_secod.setSelected(true);
                sobot_tv_icon2.setSelected(true);
                sobot_tv_status.setSelected(true);
                sobot_tv_content.setSelected(true);
                sobot_line_split.setVisibility(View.VISIBLE);
                color = ResourceUtils.getResColorValue(context, "sobot_color_title_bar_title");
                sobot_top_line_view.setBackgroundColor(Color.parseColor("#00000000"));
                sobot_ll_root.setPadding(ScreenUtils.dip2px(context, 20), ScreenUtils.dip2px(context, 30), ScreenUtils.dip2px(context, 20), 0);
            } else {
                sobot_tv_time.setSelected(false);
                sobot_tv_secod.setSelected(false);
                sobot_tv_icon2.setSelected(false);
                sobot_tv_status.setSelected(false);
                sobot_tv_content.setSelected(false);
                sobot_line_split.setVisibility(View.GONE);
                color = ResourceUtils.getResColorValue(context, "sobot_common_gray");
                sobot_top_line_view.setBackgroundColor(Color.parseColor("#EDEEF0"));
                sobot_ll_root.setPadding(ScreenUtils.dip2px(context, 20), 0, ScreenUtils.dip2px(context, 20), 0);
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(context, 14), ScreenUtils.dip2px(context, 14));
            }
            sobot_tv_icon2.setLayoutParams(lp);
            final StUserDealTicketInfo data = (StUserDealTicketInfo) item;
            sobot_tv_time.setText(DateUtil.stringToFormatString(data.getTimeStr(), "MM-dd"));
            sobot_tv_secod.setText(DateUtil.stringToFormatString(data.getTimeStr(), "HH:mm"));

                if (TextUtils.isEmpty(data.getContent())) {
                    sobot_tv_content.setText(ResourceUtils.getResString(mContext, "sobot_please_wait"));
                    sobot_tv_content_ll.setBackgroundDrawable(null);
                    sobot_tv_content_detail.setVisibility(View.GONE);
                    sobot_tv_content_detail.setOnClickListener(null);
                    sobot_tv_content_detail_split.setVisibility(View.GONE);
                    sobot_tv_content.setPadding(0, 0, 0, 0);
                } else {
                    //如果回复里包含图片（img标签），如果有，显示查看详情，并且跳转到WebViewActivity展示
                    if (StringUtils.getImgSrc(data.getContent()).size() > 0) {
                        sobot_tv_content_ll.setBackgroundDrawable(context.getResources().getDrawable(ResourceUtils.getDrawableId(context, "sobot_round_ticket")));
                        sobot_tv_content_detail.setVisibility(View.VISIBLE);
                        sobot_tv_content_detail_split.setVisibility(View.VISIBLE);
                        sobot_tv_content.setPadding(ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 11), ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 11));
                        sobot_tv_content_detail.setPadding(ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 11), ScreenUtils.dip2px(context, 15), ScreenUtils.dip2px(context, 11));
                        sobot_tv_content_detail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("url", data.getContent());
                                context.startActivity(intent);
                            }
                        });
                    } else {
                        sobot_tv_content_ll.setBackgroundDrawable(null);
                        sobot_tv_content_detail.setVisibility(View.GONE);
                        sobot_tv_content_detail.setOnClickListener(null);
                        sobot_tv_content_detail_split.setVisibility(View.GONE);
                        sobot_tv_content.setPadding(0, 0, 0, 0);
                    }
                    HtmlTools.getInstance(context).setRichText(sobot_tv_content, data.getContent().replaceAll("<br/>", "").replaceAll("\n", "<br/>").replaceAll("<img.*?/>", " " + ResourceUtils.getResString(context, "sobot_chat_type_pic") + " "), getLinkTextColor());
                }


//            if (position==1){
//                sobot_tv_content.setTextColor(ResourceUtils.getResColorValue(context,"sobot_ticket_deal_text_black"));
//            }else{
//                sobot_tv_content.setTextColor(ResourceUtils.getResColorValue(context,"sobot_common_gray"));
//            }


            recyclerView.setAdapter(new FileAttachmentAdapter(context, data.getFileList(), color, listener));

            if (data.getStartType()==0) {
                sobot_tv_status.setText(ResourceUtils.getResString(context,"sobot_completed"));
            }else{
                sobot_tv_status.setText(ResourceUtils.getResString(context, "sobot_my_reply"));
            }

        }

    }

    class Type4ViewHolder extends BaseViewHolder {
        private LinearLayout sobot_ll_score;
        private TextView sobot_tv_remark;
        private LinearLayout sobot_ll_remark;
        private RatingBar sobot_ratingBar;
        private TextView sobot_my_evaluate_tv;
        private LinearLayout sobot_my_evaluate_ll;

        Type4ViewHolder(Context context, View view) {
            super(context, view);
            sobot_ll_score = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_ll_score"));
            sobot_tv_remark = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_tv_remark"));
            sobot_ll_remark = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_ll_remark"));
            sobot_ratingBar = (RatingBar) view.findViewById(ResourceUtils.getResId(context, "sobot_ratingBar"));
            sobot_my_evaluate_tv = (TextView) view.findViewById(ResourceUtils.getResId(context, "sobot_my_evaluate_tv"));
            sobot_my_evaluate_ll = (LinearLayout) view.findViewById(ResourceUtils.getResId(context, "sobot_my_evaluate_ll"));
        }

        void bindData(Object item, int position) {
            final SobotUserTicketEvaluate mEvaluate = (SobotUserTicketEvaluate) item;
            if (mEvaluate.isOpen()) {
                if (mEvaluate.isEvalution()) {
                    //已评价
                    sobot_my_evaluate_tv.setVisibility(View.VISIBLE);
                    sobot_my_evaluate_ll.setVisibility(View.VISIBLE);
                    List<SobotUserTicketEvaluate.TicketScoreInfooListBean> infooList = mEvaluate.getTicketScoreInfooList();
                    if (infooList != null && infooList.size() >= mEvaluate.getScore()) {
                        sobot_ll_score.setVisibility(View.VISIBLE);
                        sobot_ratingBar.setRating(mEvaluate.getScore());
                    } else {
                        sobot_ll_score.setVisibility(View.GONE);
                    }

                    if (TextUtils.isEmpty(mEvaluate.getRemark())) {
                        sobot_ll_remark.setVisibility(View.GONE);
                    } else {
                        sobot_ll_remark.setVisibility(View.VISIBLE);
                        sobot_tv_remark.setText(mEvaluate.getRemark());
                    }
                } else {
                    sobot_my_evaluate_tv.setVisibility(View.GONE);
                    sobot_my_evaluate_ll.setVisibility(View.GONE);
                    sobot_ll_score.setVisibility(View.GONE);
                    sobot_ll_remark.setVisibility(View.GONE);
                }
            } else {
                sobot_my_evaluate_tv.setVisibility(View.GONE);
                sobot_my_evaluate_ll.setVisibility(View.GONE);
                sobot_ll_score.setVisibility(View.GONE);
                sobot_ll_remark.setVisibility(View.GONE);
            }

        }

    }

    //左右两边气泡内链接文字的字体颜色
    protected int getLinkTextColor() {

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_left_link_textColor) {
            return SobotUIConfig.sobot_chat_left_link_textColor;
        } else {
            return ResourceUtils.getIdByName(mContext, "color", "sobot_color_link");
        }
    }


}