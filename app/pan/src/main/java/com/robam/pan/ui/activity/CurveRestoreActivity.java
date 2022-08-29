package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.pan.bean.RecipeStep;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvStepAdapter;

import java.util.ArrayList;

//曲线还原,
public class CurveRestoreActivity extends PanBaseActivity {
    private RecyclerView rvStep;
    private TextView tvStop;
    //步骤
    private RvStepAdapter rvStepAdapter;
    //当前步骤
    private TextView tvStep;
    //倒计时
    private MCountdownView tvCountdown;
    //步骤
    private ArrayList<RecipeStep> recipeSteps;

    private ProgressBar pbTime;

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
        tvCountdown = findViewById(R.id.tv_countdown);
        pbTime = findViewById(R.id.pb_time);
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        setOnClickListener(R.id.ll_left, R.id.tv_stop_cook);
        rvStepAdapter = new RvStepAdapter();
        rvStep.setAdapter(rvStepAdapter);
    }

    @Override
    protected void initData() {
        if (null != recipeSteps) {
            //步骤图片不显示
            for (int i=0; i<recipeSteps.size(); i++)
                recipeSteps.get(i).hideImage = true;
            tvStep.setText(String.format(getResources().getString(R.string.pan_cur_step), curStep+1+"", recipeSteps.size()+""));
            rvStepAdapter.setList(recipeSteps);

            setCountDownTime(recipeSteps.get(curStep).needTime);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left || id == R.id.tv_stop_cook) {
            //停止烹饪
            stopCook();
        }
    }
    /**
     * 设置倒计时
     */
    private void setCountDownTime(int totalTime) {

        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
            @Override
            public void onCountDown(int currentSecond) {
                String time = DateUtil.secForMatTime(currentSecond);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCountdown.setText(time);
                        pbTime.setProgress((totalTime-currentSecond)*100/ totalTime);
                        if (currentSecond <= 0)
                            ;
                    }
                });
            }
        });
        tvCountdown.start();
    }

    //停止烹饪提示
    private void stopCook() {
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_PAN_COMMON);
        iDialog.setCancelable(false);
        iDialog.setContentText(R.string.pan_stop_creation_hint);
        iDialog.setCancelText(R.string.pan_stop_creation);
        iDialog.setOKText(R.string.pan_continue_creation);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //结束创作
                if (v.getId() == R.id.tv_cancel) {
                    finish();
                }
            }
        }, R.id.tv_cancel, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != tvCountdown)
            tvCountdown.stop();
    }
}