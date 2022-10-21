package com.robam.pan.ui.activity;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PPanRecipe;
import com.robam.pan.bean.PanRecipe;
import com.robam.common.constant.PanConstant;
import com.robam.pan.device.HomePan;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetPRecipeRes;
import com.robam.pan.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

//我的最爱
public class FavoriteActivity extends PanBaseActivity {
    private RecyclerView rvRecipe;
    //p档菜谱
    private RvRecipeAdapter rvRecipeAdapter;
    private Group group;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_favorite;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        group = findViewById(R.id.pan_group);
        rvRecipe = findViewById(R.id.rv_recipe);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvRecipeAdapter = new RvRecipeAdapter();
        rvRecipe.setAdapter(rvRecipeAdapter);
        rvRecipeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                PanRecipe recipe = (PanRecipe) adapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(FavoriteActivity.this, RecipeSelectedActivity.class);
                intent.putExtra(PanConstant.EXTRA_RECIPE_ID, recipe.id);
//                intent.putExtra(PanConstant.EXTRA_CURVE_ID, recipe.curveId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

        getPotPCookPage();
    }

    //获取我的最爱菜谱
    private void getPotPCookPage() {
        UserInfo info = AccountInfo.getInstance().getUser().getValue();
        //传入设备guid
        CloudHelper.getPotPCookPage(this, HomePan.getInstance().guid, (info != null) ? info.id:0, GetPRecipeRes.class, new RetrofitCallback<GetPRecipeRes>() {
            @Override
            public void onSuccess(GetPRecipeRes getPRecipeRes) {
                setData(getPRecipeRes);
            }

            @Override
            public void onFaild(String err) {
                setData(null);
            }
        });
    }
    //设置p档菜谱列表
    private void setData(GetPRecipeRes getPRecipeRes) {
        if (null != getPRecipeRes && null != getPRecipeRes.datas) {
            List<PanRecipe> panRecipes = new ArrayList<>();
            //转成正常菜谱显示
            for (PPanRecipe pPanRecipe: getPRecipeRes.datas)
                panRecipes.add(new PanRecipe(pPanRecipe.cookbookId, pPanRecipe.cookbookImgCover, pPanRecipe.cookbookName, pPanRecipe.curveCookbookId));
            if (panRecipes.size() > 0) {
                rvRecipeAdapter.setList(panRecipes);
                return;
            }
        }
        group.setVisibility(View.VISIBLE);
        rvRecipe.setVisibility(View.GONE);
    }
}