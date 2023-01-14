package com.robam.steamoven.ui.adapter;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.FuntionBean;

public class RvMainFuntionAdapter extends BaseQuickAdapter<FuntionBean, BaseViewHolder> implements View.OnClickListener{
    private int pickPosition;
    private ItemClick itemClick;

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvMainFuntionAdapter() {
        super(R.layout.steam_item_main_function);
    }


    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public FuntionBean getItem(int position) {
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
    protected void convert(@NonNull BaseViewHolder baseViewHolder, FuntionBean functionBean) {
        if (null != functionBean) {
            TextView textView = baseViewHolder.getView(R.id.tv_funtion_name);
            textView.setText(functionBean.funtionName);
            View view = baseViewHolder.getView(R.id.tv_funtion_bg);
            view.setTag(functionBean);
            view.setOnClickListener(this);
//            mTextView.setTextColor(getColor(R.color.common_text_color));
//            if (pickPosition == getItemPosition(functionBean)) {
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_80));
//            } else
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_40));
        }
    }



    @Override
    public void onClick(View view) {
        if(itemClick != null){
            itemClick.itemClick((FuntionBean) view.getTag());
        }
    }

    public static interface ItemClick{
        void itemClick(FuntionBean funtionBean);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
}
