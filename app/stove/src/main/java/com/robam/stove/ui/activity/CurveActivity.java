package com.robam.stove.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.StoveRecipe;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.adapter.RvCurveAdapter;
import com.robam.stove.ui.adapter.RvStepAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//烹饪曲线
public class CurveActivity extends StoveBaseActivity {
    private RecyclerView rvRecipe;
    private RvCurveAdapter rvCurveAdapter;
    private TextView tvRight;
    private ImageView ivRight;
    private List<StoveRecipe> stoveRecipeList = new ArrayList<>();
    private TextView tvDelete; //确认删除


    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvRight = findViewById(R.id.tv_right);
        //
        tvRight.setText(R.string.stove_delete);
        ivRight = findViewById(R.id.iv_right);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvDelete = findViewById(R.id.tv_delete);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvCurveAdapter = new RvCurveAdapter();
        rvRecipe.setAdapter(rvCurveAdapter);
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.tv_delete);
    }

    @Override
    protected void initData() {
        //for test

        stoveRecipeList.add(new StoveRecipe("创作烹饪曲线", ""));   //第一个固定是添加曲线
        stoveRecipeList.add(new StoveRecipe("蜜汁烤鸡翅", ""));
        stoveRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
        stoveRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
        stoveRecipeList.add(new StoveRecipe("烤牛排烤牛排烤牛排", ""));
        stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
        stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
        stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
        stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
        rvCurveAdapter.setList(stoveRecipeList);
        rvCurveAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //删除状态不响应
                if (rvCurveAdapter.getStatus() != RvCurveAdapter.STATUS_BACK)
                    return;
                if (position == 0)
                    selectStove();
            }
        });
        rvCurveAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                //某一条菜删除
                if (view.getId() == R.id.iv_select) {
                    StoveRecipe stoveRecipe = (StoveRecipe) adapter.getItem(position);
                    if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL) {
                        //全选-》删除
                        stoveRecipe.setSelected(false);
                        ivRight.setImageResource(R.drawable.stove_shape_button_unselected);
                        rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    } else if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE) {
                        stoveRecipe.setSelected(!stoveRecipe.isSelected());
                        //检测是否全选
                        if (isAll()) {
                            ivRight.setImageResource(R.drawable.stove_shape_button_selected);
                            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
                        }
                        else
                            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    }
                }
            }
        });
        if (stoveRecipeList.size() > 1)
            showRight();
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
            if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE)
            {
                //删除-》全选
//                tvRight.setText(R.string.pan_delete);
//                rvFavoriteAdapter.setDelete(false);
//                panRecipeList.add(0, new PanRecipe("创作烹饪曲线", ""));
//                rvFavoriteAdapter.setList(panRecipeList);
                allSelect();
                ivRight.setImageResource(R.drawable.stove_shape_button_selected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
            } else if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL){
                //全选-》取消全选
                allUnelect();
                ivRight.setImageResource(R.drawable.stove_shape_button_unselected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);

            } else {
                //返回-》删除
                tvRight.setText(R.string.stove_select_all);
                ivRight.setImageResource(R.drawable.stove_shape_button_unselected);
                stoveRecipeList.remove(0);
                rvCurveAdapter.setList(stoveRecipeList);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                tvDelete.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_left) {
            if (rvCurveAdapter.getStatus() != RvCurveAdapter.STATUS_BACK) {
                //设置非删除状态
                tvRight.setText(R.string.stove_delete);
                ivRight.setImageDrawable(null);
                stoveRecipeList.add(0, new StoveRecipe("创作烹饪曲线", ""));
                rvCurveAdapter.setList(stoveRecipeList);
                allUnelect();
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
                tvDelete.setVisibility(View.GONE);
            } else
                finish();
        } else if (id == R.id.tv_delete) {
            //确认删除
            delete();
            //设置非删除状态
            if (stoveRecipeList.size() <= 1)
                hideRight();
            tvRight.setText(R.string.stove_delete);
            ivRight.setImageDrawable(null);
            stoveRecipeList.add(0, new StoveRecipe("创作烹饪曲线", ""));
            rvCurveAdapter.setList(stoveRecipeList);
            allUnelect();
            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
            tvDelete.setVisibility(View.GONE);
        }
    }

    //全选
    private void allSelect() {
        for (int i=0; i<stoveRecipeList.size(); i++) {
            stoveRecipeList.get(i).setSelected(true);
        }
    }
    //取消全选
    private void allUnelect() {
        for (int i=0; i<stoveRecipeList.size(); i++) {
            stoveRecipeList.get(i).setSelected(false);
        }
    }
    //检查是否全选
    private boolean isAll() {
        for (int i=0; i<stoveRecipeList.size(); i++) {
            if (!stoveRecipeList.get(i).isSelected())
                return false;
        }
        return true;
    }
    //删除
    private void delete() {
        Iterator<StoveRecipe> iterator = stoveRecipeList.iterator();
        while (iterator.hasNext()) {
            StoveRecipe stoveRecipe = iterator.next();
            if (stoveRecipe.isSelected())
                iterator.remove();
        }
    }
}