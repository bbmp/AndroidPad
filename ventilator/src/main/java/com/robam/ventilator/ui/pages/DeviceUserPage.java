package com.robam.ventilator.ui.pages;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.robam.common.IDeviceType;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.bean.Pan;
import com.robam.stove.bean.Stove;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceUserRes;
import com.robam.ventilator.ui.adapter.RvDeviceUserAdapter;

public class DeviceUserPage extends VentilatorBasePage {
    private Device device;
    private RecyclerView rvUser;
    private RvDeviceUserAdapter rvDeviceUserAdapter;
    private UserInfo curUser; //登录用户
    private ImageView ivDevice;
    private TextView tvDeviceName;
    private TextView tvModel; //机型
    private TextView btnShare;

    public DeviceUserPage(Device device, UserInfo info) {
        this.device = device;
        this.curUser = info;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_bind_device;
    }

    @Override
    protected void initView() {
        rvUser = findViewById(R.id.rv_user);
        ivDevice = findViewById(R.id.iv_device);
        tvDeviceName = findViewById(R.id.tv_device_name);
        tvModel = findViewById(R.id.tv_model);
        btnShare = findViewById(R.id.btn_share);
        rvUser.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvUser.addItemDecoration(new GridSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_58)));
        rvDeviceUserAdapter = new RvDeviceUserAdapter();
        rvUser.setAdapter(rvDeviceUserAdapter);
        rvDeviceUserAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_delete) {
                    //删除提示
                    UserInfo userInfo = (UserInfo) adapter.getItem(position);
                    if (null != userInfo ) {
                        //删除自己
                        if (userInfo.id == curUser.id)
                            ;
                        else {
                            //非管理员
                            if (curUser.id != device.ownerId) {
                                ToastUtils.showShort(getContext(), R.string.ventilator_not_administrator);
                                return;
                            }
                        }
                        deleteUserDialog(userInfo.id);
                    }
                }
            }
        });
        setOnClickListener(R.id.btn_share);
    }

    @Override
    protected void initData() {
        tvDeviceName.setText(device.categoryName);
        tvModel.setText(device.displayType);
        if (device.dc.equals(IDeviceType.RRQZ))
            ivDevice.setImageResource(R.drawable.ventilator_stove);
        else if (device.dc.equals(IDeviceType.RXDG))
            ivDevice.setImageResource(R.drawable.ventilator_cabinet);
        else if (device.dc.equals(IDeviceType.RZKY))
            ivDevice.setImageResource(R.drawable.ventilator_steam);
        else if (device.dc.equals(IDeviceType.RZNG))
            ivDevice.setImageResource(R.drawable.ventilator_pan);
        else if (device.dc.equals(IDeviceType.RYYJ))
            ivDevice.setImageResource(R.drawable.ventilator_ventilator);
        else if (device.dc.equals(IDeviceType.RXWJ))
            ivDevice.setImageResource(R.drawable.ventilator_dishwasher);
        getDeviceUsers(device);
    }

    //获取设备绑定的用户
    private void getDeviceUsers(Device device) {
        CloudHelper.getDeviceUsers(this, curUser.id, device.guid, GetDeviceUserRes.class, new RetrofitCallback<GetDeviceUserRes>() {

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
    //删除用户确认
    private void deleteUserDialog(long userid) {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.ventilator_delete_user_hint);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    unbindDeviceUser(userid);
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
    //解绑用户
    private void unbindDeviceUser(long userid) {
        if (IDeviceType.RRQZ.equals(device.dc) || IDeviceType.RZNG.equals(device.dc)) {  //解绑子设备
            HomeVentilator.getInstance().notifyOnline(device.guid, device.bid, -1);

            //重新获取设备列表
            AccountInfo.getInstance().getUser().setValue(curUser);
            return;
        }
        CloudHelper.unbindDevice(this, userid, device.guid, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                if (null != baseResponse) {
                    //解绑成功
                    AccountInfo.getInstance().getUser().setValue(curUser);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_share)
            shareDialog();
    }
    //分享控制权
    private void shareDialog() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_SHARE);
        iDialog.setCancelable(false);
        iDialog.show();
    }
}
