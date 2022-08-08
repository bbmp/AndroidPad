package com.robam.ventilator.ui.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.ProductMutiItem;

public class RvProductsAdapter extends BaseMultiItemQuickAdapter<ProductMutiItem, BaseViewHolder> {

    public RvProductsAdapter() {
        addItemType(ProductMutiItem.IMAGE, R.layout.ventilator_item_layout_image);
        addItemType(ProductMutiItem.BUTTON, R.layout.ventilator_item_layout_button);
        addItemType(ProductMutiItem.DEVICE, R.layout.ventilator_item_layout_device);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ProductMutiItem productMutiItem) {
        switch (productMutiItem.getItemType()) {
            case ProductMutiItem.IMAGE:
                baseViewHolder.setImageResource(R.id.iv_head, R.mipmap.ventilator_ic_bg);
                break;
            case ProductMutiItem.BUTTON:
                break;
            case ProductMutiItem.DEVICE: {
                baseViewHolder.setText(R.id.tv_device_name, "一体机");

            }
                break;
        }
    }
}