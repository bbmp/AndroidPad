package com.robam.ventilator.ui.pages;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.ventilator.bean.Device;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceUserRes;
import com.robam.ventilator.ui.adapter.RvDeviceUserAdapter;

public class DeviceUserPage extends VentilatorBasePage {
    private Device device;
    private RecyclerView rvUser;
    private RvDeviceUserAdapter rvDeviceUserAdapter;

    public DeviceUserPage(Device device) {
        this.device = device;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_bind_device;
    }

    @Override
    protected void initView() {
        rvUser = findViewById(R.id.rv_user);
        rvUser.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvUser.addItemDecoration(new GridSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_58)));
        rvDeviceUserAdapter = new RvDeviceUserAdapter();
        rvUser.setAdapter(rvDeviceUserAdapter);
    }

    @Override
    protected void initData() {
        getDeviceUsers(device);
    }

    //获取设备绑定的用户
    private void getDeviceUsers(Device device) {
        CloudHelper.getDeviceUsers(this, device.ownerId, device.guid, GetDeviceUserRes.class, new RetrofitCallback<GetDeviceUserRes>() {

            @Override
            public void onSuccess(GetDeviceUserRes getDeviceUserRes) {
                if (null != getDeviceUserRes && null != getDeviceUserRes.users)
                    rvDeviceUserAdapter.setList(getDeviceUserRes.users);
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }
}
