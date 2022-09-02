package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetCurveDetailRes;
import com.robam.pan.response.GetRecipeDetailRes;
import com.robam.pan.ui.adapter.RvStep3Adapter;

import java.util.ArrayList;
import java.util.List;

//菜谱和曲线选中页面
public class RecipeSelectedActivity extends PanBaseActivity {
    private TextView tvRight;
    //菜谱id
    private long recipeId;
    //曲线id
    private long curveId;
    //步骤
    private RecyclerView rvStep;
    private RvStep3Adapter rvStep3Adapter;
    private TextView tvRecipeName;
    //曲线详情
    private PanCurveDetail panCurveDetail;
    //开始烹饪
    private TextView tvStartCook;


    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        if (null != getIntent()) {
            curveId = getIntent().getLongExtra(PanConstant.EXTRA_CURVE_ID, 0);
            recipeId = getIntent().getLongExtra(PanConstant.EXTRA_RECIPE_ID, 0);
        }

        tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.pan_recipe_detail);
        rvStep = findViewById(R.id.rv_step);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        tvStartCook = findViewById(R.id.tv_start_cook);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_15)));
        rvStep3Adapter = new RvStep3Adapter();
        rvStep.setAdapter(rvStep3Adapter);

        setOnClickListener(R.id.tv_right, R.id.tv_start_cook);
    }

    @Override
    protected void initData() {
        if (curveId != 0)  //获取曲线详情
            getCurveDetail();
        else if (recipeId != 0) {
            showRight();  //显示菜谱详情
            getRecipeDetail();  //先获取菜谱详情
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_right) {
            //菜谱详情
            Intent intent = new Intent();
            intent.setClass(this, RecipeDetailActivity.class);
            intent.putExtra(PanConstant.EXTRA_RECIPE_ID, recipeId);
            startActivity(intent);
        } else if (id == R.id.tv_start_cook) {
            //开始烹饪
            //炉头选择
            //检测锅和灶是否连接
            selectStove();
        }
    }

    //炉头选择
    private void selectStove() {
        //炉头选择提示
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SELECT_STOVE);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.view_left || id == R.id.view_right)
                    openFire();
            }
        }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        iDialog.show();
    }

    //点火提示
    private void openFire() {
//        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
//        iDialog.setCancelable(false);
//        iDialog.show();
        Intent intent = new Intent();
        intent.setClass(this, CurveRestoreActivity.class);
        if (null != panCurveDetail)
            intent.putExtra(PanConstant.EXTRA_CURVE_DETAIL, panCurveDetail);
        startActivity(intent);
    }

    //获取菜谱详情
    private void getRecipeDetail() {
        CloudHelper.getRecipeDetail(this, recipeId, "1", "1", GetRecipeDetailRes.class, new RetrofitCallback<GetRecipeDetailRes>() {
            @Override
            public void onSuccess(GetRecipeDetailRes getRecipeDetailRes) {
                if (null != getRecipeDetailRes && null != getRecipeDetailRes.cookbook) {
                    curveId = getRecipeDetailRes.cookbook.curveCookbookId; //曲线id
                    if (curveId != 0)
                        getCurveDetail(); //获取曲线详情
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    //获取曲线详情
    private void getCurveDetail() {
        CloudHelper.getCurvebookDetail(this, curveId, GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
            @Override
            public void onSuccess(GetCurveDetailRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.payload) {
                    panCurveDetail = getCurveDetailRes.payload;
                    //这里用了曲线名
                    tvRecipeName.setText(panCurveDetail.name);

                    List<CurveStep> curveSteps = new ArrayList<>();
                    if (null != panCurveDetail.stepList) {
                        curveSteps.addAll(panCurveDetail.stepList);
                        tvStartCook.setVisibility(View.VISIBLE);
                    }
                    rvStep3Adapter.setList(curveSteps);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

}