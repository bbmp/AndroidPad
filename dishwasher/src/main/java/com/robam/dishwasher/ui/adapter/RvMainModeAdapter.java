package com.robam.dishwasher.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.bean.DishWasherModeBean;

public class RvMainModeAdapter extends BaseQuickAdapter<DishWasherModeBean, BaseViewHolder> implements View.OnClickListener{
    private int pickPosition;
//    private RequestOptions options = RequestOptions.bitmapTransform(new MultiTransformation(new BlurTransformation(30, 3)));
    private ItemClick itemClick;
    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvMainModeAdapter() {
        super(R.layout.dishwasher_item_main_mode);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public DishWasherModeBean getItem(int position) {
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
    protected void convert(@NonNull BaseViewHolder baseViewHolder, DishWasherModeBean dishWaherModeBean) {
        if (null != dishWaherModeBean) {
            TextView textView = baseViewHolder.getView(R.id.tv_mode_name);
            textView.setText(dishWaherModeBean.name);
            View view = baseViewHolder.getView(R.id.iv_round_bg);
            view.setTag(dishWaherModeBean);
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if(itemClick != null){
            itemClick.itemClick((DishWasherModeBean) view.getTag());
        }
    }

    public static interface ItemClick{
        void itemClick(DishWasherModeBean washerModeBean);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
}
