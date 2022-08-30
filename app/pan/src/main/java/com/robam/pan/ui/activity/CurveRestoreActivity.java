package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.pan.bean.RecipeStep;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvStep2Adapter;
import com.robam.pan.ui.adapter.RvStepAdapter;

import java.util.ArrayList;

//曲线还原,
public class CurveRestoreActivity extends PanBaseActivity {
    private RecyclerView rvStep;
    private TextView tvStop;
    //步骤
    private RvStep2Adapter rvStep2Adapter;
    //当前步骤
    private TextView tvStep;

    //步骤
    private ArrayList<RecipeStep> recipeSteps;

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
            recipeSteps = (ArrayList<RecipeStep>) getIntent().getSerializableExtra(PanConstant.EXTRA_RECIPE_STEP);
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
        if (null != recipeSteps) {

            //步骤
            rvStep2Adapter.setList(recipeSteps);
            Countdown();
        }
    }
    //启动倒计时
    private void Countdown() {

        runnable = new Runnable() {

            @Override

            public void run() {

                if (curStep >= rvStep2Adapter.getData().size()) {
                    //工作结束
                    //去烹饪结束
                    startActivity(CurveSaveActivity.class);
                    finish();
                    return;
                }
                RecipeStep recipeStep = rvStep2Adapter.getData().get(curStep);

                if (recipeStep.needTime > 0) {
                    recipeStep.elapsedTime++;
                    if (recipeStep.elapsedTime == recipeStep.needTime) {
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