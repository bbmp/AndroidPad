package com.robam.roki.ui.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.HeadPage;
import com.robam.common.ui.UIService;
import com.robam.common.utils.LogUtils;
import com.robam.roki.R;
import com.robam.roki.ui.adapter.RecipeCategoryAdapter;
import com.robam.roki.bean.Recipe;
import com.robam.roki.constant.IDeviceType;
import com.robam.roki.http.CloudHelper;
import com.robam.roki.response.PersonalizedRecipeRes;
import com.robam.roki.ui.view.CustomLoadMoreView;
import com.robam.roki.utils.PageArgumentKey;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryListPage extends HeadPage {
    private String type;
    private ImageView mIvBack;
    private TextView mTvPageName;

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRv;
    int start;
    int num = 10;
    private long mUserId;

    private RecipeCategoryAdapter mRecipeCategoryAdapter;
    private String platformCode;
    private String mGuid;
    protected String title;
    private String recipeType;
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_recipecategory;
    }

    @Override
    protected void initView() {
        setStateBarDrawable(getContext().getDrawable(R.drawable.roki_shape_bg_main));
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString(PageArgumentKey.RecipeId);
            platformCode = bundle.getString(PageArgumentKey.platformCode);
            mGuid = bundle.getString(PageArgumentKey.Guid);
            LogUtils.i("list:::guid:::"+mGuid);
        }
        mIvBack = findViewById(R.id.img_back);
        mTvPageName = findViewById(R.id.tv_page_name);
        mRv = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
    }

    @Override
    protected void initData() {
//        mUserId = Plat.accountService.getCurrentUserId();
        //灶具菜谱
        if (IDeviceType.RRQZ.equals(type)) {
            title = getString(R.string.roki_home_stove_recipe_text);
        } else if (IDeviceType.RDKX.equals(type)) {
            title = getString(R.string.roki_home_oven_recipe_text);
        } else if (IDeviceType.RZQL.equals(type)) {
            title = getString(R.string.roki_home_steam_recipe_text);
        } else if (IDeviceType.RWBL.equals(type)) {
            title = getString(R.string.roki_home_microwave_recipe_text);
        } else if (IDeviceType.RZNG.equals(type)) {
            title = getString(R.string.roki_home_pot_recipe_text);
        } else if (IDeviceType.RZKY.equals(type)) {
            title = getString(R.string.roki_home_combi_steam_oven_recipe_text);
        }
        mTvPageName.setText(title);
        initBgTitle();
        initAdapter();//初始化适配器
        requestData();//初始化数据
        initListener();//监听事件和配置
    }


    private void initBgTitle() {
        if (type.equals(IDeviceType.RZNG)) {
            type = IDeviceType.RRQZ;
            recipeType = "pot";
        } else {
            recipeType = "all";
        }

    }

    private void initAdapter() {
        mRecipeCategoryAdapter = new RecipeCategoryAdapter();
        mRecipeCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int i) {
                Recipe recipe = mRecipeCategoryAdapter.getItem(i);
//                if (mGuid!=null&&mGuid.contains("920")) {
//                    Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
//                    intent.putExtra(
//                            PageArgumentKey.Id,
//                            recipe.id
//
//                    );
//                    intent.putExtra(PageArgumentKey.Guid,mGuid);
//                    startActivity(intent);
//                }else {
//                    RecipeDetailPage.show(recipe, recipe.id, RecipeDetailPage.DeviceRecipePage,
//                            RecipeRequestIdentification.RECIPE_SORTED, platformCode, mGuid);
//                }
            }
        });
        mRecipeCategoryAdapter.addChildClickViewIds(R.id.iv_collection);
        mRecipeCategoryAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter baseQuickAdapter, @NonNull View view, int i) {
                if (view.getId() == R.id.iv_collection) {
//                    boolean isLogin = Plat.accountService.isLogon();
//                    if (isLogin) {
//                        Recipe recipe = mRecipeCategoryAdapter.getItem(i);
//                        if (recipe != null) {
//                            CookbookManager cm = CookbookManager.getInstance();
//
//                            if (recipe.collected) {
//                                cm.deleteFavorityCookbooks(recipe.id, new VoidCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        recipe.setIsCollected(false);
//                                        mRecipeCategoryAdapter.notifyItemChanged(i);
//                                        ToastUtils.showShort("已取消收藏");
//                                    }
//
//                                    @Override
//                                    public void onFailure(Throwable t) {
//                                        ToastUtils.showShort(t.getMessage());
//                                    }
//                                });
//                            } else {
//                                cm.addFavorityCookbooks(recipe.id, new VoidCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        recipe.setIsCollected(true);
//                                        mRecipeCategoryAdapter.notifyItemChanged(i);
//                                        ToastUtils.showShort("收藏成功");
//                                    }
//
//                                    @Override
//                                    public void onFailure(Throwable t) {
//                                        ToastUtils.showShort(t.getMessage());
//                                    }
//                                });
//
//                            }
//                        }
//                    } else {
//                        CmccLoginHelper.getInstance().toLogin();
//                    }
                }
            }
        });


        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mRecipeCategoryAdapter);
    }

    private void requestData() {
        LogUtils.i("mUserId:::"+mUserId);
        LogUtils.i("type:::"+type);
        LogUtils.i("recipeType:::"+recipeType);
        LogUtils.i("start:::"+start);
        LogUtils.i("num:::"+num);
        LogUtils.i("platformCode:::"+platformCode);
        CloudHelper.getGroundingRecipesByDc(this, mUserId, type, recipeType, start, num, platformCode, PersonalizedRecipeRes.class,
                new RetrofitCallback<PersonalizedRecipeRes>() {
                    @Override
                    public void onSuccess(PersonalizedRecipeRes personalizedRecipeRes) {
                        List<Recipe> list = personalizedRecipeRes.cookbooks;
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            LogUtils.i("list::"+list);

                            if (list == null || list.size() <= 0) {
                                if (start > 0) {
                                    mRecipeCategoryAdapter.getLoadMoreModule().loadMoreEnd();

                                    return;
                                }
                            }

                            if (start == 0) {
                                mRecipeList.clear();
                            }
                            mRecipeList.addAll(list);
                            mRecipeCategoryAdapter.setList(mRecipeList);
                            mRecipeCategoryAdapter.getLoadMoreModule().loadMoreComplete();

                        } catch (Exception e) {
                            Log.e("RecipeCategory", "error:" + e.getMessage());
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        swipeRefreshLayout.setRefreshing(false);
                        mRecipeCategoryAdapter.getLoadMoreModule().loadMoreFail();
                    }
        });
    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start = 0 ;
                //请求数据
                requestData();
            }
        });
        mRecipeCategoryAdapter.getLoadMoreModule().setLoadMoreView(new CustomLoadMoreView());
        mRecipeCategoryAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LogUtils.i(" onLoadMore");
                start += 10 ;
//                num += 10;
                requestData();
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIService.popBack(v);

            }
        });
    }
}
