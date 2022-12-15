package com.robam.stove.ui.activity;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.robam.common.module.IPublicStoveApi;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.WindowsUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.RecipeStep;
import com.robam.stove.bean.StepParams;
import com.robam.stove.bean.StoveRecipeDetail;
import com.robam.stove.constant.DialogConstant;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.adapter.RvStep2Adapter;

import java.util.ArrayList;

//开始烹饪界面
public class RecipeCookActivity extends StoveBaseActivity {
    private RecyclerView rvStep;
    //步骤
    private RvStep2Adapter rvStep2Adapter;
    //详情
    private StoveRecipeDetail stoveRecipeDetail;
    //菜谱图片
    private ImageView ivRecipe;
    //炉头id
    private int stoveId;

    private IDialog stopDialog, completeDialog;
    //
    private TextView tvFire, tvAir;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    private LinearLayoutManager linearLayoutManager;

    //暂停烹饪， 继续
    private TextView tvPause, tvContinue, tvSwitch;
    private int curStep = 0;

    private long mLastTime;
    private long mCurTime;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_recipe_cook;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        if (null != getIntent()) {
            stoveId = getIntent().getIntExtra(StoveConstant.stoveId, IPublicStoveApi.STOVE_LEFT);
            stoveRecipeDetail = (StoveRecipeDetail) getIntent().getSerializableExtra(StoveConstant.EXTRA_RECIPE_DETAIL);
        }
        rvStep = findViewById(R.id.rv_step);
        ivRecipe = findViewById(R.id.iv_recipe);
        tvFire = findViewById(R.id.tv_fire);
        tvAir = findViewById(R.id.tv_air);
        tvPause = findViewById(R.id.tv_pause_cook);
        tvContinue = findViewById(R.id.tv_continue_cook);
        tvSwitch = findViewById(R.id.tv_switch_step);
        linearLayoutManager = new LinearLayoutManager(this);
        rvStep.setLayoutManager(linearLayoutManager);
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_30)));
        rvStep2Adapter = new RvStep2Adapter();
        rvStep.setAdapter(rvStep2Adapter);
        //关闭动画
        ((SimpleItemAnimator)rvStep.getItemAnimator()).setSupportsChangeAnimations(false);
        setOnClickListener(R.id.ll_left, R.id.tv_pause_cook, R.id.tv_continue_cook);
        //双击事件
        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                if (mCurTime - mLastTime < 500) {
                    //切换步骤
                    nextStep();
                }
            }
        });
    }

    @Override
    protected void initData() {
        if (null != stoveRecipeDetail) {
            //
            tvSwitch.setVisibility(View.VISIBLE);
            tvPause.setVisibility(View.VISIBLE);

            //步骤
            ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
            if (null != stoveRecipeDetail.steps) {
                recipeSteps.addAll(stoveRecipeDetail.steps);
                setData();
            }
            rvStep2Adapter.setList(recipeSteps);
            Countdown();
        }
    }
    //设置烟机 风量和火力
    private void setData() {
        if (curStep >= stoveRecipeDetail.steps.size())
            return;
        RecipeStep recipeStep = stoveRecipeDetail.steps.get(curStep);
        //图片
        ImageUtils.loadImage(this, recipeStep.image, ivRecipe);
        if (null != recipeStep.params && recipeStep.params.size() > 0) {
            for (StepParams params: recipeStep.params) {
                if (HomeStove.getInstance().getDp().equals(params.platCode)) {

                    if (null != params.params) {
                        for (int i = 0; i < params.params.size(); i++) {
                            if (params.params.get(i).code.equals("fanGear")) {//烟机风量
                                tvAir.setText("风量：" + params.params.get(i).valueName);
                                //设置烟机风量
                                IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                                if (null != iPublicVentilatorApi) {
                                    if (params.params.get(i).value >= 1 && params.params.get(i).value <= 3)
                                        iPublicVentilatorApi.setFanGear(1); //弱档
                                    else if (params.params.get(i).value >= 4 && params.params.get(i).value <= 6)
                                        iPublicVentilatorApi.setFanGear(3); //强档
                                    else if (params.params.get(i).value >= 7 && params.params.get(i).value <= 9)
                                        iPublicVentilatorApi.setFanGear(6); //强档
                                }
                            }
                            if (params.params.get(i).code.equals("stoveGear")) {//炉头
                                tvFire.setText("火力：" + params.params.get(i).valueName);
                                //设置灶具挡位
                                StoveAbstractControl.getInstance().setLevel(HomeStove.getInstance().guid, stoveId, 0x01, params.params.get(i).value, (int) stoveRecipeDetail.id, recipeStep.no);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_pause_cook) {
            //暂停烹饪
            //暂停
            closeCountDown();
            tvPause.setVisibility(View.GONE);
            tvContinue.setVisibility(View.VISIBLE);
            //灶具挡位调最小
            StoveAbstractControl.getInstance().setLevel(HomeStove.getInstance().guid, stoveId, 0x01, 0x01, 0, 0);
        } else if (id == R.id.tv_continue_cook) {
            //继续烹饪
            Countdown();
            tvPause.setVisibility(View.VISIBLE);
            tvContinue.setVisibility(View.GONE);
            //设置灶具挡位
            if (curStep >= stoveRecipeDetail.steps.size())
                return;
            RecipeStep recipeStep = stoveRecipeDetail.steps.get(curStep);

            if (null != recipeStep.params && recipeStep.params.size() > 0) {
                StepParams params = recipeStep.params.get(0);//取首個
                if (null != params.params) {
                    for (int i = 0; i<params.params.size(); i++) {
                        if (params.params.get(i).code.equals("stoveGear")) {//炉头
                            tvFire.setText("火力：" + params.params.get(i).valueName);
                            //设置灶具挡位
                            StoveAbstractControl.getInstance().setLevel(HomeStove.getInstance().guid, (byte) stoveId, (byte) 0x01, (byte) params.params.get(i).value, (int) stoveRecipeDetail.id, recipeStep.no);
                        }
                    }
                }
            }

        } else if (id == R.id.ll_left) {
            //返回
            stopCook();
        }
    }
    //结束烹饪提示
    private void stopCook() {
        if (null == stopDialog) {
            stopDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            stopDialog.setCancelable(false);
            stopDialog.setOKText(R.string.stove_ok_stop);
            stopDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        //关火
                        StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, (byte) stoveId, (byte) 0x01, (byte) StoveConstant.STOVE_CLOSE);
                        finish();
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        stopDialog.show();
    }

    private void Countdown() {

        runnable = new Runnable() {

            @Override

            public void run() {
                //检查灶具是否离线

                if (curStep >= rvStep2Adapter.getData().size()) {
                    //工作结束
                    //提示烹饪完成
                    workComplete();
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
    //烹饪正常结束提示
    private void workComplete() {
        //关火
        StoveAbstractControl.getInstance().setAttribute(HomeStove.getInstance().guid, (byte) stoveId, (byte) 0x01, (byte) StoveConstant.STOVE_CLOSE);

        if (null == completeDialog) {
            completeDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMPLETE);
            completeDialog.setCancelable(false);
            completeDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    //回首页
                    startActivity(MainActivity.class);
                }
            }, R.id.tv_ok);
        }
        completeDialog.show();
    }

    //切換步驟
    private void nextStep() {
        curStep++; //下一步
        rvStep2Adapter.setCurStep(curStep);
        linearLayoutManager.scrollToPositionWithOffset(curStep, 0);
        rvStep2Adapter.notifyDataSetChanged();

        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        WindowsUtils.hidePopupWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        WindowsUtils.showPopupWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCountDown();

        if (null != stopDialog && stopDialog.isShow())
            stopDialog.dismiss();
        if (null != completeDialog && completeDialog.isShow())
            completeDialog.dismiss();
    }

    private void closeCountDown() {
        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}
