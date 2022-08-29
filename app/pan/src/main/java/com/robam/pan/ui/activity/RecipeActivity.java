package com.robam.pan.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipe;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetRecipesByDeviceRes;
import com.robam.pan.ui.adapter.RvFavoriteAdapter;
import com.robam.pan.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

//云端菜谱
public class RecipeActivity extends PanBaseActivity {

    private RecyclerView rvRecipe;
    private RvRecipeAdapter rvRecipeAdapter;
    private EditText etSearch;
    private TextView tvEmpty;

    private List<PanRecipe> panRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        etSearch = findViewById(R.id.et_search);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvEmpty = findViewById(R.id.tv_empty_hint);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvRecipeAdapter = new RvRecipeAdapter();
        rvRecipe.setAdapter(rvRecipeAdapter);
        rvRecipeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                PanRecipe recipe = (PanRecipe) adapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(RecipeActivity.this, RecipeSelectedActivity.class);
                intent.putExtra(PanConstant.EXTRA_RECIPE_ID, recipe.id);
                startActivity(intent);
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = etSearch.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        //处理搜索
                        searchResult(text);
                    } else {
                        rvRecipeAdapter.setList(panRecipeList);
                        hideEmpty();
                    }
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
       getPanRecipe();
    }

    //获取灶具菜谱
    private void getPanRecipe() {
        CloudHelper.getRecipesByDevice(this, "RZNG", "all", 1, 20, GetRecipesByDeviceRes.class,
                new RetrofitCallback<GetRecipesByDeviceRes>() {
                    @Override
                    public void onSuccess(GetRecipesByDeviceRes getRecipesByDeviceRes) {
                        if (null != getRecipesByDeviceRes && getRecipesByDeviceRes.cookbooks != null) {
                            panRecipeList.addAll(getRecipesByDeviceRes.cookbooks);
                            rvRecipeAdapter.setList(panRecipeList);
                            hideEmpty();
                        } else
                            showEmpty();
                    }

                    @Override
                    public void onFaild(String err) {
                        showEmpty();
                    }
                });
    }

    //获取不到数据
    private void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
        rvRecipe.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        tvEmpty.setVisibility(View.GONE);
        rvRecipe.setVisibility(View.VISIBLE);
    }
    //搜索结果
    private void searchResult(String text) {
        List<PanRecipe> recipeList = new ArrayList<>();
        for (int i=0; i<panRecipeList.size(); i++) {
            if (panRecipeList.get(i).getName().contains(text))
                recipeList.add(panRecipeList.get(i));
        }
        rvRecipeAdapter.setList(recipeList);
        if (recipeList.size() == 0)
            showEmpty();
        else
            hideEmpty();
    }
}