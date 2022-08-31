package com.robam.pan.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipe;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.ui.adapter.RvFavoriteAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//烹饪曲线
public class CurveActivity extends PanBaseActivity {
    private RecyclerView rvRecipe;
    private RvFavoriteAdapter rvFavoriteAdapter;
    private TextView tvRight;
    private ImageView ivRight;
    private List<PanRecipe> panRecipeList = new ArrayList<>();
    private TextView tvDelete; //确认删除

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvRight = findViewById(R.id.tv_right);
        //
        tvRight.setText(R.string.pan_delete);
        ivRight = findViewById(R.id.iv_right);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvDelete = findViewById(R.id.tv_delete);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvFavoriteAdapter = new RvFavoriteAdapter();
        rvRecipe.setAdapter(rvFavoriteAdapter);
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.tv_delete);
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
                //删除状态不响应
                if (rvFavoriteAdapter.getStatus() != rvFavoriteAdapter.STATUS_BACK)
                    return;
                if (position == 0) //曲线创作
                    selectStove();
                else {
                    Intent intent = new Intent();
                    intent.setClass(CurveActivity.this, RecipeSelectedActivity.class);
                    intent.putExtra(PanConstant.EXTRA_RECIPE_ID, 15292L);
                    startActivity(intent);
                }
            }
        });
        rvFavoriteAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                //某一条菜删除
                if (view.getId() == R.id.iv_select) {
                    PanRecipe panRecipe = (PanRecipe) adapter.getItem(position);
                    if (rvFavoriteAdapter.getStatus() == rvFavoriteAdapter.STATUS_ALL) {
                        //全选-》删除
                        panRecipe.setSelected(false);
                        ivRight.setImageResource(R.drawable.pan_shape_button_unselected);
                        rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_DELETE);
                    } else if (rvFavoriteAdapter.getStatus() == rvFavoriteAdapter.STATUS_DELETE) {
                        panRecipe.setSelected(!panRecipe.isSelected());
                        //检测是否全选
                        if (isAll()) {
                            ivRight.setImageResource(R.drawable.pan_shape_button_selected);
                            rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_ALL);
                        }
                        else
                            rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_DELETE);
                    }
                }
            }
        });
        if (panRecipeList.size() > 1)
            showRight();
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
        startActivity(CurveCreateActivity.class);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right) {
            if (rvFavoriteAdapter.getStatus() == RvFavoriteAdapter.STATUS_DELETE)
            {
                //删除-》全选
                allSelect();
                ivRight.setImageResource(R.drawable.pan_shape_button_selected);
                rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_ALL);
            } else if (rvFavoriteAdapter.getStatus() == RvFavoriteAdapter.STATUS_ALL){
                //全选-》取消全选
                allUnelect();
                ivRight.setImageResource(R.drawable.pan_shape_button_unselected);
                rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_DELETE);

            } else {
                //返回-》删除
                tvRight.setText(R.string.pan_select_all);
                ivRight.setImageResource(R.drawable.pan_shape_button_unselected);
                panRecipeList.remove(0);
                rvFavoriteAdapter.setList(panRecipeList);
                rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_DELETE);
                tvDelete.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_left) {
            if (rvFavoriteAdapter.getStatus() != RvFavoriteAdapter.STATUS_BACK) {
                //设置非删除状态
                tvRight.setText(R.string.pan_delete);
                ivRight.setImageDrawable(null);
                panRecipeList.add(0, new PanRecipe("创作烹饪曲线", ""));
                rvFavoriteAdapter.setList(panRecipeList);
                allUnelect();
                rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_BACK);
                tvDelete.setVisibility(View.GONE);
            } else
                finish();
        } else if (id == R.id.tv_delete) {
            //确认删除
            delete();
            //设置非删除状态
            if (panRecipeList.size() <= 1)
                hideRight();
            tvRight.setText(R.string.pan_delete);
            ivRight.setImageDrawable(null);
            panRecipeList.add(0, new PanRecipe("创作烹饪曲线", ""));
            rvFavoriteAdapter.setList(panRecipeList);
            allUnelect();
            rvFavoriteAdapter.setStatus(RvFavoriteAdapter.STATUS_BACK);
            tvDelete.setVisibility(View.GONE);
        }
    }

    //全选
    private void allSelect() {
        for (int i=0; i<panRecipeList.size(); i++) {
            panRecipeList.get(i).setSelected(true);
        }
    }
    //取消全选
    private void allUnelect() {
        for (int i=0; i<panRecipeList.size(); i++) {
            panRecipeList.get(i).setSelected(false);
        }
    }
    //检查是否全选
    private boolean isAll() {
        for (int i=0; i<panRecipeList.size(); i++) {
            if (!panRecipeList.get(i).isSelected())
                return false;
        }
        return true;
    }
    //删除
    private void delete() {
        Iterator<PanRecipe> iterator = panRecipeList.iterator();
        while (iterator.hasNext()) {
            PanRecipe panRecipe = iterator.next();
            if (panRecipe.isSelected())
                iterator.remove();
        }
    }
}