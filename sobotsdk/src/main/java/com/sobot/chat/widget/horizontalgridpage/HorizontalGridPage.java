package com.sobot.chat.widget.horizontalgridpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sobot.chat.R;
import com.sobot.chat.api.model.ZhiChiMessageBase;

/**
 * 横向滑动页面
 *
 * @author wuhao
 */
public class HorizontalGridPage extends LinearLayout {

    PageGridView gridView;
    PageIndicatorView indicatorView;
    Context mContext;
    int currentIndex;

    public HorizontalGridPage(Context context) {
        this(context, null);
    }

    public HorizontalGridPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalGridPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    /**
     * 初始化
     *
     * @param builder 参数构建器
     */
    public void init(PageBuilder builder,int currentItem) {
        currentIndex =currentItem;
        setOrientation(LinearLayout.VERTICAL);//纵向排列
        if (builder == null) {
            builder = new PageBuilder.Builder().build();
        }

        int[] grid = builder.getGrid();
        int swipePercent = builder.getSwipePercent();
        gridView = new PageGridView(getContext(), grid, swipePercent, builder.getItemHeight());

        int indicatorSize = dip2px(6);
        int[] margins = {dip2px(builder.getIndicatorMargins()[0]), dip2px(builder.getIndicatorMargins()[1]),
                dip2px(builder.getIndicatorMargins()[2]), dip2px(builder.getIndicatorMargins()[3])};
        int[] indicatorRes = new int[]{R.drawable.sobot_indicator_oval_normal_bg, R.drawable.sobot_indicator_oval_focus_bg};
        int gravity = builder.getIndicatorGravity();
        indicatorView = new PageIndicatorView(getContext(), indicatorSize, margins, indicatorRes, gravity);
        indicatorView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        indicatorView.initIndicator(grid[1]);
        gridView.setIndicator(indicatorView);
        gridView.addItemDecoration(new SpaceItemDecoration(0, dip2px(builder.getSpace())));//设置间距
        PagerGridLayoutManager layoutManager = new PagerGridLayoutManager(grid[0], grid[1], PagerGridLayoutManager
                .HORIZONTAL);
        layoutManager.setAllowContinuousScroll(false);
        gridView.setLayoutManager(layoutManager);


        addView(gridView);
        if (builder.isShowIndicator()) {
            addView(indicatorView);
        } else {
            removeView(indicatorView);
        }
    }


    /**
     * 设置Adapter
     *
     * @param adapter 设置的Adapter
     */
    public void setAdapter(PageGridAdapter adapter, ZhiChiMessageBase message) {
        PagerGridSnapHelper snapHelper = new PagerGridSnapHelper();
        snapHelper.attachToRecyclerView(gridView);
        gridView.setAdapter(adapter);
        indicatorView.setMessage(message);

    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue 要转换的dp值
     */
    private int dip2px(int dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setSelectItem(int index) {
        if (gridView != null)
            gridView.setSelectItem(index);

    }

    public void selectCurrentItem() {
        if (gridView != null)
            gridView.setSelectItem(currentIndex);
    }
}
