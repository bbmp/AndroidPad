package com.robam.pan.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipe;
import com.robam.pan.ui.adapter.RvFavoriteAdapter;
import com.robam.pan.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

//云端菜谱
public class RecipeActivity extends PanBaseActivity {

    private RecyclerView rvRecipe;
    private RvRecipeAdapter rvRecipeAdapter;
    private List<PanRecipe> panRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvRecipe = findViewById(R.id.rv_recipe);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvRecipeAdapter = new RvRecipeAdapter();
        rvRecipe.setAdapter(rvRecipeAdapter);
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
        rvRecipeAdapter.setList(panRecipeList);
    }
}