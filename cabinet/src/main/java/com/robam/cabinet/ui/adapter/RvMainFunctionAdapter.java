package com.robam.cabinet.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.cabinet.R;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.LogUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class RvMainFunctionAdapter extends BaseQuickAdapter<CabFunBean, BaseViewHolder> {
    private int pickPosition;
    private RequestOptions options = RequestOptions.bitmapTransform(new MultiTransformation(new BlurTransformation(1, 1), new CircleCrop()));

    public void setPickPosition(int pickPosition) {
        this.pickPosition = pickPosition % getData().size();
        notifyDataSetChanged();
    }

    public RvMainFunctionAdapter() {
        super(R.layout.cabinet_item_main_function);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public CabFunBean getItem(int position) {
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
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CabFunBean cabFunBean) {
        if (null != cabFunBean) {
            TextView textView = baseViewHolder.getView(R.id.tv_funtion_name);
            textView.setText(cabFunBean.funtionName);
            //ImageView imageView = baseViewHolder.getView(R.id.iv_round_bg);
            //ImageUtils.loadImage(getContext(), R.drawable.cabinet_main_item_bg, options, imageView);
//            if (pickPosition == getItemPosition(functionBean)) {
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_80));
//            } else
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_40));
        }
    }
}
