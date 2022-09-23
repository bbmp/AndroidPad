package com.robam.steamoven.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.Material;

public class RvMaterialAdapter extends BaseQuickAdapter<Material, BaseViewHolder> {
    public RvMaterialAdapter() {
        super(R.layout.steam_item_material);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Material material) {
        if (null != material) {
            baseViewHolder.setText(R.id.tv_material_name, material.getName());
            baseViewHolder.setText(R.id.tv_material_num, material.getStandardWeight() + material.getStandardUnit());
        }
    }
}