package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipeDetail;
import com.robam.pan.bean.RecipeStep;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetRecipeDetailRes;
import com.robam.pan.ui.adapter.RvStepAdapter;

import java.util.ArrayList;
import java.util.List;

//菜谱选中页面
public class RecipeSelectedActivity extends PanBaseActivity {
    private TextView tvRight;
    //菜谱id
    private long recipeId;
    //步骤
    private RecyclerView rvStep;
    private RvStepAdapter rvStepAdapter;
    private TextView tvRecipeName;
    //菜谱步骤
    private ArrayList<RecipeStep> recipeSteps = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRight();

        if (null != getIntent())
            recipeId = getIntent().getLongExtra(PanConstant.EXTRA_RECIPE_ID, 0);

        tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.pan_recipe_detail);
        rvStep = findViewById(R.id.rv_step);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStep.addItemDecoration(new HorizontalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_40)));
        rvStepAdapter = new RvStepAdapter();
        rvStep.setAdapter(rvStepAdapter);

        setOnClickListener(R.id.tv_right, R.id.tv_start_cook);
    }

    @Override
    protected void initData() {
        getRecipeDetail();
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
        intent.putExtra(PanConstant.EXTRA_RECIPE_STEP, recipeSteps);
        startActivity(intent);
    }

    //获取菜谱详情
    private void getRecipeDetail() {
        CloudHelper.getRecipeDetail(this, recipeId, "1", "1", GetRecipeDetailRes.class, new RetrofitCallback<GetRecipeDetailRes>() {
            @Override
            public void onSuccess(GetRecipeDetailRes getRecipeDetailRes) {
                if (null != getRecipeDetailRes && null != getRecipeDetailRes.cookbook)
                    setData(getRecipeDetailRes.cookbook);
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    private void setData(PanRecipeDetail panRecipeDetail) {
        //名字
        tvRecipeName.setText(panRecipeDetail.name);
        //步骤
        if (null != panRecipeDetail.steps) {
            recipeSteps.clear();
            recipeSteps.addAll(panRecipeDetail.steps);
        }
        rvStepAdapter.setList(recipeSteps);
    }
}