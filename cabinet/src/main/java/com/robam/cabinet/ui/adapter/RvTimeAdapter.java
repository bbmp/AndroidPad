package com.robam.cabinet.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.cabinet.R;
import com.robam.common.utils.LogUtils;

public class RvTimeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int pickPosition;

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvTimeAdapter() {
        super(R.layout.cabinet_item_layout_time_select);
    }
    @Override
    public int getItemCount() {
        if (getData().size() <= 1)
            return 1;
        return Integer.MAX_VALUE;
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
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_select, s);
        TextView tvMin = baseViewHolder.getView(R.id.tv_min);
        if (getItemPosition(s) == pickPosition) {
            tvMin.setVisibility(View.VISIBLE);
        } else {

            tvMin.setVisibility(View.INVISIBLE);
        }
    }
}
