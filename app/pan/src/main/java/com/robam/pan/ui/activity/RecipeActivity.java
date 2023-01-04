package com.robam.pan.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipe;
import com.robam.common.constant.PanConstant;
import com.robam.pan.device.HomePan;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetRecipesByDeviceRes;
import com.robam.pan.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

//云端菜谱
public class RecipeActivity extends PanBaseActivity {

    private RecyclerView rvRecipe;
    private RvRecipeAdapter rvRecipeAdapter;
    private EditText etSearch;
    private TextView tvEmpty;
    private int pageNo;


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
        // 当数据不满一页时，是否继续自动加载（默认为true）
        rvRecipeAdapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(false);
        rvRecipeAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LogUtils.e("onLoadMore");
                getPanRecipe();
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = etSearch.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        //处理搜索
                        pageNo = 0;
                        rvRecipeAdapter.setNewInstance(new ArrayList<>());
                        searchResult(text);
                    } else {
                        ToastUtils.showShort(RecipeActivity.this, R.string.pan_input_empty);
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

    //获取锅菜谱
    private void getPanRecipe() {
        UserInfo userInfo = AccountInfo.getInstance().getUser().getValue();
        String dp = HomePan.getInstance().getDp();
        if (!TextUtils.isEmpty(dp)) {
            CloudHelper.getRecipesByDevice(this, (userInfo != null) ? userInfo.id : 0, IDeviceType.RZNG, 1 + pageNo * 20, 20, dp, new ArrayList(), GetRecipesByDeviceRes.class,
                    new RetrofitCallback<GetRecipesByDeviceRes>() {
                        @Override
                        public void onSuccess(GetRecipesByDeviceRes getRecipesByDeviceRes) {
                            if (null == getRecipesByDeviceRes || null == getRecipesByDeviceRes.data
                                    || getRecipesByDeviceRes.data.size() < 20) {
                                rvRecipeAdapter.getLoadMoreModule().loadMoreEnd();
                                //关闭加载更多
                                rvRecipeAdapter.getLoadMoreModule().setEnableLoadMore(false);
                            }

                            setData(getRecipesByDeviceRes);
                        }

                        @Override
                        public void onFaild(String err) {
                            setData(null);
                            rvRecipeAdapter.getLoadMoreModule().loadMoreFail();
                        }
                    });
        } else
            setData(null);
    }

    //设置菜谱数据
    private void setData(GetRecipesByDeviceRes getRecipesByDeviceRes) {
        List<PanRecipe> panRecipes = new ArrayList<>();
        if (null != getRecipesByDeviceRes && null != getRecipesByDeviceRes.data
                && getRecipesByDeviceRes.data.size() > 0) {
            //过滤其他设备菜谱
            for (PanRecipe panRecipe: getRecipesByDeviceRes.data) {
                List<PanRecipe.DCS> dcsList = panRecipe.deviceCategoryList;
                if (null != dcsList) {
                    for (PanRecipe.DCS dcs: dcsList) {
                        if (IDeviceType.RZNG.equals(dcs.categoryCode)) {
                            panRecipes.add(panRecipe);
                            break;
                        }
                    }
                }
            }
            pageNo++;
            rvRecipeAdapter.getLoadMoreModule().loadMoreComplete();
        }
        if (panRecipes.size() > 0) {
            rvRecipeAdapter.addData(panRecipes);
            hideEmpty();
        } else if (pageNo == 0)
            showEmpty();
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
        UserInfo userInfo = AccountInfo.getInstance().getUser().getValue();
        String dp = HomePan.getInstance().getDp();

        if (!TextUtils.isEmpty(dp)) {
            CloudHelper.getCookbooksByName(this, dp, true, 1 + pageNo * 20, 20, text, 1, (userInfo != null) ? userInfo.id : 0,
                    GetRecipesByDeviceRes.class, new RetrofitCallback<GetRecipesByDeviceRes>() {

                        @Override
                        public void onSuccess(GetRecipesByDeviceRes getRecipesByDeviceRes) {
                            if (null == getRecipesByDeviceRes || null == getRecipesByDeviceRes.data
                                    || getRecipesByDeviceRes.data.size() < 20) {
                                rvRecipeAdapter.getLoadMoreModule().loadMoreEnd();
                                //关闭加载更多
                                rvRecipeAdapter.getLoadMoreModule().setEnableLoadMore(false);
                            }

                            setData(getRecipesByDeviceRes);
                        }

                        @Override
                        public void onFaild(String err) {
                            setData(null);
                        }
                    });
        } else
            setData(null);
    }
}