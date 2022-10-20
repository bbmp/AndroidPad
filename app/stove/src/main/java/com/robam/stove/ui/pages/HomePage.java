package com.robam.stove.ui.pages;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.utils.TimeUtils;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.device.HomeStove;
import com.robam.common.manager.FunctionManager;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.ImageUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.bean.StoveFunBean;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.adapter.RvMainFunctionAdapter;
import com.robam.stove.ui.dialog.HomeLockDialog;

import java.util.List;

public class HomePage extends StoveBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private PickerLayoutManager pickerLayoutManager;
    //浮标
    private ImageView ivFloat;
    private LinearLayout llLeftStove, llRightStove;
    private TextView tvLeftStove, tvRightStove;
    //背景
    private ImageView imageView;
    private ImageView ivLock;

    private IDialog iDialogStop, iDialogAffirm;

    private HomeLockDialog homeLockDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();

        rvMain = findViewById(R.id.rv_main);
        ivFloat = findViewById(R.id.iv_float);
        imageView = findViewById(R.id.iv_bg);
        llLeftStove = findViewById(R.id.ll_left_stove);
        llRightStove = findViewById(R.id.ll_right_stove);
        tvLeftStove = findViewById(R.id.tv_left_stove);
        tvRightStove = findViewById(R.id.tv_right_stove);
        ivLock = findViewById(R.id.iv_lock);

        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
                .setAlpha(false)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setBackground(position);
                        //指示器更新
                        rvMainFunctionAdapter.setPickPosition(position);
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvMainFunctionAdapter = new RvMainFunctionAdapter();

        rvMain.setAdapter(rvMainFunctionAdapter);
        setOnClickListener(R.id.iv_float, R.id.ll_left_stove, R.id.ll_right_stove, R.id.iv_lock);
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s) && device instanceof Stove && device.guid.equals(HomeStove.getInstance().guid)) {
                        Stove stove = (Stove) device;
                        if (stove.lockStatus == StoveConstant.LOCK) { //锁屏状态
                            screenLock();
                        } else {
                            if (null != homeLockDialog) {
                                homeLockDialog.dismiss();
                            }
                        }
                        //左灶
                        if (stove.leftStatus == StoveConstant.STOVE_CLOSE || stove.leftLevel == 0) {
                            //关火状态
                            llLeftStove.setVisibility(View.INVISIBLE);
                            stove.leftWorkMode = 0;
                            stove.leftTimeHours = 0;
                            stove.leftWorkTemp = 0;

                        } else {
                            //开火状态
                            llLeftStove.setVisibility(View.VISIBLE);
                            if (stove.leftWorkMode == StoveConstant.MODE_FRY)
                                tvLeftStove.setText("左灶 " + stove.leftWorkTemp + "℃");
                            else
                                tvLeftStove.setText("左灶 " + stove.leftLevel + "档 " +  TimeUtils.secToMin(stove.leftTimeHours));
                        }
                        //右灶
                        if (stove.rightStatus == StoveConstant.STOVE_CLOSE || stove.rightLevel == 0) {
                            //关火状态
                            llRightStove.setVisibility(View.INVISIBLE);
                            stove.rightWorkMode = 0;
                            stove.rightTimeHours = 0;
                            stove.rightWorkTemp = 0;
                        } else {
                            //开火状态
                            llRightStove.setVisibility(View.VISIBLE);
                            if (stove.rightWorkMode == StoveConstant.MODE_FRY)
                                tvRightStove.setText("右灶 " + stove.rightWorkTemp + "℃");
                            else
                                tvRightStove.setText("右灶 " + stove.rightLevel + "档 " +  TimeUtils.secToMin(stove.rightTimeHours));
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        //初始化数据
        List<StoveFunBean> functionList = FunctionManager.getFuntionList(getContext(), StoveFunBean.class, R.raw.stove);

        if (null != functionList) {
            rvMainFunctionAdapter.setList(functionList);

            //初始位置
            int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % functionList.size();
            rvMainFunctionAdapter.setPickPosition(initPos);
            pickerLayoutManager.scrollToPosition(initPos);
            setBackground(initPos);

            rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    StoveFunBean stoveFunBean = (StoveFunBean) adapter.getItem(position);

                    HomeStove.getInstance().funCode = stoveFunBean.funtionCode;
                    Intent intent = new Intent();
                    intent.putExtra(StoveConstant.EXTRA_MODE_LIST, stoveFunBean.mode);
                    intent.setClassName(getContext(), stoveFunBean.into);
                    startActivity(intent);
                }
            });
        }
    }

    //锁屏
    private void screenLock() {
        if (null == homeLockDialog) {
            homeLockDialog = new HomeLockDialog(getContext());
            homeLockDialog.setCancelable(false);
            //长按解锁
            ImageView imageView = homeLockDialog.getRootView().findViewById(R.id.iv_lock);
            ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    StoveAbstractControl.getInstance().setLock(HomeStove.getInstance().guid, StoveConstant.UNLOCK);
                    return true;
                }
            });
            //分别设置左右灶,不关闭对话框
            LinearLayout leftStove = homeLockDialog.getRootView().findViewById(R.id.ll_left_stove);
            LinearLayout rightStove = homeLockDialog.getRootView().findViewById(R.id.ll_right_stove);
            leftStove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopCook(IPublicStoveApi.STOVE_LEFT);
                }
            });
            rightStove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopCook(IPublicStoveApi.STOVE_RIGHT);
                }
            });
        }
        homeLockDialog.checkStoveStatus();
        homeLockDialog.show();
    }

    //锁屏确认
    private void affirmLock() {
        if (null == iDialogAffirm) {
            iDialogAffirm = StoveDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            iDialogAffirm.setCancelable(false);
            iDialogAffirm.setContentText(R.string.stove_affirm_lock_hint);
            iDialogAffirm.setOKText(R.string.stove_ok);
            iDialogAffirm.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        StoveAbstractControl.getInstance().setLock(HomeStove.getInstance().guid, StoveConstant.LOCK);
                    }
                    iDialogAffirm = null;
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        iDialogAffirm.show();
    }
    //确认结束烹饪
    private void stopCook(int stove) {
        if (null == iDialogStop) {
            iDialogStop = StoveDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            iDialogStop.setCancelable(false);
            iDialogStop.setContentText(R.string.stove_stop_cook_hint);
            iDialogStop.setOKText(R.string.stove_stop_cook);
            iDialogStop.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {

                        closeFire(stove);
                    }
                    iDialogStop = null;
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        iDialogStop.show();
    }
    //关火
    private void closeFire(int stoveId) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && null != device.guid) {
                StoveAbstractControl.getInstance().setAttribute(device.guid, (byte) stoveId, (byte) 0x00, (byte) StoveConstant.STOVE_CLOSE);
                break;
            }
        }
    }

    /**
     * 设置背景图片
     *
     * @param index
     */
    private void setBackground(int index) {
        //设置背景图片
        int resId = getResources().getIdentifier(rvMainFunctionAdapter.getItem(index).backgroundImg, "drawable", getContext().getPackageName());
        ImageUtils.loadGif(getContext(), resId, imageView);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_float) {
            //快捷入口 隐式启动 降低耦合
            Intent intent = new Intent();
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left_stove) {
            if (HomeStove.getInstance().isStoveOffline()) {
                ToastUtils.showShort(getContext(), R.string.stove_offline);
                return;
            }
            stopCook(IPublicStoveApi.STOVE_LEFT);
        } else if (id == R.id.ll_right_stove) {
            if (HomeStove.getInstance().isStoveOffline()) {
                ToastUtils.showShort(getContext(), R.string.stove_offline);
                return;
            }
            stopCook(IPublicStoveApi.STOVE_RIGHT);
        } else if (id == R.id.iv_lock) {
            if (HomeStove.getInstance().isStoveOffline()) {
                ToastUtils.showShort(getContext(), R.string.stove_offline);
                return;
            }
            //锁屏提示
            affirmLock();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭dialog
        if (null != iDialogStop && iDialogStop.isShow())
            iDialogStop.dismiss();
        if (null != iDialogAffirm && iDialogAffirm.isShow())
            iDialogAffirm.dismiss();
        if (null != homeLockDialog && homeLockDialog.isShow())
            homeLockDialog.dismiss();
    }
}
