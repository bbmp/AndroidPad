package com.robam.ventilator.ui.pages;

import android.content.Intent;
import android.os.Handler;
import android.serialport.helper.SerialPortHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.skin.SkinDisplayUtils;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MD5Utils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ScreenUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.ui.activity.MainActivity;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBasePage;
import com.robam.ventilator.bean.AccountInfo;
import com.robam.ventilator.bean.Device;
import com.robam.ventilator.bean.ProductMutiItem;
import com.robam.ventilator.bean.UserInfo;
import com.robam.ventilator.bean.VenFunBean;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetDeviceRes;
import com.robam.ventilator.response.GetTokenRes;
import com.robam.ventilator.response.GetUserInfoRes;
import com.robam.ventilator.ui.activity.AddDeviceMainActivity;
import com.robam.ventilator.ui.activity.LoginPasswordActivity;
import com.robam.ventilator.ui.activity.MatchNetworkActivity;
import com.robam.ventilator.ui.activity.ShortcutActivity;
import com.robam.ventilator.ui.activity.SimpleModeActivity;
import com.robam.ventilator.ui.adapter.RvMainFunctonAdapter;
import com.robam.ventilator.ui.adapter.RvProductsAdapter;
import com.robam.ventilator.ui.adapter.RvSettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends VentilatorBasePage {
    /**
     * 主功能
     */
    private RecyclerView recyclerView;
    /**
     *
     */
    private RvMainFunctonAdapter rvFunctionAdapter;
    /**
     * 系统设置
     */
    private RecyclerView rvLeft;
    /**
     * 产品中心
     */
    private RecyclerView rvRight;
    private List<VenFunBean> funList = new ArrayList<>();
    //系统设置menu
    private List<VenFunBean> settingList = new ArrayList<>();
    //系统设置
    private RvSettingAdapter settingAdapter;
    //产品中心
    private List<ProductMutiItem> productList = new ArrayList<>();
    private RvProductsAdapter rvProductsAdapter;
    private TextView tvPerformance, tvComfort;
    private Group group;
    private DrawerLayout drawerLayout;
    private LinearLayout llSetting, llProducts;
    private static final String PASSWORD_LOGIN = "mobilePassword";

    public static HomePage newInstance() {
        return new HomePage();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_page_layout_home;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.rv_fun);
        tvPerformance = findViewById(R.id.tv_performance);
        tvComfort = findViewById(R.id.tv_comfort);
        group = findViewById(R.id.ventilator_group);
        drawerLayout = findViewById(R.id.drawer_layout);
//        drawerLayout.setScrimColor(getResources().getColor(R.color.ventilator_drawer_bg));
        llSetting = findViewById(R.id.ll_drawer_left);
        llProducts = findViewById(R.id.ll_drawer_right);
        rvLeft = findViewById(R.id.rv_left);
        rvRight = findViewById(R.id.rv_right);

        tvPerformance.setSelected(true);

        rvLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRight.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        //获取px
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_45)));
        showCenter();
        setOnClickListener(R.id.tv_performance, R.id.tv_comfort, R.id.ll_drawer_left, R.id.ll_drawer_right);

        //禁止手勢滑動
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //抽屉滑动式回调的方法
//                LogUtils.e("x" + drawerView.getWidth());
//                LogUtils.e("---onDrawerSlide---"+slideOffset);
                if (drawerView.getId() == R.id.drawer_left)
                    llSetting.setX(slideOffset * drawerView.getWidth());
                else {
//                    LogUtils.e("x" + llProducts.getX());
                    int width = ScreenUtils.getWidthPixels(getContext());
                    llProducts.setX(width - slideOffset * drawerView.getWidth() - getContext().getResources().getDimension(com.robam.common.R.dimen.dp_90));
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //抽屉打开时会回调的方法
                LogUtils.i("---onDrawerOpened---");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                LogUtils.i("---onDrawerClosed---");

                llSetting.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_transparent));
                llProducts.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_transparent));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //抽屉的状态改变时会回调的方法
                LogUtils.i("---onDrawerStateChanged---");
            }
        });
    }

    @Override
    protected void initData() {
        //主功能
        funList.add(new VenFunBean(1, "fun1", "logo_roki", "mode1", "into"));
        funList.add(new VenFunBean(1, "fun2", "logo_roki", "mode2", "into"));
        funList.add(new VenFunBean(1, "fun3", "logo_roki", "mode3", "into"));
        funList.add(new VenFunBean(1, "fun4", "logo_roki", "mode4", "into"));
        rvFunctionAdapter = new RvMainFunctonAdapter();
        recyclerView.setAdapter(rvFunctionAdapter);
        rvFunctionAdapter.setList(funList);
        //主功能
        rvFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0)
                    screenLock();
                else {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                rvFunctionAdapter.setPickPosition(position);
            }
        });
        //左边菜单
        settingAdapter = new RvSettingAdapter();
        rvLeft.setAdapter(settingAdapter);
        //设置功能
        settingList.add(new VenFunBean(1, "个人中心", "", "personal_center", "com.robam.ventilator.ui.activity.PersonalCenterActivity"));
        settingList.add(new VenFunBean(2, "网络设置", "", "wifi_connect", "com.robam.ventilator.ui.activity.WifiSettingActivity"));
        settingList.add(new VenFunBean(3, "时间设定", "", "date_setting", "com.robam.ventilator.ui.activity.DateSettingActivity"));
        settingList.add(new VenFunBean(4, "屏幕亮度", "", "screen_brightness", "com.robam.ventilator.ui.activity.ScreenBrightnessActivity"));
        settingList.add(new VenFunBean(5, "恢复出厂", "", "reset", "com.robam.ventilator.ui.activity.AboutActivity"));
        settingList.add(new VenFunBean(6, "关于售后", "", "sale_service", "com.robam.ventilator.ui.activity.SaleServiceActivity"));
        settingList.add(new VenFunBean(7, "关于产品", "", "about_product", "com.robam.ventilator.ui.activity.AboutActivity"));
        settingList.add(new VenFunBean(8, "智能设置", "", "smart_setting", "com.robam.ventilator.ui.activity.SmartSettingActivity"));
        settingList.add(new VenFunBean(9, "极简模式", "", "simple_mode", "com.robam.ventilator.ui.activity.SimpleModeActivity"));
        settingAdapter.setList(settingList);

        settingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //close setting menu
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                VenFunBean venFunBean = (VenFunBean) adapter.getItem(position);
                if (venFunBean.funtionCode == 9) {
                    simpleMode(); //进入极简模式
                } else {
                    Intent intent = new Intent();

                    intent.setClassName(getContext(), venFunBean.into);
                    startActivity(intent);
                }
            }
        });
        //右边菜单
        rvProductsAdapter  = new RvProductsAdapter();
        rvRight.setAdapter(rvProductsAdapter);
        productList.add(new ProductMutiItem(ProductMutiItem.IMAGE, ""));
        productList.add(new ProductMutiItem(ProductMutiItem.BUTTON, ""));
        rvProductsAdapter.setList(productList);

        rvProductsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //close products menu
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
                ProductMutiItem productMutiItem = (ProductMutiItem) adapter.getItem(position);
                if (productMutiItem.getItemType() == ProductMutiItem.BUTTON) {  //添加产品
                    startActivity(new Intent(getContext(), AddDeviceMainActivity.class));
                }
            }
        });
        //监听用户登录状态
        AccountInfo.getInstance().getUser().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                getDeviceInfo(userInfo);
            }
        });
        //未登录
        if (null == AccountInfo.getInstance().getUser().getValue()) {
           String json = MMKVUtils.getUser();
           try {
               //密码登录 自动登录
               UserInfo info = new Gson().fromJson(json, UserInfo.class);
               getToken(info.phone, info.password);
           } catch (Exception e) {

           }
        }
    }

    /**
     * 获取设备信息
     * @param userInfo
     */
    private void getDeviceInfo(UserInfo userInfo) {
        if (null != userInfo) {
            CloudHelper.getDevices(this, userInfo.id, GetDeviceRes.class, new RetrofitCallback<GetDeviceRes>() {
                @Override
                public void onSuccess(GetDeviceRes getDeviceRes) {
                    if (null != getDeviceRes) {
                        List<Device> deviceList = getDeviceRes.devices;
                        productList.clear();
                        productList.add(new ProductMutiItem(ProductMutiItem.IMAGE, ""));
                        for (Device device: deviceList)
                            productList.add(new ProductMutiItem(ProductMutiItem.DEVICE, device));
                        productList.add(new ProductMutiItem(ProductMutiItem.BUTTON, ""));
                        if (null != rvProductsAdapter)
                            rvProductsAdapter.setList(productList);
                    }
                }

                @Override
                public void onFaild(String err) {
                    LogUtils.e("getDevices" + err);
                }
            });
        } else {
            //logout
            productList.clear();
            productList.add(new ProductMutiItem(ProductMutiItem.IMAGE, ""));
            productList.add(new ProductMutiItem(ProductMutiItem.BUTTON, ""));
            if (null != rvProductsAdapter)
                rvProductsAdapter.setList(productList);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_performance) {
            if (!tvPerformance.isSelected()) {
                tvPerformance.setSelected(true);
                tvComfort.setSelected(false);
                group.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_comfort) {
            if (!tvComfort.isSelected()) {
                tvPerformance.setSelected(false);
                tvComfort.setSelected(true);
                group.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_drawer_left) {
            open(Gravity.LEFT);
        } else if (id == R.id.ll_drawer_right) {
            open(Gravity.RIGHT);
        }
    }

    public void open(int gravity) {
        //判断当前
        if (drawerLayout.isDrawerOpen(gravity)) {
            drawerLayout.closeDrawer(gravity);
        }
        else {
            if (gravity == Gravity.LEFT)
                llSetting.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_color_menu));
            if (gravity == Gravity.RIGHT)
                llProducts.setBackgroundColor(getContext().getResources().getColor(R.color.ventilator_color_menu));
            drawerLayout.openDrawer(gravity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //锁屏
    private void screenLock() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_LOCK);
        iDialog.setCancelable(false);
        //长按解锁
        ImageView imageView = iDialog.getRootView().findViewById(R.id.iv_lock);
        ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iDialog.dismiss();
                if (null != rvFunctionAdapter)
                    rvFunctionAdapter.setPickPosition(-1);
                return true;
            }
        });
        iDialog.show();
    }

    //切换至极简模式
    private void simpleMode() {
        IDialog iDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.ventilator_simple_mode_hint);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_ok) {
                    startActivity(new Intent(getContext(), SimpleModeActivity.class));
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }
    //获取token
    private void getToken(String phone, String pwd) {
        CloudHelper.getToken(this, PASSWORD_LOGIN, phone, "", pwd, "roki_client", "test", "RKDRD",
                GetTokenRes.class, new RetrofitCallback<GetTokenRes>() {
                    @Override
                    public void onSuccess(GetTokenRes getTokenRes) {
                        if (null != getTokenRes) {
                            getUserInfo(getTokenRes.getAccess_token());
                        } else {
                            ToastUtils.showShort(getContext(), R.string.ventilator_request_failed);
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        LogUtils.e("getToken" + err);
                    }
                });
    }
    //获取用户信息
    private void getUserInfo(String access_token) {
        CloudHelper.getUserInfo(this, access_token, GetUserInfoRes.class, new RetrofitCallback<GetUserInfoRes>() {
            @Override
            public void onSuccess(GetUserInfoRes getUserInfoRes) {
                if (null != getUserInfoRes && null != getUserInfoRes.getUser()) {
                    UserInfo info = getUserInfoRes.getUser();
                    info.loginType = PASSWORD_LOGIN;
                    //保存用户信息及登录状态
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(info);
                    //绑定设备
//                    bindDevice(getUserInfoRes.getUser().id);
                } else {
                    ToastUtils.showShort(getContext(), R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("getUserInfo" + err);
            }
        });
    }
}