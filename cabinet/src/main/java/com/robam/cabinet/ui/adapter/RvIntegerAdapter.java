package com.robam.cabinet.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.cabinet.R;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.common.utils.LogUtils;

public class RvIntegerAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    private int pickPosition;

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvIntegerAdapter() {
        super(R.layout.cabinet_item_layout_integer);
    }
    @Override
    public int getItemCount() {
        if (getData().size() <= 1)
            return 1;
        return Integer.MAX_VALUE;
    }

    @Override
    public Integer getItem(int position) {
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
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Integer integer) {
        TextView textView = baseViewHolder.getView(R.id.tv_num);
        textView.setText(integer.toString());
//        if (pickPosition == getItemPosition(integer)) {
//            baseViewHolder.setVisible(R.id.tv_min, true);
//        } else {
//            baseViewHolder.setVisible(R.id.tv_min, false);
//        }
    }
}
