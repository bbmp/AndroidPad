package com.robam.steamoven.manager;

import android.content.Context;

import com.robam.common.utils.MMKVUtils;
import com.robam.steamoven.bean.ModeBean;

import org.litepal.LitePal;

import java.util.List;

/**
 * <pre>
 *     author : huxw
 *     e-mail : xwhu93@163.com
 *     time   : 2022/05/12
 *     desc   : 数据初始化
 *     version: 1.0
 * </pre>
 */
public class DataInitManage {

    public static void savaRecipe(Context context){
//        BaseDialog mWaitDialog = new WaitDialog.Builder(context)
//                .setMessage("数据导入中...")
//                .create();
//        mWaitDialog.show();
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                List<RecipeClassifyMode> recipeClassBeans = FuntionModeManage.getRecipeClassBean(context, "recipe_classify");
//                LitePal.saveAll(recipeClassBeans);
//                //本地P档菜谱
//                List<PRecipe> recipe = FuntionModeManage.getPRecipe(context, "recipe2");
//                for (PRecipe pRecipe : recipe) {
//                    pRecipe.save();
//                    for (RecipeWork recipeWork : pRecipe.recipe_work) {
//                        recipeWork.pRecipe = pRecipe ;
//                        recipeWork.save();
//                        for (RecipeWorkMode recipeWorkMode : recipeWork.recipe_work_mode) {
//                            recipeWorkMode.recipeWork = recipeWork ;
//                            recipeWorkMode.save();
//                        }
//                    }
//                }
//                //菜谱详情
//                List<RecipeDetail> recipedetail = FuntionModeManage.getRecipeDetails(context, "recipedetail");
//                for (RecipeDetail recipeDetail : recipedetail) {
//                    recipeDetail.save();
//                    if (recipeDetail.step != null && recipeDetail.step.size() != 0){
//                        for (RecipeStep recipe_step : recipeDetail.step) {
//                            recipe_step.recipeDetail = recipeDetail ;
//                            recipe_step.save();
//                        }
//                    }
//
//                }

//                List<ModeBean> mode = FuntionModeManage.getMode(context, "steammode");
//                assert mode != null;
//                LitePal.saveAll(mode);
//                mWaitDialog.dismiss();
                MMKVUtils.initData(true);
            }
        });

    }
}
