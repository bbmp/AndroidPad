package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.Device;

public class RvBindDeviceAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    public RvBindDeviceAdapter() {
        super(R.layout.ventilator_item_layout_bind_device);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Device device) {

    }
}
