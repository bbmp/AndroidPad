package com.robam.stove.ui.pages;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.bean.StoveRecipe;
import com.robam.stove.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecipeClassifyPage extends StoveBasePage {
    private RecyclerView rvRecipe;
    private RvRecipeAdapter rvRecipeAdapter;

    private List<StoveRecipe> stoveRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.stove_page_layout_recipe_classify;
    }

    @Override
    protected void initView() {
        rvRecipe = findViewById(R.id.rv_recipe);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvRecipeAdapter = new RvRecipeAdapter();
        rvRecipe.setAdapter(rvRecipeAdapter);
    }

    @Override
    protected void initData() {
        //for test
        Bundle bundle = getArguments();
        int classify = bundle.getInt("classify");
        if (classify == 0) {
            stoveRecipeList.add(new StoveRecipe("蜜汁烤鸡翅", ""));
            stoveRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
            stoveRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
            stoveRecipeList.add(new StoveRecipe("烤牛排烤牛排烤牛排", ""));
            stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
            stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
            stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
            stoveRecipeList.add(new StoveRecipe("烤牛排", ""));
        } else {
            stoveRecipeList.add(new StoveRecipe("蜜汁烤鸡翅", ""));
            stoveRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
            stoveRecipeList.add(new StoveRecipe("脆皮猪肘", ""));
            stoveRecipeList.add(new StoveRecipe("烤牛排烤牛排烤牛排", ""));
        }
        rvRecipeAdapter.setList(stoveRecipeList);

        rvRecipeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                startActivity(RecipeSelectedActivity.class);
            }
        });
    }
}
