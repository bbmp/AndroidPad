package com.robam.steamoven.ui.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.OtherFunc;
import com.robam.steamoven.bean.SubViewModelMap;
import com.robam.steamoven.bean.SubViewModelMapSubView;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.request.GetCurveDetailReq;
import com.robam.steamoven.response.GetDeviceParamsRes;
import com.robam.steamoven.ui.pages.RecipeClassifyPage;
import com.robam.steamoven.utils.SteamDataUtil;

import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//一体机菜谱
public class RecipeActivity extends SteamBaseActivity {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    private EditText etSearch;

    private View searchPromptView;
    private TextView searchPromptBtn;
    //分类
    private List<String> classifyList = new ArrayList<>();
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();
    //菜谱分类
    private List<DeviceConfigurationFunctions> deviceConfigurationFunctionsList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_recipe;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        etSearch = findViewById(R.id.tv_search);
        tabLayout = findViewById(R.id.tablayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        searchPromptView = findViewById(R.id.recipe_search_prompt);
        searchPromptBtn = findViewById(R.id.recipe_search_prompt_btn);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                noScrollViewPager.setCurrentItem(tab.getId(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                        //ToastUtils.showShort(RecipeActivity.this, R.string.stove_input_empty);
                    }
                    return false;
                }
                return false;
            }
        });

        setOnClickListener(R.id.recipe_search_prompt_btn);
    }

    @Override
    protected void initData() {
//for test
//        classifyList.add("肉禽");
//        classifyList.add("水产品");
//        classifyList.add("主食");
//        classifyList.add("甜品");
//        classifyList.add("果蔬");
//        classifyList.add("牛奶");
//        classifyList.add("肉禽");
//        classifyList.add("肉禽");
//        classifyList.add("肉禽");
//        classifyList.add("肉禽");


        getLocalRecipe();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            finish();
        }else if(id == R.id.recipe_search_prompt_btn){
            etSearch.setText("");
            setSearchState(false);
        }
    }

    //本地菜谱
    private void getLocalRecipe() {
        String steamContent = SteamDataUtil.getSteamContent();
        if(StringUtils.isNotBlank(steamContent)){
            GetDeviceParamsRes getDeviceParamsRes = new Gson().fromJson(steamContent, GetDeviceParamsRes.class);
            if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap){
                setRecipeData(getDeviceParamsRes);
            }
        }
        UserInfo info = AccountInfo.getInstance().getUser().getValue();
        CloudHelper.getDeviceParams(this, (info != null) ? info.id:0, "CQ928", IDeviceType.RZKY, GetDeviceParamsRes.class,
                new RetrofitCallback<GetDeviceParamsRes>() {
                    @Override
                    public void onSuccess(GetDeviceParamsRes getDeviceParamsRes) {
                        if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap){
                            if(StringUtils.isBlank(steamContent)){
                                setRecipeData(getDeviceParamsRes);
                            }
                            SteamDataUtil.saveSteam(new Gson().toJson(getDeviceParamsRes, GetDeviceParamsRes.class));
                        }
                    }

                    @Override
                    public void onFaild(String err) {

                    }
                });
    }
    //菜谱数据
    private void setRecipeData(GetDeviceParamsRes getDeviceParamsRes) {
        OtherFunc otherFunc = getDeviceParamsRes.modelMap.otherFunc;
        if (null != otherFunc && null != otherFunc.deviceConfigurationFunctions) {
            for (DeviceConfigurationFunctions deviceConfigurationFunctions: otherFunc.deviceConfigurationFunctions) {
                if ("localCookbook".equals(deviceConfigurationFunctions.functionCode)) {
                    if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                        SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                        if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                            for (DeviceConfigurationFunctions deviceConfigurationFunctions1: subViewModelMapSubView.deviceConfigurationFunctions) {
                                if (!"ckno".equals(deviceConfigurationFunctions1.functionCode))
                                    deviceConfigurationFunctionsList.add(deviceConfigurationFunctions1);
                            }
                        }
                    }
                    break;
                }
            }
        }
        //添加分类
        for (int i =0; i<deviceConfigurationFunctionsList.size(); i++) {
            DeviceConfigurationFunctions deviceConfigurationFunctions = deviceConfigurationFunctionsList.get(i);
            classifyList.add(deviceConfigurationFunctions.functionName);
            //菜谱信息
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setId(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_classify, null);
            TextView classify = view.findViewById(R.id.tv_classify);
            classify.setText(classifyList.get(i));
            tab.setCustomView(view);
            tabLayout.addTab(tab);

            Fragment recipeClassifyPage = new RecipeClassifyPage();
            Bundle bundle = new Bundle();
            if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                    bundle.putInt("classify", i);
                    bundle.putParcelableArrayList(Constant.RECIPE_LIST_FLAG, (ArrayList<? extends Parcelable>) subViewModelMapSubView.deviceConfigurationFunctions);
                    recipeClassifyPage.setArguments(bundle);
                }
            }

            fragments.add(new WeakReference<>(recipeClassifyPage));
        }

        //添加设置适配器
        noScrollViewPager.setAdapter(new RecipeClassifyPagerAdapter(getSupportFragmentManager()));

        noScrollViewPager.setOffscreenPageLimit(classifyList.size());
    }

    class RecipeClassifyPagerAdapter extends FragmentStatePagerAdapter {


        public RecipeClassifyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).get();
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }

    //搜索结果
    private void searchResult(String text) {
//        CloudHelper.getCookbooksByName(this, text, false, 0L, false, true,
//                GetRecipesByDeviceRes.class, new RetrofitCallback<GetRecipesByDeviceRes>() {
//
//                    @Override
//                    public void onSuccess(GetRecipesByDeviceRes getRecipesByDeviceRes) {
//
//                        setData(getRecipesByDeviceRes);
//                    }
//
//                    @Override
//                    public void onFaild(String err) {
//                        setData(null);
//                    }
//                });
        if(TextUtils.isEmpty(text)){
            return;
        }
        this.setSearchState(searchPromptView.getVisibility() != View.VISIBLE);
    }

    private void setSearchState(boolean showSearchPrompt){
        if(showSearchPrompt){
            searchPromptView.setVisibility(View.VISIBLE);
            searchPromptBtn.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.INVISIBLE);
            noScrollViewPager.setVisibility(View.INVISIBLE);
        }else{
            searchPromptView.setVisibility(View.INVISIBLE);
            searchPromptBtn.setVisibility(View.INVISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            noScrollViewPager.setVisibility(View.VISIBLE);
        }
    }


}
