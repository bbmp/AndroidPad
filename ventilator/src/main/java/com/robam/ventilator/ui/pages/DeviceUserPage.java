package com.robam.ventilator.ui.pages;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.robam.common.IDeviceType;
import com.robam.common.bean.BaseResponse;
import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.manager.LiveDataBus;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceUserRes;
import com.robam.ventilator.ui.adapter.RvDeviceUserAdapter;

import java.util.Iterator;
import java.util.Set;

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
                        if (userInfo.id == curUser.id) {
//                            if (device.dc.equals(IDeviceType.RYYJ) && device.guid.equals(Plat.getPlatform().getDeviceOnlySign())) { //当前烟机登录的账号
                                ToastUtils.showShort(getContext().getApplicationContext(), R.string.ventilator_donot_delete_this);
                                return;
//                            }
                        }
//                        else {
//                            //非管理员
//                            if (curUser.id != device.ownerId) {
//                                ToastUtils.showShort(getContext(), R.string.ventilator_not_administrator);
//                                return;
//                            }
//                        }
                        deleteUserDialog(userInfo.id, R.string.ventilator_delete_user_hint);
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
        if (device.dc.equals(IDeviceType.RZNG) || device.dc.equals(IDeviceType.RRQZ)) { //子设备没有用户
            btnShare.setText(R.string.ventilator_delete_device);
            return;
        }
        if (null != curUser) {
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
    }
    //删除用户确认
    private void deleteUserDialog(long userid, int resId) {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(resId);
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
            //先删
            deleteSubdevice(device);
            //上报
            HomeVentilator.getInstance().notifyOnline(device.guid, device.bid, device.status);
            //重新获取设备列表
            AccountInfo.getInstance().getUser().setValue(AccountInfo.getInstance().getUser().getValue());
//            AccountInfo.getInstance().getGuid().setValue(device.guid);
            return;
        }
        CloudHelper.unbindDevice(this, userid, device.guid, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                if (null != baseResponse) {
                    //解绑成功
                    deleteDevice(device);
                    //重新获取设备列表
//                    AccountInfo.getInstance().getGuid().setValue(device.guid);
                    AccountInfo.getInstance().getUser().setValue(AccountInfo.getInstance().getUser().getValue());
                    //删除烟机的用户
//                    if (Plat.getPlatform().getDeviceOnlySign().equals(device.guid))
//                        LiveDataBus.get().with(VentilatorConstant.VENTILATOR_USER, String.class).setValue(device.guid);
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
        if (id == R.id.btn_share) {
            if (IDeviceType.RRQZ.equals(device.dc) || IDeviceType.RZNG.equals(device.dc)) //子设备
                deleteUserDialog((curUser != null)?curUser.id:0, R.string.ventilator_delete_device_hint);
            else
                shareDialog();
        }
    }
    //分享控制权
    private void shareDialog() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_SHARE);
        iDialog.setCancelable(false);
        iDialog.setContentText("GUID" + device.guid);
        iDialog.show();
    }
    //删除子设备
    private void deleteSubdevice(Device device) {
        Set<String> subDevices = MMKVUtils.getSubDevice();
        if (null != subDevices) {
            Iterator<String> iterator = subDevices.iterator();
            while (iterator.hasNext()) {
                String json = iterator.next();
                Device subDevice = new Gson().fromJson(json, Device.class);
                if ((device.guid != null && device.guid.equals(subDevice.guid))
                        || (device.mac != null && device.mac.equals(subDevice.mac))) {
                    iterator.remove();//已经有记录 删除

                    deleteDevice(device);
                    break;
                }
            }
            //写回去
            MMKVUtils.setSubDevice(subDevices);
        }
    }
    //从列表中删除设备
    private void deleteDevice(Device curDevice) {
        Iterator<Device> iterator = AccountInfo.getInstance().deviceList.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (curDevice.guid.equals(device.guid)) {
                iterator.remove();
                if (device instanceof Pan) {
                    BlueToothManager.disConnect(((Pan) device).bleDevice); //断开蓝牙
                } else if (device instanceof Stove) {
                    BlueToothManager.disConnect(((Stove) device).bleDevice);
                }
                MqttManager.getInstance().unSubscribe(device.dc, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)); //取消订阅
                break;
            }
        }
    }
}
