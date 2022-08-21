package com.robam.stove.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.StoveRecipe;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.adapter.RvCurveAdapter;

import java.util.ArrayList;
import java.util.List;

//烹饪曲线
public class CurveActivity extends StoveBaseActivity {
    private RecyclerView rvRecipe;
    private RvCurveAdapter rvCurveAdapter;
    private TextView tvRight;
    private List<StoveRecipe> panRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRight();
        tvRight = findViewById(R.id.tv_right);
        //
        tvRight.setText(R.string.stove_delete);
        rvRecipe = findViewById(R.id.rv_recipe);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvCurveAdapter = new RvCurveAdapter();
        rvRecipe.setAdapter(rvCurveAdapter);
        setOnClickListener(R.id.ll_left, R.id.ll_right);
    }

    @Override
    protected void initData() {
        //for test

        panRecipeList.add(new StoveRecipe("创作烹饪曲线", ""));   //第一个固定是添加曲线
        panRecipeList.add(new StoveRecipe("蜜汁烤鸡翅", ""));
        panRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
        panRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
        panRecipeList.add(new StoveRecipe("烤牛排烤牛排烤牛排", ""));
        panRecipeList.add(new StoveRecipe("烤牛排", ""));
        panRecipeList.add(new StoveRecipe("烤牛排", ""));
        panRecipeList.add(new StoveRecipe("烤牛排", ""));
        panRecipeList.add(new StoveRecipe("烤牛排", ""));
        rvCurveAdapter.setList(panRecipeList);
        rvCurveAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //删除状态不响应
                if (rvCurveAdapter.isDelete())
                    return;
                if (position == 0)
                    selectStove();
            }
        });
    }
    //炉头选择
    private void selectStove() {
        //炉头选择提示
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SELECT_STOVE);
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
        startActivity(CurveCreateActivity.class);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right) {
            if (rvCurveAdapter.isDelete())
            {
                //设置全选状态
//                tvRight.setText(R.string.pan_delete);
//                rvFavoriteAdapter.setDelete(false);
//                panRecipeList.add(0, new PanRecipe("创作烹饪曲线", ""));
//                rvFavoriteAdapter.setList(panRecipeList);
            } else {
                //设置删除状态
                tvRight.setText(R.string.stove_select_all);
                rvCurveAdapter.setDelete(true);
                panRecipeList.remove(0);
                rvCurveAdapter.setList(panRecipeList);
            }
        } else if (id == R.id.ll_left) {
            if (rvCurveAdapter.isDelete()) {
                //设置非删除状态
                tvRight.setText(R.string.stove_delete);
                rvCurveAdapter.setDelete(false);
                panRecipeList.add(0, new StoveRecipe("创作烹饪曲线", ""));
                rvCurveAdapter.setList(panRecipeList);
            } else
                finish();
        }
    }

}