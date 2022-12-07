package com.robam.common.ui.helper;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space; //item间间隔
    private int startSpace; //最开始间隔

    public HorizontalSpaceItemDecoration(int space) {
        this.space = space;
    }

    public HorizontalSpaceItemDecoration(int space, int startSpace) {
        this.space = space;
        this.startSpace = startSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (startSpace > 0) {
            int position = parent.getChildAdapterPosition(view);
            int totalCount = parent.getAdapter().getItemCount();
            if (position == 0) {//第一个
                outRect.left = startSpace;
                outRect.right = space;
            } else if (position == totalCount - 1) {//最后一个
                outRect.left = space;
                outRect.right = startSpace;
            } else {//中间其它的
                outRect.left = space;
                outRect.right = space;
            }
        } else {
            outRect.left = space;
            outRect.right = space;
        }
    }

}
