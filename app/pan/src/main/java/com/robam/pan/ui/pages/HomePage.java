package com.robam.pan.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.constant.StoveConstant;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.constant.PanConstant;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.ui.dialog.IDialog;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.device.HomePan;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.constant.Constant;
import com.robam.pan.R;
import com.robam.pan.base.PanBasePage;
import com.robam.pan.bean.PanFunBean;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetCurveDetailRes;
import com.robam.pan.ui.activity.CurveActivity;
import com.robam.pan.ui.activity.CurveCreateActivity;
import com.robam.pan.ui.adapter.RvMainFunctionAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends PanBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    //快炒
    private LinearLayout llQuick, llStir, llRightCenter;
    //快炒
    private TextView tvQuick;
    //十秒翻炒
    private TextView tvStir;
    //油温提示
    private TextView tvTempHint;
    //
    private IDialog batteryDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.pan_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();
        showRightCenter();
        rvMain = findViewById(R.id.rv_main);
        llQuick = findViewById(R.id.ll_quick_fry);
        llStir = findViewById(R.id.ll_stir_fry);
        llRightCenter = findViewById(R.id.ll_right_center);
        tvQuick = findViewById(R.id.tv_quick);
        tvStir = findViewById(R.id.tv_stir);
        tvTempHint = findViewById(R.id.tv_temp);
        rvMain.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvMainFunctionAdapter = new RvMainFunctionAdapter();
        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                keyTone();
                Intent intent = new Intent();
                PanFunBean panFunBean = (PanFunBean) adapter.getItem(position);
                intent.putExtra(Constant.FUNCTION_BEAN, panFunBean);
                if (panFunBean.into == null || panFunBean.into.length() == 0) {
                    ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
                    return;
                }
                intent.setClassName(getContext(), panFunBean.into);
                startActivity(intent);

            }

        });
        rvMain.setAdapter(rvMainFunctionAdapter);
        setOnClickListener(llQuick, llStir, llRightCenter);
        //监听锅状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (null != device.guid && device.guid.equals(s) && device instanceof Pan && IDeviceType.RZNG.equals(device.dc) && device.guid.equals(HomePan.getInstance().guid)) { //当前锅
                        Pan pan = (Pan) device;
                        if (pan.fryMode == PanConstant.MODE_QUICK_FRY) {
                            llQuick.setSelected(true);
                            llStir.setSelected(false);
                            tvQuick.setText(R.string.pan_quick_frying);
                            tvStir.setText(R.string.pan_stir_fry);
                        } else if (pan.fryMode == PanConstant.MODE_STIR_FRY) {
                            llStir.setSelected(true);
                            llQuick.setSelected(false);
                            tvStir.setText(R.string.pan_stir_frying);
                            tvQuick.setText(R.string.pan_quick_fry);
                        } else {
                            llQuick.setSelected(false);
                            tvQuick.setText(R.string.pan_quick_fry);
                            llStir.setSelected(false);
                            tvStir.setText(R.string.pan_stir_fry);
                        }
                        if (pan.panTemp < 60)
                            tvTempHint.setText(R.string.pan_standby_ing);
                        else if (pan.panTemp < 130)
                            tvTempHint.setText(getString(R.string.pan_oil_temp) + pan.panTemp + "℃");
                        else if (pan.panTemp >= 130 && pan.panTemp <= 180)
                            tvTempHint.setText(getString(R.string.pan_oil_temp) + pan.panTemp + getString(R.string.pan_oil_temp_hint1));
                        else if (pan.panTemp > 180 && pan.panTemp <= 240)
                            tvTempHint.setText(getString(R.string.pan_oil_temp) + pan.panTemp + getString(R.string.pan_oil_temp_hint2));
                        else if (pan.panTemp > 240 && pan.panTemp <= 280)
                            tvTempHint.setText(getString(R.string.pan_oil_temp) + pan.panTemp + getString(R.string.pan_oil_temp_hint3));
                        else
                            tvTempHint.setText(getString(R.string.pan_oil_temp) + pan.panTemp + R.string.pan_oil_temp_hint4);
                        break;
                    }
                }
            }
        });
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                Pan pan = (Pan) device;
                if (pan.mode == 1) { //曲线创建中
                    getCurveDetail();
                }
                break;
            }
        }
    }

    @Override
    protected void initData() {
        List<PanFunBean> functionList = new ArrayList<>();
        functionList.add(new PanFunBean(1, "云端菜谱", "", "recipe", "com.robam.pan.ui.activity.RecipeActivity"));
        functionList.add(new PanFunBean(2, "我的最爱", "", "favorite", "com.robam.pan.ui.activity.FavoriteActivity"));
        functionList.add(new PanFunBean(3, "烹饪曲线", "", "curve", "com.robam.pan.ui.activity.CurveActivity"));
        rvMainFunctionAdapter.setList(functionList);

    }

    //获取正在记录的曲线
    private void getCurveDetail() {
        CloudHelper.getCurvebookDetail(this, 0, HomePan.getInstance().guid, GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
            @Override
            public void onSuccess(GetCurveDetailRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.payload) {
                    PanCurveDetail panCurveDetail = getCurveDetailRes.payload;

                    List<CurveStep> curveSteps = new ArrayList<>();
                    if (null != panCurveDetail.stepList) {
                        curveSteps.addAll(panCurveDetail.stepList);
                    }
                    //继续创建曲线
                    if (null != panCurveDetail.temperatureCurveParams) {
                        Intent intent = new Intent();
                        intent.setClass(getContext(), CurveCreateActivity.class);
                        intent.putExtra(PanConstant.EXTRA_CURVE_DETAIL, panCurveDetail);
                        startActivity(intent);
                    }
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
        if (id == R.id.ll_quick_fry) {
            if (HomePan.getInstance().isPanOffline()) {
                ToastUtils.showShort(getContext(), R.string.pan_offline);
                return;
            }
            if (!llQuick.isSelected()) {
                //关闭当前模式，持续快炒中
//                llQuick.setSelected(true);
//                tvQuick.setText(R.string.pan_quick_frying);
//                llStir.setSelected(false);
//                tvStir.setText(R.string.pan_stir_fry);
                //持续快炒
                Map map = new HashMap();
                map.put(PanConstant.KEY6, new byte[] {PanConstant.MODE_QUICK_FRY});
                PanAbstractControl.getInstance().setInteractionParams(HomePan.getInstance().guid, map);
            } else {
//                llQuick.setSelected(false);
//                tvQuick.setText(R.string.pan_quick_fry);
                //关闭电机
                Map map = new HashMap();
                map.put(PanConstant.KEY6, new byte[] {PanConstant.MODE_CLOSE_FRY});
                PanAbstractControl.getInstance().setInteractionParams(HomePan.getInstance().guid, map);
            }
        } else if (id == R.id.ll_stir_fry) {
            if (HomePan.getInstance().isPanOffline()) {
                ToastUtils.showShort(getContext(), R.string.pan_offline);
                return;
            }
            if (!llStir.isSelected()) {
//                llStir.setSelected(true);
//                llQuick.setSelected(false);
//                tvQuick.setText(R.string.pan_quick_fry);
//                tvStir.setText(R.string.pan_stir_frying);
                //十秒翻炒
                Map map = new HashMap();
                map.put(PanConstant.KEY6, new byte[] {PanConstant.MODE_STIR_FRY});
                PanAbstractControl.getInstance().setInteractionParams(HomePan.getInstance().guid, map);
            } else {
//                llStir.setSelected(false);
//                tvStir.setText(R.string.pan_stir_fry);
//                tvStir.stop();
                //关闭电机
                Map map = new HashMap();
                map.put(PanConstant.KEY6, new byte[] {PanConstant.MODE_CLOSE_FRY});
                PanAbstractControl.getInstance().setInteractionParams(HomePan.getInstance().guid, map);
            }
        } else if (id == R.id.ll_right_center) {
            if (null == batteryDialog) {
                batteryDialog = PanDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_ELECTRIC_QUANTITY);
                batteryDialog.setCancelable(false);
                batteryDialog.setListeners(new IDialog.DialogOnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, R.id.tv_ok);
            }
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) {
                    Pan pan = (Pan) device;
                    if (pan.battery >= 20)
                        batteryDialog.setContentText("翻炒锅电量" + pan.battery + "%");
                    break;
                }
            }
            batteryDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (null != tvStir)
//            tvStir.stop();
        if (null != batteryDialog && batteryDialog.isShow())
            batteryDialog.dismiss();
    }
}
