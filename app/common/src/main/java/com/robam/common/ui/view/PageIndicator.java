package com.robam.common.ui.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.robam.common.R;

import java.util.ArrayList;
import java.util.List;

public class PageIndicator implements ViewPager.OnPageChangeListener {
    private int mPageCount;//页数
    private List<ImageView> mImgList;//保存img总个数
    private int img_select;
    private int img_unSelect;
    private int imgSize = 12;
    private int margin = 8;

    public PageIndicator(Context context, LinearLayout linearLayout, int pageCount) {
        this.mPageCount = pageCount;

        imgSize = (int) context.getResources().getDimension(R.dimen.dp_12);
        margin = (int) context.getResources().getDimension(R.dimen.dp_8);
        linearLayout.removeAllViews();
        mImgList = new ArrayList<>();
        img_select = R.drawable.common_dot_select;
        img_unSelect = R.drawable.common_dot_unselect;

        for (int i = 0; i < mPageCount; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //为小圆点左右添加间距
            params.leftMargin = margin;
            params.rightMargin = margin;
            //给小圆点一个默认大小
            params.height = imgSize;
            params.width = imgSize;
            if (i == 0) {
                imageView.setBackgroundResource(img_select);
            } else {
                imageView.setBackgroundResource(img_unSelect);
            }
            //为LinearLayout添加ImageView
            linearLayout.addView(imageView, params);
            mImgList.add(imageView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mPageCount; i++) {
            //选中的页面改变小圆点为选中状态，反之为未选中
            if ((position % mPageCount) == i) {
                (mImgList.get(i)).setBackgroundResource(img_select);
            } else {
                (mImgList.get(i)).setBackgroundResource(img_unSelect);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
