package com.robam.steamoven.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.constant.StoveConstant;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBasePage;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.bean.model.SteamRecipe;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.ui.activity.RecipeDetailActivity;
import com.robam.steamoven.ui.activity.RecipeModeActivity;
import com.robam.steamoven.ui.activity.RecipeModeActivityNew;
import com.robam.steamoven.ui.adapter.RvRecipeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeClassifyPage extends SteamBasePage {
    private RecyclerView rvRecipe;
    private RvRecipeAdapter rvRecipeAdapter;

    private List<SteamRecipe> stoveRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.steam_page_layout_recipe_classify;
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
        convertData(bundle.getParcelableArrayList(Constant.RECIPE_LIST_FLAG));
        rvRecipeAdapter.setList(stoveRecipeList);
        rvRecipeAdapter.setOnItemClickListener((adapter, view, position) -> {
            try {
                //startActivity(RecipeSelectedActivity.class);
                SteamRecipe stoveRecipe = (SteamRecipe) adapter.getItem(position);
                //Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
                Intent intent = new Intent(getContext(), RecipeModeActivityNew.class);
                intent.putExtra(StoveConstant.EXTRA_RECIPE_ID, stoveRecipe.id);
                intent.putExtra(SteamConstant.EXTRA_MODE_LIST,getModeBeans(stoveRecipe));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }

    private ArrayList<ModeBean> getModeBeans(SteamRecipe stoveRecipe) throws JSONException {
        JSONObject jsonObject = new JSONObject(stoveRecipe.functionParams);
        JSONObject modeObj = jsonObject.optJSONObject("model");
        int code = Integer.parseInt(modeObj.getString("value"));
        JSONObject timeDefObj = jsonObject.optJSONObject("setTimeDef");
        int defTime = Integer.parseInt(timeDefObj.getString("value"));
        JSONObject timeRuleObj = jsonObject.optJSONObject("setTime");
        JSONArray ruleArray = timeRuleObj.getJSONArray("value");
        ModeBean modeBean = new ModeBean();
        modeBean.code = code;
        modeBean.defTime = defTime;
        modeBean.minTime = ruleArray.getInt(0);
        modeBean.maxTime = ruleArray.getInt(1);

        ArrayList<ModeBean> modeBeanList = new ArrayList<>();
        modeBeanList.add(modeBean);
        return modeBeanList;
    }


    private void convertData(List<DeviceConfigurationFunctions>  recipeList){
        if(recipeList == null || recipeList.size() == 0){
            return;
        }
        for(DeviceConfigurationFunctions functions:recipeList){
            SteamRecipe  recipe = new SteamRecipe(functions.id,functions.functionName,functions.backgroundImg,functions.functionParams);
            stoveRecipeList.add(recipe);
        }
    }
}
