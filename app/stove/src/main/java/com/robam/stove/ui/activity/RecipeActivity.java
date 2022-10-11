package com.robam.stove.ui.activity;

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
import com.robam.common.IDeviceType;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.ToastUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.StoveRecipe;
import com.robam.common.constant.StoveConstant;
import com.robam.stove.http.CloudHelper;
import com.robam.stove.response.GetRecipesByDeviceRes;
import com.robam.stove.ui.adapter.RvRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

//灶具菜谱
public class RecipeActivity extends StoveBaseActivity {
    private RecyclerView rvRecipe;
    private RvRecipeAdapter rvRecipeAdapter;

    private EditText etSearch;
    private TextView tvEmpty;

    private TextView btnConnect;
    //分类
//    private List<String> classifyList = new ArrayList<>();
    //弱引用，防止内存泄漏
//    private List<WeakReference<Fragment>> fragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_recipe;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();

        etSearch = findViewById(R.id.et_search);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvEmpty = findViewById(R.id.tv_empty_hint);
        btnConnect = findViewById(R.id.btn_connect);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvRecipeAdapter = new RvRecipeAdapter();
        rvRecipe.setAdapter(rvRecipeAdapter);

        rvRecipeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                StoveRecipe stoveRecipe = (StoveRecipe) adapter.getItem(position);
                Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
                intent.putExtra(StoveConstant.EXTRA_RECIPE_ID, stoveRecipe.id);
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
                        ToastUtils.showShort(RecipeActivity.this, R.string.stove_input_empty);
                    }
                    return false;
                }
                return false;
            }
        });
        setOnClickListener(R.id.btn_connect);
    }

    @Override
    protected void initData() {
       getStoveRecipe();

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_connect) {
            //去连网
            Intent intent = new Intent();
            intent.setClassName(this, "com.robam.ventilator.ui.activity.WifiSettingActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left) { //返回
            finish();
        }
    }
    //获取灶具菜谱
    private void getStoveRecipe() {
        CloudHelper.getRecipesByDevice(this, IDeviceType.RRQZ, "all", 1, 20, GetRecipesByDeviceRes.class,
                new RetrofitCallback<GetRecipesByDeviceRes>() {
                    @Override
                    public void onSuccess(GetRecipesByDeviceRes getRecipesByDeviceRes) {

                        setData(getRecipesByDeviceRes);
                    }

                    @Override
                    public void onFaild(String err) {
                        setData(null);
                    }
                });
    }

    //设置菜谱列表
    private void setData(GetRecipesByDeviceRes getRecipesByDeviceRes) {
        List<StoveRecipe> stoveRecipes = new ArrayList<>();
        if (null != getRecipesByDeviceRes && null != getRecipesByDeviceRes.cookbooks) {
            //过滤其他设备菜谱
            for (StoveRecipe stoveRecipe: getRecipesByDeviceRes.cookbooks) {
                List<StoveRecipe.DCS> dcsList = stoveRecipe.dcs;
                if (null != dcsList) {
                    for (StoveRecipe.DCS dcs: dcsList) {
                        if (IDeviceType.RRQZ.equals(dcs.dc)) {
                            stoveRecipes.add(stoveRecipe);
                            break;
                        }
                    }
                }
            }
        }
        if (stoveRecipes.size() > 0) {
            rvRecipeAdapter.setList(stoveRecipes);
            hideEmpty();
            btnConnect.setVisibility(View.GONE);
        }
        else
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
        CloudHelper.getCookbooksByName(this, text, false, 0L, false, true,
                GetRecipesByDeviceRes.class, new RetrofitCallback<GetRecipesByDeviceRes>() {

                    @Override
                    public void onSuccess(GetRecipesByDeviceRes getRecipesByDeviceRes) {

                        setData(getRecipesByDeviceRes);
                    }

                    @Override
                    public void onFaild(String err) {
                        setData(null);
                    }
                });

    }
}