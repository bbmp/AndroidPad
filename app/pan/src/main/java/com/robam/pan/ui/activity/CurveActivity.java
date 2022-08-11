package com.robam.pan.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.Constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipe;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvFavoriteAdapter;
import com.robam.pan.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class CurveActivity extends PanBaseActivity {
    private RecyclerView rvRecipe;
    private RvFavoriteAdapter rvFavoriteAdapter;
    private TextView tvRight;
    private List<PanRecipe> panRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRight();
        tvRight = findViewById(R.id.tv_right);
        //
        tvRight.setText(R.string.pan_delete);
        rvRecipe = findViewById(R.id.rv_recipe);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvFavoriteAdapter = new RvFavoriteAdapter();
        rvRecipe.setAdapter(rvFavoriteAdapter);
        setOnClickListener(R.id.ll_left, R.id.ll_right);
    }

    @Override
    protected void initData() {
//test

        panRecipeList.add(new PanRecipe("创作烹饪曲线", ""));   //第一个固定是添加曲线
        panRecipeList.add(new PanRecipe("蜜汁烤鸡翅", ""));
        panRecipeList.add(new PanRecipe("脆皮猪肘", ""));
        panRecipeList.add(new PanRecipe("脆皮猪肘", ""));
        panRecipeList.add(new PanRecipe("烤牛排烤牛排烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        rvFavoriteAdapter.setList(panRecipeList);
        rvFavoriteAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //删除状态不响应
                if (rvFavoriteAdapter.isDelete())
                    return;
                if (position == 0)
                    selectStove();
            }
        });
    }

    private void selectStove() {
        //炉头选择提示
        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SELECT_STOVE);
        iDialog.setCancelable(false);
        iDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right) {
            if (rvFavoriteAdapter.isDelete())
            {
                //设置全选状态
//                tvRight.setText(R.string.pan_delete);
//                rvFavoriteAdapter.setDelete(false);
//                panRecipeList.add(0, new PanRecipe("创作烹饪曲线", ""));
//                rvFavoriteAdapter.setList(panRecipeList);
            } else {
                //设置删除状态
                tvRight.setText(R.string.pan_select_all);
                rvFavoriteAdapter.setDelete(true);
                panRecipeList.remove(0);
                rvFavoriteAdapter.setList(panRecipeList);
            }
        } else if (id == R.id.ll_left) {
            if (rvFavoriteAdapter.isDelete()) {
                //设置非删除状态
                tvRight.setText(R.string.pan_delete);
                rvFavoriteAdapter.setDelete(false);
                panRecipeList.add(0, new PanRecipe("创作烹饪曲线", ""));
                rvFavoriteAdapter.setList(panRecipeList);
            } else
                finish();
        }
    }
}