package com.robam.pan.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipe;
import com.robam.pan.ui.adapter.RvFavoriteAdapter;

import java.util.ArrayList;
import java.util.List;

//我的最爱
public class FavoriteActivity extends PanBaseActivity {
    private RecyclerView rvRecipe;
    private RvFavoriteAdapter rvFavoriteAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_favorite;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvRecipe = findViewById(R.id.rv_recipe);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvFavoriteAdapter = new RvFavoriteAdapter();
        rvRecipe.setAdapter(rvFavoriteAdapter);
    }

    @Override
    protected void initData() {
        //test
        List<PanRecipe> panRecipeList = new ArrayList<>();
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("蜜汁烤鸡翅", ""));
        panRecipeList.add(new PanRecipe("脆皮猪肘", ""));
        panRecipeList.add(new PanRecipe("脆皮猪肘", ""));
        panRecipeList.add(new PanRecipe("烤牛排烤牛排烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        panRecipeList.add(new PanRecipe("烤牛排", ""));
        rvFavoriteAdapter.setList(panRecipeList);
    }
}