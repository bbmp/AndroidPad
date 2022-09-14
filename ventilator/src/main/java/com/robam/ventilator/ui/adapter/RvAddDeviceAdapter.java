package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.IDeviceType;
import com.robam.ventilator.R;
import com.robam.common.bean.Device;

public class RvAddDeviceAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    public RvAddDeviceAdapter() {
        super(R.layout.ventilator_item_layout_add_device);
        //一定要在构造里设置
        addChildClickViewIds(R.id.btn_add);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Device device) {
        if (null != device) {
            baseViewHolder.setText(R.id.tv_device_name, device.getCategoryName());
            baseViewHolder.setText(R.id.tv_model, device.getDisplayType());
            if (device.dc.equals(IDeviceType.RRQZ))
                baseViewHolder.setImageResource(R.id.iv_device, R.drawable.ventilator_stove);
            else if (device.dc.equals(IDeviceType.RXDG))
                baseViewHolder.setImageResource(R.id.iv_device, R.drawable.ventilator_cabinet);
            else if (device.dc.equals(IDeviceType.RZKY))
                baseViewHolder.setImageResource(R.id.iv_device, R.drawable.ventilator_steam);
            else if (device.dc.equals(IDeviceType.RZNG))
                baseViewHolder.setImageResource(R.id.iv_device, R.drawable.ventilator_pan);
            else if (device.dc.equals(IDeviceType.RYYJ))
                baseViewHolder.setImageResource(R.id.iv_device, R.drawable.ventilator_ventilator);
            else if (device.dc.equals(IDeviceType.RXWJ))
                baseViewHolder.setImageResource(R.id.iv_device, R.drawable.ventilator_dishwasher);

        }
    }
}
