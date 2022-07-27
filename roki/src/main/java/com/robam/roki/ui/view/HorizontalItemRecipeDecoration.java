package com.robam.roki.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class HorizontalItemRecipeDecoration extends RecyclerView.ItemDecoration{
    private int space;//定义2个Item之间的距离

    public HorizontalItemRecipeDecoration(int space, Context mContext) {
        this.space = dip2px(space,mContext);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();
        if (position == 0) {//第一个
            outRect.left = 0 ;
            outRect.right = 0;
        }  else {//中间其它的
            StaggeredGridLayoutManager.LayoutParams params = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams());
//            if (params.getSpanIndex() != GridLayoutManager.LayoutParams.INVALID_SPAN_ID){
            if (params.getSpanIndex() % 2 == 0){
                outRect.left = space;
                outRect.right = 0 ;
            }else {
                outRect.left = 0;
                outRect.right = space;
            }

        }
    }

    public int dip2px(float dpValue,Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
