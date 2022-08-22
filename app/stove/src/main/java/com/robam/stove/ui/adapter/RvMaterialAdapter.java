package com.robam.stove.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.stove.R;
import com.robam.stove.bean.RecipeMaterial;

public class RvMaterialAdapter extends BaseQuickAdapter<RecipeMaterial, BaseViewHolder> {
    public RvMaterialAdapter() {
        super(R.layout.stove_item_material);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RecipeMaterial recipeMaterial) {
        if (null != recipeMaterial) {
            baseViewHolder.setText(R.id.tv_material_name, recipeMaterial.getName());
            baseViewHolder.setText(R.id.tv_material_num, recipeMaterial.getNum() + recipeMaterial.getUnit());
        }
    }
}