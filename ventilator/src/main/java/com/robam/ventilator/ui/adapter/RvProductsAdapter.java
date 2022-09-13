package com.robam.ventilator.ui.adapter;


import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.IDeviceType;
import com.robam.common.bean.Device;
import com.robam.stove.device.HomeStove;
import com.robam.common.utils.ImageUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.Ventilator;

import java.util.List;

public class RvProductsAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    private LifecycleOwner mOwner;

    public RvProductsAdapter(LifecycleOwner owner) {
        super(R.layout.ventilator_item_layout_device);
        this.mOwner = owner;
        //左灶关火
        addChildClickViewIds(R.id.btn_left_close);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Device device) {
        if (null != device) {

            baseViewHolder.setText(R.id.tv_device_name, device.getCategoryName());
            baseViewHolder.setText(R.id.tv_model, device.getDisplayType());
            ImageUtils.loadImage(getContext(), device.deviceTypeIconUrl, baseViewHolder.getView(R.id.iv_device));
            if (IDeviceType.RXDG.equals(device.dc)) {
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (device.getWorkStatus() == 0) {
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "消杀洁净\n智享厨房");
                    } else if (device.getWorkStatus() != 0) {
                        baseViewHolder.setGone(R.id.layout_offline, true);
                        baseViewHolder.setVisible(R.id.layout_work, true);
                    } else {
                        //故障
                    }
                }
            } else if (IDeviceType.RYYJ.equals(device.dc)) {//灶具
                if (device.getStatus() == Device.OFFLINE) {
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (device.getWorkStatus() == 0) {  //未工作
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
                    } else {
                        if (device.getWorkStatus() != 0) { //工作状态
                            baseViewHolder.setGone(R.id.layout_offline, true);
                            baseViewHolder.setVisible(R.id.layout_work, true);
                            baseViewHolder.setVisible(R.id.ventilator_group7, true);//显示灶具
                            baseViewHolder.setGone(R.id.ventilator_group6, true);
                            TextView leftClose = baseViewHolder.getView(R.id.btn_left_close);
                            if (HomeStove.getInstance().leftWorkMode != 0) {
                                leftClose.setBackgroundResource(R.drawable.ventilator_shape_button_selected);
                                leftClose.setTextColor(getContext().getResources().getColor(R.color.ventilator_white));
                            } else {
                                leftClose.setBackgroundResource(R.drawable.ventilator_shape_button_unselected);
                                leftClose.setTextColor(getContext().getResources().getColor(R.color.ventilator_white_50));
                            }
                        }
                    }
                }
            } else if (IDeviceType.RZKY.equals(device.dc)) { //一体机
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (device.getWorkStatus() == 0) {
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
                    } else if (device.getWorkStatus() != 0) {
                        baseViewHolder.setGone(R.id.layout_offline, true);
                        baseViewHolder.setVisible(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.ventilator_group7, true);
                        baseViewHolder.setVisible(R.id.ventilator_group6, true);
                    } else {
                        //故障
                    }
                }
            } else if (IDeviceType.RXWJ.equals(device.dc)) { //洗碗机
                if (device.getStatus() == Device.OFFLINE) {//离线
                    baseViewHolder.setVisible(R.id.layout_offline, true);
                    baseViewHolder.setGone(R.id.layout_work, true);
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
                } else {
                    //在线
                    baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
                    baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
                    if (device.getWorkStatus() == 0) {
                        baseViewHolder.setVisible(R.id.layout_offline, true);
                        baseViewHolder.setGone(R.id.layout_work, true);
                        baseViewHolder.setGone(R.id.btn_detail, true);
                        baseViewHolder.setText(R.id.tv_hint, "会洗锅的\n洗碗机");
                    } else if (device.getWorkStatus() != 0) {
                        baseViewHolder.setGone(R.id.layout_offline, true);
                        baseViewHolder.setVisible(R.id.layout_work, true);
                    } else {
                        //故障
                    }
                }
            }
        }

    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {
        super.onBindViewHolder(baseViewHolder, position);
//        if (getItemViewType(position) == ProductMutiItem.DEVICE) {
//            ProductMutiItem productMutiItem = getItem(position);
//            Device device = productMutiItem.getDevice();
//            device.observe(mOwner, new Observer<Device>() {
//                @Override
//                public void onChanged(Device device) {
//                    if (IDeviceType.RYYJ.equals(device.dc)) {//灶具
//                        if (device.getStatus() == Device.OFFLINE) {
//                            baseViewHolder.setVisible(R.id.layout_offline, true);
//                            baseViewHolder.setGone(R.id.layout_work, true);
//                            baseViewHolder.setText(R.id.tv_online, R.string.ventilator_offline);
//                            baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_offline_bg);
//                        } else {
//                            //在线
//                            baseViewHolder.setText(R.id.tv_online, R.string.ventilator_online);
//                            baseViewHolder.setImageResource(R.id.iv_online, R.drawable.ventilator_shape_online_bg);
//                            if (device.getWorkStatus() == 0) {  //未工作
//                                baseViewHolder.setVisible(R.id.layout_offline, true);
//                                baseViewHolder.setGone(R.id.layout_work, true);
//                                baseViewHolder.setGone(R.id.btn_detail, true);
//                                baseViewHolder.setText(R.id.tv_hint, "轻松烹饪\n智享厨房");
//                            } else {
//                                if (device.getWorkStatus() != 0) { //工作状态
//                                    baseViewHolder.setGone(R.id.layout_offline, true);
//                                    baseViewHolder.setVisible(R.id.layout_work, true);
//                                    baseViewHolder.setVisible(R.id.ventilator_group7, true);//显示灶具
//                                    baseViewHolder.setGone(R.id.ventilator_group6, true);
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//        }
    }

}
