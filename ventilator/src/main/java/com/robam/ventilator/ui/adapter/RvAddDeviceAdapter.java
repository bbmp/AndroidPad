package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.Device;

public class RvAddDeviceAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    public RvAddDeviceAdapter() {
        super(R.layout.ventilator_item_layout_add_device);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Device device) {
        if (null != device) {
            baseViewHolder.setText(R.id.tv_device_name, device.getName());
            baseViewHolder.setText(R.id.tv_model, device.getModel());
            baseViewHolder.setImageResource(R.id.iv_device, R.drawable.logo_roki);
            addChildClickViewIds(R.id.btn_add);
        }
    }
}
