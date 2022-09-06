package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.device.Stove;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.utils.LogUtils;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.bean.PanRecipeDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvStep2Adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

//曲线还原,
public class CurveRestoreActivity extends PanBaseActivity {
    private RecyclerView rvStep;
    private TextView tvStop;
    //步骤
    private RvStep2Adapter rvStep2Adapter;
    //当前步骤
    private TextView tvStep;

    //曲线详情
    private PanCurveDetail panCurveDetail;

    private Handler mHandler = new Handler();
    private Runnable runnable;
    private LinearLayoutManager linearLayoutManager;

    private int curStep = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve_restore;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        if (null != getIntent())
            panCurveDetail = (PanCurveDetail) getIntent().getSerializableExtra(PanConstant.EXTRA_CURVE_DETAIL);
        rvStep = findViewById(R.id.rv_step);
        tvStop = findViewById(R.id.tv_stop_cook);
        tvStep = findViewById(R.id.tv_cur_step);
        linearLayoutManager = new LinearLayoutManager(this);
        rvStep.setLayoutManager(linearLayoutManager);
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_30)));
        rvStep2Adapter = new RvStep2Adapter();
        rvStep.setAdapter(rvStep2Adapter);
        //关闭动画,防止闪烁
        ((SimpleItemAnimator)rvStep.getItemAnimator()).setSupportsChangeAnimations(false);
        setOnClickListener(R.id.ll_left, R.id.tv_stop_cook);

    }

    @Override
    protected void initData() {
        if (null != panCurveDetail) {
            Map<Integer, String> params = null;
            try {
                //温度参数
                params = new Gson().fromJson(panCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<Integer, String>>(){}.getType());
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                params = null;
            }
            if (null != panCurveDetail.stepList) {
                ArrayList<CurveStep> curveSteps = new ArrayList<>();
                curveSteps.addAll(panCurveDetail.stepList);
                //处理时长
                int i = 0;
                for (i=0; i<curveSteps.size() - 1; i++) {
                    curveSteps.get(i).needTime = curveSteps.get(i+1).markTime - curveSteps.get(i).markTime;
                }
                //最后一步
                curveSteps.get(i).needTime = panCurveDetail.needTime - curveSteps.get(i).markTime;
                //步骤
                rvStep2Adapter.setList(curveSteps);
                Countdown();
            }
        }
    }
    //启动倒计时
    private void Countdown() {

        runnable = new Runnable() {

            @Override

            public void run() {
                //判断是否结束
                if (curStep >= rvStep2Adapter.getData().size()) {
                    //还原结束
                    //去烹饪结束
                    Intent intent = new Intent();
                    if (null != panCurveDetail) {
                        intent.putExtra(PanConstant.EXTRA_CURVE_DETAIL, panCurveDetail);
                        //关火
                        if (panCurveDetail.stove == Stove.STOVE_LEFT)
                            Stove.getInstance().leftStove.setValue(false);
                        if (panCurveDetail.stove == Stove.STOVE_RIGHT)
                            Stove.getInstance().rightStove.setValue(false);
                    }
                    intent.setClass(CurveRestoreActivity.this, RestoreCompleteActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                CurveStep curveStep = rvStep2Adapter.getData().get(curStep);

                if (curveStep.needTime > 0) {
                    curveStep.elapsedTime++;
                    if (curveStep.elapsedTime == curveStep.needTime) {
                        nextStep();
                    }
                    rvStep2Adapter.notifyItemChanged(curStep);

                }

                mHandler.postDelayed(runnable, 1000L);

            }

        };
        mHandler.postDelayed(runnable, 1000L);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.tv_stop_cook) {
            //停止烹饪
            stopCook();
        }
    }

    //停止烹饪提示
    private void stopCook() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.pan_stop_cook_hint);
        iDialog.setOKText(R.string.pan_stop_cook);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //停止烹饪
                if (v.getId() == R.id.tv_ok) {
                    //关闭炉头
                    if (null != panCurveDetail) {
                        if (panCurveDetail.stove == Stove.STOVE_LEFT)
                            Stove.getInstance().leftStove.setValue(false);
                        if (panCurveDetail.stove == Stove.STOVE_RIGHT)
                            Stove.getInstance().rightStove.setValue(false);
                    }
                    //回首页
                    startActivity(MainActivity.class);
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    //切換步驟
    private void nextStep() {
        curStep++; //下一步
        rvStep2Adapter.setCurStep(curStep);
        //滑动和置顶
        linearLayoutManager.scrollToPositionWithOffset(curStep, 0);
        rvStep2Adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCountDown();
    }

    private void closeCountDown() {
        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}