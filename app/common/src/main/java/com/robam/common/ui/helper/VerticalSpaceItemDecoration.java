package com.robam.common.ui.helper;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space; //item间间隔

    public VerticalSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();
        if (position == 0) {//第一个
            outRect.top = 0;
            outRect.bottom = space;
        } else if (position == totalCount - 1) {//最后一个
            outRect.top = space;
            outRect.bottom = 0;
        } else {//中间其它的
            outRect.top = space;
            outRect.bottom = space;
        }
    }
}
