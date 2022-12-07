package com.robam.stove.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.stove.R;
import com.robam.stove.bean.Material;
import com.robam.stove.bean.MaterialClassify;

public class RvMaterialAdapter extends BaseQuickAdapter<Material, BaseViewHolder> {
    public RvMaterialAdapter() {
        super(R.layout.stove_item_material);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Material material) {
        if (null != material) {
            baseViewHolder.setText(R.id.tv_material_name, material.getName());
            baseViewHolder.setText(R.id.tv_material_num, material.getStandardWeight() + material.getStandardUnit());
        }
    }
}