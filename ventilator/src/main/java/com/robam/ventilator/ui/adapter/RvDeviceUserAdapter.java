package com.robam.ventilator.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.common.bean.UserInfo;

public class RvDeviceUserAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    public RvDeviceUserAdapter() {
        super(R.layout.ventilator_item_layout_device_user);
        //一定要在构造里设置
        addChildClickViewIds(R.id.iv_delete);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, UserInfo userInfo) {
        if (null != userInfo) {
            baseViewHolder.setText(R.id.tv_name, userInfo.nickname);
            baseViewHolder.setText(R.id.tv_phone, userInfo.phone);
        }
    }
}
