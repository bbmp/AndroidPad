package com.robam.steamoven.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;

public class RvModeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int pickPosition;

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if (getData().size() <= 1)
            return 1;
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemPosition(@Nullable String item) {
        return super.getItemPosition(item);
    }

    @Override
    public String getItem(int position) {
        int count = getHeaderLayoutCount() + getData().size();
        position = position % count ;
        return super.getItem(position);
    }

    //重写此方法，因为BaseQuickAdapter里绘制view时会调用此方法判断，position减去getHeaderLayoutCount小于data.size()时才会调用调用cover方法绘制我们自定义的view
    @Override
    public int getItemViewType(int position) {
        int count = getHeaderLayoutCount() + getData().size();
        //刚开始进入包含该类的activity时,count为0。就会出现0%0的情况，这会抛出异常，所以我们要在下面做一下判断
        if (count <= 0) {
            count = 1;
        }
        int newPosition = position % count;
        LogUtils.i("newPosition：" + newPosition);
        return super.getItemViewType(newPosition);
    }


    public RvModeAdapter() {
        super(R.layout.steam_item_mode_select);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_select, s);
    }
}
