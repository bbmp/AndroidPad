package com.robam.ventilator.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.Device;

public class RvShortcutDeviceAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    public RvShortcutDeviceAdapter() {
        super(R.layout.ventilator_item_layout_shortcut_device);
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Device device) {
        if (null != device) {
            baseViewHolder.setText(R.id.tv_device_name, device.getCategoryName());
            baseViewHolder.setText(R.id.tv_model, device.getDisplayType());
            baseViewHolder.setImageResource(R.id.iv_device, R.drawable.logo_roki);
        }
    }
}
